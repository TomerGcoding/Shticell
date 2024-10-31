package com.shticell.ui.jfx.sheetsManagement;

import com.shticell.ui.jfx.main.MainController;
import com.shticell.ui.jfx.sheetOperations.SheetOperationController;
import dto.SheetDTO;
import dto.UserAccessDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.util.Callback;

import java.io.File;
import java.util.HashMap;
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
    private TableColumn<SheetDTO, String> accessPermissionColumn;
    @FXML
    private TableColumn<SheetDTO, Void> actionColumn;

    @FXML
    private TableView<UserAccessDTO> permissionsTable;
    @FXML
    private TableColumn<UserAccessDTO, String> userNameColumn;
    @FXML
    private TableColumn<UserAccessDTO, String> permissionColumn;

    @FXML
    private Button loadXMLFileButton;
    @FXML
    private Label permissionLabel;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label progressLabel;
    @FXML
    private Label shticellLabel;
    @FXML
    private Label userNameLabel;

    private MainController mainController;
    private SheetOperationController sheetOperationController;
    private Map<String, SheetDTO> sheets = new HashMap<>();
    private ManagementRequests requests;
    private String userName;

    @FXML
    private void initialize() {
        requests = new ManagementRequests(this);
        initializeSheetsTable();
        initializePermissionsTable();
        setupRowSelectionListener();
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        this.userName = mainController.getUserName();
        userNameLabel.setText("Hello, " + userName + "!");
        requests.getActiveSheets();
    }

    public void setSheetOperationController(SheetOperationController sheetOperationController) {
        this.sheetOperationController = sheetOperationController;
    }

    private void initializeSheetsTable() {
        // Setting up the TableView columns with PropertyValueFactory
        sheetNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSheetName()));
        uploadedByColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOwner()));
        sheetSizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSize())));
        accessPermissionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserPermission(userName)));

        // Setting up the "Actions" column with a button
        actionColumn.setCellFactory(createButtonCellFactory());
    }

    private void initializePermissionsTable() {
        // Setting up the permissions TableView columns
        userNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserName()));
        permissionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAccessPermission()));
    }

    private void setupRowSelectionListener() {
        // Listening for selection changes in the sheets table
        activeSheetsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updatePermissionsTable(newValue);
            }
        });
    }

    private void updatePermissionsTable(SheetDTO sheet) {
        // Update the permissions table with the users and their access permissions
        permissionsTable.getItems().clear();
        permissionsTable.getItems().addAll(sheet.getSheetUsersAccess().getUsersAccess());
    }

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

                requests.uploadFile(file, new ManagementRequests.UploadCallback() {
                    @Override
                    public void onUploadSuccess(SheetDTO sheet) {
                        Platform.runLater(() -> {
                            progressBar.setVisible(false);
                            progressBar.setManaged(false);
                            progressLabel.setVisible(false);
                            progressLabel.setManaged(false);
                            addSheet(sheet);
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

    public void addSheet(SheetDTO sheet) {
        sheets.put(sheet.getSheetName(), sheet);
        activeSheetsTable.getItems().add(sheet);
    }

    public void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void populateSheetsTable(Map<String, SheetDTO> allSheets) {
        allSheets.values().forEach(this::addSheet);
    }

    public void updateSheet(SheetDTO sheet) {
        sheets.put(sheet.getSheetName(), sheet);
    }
}
