package engine.sheet.impl;

import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.cell.impl.CellImpl;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;
import engine.sheet.coordinate.CoordinateFormatter;

import java.util.HashMap;
import java.util.Map;

public class SheetImpl implements Sheet {

    private Map<Coordinate, Cell> activeCells;
    private static int currVersion = 0;
    private String sheetName;
    private SheetProperties properties;

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
    public SheetProperties getProperties()
    {
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

    public SheetProperties getSheetProperties(){
        return properties;
    }


    @Override
    public Cell getCell(int row, int column) {
        return activeCells.get(CoordinateFactory.createCoordinate(row, column));
    }

    @Override
    public void setCell(int row, int column, String value) {
        Coordinate coordinate = CoordinateFactory.createCoordinate(row, column);
        updateCell(coordinate, value);
    }
    @Override
    public void setCell(String cellId, String value) {
        int[] idx = CoordinateFormatter.cellIdToIndex(cellId);
        Coordinate coordinate = CoordinateFactory.createCoordinate(idx[0], idx[1]);
        if (!properties.isCoordinateLegal(coordinate))
            throw (new IllegalArgumentException("Invalid coordinate"));
        updateCell(coordinate, value);
    }

    private void updateCell(Coordinate coordinate, String value) {
        Cell cell = activeCells.get(coordinate);
        if (cell == null) {
            cell = new CellImpl(coordinate, value, currVersion);
            activeCells.put(coordinate, cell);
        }//only if "try" went good

        cell.setCellOriginalValue(value);
        cell.calculateEffectiveValue();
    }


}
