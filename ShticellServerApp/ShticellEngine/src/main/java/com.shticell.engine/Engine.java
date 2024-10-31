package com.shticell.engine;

import com.shticell.engine.users.UserManager;
import com.shticell.engine.users.accessPermission.SheetUserAccessManager;
import dto.RangeDTO;
import dto.SheetDTO;
import dto.SheetUsersAccessDTO;
import jakarta.xml.bind.JAXBException;

import java.util.List;
import java.util.Map;

public interface Engine {

    SheetDTO loadSheetFile(String filePath, String userName) throws JAXBException;

    SheetDTO showSheet(String sheetName);

    void setCell(String sheetName, String cellId, String cellValue);

    Map<Integer,Integer> showVersionTable(String sheetName);

    SheetDTO showChosenVersion(String sheetName, int chosenVersion);

    RangeDTO addRange(String sheetName, String name, String cellsRange);

    void removeRange(String sheetName, String name);

    SheetDTO sortSheet(String sheetName, String rangeToSort,String columnsToSortBy);

    SheetDTO filterSheet(String sheetName, String rangeToFilter,String columnsToFilterBy,List<String> valuesToFilterBy);

    Map<String,List<SheetDTO>>  getAllSheets(String userName);

    SheetUsersAccessDTO getSheetUsersAccess(String sheetName);
    
    void addUser(String userName);

    UserManager getUserManager();

    SheetDTO setCellForDynamicAnalysis(String sheetName, String cellId, String cellValue);
}
