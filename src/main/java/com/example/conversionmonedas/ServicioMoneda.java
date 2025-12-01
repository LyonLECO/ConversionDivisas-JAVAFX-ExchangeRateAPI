package com.example.conversionmonedas;

import com.google.gson.Gson;

import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

public class ServicioMoneda {

    // Atributo
    // 4c676285d295f9699116e504/pair/USD/MXN
    // private static String urlBase = "https://v6.exchangerate-api.com/v6/";

    // Metodo
    public String obtenerDatosDeConversion(String monedaOrigen, String monedaDestino, String API_KEY) {
        try {
            final String urlBase = "https://v6.exchangerate-api.com/v6/";
            String urlFinal = urlBase + API_KEY + "/pair/" + monedaOrigen + "/" + monedaDestino;


            HttpClient client = HttpClient.newBuilder().build();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(urlFinal))
                    .GET()
                    .build();

            HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());

            return response.body();
        } catch (Exception e) {
            System.err.println("Ocurrio un error al obtener la conversion: " + e.getMessage());
            return null;
        }
    }

    public static ConversionRateResponse analizarJson(String jsonString) {
        // Analizador Gson.
        Gson gson = new Gson();

        ConversionRateResponse conversionRateResponse = gson.fromJson(jsonString, ConversionRateResponse.class);

        return conversionRateResponse;
    }

}
