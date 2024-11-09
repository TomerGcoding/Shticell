package com.shticell.ui.jfx.sheetOperations;

import com.shticell.ui.jfx.sheetsManagement.SheetsRefresher;
import dto.CellDTO;
import dto.CoordinateDTO;
import dto.SheetDTO;
import com.shticell.ui.jfx.main.MainController;
import com.shticell.ui.jfx.sheet.SheetGridManager;
import com.shticell.ui.jfx.version.VersionController;
import com.shticell.ui.jfx.range.RangeController;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Insets;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Dialog;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.util.Pair;

import java.io.IOException;
import java.util.*;
import java.util.List;
import java.util.function.Consumer;

import static com.shticell.ui.jfx.utils.Constants.REFRESH_RATE;

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
    private Label shticellLabel;
    @FXML
    private Label currentCellLabel;
    @FXML
    private Label currentCellVersionLabel;
    @FXML
    private TextField selectedCellOriginalValueTextField;
    @FXML
    private Tab sheetTab;
    @FXML
    private TabPane sheetTabPane;
    @FXML
    private Button updateSelectedCellValueButton;
    @FXML
    private Label userNameLabel;
    @FXML
    private Button switchToManagementPageBtn;
    @FXML
    private Button dynamicAnalysisButton;
    @FXML
    private Slider dynamicAnalysisSlider;
    @FXML
    private Label cellUpdatedByLabel;
    @FXML
    private Button showLatestVersionButton;


    private MainController mainController;
    private UIModel uiModel;
    private GridPane sheetGridPane = new GridPane();
    private SheetGridManager gridManager;
    private ObjectProperty<Label> selectedCell;
    private RangeController rangeController;
    private SheetDTO sheet;
    private Map<String, SheetDTO> sheetNameTosheet = new HashMap<>();
    private SheetRequests requests = new SheetRequests();
    private boolean dynamicAnalysisMode = false;
    private SheetDTO originalSheet;


    @FXML
    private void initialize() {
        try {
            // Initialize the requests object and set the controller reference
            requests.setController(this);

            // Initialize the UI model with the required components, excluding the file path label
            uiModel = new UIModel(
                    sheetTab,
                    updateSelectedCellValueButton,
                    sheetGridPane,
                    currentCellLabel,
                    selectedCellOriginalValueTextField,
                    currentCellVersionLabel,
                    cellUpdatedByLabel,
                    versionSelectorComponent,
                    sortSheetButton,
                    filterSheetButton,
                    showLatestVersionButton
            );
            gridManager = new SheetGridManager(sheetGridPane, uiModel, this);
            selectedCell = new SimpleObjectProperty<>();
            selectedCell.addListener((observable, oldValue, newValue) -> {
                if (oldValue != null) {
                    oldValue.setId(null);
                }
                if (newValue != null) {
                    newValue.setId("selected-cell");
                }
            });
            versionSelectorComponentController.setSheetGridManager(gridManager);
           // versionSelectorComponentController.setSheetName(sheet.getSheetName());
            changeStyleComboBox.getItems().addAll(1, 2, 3);
            changeStyleComboBox.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
                if (newValue != null) {
                    applyStyles(newValue);
                }
            });
            initializeAnimationsCheckbox();
            initializeSortSheetButton();
            initializeFilterSheetButton();
            initializeDynamicAnalysisButton();
            initializeShowLatestVersionButton();
            createRangeController();
        } catch (Exception e) {
            showErrorAlert("Initialization Error", "An error occurred while initializing the sheet operation controller: " + e.getMessage());
            e.printStackTrace();}
    }

    public void setUserName (String userName) {
        userNameLabel.setText("Hello, " + userName + "!");
    }

    // Method to handle a sheet being loaded and displayed
    public void loadSheet (SheetDTO sheet){
        this.sheet = sheet;
        uiModel.nameProperty().setValue(sheet.getSheetName());
        uiModel.selectedCellIdProperty().set(null);
        uiModel.selectedCellOriginalValueProperty().set(null);
        uiModel.selectedCellVersionProperty().set(0);
        versionSelectorComponentController.clearAllVersions();
        versionSelectorComponentController.addVersion(sheet.getCurrVersion());
        uiModel.isDynamicAnalysisModeProperty().setValue(false);
        gridManager.createSheetGridPane(sheet);
        sheetTab.setContent(sheetGridPane);
        rangeController.addLoadedRange(sheet);
        versionSelectorComponentController.setSheetName(sheet.getSheetName());
    }

    @FXML
    public void updateSelectedCellValue (ActionEvent event) {
        if (uiModel.isThereNewVersionProperty().get()) {
            showErrorAlert("Update Error", "Please update to the sheet latest version.");
            return;
        }
        if (selectedCell.get() == null) {
            throw new IllegalStateException("Please select a cell to update.");
        }
        if (isCellChanged(currentCellLabel.getText())) {
            try {
                if (!dynamicAnalysisMode) {
                    String cellId = currentCellLabel.getText();
                    String cellValue = selectedCellOriginalValueTextField.getText();
                    requests.updateCellRequest(sheet.getSheetName(), cellId, cellValue);
                }
                else {
                    String cellId = currentCellLabel.getText();
                    String cellValue = Double.toString(dynamicAnalysisSlider.getValue());
                    exitDynamicAnalysisMode();
                    requests.updateCellRequest(sheet.getSheetName(), cellId, cellValue);
                }
            } catch (Exception e) {
                showErrorAlert("Update Error", "An error occurred while updating the cell: " + e.getMessage());
            }
        }
    }

    protected void showUpdatedSheet(String cellId) {

        versionSelectorComponentController.addVersion(sheet.getCurrVersion());
        CellDTO updatedCell = sheet.getCell(cellId);
        uiModel.cellIdProperty(currentCellLabel.getText()).setValue(updatedCell.getEffectiveValue().toString());
        uiModel.selectedCellOriginalValueProperty().set(selectedCellOriginalValueTextField.getText());
        uiModel.selectedCellVersionProperty().setValue(updatedCell.getVersion());
        uiModel.selectedCellLastUserUpdatingProperty().set(updatedCell.getUserNameToUpdate());
        // Update the chain of influenced cells
        updateInfluencedCells(updatedCell);
    }

    private void updateInfluencedCells(CellDTO cell) {
        for (String influencedCellId : cell.getInfluencingOn()) {
            // Update the influenced cell's value in the UI
            CellDTO influencedCell = sheet.getCell(influencedCellId);
            uiModel.cellIdProperty(influencedCellId).setValue(influencedCell.getEffectiveValue().toString());

            // Recursively update cells influenced by this cell
            updateInfluencedCells(influencedCell);
        }
    }

    public void setSheet(SheetDTO sheet) {
        this.sheet = sheet;
    }

    public void updateSheet (SheetDTO sheet) {
        this.sheet = sheet;
        mainController.updateSheet(sheet);
    }

    private boolean isCellChanged(String cellId) {
        CellDTO cell = sheet.getCell(cellId);
        boolean changed = true;
        if (cell == null) {
            return true;
        }
        if (cell.getOriginalValue().equals(selectedCellOriginalValueTextField.getText().trim())) {
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
            requests.sortSheetRequest(sheet.getSheetName(), range, columns);
        });
    }

    protected void showSortedSheetDialog(SheetDTO sortedSheet) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Sorted Sheet");
        GridPane grid = new GridPane();
        gridManager.createReadOnlySheetGridPane(grid, sortedSheet);
        grid.getStylesheets().add(getClass().getResource("/com/shticell/ui/jfx/sheet/" + gridManager.getActiveStyleSheet()).toExternalForm());
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    //Methods to handle the filter sheet feature
    private void initializeFilterSheetButton() {filterSheetButton.setOnAction(e -> showFilterDialog());}

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
                requests.filterSheetRequest(sheet.getSheetName(), range, columns, selectedValues);
            } catch (Exception ex) {
                showErrorAlert("Filtering Error", "An error occurred while filtering the sheet: " + ex.getMessage());
            }
        });
    }

    protected void showFilteredSheetDialog(SheetDTO filteredSheet) {
        Dialog<Void> dialog = new Dialog<>();
        dialog.setTitle("Filtered Sheet");
        GridPane grid = new GridPane();
        gridManager.createReadOnlySheetGridPane(grid, filteredSheet);
        grid.getStylesheets().add(getClass().getResource("/com/shticell/ui/jfx/sheet/" + gridManager.getActiveStyleSheet()).toExternalForm());
        dialog.getDialogPane().setContent(grid);
        dialog.getDialogPane().getButtonTypes().add(ButtonType.CLOSE);

        dialog.showAndWait();
    }

    //Method to color range cells upon click on the range name in the range component
    public void colorRangeCells(List<String> rangeCellIds) {gridManager.colorRangeCells(rangeCellIds);}

    //Method to show an error alert of any kind
    protected void showErrorAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }


    public BorderPane getMainBorderPane() { return mainBorderPane;}

    //Method to create and set the range component and controller
    private void createRangeController() {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shticell/ui/jfx/range/range.fxml"));
        try {
            VBox rangeView = loader.load();
            mainBorderPane.setRight(rangeView);
            rangeController = loader.getController();
            rangeController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    //Method to apply the selected style to the sheet
    private void applyStyles(int styleNumber) {
        String mainStylesheet = String.format("main%d.css", styleNumber);

        mainBorderPane.getStylesheets().clear();
        mainBorderPane.getStylesheets().add(getClass().getResource(mainStylesheet).toExternalForm());

        gridManager.setSheetStyle(styleNumber);
    }

    //Method to add a mouse click event to a cell in the sheet grid pane
    public void addMouseClickEventForCell(String cellID, Label label) {
        label.setOnMouseClicked(event -> {
            if(!dynamicAnalysisMode) {
                gridManager.resetCellBorders();
                selectedCell.set(label);
                int cellRow = CoordinateDTO.cellIdToIndex(cellID)[0];
                int cellCol = CoordinateDTO.cellIdToIndex(cellID)[1];
                CellDTO cell = sheet.getCell(cellRow, cellCol);
                uiModel.selectedCellOriginalValueProperty().set(cell == null ? "" : cell.getOriginalValue());
                uiModel.selectedCellVersionProperty().set(cell == null ? 0 : cell.getVersion());
                uiModel.selectedCellLastUserUpdatingProperty().set(cell == null ? "" : cell.getUserNameToUpdate());
                uiModel.selectedCellIdProperty().set(cellID);
                gridManager.highlightDependenciesAndInfluences(cell);
                AnimationManager.animateCellSelection(label);
            }
        });
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    public SheetDTO getSheet() {
        ScrollPane scrollPane = new ScrollPane();
        scrollPane.setContent(sheetGridPane);
        return sheet;
    }

    public void addSheet(SheetDTO sheet) {
        sheetNameTosheet.put(sheet.getSheetName(), sheet);
    }

    // Method to show the sheet view in the main controller's content pane
    public void show() {
        mainController.setMainPanelTo(mainBorderPane);
    }

    // Method to switch to the sheets management page
    public void switchToManagementPage(ActionEvent event) {mainController.switchToSheetsManagement();}

    //Methods to handle dynamic analysis feature
    private void initializeDynamicAnalysisButton() {
        dynamicAnalysisButton.setOnAction(e -> {
            if (!dynamicAnalysisMode) {
                try {
                    showDynamicAnalysisDialog();
                }catch(NumberFormatException ex) {
                    showErrorAlert("Can't start analysis","Illegal cell selection: cell original value must be number. ");
                }
                catch (IllegalArgumentException ex) {
                    showErrorAlert("Can't start analysis","Illegal cell selection "+ ex.getMessage());
                }
            } else {
                exitDynamicAnalysisMode();
            }
        });
    }

    private void showDynamicAnalysisDialog() {
        validateCellSelection(currentCellLabel.getText(),selectedCellOriginalValueTextField.getText());
        Dialog<Pair<Pair<String, String>, String>> dialog = new Dialog<>();
        dialog.setTitle("Dynamic Analysis");
        dialog.setHeaderText("Enter minimum value, maximum value and step size");

        ButtonType startButtonType = new ButtonType("Start", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(startButtonType, ButtonType.CANCEL);

        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField minField = new TextField();
        TextField maxField = new TextField();
        TextField stepSizeField = new TextField();

        grid.add(new Label("Minimum:"), 0, 0);
        grid.add(minField, 1, 0);
        grid.add(new Label("Maximum:"), 0, 1);
        grid.add(maxField, 1, 1);
        grid.add(new Label("Step size:"), 0, 2);
        grid.add(stepSizeField, 1, 2);

        dialog.getDialogPane().setContent(grid);

        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == startButtonType) {
                return new Pair<>(new Pair<>(minField.getText(), maxField.getText()), stepSizeField.getText());
            }
            return null;
        });

        Optional<Pair<Pair<String, String>, String>> result = dialog.showAndWait();

        result.ifPresent(pair -> {
            originalSheet = sheet.copySheet();
            String min = pair.getKey().getKey();
            String max = pair.getKey().getValue();
            String stepSize = pair.getValue();
            String cellId = currentCellLabel.getText();
            String cellValue = selectedCellOriginalValueTextField.getText();
            try {
                validateAnalysisParameters( min, max, stepSize);
                defineDynamicAnalysisSlider(cellId, min, max, stepSize);
                enterDynamicAnalysisMode();
            } catch (NumberFormatException e){
                showErrorAlert("Dynamic Analysis Error", "Please make sure to enter numbers for all parameters.");
            } catch (IllegalArgumentException e) {
                showErrorAlert("Dynamic Analysis Error", e.getMessage());
            }
        });
    }

    private void validateAnalysisParameters(String min, String max, String stepSize) {

        Double minValue = Double.parseDouble(min);
        Double maxValue = Double.parseDouble(max);
        Double stepSizeDouble = Double.parseDouble(stepSize);

        if(stepSizeDouble< 0) {
            throw new IllegalArgumentException("The step size must be a positive number");
        }
        if(minValue > maxValue) {
            throw new IllegalArgumentException("The minimum value must be less than maximum value");
        }
    }

    private void validateCellSelection(String cellId,String cellValue) {

        if(cellId == null) {
            throw new IllegalArgumentException("You must select a cell first");
        }
        Double.parseDouble(cellValue);
    }

    private void defineDynamicAnalysisSlider(String cellId,String minValue, String maxValue, String stepSize) {
        dynamicAnalysisSlider.setVisible(true);
        dynamicAnalysisSlider.setValue(Double.parseDouble(minValue));
        dynamicAnalysisSlider.setMax(Double.parseDouble(maxValue));
        dynamicAnalysisSlider.setMin(Double.parseDouble(minValue));
        dynamicAnalysisSlider.setMajorTickUnit(Double.parseDouble(stepSize));
        dynamicAnalysisSlider.setBlockIncrement(Double.parseDouble(stepSize));
        dynamicAnalysisSlider.setOnMouseReleased(e->{
            if(dynamicAnalysisSlider.getValue()%dynamicAnalysisSlider.getMajorTickUnit()==0){
                requests.dynamicAnalysisRequest(originalSheet.getSheetName(),
                        cellId,
                        Double.toString(dynamicAnalysisSlider.getValue()));
            }
        });
    }

    private void enterDynamicAnalysisMode() {
        dynamicAnalysisMode = true;
        dynamicAnalysisButton.setText("Stop Analysis");
        uiModel.isDynamicAnalysisModeProperty().set(dynamicAnalysisMode);
    }

    private void exitDynamicAnalysisMode() {
        dynamicAnalysisMode = false;
        dynamicAnalysisSlider.setVisible(false);
        dynamicAnalysisButton.setText("Dynamic Analysis");
        uiModel.isDynamicAnalysisModeProperty().set(dynamicAnalysisMode);
        setSheet(originalSheet);
        showUpdatedSheet(currentCellLabel.getText());
        // Any other cleanup or reset logic
    }

    //Methods to handle update to latest version feature
    private void initializeShowLatestVersionButton() {
        showLatestVersionButton.setOnAction(e -> {
            try {
                requests.getSheetLatestVersionRequest(sheet.getSheetName());
            } catch (Exception ex) {
                showErrorAlert("Show Latest Version Error", "An error occurred while showing the latest version: " + ex.getMessage());
            }
        });
    }

    public void newVersionAvailable() {
        uiModel.isThereNewVersionProperty().set(true);
    }

    public void startUpdatesRefresher() {
        UpdatesRefresher updatesRefresher = new UpdatesRefresher(this);
        Timer timer = new Timer(true); // Use a daemon timer
        timer.scheduleAtFixedRate(updatesRefresher, REFRESH_RATE, REFRESH_RATE);
    }

    public void noNewVersion() {
        uiModel.isThereNewVersionProperty().set(false);
    }
}
