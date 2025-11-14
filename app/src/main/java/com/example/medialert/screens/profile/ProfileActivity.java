package com.example.medialert.screens.profile;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medialert.R;
import com.example.medialert.screens.login.LoginActivity;
import com.example.medialert.utils.AppLogger;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

/**
 * Activity de perfil de usuario con datos de Firebase Authentication
 */
public class ProfileActivity extends AppCompatActivity {

    private static final String TAG = "ProfileActivity";

    // Firebase
    private FirebaseAuth mAuth;
    
    // UI Components
    private TextView nameText;
    private TextView emailText;
    private TextView emailVerificationStatus;
    private Button logoutButton;
    private ImageButton backButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        AppLogger.lifecycle(TAG, "onCreate");
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_profile);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.profile_root), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        AppLogger.d(TAG, "Firebase Auth inicializado");

        // Inicializar vistas
        nameText = findViewById(R.id.text_user_name);
        emailText = findViewById(R.id.text_user_email);
        logoutButton = findViewById(R.id.button_logout);
        backButton = findViewById(R.id.button_back);
        
        // Cargar datos reales del usuario
        loadUserData();

        // Setup listeners
        logoutButton.setOnClickListener(v -> {
            AppLogger.userEvent("Logout Button", "Usuario presionó botón logout en perfil");
            showLogoutConfirmationDialog();
        });

        backButton.setOnClickListener(v -> {
            AppLogger.userEvent("Back Button", "Usuario regresa desde perfil");
            finish();
        });
    }

    /**
     * Carga los datos reales del usuario desde Firebase
     */
    private void loadUserData() {
        FirebaseUser user = mAuth.getCurrentUser();
        
        if (user != null) {
            // Obtener nombre
            String displayName = user.getDisplayName();
            if (displayName != null && !displayName.isEmpty()) {
                nameText.setText(displayName);
            } else {
                nameText.setText("Usuario");
            }
            
            // Obtener email
            String email = user.getEmail();
            if (email != null && !email.isEmpty()) {
                emailText.setText(email);
            }
            
            // Verificar si el email está verificado
            if (!user.isEmailVerified()) {
                Toast.makeText(this, 
                        getString(R.string.profile_email_not_verified), 
                        Toast.LENGTH_LONG).show();
            }
        } else {
            // No hay usuario logueado, volver a login
            navigateToLogin();
        }
    }

    /**
     * Muestra diálogo de confirmación de logout
     */
    private void showLogoutConfirmationDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.profile_logout_confirm_title)
                .setMessage(R.string.profile_logout_confirm_message)
                .setPositiveButton(R.string.profile_logout_confirm_yes, (dialog, which) -> performLogout())
                .setNegativeButton(R.string.profile_logout_confirm_no, (dialog, which) -> dialog.dismiss())
                .show();
    }

    /**
     * Realiza el logout de Firebase y navega a LoginActivity
     */
    private void performLogout() {
        // Sign out de Firebase
        mAuth.signOut();
        
        // Mostrar mensaje
        Toast.makeText(this, "Sesión cerrada", Toast.LENGTH_SHORT).show();
        
        // Navegar a login
        navigateToLogin();
    }

    /**
     * Navega a LoginActivity y limpia el stack
     */
    private void navigateToLogin() {
        Intent intent = new Intent(ProfileActivity.this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP | Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }
}


