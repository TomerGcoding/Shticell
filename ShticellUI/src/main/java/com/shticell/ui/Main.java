package com.shticell.ui;

import com.shticell.engine.Engine;
import com.shticell.engine.EngineImpl;
import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.impl.CellImpl;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.sheet.impl.SheetImpl;
import com.shticell.ui.console.ConsoleUI;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.util.List;


public class Main extends Application {
//            public static void main(String[] args) {
//            Engine engine = new EngineImpl();
//            ConsoleUI ui = new ConsoleUI(engine);
//            ui.start();
//        }

    @Override
    public void start(Stage primaryStage) throws Exception {
        primaryStage.setTitle("Hello There in FXML");

        Parent load = FXMLLoader.load(getClass().getResource("jfx/main/main.fxml"));
        Scene scene = new Scene(load);
        primaryStage.setScene(scene);
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}



