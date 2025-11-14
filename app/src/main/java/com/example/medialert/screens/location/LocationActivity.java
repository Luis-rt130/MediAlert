package com.example.medialert.screens.location;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.os.Looper;
import android.provider.Settings;
import android.view.MenuItem;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.result.ActivityResultLauncher;
import androidx.activity.result.contract.ActivityResultContracts;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import com.example.medialert.R;
import com.example.medialert.utils.AppLogger;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.Priority;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.material.button.MaterialButton;
import com.google.android.material.card.MaterialCardView;

/**
 * Activity que muestra la ubicación del dispositivo en tiempo real usando GPS
 * Implementa manejo robusto de permisos y errores
 */
public class LocationActivity extends AppCompatActivity implements OnMapReadyCallback {

    private static final String TAG = "LocationActivity";

    // Constants
    private static final int LOCATION_PERMISSION_REQUEST_CODE = 1001;
    private static final long UPDATE_INTERVAL = 5000; // 5 segundos
    private static final long FASTEST_INTERVAL = 2000; // 2 segundos

    // UI Components
    private LinearLayout loadingOverlay;
    private MaterialCardView locationInfoCard;
    private MaterialCardView permissionCard;
    private MaterialCardView errorCard;
    private TextView textLatitude;
    private TextView textLongitude;
    private TextView textAccuracy;
    private TextView textError;
    private MaterialButton buttonGrantPermission;

    // Google Services
    private GoogleMap googleMap;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private Location currentLocation;

