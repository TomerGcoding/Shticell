package engine.sheet.api;

import engine.cell.api.Cell;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.impl.SheetProperties;

import java.util.Map;

public interface Sheet {
    int getVersion();
    Cell getCell(int row, int column);
    Cell getCell(String cellId);
    Sheet setCell(int row, int column, String value);
    Sheet setCell(String id, String value);
    String getSheetName();
    Map<Coordinate, Cell> getCells();
    SheetProperties getProperties();
    Coordinate getCoordinateFromCellId(String cellId);
    Cell getCell(Coordinate coordinate);
    void incrementVersion();
    SheetProperties getSheetProperties();
    void deleteCell(String cellId);
    Sheet updateCellValueAndCalculate(int row, int column, String value);
    int increaseVersion();
}
