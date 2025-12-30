package com.example.pharmacie;

import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import okhttp3.OkHttpClient;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MedicamentActivity extends AppCompatActivity {

    private RecyclerView recyclerView;
    private MedicamentAdapter adapter;
    private EditText searchInput;
    private int pharmacyId;
    private String pharmacyName;
    private PharmacieApi api;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_medicament);

        recyclerView = findViewById(R.id.medicaments_recycler_view);
        recyclerView.setLayoutManager(new LinearLayoutManager(this));
        adapter = new MedicamentAdapter(new ArrayList<>());
        recyclerView.setAdapter(adapter);

        searchInput = findViewById(R.id.med_search_input);

        // Récupérer l'id et nom de la pharmacie depuis l'intent
        pharmacyId = getIntent().getIntExtra("pharmacy_id", 0);
        pharmacyName = getIntent().getStringExtra("pharmacy_name");
        ((TextView)findViewById(R.id.pharmacy_name_title)).setText("Médicaments – " + pharmacyName);
        OkHttpClient okHttpClient = new OkHttpClient.Builder()
                .connectTimeout(60, TimeUnit.SECONDS)
                .readTimeout(60, TimeUnit.SECONDS)
                .writeTimeout(60, TimeUnit.SECONDS)
                .build();
        // Initialiser Retrofit
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl("http://192.168.0.216:8069/")
                .addConverterFactory(GsonConverterFactory.create())
                .client(okHttpClient)
                .build();
        api = retrofit.create(PharmacieApi.class);

        loadMedicaments(null);

        // Recherche en tapant un nom
        searchInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {}
            @Override
            public void afterTextChanged(Editable s) {
                loadMedicaments(s.toString());
            }
        });
    }

    private void loadMedicaments(String nameFilter) {
        Call<List<Medicament>> call = api.getMedicaments(pharmacyId, nameFilter);
        call.enqueue(new Callback<List<Medicament>>() {
            @Override
            public void onResponse(Call<List<Medicament>> call, Response<List<Medicament>> response) {
                if (response.isSuccessful() && response.body() != null) {
                    adapter.updateData(response.body());
                } else {
                    Toast.makeText(MedicamentActivity.this, "Aucun médicament trouvé", Toast.LENGTH_SHORT).show();
                }
            }
            @Override
            public void onFailure(Call<List<Medicament>> call, Throwable t) {
                Toast.makeText(MedicamentActivity.this, "Erreur réseau: " + t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }
}