    // Permission launcher
    private ActivityResultLauncher<String[]> requestPermissionLauncher;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_location);

        // Configurar Toolbar
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // Inicializar vistas
        initializeViews();

        // Configurar cliente de ubicación
        fusedLocationClient = LocationServices.getFusedLocationProviderClient(this);

        // Configurar callback de ubicación
        setupLocationCallback();

        // Configurar launcher de permisos
        setupPermissionLauncher();

        // Inicializar mapa
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        if (mapFragment != null) {
            mapFragment.getMapAsync(this);
        }

        // Configurar botón de permisos
        buttonGrantPermission.setOnClickListener(v -> requestLocationPermission());

        // Verificar permisos iniciales
        checkAndRequestPermissions();
    }

    /**
     * Inicializa todas las vistas del layout
     */
    private void initializeViews() {
        loadingOverlay = findViewById(R.id.loading_overlay);
        locationInfoCard = findViewById(R.id.location_info_card);
        permissionCard = findViewById(R.id.permission_card);
        errorCard = findViewById(R.id.error_card);
        textLatitude = findViewById(R.id.text_latitude);
        textLongitude = findViewById(R.id.text_longitude);
        textAccuracy = findViewById(R.id.text_accuracy);
        textError = findViewById(R.id.text_error);
        buttonGrantPermission = findViewById(R.id.button_grant_permission);
    }

    /**
     * Configura el callback para recibir actualizaciones de ubicación
     */
    private void setupLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(@NonNull LocationResult locationResult) {
                if (locationResult.getLastLocation() != null) {
                    currentLocation = locationResult.getLastLocation();
                    updateUIWithLocation(currentLocation);
                    updateMapLocation(currentLocation);
                }
            }
        };
    }

    /**
     * Configura el launcher para solicitar permisos
     */
    private void setupPermissionLauncher() {
        requestPermissionLauncher = registerForActivityResult(
                new ActivityResultContracts.RequestMultiplePermissions(),
                result -> {
                    Boolean fineLocationGranted = result.get(Manifest.permission.ACCESS_FINE_LOCATION);
                    Boolean coarseLocationGranted = result.get(Manifest.permission.ACCESS_COARSE_LOCATION);

                    if (Boolean.TRUE.equals(fineLocationGranted) || Boolean.TRUE.equals(coarseLocationGranted)) {
                        // Permisos concedidos
                        onPermissionsGranted();
                    } else {
                        // Permisos denegados
                        onPermissionsDenied();
                    }
                }
        );
    }

    /**
     * Verifica y solicita permisos de ubicación
     */
    private void checkAndRequestPermissions() {
        if (hasLocationPermissions()) {
            onPermissionsGranted();
        } else {
            showPermissionRequiredState();
        }
    }

    /**
     * Verifica si la app tiene permisos de ubicación
     */
    private boolean hasLocationPermissions() {
        return ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                == PackageManager.PERMISSION_GRANTED
                || ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION)
                == PackageManager.PERMISSION_GRANTED;
    }

    /**
     * Solicita permisos de ubicación al usuario
     */
    private void requestLocationPermission() {
        if (ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            // Mostrar explicación al usuario
            new AlertDialog.Builder(this)
                    .setTitle(R.string.location_permission_required)
                    .setMessage("Esta aplicación necesita acceso a tu ubicación para mostrarte tu posición actual en el mapa.")
                    .setPositiveButton("Aceptar", (dialog, which) -> launchPermissionRequest())
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        } else {
            launchPermissionRequest();
        }
    }

    /**
     * Lanza la solicitud de permisos
     */
    private void launchPermissionRequest() {
        requestPermissionLauncher.launch(new String[]{
                Manifest.permission.ACCESS_FINE_LOCATION,
                Manifest.permission.ACCESS_COARSE_LOCATION
        });
    }

    /**
     * Maneja el caso cuando los permisos son concedidos
     */
    private void onPermissionsGranted() {
        hidePermissionRequiredState();
        checkGPSEnabled();
    }

    /**
     * Maneja el caso cuando los permisos son denegados
     */
    private void onPermissionsDenied() {
        showPermissionRequiredState();
        
        // Si el usuario denegó permanentemente, ofrecer ir a settings
        if (!ActivityCompat.shouldShowRequestPermissionRationale(this, Manifest.permission.ACCESS_FINE_LOCATION)) {
            new AlertDialog.Builder(this)
                    .setTitle("Permisos necesarios")
                    .setMessage("Los permisos de ubicación fueron denegados. ¿Deseas abrir la configuración para habilitarlos?")
                    .setPositiveButton("Configuración", (dialog, which) -> openAppSettings())
                    .setNegativeButton("Cancelar", (dialog, which) -> dialog.dismiss())
                    .create()
                    .show();
        }
    }

    /**
     * Abre la configuración de la aplicación
     */
    private void openAppSettings() {
        Intent intent = new Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
        Uri uri = Uri.fromParts("package", getPackageName(), null);
        intent.setData(uri);
        startActivity(intent);
    }

    /**
     * Verifica si el GPS está habilitado
     */
    private void checkGPSEnabled() {
        LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
        boolean isGPSEnabled = locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER);
        boolean isNetworkEnabled = locationManager.isProviderEnabled(LocationManager.NETWORK_PROVIDER);

        if (!isGPSEnabled && !isNetworkEnabled) {
            showGPSDisabledDialog();
        } else {
            startLocationUpdates();
        }
    }

    /**
     * Muestra diálogo informando que el GPS está desactivado
     */
    private void showGPSDisabledDialog() {
        new AlertDialog.Builder(this)
                .setTitle(R.string.location_gps_disabled)
                .setMessage("El GPS está desactivado. ¿Deseas habilitarlo?")
                .setPositiveButton(R.string.location_enable_gps, (dialog, which) -> {
                    Intent intent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
                    startActivity(intent);
                })
                .setNegativeButton("Cancelar", (dialog, which) -> {
                    dialog.dismiss();
                    showError(getString(R.string.location_gps_disabled));
                })
                .create()
                .show();
    }

    /**
     * Inicia las actualizaciones de ubicación en tiempo real
     */
    private void startLocationUpdates() {
        if (!hasLocationPermissions()) {
            return;
        }

        showLoading();

        LocationRequest locationRequest = new LocationRequest.Builder(
                Priority.PRIORITY_HIGH_ACCURACY, UPDATE_INTERVAL)
                .setMinUpdateIntervalMillis(FASTEST_INTERVAL)
                .build();

        try {
            fusedLocationClient.requestLocationUpdates(
                    locationRequest,
                    locationCallback,
                    Looper.getMainLooper()
            );

            // Obtener última ubicación conocida
            fusedLocationClient.getLastLocation()
                    .addOnSuccessListener(this, location -> {
                        if (location != null) {
                            currentLocation = location;
                            updateUIWithLocation(location);
                            updateMapLocation(location);
                            hideLoading();
                        }
                    })
                    .addOnFailureListener(this, e -> {
                        hideLoading();
                        showError(getString(R.string.location_error) + ": " + e.getMessage());
                    });
        } catch (SecurityException e) {
            hideLoading();
            showError(getString(R.string.location_error));
        }
    }

    /**
     * Detiene las actualizaciones de ubicación
     */
    private void stopLocationUpdates() {
        if (fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
    }

    /**
     * Actualiza la UI con la información de ubicación
     */
    private void updateUIWithLocation(Location location) {
        if (location == null) return;

        // Formatear coordenadas
        String latitudeStr = String.format("%.6f°", location.getLatitude());
        String longitudeStr = String.format("%.6f°", location.getLongitude());
        String accuracyStr = String.format("%.0f %s", location.getAccuracy(), getString(R.string.location_meters));

        textLatitude.setText(latitudeStr);
        textLongitude.setText(longitudeStr);
        textAccuracy.setText(accuracyStr);

        locationInfoCard.setVisibility(View.VISIBLE);
        hideError();
    }

    /**
     * Actualiza la posición del marcador en el mapa
     */
    private void updateMapLocation(Location location) {
        if (googleMap == null || location == null) return;

        LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());

        // Limpiar marcadores anteriores y agregar nuevo
        googleMap.clear();
        googleMap.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getString(R.string.location_current)));

        // Animar cámara a la ubicación actual
        googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 15f));
    }

    @Override
    public void onMapReady(@NonNull GoogleMap map) {
        googleMap = map;

        // Configurar UI del mapa
        try {
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.getUiSettings().setMyLocationButtonEnabled(true);
            googleMap.getUiSettings().setCompassEnabled(true);

            // Habilitar capa de ubicación si hay permisos
            if (hasLocationPermissions()) {
                googleMap.setMyLocationEnabled(true);
            }
        } catch (SecurityException e) {
            showError(getString(R.string.location_error));
        }

        // Si ya tenemos ubicación, actualizarla en el mapa
        if (currentLocation != null) {
            updateMapLocation(currentLocation);
        }
    }

    // UI State Management

    private void showLoading() {
        loadingOverlay.setVisibility(View.VISIBLE);
    }

    private void hideLoading() {
        loadingOverlay.setVisibility(View.GONE);
    }

    private void showPermissionRequiredState() {
        permissionCard.setVisibility(View.VISIBLE);
        locationInfoCard.setVisibility(View.GONE);
        errorCard.setVisibility(View.GONE);
    }

    private void hidePermissionRequiredState() {
        permissionCard.setVisibility(View.GONE);
    }

    private void showError(String message) {
        textError.setText(message);
        errorCard.setVisibility(View.VISIBLE);
        locationInfoCard.setVisibility(View.GONE);
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show();
    }

    private void hideError() {
        errorCard.setVisibility(View.GONE);
    }

    // Lifecycle

    @Override
    protected void onResume() {
        super.onResume();
        if (hasLocationPermissions()) {
            checkGPSEnabled();
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        stopLocationUpdates();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopLocationUpdates();
    }
}
