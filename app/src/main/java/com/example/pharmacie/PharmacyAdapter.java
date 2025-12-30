package com.example.pharmacie;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class PharmacyAdapter extends RecyclerView.Adapter<PharmacyAdapter.ViewHolder> {

    private List<Pharmacy> pharmacies;
    private Context context;

    public PharmacyAdapter(List<Pharmacy> pharmacies, Context context) {
        this.pharmacies = pharmacies;
        this.context = context;
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {

        TextView name, address, distance;
        LinearLayout distanceContainer;
        Button btnMap , btnViewMedicaments;

        public ViewHolder(View view) {
            super(view);

            name = view.findViewById(R.id.pharmacy_name);
            address = view.findViewById(R.id.pharmacy_address);

            distanceContainer = view.findViewById(R.id.distance_container);
            distance = view.findViewById(R.id.pharmacy_distance);

            btnMap = view.findViewById(R.id.btn_open_map);
            btnViewMedicaments = view.findViewById(R.id.btn_view_medicaments);
        }
    }

    @Override
    public PharmacyAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.pharmacy_item, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {

        Pharmacy pharmacy = pharmacies.get(position);

        // NOM (fallback si null)
        holder.name.setText(
                pharmacy.getName() != null && !pharmacy.getName().isEmpty()
                        ? pharmacy.getName()
                        : "Nom inconnu"
        );

        String street = sanitize(pharmacy.getStreet());
        String city = sanitize(pharmacy.getCity());

        String address;

        if (street != null) {
            address = street;
        } else if (city != null) {
            address = city;
        } else {
            address = "Adresse inconnue";
        }

        holder.address.setText(address);


        Double dist = pharmacy.getDistance();
        if (dist != null) {
            holder.distanceContainer.setVisibility(View.VISIBLE);
            holder.distance.setText("Distance : " + String.format("%.2f", dist) + " km");
        }else{
            holder.distanceContainer.setVisibility(View.GONE);
        }


        // BOUTON GOOGLE MAPS (dir= comme sur web)
        holder.btnMap.setOnClickListener(v -> {
            String url = "https://www.google.com/maps/dir/?api=1&destination="
                    + (pharmacy.getLatitude() != 0 ? pharmacy.getLatitude() : "")
                    + ","
                    + (pharmacy.getLongitude() != 0 ? pharmacy.getLongitude() : "");

            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            v.getContext().startActivity(intent);
        });

        // 🔹 Nouveau bouton "Voir Médicaments"
        holder.btnViewMedicaments.setOnClickListener(v -> {
            Intent intent = new Intent(holder.itemView.getContext(), MedicamentActivity.class);
            intent.putExtra("pharmacy_id", pharmacy.getId());
            intent.putExtra("pharmacy_name", pharmacy.getName());
            holder.itemView.getContext().startActivity(intent);
        });
    }
    private String sanitize(String value) {
        if (value == null) return null;
        if (value.trim().isEmpty()) return null;
        if (value.equalsIgnoreCase("false")) return null;
        return value;
    }


    @Override
    public int getItemCount() {
        return pharmacies.size();
    }

    public void updateData(List<Pharmacy> newList) {
        pharmacies = newList;
        notifyDataSetChanged();
    }
}
