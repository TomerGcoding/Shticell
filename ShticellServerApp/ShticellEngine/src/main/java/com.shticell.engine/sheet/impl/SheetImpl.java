package com.shticell.engine.sheet.impl;

import com.shticell.engine.users.accessPermission.AccessPermissionType;
import com.shticell.engine.users.accessPermission.SheetUserAccessManager;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.range.Range;
import com.shticell.engine.range.RangeImpl;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.impl.CellImpl;
import com.shticell.engine.sheet.coordinate.Coordinate;
import com.shticell.engine.sheet.coordinate.CoordinateFactory;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;
import com.shticell.engine.users.accessPermission.UserAccessPermission;
import dto.UserAccessDTO;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SheetImpl implements Sheet, Serializable {

    private final Map<Coordinate, Cell> activeCells;
    private final  Map <String, Range> activeRanges;
    private static int currVersion = 0;
    private final String sheetName;
    private final SheetProperties properties;
    private SheetUserAccessManager userAccessManager;

    public SheetImpl(String sheetName, int rows, int columns, int rowHeight, int columnWidth) {
        this.activeCells = new HashMap<>();
        this.activeRanges = new HashMap<>();
        this.sheetName = sheetName;
        this.properties = new SheetProperties(rows, columns, rowHeight, columnWidth);
        this.userAccessManager = new SheetUserAccessManager();
        currVersion = 0;
    }


    @Override
    public Map<Coordinate, Cell> getCells() {
        return activeCells;
    }

    @Override
    public SheetProperties getProperties() {
        return properties;
    }

    @Override
    public String getSheetName() {
        return sheetName;
    }

    @Override
    public int getVersion() {
        return currVersion;
    }

    @Override
    public Cell getCell(int row, int column) {
        if (!properties.isCoordinateLegal(row, column)) {
            throw new IllegalArgumentException("Invalid coordinate");
        }
        return getCell(CoordinateFactory.createCoordinate(row, column));
    }

    @Override
    public Cell getCell(String cellId) {
        Coordinate coordinate = getCoordinateFromCellId(cellId);
        if (coordinate == null) {
            throw new IllegalArgumentException("Invalid coordinate");
        }
        return getCell(coordinate);
    }

    @Override
    public Sheet setCell(int row, int column, String value,String userNameToUpdate) {
        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        if (!properties.isCoordinateLegal(coordinate)) {
            throw new IllegalArgumentException("Invalid coordinate");
        }
        SheetImpl newSheet = updateCellValueAndCalculate(row, column, value,userNameToUpdate);
        newSheet.updateRangesLists(CoordinateFormatter.indexToCellId(row, column),value);
        return newSheet;
    }

    @Override
    public Sheet setCell(String cellId, String value,String userNameToUpdate) {
        int[] idx = CoordinateFormatter.cellIdToIndex(cellId);
        SheetImpl newSheet = updateCellValueAndCalculate(idx[0], idx[1], value,userNameToUpdate);
        newSheet.updateRangesLists(cellId, value);
        return newSheet;
    }

    private void updateRangesLists(String cellId, String value) {
        for (Range range : activeRanges.values()) {
            if (range.getInfluenceOnCells().contains(cellId))
                range.removeInfluence(cellId);
        }
    }


    @Override
    public void incrementVersion() {
        currVersion += 1;
    }

    @Override
    public Coordinate getCoordinateFromCellId(String cellId) {
        int[] idx = CoordinateFormatter.cellIdToIndex(cellId);
        return properties.isCoordinateLegal(idx[0], idx[1]) ? CoordinateFactory.createCoordinate(idx[0], idx[1]) : null;
    }

    @Override
    public Cell getCell(Coordinate coordinate) {
        return activeCells.get(coordinate);
    }

    @Override
    public SheetImpl updateCellValueAndCalculate(int row, int column, String value,String userNameToUpdate) {
        Cell originCell = getCell(row, column);

        if (originCell != null) {
            if (value.trim().equals(originCell.getOriginalValue().trim()))
                return this;
            originCell.deleteMeFromInfluenceList();
        }

        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);

        SheetImpl newSheetVersion = copySheet();
        Cell newCell = new CellImpl(row, column, value, newSheetVersion.getVersion() + 1, newSheetVersion,userNameToUpdate);
        newCell.calculateEffectiveValue();
        newSheetVersion.activeCells.put(coordinate, newCell);

        List<Cell> orderedCells = newSheetVersion.orderCellsForCalculation();

        List<Cell> cellsThatHaveChanged = orderedCells.stream()
                .filter(Cell::calculateEffectiveValue) // Calculate the effective value in topological order
                .collect(Collectors.toList());
        cellsThatHaveChanged.add(newCell);
        //System.out.println("");
        int newVersion = newSheetVersion.increaseVersion();
        cellsThatHaveChanged.forEach(cell -> {cell.setVersion(newVersion);
        cell.setUserNameToUpdate(userNameToUpdate);});
        return newSheetVersion;
    }

    public int increaseVersion() {
        return currVersion + 1;
    }

    private List<Cell> orderCellsForCalculation() {
        Map<Cell, Set<Cell>> adjList = new HashMap<>();
        Map<Cell, Integer> inDegree = new HashMap<>();

        for (Cell cell : activeCells.values()) {
            adjList.put(cell, new HashSet<>());
            inDegree.put(cell, 0);
        }

        for (Cell cell : activeCells.values()) {
            for (Cell neighbor : cell.getDependsOn()) {
                adjList.get(neighbor).add(cell);
                inDegree.put(cell, inDegree.get(cell) + 1);
            }
        }

        List<Cell> sortedCells = new ArrayList<>();
        Queue<Cell> queue = new LinkedList<>();

        for (Map.Entry<Cell, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            sortedCells.add(current);

            for (Cell neighbor : adjList.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        if (sortedCells.size() != inDegree.size()) {
            throw new IllegalStateException("circular dependency detected among cells.");
        }
        return sortedCells;
    }

    @Override
    public Range addRange(String name, String cellsRange) throws IllegalArgumentException {
        if (activeRanges.containsKey(name)) {
            throw new IllegalArgumentException("Invalid name: A range with the name '" + name + "' already exists.");
        }

        String[] rangeParts = cellsRange.split("\\.\\.");
        if (rangeParts.length != 2) {
            throw new IllegalArgumentException("Invalid range format: " + cellsRange);
        }
        try {
            Range range = new RangeImpl(name, rangeParts[0], rangeParts[1], this);
            activeRanges.put(name, range);
            return range;
        }
        catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Invalid coordinates: " + cellsRange);
        }
    }

    public void addCell(Coordinate coordinate, Cell cell) {
        activeCells.put(coordinate, cell);
    }

    @Override
    public SheetImpl copySheet() {
        try {
            ByteArrayOutputStream baos = new ByteArrayOutputStream();
            ObjectOutputStream oos = new ObjectOutputStream(baos);
            oos.writeObject(this);
            oos.close();

            ObjectInputStream ois = new ObjectInputStream(new ByteArrayInputStream(baos.toByteArray()));
            return (SheetImpl) ois.readObject();
        } catch (Exception e) {
            return null;
        }
    }

    @Override
    public List<EffectiveValue> getRangeValues(String rangeName) {
        if (!activeRanges.containsKey(rangeName)) {
            throw new IllegalArgumentException("Invalid range name: " + rangeName);
        }
        return activeRanges.get(rangeName).getRangeValues(this);
    }

    @Override
    public Range getRange(String rangeName) {
        return activeRanges.get(rangeName);
    }

    @Override
    public void removeRange(String name) {
        Range rangeToDelete = activeRanges.get(name);
        if (rangeToDelete == null) {
            throw new IllegalArgumentException("Invalid range name: " + name);
        }
        if (!rangeToDelete.getInfluenceOnCells().isEmpty()) {
            throw new IllegalArgumentException("Range is in use and cannot be deleted.");
        }
        else
            activeRanges.remove(name);
    }

    @Override
    public Map<String, Range> getRanges() {
        return activeRanges;
    }

    @Override
    public List<String> getUniqeColumnValues(String columnId) {
        List <String> values = new ArrayList<>();
        int column = CoordinateFormatter.cellIdToIndex(columnId + "1")[1];
        for (Cell cell : activeCells.values()) {
            if (cell.getColumn() == column && !values.contains(cell.getEffectiveValue().getValue().toString())) {
                values.add(cell.getEffectiveValue().getValue().toString());
            }
        }
        System.out.println(values);
        return values;
    }

    @Override
    public SheetUserAccessManager getSheetUserAccessManager() {
        return userAccessManager;
    }

    @Override
    public void setSheetOwner(String userName){
        userAccessManager.setOwner(userName);
    }

    @Override
    public void checkUserAccess(String userName, AccessPermissionType requiredAccessPermission) {
        UserAccessPermission accessPermission = userAccessManager.getUserAccessPermission(userName);
        switch (requiredAccessPermission) {
            case OWNER:
                if (accessPermission == null || accessPermission.getAccessPermissionType() != AccessPermissionType.OWNER) {
                    throw new IllegalArgumentException("User does not have owner access permission.");
                }
                break;
            case WRITER:
                if (accessPermission == null || (accessPermission.getAccessPermissionType() != AccessPermissionType.WRITER && accessPermission.getAccessPermissionType() != AccessPermissionType.OWNER)) {
                    throw new IllegalArgumentException("User does not have writer access permission.");
                }
                break;
            case READER:
                if (accessPermission == null || accessPermission.getAccessPermissionType() == AccessPermissionType.NONE) {
                    throw new IllegalArgumentException("User does not have viewer access permission.");
                }
                break;
        }

    }

    @Override
    public void requestAccessPermission(String userName, String requestedAccessPermission) {
        userAccessManager.requestAccessPermission(userName, requestedAccessPermission);
    }

    @Override
    public void approveAccessPermission(String owner, String userName, String requestedAccessPermission) {
        userAccessManager.approveAccessPermission(owner, userName, requestedAccessPermission);
    }

    @Override
    public void rejectAccessPermission(String owner, String userName, String requestedAccessPermission) {
        userAccessManager.rejectAccessPermission(owner, userName, requestedAccessPermission);
    }

    @Override
    public List<UserAccessDTO> getAllAccessRequests(String ownerUserName) {
        if (userAccessManager.isOwner(ownerUserName)) {
            return userAccessManager.getAllAccessRequests(ownerUserName);
        }
        else {
            throw new IllegalArgumentException("User does not have owner access permission.");
        }
    }
}
