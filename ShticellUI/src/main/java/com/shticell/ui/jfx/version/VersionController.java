package com.shticell.ui.jfx.version;

import com.shticell.engine.Engine;
import com.shticell.engine.dto.CellDTO;
import com.shticell.engine.dto.SheetDTO;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;
import javafx.fxml.FXML;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.Modality;
import javafx.stage.Stage;

public class VersionController {

    private Engine engine;
    @FXML
    private ComboBox<Integer> versionSelectorComboBox;

    @FXML
    private void initialize() {
        versionSelectorComboBox.getSelectionModel().selectedItemProperty().addListener((obs, oldValue, newValue) -> {
            if (newValue != null) {
                showSelectedVersion(newValue);
            }
        });
    }

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void addVersion(int newVersionNumber) {
        versionSelectorComboBox.getItems().add(newVersionNumber);
    }

    public void clearAllVersions() {
        versionSelectorComboBox.getItems().clear();
    }

    private void showSelectedVersion(Integer selectedVersion) {
        if (versionSelectorComboBox.getValue() != null) {
            SheetDTO sheetDTO = engine.showChosenVersion(selectedVersion);
            showSheetPopup(sheetDTO);
        }
    }

    private void showSheetPopup(SheetDTO sheetDTO) {
        Stage popupStage = new Stage();
        popupStage.initModality(Modality.APPLICATION_MODAL);
        popupStage.setTitle("Sheet Version " + sheetDTO.getCurrVersion());

        GridPane gridPane = createSheetGridPane(sheetDTO);
        gridPane.getStylesheets().add(getClass().getResource("/com/shticell/ui/jfx/sheet/sheet1.css").toExternalForm());
        Scene scene = new Scene(gridPane);
        popupStage.setScene(scene);
        popupStage.showAndWait();
    }

    private GridPane createSheetGridPane(SheetDTO sheet) {
        GridPane gridPane = new GridPane();
        gridPane.setAlignment(Pos.CENTER);
        gridPane.addColumn(0);
        gridPane.addRow(0);

        int numRows = sheet.getProperties().getNumRows();
        int numColumns = sheet.getProperties().getNumCols();
        int rowHeight = sheet.getProperties().getRowHeight();
        int colWidth = sheet.getProperties().getColWidth();

        addColumnAndRowConstraints(gridPane, numColumns, colWidth, numRows, rowHeight);
        addColumnsAndRowHeaders(gridPane, numColumns, colWidth, numRows, rowHeight);
        populateSheetGridPane(gridPane, sheet, numColumns, colWidth, numRows, rowHeight);

        return gridPane;
    }

    private void addColumnAndRowConstraints(GridPane gridPane, int numColumns, int colWidth, int numRows, int rowHeight) {
        for (int i = 0; i <= numColumns; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPrefWidth(colWidth);
            gridPane.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i <= numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(rowHeight);
            gridPane.getRowConstraints().add(rowConst);
        }
        gridPane.getColumnConstraints().get(0).setPrefWidth(20);
    }

    private void addColumnsAndRowHeaders(GridPane gridPane, int numColumns, int colWidth, int numRows, int rowHeight) {
        for (int col = 0; col <= numColumns; col++) {
            String colLabel = getColumnName(col);
            Label label = new Label("");
            if (col != 0) {
                label.setText(colLabel);
            }
            label.setPrefWidth(colWidth);
            label.getStyleClass().add("header");
            gridPane.add(label, col, 0);
        }

        for (int row = 1; row <= numRows; row++) {
            Label label = new Label(String.valueOf(row));
            label.setPrefHeight(rowHeight);
            label.setPrefWidth(20);
            label.getStyleClass().add("header");
            gridPane.add(label, 0, row);
        }
    }

    private void populateSheetGridPane(GridPane gridPane, SheetDTO sheet, int numColumns, int colWidth, int numRows, int rowHeight) {
        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numColumns; col++) {
                String cellID = CoordinateFormatter.indexToCellId(row - 1, col - 1);
                CellDTO cellDTO = sheet.getCell(row - 1, col - 1);
                Label label = new Label();
                if(cellDTO!=null){
                    label.setText(sheet.getCell(row - 1, col - 1).getEffectiveValue().toString());
                }else {
                    label.setText("");
                }
                label.setPrefHeight(rowHeight);
                label.setPrefWidth(colWidth);
                label.getStyleClass().add("cell");
                gridPane.add(label, col, row);
            }
        }
    }

    private String getColumnName(int index) {
        StringBuilder columnName = new StringBuilder();
        while (index > 0) {
            index--;
            columnName.insert(0, (char) ('A' + (index % 26)));
            index = index / 26;
        }
        return columnName.toString();
    }
}