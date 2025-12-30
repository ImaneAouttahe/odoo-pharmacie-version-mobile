package com.example.pharmacie;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import java.util.List;

public class MedicamentAdapter extends RecyclerView.Adapter<MedicamentAdapter.ViewHolder> {

    private List<Medicament> medicaments;

    public MedicamentAdapter(List<Medicament> medicaments) {
        this.medicaments = medicaments;
    }

    public void updateData(List<Medicament> newData) {
        medicaments.clear();
        medicaments.addAll(newData);
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicament, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Medicament med = medicaments.get(position);
        holder.name.setText(med.getName());
        holder.packaging.setText(med.getPackaging());
        holder.price.setText(med.getPrice() + " DH");
        holder.quantity.setText(String.valueOf(med.getQuantity()));
        if (med.isAvailable()) {
            holder.available.setText("Disponible");
            holder.available.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_green_dark));
        } else {
            holder.available.setText("Indisponible");
            holder.available.setTextColor(holder.itemView.getResources().getColor(android.R.color.holo_red_dark));
        }
    }

    @Override
    public int getItemCount() {
        return medicaments.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder {
        TextView name, packaging, price, quantity, available;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            name = itemView.findViewById(R.id.med_name);
            packaging = itemView.findViewById(R.id.med_packaging);
            price = itemView.findViewById(R.id.med_price);
            quantity = itemView.findViewById(R.id.med_quantity);
            available = itemView.findViewById(R.id.med_available);
        }
    }
}
