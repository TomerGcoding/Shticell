package com.shticell.ui.jfx.sheetsManagement;
import com.shticell.ui.jfx.main.MainController;
import com.shticell.ui.jfx.sheet.SheetGridManager;
import com.shticell.ui.jfx.sheetOperations.SheetOperationController;
import com.shticell.ui.jfx.sheetOperations.UIModel;
import dto.SheetDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetsManagementController {

    @FXML
    private Label activeSheetLabel;

    @FXML
    private GridPane  activeSheetsGridPaneComponent ;

    @FXML
    private Label chosenFileFullPathLabel;

    @FXML
    private Button loadXMLFileButton;

    @FXML
    private Label permissionLabel;

    @FXML
    private Label permissionsGridPaneComponent;

    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private Label shticellLabel;

    @FXML
    private Label userNameLabel;


    private SheetOperationController sheetOperationController;
    private MainController mainController;
    private Map<String, SheetDTO> sheets = new HashMap<>();
    private ManagementRequests requests;


    @FXML
    private void initialize()
    {
        requests = new ManagementRequests(this);
        requests.getActiveSheets();
        updateSheetsTable();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    protected void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void addSheet(SheetDTO sheet, String userName) {
        mainController.getSheetOperationController().addSheet(sheet);
        addSheetToTable(sheet.getSheetName(), userName);
    }

    public void updateSheetsTable () {
        requests.getActiveSheets();
    }

    public void populateSheetsTable(Map<String, List<SheetDTO>> sheets) {
        for (Map.Entry<String, List<SheetDTO>> entry : sheets.entrySet()) {
            String userName = entry.getKey();
            for (SheetDTO sheet : entry.getValue()) {
                String sheetName = sheet.getSheetName();
                addSheetToTable(sheetName, userName);
            }
        }
    }

    public void addSheetToTable(String sheetName, String userName) {
        // Get the next available row index
        int newRowIndex = activeSheetsGridPaneComponent.getRowCount();

        // Create the new elements to add
        Label sheetNameLabel = new Label(sheetName);
        Label userNameLabel = new Label(userName);
        CheckBox checkBox = new CheckBox();

        // Add the elements to the new row
        activeSheetsGridPaneComponent.add(sheetNameLabel, 0, newRowIndex); // Column 0
        activeSheetsGridPaneComponent.add(userNameLabel, 1, newRowIndex);  // Column 1
        activeSheetsGridPaneComponent.add(checkBox, 2, newRowIndex);

        // Column 2
    }

    @FXML
    public void loadXMLFile(ActionEvent event) {
        mainController.getSheetOperationController().loadXMLFile(event);

    }
}
