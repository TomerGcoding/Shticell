package engine;

import engine.dto.CellDTO;
import engine.dto.SheetDTO;
import engine.sheet.api.Sheet;
import engine.dto.DTOCreator;
import engine.cell.api.Cell;
import engine.utils.SheetLoader;
import engine.utils.VersionShower;
import jakarta.xml.bind.JAXBException;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class EngineImpl implements Engine, Serializable {
    private Sheet sheet = null;
    private  final SheetLoader sheetLoader = new SheetLoader();
    private final Map<Integer,SheetDTO> availableVersions = new HashMap<>();

    @Override
    public void loadSheetFile(String filePath) throws JAXBException {
        sheetLoader.loadSheetFile(filePath);
        this.sheet = sheetLoader.getSheet();
        availableVersions.clear();
        availableVersions.put(sheet.getVersion(),DTOCreator.sheetToDTO(sheet));
    }

    @Override
    public SheetDTO showSheet() {
        if (sheet == null) {
            throw new IllegalStateException("No sheet is currently loaded.");
        }
        return DTOCreator.sheetToDTO(sheet);
    }

    @Override
    public CellDTO getCellInfo(String cellId) {
        if (sheet == null) {
            throw new IllegalStateException("No sheet is currently loaded.");
        }
        Cell originalCell = sheet.getCell(cellId);
        return originalCell != null? DTOCreator.cellToDTO(originalCell): null;
    }

    @Override
    public void setCell(String cellId, String cellValue) {
        if (sheet == null) {
            throw new IllegalStateException("No sheet is currently loaded.");
        }
        try {
                Sheet newSheet = sheet.setCell(cellId, cellValue);
                if (newSheet != sheet){
                    sheet = newSheet;
                    sheet.incrementVersion();}
        }

        catch (Exception e) {
            throw new IllegalArgumentException("\nFailed to update cell: " + cellId + " with the value: " + cellValue + " because: "
                    + e.getMessage() + "\n" );
        }
        SheetDTO newSheet = DTOCreator.sheetToDTO(sheet);
        availableVersions.put(newSheet.getCurrVersion(),newSheet);
    }

    @Override
    public Map<Integer,Integer> showVersionTable() {
        if(availableVersions.isEmpty()){
            throw new IllegalStateException("No versions were loaded.");
        }
        return VersionShower.getVersionsToChooseFrom(availableVersions.values());
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
