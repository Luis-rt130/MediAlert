package com.example.medialert.screens.login;

import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.medialert.R;
import com.example.medialert.utils.AuthUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

/**
 * Activity para recuperación de contraseña con Firebase Authentication
 */
public class ForgotPasswordActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth;

    // UI Components
    private TextInputLayout emailLayout;
    private TextInputEditText emailInput;
    private Button sendButton;
    private TextView backToLoginText;
    private ProgressBar progressBar;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_forgot_password);

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();

        // Inicializar vistas
        initializeViews();
        setupClickListeners();
    }

    /**
     * Inicializa todas las vistas
     */
    private void initializeViews() {
        rootView = findViewById(android.R.id.content);
        emailLayout = findViewById(R.id.layout_email);
        emailInput = findViewById(R.id.edit_email);
        sendButton = findViewById(R.id.button_send);
        backToLoginText = findViewById(R.id.text_back_to_login);
        progressBar = findViewById(R.id.progress_bar);
    }

    /**
     * Configura los listeners de clicks
     */
    private void setupClickListeners() {
        sendButton.setOnClickListener(v -> sendPasswordResetEmail());
        
        backToLoginText.setOnClickListener(v -> finish());
    }

    /**
     * Envía email de recuperación de contraseña
     */
    private void sendPasswordResetEmail() {
        // Limpiar errores previos
        emailLayout.setError(null);

        // Obtener email con protección contra null
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";

        // Validar email
        if (!AuthUtils.isValidEmail(email)) {
            String errorMessage = getString(R.string.auth_error_invalid_email);
            emailLayout.setError(errorMessage);
            showError(errorMessage);
            return;
        }

        // Mostrar loading
        setLoading(true);

        // Enviar email de recuperación
        mAuth.sendPasswordResetEmail(email)
                .addOnCompleteListener(task -> {
                    setLoading(false);

                    if (task.isSuccessful()) {
                        // Email enviado exitosamente
                        showSuccess(getString(R.string.forgot_password_success));
                        Toast.makeText(this, 
                                getString(R.string.forgot_password_check_email),
                                Toast.LENGTH_LONG).show();
                        
                        // Volver a login después de 2 segundos
                        rootView.postDelayed(this::finish, 2000);
                    } else {
                        // Error al enviar email
                        String errorMessage = AuthUtils.getFirebaseErrorMessage(task.getException());
                        showError(errorMessage);
                        emailLayout.setError(" "); // Espacio para mostrar error visual
                    }
                });
    }

    /**
     * Muestra/oculta loading state
     */
    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            sendButton.setEnabled(false);
            sendButton.setText(R.string.auth_sending_email);
            emailInput.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            sendButton.setEnabled(true);
            sendButton.setText(R.string.forgot_password_send);
            emailInput.setEnabled(true);
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
        Snackbar.make(rootView, message, Snackbar.LENGTH_SHORT)
                .setBackgroundTint(getColor(R.color.success))
                .show();
    }
}
