package com.example.medialert.adapters;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.medialert.R;
import com.example.medialert.models.Medicine;
import com.google.android.material.card.MaterialCardView;

import java.util.ArrayList;
import java.util.List;

/**
 * Adapter para mostrar la lista de medicamentos en un RecyclerView
 */
public class MedicineAdapter extends RecyclerView.Adapter<MedicineAdapter.MedicineViewHolder> {

    private List<Medicine> medicines;
    private OnMedicineClickListener clickListener;
    private OnMedicineLongClickListener longClickListener;

    public MedicineAdapter() {
        this.medicines = new ArrayList<>();
    }

    /**
     * Interfaz para manejar clicks en los items
     */
    public interface OnMedicineClickListener {
        void onMedicineClick(Medicine medicine);
    }

    /**
     * Interfaz para manejar clicks largos en los items
     */
    public interface OnMedicineLongClickListener {
        void onMedicineLongClick(Medicine medicine);
    }

    /**
     * Establece el listener para clicks
     */
    public void setOnMedicineClickListener(OnMedicineClickListener listener) {
        this.clickListener = listener;
    }

    /**
     * Establece el listener para clicks largos
     */
    public void setOnMedicineLongClickListener(OnMedicineLongClickListener listener) {
        this.longClickListener = listener;
    }

    @NonNull
    @Override
    public MedicineViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.item_medicine, parent, false);
        return new MedicineViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull MedicineViewHolder holder, int position) {
        Medicine medicine = medicines.get(position);
        holder.bind(medicine);
    }

    @Override
    public int getItemCount() {
        return medicines.size();
    }

    /**
     * Actualiza la lista de medicamentos
     */
    public void updateMedicines(List<Medicine> newMedicines) {
        this.medicines = newMedicines != null ? newMedicines : new ArrayList<>();
        notifyDataSetChanged();
    }

    /**
     * Agrega un medicamento a la lista
     */
    public void addMedicine(Medicine medicine) {
        if (medicine != null) {
            medicines.add(medicine);
            notifyItemInserted(medicines.size() - 1);
        }
    }

    /**
     * Remueve un medicamento de la lista
     */
    public void removeMedicine(Medicine medicine) {
        int position = medicines.indexOf(medicine);
        if (position >= 0) {
            medicines.remove(position);
            notifyItemRemoved(position);
        }
    }

    /**
     * Obtiene un medicamento por su posición
     */
    public Medicine getMedicineAt(int position) {
        if (position >= 0 && position < medicines.size()) {
            return medicines.get(position);
        }
        return null;
    }

    /**
     * ViewHolder para los items de medicamentos
     */
    class MedicineViewHolder extends RecyclerView.ViewHolder {
        private TextView nameTextView;
        private TextView detailsTextView;
        private TextView timeTextView;
        private ImageView iconMedicine;
        private MaterialCardView cardMedicinePhoto;
        private ImageView imageMedicinePhoto;

        MedicineViewHolder(@NonNull View itemView) {
            super(itemView);
            nameTextView = itemView.findViewById(R.id.text_view_medicine_name);
            detailsTextView = itemView.findViewById(R.id.text_view_medicine_details);
            timeTextView = itemView.findViewById(R.id.text_view_medicine_time);
            iconMedicine = itemView.findViewById(R.id.icon_medicine);
            cardMedicinePhoto = itemView.findViewById(R.id.card_medicine_photo);
            imageMedicinePhoto = itemView.findViewById(R.id.image_medicine_photo);

            // Configurar click listener
            itemView.setOnClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && clickListener != null) {
                    clickListener.onMedicineClick(medicines.get(position));
                }
            });

            // Configurar long click listener
            itemView.setOnLongClickListener(v -> {
                int position = getAdapterPosition();
                if (position != RecyclerView.NO_POSITION && longClickListener != null) {
                    longClickListener.onMedicineLongClick(medicines.get(position));
                    return true;
                }
                return false;
            });
        }

        void bind(Medicine medicine) {
            if (medicine == null) return;

            // Nombre del medicamento
            nameTextView.setText(medicine.getName());

            // Detalles: dosis y frecuencia
            String details = medicine.getDose();
            if (medicine.getFrequency() != null && !medicine.getFrequency().isEmpty()) {
                if (details != null && !details.isEmpty()) {
                    details += " - " + medicine.getFrequency();
                } else {
                    details = medicine.getFrequency();
                }
            }
            detailsTextView.setText(details != null ? details : "");

            // Hora
            String time = medicine.getTime();
            if (time != null && !time.isEmpty()) {
                // Extraer solo la hora si está en formato "HH:MM" o "HH:MM AM/PM"
                if (time.contains(" ")) {
                    time = time.split(" ")[0]; // Tomar solo la parte de la hora
                }
                timeTextView.setText(time);
            } else {
                timeTextView.setText("");
            }
            
            // Foto del medicamento
            if (medicine.getPhotoUrl() != null && !medicine.getPhotoUrl().isEmpty()) {
                // Mostrar foto circular
                cardMedicinePhoto.setVisibility(View.VISIBLE);
                iconMedicine.setVisibility(View.GONE);
                
                // Cargar imagen con Glide
                Glide.with(itemView.getContext())
                        .load(medicine.getPhotoUrl())
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .placeholder(android.R.drawable.ic_menu_gallery)
                        .error(android.R.drawable.ic_menu_gallery)
                        .centerCrop()
                        .into(imageMedicinePhoto);
            } else {
                // Mostrar icono por defecto
                cardMedicinePhoto.setVisibility(View.GONE);
                iconMedicine.setVisibility(View.VISIBLE);
            }
        }
    }
}

