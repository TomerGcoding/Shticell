package com.shticell.ui.gui.body;

import com.shticell.engine.Engine;
import com.shticell.engine.dto.CellDTO;
import com.shticell.engine.dto.SheetDTO;
import com.shticell.ui.gui.app.AppController;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.Border;
import javafx.scene.layout.GridPane;

public class BodyController {

    private Engine engine;
    private AppController appController;


    @FXML
    private GridPane sheetGridPane;

    public void populateSheetGrid(SheetDTO sheetDTO) {
        sheetGridPane.getChildren().clear();

        int rows = sheetDTO.getProperties().getNumRows();
        int columns = sheetDTO.getProperties().getNumCols();
        String cellValue;

        // Add column headers (A, B, C, ...)
        for (int colIndex = 0; colIndex <= columns; colIndex++) {
            Label headerLabel;
            if (colIndex == 0) {
                headerLabel = new Label(""); // Empty corner cell
            } else {
                headerLabel = new Label(String.valueOf((char)('A' + colIndex - 1)));
            }
            headerLabel.setPrefWidth(150);
            headerLabel.setPrefHeight(25);
            headerLabel.getStyleClass().addAll("sheet-header", "column-header");
            sheetGridPane.add(headerLabel, colIndex, 0);
        }

        // Add row numbers and cell values
        for (int rowIndex = 0; rowIndex < rows; rowIndex++) {
            // Add row number
            Label rowLabel = new Label(String.valueOf(rowIndex + 1));
            rowLabel.setPrefWidth(50);
            rowLabel.setPrefHeight(25);
            rowLabel.getStyleClass().addAll("sheet-header", "row-header");
            sheetGridPane.add(rowLabel, 0, rowIndex + 1);

            // Add cell values
            for (int colIndex = 0; colIndex < columns; colIndex++) {
                if (sheetDTO.getCell(rowIndex + 1, colIndex + 1) != null) {
                    cellValue = sheetDTO.getCell(rowIndex + 1, colIndex + 1).getEffectiveValue().toString();
                } else {
                    cellValue = "";
                }
                Label label = new Label(cellValue);
                label.setPrefWidth(150);
                label.setPrefHeight(25);
                label.getStyleClass().add("sheet-cell");
                sheetGridPane.add(label, colIndex + 1, rowIndex + 1);
            }
        }
    }


    public void setMainController(AppController appController) {
        this.appController = appController;
    }
    public void setEngine(Engine engine) {
        this.engine = engine;
    }
}
