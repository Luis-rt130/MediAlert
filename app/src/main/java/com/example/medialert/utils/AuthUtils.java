package com.example.medialert.utils;

import android.text.TextUtils;
import android.util.Patterns;

import com.google.firebase.auth.FirebaseAuthException;

/**
 * Clase de utilidades para validaciones de autenticación
 */
public class AuthUtils {

    // Constantes
    private static final int MIN_PASSWORD_LENGTH = 6;
    private static final int MIN_NAME_LENGTH = 3;

    /**
     * Valida formato de email
     */
    public static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Valida longitud y formato de contraseña
     */
    public static boolean isValidPassword(String password) {
        return !TextUtils.isEmpty(password) && password.length() >= MIN_PASSWORD_LENGTH;
    }

    /**
     * Valida nombre completo
     */
    public static boolean isValidName(String name) {
        return !TextUtils.isEmpty(name) && name.trim().length() >= MIN_NAME_LENGTH;
    }

    /**
     * Verifica que las contraseñas coincidan
     */
    public static boolean passwordsMatch(String password, String confirmPassword) {
        return !TextUtils.isEmpty(password) && 
               !TextUtils.isEmpty(confirmPassword) && 
               password.equals(confirmPassword);
    }

    /**
     * Obtiene mensaje de error amigable desde código de error de Firebase
     */
    public static String getFirebaseErrorMessage(Exception exception) {
        if (exception instanceof FirebaseAuthException) {
            String errorCode = ((FirebaseAuthException) exception).getErrorCode();
            
            switch (errorCode) {
                case "ERROR_INVALID_EMAIL":
                    return "El formato del correo electrónico es inválido";
                    
                case "ERROR_WRONG_PASSWORD":
                    return "La contraseña es incorrecta";
                    
                case "ERROR_USER_NOT_FOUND":
                    return "No existe una cuenta con este correo electrónico";
                    
                case "ERROR_USER_DISABLED":
                    return "Esta cuenta ha sido deshabilitada";
                    
                case "ERROR_EMAIL_ALREADY_IN_USE":
                    return "Ya existe una cuenta con este correo electrónico";
                    
                case "ERROR_WEAK_PASSWORD":
                    return "La contraseña es muy débil. Debe tener al menos 6 caracteres";
                    
                case "ERROR_NETWORK_REQUEST_FAILED":
                    return "Error de conexión. Verifica tu internet";
                    
                case "ERROR_TOO_MANY_REQUESTS":
                    return "Demasiados intentos. Intenta más tarde";
                    
                case "ERROR_OPERATION_NOT_ALLOWED":
                    return "Operación no permitida. Contacta al soporte";
                    
                case "ERROR_INVALID_CREDENTIAL":
                    return "Las credenciales proporcionadas son inválidas";
                    
                default:
                    return "Error de autenticación: " + exception.getMessage();
            }
        }
        
        return "Error: " + exception.getMessage();
    }

    /**
     * Valida todos los campos de registro
     */
    public static ValidationResult validateRegistration(String name, String email, 
                                                         String password, String confirmPassword) {
        if (!isValidName(name)) {
            return new ValidationResult(false, "El nombre debe tener al menos " + MIN_NAME_LENGTH + " caracteres");
        }
        
        if (!isValidEmail(email)) {
            return new ValidationResult(false, "Ingresa un correo electrónico válido");
        }
        
        if (!isValidPassword(password)) {
            return new ValidationResult(false, "La contraseña debe tener al menos " + MIN_PASSWORD_LENGTH + " caracteres");
        }
        
        if (!passwordsMatch(password, confirmPassword)) {
            return new ValidationResult(false, "Las contraseñas no coinciden");
        }
        
        return new ValidationResult(true, "");
    }

    /**
     * Valida campos de login
     */
    public static ValidationResult validateLogin(String email, String password) {
        if (!isValidEmail(email)) {
            return new ValidationResult(false, "Ingresa un correo electrónico válido");
        }
        
        if (TextUtils.isEmpty(password)) {
            return new ValidationResult(false, "Ingresa tu contraseña");
        }
        
        return new ValidationResult(true, "");
    }

    /**
     * Clase para resultado de validación
     */
    public static class ValidationResult {
        private final boolean isValid;
        private final String errorMessage;

        public ValidationResult(boolean isValid, String errorMessage) {
            this.isValid = isValid;
            this.errorMessage = errorMessage;
        }

        public boolean isValid() {
            return isValid;
        }

        public String getErrorMessage() {
            return errorMessage;
        }
    }
}
