package com.shticell.ui.jfx.sheetsManagement;

import com.shticell.ui.jfx.main.MainController;
import com.shticell.ui.jfx.sheetOperations.SheetOperationController;
import dto.SheetDTO;
import dto.UserAccessDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;

import java.util.HashMap;
import java.util.Map;
import java.io.File;

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
    private ComboBox<String> permissionComboBox;
    @FXML
    private Button requestPermissionButton;
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
        // Setting up the TableView columns
        sheetNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSheetName()));
        uploadedByColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOwner()));
        sheetSizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSize())));
        accessPermissionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserPermission(userName)));
        actionColumn.setCellFactory(createButtonCellFactory());
    }


    private void initializePermissionsTable() {
        // Setting up the permissions TableView columns
        userNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserName()));
        permissionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAccessPermission()));

        // Adding "Approve" and "Reject" buttons for owners
        permissionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");

            {
                approveButton.setOnAction(event -> {
                    UserAccessDTO userAccess = getTableView().getItems().get(getIndex());
                    handleApproveRequest(userAccess);
                });

                rejectButton.setOnAction(event -> {
                    UserAccessDTO userAccess = getTableView().getItems().get(getIndex());
                    handleRejectRequest(userAccess);
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || !isOwner()) {
                    setGraphic(null);
                } else {
                    HBox buttons = new HBox(approveButton, rejectButton);
                    buttons.setSpacing(5);
                    setGraphic(buttons);
                }
            }
        });
    }

    private void setupRowSelectionListener() {
        activeSheetsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updatePermissionsTable(newValue);
            }
        });
    }

    private void updatePermissionsTable(SheetDTO sheet) {
        permissionsTable.getItems().clear();
        permissionsTable.getItems().addAll(sheet.getSheetUsersAccess().getUsersAccess());
    }

    @FXML
    private void handleRequestPermission(ActionEvent event) {
        String requestedPermission = permissionComboBox.getValue();
        SheetDTO selectedSheet = activeSheetsTable.getSelectionModel().getSelectedItem();
        if (selectedSheet != null && requestedPermission != null) {
            requests.requestAccessPermission(selectedSheet.getSheetName(), requestedPermission);
        }
    }

    private void handleApproveRequest(UserAccessDTO userAccess) {
        SheetDTO selectedSheet = activeSheetsTable.getSelectionModel().getSelectedItem();
        if (selectedSheet != null) {
            // Call the engine method to approve the access
            requests.approveAccessPermission(this.userName, selectedSheet.getSheetName(), userAccess.getUserName(), userAccess.getRequestedAccessPermission());

            // Update the user access permission and status
            userAccess.setAccessPermission(userAccess.getRequestedAccessPermission());
            userAccess.setAccessPermissionStatus("Approved");
            refreshPermissionsTable();
        }
    }

    private void handleRejectRequest(UserAccessDTO userAccess) {
        SheetDTO selectedSheet = activeSheetsTable.getSelectionModel().getSelectedItem();
        if (selectedSheet != null) {
            // Call the engine method to reject the access
            requests.rejectAccessPermission(selectedSheet.getSheetName(), userAccess.getUserName(), userAccess.getRequestedAccessPermission());

            // Update the user access status
            userAccess.setAccessPermissionStatus("Rejected");
            refreshPermissionsTable();
        }
    }

    // Method to refresh the permissions table
    private void refreshPermissionsTable() {
        permissionsTable.refresh(); // Refresh the table to reflect updated data
    }


    private boolean isOwner() {
        SheetDTO selectedSheet = activeSheetsTable.getSelectionModel().getSelectedItem();
        return selectedSheet != null && selectedSheet.getOwner().equals(userName);
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
