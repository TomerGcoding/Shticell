package engine;

import engine.dto.*;
import jakarta.xml.bind.JAXBException;

import java.util.Map;

public interface Engine {

    void loadSheetFile(String filePath) throws JAXBException;

    SheetDTO showSheet();

    CellDTO showCell();

    SheetDTO setCell(String cellId, String cellValue);

    Map<Integer,Integer> showVersionTable();

    SheetDTO showChosenVersion(int chosenVersion);


}
