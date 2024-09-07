package com.shticell.ui.gui.app;

import com.shticell.engine.Engine;
import com.shticell.engine.EngineImpl;
import com.shticell.engine.dto.SheetDTO;
import com.shticell.ui.gui.body.BodyController;
import com.shticell.ui.gui.header.HeaderController;
import javafx.fxml.FXML;
import javafx.scene.control.ScrollPane;

public class AppController {

    private Engine engine = new EngineImpl();
    @FXML private ScrollPane headerComponent;
    @FXML private HeaderController headerComponentController;
    @FXML private ScrollPane bodyComponent;
    @FXML private BodyController bodyComponentController;


    @FXML
    public void initialize() {
        if (headerComponentController != null && bodyComponentController != null) {
            headerComponentController.setMainController(this);
            bodyComponentController.setMainController(this);
        }
    }

    public void setHeaderComponentController(HeaderController headerComponentController) {
        this.headerComponentController = headerComponentController;
        headerComponentController.setMainController(this);
        headerComponentController.setEngine(engine);
    }

    public void setBodyComponentController(BodyController bodyComponentController) {
        this.bodyComponentController = bodyComponentController;
        bodyComponentController.setMainController(this);
        bodyComponentController.setEngine(engine);
    }

    public void populateSheetGrid(SheetDTO sheetDTO){
        bodyComponentController.populateSheetGrid(sheetDTO);
    }
}
