package com.shticell.ui.jfx.sheetsManagement;

import com.shticell.ui.jfx.main.MainController;
import com.shticell.ui.jfx.sheetOperations.SheetOperationController;
import dto.SheetDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SheetsManagementController {

    @FXML
    private Label activeSheetLabel;

    @FXML
    private TableView<SheetDTO> activeSheetsTable;
    @FXML
    private TableColumn<SheetDTO, String> sheetNameColumn;
    @FXML
    private TableColumn<SheetDTO, String> uploadedByColumn;
    @FXML
    private TableColumn<SheetDTO, String> sheetSizeColumn;
    @FXML
    private TableColumn<SheetDTO, Void> actionColumn;  // New column for the "Open Sheet" button

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
    private void initialize() {
        requests = new ManagementRequests(this);
        initializeSheetsTable();
        requests.getActiveSheets();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public void setSheetOperationController(SheetOperationController sheetOperationController) {
        this.sheetOperationController = sheetOperationController;
    }

    private void initializeSheetsTable() {
        // Setting up the TableView columns
        sheetNameColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getSheetName()));
        uploadedByColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(cellData.getValue().getUploadedBy()));
        sheetSizeColumn.setCellValueFactory(cellData -> new SimpleObjectProperty<>(String.valueOf(cellData.getValue().getSize())));

        // Set up the "Open Sheet" button column
        actionColumn = new TableColumn<>("Actions");
        actionColumn.setCellFactory(createButtonCellFactory());
        activeSheetsTable.getColumns().add(actionColumn);
    }

    // Create a cell factory that adds a button to each row
    private Callback<TableColumn<SheetDTO, Void>, TableCell<SheetDTO, Void>> createButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button openButton = new Button("Open Sheet");

            {
                openButton.setOnAction(event -> {
                    SheetDTO sheet = getTableView().getItems().get(getIndex());
                    if (sheet != null) {
                        sheetOperationController.loadSheet(sheet);
                        sheetOperationController.show();
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(openButton);
                }
            }
        };
    }

    @FXML
    public void loadXMLFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            progressBar.setVisible(true);
            progressBar.setManaged(true);
            progressLabel.setVisible(true);
            progressLabel.setManaged(true);

            uploadFile(file);
        }
    }

    private void uploadFile(File file) {
        Task<Void> loadTask = new Task<>() {
            @Override
            protected Void call() throws Exception {
                updateMessage("Fetching file...");
                updateProgress(0.2, 1);
                Thread.sleep(1000);

                updateMessage("Loading sheet data...");
                updateProgress(0.6, 1);

                // Call the uploadFile method in requests and handle callbacks
                requests.uploadFile(file, new ManagementRequests.UploadCallback() {
                    @Override
                    public void onUploadSuccess(SheetDTO sheet) {
                        Platform.runLater(() -> {
                            progressBar.setVisible(false);
                            progressBar.setManaged(false);
                            progressLabel.setVisible(false);
                            progressLabel.setManaged(false);

                            // Add the sheet to the table
                            addSheet(sheet, "Uploaded User");  // Replace with the actual user name if needed
                        });
                    }

                    @Override
                    public void onUploadFailed(String errorMessage) {
                        Platform.runLater(() -> {
                            progressBar.setVisible(false);
                            progressBar.setManaged(false);
                            progressLabel.setVisible(false);
                            progressLabel.setManaged(false);
                            showErrorAlert("Upload Error", errorMessage);
                        });
                    }
                });

                Thread.sleep(1000);
                updateMessage("File upload successful!");
                updateProgress(1, 1);
                return null;
            }
        };

        progressBar.progressProperty().bind(loadTask.progressProperty());
        progressLabel.textProperty().bind(loadTask.messageProperty());

        Thread loadThread = new Thread(loadTask);
        loadThread.setDaemon(true);
        loadThread.start();
    }

    public void addSheet(SheetDTO sheet, String userName) {
        sheet.setUploadedBy(userName);
        activeSheetsTable.getItems().add(sheet);
    }

    public void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Populate the TableView with sheets from the server
    public void populateSheetsTable(Map<String, List<SheetDTO>> sheets) {
        for (Map.Entry<String, List<SheetDTO>> entry : sheets.entrySet()) {
            String userName = entry.getKey();
            for (SheetDTO sheet : entry.getValue()) {
                addSheet(sheet, userName);
            }
        }
    }
}
