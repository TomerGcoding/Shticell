package com.shticell.ui.jfx.main;

import javafx.beans.property.BooleanProperty;
import javafx.beans.property.SimpleBooleanProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;

public class UIModel {

    private final StringProperty fullPath;
    private final StringProperty name;
    private final BooleanProperty isFileSelected;

    public UIModel(Label fileFullPathLabel, Tab sheetNameTab, Button updateSelectedCellValueButton) {
        this.fullPath = new SimpleStringProperty( );
        this.name = new SimpleStringProperty( );
        this.isFileSelected = new SimpleBooleanProperty(false );
        fileFullPathLabel.textProperty().bind( this.fullPath );
        sheetNameTab.textProperty().bind( this.name );
        updateSelectedCellValueButton.disableProperty().bind( this.isFileSelected.not());
    }

    public StringProperty fullPathProperty( ) {
        return this.fullPath;
    }
    public StringProperty nameProperty( ) {
        return this.name;
    }

}
