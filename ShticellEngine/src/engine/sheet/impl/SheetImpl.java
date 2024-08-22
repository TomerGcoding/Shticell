package engine.sheet.impl;

import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.cell.impl.CellImpl;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import engine.sheet.coordinate.CoordinateFormatter;
//import engine.sheet.utils.FunctionParser;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.util.*;
import java.util.stream.Collectors;
import java.io.*;

//import static java.util.stream.Nodes.collect;

public class SheetImpl implements Sheet {

    private final Map<Coordinate, Cell> activeCells;
    private static int currVersion = 1;
    private final String sheetName;
    private final SheetProperties properties;

    public SheetImpl(String sheetName,int rows, int columns,int rowHeight, int columnWidth) {
        this.activeCells = new HashMap<>();
        this.sheetName = sheetName;
        properties = new SheetProperties(rows, columns, rowHeight, columnWidth);
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
    public SheetProperties getSheetProperties() {
        return properties;
    }

    @Override
    public Cell getCell(int row, int column) {
        if (!properties.isCoordinateLegal(row, column))
            throw new IllegalArgumentException("Invalid coordinate");
        return getCell(CoordinateFactory.createCoordinate(row, column));
    }
    @Override
    public Cell getCell(String cellId)
    {
        Coordinate coordinate = getCoordinateFromCellId(cellId);
        if (coordinate == null)
            throw new IllegalArgumentException("Invalid coordinate");
        return getCell(coordinate);
    }

    @Override
    public void setCell(int row, int column, String value) {
        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        if (!properties.isCoordinateLegal(coordinate))
            throw (new IllegalArgumentException("Invalid coordinate"));
        updateCell(CoordinateFormatter.indexToCellId(row, column), coordinate, value);
    }

    @Override
    public void setCell(String cellId, String value) {
        // currVersion++;
        int[] idx = CoordinateFormatter.cellIdToIndex(cellId);
        Coordinate coordinate = CoordinateFactory.createCoordinate(idx[0], idx[1]);
        if (!properties.isCoordinateLegal(coordinate))
            throw (new IllegalArgumentException("Invalid coordinate"));
        updateCell(cellId, coordinate, value);

    }

    private void updateCell(String cellId, Coordinate coordinate, String value) {
        Cell cell = activeCells.get(coordinate);
        if (cell == null) {
            cell = new CellImpl(cellId, coordinate, value, currVersion);
        }
        else {
            cell.setVersion(currVersion);
        }
        cell.setCellOriginalValue(value);
        try {
            cell.calculateEffectiveValue();
        }
        catch (Exception e) {
            throw new IllegalArgumentException("SheetImpl threw this exception after trying to update cell");
        }
        activeCells.put(coordinate,cell);
    }

    @Override
    public void incrementVersion() {
        currVersion += 1;
    }

    // Method to get Coordinate from cell ID (like "12B")
    @Override
    public Coordinate getCoordinateFromCellId(String cellId) {
        int[] idx = CoordinateFormatter.cellIdToIndex(cellId);
        return properties.isCoordinateLegal(idx[0], idx[1])? CoordinateFactory.createCoordinate(idx[0], idx[1]): null;
    }

    // Method to retrieve a Cell by Coordinate
    @Override
    public Cell getCell(Coordinate coordinate) {
        return activeCells.get(coordinate);
    }

    @Override
    public void deleteCell(String cellId) {
        Cell cell = getCell(cellId);
        cell.deleteCell();
        activeCells.remove(getCoordinateFromCellId(cellId));
    }
    @Override
    public Sheet updateCellValueAndCalculate(int row, int column, String value) {

        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);

        SheetImpl newSheetVersion = copySheet();
        Cell newCell = new CellImpl(row, column, value, newSheetVersion.getVersion() + 1, newSheetVersion);
        newSheetVersion.activeCells.put(coordinate, newCell);

        try {
            newSheetVersion
                    .orderCellsForCalculation()
                    .stream()
                    .filter(Cell::calculateEffectiveValue)
                    .collect(Collectors.toList());



            // successful calculation. update sheet and relevant cells version
            // int newVersion = newSheetVersion.increaseVersion();
            // cellsThatHaveChanged.forEach(cell -> cell.updateVersion(newVersion));

            return newSheetVersion;
        } catch (Exception e) {
            return null;
        }
    }

    private List<Cell> orderCellsForCalculation() {
        // Step 1: Build the graph
        Map<Cell, List<Cell>> adjList = new HashMap<>();
        Map<Cell, Integer> inDegree = new HashMap<>();

        // Initialize the graph
        for (Cell cell : activeCells.values()) {
            adjList.put(cell, new ArrayList<>());
            inDegree.put(cell, 0);
        }

        // Populate the graph with dependencies (edges)
        for (Cell cell : activeCells.values()) {
            for (Cell dependent : cell.getDependsOn()) {
                adjList.get(dependent).add(cell); // dependent -> cell (an edge)
                inDegree.put(cell, inDegree.get(cell) + 1); // increase in-degree of cell
            }
        }

        // Step 2: Perform topological sort using Kahn's algorithm
        List<Cell> sortedCells = new ArrayList<>();
        Queue<Cell> queue = new LinkedList<>();

        // Start with cells that have no dependencies (in-degree 0)
        for (Map.Entry<Cell, Integer> entry : inDegree.entrySet()) {
            if (entry.getValue() == 0) {
                queue.add(entry.getKey());
            }
        }

        while (!queue.isEmpty()) {
            Cell current = queue.poll();
            sortedCells.add(current);

            // Reduce the in-degree of all neighbors
            for (Cell neighbor : adjList.get(current)) {
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

        // If there are still cells with a non-zero in-degree, a cycle exists
        if (sortedCells.size() != activeCells.size()) {
            throw new IllegalStateException("Circular dependency detected among cells.");
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
            //////
            return null;
        }

    }

}
