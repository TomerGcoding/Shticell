package com.shticell.ui.jfx.main;

import javafx.animation.*;
import javafx.scene.Node;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import javafx.util.Duration;
import javafx.scene.control.Label;
import javafx.scene.shape.Rectangle;

import java.awt.*;

public class AnimationManager {
    private static boolean animationsEnabled = false;

    public static void setAnimationsEnabled(boolean enabled) {
        animationsEnabled = enabled;
    }

    public static boolean areAnimationsEnabled() {
        return animationsEnabled;
    }


    public static void animateSheetPresentation(Node sheet) {
        if (!animationsEnabled) return;

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(500), sheet);
        scaleTransition.setFromX(0);
        scaleTransition.setFromY(0);
        scaleTransition.setToX(1);
        scaleTransition.setToY(1);
        scaleTransition.play();
    }

    public static void animateCellSelection(Node cell) {
        if (!animationsEnabled) return;

        ScaleTransition scaleTransition = new ScaleTransition(Duration.millis(200), cell);
        scaleTransition.setFromX(1);
        scaleTransition.setFromY(1);
        scaleTransition.setToX(1.05);
        scaleTransition.setToY(1.05);
        scaleTransition.setCycleCount(2);
        scaleTransition.setAutoReverse(true);
        scaleTransition.play();
    }

    public static void animateShticellLabel(Label shticellLabel) {
        if (!animationsEnabled) {
            shticellLabel.setTextFill(Color.BLACK);
            shticellLabel.setRotate(0);
            return;
        }

        RotateTransition rotateTransition = new RotateTransition(Duration.seconds(1), shticellLabel);
        rotateTransition.setByAngle(360);
        rotateTransition.setCycleCount(1);


        ParallelTransition parallelTransition = new ParallelTransition(rotateTransition);
        parallelTransition.play();
    }
}
