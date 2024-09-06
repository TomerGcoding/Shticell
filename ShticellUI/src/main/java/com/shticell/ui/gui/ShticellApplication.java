package com.shticell.ui.gui;

import com.shticell.engine.Engine;
import com.shticell.engine.EngineImpl;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class ShticellApplication extends Application {
    private Engine engine;
    @Override
    public void start(Stage primaryStage) throws Exception {
        engine = new EngineImpl();
        primaryStage.setTitle("Shticell");

        Parent load = FXMLLoader.load(getClass().getResource("header/header.fxml"));
        Scene scene = new Scene(load, 600, 400);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}

