package com.example.medialert.screens.addmedicine;

import android.Manifest;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.example.medialert.R;
import com.example.medialert.models.Medicine;
import com.example.medialert.utils.AppLogger;
import com.example.medialert.utils.DatabaseManager;
import com.example.medialert.utils.ImageStorageManager;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.textfield.TextInputEditText;

import java.util.Calendar;
import java.util.Locale;

public class AddMedicineActivity extends AppCompatActivity {

    private static final String TAG = "AddMedicineActivity";
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;

    private TextInputEditText timeEditText;
    private AutoCompleteTextView frequencyEditText;
    private ImageButton backButton;
    private Button saveButton;
    private Button cancelButton;
    private ProgressBar progressBar;
    private ImageView imagePreview;
    private MaterialButton buttonTakePhoto;
    private MaterialButton buttonRemovePhoto;
    
    private DatabaseManager databaseManager;
    private ImageStorageManager imageStorageManager;
    private Medicine medicineToEdit; // Para modo edición
    private Bitmap capturedPhoto;
    private String photoUrl; // URL de la foto en Firebase Storage

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
        imagePreview = findViewById(R.id.image_medicine_preview);
        buttonTakePhoto = findViewById(R.id.button_take_photo);
        buttonRemovePhoto = findViewById(R.id.button_remove_photo);

        // Inicializar Managers
        databaseManager = DatabaseManager.getInstance();
        imageStorageManager = ImageStorageManager.getInstance();
        
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
        setupPhotoButtons();
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
                
                // Cargar foto si existe
                if (medicine.getPhotoUrl() != null && !medicine.getPhotoUrl().isEmpty()) {
                    photoUrl = medicine.getPhotoUrl();
                    
                    // Cargar imagen desde URL usando Glide
                    Glide.with(AddMedicineActivity.this)
                            .load(photoUrl)
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .placeholder(android.R.drawable.ic_menu_gallery)
                            .error(android.R.drawable.ic_menu_gallery)
                            .centerCrop()
                            .into(imagePreview);
                    
                    imagePreview.setVisibility(View.VISIBLE);
                    buttonTakePhoto.setText("Cambiar Foto");
                    buttonRemovePhoto.setVisibility(View.VISIBLE);
                }
                
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
        
        // Si hay foto capturada, subirla primero
        if (capturedPhoto != null) {
            uploadPhotoAndSave(name, dose, time, frequency);
        } else {
            saveMedicineToDatabase(name, dose, time, frequency, photoUrl);
        }
    }
    
    /**
     * Sube la foto y luego guarda el medicamento
     */
    private void uploadPhotoAndSave(String name, String dose, String time, String frequency) {
        imageStorageManager.uploadMedicineImage(capturedPhoto, new ImageStorageManager.OnImageUploadListener() {
            @Override
            public void onSuccess(String imageUrl) {
                photoUrl = imageUrl;
                saveMedicineToDatabase(name, dose, time, frequency, photoUrl);
            }
            
            @Override
            public void onFailure(Exception e) {
                showProgress(false);
                saveButton.setEnabled(true);
                Toast.makeText(AddMedicineActivity.this, 
                    "Error al subir foto. Guardando sin foto...", 
                    Toast.LENGTH_SHORT).show();
                saveMedicineToDatabase(name, dose, time, frequency, null);
            }
            
            @Override
            public void onProgress(double progress) {
                // Opcional: mostrar progreso
            }
        });
    }
    
    /**
     * Guarda el medicamento en la base de datos
     */
    private void saveMedicineToDatabase(String name, String dose, String time, String frequency, String photoUrl) {
        if (medicineToEdit != null) {
            // Modo edición: actualizar medicamento existente
            medicineToEdit.setName(name);
            medicineToEdit.setDose(dose);
            medicineToEdit.setTime(time);
            medicineToEdit.setFrequency(frequency);
            medicineToEdit.setPhotoUrl(photoUrl);
            
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
            Medicine medicine = new Medicine(null, name, dose, time, frequency, null, photoUrl);
            
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
    
    /**
     * Configura los botones de foto
     */
    private void setupPhotoButtons() {
        buttonTakePhoto.setOnClickListener(v -> {
            AppLogger.userEvent("Botón Tomar Foto", "Usuario presionó botón de captura");
            checkCameraPermissionAndCapture();
        });
        
        buttonRemovePhoto.setOnClickListener(v -> {
            AppLogger.userEvent("Botón Quitar Foto", "Usuario quitó la foto");
            removePhoto();
        });
    }
    
    /**
     * Verifica permisos de cámara y abre la cámara
     */
    private void checkCameraPermissionAndCapture() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.CAMERA)
                == PackageManager.PERMISSION_GRANTED) {
            AppLogger.i(TAG, "Permiso de cámara concedido, abriendo cámara");
            openCamera();
        } else {
            AppLogger.w(TAG, "Permiso de cámara no concedido, solicitando permiso");
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.CAMERA},
                    CAMERA_PERMISSION_CODE);
        }
    }
    
    /**
     * Abre la cámara para capturar foto
     */
    private void openCamera() {
        AppLogger.d(TAG, "Abriendo intent de cámara");
        Intent cameraIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (cameraIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(cameraIntent, CAMERA_REQUEST_CODE);
        } else {
            AppLogger.e(TAG, "No se encontró app de cámara en el dispositivo");
            Toast.makeText(this, "No se puede acceder a la cámara", Toast.LENGTH_SHORT).show();
        }
    }
    
    /**
     * Maneja el resultado de la solicitud de permisos
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions,
                                           @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == CAMERA_PERMISSION_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                AppLogger.i(TAG, "Permiso de cámara concedido por el usuario");
                openCamera();
            } else {
                AppLogger.w(TAG, "Permiso de cámara denegado por el usuario");
                Toast.makeText(this, "Se necesita permiso de cámara para usar esta función",
                        Toast.LENGTH_SHORT).show();
            }
        }
    }
    
    /**
     * Maneja el resultado de la captura de foto
     */
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        
        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                capturedPhoto = (Bitmap) data.getExtras().get("data");
                if (capturedPhoto != null) {
                    AppLogger.i(TAG, "Imagen capturada exitosamente");
                    displayPhoto();
                } else {
                    AppLogger.e(TAG, "Error: Bitmap capturado es null");
                }
            }
        }
    }
    
    /**
     * Muestra la foto capturada
     */
    private void displayPhoto() {
        if (capturedPhoto != null) {
            imagePreview.setImageBitmap(capturedPhoto);
            imagePreview.setVisibility(View.VISIBLE);
            buttonRemovePhoto.setVisibility(View.VISIBLE);
            buttonTakePhoto.setText("Cambiar Foto");
        }
    }
    
    /**
     * Quita la foto
     */
    private void removePhoto() {
        capturedPhoto = null;
        photoUrl = null;
        imagePreview.setImageBitmap(null);
        imagePreview.setVisibility(View.GONE);
        buttonRemovePhoto.setVisibility(View.GONE);
        buttonTakePhoto.setText("Tomar Foto");
        Toast.makeText(this, "Foto removida", Toast.LENGTH_SHORT).show();
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
    
    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Liberar recursos de imagen
        if (capturedPhoto != null && !capturedPhoto.isRecycled()) {
            capturedPhoto.recycle();
            capturedPhoto = null;
        }
    }
}

