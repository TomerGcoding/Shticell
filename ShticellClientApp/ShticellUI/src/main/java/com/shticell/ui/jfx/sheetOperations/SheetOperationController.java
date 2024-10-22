package com.shticell.ui.jfx.sheetOperations;

import com.google.gson.GsonBuilder;
import com.google.gson.Gson;
import dto.CellDTO;
import dto.CoordinateDTO;
import dto.SheetDTO;
import com.shticell.ui.jfx.main.MainController;
import com.shticell.ui.jfx.sheet.SheetGridManager;
import com.shticell.ui.jfx.utils.http.HttpClientUtil;
import com.shticell.ui.jfx.version.VersionController;
import com.shticell.ui.jfx.range.RangeController;
import dto.json.SheetDTODeserializer;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.util.Pair;
import okhttp3.*;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import static com.shticell.ui.jfx.utils.Constants.*;
public class SheetOperationController {

    @FXML
    private CheckBox animationsCheckbox;
    @FXML
    private Button filterSheetButton;
    @FXML
    private Button sortSheetButton;
    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private ComboBox<Integer> changeStyleComboBox;
    @FXML
    private VersionController versionSelectorComponentController;
    @FXML
    private AnchorPane versionSelectorComponent;
    @FXML
    private ProgressBar progressBar;
    @FXML
    private Label shticellLabel;
    @FXML
    private Label progressLabel;
    @FXML
    private Label chosenFileFullPathLabel;
    @FXML
    private Label currentCellLabel;
    @FXML
    private Label currentCellVersionLabel;
    @FXML
    private Button loadXMLFileButton;
    @FXML
    private TextField selectedCellOriginalValueTextField;
    @FXML
    private Tab sheetTab;
    @FXML
    private TabPane sheetTabPane;
    @FXML
    private Button updateSelectedCellValueButton;

    private MainController mainController;
    private GridPane loginComponent;
    private UIModel uiModel;
    private GridPane sheetGridPane  = new GridPane();
    private SheetGridManager gridManager;
    private ObjectProperty<Label> selectedCell;
    private RangeController rangeController;
    private SheetDTO sheet;
    private SheetRequests requests = new SheetRequests();

