package com.example.conversionmonedas;

import com.google.gson.Gson;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;

import java.net.URL;
import java.util.ResourceBundle;

public class HelloController implements Initializable {
    // Define la constante API_KEY aquí (private static final String).
    private static final String API_KEY = "4c676285d295f9699116e504";

    // Instancia de ServicioMoneda (private final ServicioMoneda)
    private final ServicioMoneda servicioMoneda = new ServicioMoneda();

    @FXML
    private TextField cantidadInput;

    @FXML
    private ComboBox<String> comboMonedaOrigen;

    @FXML
    private ComboBox<String> comboMonedaDestino;

    @FXML
    private Label resultadoLabel;

    @FXML
    public void convertirMonto(ActionEvent actionEvent) {
        // Obtener la cantidad a convertir
        final String cantidad = cantidadInput.getText();

        // Validar si el input esta vacio.
        if (cantidad.isBlank()) {
            resultadoLabel.setText("ERROR: Por favor, ingresa una cantidad para convertir.");
            return;
        }
        resultadoLabel.setText("Convirtiendo " + cantidad + "...");

        // Obtener datos del ComboBox
        String codigoOrigen = comboMonedaOrigen.getValue();
        String codigoDestino = comboMonedaDestino.getValue();

        // Validar si los comboBox estan vacios.
        if (codigoOrigen == null || codigoDestino == null) {
            resultadoLabel.setText("ERROR: Por favor, selecciona dos divisas.");
            return;
        }

        double monto;
        try {
            // Intentamos convertir la cadena a un número.
            monto = Double.parseDouble(cantidad);

            Task<ConversionRateResponse> task = new Task<ConversionRateResponse>() {
                @Override
                protected ConversionRateResponse call() throws Exception {
                    // Obtener JSON (Paso Lento de Red)
                    String datosJson = servicioMoneda.obtenerDatosDeConversion(codigoOrigen, codigoDestino, API_KEY);

                    // Manejo de Error
                    if (datosJson == null) {
                        throw new RuntimeException("No se pudo obtener la conversion");
                    }

                    // Analizar el JSON (Paso Rápido)
                    return ServicioMoneda.analizarJson(datosJson);
                }
            };

            Thread thread = new Thread(task);
            thread.setDaemon(true); // Permite que el programa termine si solo queda este hilo
            thread.start();         // Comienza la ejecución del método call() en el fondo.

            task.setOnSucceeded( e -> {
                ConversionRateResponse conversionRateResponse = task.getValue();
                double conversion = monto * conversionRateResponse.conversion_rate;

                String resultadoFinal = monto + " " + codigoOrigen +
                        " equivalen a: " +
                        String.format("%.2f", conversion) + " " + codigoDestino;

                resultadoLabel.setText(resultadoFinal);
            });

            task.setOnFailed(e -> {
                Throwable error = task.getException();

                System.err.println("La conversion fallo: " + error.getMessage());

                resultadoLabel.setText("Error al hacer la conversion.");
            });


        } catch (NumberFormatException e) {
            // Si la conversión falla (el usuario puso letras), entramos aquí.
            resultadoLabel.setText("ERROR: La cantidad debe de ser un número valido.");
            return;
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Agregar opciones al ComboBox
        comboMonedaOrigen.getItems().addAll("MXN", "ARS", "CLP", "COP", "PEN", "UYU", "VES", "PYG", "USD");
        comboMonedaDestino.getItems().addAll("MXN", "ARS", "CLP", "COP", "PEN", "UYU", "VES", "PYG", "USD");
    }
}
