package com.shticell.ui.gui.header;

import com.shticell.engine.Engine;
import com.shticell.engine.EngineImpl;
import com.shticell.engine.dto.SheetDTO;
import com.shticell.ui.utils.SheetPrinter;
import jakarta.xml.bind.JAXBException;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;


import java.io.File;


public class HeaderController {

    private Engine engine= new EngineImpl();

    @FXML
    private Label filePathLabel;

    @FXML
    private TextField cellIDTextField;

    @FXML
    private TextField originalValueTextField;

    @FXML
    private Label cellVersionLabel;

    @FXML
    private ComboBox<Integer> versionPickerComboBox;

    @FXML
    private void handleLoadFileButtonAction() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML File");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML Files", "*.xml"));
        File selectedFile = fileChooser.showOpenDialog(null);

        if (selectedFile != null) {
            try {
                engine.loadSheetFile(selectedFile.getAbsolutePath());
                filePathLabel.setText(selectedFile.getAbsolutePath());
                versionPickerComboBox.getItems().clear();
                versionPickerComboBox.getItems().add(1);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Error");
                alert.setHeaderText("Failed to load file");
                alert.setContentText(e.getMessage());
                alert.showAndWait();
            }
        }
    }

    @FXML
    private void handleUpdateCellButtonAction() {
        String cellID = cellIDTextField.getText();
        String originalValue = originalValueTextField.getText();
        try {
            engine.setCell(cellID, originalValue);
            versionPickerComboBox.getItems().add(versionPickerComboBox.getItems().size()+ 1);
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to update cell");
            alert.setContentText(e.getMessage());
        }
    }

    @FXML
    private void handleVersionPickerAction(){
        int selectedVersion = versionPickerComboBox.getSelectionModel().getSelectedItem();
        try {
            SheetDTO sheetDTO = engine.showChosenVersion(selectedVersion);
            SheetPrinter.printSheet(sheetDTO);
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to update version");
            alert.setContentText(e.getMessage());
        }
    }
}
