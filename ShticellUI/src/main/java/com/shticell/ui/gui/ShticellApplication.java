package com.shticell.ui.gui;

import com.shticell.engine.Engine;
import com.shticell.engine.EngineImpl;
import com.shticell.ui.gui.app.AppController;
import com.shticell.ui.gui.body.BodyController;
import com.shticell.ui.gui.header.HeaderController;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.BorderPane;
import javafx.stage.Stage;

import java.net.URL;

public class ShticellApplication extends Application {
    private Engine engine;
    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Shticell");

        FXMLLoader fxmlLoader = new FXMLLoader();
        URL url = getClass().getResource("header/header.fxml");
        fxmlLoader.setLocation(url);
        ScrollPane headerComponent = fxmlLoader.load(url.openStream());
        HeaderController headerController = fxmlLoader.getController();

        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("body/body.fxml");
        fxmlLoader.setLocation(url);
        ScrollPane bodyComponent = fxmlLoader.load(url.openStream());
        BodyController bodyController = fxmlLoader.getController();

        fxmlLoader = new FXMLLoader();
        url = getClass().getResource("app/app.fxml");
        fxmlLoader.setLocation(url);
        BorderPane root = fxmlLoader.load(url.openStream());
        AppController appController = fxmlLoader.getController();

        root.setTop(headerComponent);
        root.setCenter(bodyComponent);

        appController.setBodyComponentController(bodyController);
        appController.setHeaderComponentController(headerController);

        Scene scene = new Scene(root, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

