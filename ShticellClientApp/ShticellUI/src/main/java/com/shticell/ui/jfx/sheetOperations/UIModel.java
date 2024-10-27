package com.shticell.ui.jfx.sheetOperations;

import dto.CoordinateDTO;
import javafx.beans.property.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.GridPane;

import java.util.HashMap;
import java.util.Map;

public class UIModel {

    private final StringProperty fullPath;
    private final StringProperty name;
    private final BooleanProperty isFileSelected;
    private final BooleanProperty isLoading;
    private IntegerProperty selectedCellVersion;
    private Map<String,StringProperty> cellIdtoCellValue;
    private StringProperty selectedCellId;
    private StringProperty selectedCellOriginalValue;

    public UIModel(Tab sheetNameTab,
                   Button updateSelectedCellValueButton, GridPane sheetGridPane,
                   Label currentCellLabel, TextField selectedCellOriginalValueTextField,
                   Label lastVersionUpdateLabel, AnchorPane versionSelectorComponent,
                   Button sortSheetButton,
                   Button filterSheetButton) {
        this.fullPath = new SimpleStringProperty( );
        this.name = new SimpleStringProperty( );
        this.isFileSelected = new SimpleBooleanProperty(false );
        this.selectedCellId = new SimpleStringProperty( );
        this.selectedCellOriginalValue = new SimpleStringProperty( );
        this.selectedCellVersion = new SimpleIntegerProperty();
        this.isLoading = new SimpleBooleanProperty( false );
        sheetNameTab.textProperty().bind( this.name );
        updateSelectedCellValueButton.disableProperty().bind( this.isFileSelected.not().or(this.isLoading) );
        versionSelectorComponent.disableProperty().bind( this.isFileSelected.not().or(this.isLoading));
        selectedCellOriginalValueTextField.disableProperty().bind(this.isLoading);
        sheetNameTab.disableProperty().bind( this.isFileSelected.not().or(this.isLoading) );
        currentCellLabel.textProperty().bind( this.selectedCellId );
        selectedCellOriginalValueTextField.textProperty().bindBidirectional( this.selectedCellOriginalValue );
        lastVersionUpdateLabel.textProperty().bind(this.selectedCellVersion.asString());
        sortSheetButton.disableProperty().bind( this.isFileSelected.not().or(this.isLoading) );
        filterSheetButton.disableProperty().bind( this.isFileSelected.not().or(this.isLoading) );
    }

    public void initializePropertiesForEachCell(GridPane sheetGridPane) {
        cellIdtoCellValue = new HashMap<>();
        for (int row = 0; row < sheetGridPane.getRowCount(); row++) {
            for (int col = 0; col < sheetGridPane.getColumnCount() ; col++) {
                String cellID = CoordinateDTO.indexToCellId(row,col);
                cellIdtoCellValue.put(cellID,new SimpleStringProperty(""));
            }
        }
    }

    public BooleanProperty isLoadingProperty() {
        return isLoading;
    }

    public IntegerProperty selectedCellVersionProperty() {
        return this.selectedCellVersion;
    }

    public StringProperty selectedCellOriginalValueProperty() {
        return this.selectedCellOriginalValue;
    }

    public StringProperty selectedCellIdProperty() {
        return this.selectedCellId;
    }

    public StringProperty cellIdProperty(String cellId) {
        return cellIdtoCellValue.get(cellId);
    }

    public StringProperty fullPathProperty( ) {
        return this.fullPath;
    }
    public StringProperty nameProperty( ) {
        return this.name;
    }
    public BooleanProperty isFileSelectedProperty( ) {
        return this.isFileSelected;
    }

}
