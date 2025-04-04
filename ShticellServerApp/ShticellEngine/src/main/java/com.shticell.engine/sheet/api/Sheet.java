package com.shticell.engine.sheet.api;

import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.range.Range;
import com.shticell.engine.sheet.coordinate.Coordinate;
import com.shticell.engine.sheet.impl.SheetImpl;
import com.shticell.engine.sheet.impl.SheetProperties;
import com.shticell.engine.users.accessPermission.AccessPermissionType;
import com.shticell.engine.users.accessPermission.SheetUserAccessManager;
import dto.UserAccessDTO;

import java.util.List;
import java.util.Map;

public interface Sheet {
    int getVersion();
    Cell getCell(int row, int column);
    Cell getCell(String cellId);
    Sheet setCell(int row, int column, String value, String userName);
    Sheet setCell(String id, String value, String userName);
    String getSheetName();
    Map<Coordinate, Cell> getCells();
    SheetProperties getProperties();
    Coordinate getCoordinateFromCellId(String cellId);
    Cell getCell(Coordinate coordinate);
    void incrementVersion();
    Sheet updateCellValueAndCalculate(int row, int column, String value,String userName);
    int increaseVersion();
    SheetImpl copySheet();
    Range addRange(String name, String cellsRange);
    void addCell(Coordinate coordinate, Cell cell);

    List<EffectiveValue> getRangeValues(String rangeName);

    Map<String,Range> getRanges();
    Range getRange(String rangeName);

    void removeRange(String name);

    List<String> getUniqeColumnValues(String columnId);

    SheetUserAccessManager getSheetUserAccessManager();

    void setSheetOwner(String userName);

    void checkUserAccess(String userName, AccessPermissionType requiredAccessPermission);

    void requestAccessPermission(String userName, String requestedAccessPermission);

    void approveAccessPermission(String owner, String userName, String requestedAccessPermission);

    void rejectAccessPermission(String owner, String userName, String requestedAccessPermission);

    List<UserAccessDTO> getAllAccessRequests(String ownerUserName);
}
