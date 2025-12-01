module com.example.conversionmonedas {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.net.http;
    requires com.google.gson;


    opens com.example.conversionmonedas to javafx.fxml;
    exports com.example.conversionmonedas;
}