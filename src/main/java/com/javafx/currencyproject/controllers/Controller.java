package com.javafx.currencyproject.controllers;

import com.javafx.currencyproject.entities.Currency;
import com.javafx.currencyproject.utilities.*;

import io.github.palexdev.materialfx.controls.MFXToggleButton;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.application.Platform;
import javafx.beans.binding.BooleanBinding;
import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.input.MouseButton;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.util.Duration;
import javafx.util.converter.DefaultStringConverter;

import java.io.IOException;
import java.net.URI;
import java.net.URL;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.text.MessageFormat;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.function.UnaryOperator;


public class Controller implements Initializable {
    @FXML private ComboBox<Currency> cmBoxSource, cmBoxDestiny;
    @FXML private MFXToggleButton tgButton;
    @FXML Label lblTextSourceComboBox, lblTextDestinyComboBox, lblAmount, lblMainTitle;
    @FXML Label lblCircleButton, lblCurrentTime, lblTimeMessage, lblResultConversion;
    @FXML Button btnConvert;
    @FXML WebView wbCurrencyChart;
    @FXML TextField txtCurrencyAmount;
    private final BooleanProperty isEnglish = new SimpleBooleanProperty(false);    
    private static final String API_KEY = "ef192fe43892e05dbc1522ae1f37cd8b";
    private WebEngine engine;
    private final Map<String,String> cache = new HashMap<>();
    private Locale currentLocale = Locale.forLanguageTag("es-AR");
    private Currency sourceCurrency;
    private Currency destinyCurrency;
    private String convertedValue;
    private String amountToconvert;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // TODO Auto-generated method stub
        System.out.println("Iniciando la carga del sistema...");

        wbCurrencyChart.setStyle("-fx-background-color: transparent;");

        TextFormatter<String> formatter = getStringTextFormatter();
        txtCurrencyAmount.setTextFormatter(formatter);

        BooleanBinding cmbSourceSelected = cmBoxSource.getSelectionModel().selectedItemProperty().isNull();
        BooleanBinding cmbDestinySelected = cmBoxDestiny.getSelectionModel().selectedItemProperty().isNull();

        BooleanBinding textValid = new BooleanBinding() {
            {
                super.bind(txtCurrencyAmount.textProperty());
            }

            @Override
            protected boolean computeValue() {
                String txt = txtCurrencyAmount.getText();
                return txt != null && !txt.isEmpty() && txt.matches("^(?:0|[1-9][0-9]*)(?:[\\\\.,][0-9]+)?$");
            }
        };

        BooleanBinding disableButton = cmbSourceSelected
                .or(cmbDestinySelected)
                .or(textValid.not());

        // Enlazamos:
        btnConvert.disableProperty().bind(disableButton);

        refreshText(resources);

        ObservableList<Currency> currenciesList;
        
        engine = wbCurrencyChart.getEngine();
        engine.load(getClass().getResource("/web/index.html").toExternalForm());


        try {
            List<Currency> listCurrencies = new DataSource().getDataList();
            CurrencySpanishTranslator.translateToSpanish(listCurrencies);
            currenciesList = FXCollections.observableList(listCurrencies);
        } catch (IOException | InterruptedException e) {
            throw new RuntimeException(e);
        }        

        Font font = Font.loadFont(getClass().getResourceAsStream("/fonts/MaterialSymbolsOutlined-VariableFont_FILL,GRAD,opsz,wght.ttf"), 20);

        String codepointHex = "ea18";
        int code = Integer.parseInt(codepointHex, 16);
        String iconText = new String(Character.toChars(code)); // convierte a String con el char correspondiente

        lblCircleButton.setText(iconText);
        lblCircleButton.setFont(Font.font(font.getFamily(), 18));
        lblCircleButton.setAlignment(Pos.CENTER);
        lblCircleButton.setPadding(new Insets(1, 0, 0, 0));
        lblCircleButton.setTextFill(Color.WHITE);

        lblCircleButton.setOnMouseClicked(evtMouse -> {
            if(evtMouse.getButton() == MouseButton.PRIMARY){
                if(cmBoxSource.getValue() == null || cmBoxDestiny.getValue() == null){
                    txtCurrencyAmount.clear();
                    Alert warning = new Alert(Alert.AlertType.WARNING);
                    warning.setTitle("¡Atención!");
                    warning.setHeaderText("Operación riesgosa");
                    warning.setContentText("Estás a punto de realizar una acción crítica.");
                    warning.showAndWait();
                }else{
                    Currency selectedCurrencyDestiny = cmBoxDestiny.getValue();
                    Currency selectedCurrencySource = cmBoxSource.getValue();
                    cmBoxSource.setValue(selectedCurrencyDestiny);
                    cmBoxDestiny.setValue(selectedCurrencySource);
                    txtCurrencyAmount.clear();
                }
            }
        });

