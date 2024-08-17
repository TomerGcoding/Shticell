package engine;

import engine.dto.CellDTO;
import engine.dto.SheetDTO;
import engine.dto.VersionTableDTO;
import engine.sheet.api.Sheet;

public class EngineImpl implements Engine{
    private Sheet sheet = null;

    public EngineImpl(Sheet sheet) {
        this.sheet = sheet;
    }

    @Override
    public void loadSheetFile(String filePath) {

    }

    @Override
    public SheetDTO showSheet() {
        SheetDTO sheetDTO = new SheetDTO(sheet.getCells(),sheet.getVersion(),
                sheet.getSheetName(),sheet.getProperties());
        return sheetDTO;
    }

    @Override
    public CellDTO showCell() {
        return null;
    }

    @Override
    public SheetDTO setCell(String cellId, String cellValue) {
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
