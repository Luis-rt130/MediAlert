package com.example.medialert.models;

import java.util.HashMap;
import java.util.Map;

/**
 * Modelo de datos para representar un medicamento
 */
public class Medicine {
    private String id;
    private String name;
    private String dose;
    private String time;
    private String frequency;
    private String userId;
    private String photoUrl;
    private long createdAt;
    private boolean isActive;

    // Constructor vacío requerido por Firestore
    public Medicine() {
    }

    // Constructor completo
    public Medicine(String id, String name, String dose, String time, String frequency, String userId) {
        this.id = id;
        this.name = name;
        this.dose = dose;
        this.time = time;
        this.frequency = frequency;
        this.userId = userId;
        this.photoUrl = null;
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
    }

    // Constructor con foto
    public Medicine(String id, String name, String dose, String time, String frequency, String userId, String photoUrl) {
        this.id = id;
        this.name = name;
        this.dose = dose;
        this.time = time;
        this.frequency = frequency;
        this.userId = userId;
        this.photoUrl = photoUrl;
        this.createdAt = System.currentTimeMillis();
        this.isActive = true;
    }

    // Getters y Setters
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDose() {
        return dose;
    }

    public void setDose(String dose) {
        this.dose = dose;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getFrequency() {
        return frequency;
    }

    public void setFrequency(String frequency) {
        this.frequency = frequency;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public long getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(long createdAt) {
        this.createdAt = createdAt;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public String getPhotoUrl() {
        return photoUrl;
    }

    public void setPhotoUrl(String photoUrl) {
        this.photoUrl = photoUrl;
    }

    /**
     * Convierte el objeto a un Map para Firestore
     */
    public Map<String, Object> toMap() {
        Map<String, Object> map = new HashMap<>();
        if (id != null) map.put("id", id);
        if (name != null) map.put("name", name);
        if (dose != null) map.put("dose", dose);
        if (time != null) map.put("time", time);
        if (frequency != null) map.put("frequency", frequency);
        if (userId != null) map.put("userId", userId);
        if (photoUrl != null) map.put("photoUrl", photoUrl);
        map.put("createdAt", createdAt);
        map.put("isActive", isActive);
        return map;
    }

    @Override
    public String toString() {
        return "Medicine{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", dose='" + dose + '\'' +
                ", time='" + time + '\'' +
                ", frequency='" + frequency + '\'' +
                ", userId='" + userId + '\'' +
                ", photoUrl='" + photoUrl + '\'' +
                ", createdAt=" + createdAt +
                ", isActive=" + isActive +
                '}';
    }
}

