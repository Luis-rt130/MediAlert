package com.example.medialert.screens.addmedicine;

import android.app.TimePickerDialog;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medialert.R;
import com.example.medialert.models.Medicine;
import com.example.medialert.utils.AppLogger;
import com.example.medialert.utils.DatabaseManager;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class AddMedicineActivity extends AppCompatActivity {

    private static final String TAG = "AddMedicineActivity";

    private TextInputEditText timeEditText;
    private AutoCompleteTextView frequencyEditText;
    private ImageButton backButton;
    private Button saveButton;
    private Button cancelButton;
    private ProgressBar progressBar;
    
    private DatabaseManager databaseManager;
    private Medicine medicineToEdit; // Para modo edición

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLogger.lifecycle(TAG, "onCreate");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_add_medicine);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.add_medicine_layout), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        timeEditText = findViewById(R.id.edit_medicine_time);
        frequencyEditText = findViewById(R.id.edit_medicine_frequency);
        backButton = findViewById(R.id.button_back);
        saveButton = findViewById(R.id.button_save);
        cancelButton = findViewById(R.id.button_cancel);
        progressBar = findViewById(R.id.progress_bar);

        // Inicializar DatabaseManager
        databaseManager = DatabaseManager.getInstance();
        
        // Verificar si se está editando un medicamento
        String medicineId = getIntent().getStringExtra("medicine_id");
        if (medicineId != null) {
            loadMedicineForEdit(medicineId);
        }

        setupFrequencyDropdown();
        setupTimePicker();
        setupBackButton();
        setupSaveButton();
        setupCancelButton();
    }

    private void setupFrequencyDropdown() {
        String[] frequencies = new String[] {
                "Cada 4 horas", "Cada 6 horas", "Cada 8 horas", "Cada 12 horas", "Cada 24 horas"
        };
        ArrayAdapter<String> adapter = new ArrayAdapter<>(this, android.R.layout.simple_dropdown_item_1line, frequencies);
        frequencyEditText.setAdapter(adapter);
    }

    private void setupTimePicker() {
        timeEditText.setOnClickListener(v -> {
            Calendar calendar = Calendar.getInstance();
            int hour = calendar.get(Calendar.HOUR_OF_DAY);
            int minute = calendar.get(Calendar.MINUTE);

            TimePickerDialog timePickerDialog = new TimePickerDialog(this, (view, hourOfDay, minuteOfHour) -> {
                String time = String.format(Locale.getDefault(), "%02d:%02d", hourOfDay, minuteOfHour);
                timeEditText.setText(time);
            }, hour, minute, true); // true for 24-hour format

            timePickerDialog.show();
        });
    }

    private void setupBackButton() {
        backButton.setOnClickListener(v -> {
            finish(); // Volver a la pantalla anterior
        });
    }

    private void setupSaveButton() {
        saveButton.setOnClickListener(v -> {
            // Validar campos antes de guardar
            if (validateFields()) {
                saveMedicine();
            }
        });
    }
    
    /**
     * Carga un medicamento para edición
     */
    private void loadMedicineForEdit(String medicineId) {
        showProgress(true);
        databaseManager.getMedicineById(medicineId, new DatabaseManager.OnCompleteListener<Medicine>() {
            @Override
            public void onSuccess(Medicine medicine) {
                medicineToEdit = medicine;
                // Llenar los campos con los datos del medicamento
                TextInputEditText nameEditText = findViewById(R.id.edit_medicine_name);
                TextInputEditText doseEditText = findViewById(R.id.edit_medicine_dose);
                
                if (nameEditText != null) nameEditText.setText(medicine.getName());
                if (doseEditText != null) doseEditText.setText(medicine.getDose());
                if (timeEditText != null) timeEditText.setText(medicine.getTime());
                if (frequencyEditText != null) frequencyEditText.setText(medicine.getFrequency());
                
                showProgress(false);
            }
            
            @Override
            public void onFailure(Exception e) {
                showProgress(false);
                Toast.makeText(AddMedicineActivity.this, 
                    "Error al cargar medicamento: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                finish();
            }
        });
    }
    
    /**
     * Guarda el medicamento en Firestore
     */
    private void saveMedicine() {
        // Obtener valores de los campos
        TextInputEditText nameEditText = findViewById(R.id.edit_medicine_name);
        TextInputEditText doseEditText = findViewById(R.id.edit_medicine_dose);
        
        String name = nameEditText.getText().toString().trim();
        String dose = doseEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String frequency = frequencyEditText.getText().toString().trim();
        
        showProgress(true);
        saveButton.setEnabled(false);
        
        if (medicineToEdit != null) {
            // Modo edición: actualizar medicamento existente
            medicineToEdit.setName(name);
            medicineToEdit.setDose(dose);
            medicineToEdit.setTime(time);
            medicineToEdit.setFrequency(frequency);
            
            databaseManager.updateMedicine(medicineToEdit, new DatabaseManager.OnCompleteListener<Medicine>() {
                @Override
                public void onSuccess(Medicine medicine) {
                    showProgress(false);
                    saveButton.setEnabled(true);
                    Toast.makeText(AddMedicineActivity.this, 
                        "Medicamento actualizado exitosamente", 
                        Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
                
                @Override
                public void onFailure(Exception e) {
                    showProgress(false);
                    saveButton.setEnabled(true);
                    Toast.makeText(AddMedicineActivity.this, 
                        "Error al actualizar: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            // Modo creación: agregar nuevo medicamento
            Medicine medicine = new Medicine(null, name, dose, time, frequency, null);
            
            databaseManager.addMedicine(medicine, new DatabaseManager.OnCompleteListener<Medicine>() {
                @Override
                public void onSuccess(Medicine savedMedicine) {
                    showProgress(false);
                    saveButton.setEnabled(true);
                    Toast.makeText(AddMedicineActivity.this, 
                        "Medicamento guardado exitosamente", 
                        Toast.LENGTH_SHORT).show();
                    setResult(RESULT_OK);
                    finish();
                }
                
                @Override
                public void onFailure(Exception e) {
                    showProgress(false);
                    saveButton.setEnabled(true);
                    Toast.makeText(AddMedicineActivity.this, 
                        "Error al guardar: " + e.getMessage(), 
                        Toast.LENGTH_SHORT).show();
                }
            });
        }
    }
    
    /**
     * Muestra u oculta el indicador de progreso
     */
    private void showProgress(boolean show) {
        if (progressBar != null) {
            progressBar.setVisibility(show ? View.VISIBLE : View.GONE);
        }
        saveButton.setEnabled(!show);
        cancelButton.setEnabled(!show);
    }

    private void setupCancelButton() {
        cancelButton.setOnClickListener(v -> {
            // Mostrar toast de cancelación
            Toast.makeText(this, "Operación cancelada", Toast.LENGTH_SHORT).show();
            finish(); // Volver a la pantalla anterior
        });
    }

    private boolean validateFields() {
        // Obtener referencias a los campos de texto
        TextInputEditText nameEditText = findViewById(R.id.edit_medicine_name);
        TextInputEditText doseEditText = findViewById(R.id.edit_medicine_dose);
        
        String name = nameEditText.getText().toString().trim();
        String dose = doseEditText.getText().toString().trim();
        String time = timeEditText.getText().toString().trim();
        String frequency = frequencyEditText.getText().toString().trim();

        // Validar que todos los campos estén llenos
        if (name.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa el nombre del medicamento", Toast.LENGTH_SHORT).show();
            nameEditText.requestFocus();
            return false;
        }

        if (dose.isEmpty()) {
            Toast.makeText(this, "Por favor ingresa la dosis del medicamento", Toast.LENGTH_SHORT).show();
            doseEditText.requestFocus();
            return false;
        }

        if (time.isEmpty()) {
            Toast.makeText(this, "Por favor selecciona la hora del medicamento", Toast.LENGTH_SHORT).show();
            timeEditText.requestFocus();
            return false;
        }

        if (frequency.isEmpty()) {
            Toast.makeText(this, "Por favor selecciona la frecuencia del medicamento", Toast.LENGTH_SHORT).show();
            frequencyEditText.requestFocus();
            return false;
        }

        return true;
    }
}

