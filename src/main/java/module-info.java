module com.javafx.currencyproject {
    requires javafx.controls;
    requires javafx.fxml;

    requires org.controlsfx.controls;
    requires com.dlsc.formsfx;
    requires org.kordamp.bootstrapfx.core;
    requires java.net.http;
    requires com.fasterxml.jackson.databind;
    requires MaterialFX;
    requires javafx.web;
    requires org.kordamp.ikonli.javafx;
    requires org.kordamp.ikonli.fontawesome6;

    opens com.javafx.currencyproject to javafx.fxml, javafx.controls;
    exports com.javafx.currencyproject;
    exports com.javafx.currencyproject.controllers;
    exports com.javafx.currencyproject.entities;
    exports com.javafx.currencyproject.utilities;
    opens com.javafx.currencyproject.controllers to javafx.fxml;


}