package com.javafx.currencyproject.utilities;

import com.javafx.currencyproject.entities.Currency;
import javafx.application.Platform;
import javafx.collections.transformation.FilteredList;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.control.skin.ComboBoxListViewSkin;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.VBox;


public class DemoSkinComboBox<Currency> extends ComboBoxListViewSkin<com.javafx.currencyproject.entities.Currency> {
    private final TextField searchTextField;
    private final ListView<com.javafx.currencyproject.entities.Currency> listView;
    private boolean wasClicked = false;
    private boolean isEnglish;
    private VBox popupContent;
    private final FilteredList<com.javafx.currencyproject.entities.Currency> filteredItems;
    private final GraphCellElement listCellElement;

    public DemoSkinComboBox(final ComboBox<com.javafx.currencyproject.entities.Currency> control, boolean isEnglish){
        this(control, isEnglish, null);
    }

    public DemoSkinComboBox(final ComboBox<com.javafx.currencyproject.entities.Currency> control, boolean isEnglish, final ComboBox<com.javafx.currencyproject.entities.Currency> firstControl){
        super(control);
        this.isEnglish = isEnglish;

        if(firstControl != null){
            com.javafx.currencyproject.entities.Currency selectedItem = firstControl.getValue();
            if(selectedItem != null){
                this.filteredItems = new FilteredList<>(control.getItems(), c -> !c.getCode().equals(selectedItem.getCode()));
            }else{
                this.filteredItems = new FilteredList<>(control.getItems(), c -> true);
            }
        }else{
            this.filteredItems = new FilteredList<>(control.getItems(), c -> true);
        }

        //this.filteredItems = new FilteredList<>(control.getItems(), c -> true);
        this.listView = new ListView<>(filteredItems);
        this.listView.setPrefWidth(405);
        this.listView.setCellFactory(lv -> new GraphCellElement(isEnglish));
        this.listCellElement = new GraphCellElement(isEnglish);
        control.setButtonCell(listCellElement);
        this.searchTextField = new TextField();
        this.searchTextField.setPromptText(isEnglish ? "Enter a currency" : "Ingrese una moneda");

        configureComponents(control);

    }

