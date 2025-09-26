package com.example.medialert.screens.login;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.medialert.R;
import com.example.medialert.screens.main.MainActivity;

public class LoginActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_login);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.login_logo), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        Button loginButton = findViewById(R.id.button_login);
        TextView forgotPasswordText = findViewById(R.id.text_forgot_password);
        TextView registerText = findViewById(R.id.text_register);

        loginButton.setOnClickListener(v -> {
            // For now, we'll just navigate to the main screen
            Intent intent = new Intent(LoginActivity.this, MainActivity.class);
            startActivity(intent);
            finish(); // Finish LoginActivity so the user can't go back to it
        });

        forgotPasswordText.setOnClickListener(v -> {
            // TODO: Implement forgot password functionality
            // For now, just show a toast or navigate to forgot password screen
        });

        registerText.setOnClickListener(v -> {
            // TODO: Implement registration functionality
            // For now, just show a toast or navigate to registration screen
        });
    }
}
