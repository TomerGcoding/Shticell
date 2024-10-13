package com.shticell.ui.jfx.version;

import com.shticell.engine.Engine;
import com.shticell.engine.dto.CellDTO;
import com.shticell.engine.dto.SheetDTO;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;
import com.shticell.ui.jfx.sheet.SheetGridManager;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VersionController {

    private SheetGridManager sheetGridManager;
    private Engine engine;
    @FXML
    private ComboBox<Integer> versionSelectorComboBox;

    @FXML
    private void initialize() {
        versionSelectorComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                showSelectedVersion(newValue);
            }
        });
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void setSheetGridManager(SheetGridManager sheetGridManager) {
        this.sheetGridManager = sheetGridManager;
    }

    public void addVersion(int newVersionNumber) {
        versionSelectorComboBox.getItems().add(newVersionNumber);
    }

    public void clearAllVersions() {
        versionSelectorComboBox.getItems().clear();
    }

    private void showSelectedVersion(Integer selectedVersion) {
        if (versionSelectorComboBox.getValue() != null) {
            SheetDTO sheetDTO = engine.showChosenVersion(selectedVersion);
            showSheetPopup(sheetDTO);
        }
    }

    private void showSheetPopup(SheetDTO sheetDTO) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Sheet Version " + sheetDTO.getCurrVersion());

        GridPane gridPane = new GridPane();
        sheetGridManager.createReadOnlySheetGridPane(gridPane,sheetDTO);
        gridPane.getStylesheets().add(getClass().getResource("/com/shticell/ui/jfx/sheet/"+sheetGridManager.getActiveStyleSheet()).toExternalForm());
        Scene scene = new Scene(gridPane);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }


}