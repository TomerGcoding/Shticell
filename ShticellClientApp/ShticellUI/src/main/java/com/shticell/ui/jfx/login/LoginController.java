package com.shticell.ui.jfx.login;


import com.shticell.ui.jfx.main.MainController;
import com.shticell.ui.jfx.sheetOperations.SheetOperationController;
import com.shticell.ui.jfx.utils.http.HttpClientUtil;
import javafx.application.Platform;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.input.KeyEvent;
import org.jetbrains.annotations.NotNull;
import okhttp3.*;
import javafx.event.ActionEvent;
import java.io.IOException;

import static com.shticell.ui.jfx.utils.Constants.BASE_URL;
import static com.shticell.ui.jfx.utils.Constants.LOGIN_PAGE;

public class LoginController {

    @FXML
    public TextField userNameTextField;

    @FXML
    public Label errorMessageLabel;

    private Runnable loginListener;

    private final StringProperty errorMessageProperty = new SimpleStringProperty();

    private String userName;
    @FXML
    public void initialize() {
        errorMessageLabel.textProperty().bind(errorMessageProperty);
    }

    @FXML
    private void loginButtonClicked() {
        String userName = userNameTextField.getText();
        setUserName(userName);
        if (userName.isEmpty()) {
            errorMessageProperty.set("User name is empty. You can't login with empty user name");
            return;
        }

        String finalUrl = HttpUrl
                .parse(BASE_URL + LOGIN_PAGE)
                .newBuilder()
                .addQueryParameter("username", userName)
                .build()
                .toString();


        HttpClientUtil.runAsync(finalUrl, new Callback() {

            @Override
            public void onFailure(@NotNull Call call, @NotNull IOException e) {
                Platform.runLater(() ->
                        errorMessageProperty.set("Something went wrong: " + e.getMessage())
                );
            }

            @Override
            public void onResponse(@NotNull Call call, @NotNull Response response) throws IOException {
                if (response.code() != 200) {
                    String responseBody = response.body().string();
                    response.close();
                    Platform.runLater(() ->
                            errorMessageProperty.set("Something went wrong: " + responseBody)
                    );
                } else {
                    setUserName(userName);
                    Platform.runLater(() -> {
                      loginListener.run();
                    });
                }
            }
        });
    }

    private void setUserName(String userName) {
        this.userName = userName;  }

    public String getUserName() {
        return userName;
    }

    @FXML
    private void userNameKeyTyped(KeyEvent event) {
        errorMessageProperty.set("");
    }


    public void setLoginListener(Runnable listener) {
        this.loginListener = listener;
    }

    @FXML
    private void handleKeyPress(KeyEvent event) {
        if (event.getCode().toString().equals("ENTER")) {
            loginButtonClicked();
        }
    }
}

