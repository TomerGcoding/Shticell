package com.shticell.ui.jfx.main;

import com.shticell.engine.Engine;
import com.shticell.engine.EngineImpl;
import com.shticell.engine.dto.CellDTO;
import com.shticell.engine.dto.SheetDTO;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;
import com.shticell.ui.jfx.range.RangeController;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.scene.control.Label;
import javafx.stage.FileChooser;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class MainController {

    @FXML
    private ProgressBar progressBar;

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
    @FXML
    private ComboBox<Integer> versionSelectorComboBox;
    @FXML
    private BorderPane mainBorderPane;
    private Engine engine = new EngineImpl();
    private UIModel uiModel;
    private GridPane sheetGridPane;
    private Map<String,Label> cellIDtoLabel = new HashMap<>();
    private ObjectProperty<Label> selectedCell;
    private RangeController rangeController;

    @FXML
    private void initialize() {
        uiModel = new UIModel(chosenFileFullPathLabel, sheetTab,updateSelectedCellValueButton,sheetGridPane,currentCellLabel,selectedCellOriginalValueTextField,
                currentCellVersionLabel);
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
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shticell/ui/jfx/range/range.fxml"));
            Parent rangeView = loader.load();
            mainBorderPane.setRight(rangeView);
            rangeController = loader.getController();
            rangeController.setEngine(engine);
            rangeController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }

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

            Task<SheetDTO> loadTask = new Task<SheetDTO>() {
                protected SheetDTO call() throws Exception {
                    updateMessage("Fetching file...");
                    updateProgress(0.2, 1);
                    Thread.sleep(1000); // Simulating some work

                    updateMessage("Loading sheet data...");
                    updateProgress(0.6, 1);
                    SheetDTO sheetDTO = engine.loadSheetFile(file.getAbsolutePath());
                    Thread.sleep(1000); // Simulating some work

                    updateMessage("Creating sheet grid...");
                    updateProgress(0.8, 1);
                    Thread.sleep(1000); // Simulating some work

                    updateMessage("Sheet loaded successfully!");
                    updateProgress(1, 1);
                    return sheetDTO;
                }
            };
            loadTask.setOnSucceeded(e -> {
                SheetDTO sheetDTO = loadTask.getValue();
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    progressBar.setManaged(false);
                    progressLabel.setVisible(false);
                    progressLabel.setManaged(false);
                    chosenFileFullPathLabel.setVisible(true);
                    uiModel.fullPathProperty().setValue(file.getAbsolutePath());
                    uiModel.nameProperty().setValue(engine.showSheet().getSheetName());
                    versionSelectorComboBox.getItems().clear();
                    versionSelectorComboBox.getItems().add(engine.showSheet().getCurrVersion());
                    uiModel.isFileSelectedProperty().setValue(true);
                    createSheetGridPane(sheetDTO);
                });
            });
            loadTask.setOnFailed(e -> {
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    progressBar.setManaged(false);
                    progressLabel.setVisible(false);
                    progressLabel.setManaged(false);
                    chosenFileFullPathLabel.setVisible(true);
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



//    @FXML
//    public void loadXMLFile(ActionEvent event) {
//        try {
//            FileChooser fileChooser = new FileChooser();
//            fileChooser.setTitle("Open XML file");
//            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
//            File file = fileChooser.showOpenDialog(null);
//            uiModel.fullPathProperty().setValue(file.getAbsolutePath());
//            SheetDTO sheetDTO = engine.loadSheetFile(file.getAbsolutePath());
//            uiModel.nameProperty().setValue(engine.showSheet().getSheetName());
//            versionSelectorComboBox.getItems().clear();
//            versionSelectorComboBox.getItems().add(engine.showSheet().getCurrVersion());
//            uiModel.isFileSelectedProperty().setValue(true);
//            createSheetGridPane(sheetDTO);
//        }catch (Exception e){
//            Alert alert = new Alert(Alert.AlertType.ERROR);
//            alert.setTitle("Error");
//            alert.setHeaderText(null);
//            alert.setContentText(e.getMessage());
//            alert.showAndWait();
//        }
//    }

    @FXML
    public void updateSelectedCellValue(ActionEvent event) {
        try {
            if (selectedCell.get()==null) {
                throw new IllegalStateException("Please select a cell to update.");
            }
            engine.setCell(currentCellLabel.getText(), selectedCellOriginalValueTextField.getText());
            versionSelectorComboBox.getItems().add(engine.showSheet().getCurrVersion());
            SheetDTO updatedSheet = engine.showSheet();
            CellDTO updatedCell = updatedSheet.getCell(CoordinateFormatter.cellIdToIndex(currentCellLabel.getText())[0],
                    CoordinateFormatter.cellIdToIndex(currentCellLabel.getText())[1]);
            uiModel.cellIdProperty(currentCellLabel.getText()).setValue(updatedCell.getEffectiveValue().toString());
            uiModel.selectedCellOriginalValueProperty().set(selectedCellOriginalValueTextField.getText());
            uiModel.selectedCellVersionProperty().setValue(updatedCell.getVersion());

            // Update the labels of influenced cells
            for (CellDTO influencedCell : updatedCell.getInfluencingOn()) {
                String influencedCellId = influencedCell.getId();
                uiModel.cellIdProperty(influencedCellId).setValue(influencedCell.getEffectiveValue().toString());
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to update Cell:");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    private void addColumnAndRowConstraints(int numColumns, int colWidth,int numRows,int rowHeight) {
        // Constraints for columns and rows
        for (int i = 0; i <= numColumns; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPrefWidth(colWidth); // width of each column
            sheetGridPane.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i <= numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(rowHeight); // height of each row
            sheetGridPane.getRowConstraints().add(rowConst);
        }
        sheetGridPane.getColumnConstraints().get(0).setPrefWidth(20);
    }

    private void addColumnsAndRowHeaders(int numColumns, int colWidth,int numRows,int rowHeight) {
        // Adding column headers (A, B, C, ...)
        for (int col = 0; col <= numColumns; col++) {
            String colLabel = getColumnName(col);
            Label label = new Label("");
            if(col != 0) {
                label.setText(colLabel);
            }
            label.setPrefWidth(colWidth);
            label.getStyleClass().add("header");
            sheetGridPane.add(label, col, 0);
        }

        // Adding row headers (1, 2, 3, ...)
        for (int row = 1; row <= numRows; row++) {
            Label label = new Label(String.valueOf(row));
            label.setPrefHeight(rowHeight);
            label.setPrefWidth(20);
            label.getStyleClass().add("header");;
            sheetGridPane.add(label, 0, row);
        }
    }

    private void populateSheetGridPane(SheetDTO sheet, int numColumns, int colWidth, int numRows, int rowHeight) {
        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numColumns; col++) {
                String cellID = CoordinateFormatter.indexToCellId(row - 1, col - 1);
                StringProperty currentCellProperty = uiModel.cellIdProperty(cellID);
                Label label = new Label();
                label.textProperty().bind(currentCellProperty);
                CellDTO cellDTO = sheet.getCell(row - 1, col - 1);
                if (cellDTO != null) {
                    currentCellProperty.set(cellDTO.getEffectiveValue().toString());
                }
                cellIDtoLabel.put(cellID, label);
                label.setPrefHeight(rowHeight);
                label.setPrefWidth(colWidth);
                label.getStyleClass().add("cell");
                addMouseClickEventForCell(cellID,label);
                sheetGridPane.add(label, col, row);
            }
        }
    }

    private void addMouseClickEventForCell(String cellID, Label label) {
        label.setOnMouseClicked(event->{
            resetCellBorders();
            selectedCell.set(label);
            int cellRow = CoordinateFormatter.cellIdToIndex(cellID)[0];
            int cellCol = CoordinateFormatter.cellIdToIndex(cellID)[1];
            SheetDTO sheetDTO = engine.showSheet();
            CellDTO cell = sheetDTO.getCell(cellRow, cellCol);
            uiModel.selectedCellOriginalValueProperty().set(cell == null?"": cell.getOriginalValue());
            uiModel.selectedCellVersionProperty().set(cell == null? 0:cell.getVersion());
            uiModel.selectedCellIdProperty().set(cellID);
            resetCellBorders();
            highlightDependenciesAndInfluences(cell);
        });
    }

    private void createSheetGridPane(SheetDTO sheet) {
        sheetGridPane = new GridPane();
        sheetGridPane.setAlignment(Pos.CENTER);
        sheetGridPane.addColumn(0);
        sheetGridPane.addRow(0);

        int numRows = sheet.getProperties().getNumRows();
        int numColumns = sheet.getProperties().getNumCols();
        int rowHeight = sheet.getProperties().getRowHeight();
        int colWidth = sheet.getProperties().getColWidth();

        addColumnAndRowConstraints(numColumns,colWidth,numRows,rowHeight);

        addColumnsAndRowHeaders(numColumns,colWidth,numRows,rowHeight);

        uiModel.initializePropertiesForEachCell(sheetGridPane);

        populateSheetGridPane(sheet,numColumns,colWidth,numRows,rowHeight);

        sheetTab.setContent(sheetGridPane);
    }

    private String getColumnName(int index) {
        StringBuilder columnName = new StringBuilder();
        while (index > 0) {
            index--; // Decrease index to make it 0-based
            columnName.insert(0, (char) ('A' + (index % 26)));
            index = index / 26;
        }
        return columnName.toString();
    }

    private void highlightDependenciesAndInfluences(CellDTO cellDTO) {
        if (cellDTO != null) {
            for (CellDTO dependencyCell : cellDTO.getDependsOn()) {
                String dependencyCellId = CoordinateFormatter.indexToCellId(dependencyCell.getCoordinate().getRow(),
                        dependencyCell.getCoordinate().getColumn());
                Label dependencyLabel = cellIDtoLabel.get(dependencyCellId);
                if (dependencyLabel != null) {
                    dependencyLabel.getStyleClass().add("dependency-cell");
                }
            }
            for (CellDTO influencedCell : cellDTO.getInfluencingOn()) {
                String influencedCellId = CoordinateFormatter.indexToCellId(influencedCell.getCoordinate().getRow(),
                        influencedCell.getCoordinate().getColumn());
                Label influencedLabel = cellIDtoLabel.get(influencedCellId);
                if (influencedLabel != null) {
                    influencedLabel.getStyleClass().add("influence-cell");
                }
            }
        }
    }

private void resetCellBorders() {
    for (Label label : cellIDtoLabel.values()) {
        label.setStyle("");
        label.getStyleClass().removeAll("dependency-cell", "influence-cell");
    }
}

//
//    private void createRangeController() {
//        FXMLLoader loader = new FXMLLoader();
//        loader.setLocation(getClass().getResource("/com/shticell/ui/jfx/main/Range.fxml"));
//        RangeController rangeController = (RangeController)loader.getController();
//
//    }

    public void colorRangeCells(List<String> cellIds) {
        resetCellBorders();

        for (String cellId : cellIds) {
            Label cellLabel = cellIDtoLabel.get(cellId);  // Get the label by its cell ID
            if (cellLabel != null) {
                cellLabel.setStyle("-fx-border-color: pink; -fx-border-width: 2px;");
            }
        }
    }

}
