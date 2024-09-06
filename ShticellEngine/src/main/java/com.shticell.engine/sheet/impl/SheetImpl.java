package com.shticell.engine.sheet.impl;

import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.cell.impl.CellImpl;
import com.shticell.engine.sheet.coordinate.Coordinate;
import com.shticell.engine.sheet.coordinate.CoordinateFactory;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;

import java.io.*;
import java.util.*;
import java.util.stream.Collectors;

public class SheetImpl implements Sheet, Serializable {

    private final Map<Coordinate, Cell> activeCells;
    private static int currVersion = 0;
    private final String sheetName;
    private final SheetProperties properties;

    public SheetImpl(String sheetName, int rows, int columns, int rowHeight, int columnWidth) {
        this.activeCells = new HashMap<>();
        this.sheetName = sheetName;
        this.properties = new SheetProperties(rows, columns, rowHeight, columnWidth);
        currVersion = 0;
    }

    public SheetImpl() {
        this.activeCells = new HashMap<>();
        this.sheetName = "testtttt";
        this.properties = new SheetProperties(10, 10, 1, 5);
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
    public Sheet setCell(int row, int column, String value) {
        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        if (!properties.isCoordinateLegal(coordinate)) {
            throw new IllegalArgumentException("Invalid coordinate");
        }
            return updateCellValueAndCalculate(row, column, value);
    }

    @Override
    public Sheet setCell(String cellId, String value) {
        int[] idx = CoordinateFormatter.cellIdToIndex(cellId);
        return updateCellValueAndCalculate(idx[0], idx[1], value);
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
    public Sheet updateCellValueAndCalculate(int row, int column, String value) {
        Cell originCell = getCell(row, column);

        if (originCell != null) {
            if (value.trim().equals(originCell.getOriginalValue().trim()))
                return this;
            originCell.deleteMeFromInfluenceList();
        }

        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);

        SheetImpl newSheetVersion = copySheet();
        Cell newCell = new CellImpl(row, column, value, newSheetVersion.getVersion() + 1, newSheetVersion);
        newCell.calculateEffectiveValue();
        newSheetVersion.activeCells.put(coordinate, newCell);

        List<Cell> orderedCells = newSheetVersion.orderCellsForCalculation();

        List<Cell> cellsThatHaveChanged = orderedCells.stream()
                .filter(Cell::calculateEffectiveValue) // Calculate the effective value in topological order
                .collect(Collectors.toList());
        cellsThatHaveChanged.add(newCell);

        int newVersion = newSheetVersion.increaseVersion();
        cellsThatHaveChanged.forEach(cell -> cell.setVersion(newVersion));
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



    private SheetImpl copySheet() {
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

}
