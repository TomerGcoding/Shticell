package engine.sheet.impl;
import engine.sheet.coordinate.Coordinate;

public class SheetProperties {
    private final int numCols;
    private final int numRows;
    private final int colWidth;
    private final int rowHeight;

    public SheetProperties(int numRows, int numCols, int rowHeight, int colWidth) {
        this.numRows = numRows;
        this.numCols = numCols;
        this.colWidth = colWidth;
        this.rowHeight = rowHeight;
    }

    public int getNumCols() {
        return numCols;
    }

    public int getNumRows() {
        return numRows;
    }

    public int getColWidth() {
        return colWidth;
    }

    public int getRowHeight() {
        return rowHeight;
    }

    public boolean isCoordinateLegal(Coordinate coordinate) {
        int row = coordinate.getRow();
        int column = coordinate.getColumn();
        return isCoordinateLegal(row, column);
    }
    public boolean isCoordinateLegal(int row, int column) {
        return row >= 0 && row < numRows && column >= 0 && column < numCols;
    }

}