        lblCurrentTime.getStyleClass().add("blink-clock");

        setCurrentTime(lblCurrentTime);

        FilteredList<Currency> filteredCurrenciesSource = new FilteredList<>(currenciesList, c -> true);
        FilteredList<Currency> filteredCurrenciesDestiny = new FilteredList<>(currenciesList, c -> true);

        cmBoxSource.setItems(filteredCurrenciesSource);
        cmBoxSource.setPrefHeight(35);
        DemoSkinComboBox<Currency> demoSkinComboBoxSource = new DemoSkinComboBox<>(cmBoxSource, isEnglish.get());
        cmBoxSource.setSkin(demoSkinComboBoxSource);

        cmBoxDestiny.setItems(filteredCurrenciesDestiny);
        cmBoxDestiny.setPrefHeight(35);
        DemoSkinComboBox<Currency> demoSkinComboBoxDestiny = new DemoSkinComboBox<>(cmBoxDestiny, isEnglish.get());
        cmBoxDestiny.setSkin(demoSkinComboBoxDestiny);

        BooleanBinding selectedItemfirstComboBox = cmBoxSource.getSelectionModel().selectedItemProperty().isNull();
        cmBoxDestiny.disableProperty().bind(selectedItemfirstComboBox);

        cmBoxSource.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if(newItem != null){
                filteredCurrenciesDestiny.setPredicate(c -> !c.getCode().equals(newItem.getCode()));
                lblResultConversion.setText("");
            }else{
                cmBoxSource.setPromptText(isEnglish.get() ? "Select the source currency" : "Seleccionar la moneda de origen de cambio");
                cmBoxSource.setButtonCell(new GraphCellElement(isEnglish.get(), cmBoxSource));
                cmBoxDestiny.getSelectionModel().clearSelection();
                cmBoxDestiny.setPromptText(isEnglish.get() ? "Select the target currency" : "Seleccionar la moneda de destino de cambio");
                cmBoxDestiny.setButtonCell(new GraphCellElement(isEnglish.get(), cmBoxDestiny));
            }
        });

        isEnglish.bind(tgButton.selectedProperty());

        isEnglish.addListener((obs, oldVal, newVal) -> {
            currentLocale = Locale.ENGLISH;
            ResourceBundle rb = ResourceBundle.getBundle("messages_en", currentLocale);

            if(!newVal){
                currentLocale = Locale.forLanguageTag("es");
                rb = ResourceBundle.getBundle("messages_es", currentLocale);
            }

            changeLocale(newVal ? Locale.forLanguageTag("en-US") : Locale.forLanguageTag("es-AR"));

            refreshText(rb);

            demoSkinComboBoxSource.setEnglish(newVal);
            cmBoxSource.setButtonCell(new GraphCellElement(newVal));
            demoSkinComboBoxSource.getListView().setCellFactory(lv -> new GraphCellElement(newVal));

            demoSkinComboBoxDestiny.setEnglish(newVal);
            cmBoxDestiny.setButtonCell(new GraphCellElement(newVal));
            demoSkinComboBoxDestiny.getListView().setCellFactory(lv -> new GraphCellElement(newVal));


        });

        btnConvert.setOnAction(evt -> {
            sourceCurrency = cmBoxSource.getSelectionModel().getSelectedItem();
            destinyCurrency = cmBoxDestiny.getSelectionModel().getSelectedItem();

            LocalDate currentDate = LocalDate.now();
            LocalDate firstDate = currentDate.minusDays(30);

            DataSource.currencyConversion(sourceCurrency.getCode(),
                                          destinyCurrency.getCode(),
                                          txtCurrencyAmount.getText())
                    .thenAccept(map -> {
                        convertedValue = map.get("conversion_result");
                        amountToconvert = txtCurrencyAmount.getText();
                        Platform.runLater(() -> {
                            ResourceBundle rb = ResourceBundle.getBundle(isEnglish.get() ? "messages_en" : "messages_es",
                                                isEnglish.get() ? Locale.ENGLISH : Locale.forLanguageTag("es"));
                            lblResultConversion.setText(MessageFormat.format(rb.getString("label.outputconversiontext"),
                                    txtCurrencyAmount.getText(), sourceCurrency.getCode(), (isEnglish.get() ? sourceCurrency.getEnglishName() : sourceCurrency.getSpanishName()),
                                    convertedValue, destinyCurrency.getCode(),(isEnglish.get() ? destinyCurrency.getEnglishName() : destinyCurrency.getSpanishName())));
                            txtCurrencyAmount.clear();
                        });
                    });

            updateChart(sourceCurrency.getCode(), destinyCurrency.getCode(),
                    firstDate.toString(), currentDate.toString());
        });

        System.out.println("Datos cargados en el sistema...");
    }

    private static TextFormatter<String> getStringTextFormatter() {
        UnaryOperator<TextFormatter.Change> filter = change -> {
            String newText = change.getControlNewText();
            // permitimos cadena vacía o número con separador
            if (newText.isEmpty() || newText.matches("^[0-9]*([\\\\.]?[0-9]*)?$")) {
                return change;
            }
            return null;
        };

        return new TextFormatter<>(new DefaultStringConverter(),"", filter);
    }

    public void setCurrentTime(Label lblClock){
        final boolean[] blinkingOn = {true};

        Timeline timeline = new Timeline(new KeyFrame(Duration.seconds(1), e -> {
            String[] parts = LocalTime.now().format(DateTimeFormatter.ofPattern("HH mm ss")).split(" ");
            String sep = blinkingOn[0] ? ":" : " ";
            blinkingOn[0] = !blinkingOn[0];
            lblClock.setText(parts[0] + sep + parts[1] + sep + parts[2]);
        }));

        timeline.setCycleCount(Timeline.INDEFINITE);
        timeline.play();
    }

    private void refreshText(ResourceBundle resourceBundle){
        lblMainTitle.setText(resourceBundle.getString("label.maintext"));
        tgButton.setText(resourceBundle.getString("label.toggletext"));
        lblTimeMessage.setText(resourceBundle.getString("label.time"));
        lblAmount.setText(resourceBundle.getString("label.textamount"));
        lblTextSourceComboBox.setText(resourceBundle.getString("label.textcurrencysource"));
        lblTextDestinyComboBox.setText(resourceBundle.getString("label.textcurrencydestiny"));
        btnConvert.setText(resourceBundle.getString("btn.textoutput"));
        cmBoxSource.setPromptText(resourceBundle.getString("cmbsource.prompttext"));
        cmBoxDestiny.setPromptText(resourceBundle.getString("cmbdestiny.prompttext"));
        if(!lblResultConversion.getText().isEmpty()){
            lblResultConversion.setText(MessageFormat.format(resourceBundle.getString("label.outputconversiontext"),
                    amountToconvert, sourceCurrency.getCode(), (isEnglish.get() ? sourceCurrency.getEnglishName() : sourceCurrency.getSpanishName()),
                    convertedValue, destinyCurrency.getCode(),(isEnglish.get() ? destinyCurrency.getEnglishName() : destinyCurrency.getSpanishName())));
        }
    }

    public void updateChart(String source, String currencies, String startDate, String endDate) {
        String cacheKey = source + "|" + currencies + "|" + startDate + "|" + endDate;

        if (cache.containsKey(cacheKey)) {
            injectAndDraw(cache.get(cacheKey), source, currencies);
            return;
        }

        String url = String.format(
                "https://api.exchangerate.host/timeframe?access_key=%s"
                        + "&source=%s&currencies=%s&start_date=%s&end_date=%s",
                API_KEY, source, currencies, startDate, endDate
        );

        HttpClient.newHttpClient()
                .sendAsync(
                        HttpRequest.newBuilder(URI.create(url)).GET().build(),
                        HttpResponse.BodyHandlers.ofString()
                )
                .thenApply(HttpResponse::body)
                .thenAccept(jsonBody -> {
                    cache.put(cacheKey, jsonBody);
                    injectAndDraw(jsonBody, source, currencies);
                })
                .exceptionally(ex -> { ex.printStackTrace(); return null; });
    }

    private void injectAndDraw(String jsonBody,String source, String currencies) {
        String escaped = jsonBody
                .replace("\\", "\\\\")
                .replace("'", "\\'");
        String config = String.format(
                "window.fxConfig = { source:'%s', currencies:'%s', "
                        + "locale:'%s' };",
                source, currencies, currentLocale.toLanguageTag()
        );
        String script = String.join("",
                "window.fxData = JSON.parse('", escaped, "');",
                config,
                "drawChartFromData(window.fxData, window.fxConfig);"
        );
        Platform.runLater(() -> engine.executeScript(script));
    }

    public void changeLocale(Locale newLocale) {
        // Recupera la última configuración
        Platform.runLater(() -> engine.executeScript(
                String.format(
                        "if(window.fxConfig != undefined) {window.fxConfig.locale='%s'; drawChartFromData(window.fxData, window.fxConfig);}",
                        newLocale.toLanguageTag()
                )
        ));
    }





}