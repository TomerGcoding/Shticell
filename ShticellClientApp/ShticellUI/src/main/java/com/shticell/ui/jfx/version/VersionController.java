package com.shticell.ui.jfx.version;

import com.shticell.ui.jfx.sheetOperations.SheetOperationController;
import dto.SheetDTO;
import com.shticell.ui.jfx.sheet.SheetGridManager;
import javafx.fxml.FXML;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.util.List;

public class VersionController {

    private SheetGridManager sheetGridManager;
    @FXML
    private ComboBox<Integer> versionSelectorComboBox;
    private VersionRequests requests;
    private String sheetName;

    @FXML
    private void initialize() {
        versionSelectorComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                showSelectedVersion(newValue);
            }
        });
        requests = new VersionRequests(this);

    }

    public void setSheetGridManager(SheetGridManager sheetGridManager) {
        this.sheetGridManager = sheetGridManager;
    }

    public void setSheetName (String sheetName) {
        this.sheetName = sheetName;
    }

    public void addVersion(int newVersionNumber) {
        List<Integer> versions = versionSelectorComboBox.getItems();
        for (int i = 1; i <= newVersionNumber; i = i + 1)
        {
            if (!versions.contains(i))
            {
                versionSelectorComboBox.getItems().add(i);
            }
        }
    }

    public void clearAllVersions() {
        versionSelectorComboBox.getItems().clear();
    }

    private void showSelectedVersion(Integer selectedVersion) {
        if (versionSelectorComboBox.getValue() != null) {
            requests.showVersion(sheetName, selectedVersion);
        }
    }

    protected void showSheetPopup(SheetDTO sheetDTO) {
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

    public void showErrorAlert(String filteringError, String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(filteringError);
        alert.setContentText(s);
        alert.showAndWait();
    }

}