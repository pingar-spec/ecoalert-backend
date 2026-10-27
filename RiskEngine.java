package com.ucv.semaforo;

public class RiskEngine {

    public static String determineRiskLevel(double uvIndex, double temperature) {
        if (uvIndex >= 11.0 || temperature >= 32.0) {
            return "ROJO_EXTREMO";
        } else if (uvIndex >= 6.0 || temperature >= 28.0) {
            return "AMARILLO_ALTO";
        } else {
            return "VERDE_BAJO";
        }
    }

    public static String generateBunnyMessage(String userName, String riskLevel, double uvIndex, double temp) {
        String baseGreeting = "¡Hola " + userName + "! Soy Bunny-Bot, tu asistente de la UCV SJL. ";
        
        switch (riskLevel) {
            case "ROJO_EXTREMO":
                return baseGreeting + "ALERTA EXTREMA: El índice UV está en " + uvIndex + " y la temperatura en " + temp + "°C. Busca inmediatamente las rutas sombreadas del Pabellón A o la Torre Académica y usa agua de las estaciones.";
            case "AMARILLO_ALTO":
                return baseGreeting + "Atención: El índice UV es " + uvIndex + " (Alto). Se recomienda caminar por pasillos techados y mantenerse hidratado en el campus.";
            default:
                return baseGreeting + "Las condiciones climáticas son favorables en el campus San Juan de Lurigancho. La temperatura es de " + temp + "°C.";
        }
    }
}