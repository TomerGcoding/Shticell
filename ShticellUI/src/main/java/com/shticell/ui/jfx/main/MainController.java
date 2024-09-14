package com.shticell.ui.jfx.main;

import com.shticell.engine.Engine;
import com.shticell.engine.EngineImpl;
import com.shticell.engine.dto.CellDTO;
import com.shticell.engine.dto.SheetDTO;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;
import com.shticell.ui.jfx.version.VersionController;
import com.shticell.ui.jfx.range.RangeController;
import javafx.application.Platform;
import javafx.beans.property.ObjectProperty;
import javafx.beans.property.SimpleObjectProperty;
import javafx.beans.property.StringProperty;
import javafx.concurrent.Task;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.control.*;
import javafx.scene.layout.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.ColumnConstraints;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.RowConstraints;
import javafx.stage.FileChooser;


import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class MainController {

    @FXML
    private BorderPane mainBorderPane;
    @FXML
    private Button changeStyleButton;
    @FXML
    private VersionController versionSelectorComponentController;
    @FXML
    private AnchorPane versionSelectorComponent;
    @FXML
    private ProgressBar progressBar;

    @FXML
    private Label progressLabel;

    @FXML
    private Label chosenFileFullPathLabel;

    @FXML
    private Label currentCellLabel;
    @FXML
    private Label currentCellVersionLabel;
    @FXML
    private Button loadXMLFileButton;
    @FXML
    private TextField selectedCellOriginalValueTextField;
    @FXML
    private Tab sheetTab;
    @FXML
    private TabPane sheetTabPane;
    @FXML
    private Button updateSelectedCellValueButton;

    private Engine engine = new EngineImpl();

    private UIModel uiModel;

    private GridPane sheetGridPane=new GridPane();

    private SheetGridManager gridManager;

    private Map<String,Label> cellIDtoLabel = new HashMap<>();

    private ObjectProperty<Label> selectedCell;

    private RangeController rangeController;

    @FXML
    private void initialize() {
        uiModel = new UIModel(chosenFileFullPathLabel, sheetTab,updateSelectedCellValueButton,sheetGridPane,currentCellLabel,selectedCellOriginalValueTextField,
                currentCellVersionLabel,versionSelectorComponent);
        gridManager = new SheetGridManager(sheetGridPane,uiModel,engine,this);
        chosenFileFullPathLabel.setId("file-path");
        selectedCell = new SimpleObjectProperty<>();
        selectedCell.addListener((observable, oldValue, newValue) -> {
            if(oldValue != null) {
                oldValue.setId(null);
            }
            if(newValue != null) {
                newValue.setId("selected-cell");
            }
        });
        versionSelectorComponentController.setEngine(engine);
//        try {
//            FXMLLoader loader = new FXMLLoader(getClass().getResource("/com/shticell/ui/jfx/range/range.fxml"));
//            Parent rangeView = loader.load();
//            mainBorderPane.setRight(rangeView);
//            rangeController = loader.getController();
//            rangeController.setEngine(engine);
//        } catch (IOException e) {
//            e.printStackTrace();
//        }
    }


    @FXML
    public void loadXMLFile(ActionEvent event) {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Open XML file");
        fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("XML files", "*.xml"));
        File file = fileChooser.showOpenDialog(null);
        if (file != null) {
            chosenFileFullPathLabel.setVisible(false);
            progressBar.setVisible(true);
            progressBar.setManaged(true);
            progressLabel.setVisible(true);
            progressLabel.setManaged(true);
            uiModel.isLoadingProperty().set(true);
            Task<SheetDTO> loadTask = new Task<SheetDTO>() {
                protected SheetDTO call() throws Exception {
                    updateMessage("Fetching file...");
                    updateProgress(0.2, 1);
                    Thread.sleep(1000); // Simulating some work

                    updateMessage("Loading sheet data...");
                    updateProgress(0.6, 1);
                    SheetDTO sheetDTO = engine.loadSheetFile(file.getAbsolutePath());
                    Thread.sleep(1000); // Simulating some work

                    updateMessage("Creating sheet grid...");
                    updateProgress(0.8, 1);
                    Thread.sleep(1000); // Simulating some work

                    updateMessage("Sheet loaded successfully!");
                    updateProgress(1, 1);
                    return sheetDTO;
                }
            };
            loadTask.setOnSucceeded(e -> {
                SheetDTO sheetDTO = loadTask.getValue();
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    progressBar.setManaged(false);
                    progressLabel.setVisible(false);
                    progressLabel.setManaged(false);
                    chosenFileFullPathLabel.setVisible(true);
                    uiModel.isLoadingProperty().set(false);
                    uiModel.fullPathProperty().setValue(file.getAbsolutePath());
                    uiModel.nameProperty().setValue(engine.showSheet().getSheetName());
                    uiModel.selectedCellIdProperty().set(null);
                    uiModel.selectedCellOriginalValueProperty().set(null);
                    uiModel.selectedCellVersionProperty().set(0);
                    versionSelectorComponentController.clearAllVersions();
                    versionSelectorComponentController.addVersion(engine.showSheet().getCurrVersion());
                    uiModel.isFileSelectedProperty().setValue(true);
                    gridManager.createSheetGridPane(sheetDTO);
                    sheetTab.setContent(sheetGridPane);
                });
            });
            loadTask.setOnFailed(e -> {
                Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    progressBar.setManaged(false);
                    progressLabel.setVisible(false);
                    progressLabel.setManaged(false);
                    chosenFileFullPathLabel.setVisible(true);
                    uiModel.isLoadingProperty().set(false);
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Error");
                    alert.setHeaderText("Failed to load file:");
                    alert.setContentText(loadTask.getException().getMessage());
                    alert.showAndWait();
                });
            });
            progressBar.progressProperty().bind(loadTask.progressProperty());
            progressLabel.textProperty().bind(loadTask.messageProperty());

            Thread loadThread = new Thread(loadTask);
            loadThread.setDaemon(true);
            loadThread.start();
        }
    }


    @FXML
    public void updateSelectedCellValue(ActionEvent event) {
        try {
            if (selectedCell.get()==null) {
                throw new IllegalStateException("Please select a cell to update.");
            }
            if(isCellChanged(currentCellLabel.getText())) {

                engine.setCell(currentCellLabel.getText(), selectedCellOriginalValueTextField.getText());
                versionSelectorComponentController.addVersion(engine.showSheet().getCurrVersion());
                SheetDTO updatedSheet = engine.showSheet();
                CellDTO updatedCell = updatedSheet.getCell(CoordinateFormatter.cellIdToIndex(currentCellLabel.getText())[0],
                        CoordinateFormatter.cellIdToIndex(currentCellLabel.getText())[1]);
                uiModel.cellIdProperty(currentCellLabel.getText()).setValue(updatedCell.getEffectiveValue().toString());
                uiModel.selectedCellOriginalValueProperty().set(selectedCellOriginalValueTextField.getText());
                uiModel.selectedCellVersionProperty().setValue(updatedCell.getVersion());

                // Update the labels of influenced cells
                for (CellDTO influencedCell : updatedCell.getInfluencingOn()) {
                    String influencedCellId = influencedCell.getId();
                    uiModel.cellIdProperty(influencedCellId).setValue(influencedCell.getEffectiveValue().toString());
                }
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Error");
            alert.setHeaderText("Failed to update Cell:");
            alert.setContentText(e.getMessage());
            alert.showAndWait();
        }
    }
    private boolean isCellChanged(String cellId){
        CellDTO cell = engine.getCellInfo(cellId);
        boolean changed = true;
        if(cell == null) {
            return true;
        }
        if (cell.getOriginalValue().equals(selectedCellOriginalValueTextField.getText().trim()))
        {
            changed = false;
        }
        return changed;
    }


    public void addMouseClickEventForCell(String cellID, Label label) {
        label.setOnMouseClicked(event->{
            gridManager.resetCellBorders();
            selectedCell.set(label);
            int cellRow = CoordinateFormatter.cellIdToIndex(cellID)[0];
            int cellCol = CoordinateFormatter.cellIdToIndex(cellID)[1];
            SheetDTO sheetDTO = engine.showSheet();
            CellDTO cell = sheetDTO.getCell(cellRow, cellCol);
            uiModel.selectedCellOriginalValueProperty().set(cell == null?"": cell.getOriginalValue());
            uiModel.selectedCellVersionProperty().set(cell == null? 0:cell.getVersion());
            uiModel.selectedCellIdProperty().set(cellID);
            gridManager.highlightDependenciesAndInfluences(cell);
        });
    }

    private void createRangeController() {
        FXMLLoader loader = new FXMLLoader();
        loader.setLocation(getClass().getResource("/com/shticell/ui/jfx/main/Range.fxml"));
        RangeController rangeController = (RangeController)loader.getController();

    }
}
