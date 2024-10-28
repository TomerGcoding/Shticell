package com.shticell.ui.jfx.sheetsManagement;

import com.shticell.ui.jfx.sheetOperations.SheetOperationController;
import dto.SheetDTO;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.util.Callback;

class ButtonCellFactory implements Callback<TableColumn<SheetDTO, Void>, TableCell<SheetDTO, Void>> {

    private SheetOperationController sheetOperationController;

    public ButtonCellFactory(SheetOperationController sheetOperationController) {
        this.sheetOperationController = sheetOperationController;
    }

    public void setSheetOperationController(SheetOperationController sheetOperationController) {
        this.sheetOperationController = sheetOperationController;
    }

    @Override
    public TableCell<SheetDTO, Void> call(final TableColumn<SheetDTO, Void> param) {
        return new TableCell<>() {

            private final Button openButton = new Button("Open Sheet");

            {
                openButton.setOnAction(event -> {
                    // Get the selected SheetDTO object for the current row
                    SheetDTO selectedSheet = getTableView().getItems().get(getIndex());

                    if (selectedSheet != null) {
                        // Use the same behavior as when selecting the sheet by its name
                        if (sheetOperationController != null) {
                            sheetOperationController.loadSheet(selectedSheet);
                            sheetOperationController.show();
                        }
                        else
                            throw new IllegalStateException("SheetOperationController is not set");
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(openButton);
                }
            }
        };
    }
}
