package com.example.medialert.utils;

import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;

import java.io.ByteArrayOutputStream;
import java.util.UUID;

/**
 * Manager para manejar la subida y gestión de imágenes en Firebase Storage
 */
public class ImageStorageManager {
    
    private static final String TAG = "ImageStorageManager";
    private static ImageStorageManager instance;
    private final StorageReference storageRef;
    private final FirebaseAuth auth;
    
    private ImageStorageManager() {
        FirebaseStorage storage = FirebaseStorage.getInstance();
        storageRef = storage.getReference();
        auth = FirebaseAuth.getInstance();
    }
    
    public static synchronized ImageStorageManager getInstance() {
        if (instance == null) {
            instance = new ImageStorageManager();
        }
        return instance;
    }
    
    /**
     * Interfaz para callbacks de subida de imagen
     */
    public interface OnImageUploadListener {
        void onSuccess(String imageUrl);
        void onFailure(Exception e);
        void onProgress(double progress);
    }
    
    /**
     * Sube una imagen desde un Bitmap
     * @param bitmap La imagen a subir
     * @param listener Listener para callbacks
     */
    public void uploadMedicineImage(Bitmap bitmap, OnImageUploadListener listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            if (listener != null) {
                listener.onFailure(new Exception("Usuario no autenticado"));
            }
            return;
        }
        
        // Comprimir bitmap a JPEG
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bitmap.compress(Bitmap.CompressFormat.JPEG, 80, baos);
        byte[] data = baos.toByteArray();
        
        // Crear referencia única para la imagen
        String imageName = "medicine_" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageRef.child("medicine_photos")
                .child(user.getUid())
                .child(imageName);
        
        // Subir imagen
        UploadTask uploadTask = imageRef.putBytes(data);
        
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            if (listener != null) {
                listener.onProgress(progress);
            }
            Log.d(TAG, "Progreso de subida: " + progress + "%");
        }).addOnSuccessListener(taskSnapshot -> {
            // Obtener URL de descarga
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Log.i(TAG, "Imagen subida exitosamente: " + downloadUrl);
                if (listener != null) {
                    listener.onSuccess(downloadUrl);
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error al obtener URL de descarga", e);
                if (listener != null) {
                    listener.onFailure(e);
                }
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error al subir imagen", e);
            if (listener != null) {
                listener.onFailure(e);
            }
        });
    }
    
    /**
     * Sube una imagen desde un Uri
     * @param imageUri El URI de la imagen a subir
     * @param listener Listener para callbacks
     */
    public void uploadMedicineImage(Uri imageUri, OnImageUploadListener listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            if (listener != null) {
                listener.onFailure(new Exception("Usuario no autenticado"));
            }
            return;
        }
        
        // Crear referencia única para la imagen
        String imageName = "medicine_" + UUID.randomUUID().toString() + ".jpg";
        StorageReference imageRef = storageRef.child("medicine_photos")
                .child(user.getUid())
                .child(imageName);
        
        // Subir imagen
        UploadTask uploadTask = imageRef.putFile(imageUri);
        
        uploadTask.addOnProgressListener(taskSnapshot -> {
            double progress = (100.0 * taskSnapshot.getBytesTransferred()) / taskSnapshot.getTotalByteCount();
            if (listener != null) {
                listener.onProgress(progress);
            }
            Log.d(TAG, "Progreso de subida: " + progress + "%");
        }).addOnSuccessListener(taskSnapshot -> {
            // Obtener URL de descarga
            imageRef.getDownloadUrl().addOnSuccessListener(uri -> {
                String downloadUrl = uri.toString();
                Log.i(TAG, "Imagen subida exitosamente: " + downloadUrl);
                if (listener != null) {
                    listener.onSuccess(downloadUrl);
                }
            }).addOnFailureListener(e -> {
                Log.e(TAG, "Error al obtener URL de descarga", e);
                if (listener != null) {
                    listener.onFailure(e);
                }
            });
        }).addOnFailureListener(e -> {
            Log.e(TAG, "Error al subir imagen", e);
            if (listener != null) {
                listener.onFailure(e);
            }
        });
    }
    
    /**
     * Elimina una imagen de Firebase Storage
     * @param imageUrl URL de la imagen a eliminar
     * @param listener Listener para callbacks
     */
    public void deleteImage(String imageUrl, OnDeleteListener listener) {
        if (imageUrl == null || imageUrl.isEmpty()) {
            if (listener != null) {
                listener.onSuccess();
            }
            return;
        }
        
        try {
            StorageReference imageRef = FirebaseStorage.getInstance().getReferenceFromUrl(imageUrl);
            imageRef.delete()
                    .addOnSuccessListener(aVoid -> {
                        Log.i(TAG, "Imagen eliminada exitosamente");
                        if (listener != null) {
                            listener.onSuccess();
                        }
                    })
                    .addOnFailureListener(e -> {
                        Log.e(TAG, "Error al eliminar imagen", e);
                        if (listener != null) {
                            listener.onFailure(e);
                        }
                    });
        } catch (IllegalArgumentException e) {
            Log.e(TAG, "URL de imagen inválida: " + imageUrl, e);
            if (listener != null) {
                listener.onFailure(e);
            }
        }
    }
    
    /**
     * Interfaz para callbacks de eliminación
     */
    public interface OnDeleteListener {
        void onSuccess();
        void onFailure(Exception e);
    }
}
