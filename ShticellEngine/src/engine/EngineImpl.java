package engine;

import engine.dto.CellDTO;
import engine.dto.SheetDTO;
import engine.sheet.api.Sheet;
import engine.dto.DTOCreator;
import engine.sheet.cell.api.Cell;
import engine.utils.SheetLoader;
import engine.utils.VersionShower;
import jakarta.xml.bind.JAXBException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class EngineImpl implements Engine, Serializable {
    private Sheet sheet = null;
    private SheetLoader sheetLoader = new SheetLoader();
    private Map<Integer,SheetDTO> availableVersions = new HashMap<>();

    @Override
    public void loadSheetFile(String filePath) throws JAXBException {
        try {
            sheetLoader.loadSheetFile(filePath);
        }
        catch (IllegalArgumentException e)
        {
            throw e;
        }
        this.sheet = sheetLoader.getSheet();
        availableVersions.clear();
        availableVersions.put(sheet.getVersion(),DTOCreator.sheetToDTO(sheet));
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
    public CellDTO getCellInfo(String cellId) {
        Cell originalCell = sheet.getCell(cellId);
        if (originalCell == null) {
            return null;
        }

        return DTOCreator.cellToDTO(originalCell);
    }

    @Override
    public void setCell(String cellId, String cellValue) {

        //just an example of some kind of exception struct
        try {
            if (cellValue.isEmpty())
                sheet.deleteCell(cellId);
            else
                this.sheet = sheet.setCell(cellId, cellValue);
            sheet.incrementVersion();
            }
        catch (Exception e) {
            throw new IllegalArgumentException("\nFailed to update cell: " + cellId + " with the value: " + cellValue);
        }
        SheetDTO newSheet = DTOCreator.sheetToDTO(sheet);
        availableVersions.put(newSheet.getCurrVersion(),newSheet);
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
        if (availableVersions.containsKey(chosenVersion)) {
            return availableVersions.get(chosenVersion);
        }
        throw new IllegalStateException("No version was found for version: " + chosenVersion);
    }

    @Override
    public void writeEngineToFile(String fileName) throws IOException {
        try (ObjectOutputStream out =
                     new ObjectOutputStream(
                             new FileOutputStream(fileName))) {
            out.writeObject(this);
            out.flush();
        } catch (IOException e) {
            throw e;
        }
    }

    @Override
    public Engine readEngineFromFile(String fileName) throws IOException, ClassNotFoundException {
        try (ObjectInputStream in =
                     new ObjectInputStream(
                             new FileInputStream(fileName))) {
            Engine engine = (EngineImpl) in.readObject();
            return engine;
        }catch (IOException e){
            throw e;

        }catch (ClassNotFoundException e) {
            throw e;
        }
    }
}
