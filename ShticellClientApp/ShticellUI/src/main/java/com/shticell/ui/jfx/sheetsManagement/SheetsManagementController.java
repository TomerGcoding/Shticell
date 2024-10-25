package com.shticell.ui.jfx.sheetsManagement;
import com.shticell.ui.jfx.sheet.SheetGridManager;
import com.shticell.ui.jfx.sheetOperations.UIModel;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ProgressBar;
import javafx.scene.layout.GridPane;
import javafx.stage.FileChooser;

import java.io.File;

public class SheetsManagementController {

    @FXML
    private Label activeSheetLabel;

    @FXML
    private GridPane activeSheetsGridPaneComponent;

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

    private sheetOperationController sheetOperationController;


    @FXML
    private void initialize()
    {
        userNameLabel.setText("User: " + UIModel.getInstance().getUserName());
        activeSheetLabel.setText("Active Sheet: " + UIModel.getInstance().getActiveSheetName());
        permissionLabel.setText("Permission: " + UIModel.getInstance().getPermission());
        permissionsGridPaneComponent.setText("Permissions: " + UIModel.getInstance().getPermissions());
        sheetOperationController = new sheetOperationController();
        sheetOperationController.setSheetOperationListener(this::showSheetOperation);
    }

}
