package com.example.medialert.utils;

import android.util.Log;

import com.example.medialert.models.Medicine;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.Query;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.WriteBatch;
import com.google.firebase.firestore.FirebaseFirestoreException;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Clase de utilidad para manejar operaciones de base de datos con Firestore
 * 
 * Esta clase usa la configuración de Firebase del proyecto (medialert-c7847)
 * definida en app/google-services.json. FirebaseFirestore.getInstance() y
 * FirebaseAuth.getInstance() automáticamente usan esta configuración.
 * 
 * Los medicamentos se almacenan en la colección "medicines" de Firestore,
 * asociados al userId del usuario autenticado.
 */
public class DatabaseManager {
    private static final String TAG = "DatabaseManager";
    private static final String COLLECTION_MEDICINES = "medicines";
    
    private static DatabaseManager instance;
    private FirebaseFirestore db;
    private FirebaseAuth auth;

    private DatabaseManager() {
        // Obtiene la instancia de Firestore configurada para este proyecto
        // (usando la configuración de app/google-services.json)
        db = FirebaseFirestore.getInstance();
        // Obtiene la instancia de Auth configurada para este proyecto
        auth = FirebaseAuth.getInstance();
    }

    /**
     * Obtiene la instancia singleton de DatabaseManager
     */
    public static synchronized DatabaseManager getInstance() {
        if (instance == null) {
            instance = new DatabaseManager();
        }
        return instance;
    }

