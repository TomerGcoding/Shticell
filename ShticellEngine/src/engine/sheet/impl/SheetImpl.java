package engine.sheet.impl;

import engine.sheet.api.EffectiveValue;
import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.cell.impl.CellImpl;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateFactory;

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
        Cell cell = activeCells.get(coordinate);
        if (cell == null) {
            cell = new CellImpl(row,column,value,currVersion);
            cell.calculateEffectiveValue();
            activeCells.put(coordinate,cell);
        }
        // if null need to create the cell...
        else{
            cell.setCellOriginalValue(value);
            cell.calculateEffectiveValue();
        }

    }
}
