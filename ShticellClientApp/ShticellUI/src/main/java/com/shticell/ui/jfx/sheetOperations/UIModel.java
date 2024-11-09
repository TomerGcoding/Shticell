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
import java.util.List;
import java.util.Map;

public class UIModel {

    private final StringProperty fullPath;
    private final StringProperty name;
    private final BooleanProperty isDynamicAnalysisMode;
    private final BooleanProperty isLoading;
    private final BooleanProperty isThereNewVersion;
    private IntegerProperty selectedCellVersion;
    private Map<String,StringProperty> cellIdtoCellValue;
    private StringProperty selectedCellId;
    private StringProperty selectedCellOriginalValue;
    private StringProperty selectedCellLastUserUpdating;

    public UIModel(Tab sheetNameTab,
                   Button updateSelectedCellValueButton, GridPane sheetGridPane,
                   Label currentCellLabel, TextField selectedCellOriginalValueTextField,
                   Label lastVersionUpdateLabel,Label cellUpdatedByLabel, AnchorPane versionSelectorComponent,
                   Button sortSheetButton,
                   Button filterSheetButton,Button showLatestVersionButton) {
        this.fullPath = new SimpleStringProperty( );
        this.name = new SimpleStringProperty( );
        this.isDynamicAnalysisMode = new SimpleBooleanProperty(false );
        this.selectedCellId = new SimpleStringProperty( );
        this.selectedCellOriginalValue = new SimpleStringProperty( );
        this.selectedCellVersion = new SimpleIntegerProperty();
        this.selectedCellLastUserUpdating = new SimpleStringProperty();
        this.isLoading = new SimpleBooleanProperty( false );
        this.isThereNewVersion = new SimpleBooleanProperty( false );
        sheetNameTab.textProperty().bind( this.name );
        updateSelectedCellValueButton.disableProperty().set(false);
        versionSelectorComponent.disableProperty().bind( this.isDynamicAnalysisMode);
        selectedCellOriginalValueTextField.disableProperty().bind(this.isLoading);
        currentCellLabel.textProperty().bind( this.selectedCellId );
        selectedCellOriginalValueTextField.textProperty().bindBidirectional( this.selectedCellOriginalValue );
        lastVersionUpdateLabel.textProperty().bind(this.selectedCellVersion.asString());
        cellUpdatedByLabel.textProperty().bind(this.selectedCellLastUserUpdating);
        sortSheetButton.disableProperty().bind( this.isDynamicAnalysisMode);
        filterSheetButton.disableProperty().bind( this.isDynamicAnalysisMode);
        showLatestVersionButton.disableProperty().bind( this.isDynamicAnalysisMode);
        showLatestVersionButton.visibleProperty().bind( this.isThereNewVersion);
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

    public BooleanProperty isDynamicAnalysisModeProperty( ) {
        return this.isDynamicAnalysisMode;
    }

    public StringProperty selectedCellLastUserUpdatingProperty() {return this.selectedCellLastUserUpdating;}

    public BooleanProperty isThereNewVersionProperty() {
        return this.isThereNewVersion;
    }
}
