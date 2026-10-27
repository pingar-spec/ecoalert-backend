package com.ucv.semaforo;

import com.sun.net.httpserver.HttpServer;
import com.sun.net.httpserver.HttpHandler;
import com.sun.net.httpserver.HttpExchange;

import java.io.IOException;
import java.io.OutputStream;
import java.io.InputStream;
import java.net.InetSocketAddress;

public class MainServer {
    public static void main(String[] args) throws IOException {
        // Asignación de puerto dinámico para servidores en la nube (Render / Railway)
        // Si no detecta la variable PORT del entorno, utilizará por defecto el puerto 8080 local.
        String portStr = System.getenv("PORT");
        int port = (portStr != null && !portStr.isEmpty()) ? Integer.parseInt(portStr) : 8080;

        HttpServer server = HttpServer.create(new InetSocketAddress(port), 0);

        server.createContext("/api/semaforo", new SemaforoHandler());

        server.setExecutor(null);
        System.out.println("=================================================");
        System.out.println("SERVIDOR UCV - CIBERSEGURIDAD ACTIVA EN PUERTO: " + port);
        System.out.println("=================================================");
        server.start();
    }

    static class SemaforoHandler implements HttpHandler {
        @Override
        public void handle(HttpExchange exchange) throws IOException {
            // Habilitación completa de CORS para peticiones desde GitHub Pages / Netlify
            exchange.getResponseHeaders().add("Access-Control-Allow-Origin", "*");
            exchange.getResponseHeaders().add("Access-Control-Allow-Methods", "GET, POST, OPTIONS");
            exchange.getResponseHeaders().add("Access-Control-Allow-Headers", "Content-Type");
            exchange.getResponseHeaders().add("Content-Type", "application/json; charset=UTF-8");

            // Manejo de la petición Preflight de seguridad (OPTIONS)
            if ("OPTIONS".equalsIgnoreCase(exchange.getRequestMethod())) {
                exchange.sendResponseHeaders(204, -1);
                return;
            }

            if ("POST".equalsIgnoreCase(exchange.getRequestMethod())) {
                InputStream is = exchange.getRequestBody();
                String body = new String(is.readAllBytes(), "UTF-8");
                
                String email = extractParam(body, "email").trim().toLowerCase();

                // Validación estricta de seguridad en backend
                if (!SecurityUtils.validateUCVEmail(email)) {
                    String errorJson = "{\"status\":\"ERROR\", \"message\":\"Acceso denegado. Se requiere un correo institucional autorizado de la UCV.\"}";
                    sendResponse(exchange, 403, errorJson);
                    return;
                }

                // Asignación de usuario
                String verifiedUser = deriveUserFromEmail(email);

                WeatherService weatherService = new WeatherService();
                String weatherJson = weatherService.fetchSJLWeatherData();
                
                double uvIndex = 8.5; 
                double temp = 28.0;
                String riskLevel = RiskEngine.determineRiskLevel(uvIndex, temp);
                String bunnyMessage = RiskEngine.generateBunnyMessage(verifiedUser, riskLevel, uvIndex, temp);

                String cleanJson = "{\"status\":\"SUCCESS\",\"user\":\"" + verifiedUser + 
                                   "\",\"riskLevel\":\"" + riskLevel + 
                                   "\",\"uvIndex\":" + uvIndex + 
                                   ",\"temperature\":" + temp + 
                                   ",\"bunnyMessage\":\"" + bunnyMessage + 
                                   "\",\"weatherDetails\":" + weatherJson + "}";

                sendResponse(exchange, 200, cleanJson);
            } else {
                sendResponse(exchange, 405, "{\"error\":\"Metodo no permitido\"}");
            }
        }

        private String deriveUserFromEmail(String email) {
            if (email.contains("pingar")) {
                return "Piero";
            }
            return "Estudiante";
        }

        private String extractParam(String body, String paramName) {
            try {
                int keyIdx = body.indexOf("\"" + paramName + "\"");
                if (keyIdx == -1) return "";
                int valStart = body.indexOf(":", keyIdx) + 1;
                int quoteStart = body.indexOf("\"", valStart) + 1;
                int quoteEnd = body.indexOf("\"", quoteStart);
                return body.substring(quoteStart, quoteEnd);
            } catch (Exception e) {
                return "";
            }
        }

        private void sendResponse(HttpExchange exchange, int statusCode, String responseText) throws IOException {
            byte[] responseBytes = responseText.getBytes("UTF-8");
            exchange.sendResponseHeaders(statusCode, responseBytes.length);
            OutputStream os = exchange.getResponseBody();
            os.write(responseBytes);
            os.close();
        }
    }
}