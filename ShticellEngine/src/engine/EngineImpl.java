package engine;

import engine.dto.CellDTO;
import engine.dto.SheetDTO;
import engine.dto.VersionTableDTO;

public class EngineImpl implements Engine{
    @Override
    public void loadSheetFile(String filePath) {

    }

    @Override
    public SheetDTO showSheet() {

        return null;
    }

    @Override
    public CellDTO showCell() {
        return null;
    }

    @Override
    public SheetDTO updateCell(String cellId, String cellValue) {
        return null;
    }

    @Override
    public VersionTableDTO showVersionTable() {
        return null;
    }

    @Override
    public SheetDTO showChosenVersion(int chosenVersion) {
        return null;
    }
}
