package com.shticell.ui.jfx.main;

import com.shticell.ui.jfx.sheetsManagement.SheetsManagementController;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import com.shticell.ui.jfx.sheetOperations.*;
import javafx.scene.layout.BorderPane;

import java.io.IOException;
import java.net.URL;

import static com.shticell.ui.jfx.utils.Constants.*;

public class MainController {

    @FXML
    private ScrollPane mainPanel;
    private SheetOperationController sheetOperationController = null;
    private BorderPane sheetOperationComponent;
    private SheetsManagementController sheetsManagementController = null;
    private BorderPane sheetsManagementComponent;


    @FXML
    public void initialize() {
        try{
            loadSheetOperationPage();
            loadSheetsManagementPage();

        } catch (Exception e) {
            System.out.println("Error loading pages" + e.getMessage());
            e.printStackTrace();
        }

    }

    private void loadSheetsManagementPage() {
        URL loginPageUrl = getClass().getResource(SHEETS_MANAGEMENT_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPageUrl);
            sheetsManagementComponent = fxmlLoader.load();
            sheetsManagementController = fxmlLoader.getController();
            sheetsManagementController.setMainController(this);
            sheetsManagementController.setSheetOperationController(sheetOperationController);
            setMainPanelTo(sheetsManagementComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void loadSheetOperationPage() {
        URL loginPageUrl = getClass().getResource(SHEET_OPERATION_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPageUrl);
            sheetOperationComponent = fxmlLoader.load();
            sheetOperationController = fxmlLoader.getController();
            sheetOperationController.setMainController(this);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public void setMainPanelTo(Parent pane) {
        mainPanel.setContent(pane);
    }

    public void switchToSheetOperation() {
        setMainPanelTo(sheetOperationComponent);
    }
    public void switchToSheetsManagement() {
        setMainPanelTo(sheetsManagementComponent);
    }

    public SheetOperationController getSheetOperationController() {
        return sheetOperationController;
    }
}

