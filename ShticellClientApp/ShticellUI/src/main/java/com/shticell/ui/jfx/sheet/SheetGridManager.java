package com.shticell.ui.jfx.sheet;

import dto.CellDTO;
import dto.CoordinateDTO;
import dto.SheetDTO;
import com.shticell.ui.jfx.sheetOperations.AnimationManager;
import com.shticell.ui.jfx.sheetOperations.SheetOperationController;
import com.shticell.ui.jfx.sheetOperations.UIModel;
import javafx.beans.property.StringProperty;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;


public class SheetGridManager {

    private final SheetOperationController mainController;
    private final ContextMenuFactory contextMenuFactory;
    private final GridPane sheetGridPane;
    private final UIModel uiModel;
    private final Map<String, Label> cellIDtoLabel;
    private String activeStyleSheet;

    public SheetGridManager(GridPane sheetGridPane, UIModel uiModel, SheetOperationController mainController) {
        this.sheetGridPane = sheetGridPane;
        this.activeStyleSheet = "sheet1.css";
        this.sheetGridPane.getStylesheets().add(Objects.requireNonNull(getClass().getResource(activeStyleSheet)).toExternalForm());
        this.uiModel = uiModel;
        this.cellIDtoLabel = new HashMap<>();
        this.mainController = mainController;
        this.contextMenuFactory = new ContextMenuFactory(this);
    }

    public void createSheetGridPane(SheetDTO sheet) {
        sheetGridPane.getChildren().clear();
        sheetGridPane.getColumnConstraints().clear();
        sheetGridPane.getRowConstraints().clear();
        int numRows = sheet.getNumRows();
        int numColumns = sheet.getNumColumns();
        int rowHeight = sheet.getRowHeight();
        int colWidth = sheet.getColWidth();

        addColumnAndRowConstraints(numColumns, colWidth, numRows, rowHeight);
        addColumnsAndRowHeaders(numColumns, colWidth, numRows, rowHeight);
        uiModel.initializePropertiesForEachCell(sheetGridPane);
        populateSheetGridPane(sheet, numColumns, colWidth, numRows, rowHeight);
        ScrollPane scrollPane = new ScrollPane(sheetGridPane);
        scrollPane.setHbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setVbarPolicy(ScrollPane.ScrollBarPolicy.AS_NEEDED);
        scrollPane.setFitToHeight(true);
        scrollPane.setFitToWidth(true);
        sheetGridPane.prefWidthProperty().bind(scrollPane.widthProperty().subtract(0));
        sheetGridPane.prefHeightProperty().bind(scrollPane.heightProperty().subtract(0));
        mainController.getMainBorderPane().setCenter(scrollPane);
        AnimationManager.animateSheetPresentation(scrollPane);
    }

    public void createReadOnlySheetGridPane(GridPane gridPane,SheetDTO sheet) {
        gridPane.getChildren().clear();
        gridPane.getColumnConstraints().clear();
        gridPane.getRowConstraints().clear();

        int numRows = sheet.getNumRows();
        int numColumns = sheet.getNumColumns();
        int rowHeight = sheet.getRowHeight();
        int colWidth = sheet.getColWidth();

        addColumnAndRowConstraintsReadOnly(gridPane,numColumns, colWidth, numRows, rowHeight);
        addColumnsAndRowHeadersReadOnly(gridPane,numColumns, colWidth, numRows, rowHeight);
        populateReadOnlySheetGridPane(gridPane,sheet, numColumns, colWidth, numRows, rowHeight);
    }


    private void addColumnAndRowConstraints(int numColumns, int colWidth, int numRows, int rowHeight) {
        for (int i = 0; i <= numColumns; i++) {
            ColumnConstraints colConst = new ColumnConstraints();
            colConst.setPrefWidth(colWidth);
            sheetGridPane.getColumnConstraints().add(colConst);
        }

        for (int i = 0; i <= numRows; i++) {
            RowConstraints rowConst = new RowConstraints();
            rowConst.setPrefHeight(rowHeight);
            sheetGridPane.getRowConstraints().add(rowConst);
        }
        sheetGridPane.getColumnConstraints().getFirst().setPrefWidth(20);
    }

