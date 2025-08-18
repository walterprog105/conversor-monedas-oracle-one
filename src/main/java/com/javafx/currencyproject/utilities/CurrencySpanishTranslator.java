package com.javafx.currencyproject.utilities;

import com.javafx.currencyproject.entities.Currency;

import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

public class CurrencySpanishTranslator {
    private static final Locale locale = Locale.getDefault();
    private static final ResourceBundle resourceBundle = ResourceBundle.getBundle("currencies_es", locale);


    public static void translateToSpanish(List<Currency> l){
        for (Currency c : l) {
            c.setSpanishName(resourceBundle.
                    getString("currency." + c.getEnglishName().replace(" ", "_").
                            toLowerCase()));
        }
    }






}
