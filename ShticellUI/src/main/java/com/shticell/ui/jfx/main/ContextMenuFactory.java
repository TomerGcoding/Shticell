package com.shticell.ui.jfx.main;

import javafx.geometry.Pos;
import javafx.scene.control.*;

public class ContextMenuFactory {
    private SheetGridManager gridManager;

    public ContextMenuFactory(SheetGridManager gridManager) {
        this.gridManager = gridManager;
    }
    public ContextMenu createColumnContextMenu(int columnIndex) {
        ContextMenu menu = new ContextMenu();

        Menu alignmentMenu = new Menu("Coulmn Alignment");
        MenuItem leftAlign = new MenuItem("Left");
        MenuItem centerAlign = new MenuItem("Center");
        MenuItem rightAlign = new MenuItem("Right");
        alignmentMenu.getItems().addAll(leftAlign, centerAlign, rightAlign);

        MenuItem setWidth = new MenuItem("Set Column Width");
        menu.getItems().addAll(alignmentMenu,setWidth);

        leftAlign.setOnAction(e->gridManager.setColumnAlignment(columnIndex, Pos.CENTER_LEFT));
        centerAlign.setOnAction(e->gridManager.setColumnAlignment(columnIndex, Pos.CENTER));
        rightAlign.setOnAction(e->gridManager.setColumnAlignment(columnIndex, Pos.CENTER_RIGHT));
        setWidth.setOnAction(e -> showSetColumnWidthDialog(columnIndex));

        return menu;
    }
    public ContextMenu createRowContextMenu(int rowIndex) {
        ContextMenu contextMenu = new ContextMenu();
        MenuItem setHeight = new MenuItem("Set Row Height");
        contextMenu.getItems().add(setHeight);

        setHeight.setOnAction(e -> showSetRowHeightDialog(rowIndex));

        return contextMenu;
    }
    private void showSetColumnWidthDialog(int columnIndex) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set Column Width");
        dialog.setHeaderText("Enter new width for column " + gridManager.getColumnName(columnIndex));
        dialog.setContentText("Width:");

        dialog.showAndWait().ifPresent(result -> {
            try {
                double width = Double.parseDouble(result);
                gridManager.setColumnWidth(columnIndex, width);
            } catch (NumberFormatException e) {
                showAlert("Invalid input", "Please enter a valid number for the width.");
            }
        });
    }

    private void showSetRowHeightDialog(int rowIndex) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Set Row Height");
        dialog.setHeaderText("Enter new height for row " + rowIndex);
        dialog.setContentText("Height:");

        dialog.showAndWait().ifPresent(result -> {
            try {
                double height = Double.parseDouble(result);
                gridManager.setRowHeight(rowIndex, height);
            } catch (NumberFormatException e) {
                showAlert("Invalid input", "Please enter a valid number for the height.");
            }
        });
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

//    public ContextMenu createCellContextMenu() {
//
//    }

