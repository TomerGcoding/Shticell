package com.shticell.ui.jfx.range;

import com.shticell.engine.Engine;
import com.shticell.engine.dto.RangeDTO;
import com.shticell.ui.jfx.main.MainController;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.VBox;

import java.util.List;

public class RangeController {

    @FXML
    private Accordion activeRangeAccordion;

    @FXML
    private TitledPane activeRangesTitledPane;

    @FXML
    private TextField insertNameTextField;

    @FXML
    private TextField insertRangeTextField;

    @FXML
    private VBox vboxInsideTitledPane;
    @FXML
    private TextField deleteNameTextField;

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
            List<String> rangeCellIds = rangeDTO.getCellIds();

            Label newLabel = new Label(rangeDTO.getName());
            vboxInsideTitledPane.getChildren().add(newLabel);

            // Attach event handler to highlight range cells when clicked
            newLabel.setOnMouseClicked(e -> mainController.colorRangeCells(rangeCellIds));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error while adding new range");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }

    }
    @FXML
    private void deleteRange(ActionEvent event) {
        try {
            String rangeName = deleteNameTextField.getText();
            engine.removeRange(rangeName);
            vboxInsideTitledPane.getChildren().removeIf(node ->
                    node instanceof Label && ((Label) node).getText().equals(rangeName));
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error while deleting range");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
}
