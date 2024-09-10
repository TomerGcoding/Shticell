package com.shticell.ui.jfx.main;

import com.shticell.engine.Engine;
import com.shticell.engine.EngineImpl;
import com.shticell.engine.dto.SheetDTO;
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

    @FXML
    private void initialize() {
        uiModel = new UIModel(chosenFileFullPathLabel, sheetTab,updateSelectedCellValueButton);
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
            createSheetGridPane(sheetDTO);
        }catch (Exception e){
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText(null);
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    public void createSheetGridPane(SheetDTO sheet) {
        sheetGridPane = new GridPane();
        sheetGridPane.setAlignment(Pos.CENTER);
        sheetGridPane.addColumn(0);
        sheetGridPane.addRow(0);
        int numRows = sheet.getProperties().getNumRows();
        int numColumns = sheet.getProperties().getNumCols();
        int rowHeight = sheet.getProperties().getRowHeight();
        int colWidth = sheet.getProperties().getColWidth();

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

        // Adding column headers (A, B, C, ...)
        for (int col = 0; col <= numColumns; col++) {
            String colLabel = getColumnName(col);
            Label label = new Label("");
            if(col != 0) {
                label.setText(colLabel);
            }
            label.setPrefWidth(colWidth);
            label.setStyle("-fx-background-color: lightgrey; -fx-alignment: center;");
            sheetGridPane.add(label, col, 0); // Adding to the first row (row 0)
        }

        // Adding row headers (1, 2, 3, ...)
        for (int row = 1; row <= numRows; row++) {
            Label label = new Label(String.valueOf(row));
            label.setPrefHeight(rowHeight);
            label.setStyle("-fx-background-color: lightgrey; -fx-alignment: center;");
            sheetGridPane.add(label, 0, row); // Adding to the first column (column 0)
        }
        sheetGridPane.getColumnConstraints().get(0).setPrefWidth(20);
        sheetGridPane.setHgap(10);
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
}
