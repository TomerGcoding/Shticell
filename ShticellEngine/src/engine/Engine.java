package engine;

import engine.dto.*;

public interface Engine {

    void loadSheetFile(String filePath);

    SheetDTO showSheet();

    CellDTO showCell();

    SheetDTO updateCell(String cellId, String cellValue);

    VersionTableDTO showVersionTable();

    SheetDTO showChosenVersion(int chosenVersion);


}
