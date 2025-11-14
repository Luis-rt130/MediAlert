package com.example.medialert.screens.login;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medialert.R;
import com.example.medialert.screens.main.MainActivity;
import com.example.medialert.utils.AppLogger;
import com.example.medialert.utils.AuthUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

/**
 * Activity de registro de nuevos usuarios con Firebase Authentication
 */
public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    // Firebase
    private FirebaseAuth mAuth;

    // UI Components
    private TextInputLayout nameLayout;
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputLayout confirmPasswordLayout;
    private TextInputEditText nameInput;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private TextInputEditText confirmPasswordInput;
    private Button registerButton;
    private TextView loginText;
    private ProgressBar progressBar;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLogger.lifecycle(TAG, "onCreate");
        setContentView(R.layout.activity_register);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        AppLogger.d(TAG, "Firebase Auth inicializado");

        // Inicializar vistas
        initializeViews();
        setupClickListeners();
    }

    /**
     * Inicializa todas las vistas
     */
    private void initializeViews() {
        rootView = findViewById(android.R.id.content);
        nameLayout = findViewById(R.id.layout_name);
        emailLayout = findViewById(R.id.layout_email);
        passwordLayout = findViewById(R.id.layout_password);
        confirmPasswordLayout = findViewById(R.id.layout_confirm_password);
        nameInput = findViewById(R.id.edit_name);
        emailInput = findViewById(R.id.edit_email);
        passwordInput = findViewById(R.id.edit_password);
        confirmPasswordInput = findViewById(R.id.edit_confirm_password);
        registerButton = findViewById(R.id.button_register);
        loginText = findViewById(R.id.text_login);
        progressBar = findViewById(R.id.progress_bar);
    }

    /**
     * Configura los listeners de clicks
     */
    private void setupClickListeners() {
        registerButton.setOnClickListener(v -> performRegister());
        
        loginText.setOnClickListener(v -> {
            AppLogger.userEvent("Back to Login", "Usuario vuelve a LoginActivity");
            finish(); // Volver a LoginActivity
        });
    }

    /**
     * Realiza el registro con Firebase
     */
    private void performRegister() {
        AppLogger.userEvent("Register Attempt", "Usuario intenta registrarse");
        // Limpiar errores previos
        nameLayout.setError(null);
        emailLayout.setError(null);
        passwordLayout.setError(null);
        confirmPasswordLayout.setError(null);

        // Obtener valores con protección contra null
        String name = nameInput.getText() != null ? nameInput.getText().toString().trim() : "";
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";
        String confirmPassword = confirmPasswordInput.getText() != null ? confirmPasswordInput.getText().toString().trim() : "";

        // Validar campos
        AuthUtils.ValidationResult validation = AuthUtils.validateRegistration(
                name, email, password, confirmPassword);
        
        if (!validation.isValid()) {
            showError(validation.getErrorMessage());
            highlightErrorFields(name, email, password, confirmPassword);
            return;
        }

        // Mostrar loading
        setLoading(true);

        // Crear usuario en Firebase
        mAuth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Registro exitoso, actualizar perfil con nombre
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            updateUserProfile(user, name);
                        }
                    } else {
                        // Registro fallido
                        setLoading(false);
                        String errorMessage = AuthUtils.getFirebaseErrorMessage(task.getException());
                        showError(errorMessage);
                    }
                });
    }

    /**
     * Actualiza el perfil del usuario con su nombre
     */
    private void updateUserProfile(FirebaseUser user, String name) {
        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name)
                .build();

        user.updateProfile(profileUpdates)
                .addOnCompleteListener(task -> {
                    if (task.isSuccessful()) {
                        // Enviar email de verificación
                        sendVerificationEmail(user);
                    } else {
                        setLoading(false);
                        showError("Error al actualizar perfil");
                    }
                });
    }

    /**
     * Envía email de verificación al usuario
     */
    private void sendVerificationEmail(FirebaseUser user) {
        user.sendEmailVerification()
                .addOnCompleteListener(task -> {
                    setLoading(false);
                    
                    if (task.isSuccessful()) {
                        showSuccess(getString(R.string.register_success));
                        Toast.makeText(this, 
                                getString(R.string.register_verification_sent), 
                                Toast.LENGTH_LONG).show();
                        
                        // Navegar a MainActivity después de un pequeño delay
                        rootView.postDelayed(this::navigateToMain, 1500);
                    } else {
                        // Registro exitoso pero email no enviado
                        showSuccess(getString(R.string.register_success));
                        navigateToMain();
                    }
                });
    }

    /**
     * Navega a MainActivity
     */
    private void navigateToMain() {
        Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Resalta los campos con error
     */
    private void highlightErrorFields(String name, String email, 
                                       String password, String confirmPassword) {
        if (!AuthUtils.isValidName(name)) {
            nameLayout.setError(getString(R.string.auth_error_empty_name));
        }
        if (!AuthUtils.isValidEmail(email)) {
            emailLayout.setError(getString(R.string.auth_error_invalid_email));
        }
        if (!AuthUtils.isValidPassword(password)) {
            passwordLayout.setError(getString(R.string.auth_error_weak_password));
        }
        if (!AuthUtils.passwordsMatch(password, confirmPassword)) {
            confirmPasswordLayout.setError(getString(R.string.auth_error_passwords_not_match));
        }
    }

    /**
     * Muestra/oculta loading state
     */
    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            registerButton.setEnabled(false);
            registerButton.setText(R.string.auth_registering);
            nameInput.setEnabled(false);
            emailInput.setEnabled(false);
            passwordInput.setEnabled(false);
            confirmPasswordInput.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            registerButton.setEnabled(true);
            registerButton.setText(R.string.auth_register_button);
            nameInput.setEnabled(true);
            emailInput.setEnabled(true);
            passwordInput.setEnabled(true);
            confirmPasswordInput.setEnabled(true);
        }
    }

    /**
     * Muestra mensaje de error
     */
    private void showError(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_LONG).show();
    }

    /**
     * Muestra mensaje de éxito
     */
    private void showSuccess(String message) {
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT).show();
    }
}
