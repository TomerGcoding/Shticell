package com.shticell.ui.jfx.range;

import dto.RangeDTO;
import dto.SheetDTO;
import com.shticell.ui.jfx.sheetOperations.SheetOperationController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class RangeController {

    @FXML
    private TextField deleteNameTextField;
    @FXML
    private TextField insertNameTextField;
    @FXML
    private TextField insertRangeTextField;
    @FXML
    private VBox vboxInsideTitledPane;

    private final RangeRequests requests = new RangeRequests(this);
    private SheetOperationController mainController;
    public void setMainController(SheetOperationController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void addNewRange(ActionEvent event) {
        try {
            String rangeName = insertNameTextField.getText();
            String rangeCells = insertRangeTextField.getText();
            requests.addRangeRequest(rangeName, rangeCells);
          //  RangeDTO rangeDTO = engine.addRange(rangeName, rangeCells);
        }
      catch (Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error while adding new range");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}

    protected void showRange(RangeDTO range){
    Label newLabel = new Label(range.getName());
    vboxInsideTitledPane.getChildren().add(newLabel);
    newLabel.setOnMouseClicked(e -> mainController.colorRangeCells(range.getCellsIdInRange()));
    insertNameTextField.clear();
    insertRangeTextField.clear();
}


    @FXML
    void deleteRange(ActionEvent event) {
    String rangeName = deleteNameTextField.getText();
    try {
        requests.deleteRangeRequest(rangeName);

        for (javafx.scene.Node node : vboxInsideTitledPane.getChildren()) {
            if (node instanceof Label) {
                Label label = (Label) node;
                if (label.getText().equals(rangeName)) {
                    vboxInsideTitledPane.getChildren().remove(label);
                    break;
                }
            }
        }
        deleteNameTextField.clear();
    } catch (Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error while deleting range");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        }
    }

    public void addLoadedRange(SheetDTO sheetDTO) {
        vboxInsideTitledPane.getChildren().clear();
        for(RangeDTO range: sheetDTO.getActiveRanges().values()){
            Label newLabel = new Label(range.getName());
            vboxInsideTitledPane.getChildren().add(newLabel);
            newLabel.setOnMouseClicked(e -> mainController.colorRangeCells(range.getCellsIdInRange()));
        }
    }

    public void showErrorAlert(String filteringError, String s) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(filteringError);
        alert.setContentText(s);
        alert.showAndWait();
    }
}

