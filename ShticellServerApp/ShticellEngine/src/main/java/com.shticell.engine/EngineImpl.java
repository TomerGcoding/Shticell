package com.shticell.engine;

import dto.RangeDTO;
import dto.SheetDTO;
import com.shticell.engine.range.Range;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.utils.SheetFilterer;
import com.shticell.engine.utils.SheetLoader;
import com.shticell.engine.utils.SheetSorter;
import com.shticell.engine.utils.VersionShower;
import jakarta.xml.bind.JAXBException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shticell.engine.utils.DTOCreator.rangeToDTO;
import static com.shticell.engine.utils.DTOCreator.sheetToDTO;

public class EngineImpl implements Engine, Serializable {
    private Map<String, Sheet> sheets = new HashMap<>();
    private  final SheetLoader sheetLoader = new SheetLoader();
    private final Map<String, Map<Integer, SheetDTO>> avilableVersions = new HashMap<>();
    private Map <String, String> sheetNameToUser = new HashMap<>();

    @Override
    public SheetDTO loadSheetFile(String filePath, String userName) throws JAXBException {
        sheetLoader.loadSheetFile(filePath);
        Sheet sheet = sheetLoader.getSheet();
        if (sheets.containsKey(sheet.getSheetName())) {
            throw new IllegalArgumentException("Sheet with the name " + sheet.getSheetName() + " already exists.");
        }
        this.sheets.put(sheet.getSheetName(), sheet);
        Map <Integer,SheetDTO> versions = new HashMap<>();
        versions.put(sheet.getVersion(),sheetToDTO(sheet));
        avilableVersions.put(sheet.getSheetName(), versions);
        sheetNameToUser .put(sheet.getSheetName(),userName);
        return sheetToDTO(sheet);
    }

    @Override
    public SheetDTO showSheet(String sheetName) {
        if (sheets.isEmpty()) {
            throw new IllegalStateException("No sheet is currently loaded.");
        }
        if (!sheets.containsKey(sheetName)) {
            throw new IllegalArgumentException("Sheet with the name " + sheetName + " does not exist.");
        }
        return sheetToDTO(sheets.get(sheetName));
    }


    @Override
    public void setCell(String sheetName, String cellId, String cellValue) {

        if (!sheets.containsKey(sheetName)){
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        try {
                Sheet sheet = sheets.get(sheetName);
                Sheet newSheet = sheet.setCell(cellId, cellValue);
                if (newSheet != sheet){
                    sheets.put(sheetName,newSheet);
                    newSheet.incrementVersion();}
        }

        catch (Exception e) {
            throw new IllegalArgumentException("Cell "+ cellId + " can't be updated with the value " + cellValue + " because "
                    + e.getMessage() + "\n" );
        }
        SheetDTO newSheet = sheetToDTO(sheets.get(sheetName));
        Map<Integer, SheetDTO> versions = avilableVersions.get(sheetName);
        versions.put(newSheet.getCurrVersion(),newSheet);
    }

    @Override
    public Map<Integer,Integer> showVersionTable(String sheetName) {
        if(!avilableVersions.containsKey(sheetName)){
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        return VersionShower.getVersionsToChooseFrom(avilableVersions.get(sheetName).values());
    }

    @Override
    public SheetDTO showChosenVersion(String sheetName, int chosenVersion) {
        if (!avilableVersions.containsKey(sheetName)) {
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        if (avilableVersions.get(sheetName).containsKey(chosenVersion)) {
            return avilableVersions.get(sheetName).get(chosenVersion);
        }
        throw new IllegalStateException("No version was found for version: " + chosenVersion);
    }

    @Override
    public RangeDTO addRange(String sheetName, String name, String cellsRange) {
        if (!sheets.containsKey(sheetName)) {
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        Sheet sheet = sheets.get(sheetName);
        Range newRange = sheet.addRange(name, cellsRange);
        return rangeToDTO(newRange, sheet);
    }

    @Override
    public void removeRange(String sheetName, String name) {
        if (!avilableVersions.containsKey(sheetName)) {
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        Sheet sheet = sheets.get(sheetName);
        sheet.removeRange(name);
    }

    @Override
    public SheetDTO sortSheet(String sheetName, String rangeToSort,String columnsToSortBy){
        if (!sheets.containsKey(sheetName)) {
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        Sheet sheet = sheets.get(sheetName);
        SheetSorter sorter = new SheetSorter(sheet,rangeToSort,columnsToSortBy);
        return sorter.sort();
    }

    @Override
    public SheetDTO filterSheet(String sheetName, String rangeToFilter,String columnsToFilterBy,List<String> valuesToFilterBy){
        if (!sheets.containsKey(sheetName)) {
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        Sheet sheet = sheets.get(sheetName);
        SheetFilterer filterer = new SheetFilterer(sheet,rangeToFilter,columnsToFilterBy);
        return filterer.filter(valuesToFilterBy);

    }

    @Override
    public Map<String,List<SheetDTO>> getAllSheets(){
        Map<String,List<SheetDTO>> allSheets = new HashMap<>();

        for (Map.Entry<String,Sheet> entry : sheets.entrySet()){
            String sheetName = entry.getKey();
            String userName = sheetNameToUser.get(sheetName);

            if (!allSheets.containsKey(userName)){
                List<SheetDTO> sheetsForUser = new ArrayList<>();
                sheetsForUser.add(sheetToDTO(entry.getValue()));
                allSheets.put(userName, sheetsForUser);
            }
            else {
                List<SheetDTO> sheets = allSheets.get(userName);
                sheets.add(sheetToDTO(entry.getValue()));
                allSheets.put(userName,sheets);
            }
        }
        return allSheets;
    }


}
