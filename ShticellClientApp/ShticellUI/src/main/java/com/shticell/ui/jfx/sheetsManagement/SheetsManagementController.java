package com.shticell.ui.jfx.sheetsManagement;

import com.shticell.ui.jfx.main.MainController;
import com.shticell.ui.jfx.sheetOperations.SheetOperationController;
import dto.SheetDTO;
import dto.UserAccessDTO;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.SortedList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.util.Callback;
import javafx.concurrent.Task;
import javafx.stage.FileChooser;

import java.io.IOException;
import java.util.*;
import java.io.File;
import java.util.Timer;
import java.util.function.Consumer;

import static com.shticell.ui.jfx.utils.Constants.REFRESH_RATE;

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
    private TableColumn<SheetDTO, Void> sheetsActionColumn;
    @FXML
    private TableView<UserAccessDTO> permissionsTable;
    @FXML
    private TableColumn<UserAccessDTO, String> userNameColumn;
    @FXML
    private TableColumn<UserAccessDTO, String> currentPermissionColumn;
    @FXML
    private TableColumn<UserAccessDTO, String> requestedPermissionColumn;
    @FXML
    private TableColumn<UserAccessDTO, String> permissionStatusColumn;
    @FXML
    private TableColumn<UserAccessDTO, String> permissionActionColumn;
    @FXML
    private ComboBox<String> permissionComboBox;
    @FXML
    private Button requestPermissionButton;
    @FXML
    private Label permissionLabel;
    @FXML
    private Label shticellLabel;
    @FXML
    private Label userNameLabel;

    private MainController mainController;



    private SheetOperationController sheetOperationController;
    private Map<String, SheetDTO> sheets = new HashMap<>();
    private ManagementRequests requests;
    private String userName;
    private SheetDTO currentlySelectedSheet;
    private ObservableList<UserAccessDTO> userAccessList;


    @FXML
    private void initialize() {
        requests = new ManagementRequests(this);
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
        this.userName = mainController.getUserName();
        initializeSheetsTable();
        initializePermissionsTable();
        setupRowSelectionListener();
        userNameLabel.setText("Hello, " + userName + "!");
        requests.getActiveSheets();
        startSheetsRefresher();
    }

    public void setSheetOperationController(SheetOperationController sheetOperationController) {
        this.sheetOperationController = sheetOperationController;
    }

    private void initializeSheetsTable() {
        sheetNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSheetName()));
        uploadedByColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getOwner()));
        sheetSizeColumn.setCellValueFactory(cellData -> new SimpleStringProperty(String.valueOf(cellData.getValue().getSize())));
        accessPermissionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserPermission(userName)));

        centerTableColumnText(sheetNameColumn);
        centerTableColumnText(uploadedByColumn);
        centerTableColumnText(sheetSizeColumn);
        centerTableColumnText(accessPermissionColumn);

        sheetsActionColumn.setCellFactory(createButtonCellFactory());
    }

    private <T> void centerTableColumnText(TableColumn<T, String> column) {
        column.setCellFactory(tc -> {
            TableCell<T, String> cell = new TableCell<>() {
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText(item);
                        setStyle("-fx-alignment: CENTER;"); // Center the text
                    }
                }
            };
            return cell;
        });
    }


    private void initializePermissionsTable() {
        userAccessList = FXCollections.observableArrayList();

        SortedList<UserAccessDTO> sortedList = new SortedList<>(userAccessList);
        sortedList.setComparator(Comparator.comparing(UserAccessDTO::getUserName));

        permissionsTable.setItems(sortedList);

        userNameColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getUserName()));
        currentPermissionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAccessPermission()));
        requestedPermissionColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getRequestedAccessPermission()));
        permissionStatusColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAccessPermissionStatus()));

        centerTableColumnText(userNameColumn);
        centerTableColumnText(currentPermissionColumn);
        centerTableColumnText(requestedPermissionColumn);
        centerTableColumnText(permissionStatusColumn);

        permissionActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button approveButton = new Button("Approve");
            private final Button rejectButton = new Button("Reject");
            private final HBox actionButtons = new HBox(approveButton, rejectButton);

            {
                actionButtons.setSpacing(5);

                approveButton.setOnAction(event -> {
                    UserAccessDTO userAccess = getTableView().getItems().get(getIndex());
                    if (userAccess != null) {
                        handleApproveRequest(userAccess);
                    }
                });

                rejectButton.setOnAction(event -> {
                    UserAccessDTO userAccess = getTableView().getItems().get(getIndex());
                    if (userAccess != null) {
                        handleRejectRequest(userAccess);
                    }
                });
            }

            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);

                // Check if the current item is non-empty and the user has writer permissions
                if (empty || getIndex() >= getTableView().getItems().size()) {
                    setGraphic(null);
                } else {
                    UserAccessDTO userAccess = getTableView().getItems().get(getIndex());
                    if (hasOwnerPermission() && permissionRequested(userAccess)) {
                        System.out.println("suppose to see the buttons");
                        setGraphic(actionButtons); // Show the buttons only if the user is the owner
                    } else {
                        setGraphic(null);
                    }
                }
            }
        });
    }

    private boolean permissionRequested(UserAccessDTO accessPermission) {
        if (accessPermission == null) {
            return false;
        }
        String status = accessPermission.getAccessPermissionStatus();
        String requestedPermission = accessPermission.getRequestedAccessPermission();
        if (status != null && requestedPermission != null) {
            System.out.println("status is not null in permissionRequested");
            return status.equalsIgnoreCase("Pending");
        }
        return false;
    }

    private boolean hasOwnerPermission() {
        if (currentlySelectedSheet == null) {
            return false;
        }
        return currentlySelectedSheet.getOwner().equals(userName);
    }


    private void setupRowSelectionListener() {
        activeSheetsTable.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                currentlySelectedSheet = newValue; // Update the currently selected sheet
                updatePermissionsTable(newValue);  // Update the permissions table with the new sheet's data
            }
        });
    }


    private void updatePermissionsTable(SheetDTO sheet) {
        if (sheet != null) {
            userAccessList.clear();
            userAccessList.addAll(sheet.getSheetUsersAccess().getUsersAccess());
        }
    }


    private Callback<TableColumn<SheetDTO, Void>, TableCell<SheetDTO, Void>> createButtonCellFactory() {
        return param -> new TableCell<>() {
            private final Button openButton = new Button("Open Sheet");

            {
                // Set up the button action
                openButton.setOnAction(event -> {
                    SheetDTO sheet = getTableView().getItems().get(getIndex());
                    if (sheet != null) {
                        sheetOperationController.loadSheet(sheet);
                        sheetOperationController.show();
                        sheetOperationController.startUpdatesRefresher();
                    }
                });

                // Apply hover effect for the button when permission is "None"
                openButton.setOnMouseEntered(event -> {
                    if (openButton.isDisabled()) {
                        openButton.setStyle("-fx-background-color: lightgrey; -fx-cursor: not-allowed;");
                    }
                });

                openButton.setOnMouseExited(event -> {
                    if (openButton.isDisabled()) {
                        openButton.setStyle(""); // Reset style when the mouse exits
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);

                if (empty) {
                    setGraphic(null);
                } else {
                    SheetDTO sheet = getTableView().getItems().get(getIndex());
                    if (sheet != null) {
                        String accessPermission = sheet.getUserPermission(userName);
                        if ("None".equalsIgnoreCase(accessPermission)) {
                            openButton.setDisable(true);
                            openButton.setStyle("-fx-background-color: lightgrey; -fx-cursor: not-allowed;");
                        } else {
                            openButton.setDisable(false);
                            openButton.setStyle("");
                        }
                        setGraphic(openButton);
                    }
                }
            }
        };
    }

    @FXML
    private void handleRequestPermission(ActionEvent event) {
        String requestedPermission = permissionComboBox.getValue();
        if (currentlySelectedSheet != null && requestedPermission != null) {
            requests.requestAccessPermission(currentlySelectedSheet.getSheetName(), requestedPermission);
        } else {
            showErrorAlert("No Sheet Selected", "Please select a sheet from the table to request permission.");
        }
    }


    private void handleApproveRequest(UserAccessDTO userAccess) {
        if (currentlySelectedSheet != null) {
            System.out.println("handle approve request in management controller");
            requests.approveAccessPermission(currentlySelectedSheet.getSheetName(), userAccess);
        }
    }

    private void handleRejectRequest(UserAccessDTO userAccess) {
        if (currentlySelectedSheet != null) {
            requests.rejectAccessPermission(currentlySelectedSheet.getSheetName(), userAccess);

            userAccess.setAccessPermissionStatus("Rejected");
            refreshPermissionsTable();
        }
    }

    private void refreshPermissionsTable() {
        permissionsTable.refresh();
    }


    @FXML
    public void loadXMLFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File file = fileChooser.showOpenDialog(null);

        if (file != null) {
            try {
                uploadFile(file);
            } catch (IOException e) {
                showErrorAlert("Upload Error", "An error occurred while uploading the file.");
            }
        }
    }

    private void uploadFile(File file) throws IOException {

        requests.uploadFile(file, new ManagementRequests.UploadCallback() {
            @Override
            public void onUploadSuccess(SheetDTO sheet) {
                Platform.runLater(() -> {
                    addSheet(sheet);
                });
            }

            @Override
            public void onUploadFailed(String errorMessage) {
                Platform.runLater(() -> {
                    showErrorAlert("Upload Error", errorMessage);
                });
            }
        });
    }

    public void addSheet(SheetDTO sheet) {
        if (sheets.containsKey(sheet.getSheetName())) {
            sheets.put(sheet.getSheetName(), sheet);
        } else {
            sheets.put(sheet.getSheetName(), sheet);
            activeSheetsTable.getItems().add(sheet);
        }

    }

    public void showErrorAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    public void updateSheet(SheetDTO sheet) {
        sheets.put(sheet.getSheetName(), sheet);
        populateSheetsTable(sheets);
    }

    public void startSheetsRefresher() {
    Consumer<Map<String, SheetDTO>> updateSheetsConsumer = this::populateSheetsTable;
    SheetsRefresher sheetsRefresher = new SheetsRefresher(this, updateSheetsConsumer);
    Timer timer = new Timer(true); // Use a daemon timer
    timer.scheduleAtFixedRate(sheetsRefresher, REFRESH_RATE, REFRESH_RATE);
}

    public void populateSheetsTable(Map<String, SheetDTO> updatedSheets) {
        String selectedSheetName = currentlySelectedSheet != null ? currentlySelectedSheet.getSheetName() : null;
        activeSheetsTable.getItems().clear();
        sheets.clear();

        // Check if the map has data
        if (updatedSheets.isEmpty()) {
            return;
        }

        updatedSheets.values().forEach(sheet -> {
            sheets.put(sheet.getSheetName(), sheet);
            activeSheetsTable.getItems().add(sheet);
        });

        activeSheetsTable.refresh();
        if (selectedSheetName != null) {
           currentlySelectedSheet = updatedSheets.get(selectedSheetName);
        }
        updatePermissionsTable(currentlySelectedSheet);
        refreshPermissionsTable();

    }
}
