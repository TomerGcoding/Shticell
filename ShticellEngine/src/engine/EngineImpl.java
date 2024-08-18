package engine;

import engine.dto.CellDTO;
import engine.dto.SheetDTO;
import engine.dto.VersionTableDTO;
import engine.sheet.api.Sheet;

import java.util.ArrayList;
import java.util.List;

public class EngineImpl implements Engine{
    private Sheet sheet = null;
    private List<SheetDTO> sheets = new ArrayList<>();

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

        //just an example of some kind of exception struct
        try {
            sheet.setCell(cellId, cellValue);
            }
        catch (Exception e) {
            System.out.println("something went wrong with update the cell, please try again");
        }
        finally {
            System.out.println("we tried to update cell: " + cellId + "with value: " + cellValue);
        }
        SheetDTO newSheet = new SheetDTO(sheet.getCells(),sheet.getVersion(), sheet.getSheetName(),sheet.getProperties());
        sheets.add(newSheet);
        return newSheet;
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
