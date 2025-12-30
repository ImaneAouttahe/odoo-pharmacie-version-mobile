package com.example.pharmacie;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 100;

    private PharmacieApi api;
    private RecyclerView recyclerView;
    private PharmacyAdapter adapter;
    private Spinner citySpinner, modeSpinner;
    private EditText searchInput;
    private Button btnSearch, btnGeo;
    private FusedLocationProviderClient fusedLocationClient;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Initialisation des vues
        recyclerView = findViewById(R.id.pharmacy_results_final);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new PharmacyAdapter(new ArrayList<>(), this);
        recyclerView.setAdapter(adapter);

        citySpinner = findViewById(R.id.city_select_final);
        modeSpinner = findViewById(R.id.mode_select_final);
        searchInput = findViewById(R.id.search_input_final);
        btnSearch = findViewById(R.id.btn_search_manual_final);
        btnGeo = findViewById(R.id.btn_geolocation_final);

        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Retrofit
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();

        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.129.90:8069/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();

        api = retrofit.create(PharmacieApi.class);

        // Charger les villes
        loadCities();

        // Setup mode spinner
        String[] modes = {"Proximité", "Permanence"};
        ArrayAdapter<String> modeAdapter = new ArrayAdapter<>(
                this,
                android.R.layout.simple_spinner_item,
                modes
        );
        modeAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        modeSpinner.setAdapter(modeAdapter);

        // Bouton 🔍 recherche manuelle
        btnSearch.setOnClickListener(v -> searchPharmaciesManual(false));

        // Bouton 📍 géolocalisation
        btnGeo.setOnClickListener(v -> {
            if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                    != PackageManager.PERMISSION_GRANTED &&
                    ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                            != PackageManager.PERMISSION_GRANTED) {

                ActivityCompat.requestPermissions(
                        this,
                        new String[]{Manifest.permission.ACCESS_FINE_LOCATION, Manifest.permission.ACCESS_COARSE_LOCATION},
                        LOCATION_PERMISSION_REQUEST_CODE
                );
            } else {
                fusedLocationClient.getCurrentLocation(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                        null
                ).addOnSuccessListener(location -> {
                    if (location != null) {
                        searchPharmaciesGeo(location.getLatitude(), location.getLongitude());
                    } else {
                        Toast.makeText(this, "Impossible d'obtenir la localisation", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if(requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                fusedLocationClient.getCurrentLocation(
                        com.google.android.gms.location.Priority.PRIORITY_HIGH_ACCURACY,
                        null
                ).addOnSuccessListener(location -> {
                    if (location != null) {
                        searchPharmaciesGeo(location.getLatitude(), location.getLongitude());
                    }
                });
            } else {
                Toast.makeText(this, "Permission localisation refusée", Toast.LENGTH_SHORT).show();
            }
        }
    }

    // Charger les villes depuis Odoo
    private void loadCities() {

        Call<ResponseBody> call = api.getCitiesCSV();

        call.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (!response.isSuccessful() || response.body() == null) {
                    Toast.makeText(MainActivity.this, "Impossible de charger les villes", Toast.LENGTH_SHORT).show();
                    return;
                }

                try {
                    // Lire tout le CSV comme texte
                    String csv = response.body().string();

                    List<String> cities = new ArrayList<>();
                    cities.add("-- Sélectionnez une ville --");

                    // Séparer en lignes
                    String[] lines = csv.split("\n");

                    for (int i = 1; i < lines.length; i++) { // ignorer l'entête
                        String[] parts = lines[i].split(",");
                        if (parts.length > 0) {
                            String ville = parts[0].trim();
                            if (!ville.isEmpty()) {
                                cities.add(ville);
                            }
                        }
                    }

                    // Appliquer dans le Spinner
                    ArrayAdapter<String> adapter = new ArrayAdapter<>(
                            MainActivity.this,
                            android.R.layout.simple_spinner_item,
                            cities
                    );
                    adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
                    citySpinner.setAdapter(adapter);
                    searchPharmaciesManual(true);

                } catch (Exception e) {
                    Toast.makeText(MainActivity.this, "Erreur lecture CSV", Toast.LENGTH_SHORT).show();
                    e.printStackTrace();
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur réseau", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 🔍 Recherche manuelle
    private boolean isFirstManualSearch = true; // variable pour savoir si c'est le premier lancement

    private void searchPharmaciesManual(boolean isFirstLaunch) {
        String mode = modeSpinner.getSelectedItem() != null ? modeSpinner.getSelectedItem().toString() : "Proximité";
        String city;

        if (isFirstLaunch) {
            city = "casablanca"; // Par défaut au premier lancement
        } else {
            city = citySpinner.getSelectedItem() != null ? citySpinner.getSelectedItem().toString() : "Casablanca";
            if(city.equals("-- Sélectionnez une ville --")) {
                city = "casablanca"; // fallback si utilisateur n'a rien sélectionné
            }
        }

        String name = searchInput.getText().toString().trim();

        if (mode.equalsIgnoreCase("Proximité")) {
            Call<List<Pharmacy>> call = api.searchPharmacies(null, null, city, name);
            call.enqueue(new Callback<List<Pharmacy>>() {
                @Override
                public void onResponse(Call<List<Pharmacy>> call, Response<List<Pharmacy>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        adapter.updateData(response.body());
                        if (response.body().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Aucune pharmacie trouvée", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Aucune pharmacie trouvée", Toast.LENGTH_SHORT).show();
                        Log.e(TAG, "Manual search unsuccessful for this city ");
                    }
                }

                @Override
                public void onFailure(Call<List<Pharmacy>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Erreur lors de la recherche", Toast.LENGTH_SHORT).show();
                    Log.e(TAG, "Failed manual search: " + t.getMessage());
                }
            });
        } else if (mode.equalsIgnoreCase("Permanence")) {
            Call<List<Pharmacy>> call = api.searchPermanence(city);
            call.enqueue(new Callback<List<Pharmacy>>() {
                @Override
                public void onResponse(Call<List<Pharmacy>> call, Response<List<Pharmacy>> response) {
                    if (response.isSuccessful() && response.body() != null) {
                        adapter.updateData(response.body());
                        if (response.body().isEmpty()) {
                            Toast.makeText(MainActivity.this, "Aucune pharmacie de garde trouvée", Toast.LENGTH_SHORT).show();
                        }
                    } else {
                        Toast.makeText(MainActivity.this, "Aucune pharmacie de garde trouvée", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onFailure(Call<List<Pharmacy>> call, Throwable t) {
                    Toast.makeText(MainActivity.this, "Erreur lors de la recherche", Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    private void searchPharmaciesByCity(String city, String name) {
        Call<List<Pharmacy>> call = api.searchManual(city, name);
        call.enqueue(new Callback<List<Pharmacy>>() {
            @Override
            public void onResponse(Call<List<Pharmacy>> call, Response<List<Pharmacy>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                    if(response.body().isEmpty()){
                        Toast.makeText(MainActivity.this, "Aucune pharmacie trouvée", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Aucune pharmacie trouvée", Toast.LENGTH_SHORT).show();
                }
            }

            @Override
            public void onFailure(Call<List<Pharmacy>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur lors de la recherche", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // 📍 Recherche géolocalisée
    private void searchPharmaciesGeo(double lat, double lon) {
        Call<List<Pharmacy>> call = api.searchPharmacies(lat, lon, null, null);
        call.enqueue(new Callback<List<Pharmacy>>() {
            @Override
            public void onResponse(Call<List<Pharmacy>> call, Response<List<Pharmacy>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                    if (response.body().isEmpty()) {
                        Toast.makeText(MainActivity.this, "Aucune pharmacie proche trouvée", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Toast.makeText(MainActivity.this, "Aucune pharmacie proche trouvée", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Pharmacy>> call, Throwable t) {
                Toast.makeText(MainActivity.this, "Erreur lors de la recherche", Toast.LENGTH_SHORT).show();
            }
        });
    }
}
