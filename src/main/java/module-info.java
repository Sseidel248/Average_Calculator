module com.sseidel.average_calculator {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.desktop;
    requires com.fasterxml.jackson.databind;

    opens com.sseidel.average_calculator.application to javafx.fxml;

    exports com.sseidel.average_calculator.model;
    exports com.sseidel.average_calculator.application;
}