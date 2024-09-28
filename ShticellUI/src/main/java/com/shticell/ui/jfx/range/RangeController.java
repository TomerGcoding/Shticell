package com.shticell.ui.jfx.range;

import com.shticell.engine.Engine;
import com.shticell.engine.dto.RangeDTO;
import com.shticell.engine.dto.SheetDTO;
import com.shticell.ui.jfx.main.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

public class RangeController {

    @FXML
    private Accordion activeRangeAccordion;

    @FXML
    private TitledPane activeRangesTitledPane;

    @FXML
    private Label daleteNameLabel;

    @FXML
    private TextField deleteNameTextField;

    @FXML
    private Button deleteRangeBtn;

    @FXML
    private TextField insertNameTextField;

    @FXML
    private TextField insertRangeTextField;

    @FXML
    private VBox vboxInsideTitledPane;

    private Engine engine;

    private MainController mainController; // Reference to MainController

    public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }

    @FXML
    void addNewRange(ActionEvent event) {
        try {
            String rangeName = insertNameTextField.getText();
            String rangeCells = insertRangeTextField.getText();
            RangeDTO rangeDTO = engine.addRange(rangeName, rangeCells);
            Label newLabel = new Label(rangeDTO.getName());
            vboxInsideTitledPane.getChildren().add(newLabel);
            newLabel.setOnMouseClicked(e -> mainController.colorRangeCells(rangeDTO.getCellsIdInRange()));
            insertNameTextField.clear();
            insertRangeTextField.clear();
        }
      catch (Exception e){
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText("Error while adding new range");
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
}

@FXML
void deleteRange(ActionEvent event) {
    String rangeName = deleteNameTextField.getText();
    try {
        engine.removeRange(rangeName);

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

    public void resetRanges() {
        vboxInsideTitledPane.getChildren().clear();
    }
}

