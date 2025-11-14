package com.example.medialert.screens.camera;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.provider.MediaStore;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.medialert.R;
import com.example.medialert.utils.AppLogger;
import com.google.android.material.button.MaterialButton;

/**
 * Activity para capturar fotos usando la cámara del dispositivo
 * Esta funcionalidad no interfiere con el flujo principal de la app
 */
public class CameraActivity extends AppCompatActivity {

    private static final String TAG = "CameraActivity";
    private static final int CAMERA_PERMISSION_CODE = 100;
    private static final int CAMERA_REQUEST_CODE = 101;

    private ImageView imagePreview;
    private MaterialButton btnCapture;
    private MaterialButton btnClose;
    private Bitmap capturedImage;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLogger.lifecycle(TAG, "onCreate");
        setContentView(R.layout.activity_camera);

        initializeViews();
        setupClickListeners();
    }

    private void initializeViews() {
        AppLogger.d(TAG, "Inicializando vistas");
        imagePreview = findViewById(R.id.image_preview);
        btnCapture = findViewById(R.id.btn_capture);
        btnClose = findViewById(R.id.btn_close);
    }

    private void setupClickListeners() {
        btnCapture.setOnClickListener(v -> {
            AppLogger.userEvent("Botón Capturar", "Usuario presionó botón de captura");
            checkCameraPermissionAndCapture();
        });

        btnClose.setOnClickListener(v -> {
            AppLogger.userEvent("Botón Cerrar", "Usuario cerró CameraActivity");
            finish();
        });
    }

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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CAMERA_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data != null && data.getExtras() != null) {
                capturedImage = (Bitmap) data.getExtras().get("data");
                if (capturedImage != null) {
                    AppLogger.i(TAG, "Imagen capturada exitosamente");
                    imagePreview.setImageBitmap(capturedImage);
                    Toast.makeText(this, "Foto capturada exitosamente", Toast.LENGTH_SHORT).show();
                } else {
                    AppLogger.e(TAG, "Error: Bitmap capturado es null");
                }
            } else {
                AppLogger.e(TAG, "Error: Intent data es null después de captura");
            }
        } else {
            AppLogger.d(TAG, "Captura de foto cancelada o falló");
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        AppLogger.lifecycle(TAG, "onStart");
    }

    @Override
    protected void onResume() {
        super.onResume();
        AppLogger.lifecycle(TAG, "onResume");
    }

    @Override
    protected void onPause() {
        super.onPause();
        AppLogger.lifecycle(TAG, "onPause");
    }

    @Override
    protected void onStop() {
        super.onStop();
        AppLogger.lifecycle(TAG, "onStop");
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        AppLogger.lifecycle(TAG, "onDestroy");
        // Liberar recursos
        if (capturedImage != null && !capturedImage.isRecycled()) {
            capturedImage.recycle();
            capturedImage = null;
        }
    }
}
