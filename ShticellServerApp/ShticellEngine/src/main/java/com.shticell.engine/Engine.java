package com.shticell.engine;

import com.shticell.engine.users.UserManager;
import com.shticell.engine.users.accessPermission.SheetUserAccessManager;
import dto.RangeDTO;
import dto.SheetDTO;
import dto.SheetUsersAccessDTO;
import dto.UserAccessDTO;
import jakarta.xml.bind.JAXBException;

import java.util.List;
import java.util.Map;

public interface Engine {

    SheetDTO loadSheetFile(String filePath, String userName) throws JAXBException;

    SheetDTO showSheet(String sheetName, String userName);

    void setCell(String sheetName, String cellId, String cellValue, String userName);

    Map<Integer,Integer> showVersionTable(String sheetName, String userName);

    SheetDTO showChosenVersion(String sheetName, int chosenVersion, String userName);

    RangeDTO addRange(String sheetName, String name, String cellsRange, String userName);

    void removeRange(String sheetName, String name, String userName);

    SheetDTO sortSheet(String sheetName, String rangeToSort,String columnsToSortBy, String userName);

    SheetDTO filterSheet(String sheetName, String rangeToFilter,String columnsToFilterBy,List<String> valuesToFilterBy, String userName);

    Map <String, SheetDTO> getAllSheets(String userName);
   // Map<String,List<SheetDTO>>  getAllSheets(String userName);

    SheetUsersAccessDTO getSheetUsersAccess(String sheetName);
    
    void addUser(String userName);

    UserManager getUserManager();

    SheetDTO setCellForDynamicAnalysis(String sheetName, String cellId, String cellValue,String userName);

    void requestAccessPermission(String sheetName, String userName, String requestedAccessPermission);

    void approveAccessPermission(String owner, String sheetName, String userName, String requestedAccessPermission);

    void rejectAccessPermission(String owner, String sheetName, String userName, String requestedAccessPermission);

    List<UserAccessDTO> getAllAccessRequests(String sheetName, String ownerUserName);

    SheetDTO getSheetLatestVersion(String username, String sheetName);
}
