package engine;

import engine.dto.*;
import jakarta.xml.bind.JAXBException;

public interface Engine {

    void loadSheetFile(String filePath) throws JAXBException;

    SheetDTO showSheet();

    CellDTO getCellInfo(String cellId);

    void setCell(String cellId, String cellValue);

    VersionTableDTO showVersionTable();

    SheetDTO showChosenVersion(int chosenVersion);


}