    /**
     * Obtiene la referencia a la colección de medicamentos del usuario actual
     */
    private CollectionReference getMedicinesCollection() {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            Log.w(TAG, "No hay usuario autenticado");
            return null;
        }
        return db.collection(COLLECTION_MEDICINES);
    }

    /**
     * Guarda un nuevo medicamento en Firestore
     */
    public void addMedicine(Medicine medicine, OnCompleteListener<Medicine> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            if (listener != null) {
                listener.onFailure(new Exception("Usuario no autenticado"));
            }
            return;
        }

        CollectionReference medicinesRef = getMedicinesCollection();
        if (medicinesRef == null) {
            if (listener != null) {
                listener.onFailure(new Exception("Error al obtener referencia de base de datos"));
            }
            return;
        }

        // Asegurar que el userId esté establecido
        medicine.setUserId(user.getUid());

        // Crear mapa de datos
        Map<String, Object> medicineData = medicine.toMap();
        
        // Agregar el medicamento a Firestore
        medicinesRef.add(medicineData)
                .addOnSuccessListener(documentReference -> {
                    Log.d(TAG, "Medicamento agregado con ID: " + documentReference.getId());
                    medicine.setId(documentReference.getId());
                    
                    // Actualizar el documento con el ID
                    documentReference.update("id", documentReference.getId())
                            .addOnSuccessListener(aVoid -> {
                                if (listener != null) {
                                    listener.onSuccess(medicine);
                                }
                            })
                            .addOnFailureListener(e -> {
                                Log.e(TAG, "Error al actualizar ID del medicamento", e);
                                // Aún así, consideramos éxito porque el medicamento fue creado
                                if (listener != null) {
                                    listener.onSuccess(medicine);
                                }
                            });
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al agregar medicamento", e);
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    /**
     * Obtiene todos los medicamentos activos del usuario actual
     */
    public void getMedicines(OnCompleteListener<List<Medicine>> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            if (listener != null) {
                listener.onFailure(new Exception("Usuario no autenticado"));
            }
            return;
        }

        CollectionReference medicinesRef = getMedicinesCollection();
        if (medicinesRef == null) {
            if (listener != null) {
                listener.onFailure(new Exception("Error al obtener referencia de base de datos"));
            }
            return;
        }

        // Base de la consulta (filtros por usuario y activo)
        Query baseQuery = medicinesRef
                .whereEqualTo("userId", user.getUid())
                .whereEqualTo("isActive", true);

        // Intento 1: con orderBy (requiere índice compuesto)
        baseQuery
                .orderBy("createdAt", Query.Direction.DESCENDING)
                .get()
                .addOnSuccessListener(queryDocumentSnapshots -> {
                    List<Medicine> medicines = new ArrayList<>();
                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                        Medicine medicine = document.toObject(Medicine.class);
                        medicine.setId(document.getId());
                        medicines.add(medicine);
                    }
                    Log.d(TAG, "Medicamentos obtenidos: " + medicines.size());
                    if (listener != null) {
                        listener.onSuccess(medicines);
                    }
                })
                .addOnFailureListener(e -> {
                    // Si falla por índice faltante, reintentar sin orderBy y ordenar en cliente
                    if (e instanceof FirebaseFirestoreException &&
                            ((FirebaseFirestoreException) e).getCode() == FirebaseFirestoreException.Code.FAILED_PRECONDITION) {
                        Log.w(TAG, "Índice compuesto faltante, reintentando sin orderBy. Detalle: " + e.getMessage());
                        baseQuery.get()
                                .addOnSuccessListener(queryDocumentSnapshots -> {
                                    List<Medicine> medicines = new ArrayList<>();
                                    for (QueryDocumentSnapshot document : queryDocumentSnapshots) {
                                        Medicine medicine = document.toObject(Medicine.class);
                                        medicine.setId(document.getId());
                                        medicines.add(medicine);
                                    }
                                    // Ordenar por createdAt desc en cliente
                                    Collections.sort(medicines, new Comparator<Medicine>() {
                                        @Override
                                        public int compare(Medicine o1, Medicine o2) {
                                            return Long.compare(o2.getCreatedAt(), o1.getCreatedAt());
                                        }
                                    });
                                    if (listener != null) {
                                        listener.onSuccess(medicines);
                                    }
                                })
                                .addOnFailureListener(e2 -> {
                                    Log.e(TAG, "Error al obtener medicamentos (fallback)", e2);
                                    if (listener != null) {
                                        listener.onFailure(e2);
                                    }
                                });
                    } else {
                        Log.e(TAG, "Error al obtener medicamentos", e);
                        if (listener != null) {
                            listener.onFailure(e);
                        }
                    }
                });
    }

    /**
     * Obtiene un medicamento por su ID
     */
    public void getMedicineById(String medicineId, OnCompleteListener<Medicine> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            if (listener != null) {
                listener.onFailure(new Exception("Usuario no autenticado"));
            }
            return;
        }

        CollectionReference medicinesRef = getMedicinesCollection();
        if (medicinesRef == null) {
            if (listener != null) {
                listener.onFailure(new Exception("Error al obtener referencia de base de datos"));
            }
            return;
        }

        medicinesRef.document(medicineId).get()
                .addOnSuccessListener(documentSnapshot -> {
                    if (documentSnapshot.exists()) {
                        Medicine medicine = documentSnapshot.toObject(Medicine.class);
                        if (medicine != null) {
                            medicine.setId(documentSnapshot.getId());
                            if (listener != null) {
                                listener.onSuccess(medicine);
                            }
                        } else {
                            if (listener != null) {
                                listener.onFailure(new Exception("Error al convertir documento"));
                            }
                        }
                    } else {
                        if (listener != null) {
                            listener.onFailure(new Exception("Medicamento no encontrado"));
                        }
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al obtener medicamento", e);
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    /**
     * Actualiza un medicamento existente
     */
    public void updateMedicine(Medicine medicine, OnCompleteListener<Medicine> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null || medicine.getId() == null) {
            if (listener != null) {
                listener.onFailure(new Exception("Usuario no autenticado o ID de medicamento inválido"));
            }
            return;
        }

        CollectionReference medicinesRef = getMedicinesCollection();
        if (medicinesRef == null) {
            if (listener != null) {
                listener.onFailure(new Exception("Error al obtener referencia de base de datos"));
            }
            return;
        }

        DocumentReference medicineRef = medicinesRef.document(medicine.getId());
        Map<String, Object> updates = new HashMap<>();
        
        if (medicine.getName() != null) updates.put("name", medicine.getName());
        if (medicine.getDose() != null) updates.put("dose", medicine.getDose());
        if (medicine.getTime() != null) updates.put("time", medicine.getTime());
        if (medicine.getFrequency() != null) updates.put("frequency", medicine.getFrequency());
        updates.put("isActive", medicine.isActive());

        medicineRef.update(updates)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Medicamento actualizado: " + medicine.getId());
                    if (listener != null) {
                        listener.onSuccess(medicine);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al actualizar medicamento", e);
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    /**
     * Elimina un medicamento (marca como inactivo)
     */
    public void deleteMedicine(String medicineId, OnCompleteListener<Void> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            if (listener != null) {
                listener.onFailure(new Exception("Usuario no autenticado"));
            }
            return;
        }

        CollectionReference medicinesRef = getMedicinesCollection();
        if (medicinesRef == null) {
            if (listener != null) {
                listener.onFailure(new Exception("Error al obtener referencia de base de datos"));
            }
            return;
        }

        // Marcar como inactivo en lugar de eliminar físicamente
        medicinesRef.document(medicineId).update("isActive", false)
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Medicamento eliminado (marcado como inactivo): " + medicineId);
                    if (listener != null) {
                        listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al eliminar medicamento", e);
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    /**
     * Elimina físicamente un medicamento de Firestore
     */
    public void permanentlyDeleteMedicine(String medicineId, OnCompleteListener<Void> listener) {
        FirebaseUser user = auth.getCurrentUser();
        if (user == null) {
            if (listener != null) {
                listener.onFailure(new Exception("Usuario no autenticado"));
            }
            return;
        }

        CollectionReference medicinesRef = getMedicinesCollection();
        if (medicinesRef == null) {
            if (listener != null) {
                listener.onFailure(new Exception("Error al obtener referencia de base de datos"));
            }
            return;
        }

        medicinesRef.document(medicineId).delete()
                .addOnSuccessListener(aVoid -> {
                    Log.d(TAG, "Medicamento eliminado permanentemente: " + medicineId);
                    if (listener != null) {
                        listener.onSuccess(null);
                    }
                })
                .addOnFailureListener(e -> {
                    Log.e(TAG, "Error al eliminar permanentemente medicamento", e);
                    if (listener != null) {
                        listener.onFailure(e);
                    }
                });
    }

    /**
     * Interfaz para callbacks de operaciones completadas
     */
    public interface OnCompleteListener<T> {
        void onSuccess(T result);
        void onFailure(Exception e);
    }
}