    @FXML
    private void initialize() {
        requests.setController(this);
        uiModel = new UIModel(chosenFileFullPathLabel, sheetTab,updateSelectedCellValueButton,sheetGridPane,currentCellLabel,selectedCellOriginalValueTextField,
                currentCellVersionLabel,versionSelectorComponent, sortSheetButton,filterSheetButton);
        gridManager = new SheetGridManager(sheetGridPane,uiModel, this);
        chosenFileFullPathLabel.setId("file-path");
        selectedCell = new SimpleObjectProperty<>();
        selectedCell.addListener((observable, oldValue, newValue) -> {
            if(oldValue != null) {
                oldValue.setId(null);
            }
            if(newValue != null) {
                newValue.setId("selected-cell");
            }
        });
        versionSelectorComponentController.setSheetGridManager(gridManager);
        changeStyleComboBox.getItems().addAll(1,2,3);
        changeStyleComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if(newValue != null) {
                applyStyles(newValue);
            }
        });
        initializeAnimationsCheckbox();
        initializeSortSheetButton();
        initializeFilterSheetButton();
        createRangeController();
    }

    @FXML
    public void loadXMLFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            chosenFileFullPathLabel.setVisible(false);
            progressBar.setVisible(true);
            progressBar.setManaged(true);
            progressLabel.setVisible(true);
            progressLabel.setManaged(true);
            uiModel.isLoadingProperty().set(true);
            Task<Void> loadTask = new Task<>() {
                protected Void call() throws Exception {
                    updateMessage("Fetching file...");
                    updateProgress(0.2, 1);
                    Thread.sleep(1000);

                    updateMessage("Loading sheet data...");
                    updateProgress(0.6, 1);
                    requests.uploadFile(file);
                    Thread.sleep(1000);// Simulating some work

                    updateMessage("Creating sheet grid...");
                    updateProgress(0.8, 1);
                    Thread.sleep(1000);

                    updateMessage("Sheet loaded successfully!");
                    updateProgress(1, 1);
                    return null;
                }
            };
            loadTask.setOnSucceeded(e -> {
                loadTask.getValue();
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    progressBar.setManaged(false);
                    progressLabel.setVisible(false);
                    progressLabel.setManaged(false);
                    chosenFileFullPathLabel.setVisible(true);
                    uiModel.isLoadingProperty().set(false);
                    uiModel.fullPathProperty().setValue(file.getAbsolutePath());
                    uiModel.nameProperty().setValue(sheet.getSheetName());
                    uiModel.selectedCellIdProperty().set(null);
                    uiModel.selectedCellOriginalValueProperty().set(null);
                    uiModel.selectedCellVersionProperty().set(0);
                    versionSelectorComponentController.clearAllVersions();
                    versionSelectorComponentController.addVersion(sheet.getCurrVersion());
                    uiModel.isFileSelectedProperty().setValue(true);
                    gridManager.createSheetGridPane(sheet);
                    sheetTab.setContent(sheetGridPane);
                    rangeController.addLoadedRange(sheet);
                });
            });
            loadTask.setOnFailed(e -> {
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    progressBar.setManaged(false);
                    progressLabel.setVisible(false);
                    progressLabel.setManaged(false);
                    chosenFileFullPathLabel.setVisible(true);
                    uiModel.isLoadingProperty().set(false);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                     alert.setHeaderText("Failed to load file:");
                    alert.setContentText(loadTask.getException().getMessage());
                    alert.showAndWait();
                });
            });
            progressBar.progressProperty().bind(loadTask.progressProperty());
            progressLabel.textProperty().bind(loadTask.messageProperty());

            Thread loadThread = new Thread(loadTask);
            loadThread.setDaemon(true);
            loadThread.start();
        }
    }

    @FXML
    public void updateSelectedCellValue(ActionEvent event) {
            if (selectedCell.get() == null) {
                throw new IllegalStateException("Please select a cell to update.");
            }
            if (isCellChanged(currentCellLabel.getText())) {
                try {
                String cellId = currentCellLabel.getText();
                String cellValue = selectedCellOriginalValueTextField.getText();
                requests.updateCellRequest(cellId, cellValue);
            }

//                String finalUrl = HttpUrl
//                        .parse(BASE_URL + UPDATE_CELL)
//                        .newBuilder()
//                        .addQueryParameter("cellId", cellId)
//                        .addQueryParameter("cellValue", cellValue)
//                        .build()
//                        .toString();

                //async


                //sync

//                Request request = new Request.Builder()
//                        .url(finalUrl)
//                        .get()
//                        .build();
//
//
//                try (Response response = HttpClientUtil.getHttpClient().newCall(request).execute()) {
//                    if (!response.isSuccessful()) {
//                        throw new IOException("Eror while update cell " + response);
//                    }
//
//                    String responseBody = response.body().string();
//
//                    System.out.println(responseBody);
//
//                    Gson gson = new GsonBuilder()
//                            .registerTypeAdapter(SheetDTO.class, new SheetDTODeserializer())
//                            .create();
//
//                    this.sheet = gson.fromJson(responseBody, SheetDTO.class);
//                }
                catch (Exception e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to update Cell:");
                    alert.setContentText(e.getMessage());
                    alert.showAndWait();
                }
            }
        }

    protected void showUpdatedSheet(String cellId) {
        versionSelectorComponentController.addVersion(sheet.getCurrVersion());
        CellDTO updatedCell = sheet.getCell(cellId);
        uiModel.cellIdProperty(currentCellLabel.getText()).setValue(updatedCell.getEffectiveValue().toString());
        uiModel.selectedCellOriginalValueProperty().set(selectedCellOriginalValueTextField.getText());
        uiModel.selectedCellVersionProperty().setValue(updatedCell.getVersion());

        for (String influencedCellId : updatedCell.getInfluencingOn()) {
            uiModel.cellIdProperty(influencedCellId).setValue(sheet.getCell(influencedCellId).getEffectiveValue().toString());
        }
    }


    public void setSheet (SheetDTO sheet) {
        this.sheet = sheet;
    }

    private boolean isCellChanged(String cellId){
        CellDTO cell = sheet.getCell(cellId);
        boolean changed = true;
        if(cell == null) {
            return true;
        }
        if (cell.getOriginalValue().equals(selectedCellOriginalValueTextField.getText().trim()))
        {
            changed = false;
        }
        return changed;
    }

    private void initializeAnimationsCheckbox() {
        animationsCheckbox.setSelected(false);
        animationsCheckbox.selectedProperty().addListener((observable, oldValue, newValue) -> {
            AnimationManager.setAnimationsEnabled(newValue);
            if (newValue) {
                AnimationManager.animateShticellLabel(shticellLabel);
            } else {
                shticellLabel.setTextFill(Color.BLACK);
                shticellLabel.setRotate(0);
            }
        });
    }

    private void initializeSortSheetButton() {
        sortSheetButton.setOnAction(e -> showSortDialog());
    }

    private void showSortDialog() {
        Dialog<Pair<String, String>> dialog = new Dialog<>();
        dialog.setTitle("Sort Sheet");
        dialog.setHeaderText("Enter sort range and columns");

        ButtonType sortButtonType = new ButtonType("Sort", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(sortButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField rangeField = new TextField();
        rangeField.setPromptText("e.g., A4..D6");
        TextField columnsField = new TextField();
        columnsField.setPromptText("e.g., C,D,B");

        grid.add(new Label("Range:"), 0, 0);
        grid.add(rangeField, 1, 0);
        grid.add(new Label("Columns to sort by:"), 0, 1);
        grid.add(columnsField, 1, 1);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == sortButtonType) {
                return new Pair<>(rangeField.getText(), columnsField.getText());
            }
            return null;
        });

        Optional<Pair<String, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
                String range = pair.getKey();
                String columns = pair.getValue();
                requests.sortSheetRequest(range, columns);
            });
    }

    protected void showSortedSheetDialog(SheetDTO sortedSheet) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Sorted Sheet");
        GridPane grid = new GridPane();
        gridManager.createReadOnlySheetGridPane(grid, sortedSheet);
        grid.getStylesheets().add(getClass().getResource("/com/shticell/ui/jfx/sheet/"+gridManager.getActiveStyleSheet()).toExternalForm());
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    private void initializeFilterSheetButton() {
        filterSheetButton.setOnAction(e -> showFilterDialog());
    }

    private void showFilterDialog() {
        Dialog<Pair<String, List<String>>> dialog = new Dialog<>();
        dialog.setTitle("Filter Sheet");
        dialog.setHeaderText("Enter filter range and select values for filtering");

        ButtonType filterButtonType = new ButtonType("Filter", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(filterButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField rangeField = new TextField();
        rangeField.setPromptText("e.g., A4..D6");
        TextField columnsField = new TextField();
        columnsField.setPromptText("e.g., C");

        ListView<String> uniqueValuesListView = new ListView<>();
        uniqueValuesListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        uniqueValuesListView.setDisable(true);

        Label filterLabel = new Label("Unique Values:");

        grid.add(new Label("Range:"), 0, 0);
        grid.add(rangeField, 1, 0);
        grid.add(new Label("Column to filter by:"), 0, 1);
        grid.add(columnsField, 1, 1);
        grid.add(filterLabel, 0, 2);
        grid.add(uniqueValuesListView, 1, 2);

        dialog.getDialogPane().setContent(grid);

        columnsField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    List<String> uniqueValues = sheet.getUniqueColumnValues(newValue.trim());
                    System.out.println("uniqu values in showFilterDialog: " + uniqueValues);

                    uniqueValuesListView.getItems().clear();
                    uniqueValuesListView.getItems().addAll(uniqueValues);
                    uniqueValuesListView.setDisable(false);

                } catch (Exception ex) {
                    showErrorAlert("Fetching Unique Values", "An error occurred while fetching unique values: " + ex.getMessage());
                }
            }
        });

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == filterButtonType) {
                List<String> selectedValues = new ArrayList<>(uniqueValuesListView.getSelectionModel().getSelectedItems());
                if (!selectedValues.isEmpty()) {
                    return new Pair<>(rangeField.getText(), selectedValues);
                }
            }
            return null;
        });

        Optional<Pair<String, List<String>>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            try {
                String range = pair.getKey();
                List<String> selectedValues = pair.getValue();
                String columns = columnsField.getText();
                System.out.println(columns);
                requests.filterSheetRequest(range, columns, selectedValues);
            } catch (Exception ex) {
                showErrorAlert("Filtering Error", "An error occurred while filtering the sheet: " + ex.getMessage());
            }
        });
    }

    public void colorRangeCells(List<String> rangeCellIds) {
        gridManager.colorRangeCells(rangeCellIds);
    }

    protected void showFilteredSheetDialog(SheetDTO filteredSheet) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Filtered Sheet");
        GridPane grid = new GridPane();
        gridManager.createReadOnlySheetGridPane(grid, filteredSheet);
        grid.getStylesheets().add(getClass().getResource("/com/shticell/ui/jfx/sheet/"+gridManager.getActiveStyleSheet()).toExternalForm());
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    protected void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public BorderPane getMainBorderPane() {
        return mainBorderPane;
    }

    private void createRangeController()  {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shticell/ui/jfx/range/range.fxml"));
        try{
            VBox rangeView = loader.load();
            mainBorderPane.setRight(rangeView);
            rangeController = loader.getController();
            rangeController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void applyStyles(int styleNumber) {
        String mainStylesheet = String.format("main%d.css", styleNumber);

        mainBorderPane.getStylesheets().clear();
        mainBorderPane.getStylesheets().add(getClass().getResource(mainStylesheet).toExternalForm());

        gridManager.setSheetStyle(styleNumber);
    }

    public void addMouseClickEventForCell(String cellID, Label label) {
        label.setOnMouseClicked(event->{
            gridManager.resetCellBorders();
            selectedCell.set(label);
            int cellRow = CoordinateDTO.cellIdToIndex(cellID)[0];
            int cellCol = CoordinateDTO.cellIdToIndex(cellID)[1];
            CellDTO cell = sheet.getCell(cellRow, cellCol);
            uiModel.selectedCellOriginalValueProperty().set(cell == null?"": cell.getOriginalValue());
            uiModel.selectedCellVersionProperty().set(cell == null? 0:cell.getVersion());
            uiModel.selectedCellIdProperty().set(cellID);
            gridManager.highlightDependenciesAndInfluences(cell);
            AnimationManager.animateCellSelection(label);
        });
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }
}