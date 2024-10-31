package com.shticell.ui;

import com.shticell.ui.jfx.main.MainController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import com.shticell.ui.jfx.login.LoginController;

import java.net.URL;

import static com.shticell.ui.jfx.utils.Constants.*;
import static com.shticell.ui.jfx.utils.http.HttpClientUtil.shutdown;


public class Main extends Application {

    private Stage primaryStage;
    private String userName;
    private LoginController loginController;

    @Override
    public void start(Stage primaryStage) throws Exception {
        this.primaryStage = primaryStage;
        showLoginPage();

    }
    private void showLoginPage() throws Exception {
        primaryStage.setTitle("Shticell - Login");
        FXMLLoader loader = new FXMLLoader();
        URL loginFXML = getClass().getResource(LOGIN_PAGE_FXML_RESOURCE_LOCATION); // Update the path accordingly
        loader.setLocation(loginFXML);
        Parent root = loader.load();
        root.getStyleClass().add("shticell");
        Scene scene = new Scene(root, 500, 400);
        primaryStage.setScene(scene);
        primaryStage.setMinWidth(400);
        primaryStage.setMinHeight(400);
        primaryStage.setOnCloseRequest(event -> {
            event.consume();
            confirmExit();
        });

        primaryStage.show();

        this.loginController = loader.getController();
        loginController.setLoginListener(this::showMainApp);
        //this.userName = loginController.getUserName();
    }

    private void confirmExit() {
        primaryStage.close();
        shutdown();
    }
    // Method to show the main application
    private void showMainApp() {
        try {
            this.userName = loginController.getUserName();
            FXMLLoader loader = new FXMLLoader();
            URL mainFXML = getClass().getResource(MAIN_PAGE_FXML_RESOURCE_LOCATION);
            loader.setLocation(mainFXML);
            Parent root = loader.load();
            Scene scene = new Scene(root, 820, 620);
            MainController mainController = loader.getController();
            mainController.setUserName(userName);
            mainController.loadPages(userName);
            primaryStage.setScene(scene);
            primaryStage.setTitle("Shticell - Application");
            primaryStage.centerOnScreen();
            primaryStage.show(); // Ensure the stage is shown
        } catch (Exception e) {
            e.printStackTrace(); // Handle exceptions appropriately
        }
    }



public static void main(String[] args) {
        launch(args);
    }
}




