package com.shticell.ui.jfx.sheet;

import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;

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
        menu.getItems().addAll(alignmentMenu, setWidth);

        leftAlign.setOnAction(e -> gridManager.setColumnAlignment(columnIndex, Pos.CENTER_LEFT));
        centerAlign.setOnAction(e -> gridManager.setColumnAlignment(columnIndex, Pos.CENTER));
        rightAlign.setOnAction(e -> gridManager.setColumnAlignment(columnIndex, Pos.CENTER_RIGHT));
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

    public ContextMenu createCellContextMenu(Label cellLabel) {
        ContextMenu contextMenu = new ContextMenu();

        MenuItem changeTextColor = new MenuItem("Change Text Color");
        MenuItem changeCellBackgroundColor = new MenuItem("Change Cell Background Color");
        MenuItem resetToDefault = new MenuItem("Reset Cell Styling");

        resetToDefault.setOnAction(e -> {
            cellLabel.setTextFill(Color.BLACK);  // Reset text color to default (black)
            cellLabel.setStyle("");  // Clear all styles
            cellLabel.getStyleClass().clear();  // Clear any custom style classes
            cellLabel.getStyleClass().addAll("label","cell");  // Add back the default label style class
        });

        changeTextColor.setOnAction(e -> {
            ColorPicker colorPicker = new ColorPicker();
            colorPicker.setValue((Color) cellLabel.getTextFill()); // Default color, you can change this

            Dialog<Color> dialog = new Dialog<>();
            dialog.setTitle("Choose Text Color");
            dialog.setHeaderText("Select a color for the cell text");

            ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            grid.add(colorPicker, 0, 0);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == selectButtonType) {
                    return colorPicker.getValue();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(color -> {
                cellLabel.setTextFill(color);
            });
        });

        changeCellBackgroundColor.setOnAction(e -> {
            ColorPicker colorPicker = new ColorPicker();
            colorPicker.setValue(getCurrentBackgroundColor(cellLabel)); // Default color, you can change this

            Dialog<Color> dialog = new Dialog<>();
            dialog.setTitle("Choose Background Color");
            dialog.setHeaderText("Select a color for the cell background");

            ButtonType selectButtonType = new ButtonType("Select", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(selectButtonType, ButtonType.CANCEL);

            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            grid.add(colorPicker, 0, 0);

            dialog.getDialogPane().setContent(grid);

            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == selectButtonType) {
                    return colorPicker.getValue();
                }
                return null;
            });

            dialog.showAndWait().ifPresent(color -> {
                cellLabel.setStyle("-fx-background-color: #" + toHexString(color) + ";");
            });
        });

        contextMenu.getItems().addAll(changeTextColor, changeCellBackgroundColor,new SeparatorMenuItem(), resetToDefault);

        return contextMenu;
    }
    private String toHexString(Color color) {
        return String.format("%02X%02X%02X",
                (int) (color.getRed() * 255),
                (int) (color.getGreen() * 255),
                (int) (color.getBlue() * 255));
    }
    private Color getCurrentBackgroundColor(Label cellLabel) {
        String style = cellLabel.getStyle();
        if (style.contains("-fx-background-color")) {
            String[] styles = style.split(";");
            for (String s : styles) {
                if (s.trim().startsWith("-fx-background-color")) {
                    String colorString = s.split(":")[1].trim();
                    return Color.web(colorString);
                }
            }
        }
        return Color.WHITE; // Default color if no background color is set
    }

}