    private void addColumnAndRowConstraintsReadOnly(GridPane gridPane,int numColumns, int colWidth, int numRows, int rowHeight) {
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
        gridPane.getColumnConstraints().getFirst().setPrefWidth(20);
    }

    private void addColumnsAndRowHeaders(int numColumns, int colWidth, int numRows, int rowHeight) {
        for (int col = 0; col <= numColumns; col++) {
            String colLabel = getColumnName(col);
            Label label = new Label("");
            if (col != 0) {
                final int columnIndex = col;
                label.setOnContextMenuRequested(event -> {
                    ContextMenu contextMenu = contextMenuFactory.createColumnContextMenu(columnIndex);
                    contextMenu.show(label, event.getScreenX(), event.getScreenY());
                });
                label.setText(colLabel);
            }
            label.setPrefWidth(colWidth);
            label.getStyleClass().add("header");
            sheetGridPane.add(label, col, 0);
        }

        for (int row = 1; row <= numRows; row++) {
            Label label = new Label(String.valueOf(row));
            label.setPrefHeight(rowHeight);
            label.setPrefWidth(20);
            label.getStyleClass().add("header");
            final int rowIndex = row;
            label.setOnContextMenuRequested(event -> {
                ContextMenu contextMenu = contextMenuFactory.createRowContextMenu(rowIndex);
                contextMenu.show(label, event.getScreenX(), event.getScreenY());
            });
            sheetGridPane.add(label, 0, row);
        }
    }

