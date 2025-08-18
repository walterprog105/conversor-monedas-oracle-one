package com.javafx.currencyproject.entities;

import javafx.scene.image.Image;

import java.util.Objects;

public class Currency {
    private String code;
    private String englishName;
    private String spanishName;
    private Image iconCurrency;

    public Currency() {}

    public Currency(String code, String englishName) {
        this(code, englishName, null);
    }

    public Currency(String code, String englishName, String spanishName) {
        this.code = code;
        this.englishName = englishName;
        this.spanishName = spanishName;
    }

    public String getCode() {
        return code;
    }

    public String getEnglishName() {
        return englishName;
    }

    public String getSpanishName() {
        return spanishName;
    }

    public void setSpanishName(String spanishName) {
        this.spanishName = spanishName;
    }

    public Image getIconCurrency() {
        return iconCurrency;
    }

    public void setIconCurrency(Image iconCurrency) {
        this.iconCurrency = iconCurrency;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Currency currency = (Currency) o;
        return code.equals(currency.code);
    }

    @Override
    public int hashCode() {
        return Objects.hash(code, englishName, spanishName);
    }

    @Override
    public String toString() {
        return englishName + " (" + code + ")";
    }
}
