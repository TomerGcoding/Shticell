package com.shticell.ui.gui.body;

import com.shticell.engine.Engine;
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

    public void populateSheetGrid(SheetDTO sheetDTO){
        sheetGridPane.getChildren().clear();
        for(int rowIndex = 0; rowIndex<sheetDTO.getProperties().getNumRows(); rowIndex++){
            for (int colIndex = 0; colIndex<sheetDTO.getProperties().getNumCols(); colIndex++){
                if(sheetDTO.getCell(rowIndex,colIndex)!= null) {
                    String cellValue = sheetDTO.getCell(rowIndex, colIndex).getEffectiveValue().toString();
                    Label label = new Label(cellValue);
                    label.setPrefWidth(150);
                    label.setPrefHeight(25);
                    label.getStyleClass().add("sheet-cell");
                    sheetGridPane.add(label, colIndex, rowIndex);
                }
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
