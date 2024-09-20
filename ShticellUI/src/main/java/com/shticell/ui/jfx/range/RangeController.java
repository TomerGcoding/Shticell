package com.shticell.ui.jfx.range;

import com.shticell.engine.Engine;
import com.shticell.engine.dto.RangeDTO;
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
        String rangeName =  insertNameTextField.getText();
        String rangeCells = insertRangeTextField.getText();
        RangeDTO rangeDTO = engine.addRange(rangeName, rangeCells);
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
    void deleteRange(ActionEvent event) {
        String rangeName = deleteNameTextField.getText();
        try {
            engine.removeRange(rangeName);

            // Find and remove the label with the matching rangeName
            for (javafx.scene.Node node : vboxInsideTitledPane.getChildren()) {
                if (node instanceof Label) {
                    Label label = (Label) node;
                    if (label.getText().equals(rangeName)) {
                        vboxInsideTitledPane.getChildren().remove(label);
                        break;
                    }
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Error while deleting range");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }

     public void setEngine(Engine engine) {
        this.engine = engine;
    }

    public void initialize() {

    }

}
