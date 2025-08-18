package com.javafx.currencyproject.utilities;

import com.javafx.currencyproject.entities.Currency;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;

public class GraphCellElement extends ListCell<Currency> {

    private boolean isEnglish;
    private final ComboBox<Currency> auxCmb;

    public GraphCellElement(boolean isEnglish){
        this(isEnglish, null);
    }

    public GraphCellElement(boolean isEnglish, final ComboBox<Currency> cmb){
        this.isEnglish = isEnglish;
        this.auxCmb = cmb;
    }



    public void setIsEnglish(boolean isEnglish){
        this.isEnglish = isEnglish;
    }

    @Override
    public void updateItem(Currency item, boolean empty){
        super.updateItem(item, empty);
        if(item == null || empty){
            setText(auxCmb != null ? auxCmb.getPromptText() : null);
            setGraphic(null);
        }else{
            setText(null);
            setGraphic(GraphicHelper.generateHBoxCell(item, isEnglish));
        }
    }

}
