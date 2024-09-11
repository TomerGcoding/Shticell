package com.shticell.ui.jfx.main;

import com.shticell.engine.Engine;
import com.shticell.engine.EngineImpl;
import com.shticell.engine.dto.CellDTO;
import com.shticell.engine.dto.SheetDTO;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;

import java.io.File;
import java.util.HashMap;
import java.util.Map;

public class MainController {

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

    private Engine engine = new EngineImpl();

    private UIModel uiModel;

    private GridPane sheetGridPane;

    private Map<String,Label> cellIDtoLabel = new HashMap<>();

    private ObjectProperty<Label> selectedCellLabel;

    @FXML
    private void initialize() {
        uiModel = new UIModel(chosenFileFullPathLabel, sheetTab,updateSelectedCellValueButton,sheetGridPane,currentCellLabel,selectedCellOriginalValueTextField,
                currentCellVersionLabel);
        selectedCellLabel = new SimpleObjectProperty<>();
    }

    @FXML
    public void loadXMLFile(ActionEvent event) {
        try {
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Open XML file");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
            File file = fileChooser.showOpenDialog(null);
            uiModel.fullPathProperty().setValue(file.getAbsolutePath());
            SheetDTO sheetDTO = engine.loadSheetFile(file.getAbsolutePath());
            uiModel.nameProperty().setValue(engine.showSheet().getSheetName());
            versionSelectorComboBox.getItems().clear();
            versionSelectorComboBox.getItems().add(engine.showSheet().getCurrVersion());
            uiModel.isFileSelectedProperty().setValue(true);
            createSheetGridPane(sheetDTO);
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void updateSelectedCellValue(ActionEvent event) {
        try {
            engine.setCell(currentCellLabel.getText(), selectedCellOriginalValueTextField.getText());
            versionSelectorComboBox.getItems().add(engine.showSheet().getCurrVersion());
            SheetDTO updatedSheet = engine.showSheet();
            CellDTO updatedCell = updatedSheet.getCell(CoordinateFormatter.cellIdToIndex(currentCellLabel.getText())[0],
                    CoordinateFormatter.cellIdToIndex(currentCellLabel.getText())[1]);
            uiModel.cellIdProperty(currentCellLabel.getText()).setValue(updatedCell.getEffectiveValue().toString());
            uiModel.selectedCellVersionProperty().setValue(updatedCell.getVersion());

            // Update the labels of influenced cells
            for (CellDTO influencedCell : updatedCell.getInfluencingOn()) {
                String influencedCellId = CoordinateFormatter.indexToCellId(influencedCell.getCoordinate().getRow(),
                        influencedCell.getCoordinate().getColumn());
                uiModel.cellIdProperty(influencedCellId).setValue(influencedCell.getEffectiveValue().toString());
//                uiModel.cellVersionProperty(influencedCellId).setValue(influencedCell.getVersion());
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
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
            sheetGridPane.add(label, col, 0); // Adding to the first row (row 0)
        }

        // Adding row headers (1, 2, 3, ...)
        for (int row = 1; row <= numRows; row++) {
            Label label = new Label(String.valueOf(row));
            label.setPrefHeight(rowHeight);
            label.getStyleClass().add("header");;
            sheetGridPane.add(label, 0, row); // Adding to the first column (column 0)
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
                label.setOnMouseClicked(event -> {
                    uiModel.selectedCellIdProperty().setValue(cellID);
                    uiModel.selectedCellOriginalValueProperty().setValue(cellDTO == null ? "" : cellDTO.getOriginalValue());
                    uiModel.selectedCellVersionProperty().setValue(cellDTO == null ? 1 : cellDTO.getVersion());
                    if (selectedCellLabel.get() != null) {
                        selectedCellLabel.get().setId("");
                        resetCellBorders(); // Reset the borders of previously highlighted cells
                    }
                    selectedCellLabel.setValue(label);
                    selectedCellLabel.get().setId("selected-cell");
                    highlightDependenciesAndInfluences(cellDTO); // Highlight the borders of dependencies and influences
                });
                sheetGridPane.add(label, col, row);
            }
        }
    }

    public ObjectProperty<Label> selectedCellLabelProperty() {
        return selectedCellLabel;
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
            label.getStyleClass().removeAll("dependency-cell", "influence-cell");
        }
    }

}