    private void configureComponents(final ComboBox<com.javafx.currencyproject.entities.Currency> auxComboBox){
        searchTextField.textProperty().addListener((obs, oldText, newText) -> {
            if(newText != null && !newText.isEmpty()){
                auxComboBox.setValue(null);
                String stringToSearch = newText.toLowerCase().trim();
                filteredItems.setPredicate(c -> c.getCode().toLowerCase().contains(stringToSearch) ||
                                (isEnglish ? c.getEnglishName().toLowerCase().contains(stringToSearch) :
                                        c.getSpanishName().toLowerCase().contains(stringToSearch)));
            }else{
                filteredItems.setPredicate(c -> true);
            }
        });

        searchTextField.setOnKeyPressed(evtKey -> {
            if(evtKey.getCode() == KeyCode.ENTER || evtKey.getCode() == KeyCode.TAB){
                if(!listView.getItems().isEmpty()){
                    if(searchTextField.getText().isEmpty()){
                        com.javafx.currencyproject.entities.Currency selectedCurrency = auxComboBox.getValue();
                        if(selectedCurrency != null){
                            listView.getSelectionModel().select(selectedCurrency);
                            listView.scrollTo(selectedCurrency);
                        }
                    }else{
                        listView.getSelectionModel().selectFirst();
                    }
                    listView.requestFocus();
                }
                evtKey.consume();
            }
        });

        listView.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if(newItem != null){
                auxComboBox.setValue(newItem);
                if(wasClicked){
                    Platform.runLater(auxComboBox::hide);

                }
            }
        });

        listView.addEventFilter(MouseEvent.ANY, evtMouse -> wasClicked = evtMouse.getEventType().equals(MouseEvent.MOUSE_PRESSED));

        listView.addEventFilter(KeyEvent.KEY_PRESSED, evtKey -> {
            if(evtKey.getCode() == KeyCode.ENTER){
                com.javafx.currencyproject.entities.Currency selectedCurrency = listView.getSelectionModel().getSelectedItem();
                auxComboBox.setValue(selectedCurrency);
                auxComboBox.hide();
                evtKey.consume();
            }else if(evtKey.getCode() == KeyCode.ESCAPE && auxComboBox.isShowing()){
                auxComboBox.hide();
                evtKey.consume();
            }

        });

        listView.addEventFilter(MouseEvent.MOUSE_CLICKED, evtMouse -> {
            if(evtMouse.getButton() == MouseButton.PRIMARY){
                com.javafx.currencyproject.entities.Currency selectedCurrency = listView.getSelectionModel().getSelectedItem();
                auxComboBox.setValue(selectedCurrency);
                auxComboBox.hide();
                evtMouse.consume();
            }

        });

        auxComboBox.addEventHandler(ComboBox.ON_SHOWING, evt -> {
            if(!evt.isConsumed()){
                searchTextField.clear();
                searchTextField.setPromptText(isEnglish ? "Enter a currency" : "Ingrese una moneda");
                if(!listView.getItems().isEmpty()){
                    com.javafx.currencyproject.entities.Currency selectedCurrency = auxComboBox.getValue();
                    if(selectedCurrency != null){
                        auxComboBox.setValue(selectedCurrency);
                        listView.getSelectionModel().select(selectedCurrency);
                        listView.scrollTo(selectedCurrency);
                    }
                    listView.requestFocus();
                }
                //evt.consume();
            }
        });

        auxComboBox.addEventFilter(MouseEvent.MOUSE_PRESSED, evtMouse -> {
            if(evtMouse.getButton() == MouseButton.PRIMARY && !auxComboBox.isShowing()){
                auxComboBox.show();
                if(!listView.getItems().isEmpty()){
                    com.javafx.currencyproject.entities.Currency selectedCurrency = auxComboBox.getValue();
                    if(selectedCurrency != null){
                        listView.getSelectionModel().select(selectedCurrency);
                        listView.scrollTo(selectedCurrency);
                    }else{
                        listView.getSelectionModel().selectFirst();
                    }
                    listView.requestFocus();
                }
            }
            evtMouse.consume();
        });

        auxComboBox.addEventFilter(KeyEvent.KEY_PRESSED, evtKey -> {
            if((evtKey.getCode() == KeyCode.DOWN || evtKey.getCode() == KeyCode.UP || evtKey.getCode() == KeyCode.ENTER)
                    && !auxComboBox.isShowing()){
                auxComboBox.show();
                if(!listView.getItems().isEmpty()){
                    com.javafx.currencyproject.entities.Currency selectedCurrency = auxComboBox.getValue();
                    if(selectedCurrency != null){
                        listView.getSelectionModel().select(selectedCurrency);
                        listView.scrollTo(selectedCurrency);
                    }else{
                        listView.getSelectionModel().selectFirst();
                    }
                    listView.requestFocus();
                }
                evtKey.consume();
            }
        });

        auxComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldItem, newItem) -> {
            if(auxComboBox.isShowing()){
                com.javafx.currencyproject.entities.Currency selectedItem = auxComboBox.getValue();
                if(selectedItem != null){
                    listView.getSelectionModel().select(selectedItem);
                    listView.scrollTo(selectedItem);
                }
            }
        });

    }

    @Override
    public Node getPopupContent() {

        if(popupContent == null){
            popupContent = new VBox(5, searchTextField, listView);
            popupContent.setPadding(new Insets(5));

        }

        return popupContent;
    }

    public void setEnglish(boolean isEnglish){
        this.isEnglish = isEnglish;
    }

    public ListView<com.javafx.currencyproject.entities.Currency> getListView() {
        return listView;
    }

}
