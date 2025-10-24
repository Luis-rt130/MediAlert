package com.example.medialert.screens.main;

import android.Manifest;
import android.content.pm.PackageManager;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;
import android.content.Intent;
import android.view.View;

import com.example.medialert.R;
import com.example.medialert.screens.addmedicine.AddMedicineActivity;
import com.example.medialert.screens.login.LoginActivity;
import com.example.medialert.screens.profile.ProfileActivity;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.io.IOException;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Activity principal de la aplicación con gestión de sesiones de Firebase
 */
public class MainActivity extends AppCompatActivity {

    // Firebase
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener authStateListener;

    // Location
    private FusedLocationProviderClient fusedLocationClient;
    private TextView locationText;
    private TextView locationSubtitle;
    private ExecutorService executorService;
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;

    private LinearLayout welcomeStateLayout;
    private LinearLayout emptyStateLayout;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        // Inicializar Firebase Auth
        mAuth = FirebaseAuth.getInstance();
        
        // Verificar si hay usuario logueado
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser == null) {
            // No hay usuario logueado, redirigir a LoginActivity
            navigateToLogin();
            return;
        }
        
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });
        
        // Configurar listener de estado de autenticación
        setupAuthStateListener();

        // Inicializar location client y executor
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
        executorService = Executors.newSingleThreadExecutor();

        // Inicializar vistas
        try {
            locationText = findViewById(R.id.text_location);
            locationSubtitle = findViewById(R.id.text_location_subtitle);
            welcomeStateLayout = findViewById(R.id.welcome_state_layout);
            emptyStateLayout = findViewById(R.id.empty_state_layout);
            fab = findViewById(R.id.fab_add_medicine);

            fab.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, AddMedicineActivity.class);
                startActivity(intent);
            });

            // Mostrar estado de bienvenida por defecto
            if (welcomeStateLayout != null && emptyStateLayout != null) {
                showWelcomeState();
            }

            // Obtener ubicación
            getUserLocation();

        } catch (Exception e) {
            // Si hay error con los layouts, continuar sin ellos
            fab = findViewById(R.id.fab_add_medicine);
            if (fab != null) {
                fab.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, AddMedicineActivity.class);
                    startActivity(intent);
                });
            }
        }
    }

    private void showWelcomeState() {
        if (welcomeStateLayout != null && emptyStateLayout != null) {
            welcomeStateLayout.setVisibility(LinearLayout.VISIBLE);
            emptyStateLayout.setVisibility(LinearLayout.GONE);
        }
    }

    private void showEmptyState() {
        if (welcomeStateLayout != null && emptyStateLayout != null) {
            welcomeStateLayout.setVisibility(LinearLayout.GONE);
            emptyStateLayout.setVisibility(LinearLayout.VISIBLE);
        }
    }

    private void showMedicineList() {
        if (welcomeStateLayout != null && emptyStateLayout != null) {
            welcomeStateLayout.setVisibility(LinearLayout.GONE);
            emptyStateLayout.setVisibility(LinearLayout.GONE);
        }
        // TODO: Mostrar RecyclerView con medicamentos
    }

    @Override
    public boolean onCreateOptionsMenu(android.view.Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(android.view.MenuItem item) {
        int itemId = item.getItemId();
        
        if (itemId == R.id.action_profile) {
            Intent intent = new Intent(this, ProfileActivity.class);
            startActivity(intent);
            return true;
        } else if (itemId == R.id.action_location) {
            Intent intent = new Intent(this, com.example.medialert.screens.location.LocationActivity.class);
            startActivity(intent);
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * Configura el listener de estado de autenticación
     */
    private void setupAuthStateListener() {
        authStateListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user == null) {
                // Usuario se deslogueó, volver a login
                navigateToLogin();
            }
        };
    }

    /**
     * Navega a LoginActivity
     */
    private void navigateToLogin() {
        Intent intent = new Intent(this, LoginActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TASK);
        startActivity(intent);
        finish();
    }

    @Override
    protected void onStart() {
        super.onStart();
        // Agregar listener de auth
        if (authStateListener != null) {
            mAuth.addAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onStop() {
        super.onStop();
        // Remover listener de auth
        if (authStateListener != null) {
            mAuth.removeAuthStateListener(authStateListener);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        // Limpiar executor
        if (executorService != null && !executorService.isShutdown()) {
            executorService.shutdown();
        }
    }

    /**
     * Obtiene la ubicación del usuario y actualiza el texto
     */
    private void getUserLocation() {
        if (locationText == null) return;

        // Verificar permisos
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) 
                != PackageManager.PERMISSION_GRANTED) {
            // No hay permisos, mostrar mensaje
            locationText.setText(R.string.main_location_unavailable);
            return;
        }

        // Obtener última ubicación conocida (más rápido)
        fusedLocationClient.getLastLocation()
                .addOnSuccessListener(this, location -> {
                    if (location != null) {
                        // Geocodificar en background thread
                        getCityFromLocation(location.getLatitude(), location.getLongitude());
                    } else {
                        // No hay última ubicación
                        locationText.setText(R.string.main_location_unavailable);
                    }
                })
                .addOnFailureListener(e -> {
                    // Error al obtener ubicación
                    locationText.setText(R.string.main_location_unavailable);
                });
    }

    /**
     * Convierte coordenadas a ciudad y país usando Geocoder
     */
    private void getCityFromLocation(double latitude, double longitude) {
        executorService.execute(() -> {
            try {
                Geocoder geocoder = new Geocoder(this, Locale.getDefault());
                List<Address> addresses = geocoder.getFromLocation(latitude, longitude, 1);
                
                if (addresses != null && !addresses.isEmpty()) {
                    Address address = addresses.get(0);
                    String city = address.getLocality(); // Ciudad
                    String country = address.getCountryName(); // País
                    
                    // Construir texto de ubicación
                    StringBuilder locationBuilder = new StringBuilder();
                    
                    if (city != null && !city.isEmpty()) {
                        locationBuilder.append(city);
                    }
                    
                    if (country != null && !country.isEmpty()) {
                        if (locationBuilder.length() > 0) {
                            locationBuilder.append(", ");
                        }
                        locationBuilder.append(country);
                    }
                    
                    String finalLocation = locationBuilder.length() > 0 
                            ? locationBuilder.toString() 
                            : getString(R.string.main_location_unavailable);
                    
                    // Actualizar UI en main thread
                    runOnUiThread(() -> {
                        if (locationText != null) {
                            locationText.setText(getString(R.string.main_location_prefix) + " " + finalLocation);
                            if (locationSubtitle != null) {
                                locationSubtitle.setVisibility(View.VISIBLE);
                            }
                        }
                    });
                } else {
                    // No se encontró dirección
                    runOnUiThread(() -> {
                        if (locationText != null) {
                            locationText.setText(R.string.main_location_unavailable);
                        }
                    });
                }
            } catch (IOException e) {
                // Error de red o geocoding
                runOnUiThread(() -> {
                    if (locationText != null) {
                        locationText.setText(R.string.main_location_unavailable);
                    }
                });
            }
        });
    }
}
