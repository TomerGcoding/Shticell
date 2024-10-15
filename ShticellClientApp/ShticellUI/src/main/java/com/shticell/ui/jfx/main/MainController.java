package com.shticell.ui.jfx.main;

import javafx.beans.binding.Bindings;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import com.shticell.ui.jfx.login.*;
import com.shticell.ui.jfx.sheetOperations.*;

import java.io.IOException;
import java.net.URL;

import static com.shticell.ui.jfx.utils.Constants.*;

public class MainController {

    @FXML
    private AnchorPane mainPanel;

    @FXML
    private Label userNameLabel;

    private VBox loginComponent;
    private LoginController loginComponentController;

    private ScrollPane SheetOperationComponent;
    private SheetOperationController SheetOperationComponentController;

    private final StringProperty currentUserName;

    public MainController() {
        currentUserName = new SimpleStringProperty("User");
    }

    @FXML
    public void initialize() {
        userNameLabel.textProperty().bind(Bindings.concat("Hello, ", currentUserName + "!"));
        loadLoginPage();
        loadSheetOperationPage();
    }

    private void loadSheetOperationPage() {
        URL SheetOperationPageUrl = getClass().getResource(SHEET_OPERATION_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(SheetOperationPageUrl);
            SheetOperationComponent = fxmlLoader.load();
            SheetOperationComponentController = fxmlLoader.getController();
            SheetOperationComponentController.setMainController(this);
            setMainPanelTo(SheetOperationComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void updateUserName(String userName) {
        currentUserName.set(userName);
    }

    private void setMainPanelTo(Parent pane) {
        mainPanel.getChildren().add(pane);
        AnchorPane.setBottomAnchor(pane, 1.0);
        AnchorPane.setTopAnchor(pane, 1.0);
        AnchorPane.setLeftAnchor(pane, 1.0);
        AnchorPane.setRightAnchor(pane, 1.0);
    }
    private void loadLoginPage() {
        URL loginPageUrl = getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION);
        try {
            FXMLLoader fxmlLoader = new FXMLLoader();
            fxmlLoader.setLocation(loginPageUrl);
            loginComponent = fxmlLoader.load();
            loginComponentController = fxmlLoader.getController();
            loginComponentController.setMainController(this);
            setMainPanelTo(loginComponent);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void switchToSheetOperation() {
        setMainPanelTo(SheetOperationComponent);
    }
}

