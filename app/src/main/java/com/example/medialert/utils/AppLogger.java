package com.example.medialert.utils;

import android.util.Log;

/**
 * Clase centralizada para logging en toda la aplicación
 * Proporciona métodos convenientes para diferentes niveles de log
 */
public class AppLogger {
    
    private static final String TAG_PREFIX = "MediAlert_";
    private static final boolean LOGGING_ENABLED = true; // Cambiar a false para desactivar logs en producción
    
    /**
     * Log de información
     */
    public static void i(String tag, String message) {
        if (LOGGING_ENABLED) {
            Log.i(TAG_PREFIX + tag, message);
        }
    }
    
    /**
     * Log de debug
     */
    public static void d(String tag, String message) {
        if (LOGGING_ENABLED) {
            Log.d(TAG_PREFIX + tag, message);
        }
    }
    
    /**
     * Log de warning
     */
    public static void w(String tag, String message) {
        if (LOGGING_ENABLED) {
            Log.w(TAG_PREFIX + tag, message);
        }
    }
    
    /**
     * Log de error
     */
    public static void e(String tag, String message) {
        if (LOGGING_ENABLED) {
            Log.e(TAG_PREFIX + tag, message);
        }
    }
    
    /**
     * Log de error con excepción
     */
    public static void e(String tag, String message, Throwable throwable) {
        if (LOGGING_ENABLED) {
            Log.e(TAG_PREFIX + tag, message, throwable);
        }
    }
    
    /**
     * Log de verbose
     */
    public static void v(String tag, String message) {
        if (LOGGING_ENABLED) {
            Log.v(TAG_PREFIX + tag, message);
        }
    }
    
    /**
     * Log de ciclo de vida de Activity
     */
    public static void lifecycle(String activityName, String lifecycleMethod) {
        if (LOGGING_ENABLED) {
            Log.d(TAG_PREFIX + "Lifecycle", activityName + " - " + lifecycleMethod);
        }
    }
    
    /**
     * Log de eventos de usuario
     */
    public static void userEvent(String eventName, String details) {
        if (LOGGING_ENABLED) {
            Log.i(TAG_PREFIX + "UserEvent", eventName + ": " + details);
        }
    }
    
    /**
     * Log de navegación
     */
    public static void navigation(String from, String to) {
        if (LOGGING_ENABLED) {
            Log.i(TAG_PREFIX + "Navigation", "From " + from + " to " + to);
        }
    }
}
