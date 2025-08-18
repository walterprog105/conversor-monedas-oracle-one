package com.javafx.currencyproject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;
import java.util.Locale;
import java.util.ResourceBundle;

public class Main extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        Locale locale = Locale.forLanguageTag("es");
        ResourceBundle resourceBundle = ResourceBundle.getBundle("messages_es", locale);
        FXMLLoader fxmlLoader = new FXMLLoader(Main.class.getResource("main-view.fxml"), resourceBundle);
        Scene scene = new Scene(fxmlLoader.load());
        scene.getStylesheets().add(getClass().getResource("/css/styles.css").toExternalForm());
        stage.setTitle("Hello!");
        stage.setScene(scene);
        stage.show();
    }

    public static void main(String[] args) {
        launch();
    }
}