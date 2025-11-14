package com.example.medialert.screens.login;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medialert.R;
import com.example.medialert.screens.main.MainActivity;
import com.example.medialert.utils.AppLogger;
import com.example.medialert.utils.AuthUtils;
import com.google.android.material.snackbar.Snackbar;
import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity de inicio de sesión con Firebase Authentication
 */
public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    // Firebase
    private FirebaseAuth mAuth;

    // UI Components
    private TextInputLayout emailLayout;
    private TextInputLayout passwordLayout;
    private TextInputEditText emailInput;
    private TextInputEditText passwordInput;
    private Button loginButton;
    private TextView forgotPasswordText;
    private TextView registerText;
    private ProgressBar progressBar;
    private View rootView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLogger.lifecycle(TAG, "onCreate");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_logo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        AppLogger.d(TAG, "Firebase Auth inicializado");

        // Verificar si ya hay usuario logueado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            // Usuario ya logueado, ir a MainActivity
            AppLogger.i(TAG, "Usuario ya logueado: " + currentUser.getEmail());
            navigateToMain();
            return;
        }
        AppLogger.d(TAG, "No hay usuario logueado, mostrando pantalla de login");

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
        passwordLayout = findViewById(R.id.layout_password);
        emailInput = findViewById(R.id.edit_email);
        passwordInput = findViewById(R.id.edit_password);
        loginButton = findViewById(R.id.button_login);
        forgotPasswordText = findViewById(R.id.text_forgot_password);
        registerText = findViewById(R.id.text_register);
        progressBar = findViewById(R.id.progress_bar);
    }

    /**
     * Configura los listeners de clicks
     */
    private void setupClickListeners() {
        loginButton.setOnClickListener(v -> performLogin());
        
        forgotPasswordText.setOnClickListener(v -> {
            AppLogger.userEvent("Forgot Password", "Usuario presionó olvidar contraseña");
            Intent intent = new Intent(LoginActivity.this, ForgotPasswordActivity.class);
            startActivity(intent);
        });

        registerText.setOnClickListener(v -> {
            AppLogger.userEvent("Register", "Usuario presionó registrarse");
            Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
            startActivity(intent);
        });
    }

    /**
     * Realiza el login con Firebase
     */
    private void performLogin() {
        AppLogger.userEvent("Login Attempt", "Usuario intenta iniciar sesión");
        // Limpiar errores previos
        emailLayout.setError(null);
        passwordLayout.setError(null);

        // Obtener valores con protección contra null
        String email = emailInput.getText() != null ? emailInput.getText().toString().trim() : "";
        String password = passwordInput.getText() != null ? passwordInput.getText().toString().trim() : "";

        // Validar campos
        AuthUtils.ValidationResult validation = AuthUtils.validateLogin(email, password);
        if (!validation.isValid()) {
            AppLogger.w(TAG, "Validación fallida: " + validation.getErrorMessage());
            showError(validation.getErrorMessage());
            if (!AuthUtils.isValidEmail(email)) {
                emailLayout.setError(validation.getErrorMessage());
            } else if (TextUtils.isEmpty(password)) {
                passwordLayout.setError(validation.getErrorMessage());
            }
            return;
        }

        // Mostrar loading
        setLoading(true);

        // Autenticar con Firebase
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, task -> {
                    setLoading(false);

                    if (task.isSuccessful()) {
                        // Login exitoso
                        FirebaseUser user = mAuth.getCurrentUser();
                        if (user != null) {
                            AppLogger.i(TAG, "Login exitoso para: " + user.getEmail());
                            showSuccess("Bienvenido");
                            navigateToMain();
                        }
                    } else {
                        // Login fallido
                        String errorMessage = AuthUtils.getFirebaseErrorMessage(task.getException());
                        AppLogger.e(TAG, "Login fallido: " + errorMessage, task.getException());
                        showError(errorMessage);
                        passwordLayout.setError(" "); // Espacio para mostrar error visual
                    }
                });
    }

    /**
     * Navega a MainActivity y limpia el stack
     */
    private void navigateToMain() {
        AppLogger.navigation(TAG, "MainActivity");
        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    /**
     * Muestra/oculta loading state
     */
    private void setLoading(boolean isLoading) {
        if (isLoading) {
            progressBar.setVisibility(View.VISIBLE);
            loginButton.setEnabled(false);
            loginButton.setText(R.string.auth_logging_in);
            emailInput.setEnabled(false);
            passwordInput.setEnabled(false);
        } else {
            progressBar.setVisibility(View.GONE);
            loginButton.setEnabled(true);
            loginButton.setText(R.string.auth_login_button);
            emailInput.setEnabled(true);
            passwordInput.setEnabled(true);
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
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }
}
