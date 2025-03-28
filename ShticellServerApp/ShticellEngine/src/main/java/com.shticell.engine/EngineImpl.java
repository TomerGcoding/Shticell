package com.shticell.engine;

import com.shticell.engine.users.UserManager;
import com.shticell.engine.users.accessPermission.AccessPermissionStatus;
import com.shticell.engine.users.accessPermission.AccessPermissionType;
import com.shticell.engine.users.accessPermission.SheetUserAccessManager;
import com.shticell.engine.utils.*;
import dto.RangeDTO;
import dto.SheetDTO;
import com.shticell.engine.range.Range;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.users.accessPermission.UserAccessPermission;
import dto.SheetUsersAccessDTO;
import dto.UserAccessDTO;
import jakarta.xml.bind.JAXBException;

import java.io.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.shticell.engine.users.accessPermission.AccessPermissionType.NONE;
import static com.shticell.engine.users.accessPermission.AccessPermissionType.OWNER;
import static com.shticell.engine.users.accessPermission.AccessPermissionStatus.APPROVED;
import static com.shticell.engine.utils.DTOCreator.*;

public class EngineImpl implements Engine, Serializable {
    private Map<String, Sheet> sheets = new HashMap<>();
    private  final SheetLoader sheetLoader = new SheetLoader();
    private final Map<String, Map<Integer, SheetDTO>> avilableVersions = new HashMap<>();
    private final Map<String, SheetUserAccessManager> sheetNameToSheetUserAccessManager = new HashMap<>();
    private final UserManager userManager = new UserManager();

    @Override
    public SheetDTO loadSheetFile(String filePath, String userName) throws JAXBException {
        sheetLoader.loadSheetFile(filePath, userName);
        Sheet sheet = sheetLoader.getSheet();
        setSheetOwner(sheet, userName);
        synchronized (this) {
            if (sheets.containsKey(sheet.getSheetName())) {
                throw new IllegalArgumentException("Sheet with the name " + sheet.getSheetName() + " already exists.");
            }
            this.sheets.put(sheet.getSheetName(), sheet);
        }
        Map <Integer,SheetDTO> versions = new HashMap<>();
        versions.put(sheet.getVersion(),sheetToDTO(sheet));
        avilableVersions.put(sheet.getSheetName(), versions);
        sheet.getSheetUserAccessManager().addUserAccessPermission(new UserAccessPermission(userName, OWNER, APPROVED));
        return sheetToDTO(sheet);
    }

    private void setSheetOwner(Sheet sheet, String userName) {
        sheet.setSheetOwner(userName);
    }

    @Override
    public SheetDTO showSheet(String sheetName, String userName) {

        if (sheets.isEmpty()) {
            throw new IllegalStateException("No sheet is currently loaded.");
        }
        if (!sheets.containsKey(sheetName)) {
            throw new IllegalArgumentException("Sheet with the name " + sheetName + " does not exist.");
        }
        sheets.get(sheetName).checkUserAccess(userName, AccessPermissionType.READER);
        return sheetToDTO(sheets.get(sheetName));
    }

