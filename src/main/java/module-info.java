module com.example.javaproject {
    requires javafx.controls;
    requires javafx.fxml;
    requires com.google.gson;
    requires java.desktop;

    opens com.example.javaproject to javafx.fxml, com.google.gson;
    exports com.example.javaproject;
}