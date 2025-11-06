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
import android.widget.Toast;

import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.medialert.R;
import com.example.medialert.adapters.MedicineAdapter;
import com.example.medialert.models.Medicine;
import com.example.medialert.screens.addmedicine.AddMedicineActivity;
import com.example.medialert.screens.login.LoginActivity;
import com.example.medialert.screens.profile.ProfileActivity;
import com.example.medialert.utils.DatabaseManager;
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
    private FloatingActionButton fabLogout;
    
    // Medicine List
    private RecyclerView recyclerViewMedicines;
    private MedicineAdapter medicineAdapter;
    private DatabaseManager databaseManager;
    private static final int REQUEST_CODE_ADD_MEDICINE = 1001;

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

        // Inicializar DatabaseManager
        databaseManager = DatabaseManager.getInstance();
        
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
            recyclerViewMedicines = findViewById(R.id.recycler_view_medicines);

            // Configurar RecyclerView
            setupRecyclerView();

            fab.setOnClickListener(view -> {
                Intent intent = new Intent(MainActivity.this, AddMedicineActivity.class);
                startActivityForResult(intent, REQUEST_CODE_ADD_MEDICINE);
            });

            // Configurar botón de logout
            fabLogout = findViewById(R.id.fab_logout);
            if (fabLogout != null) {
                fabLogout.setOnClickListener(view -> showLogoutDialog());
            }

            // Mostrar estado de bienvenida por defecto
            if (welcomeStateLayout != null && emptyStateLayout != null) {
                showWelcomeState();
            }

            // Obtener ubicación
            getUserLocation();
            
            // Cargar medicamentos
            loadMedicines();

        } catch (Exception e) {
            // Si hay error con los layouts, continuar sin ellos
            fab = findViewById(R.id.fab_add_medicine);
            if (fab != null) {
                fab.setOnClickListener(view -> {
                    Intent intent = new Intent(MainActivity.this, AddMedicineActivity.class);
                    startActivityForResult(intent, REQUEST_CODE_ADD_MEDICINE);
                });
            }
            
            // Configurar botón de logout
            fabLogout = findViewById(R.id.fab_logout);
            if (fabLogout != null) {
                fabLogout.setOnClickListener(view -> showLogoutDialog());
            }
            
            // Configurar RecyclerView si está disponible
            recyclerViewMedicines = findViewById(R.id.recycler_view_medicines);
            if (recyclerViewMedicines != null) {
                setupRecyclerView();
                loadMedicines();
            }
        }
    }
    
    /**
     * Configura el RecyclerView para mostrar los medicamentos
     */
    private void setupRecyclerView() {
        if (recyclerViewMedicines == null) return;
        
        medicineAdapter = new MedicineAdapter();
        recyclerViewMedicines.setLayoutManager(new LinearLayoutManager(this));
        recyclerViewMedicines.setAdapter(medicineAdapter);
        
        // Configurar click listener para editar
        medicineAdapter.setOnMedicineClickListener(medicine -> {
            Intent intent = new Intent(MainActivity.this, AddMedicineActivity.class);
            intent.putExtra("medicine_id", medicine.getId());
            startActivityForResult(intent, REQUEST_CODE_ADD_MEDICINE);
        });
        
        // Configurar long click listener para eliminar
        medicineAdapter.setOnMedicineLongClickListener(medicine -> {
            showDeleteMedicineDialog(medicine);
            return true;
        });
    }
    
    /**
     * Carga los medicamentos desde Firestore
     */
    private void loadMedicines() {
        databaseManager.getMedicines(new DatabaseManager.OnCompleteListener<List<Medicine>>() {
            @Override
            public void onSuccess(List<Medicine> medicines) {
                if (medicineAdapter != null) {
                    medicineAdapter.updateMedicines(medicines);
                }
                updateUIState(medicines != null && !medicines.isEmpty());
            }
            
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, 
                    "Error al cargar medicamentos: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
                updateUIState(false);
            }
        });
    }
    
    /**
     * Actualiza el estado de la UI según si hay medicamentos o no
     */
    private void updateUIState(boolean hasMedicines) {
        if (hasMedicines) {
            showMedicineList();
        } else {
            // Verificar si es la primera vez (sin medicamentos previos)
            if (medicineAdapter == null || medicineAdapter.getItemCount() == 0) {
                showWelcomeState();
            } else {
                showEmptyState();
            }
        }
    }
    
    /**
     * Muestra el diálogo de confirmación para eliminar un medicamento
     */
    private void showDeleteMedicineDialog(Medicine medicine) {
        new AlertDialog.Builder(this)
                .setTitle("Eliminar medicamento")
                .setMessage("¿Estás seguro de que deseas eliminar " + medicine.getName() + "?")
                .setPositiveButton("Eliminar", (dialog, which) -> deleteMedicine(medicine))
                .setNegativeButton("Cancelar", null)
                .show();
    }
    
    /**
     * Elimina un medicamento
     */
    private void deleteMedicine(Medicine medicine) {
        databaseManager.deleteMedicine(medicine.getId(), new DatabaseManager.OnCompleteListener<Void>() {
            @Override
            public void onSuccess(Void result) {
                Toast.makeText(MainActivity.this, 
                    "Medicamento eliminado", 
                    Toast.LENGTH_SHORT).show();
                loadMedicines(); // Recargar lista
            }
            
            @Override
            public void onFailure(Exception e) {
                Toast.makeText(MainActivity.this, 
                    "Error al eliminar: " + e.getMessage(), 
                    Toast.LENGTH_SHORT).show();
            }
        });
    }

    private void showWelcomeState() {
        if (welcomeStateLayout != null) {
            welcomeStateLayout.setVisibility(LinearLayout.VISIBLE);
        }
        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(LinearLayout.GONE);
        }
        if (recyclerViewMedicines != null) {
            recyclerViewMedicines.setVisibility(View.GONE);
        }
    }

    private void showEmptyState() {
        if (welcomeStateLayout != null) {
            welcomeStateLayout.setVisibility(LinearLayout.GONE);
        }
        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(LinearLayout.VISIBLE);
        }
        if (recyclerViewMedicines != null) {
            recyclerViewMedicines.setVisibility(View.GONE);
        }
    }

    private void showMedicineList() {
        if (welcomeStateLayout != null) {
            welcomeStateLayout.setVisibility(LinearLayout.GONE);
        }
        if (emptyStateLayout != null) {
            emptyStateLayout.setVisibility(LinearLayout.GONE);
        }
        if (recyclerViewMedicines != null) {
            recyclerViewMedicines.setVisibility(View.VISIBLE);
        }
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
     * Muestra diálogo de confirmación para cerrar sesión
     */
    private void showLogoutDialog() {
        new AlertDialog.Builder(this)
                .setTitle("Cerrar sesión")
                .setMessage("¿Estás seguro de que deseas cerrar sesión?")
                .setPositiveButton("Cerrar sesión", (dialog, which) -> performLogout())
                .setNegativeButton("Cancelar", null)
                .show();
    }

    /**
     * Realiza el logout de Firebase
     */
    private void performLogout() {
        if (mAuth != null) {
            mAuth.signOut();
            navigateToLogin();
        }
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
        // Recargar medicamentos cuando la actividad se reanuda
        if (databaseManager != null) {
            loadMedicines();
        }
    }
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REQUEST_CODE_ADD_MEDICINE && resultCode == RESULT_OK) {
            // Recargar medicamentos después de agregar/editar
            loadMedicines();
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
            // Solicitar permisos en runtime
            ActivityCompat.requestPermissions(this, 
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
                                 Manifest.permission.ACCESS_FINE_LOCATION}, 
                    LOCATION_PERMISSION_REQUEST_CODE);
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

    /**
     * Callback cuando el usuario responde a la solicitud de permisos
     */
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, 
                                          @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        
        if (requestCode == LOCATION_PERMISSION_REQUEST_CODE) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Permiso concedido, intentar obtener ubicación nuevamente
                getUserLocation();
            } else {
                // Permiso denegado
                if (locationText != null) {
                    locationText.setText(R.string.main_location_unavailable);
                }
            }
        }
    }
}

