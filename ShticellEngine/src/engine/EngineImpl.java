package engine;

import engine.dto.CellDTO;
import engine.dto.SheetDTO;
import engine.dto.VersionTableDTO;
import engine.sheet.api.Sheet;
import engine.dto.DTOCreator;
import engine.utils.SheetLoader;
import engine.utils.VersionShower;
import jakarta.xml.bind.JAXBException;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class EngineImpl implements Engine{
    private Sheet sheet = null;
    private SheetLoader sheetLoader = new SheetLoader();
    private Map<Integer,SheetDTO> availableVersions = new HashMap<>();

    @Override
    public void loadSheetFile(String filePath) throws JAXBException {
        try {
            sheetLoader.loadSheetFile(filePath);
            this.sheet = sheetLoader.getSheet();
            availableVersions.clear();
            availableVersions.put(sheet.getVersion(),DTOCreator.sheetToDTO(sheet));
        } catch (JAXBException | IllegalArgumentException e) {
            // Handle exceptions, such as invalid XML format or invalid sheet dimensions
            throw e;  // rethrow to maintain behavior
        }
    }

    @Override
    public SheetDTO showSheet() {
        if (sheet == null) {
            throw new IllegalStateException("No sheet is currently loaded.");
        }
        SheetDTO sheetDTO = DTOCreator.sheetToDTO(sheet);
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
            return null;
        }
        finally {
            System.out.println("we tried to update cell: " + cellId + "with value: " + cellValue);
        }
        SheetDTO newSheet = DTOCreator.sheetToDTO(sheet);
        availableVersions.put(newSheet.getCurrVersion(),newSheet);
        return newSheet;
    }

    @Override
    public Map<Integer,Integer> showVersionTable() {
        if(availableVersions.isEmpty()){
            throw new IllegalStateException("No versions were loaded.");
        }
        Map<Integer,Integer> versionTable = VersionShower.getVersionsToChooseFrom(availableVersions.values());
        return versionTable;
    }

    @Override
    public SheetDTO showChosenVersion(int chosenVersion) {
        return availableVersions.get(chosenVersion);
    }
}
