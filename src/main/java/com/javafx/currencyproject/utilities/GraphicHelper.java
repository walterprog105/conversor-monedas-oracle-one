package com.javafx.currencyproject.utilities;

import com.javafx.currencyproject.entities.Currency;
import javafx.geometry.Orientation;
import javafx.geometry.Pos;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.text.Font;
import javafx.scene.paint.Color;

import java.util.Objects;


public class GraphicHelper {

    public static ImageView getImageView(Image img, int widthImage) {

        if (img != null) {
            ImageView imgView = new ImageView(img);
            imgView.setFitWidth(widthImage);
            imgView.setFitHeight(25);
            imgView.setSmooth(true);
            //imgView.setPreserveRatio(true);

            return imgView;
        } else {
            return null;
        }


    }

    public static HBox generateHBoxCell(Currency currency, boolean isEnglish) {
        String currencyName = isEnglish ? currency.getEnglishName() : currency.getSpanishName();
        String stringForLabel = currency.getCode() + " - " + currencyName;

        Label lblCurrencyText = new Label(stringForLabel);
        lblCurrencyText.setFont(new Font(12));
        lblCurrencyText.setTextFill(Color.BLACK);

        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setPrefWidth(3);
        separator.setMaxHeight(Double.MAX_VALUE);

        HBox hbox = new HBox(getImageView(currency.getIconCurrency(), 40), separator, lblCurrencyText);
        hbox.setSpacing(10);
        hbox.setAlignment(Pos.CENTER_LEFT);

        return hbox;
    }

    public static HBox generateSearchBox(boolean isEnglish) {
        TextField txtCurrencySearch = new TextField();
        txtCurrencySearch.setFont(new Font(12));
        txtCurrencySearch.setPromptText(isEnglish ? "Search currency..." : "Buscar una moneda...");
        txtCurrencySearch.setPrefWidth(300);
        txtCurrencySearch.setEditable(true);
        //txtCurrencySearch.requestFocus();


        Separator separator = new Separator(Orientation.VERTICAL);
        separator.setPrefWidth(3);
        separator.setMaxHeight(Double.MAX_VALUE);

        HBox hbox = new HBox(txtCurrencySearch, separator, getImageView(new Image(Objects.requireNonNull(GraphicHelper.class.getResource("/images/lupa.png")).toString()), 25));
        hbox.setAlignment(Pos.CENTER_LEFT);
        hbox.setSpacing(10);

        return hbox;
    }
}