    private void addColumnsAndRowHeadersReadOnly(GridPane gridPane,int numColumns, int colWidth, int numRows, int rowHeight) {
        for (int col = 0; col <= numColumns; col++) {
            String colLabel = getColumnName(col);
            Label label = new Label("");
            if (col != 0) {
                final int columnIndex = col;
                label.setOnContextMenuRequested(event -> {
                    ContextMenu contextMenu = contextMenuFactory.createColumnContextMenu(columnIndex);
                    contextMenu.show(label, event.getScreenX(), event.getScreenY());
                });
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
            final int rowIndex = row;
            label.setOnContextMenuRequested(event -> {
                ContextMenu contextMenu = contextMenuFactory.createRowContextMenu(rowIndex);
                contextMenu.show(label, event.getScreenX(), event.getScreenY());
            });
            gridPane.add(label, 0, row);
        }
    }

    private void populateSheetGridPane(SheetDTO sheet, int numColumns, int colWidth, int numRows, int rowHeight) {
        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numColumns; col++) {
                String cellID = CoordinateDTO.indexToCellId(row - 1, col - 1);
                StringProperty currentCellProperty = uiModel.cellIdProperty(cellID);
                Label label = new Label();
                label.textProperty().bind(currentCellProperty);
                CellDTO cellDTO = sheet.getCell(row - 1, col - 1);
                if (cellDTO != null) {
                    currentCellProperty.set(cellDTO.getEffectiveValue().toString());
                }
                cellIDtoLabel.put(cellID, label);
                label.setAlignment(Pos.CENTER);
                label.setPrefHeight(rowHeight);
                label.setPrefWidth(colWidth);
                label.getStyleClass().add("cell");
                ContextMenu contextMenu = contextMenuFactory.createCellContextMenu(label);
                label.setContextMenu(contextMenu);
                mainController.addMouseClickEventForCell(cellID, label);
                sheetGridPane.add(label, col, row);
            }
        }
    }

    private void populateReadOnlySheetGridPane(GridPane gridPane, SheetDTO sheet, int numColumns, int colWidth, int numRows, int rowHeight) {
        for (int row = 1; row <= numRows; row++) {
            for (int col = 1; col <= numColumns; col++) {
                String cellID = CoordinateDTO.indexToCellId(row - 1, col - 1);
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
                label.setStyle(cellIDtoLabel.get(cellID).getStyle());
                gridPane.add(label, col, row);
            }
        }
    }

    public String getColumnName(int index) {
        StringBuilder columnName = new StringBuilder();
        while (index > 0) {
            index--;
            columnName.insert(0, (char) ('A' + (index % 26)));
            index = index / 26;
        }
        return columnName.toString();
    }

    public void highlightDependenciesAndInfluences(CellDTO cellDTO) {
        if (cellDTO != null) {
            for (String dependencyCellId : cellDTO.getDependsOn()) {
                Label dependencyLabel = cellIDtoLabel.get(dependencyCellId);
                if (dependencyLabel != null) {
                    dependencyLabel.getStyleClass().add("dependency-cell");
                    AnimationManager.animateCellSelection(dependencyLabel);

                }
            }
            for (String influencedCellId : cellDTO.getInfluencingOn()) {
                Label influencedLabel = cellIDtoLabel.get(influencedCellId);
                if (influencedLabel != null) {
                    influencedLabel.getStyleClass().add("influence-cell");
                    AnimationManager.animateCellSelection(influencedLabel);
                }
            }
        }
    }

    public void resetCellBorders() {
        for (Label label : cellIDtoLabel.values()) {
            label.getStyleClass().removeAll("dependency-cell", "influence-cell","ranged-cell");
        }
    }

    public void setColumnAlignment(int columnIndex, Pos alignment) {
        for (Node node : sheetGridPane.getChildren()) {
            Integer nodeColumnIndex = GridPane.getColumnIndex(node);
            Integer nodeRowIndex = GridPane.getRowIndex(node);
            if (nodeColumnIndex != null && nodeColumnIndex == columnIndex && node instanceof Label label && nodeRowIndex > 0) {
                label.setAlignment(alignment);
            }
        }
    }

    public void setColumnWidth(int columnIndex, double width) {
        while (sheetGridPane.getColumnConstraints().size() <= columnIndex) {
            sheetGridPane.getColumnConstraints().add(new ColumnConstraints());
        }

        ColumnConstraints columnConstraints = sheetGridPane.getColumnConstraints().get(columnIndex);

        columnConstraints.setPrefWidth(width);
        columnConstraints.setMinWidth(width);
        columnConstraints.setMaxWidth(width);

        for (Node node : sheetGridPane.getChildren()) {
            Integer nodeColumnIndex = GridPane.getColumnIndex(node);
            if (nodeColumnIndex != null && nodeColumnIndex == columnIndex) {
                if (node instanceof Region region) {
                    region.setPrefWidth(width);
                    region.setMaxWidth(width);
                }
            }
        }

        sheetGridPane.requestLayout();
    }

    public void setRowHeight(int rowIndex, double height) {
        while (sheetGridPane.getRowConstraints().size() <= rowIndex) {
            sheetGridPane.getRowConstraints().add(new RowConstraints());
        }

        RowConstraints rowConstraints = sheetGridPane.getRowConstraints().get(rowIndex);

        rowConstraints.setPrefHeight(height);
        rowConstraints.setMinHeight(height);
        rowConstraints.setMaxHeight(height);

        for (Node node : sheetGridPane.getChildren()) {
            Integer nodeRowIndex = GridPane.getRowIndex(node);
            if (nodeRowIndex != null && nodeRowIndex == rowIndex) {
                if (node instanceof Region region) {
                    region.setPrefHeight(height);
                    region.setMaxHeight(height);
                }
            }
        }

        sheetGridPane.requestLayout();
    }

    public void setSheetStyle(int styleNumber) {
        activeStyleSheet = String.format("sheet%d.css", styleNumber);
        this.sheetGridPane.getStylesheets().clear();
        this.sheetGridPane.getStylesheets().add(getClass().getResource(activeStyleSheet).toExternalForm());
    }
    public String getActiveStyleSheet() {
        return activeStyleSheet;
    }

    public void colorRangeCells(List<String> rangeCellIds)   {
        resetCellBorders();
        for (String cellId : rangeCellIds) {
            Label cellLabel = cellIDtoLabel.get(cellId);
            if (cellLabel != null) {
                cellLabel.getStyleClass().add("ranged-cell");
                AnimationManager.animateCellSelection(cellLabel);
            }
        }
    }

    public Map<String, Label> getCellIDtoLabel() {
        return cellIDtoLabel;
    }
}