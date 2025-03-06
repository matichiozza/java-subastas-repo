package com.example.demo.modelo;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.stereotype.Service;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

@Service
public class GeocodingService {

    private static final String API_KEY = "a4cd16373b671db13de4a66436975d7d"; // Tu API Key de PositionStack

    public LatLng obtenerLatitudLongitud(String direccion) {
        try {
            // Codificar la dirección para que sea válida en la URL
            String direccionCodificada = URLEncoder.encode(direccion, StandardCharsets.UTF_8.toString());

            // Crear la URL con la dirección codificada
            String urlString = "http://api.positionstack.com/v1/forward?access_key=" + API_KEY + "&query=" + direccionCodificada + "&limit=1";

            URL url = new URL(urlString);

            HttpURLConnection connection = (HttpURLConnection) url.openConnection();
            connection.setRequestMethod("GET");
            connection.setConnectTimeout(5000);
            connection.setReadTimeout(5000);

            BufferedReader in = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String inputLine;
            StringBuilder response = new StringBuilder();
            while ((inputLine = in.readLine()) != null) {
                response.append(inputLine);
            }
            in.close();

            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode rootNode = objectMapper.readTree(response.toString());

            if (rootNode.has("data") && rootNode.get("data").isArray()) {
                JsonNode dataNode = rootNode.get("data").get(0);
                double latitud = dataNode.get("latitude").asDouble();
                double longitud = dataNode.get("longitude").asDouble();

                return new LatLng(latitud, longitud);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;  // Si no se pudo obtener latitud y longitud
    }
}
