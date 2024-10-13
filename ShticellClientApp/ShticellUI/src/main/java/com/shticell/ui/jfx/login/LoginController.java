package com.shticell.ui.jfx.login;


import com.shticell.ui.jfx.main.MainController;
import chat.client.util.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;

public class LoginController {

    @FXML
    public TextField userNameTextField;

    @FXML
    public Label errorMessageLabel;

    private MainController MainController;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
//        HttpClientUtil.setCookieManagerLoggingFacility(line ->
//                Platform.runLater(() ->
//                        updateHttpStatusLine(line)));
    }



    @FXML
    private void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }

    @FXML
    private void quitButtonClicked(ActionEvent e) {
        Platform.exit();
    }

    private void updateHttpStatusLine(String data) {
        MainController.updateHttpLine(data);
    }

    public void setMainController(MainController MainController) {
        this.MainController = MainController;
    }
}