    @Override
    public void setCell(String sheetName, String cellId, String cellValue, String userName) {

        if (!sheets.containsKey(sheetName)){
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        try {
                Sheet sheet = sheets.get(sheetName);
                sheet.checkUserAccess(userName, AccessPermissionType.WRITER);
                Sheet newSheet = sheet.setCell(cellId, cellValue,userName);
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
    public Map<Integer,Integer> showVersionTable(String sheetName, String userName) {
        if(!avilableVersions.containsKey(sheetName)){
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        sheets.get(sheetName).checkUserAccess(userName, AccessPermissionType.READER);
        return VersionShower.getVersionsToChooseFrom(avilableVersions.get(sheetName).values());
    }

    @Override
    public SheetDTO showChosenVersion(String sheetName, int chosenVersion, String userName) {
        if (!avilableVersions.containsKey(sheetName)) {
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        sheets.get(sheetName).checkUserAccess(userName, AccessPermissionType.READER);
        if (avilableVersions.get(sheetName).containsKey(chosenVersion)) {
            return avilableVersions.get(sheetName).get(chosenVersion);
        }
        throw new IllegalStateException("No version was found for version: " + chosenVersion);
    }

    @Override
    public RangeDTO addRange(String sheetName, String name, String cellsRange, String userName) {
        if (!sheets.containsKey(sheetName)) {
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        Sheet sheet = sheets.get(sheetName);
        sheet.checkUserAccess(userName, AccessPermissionType.WRITER);
        Range newRange = sheet.addRange(name, cellsRange);
        return rangeToDTO(newRange, sheet);
    }

    @Override
    public void removeRange(String sheetName, String name, String userName) {
        if (!avilableVersions.containsKey(sheetName)) {
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        Sheet sheet = sheets.get(sheetName);
        sheet.checkUserAccess(userName, AccessPermissionType.WRITER);
        sheet.removeRange(name);
    }

    @Override
    public SheetDTO sortSheet(String sheetName, String rangeToSort,String columnsToSortBy, String userName) {
        if (!sheets.containsKey(sheetName)) {
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        Sheet sheet = sheets.get(sheetName);
        sheet.checkUserAccess(userName, AccessPermissionType.READER);
        SheetSorter sorter = new SheetSorter(sheet,rangeToSort,columnsToSortBy,userName);
        return sorter.sort();
    }

    @Override
    public SheetDTO filterSheet(String sheetName, String rangeToFilter,String columnsToFilterBy,List<String> valuesToFilterBy, String userName){
        if (!sheets.containsKey(sheetName)) {
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        Sheet sheet = sheets.get(sheetName);
        sheet.checkUserAccess(userName, AccessPermissionType.READER);
        SheetFilterer filterer = new SheetFilterer(sheet,rangeToFilter,columnsToFilterBy,userName);
        return filterer.filter(valuesToFilterBy);

    }

    @Override
    public Map<String, SheetDTO> getAllSheets(String userName) {
        Map<String, SheetDTO> allSheets = new HashMap<>();
        for (Map.Entry<String, Sheet> entry : sheets.entrySet()){
            Sheet sheet = entry.getValue();
            if (!sheet.getSheetUserAccessManager().getSheetUserAccessManager().containsKey(userName)){
                sheet.getSheetUserAccessManager().addUserAccessPermission(new UserAccessPermission(userName, AccessPermissionType.NONE, AccessPermissionStatus.APPROVED));
            }
            SheetDTO sheetDTO = sheetToDTO(sheet);
            allSheets.put(sheet.getSheetName(), sheetDTO);
            }
        return allSheets;
    }

    private String getSheetOwner (String sheetName){
        SheetUserAccessManager sheetUserAccessManager = sheets.get(sheetName).getSheetUserAccessManager();
        for (Map.Entry<String, UserAccessPermission> entry : sheetUserAccessManager.getSheetUserAccessManager().entrySet()){
            if (entry.getValue().getAccessPermissionType() == OWNER && entry.getValue().getAccessPermissionStatus() == APPROVED){
                return entry.getKey();
            }
        }
        throw new IllegalStateException("No owner was found for the sheet " + sheetName);
    }

    @Override
    public SheetUsersAccessDTO getSheetUsersAccess(String sheetName) {
        if (!sheetNameToSheetUserAccessManager.containsKey(sheetName)) {
            throw new IllegalArgumentException("Sheet with the name " + sheetName + " does not exist.");
        }
        return sheetUsersAccessToDTO(sheets.get(sheetName).getSheetUserAccessManager());
    }

    @Override
    public void addUser(String userName){
        userManager.addUser(userName);
        for (Sheet sheet : sheets.values()){
            sheet.getSheetUserAccessManager().addUserAccessPermission(new UserAccessPermission(userName, NONE, APPROVED));
        }
    }

    @Override
    public UserManager getUserManager() {
        return userManager;
    }

    @Override
    public SheetDTO setCellForDynamicAnalysis(String sheetName, String cellId, String cellValue,String userName){

        if (!sheets.containsKey(sheetName)){
            throw new IllegalStateException("No sheet with the name " + sheetName + " is currently loaded.");
        }
        try {
            Sheet sheet = sheets.get(sheetName);
            Sheet sheetCopy = sheet.copySheet();
            sheetCopy = sheetCopy.setCell(cellId, cellValue,userName);
            SheetDTO newSheet = sheetToDTO(sheetCopy);
            return newSheet;
        }
        catch (Exception e) {
            throw new IllegalArgumentException("Cell "+ cellId + " can't be updated with the value " + cellValue + " because "
                    + e.getMessage() + "\n" );
        }
    }

    public void requestAccessPermission(String sheetName, String userName, String requestedAccessPermission) {
        if (!sheets.containsKey(sheetName)) {
            throw new IllegalArgumentException("Sheet with the name " + sheetName + " does not exist.");
        }
        sheets.get(sheetName).requestAccessPermission(userName, requestedAccessPermission);
    }

    @Override
    public void approveAccessPermission(String owner, String sheetName, String userName, String requestedAccessPermission) {
        if (!sheets.containsKey(sheetName)) {
            throw new IllegalArgumentException("Sheet with the name " + sheetName + " does not exist.");
        }
        sheets.get(sheetName).approveAccessPermission(owner, userName, requestedAccessPermission);
    }

    @Override
    public void rejectAccessPermission(String owner, String sheetName, String userName, String requestedAccessPermission) {
        if (!sheets.containsKey(sheetName)) {
            throw new IllegalArgumentException("Sheet with the name " + sheetName + " does not exist.");
        }
        sheets.get(sheetName).rejectAccessPermission(owner, userName, requestedAccessPermission);

    }

    @Override
    public List<UserAccessDTO> getAllAccessRequests(String sheetName, String ownerUserName) {
        if (!sheets.containsKey(sheetName)) {
            throw new IllegalArgumentException("Sheet with the name " + sheetName + " does not exist.");
        }
        return sheets.get(sheetName).getAllAccessRequests(ownerUserName);
    }

    @Override
    public SheetDTO getSheetLatestVersion(String username, String sheetName) {
        if (!sheets.containsKey(sheetName)) {
            throw new IllegalArgumentException("Sheet with the name " + sheetName + " does not exist.");
        }
        Sheet sheet = sheets.get(sheetName);
//        sheet.checkUserAccess(username, AccessPermissionType.READER);
        return sheetToDTO(sheet);
    }

}
