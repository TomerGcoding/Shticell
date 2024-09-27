package com.shticell.engine;

import com.shticell.engine.dto.CellDTO;
import com.shticell.engine.dto.RangeDTO;
import com.shticell.engine.dto.SheetDTO;
import com.shticell.engine.range.Range;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.dto.DTOCreator;
import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.utils.SheetFilterer;
import com.shticell.engine.utils.SheetLoader;
import com.shticell.engine.utils.SheetSorter;
import com.shticell.engine.utils.VersionShower;
import jakarta.xml.bind.JAXBException;

import java.io.*;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shticell.engine.dto.DTOCreator.rangeToDTO;
import static com.shticell.engine.dto.DTOCreator.sheetToDTO;

public class EngineImpl implements Engine, Serializable {
    private Sheet sheet = null;
    private  final SheetLoader sheetLoader = new SheetLoader();
    private final Map<Integer,SheetDTO> availableVersions = new HashMap<>();

    @Override
    public SheetDTO loadSheetFile(String filePath) throws JAXBException {
        sheetLoader.loadSheetFile(filePath);
        this.sheet = sheetLoader.getSheet();
        availableVersions.clear();
        availableVersions.put(sheet.getVersion(), sheetToDTO(sheet));
        return sheetToDTO(sheet);
    }

    @Override
    public SheetDTO showSheet() {
        if (sheet == null) {
            throw new IllegalStateException("No sheet is currently loaded.");
        }
        return sheetToDTO(sheet);
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
            throw new IllegalArgumentException("Cell "+ cellId + " can't be updated with the value " + cellValue + " because "
                    + e.getMessage() + "\n" );
        }
        SheetDTO newSheet = sheetToDTO(sheet);
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

    @Override
    public RangeDTO addRange(String name, String cellsRange) {
        if (sheet == null) {
            throw new IllegalStateException("No sheet is currently loaded.");
        }
        Range newRange = sheet.addRange(name, cellsRange);
        return rangeToDTO(newRange, sheet);
    }

    @Override
    public void removeRange(String name) {
        if (sheet == null) {
            throw new IllegalStateException("No sheet is currently loaded.");
        }
        sheet.removeRange(name);
    }

    @Override
    public SheetDTO sortSheet(String rangeToSort,String columnsToSortBy){
        if (sheet == null) {
            throw new IllegalStateException("No sheet is currently loaded.");
        }
        SheetSorter sorter = new SheetSorter(sheet,rangeToSort,columnsToSortBy);
        return sorter.sort();
    }

    @Override
    public SheetDTO filterSheet(String rangeToFilter,String columnsToFilterBy,String valuesToFilterBy){
        if (sheet == null) {
            throw new IllegalStateException("No sheet is currently loaded.");
        }
        SheetFilterer filterer = new SheetFilterer(sheet,rangeToFilter,columnsToFilterBy);
        return filterer.filter(valuesToFilterBy);

    }

    @Override
    public List<String> getUniqueColumnValues(String columnId) {
        return sheet.getUniqeColumnValues(columnId);
    }
}
