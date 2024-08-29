package engine.sheet.impl;

import engine.sheet.api.Sheet;
import engine.cell.api.Cell;
import engine.cell.impl.CellImpl;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import engine.sheet.coordinate.CoordinateFormatter;

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
    public SheetProperties getSheetProperties() {
        return properties;
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
    public void deleteCell(String cellId) {
        Cell cell = getCell(cellId);
        if (cell != null)
            cell.deleteCell();
        activeCells.remove(getCoordinateFromCellId(cellId));
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
       // newCell.deleteEffectiveValue();

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

        // Initialize the graph
        for (Cell cell : activeCells.values()) {
            adjList.put(cell, new HashSet<>());
            inDegree.put(cell, 0);
        }

        // Populate the graph with dependencies (edges)
        for (Cell cell : activeCells.values()) {
         //   if (cell.getId().equals("B4"))
               // System.out.println("\nlet's look at B4 list: " +  cell.getInfluencingOn());
            //Set<Cell> neighbores = cell.getInfluencingOn();
            for (Cell neighbor : cell.getDependsOn()) {
                adjList.get(neighbor).add(cell);
                inDegree.put(cell, inDegree.get(cell) + 1);
            }
        }

//        // Debugging: Print the graph structure
//        System.out.println("Adjacency List:");
//        adjList.forEach((key, value) -> System.out.println(key.getId() + " -> " + value.stream().map(Cell::getId).collect(Collectors.joining(", "))));
//
//        System.out.println("In-Degree Map:");
//        inDegree.forEach((key, value) -> System.out.println(key.getId() + ": " + value));

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
            //System.out.println("current = " + current.getId());
            sortedCells.add(current);

            // Reduce the in-degree of all neighbors
          // System.out.println("neighbore: ");
            for (Cell neighbor : adjList.get(current)) {
              //  System.out.println(neighbor.getId());
                inDegree.put(neighbor, inDegree.get(neighbor) - 1);
                if (inDegree.get(neighbor) == 0) {
                    queue.add(neighbor);
                }
            }
        }

//        // If there are still cells with a non-zero in-degree, a cycle exists
        if (sortedCells.size() != inDegree.size()) {
//            System.out.println("Detected a cycle. Cells not sorted:");
//            for (Map.Entry<Cell, Integer> entry : inDegree.entrySet()) {
//                if (entry.getValue() > 0) {
//                    System.out.println(entry.getKey().getId() + "degree = " + entry.getValue());
//                }
//            }
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
            return null;
        }
    }

}
