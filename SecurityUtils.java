package com.ucv.semaforo;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
import java.util.regex.Pattern;

public class SecurityUtils {
    // Patrón institucional obligatorio de la UCV
    private static final Pattern UCV_EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9._%+-]+@ucvvirtual\\.edu\\.pe$");

    // LISTA BLANCA (Whitelist) oficial de usuarios autorizados
    private static final Set<String> AUTHORIZED_WHITELIST = new HashSet<>(Arrays.asList(
        "pingar@ucvvirtual.edu.pe"
        // Agrega más correos institucionales autorizados aquí si lo necesitas
    ));

    public static boolean validateUCVEmail(String email) {
        if (email == null || email.trim().isEmpty()) {
            return false;
        }
        String cleanEmail = email.trim().toLowerCase();
        
        // Validación estricta: formato institucional Y pertenecer a la lista blanca
        return UCV_EMAIL_PATTERN.matcher(cleanEmail).matches() && AUTHORIZED_WHITELIST.contains(cleanEmail);
    }

    public static String sanitizeInput(String input) {
        if (input == null) return "";
        return input.replaceAll("[<>\"'%;()&+]", "").trim();
    }
}