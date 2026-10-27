package com.ucv.semaforo;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class WeatherService {
    // Coordenadas geográficas exactas de la UCV Sede San Juan de Lurigancho (-12.0315, -77.0032)
    private static final String API_URL = "https://api.open-meteo.com/v1/forecast?latitude=-12.0315&longitude=-77.0032&current=temperature_2m,relative_humidity_2m,apparent_temperature,uv_index&timezone=America%2FLima";

    public String fetchSJLWeatherData() {
        try {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(API_URL))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
            return response.body();
        } catch (Exception e) {
            return "{\"error\": \"Error al consultar la estacion meteorologica de SJL\"}";
        }
    }
}