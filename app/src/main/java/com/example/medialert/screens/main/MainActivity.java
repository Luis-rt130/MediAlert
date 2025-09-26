package com.example.medialert.screens.main;

import android.os.Bundle;
import android.widget.LinearLayout;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import com.example.medialert.R;
import com.example.medialert.screens.addmedicine.AddMedicineActivity;
import com.example.medialert.screens.profile.ProfileActivity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;


public class MainActivity extends AppCompatActivity {

    private LinearLayout welcomeStateLayout;
    private LinearLayout emptyStateLayout;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Inicializar vistas
        welcomeStateLayout = findViewById(R.id.welcome_state_layout);
        emptyStateLayout = findViewById(R.id.empty_state_layout);
        fab = findViewById(R.id.fab_add_medicine);

        // Verificar que las vistas se encontraron correctamente
        if (welcomeStateLayout == null || emptyStateLayout == null || fab == null) {
            throw new RuntimeException("No se pudieron encontrar las vistas necesarias en el layout");
        }

        fab.setOnClickListener(view -> {
            Intent intent = new Intent(MainActivity.this, AddMedicineActivity.class);
            startActivity(intent);
        });

        // Mostrar estado de bienvenida por defecto
        showWelcomeState();
    }

    private void showWelcomeState() {
        welcomeStateLayout.setVisibility(LinearLayout.VISIBLE);
        emptyStateLayout.setVisibility(LinearLayout.GONE);
    }

    private void showEmptyState() {
        welcomeStateLayout.setVisibility(LinearLayout.GONE);
        emptyStateLayout.setVisibility(LinearLayout.VISIBLE);
    }

    private void showMedicineList() {
        welcomeStateLayout.setVisibility(LinearLayout.GONE);
        emptyStateLayout.setVisibility(LinearLayout.GONE);
        // TODO: Mostrar RecyclerView con medicamentos
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        if (item.getItemId() == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
