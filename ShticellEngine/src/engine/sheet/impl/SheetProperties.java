package engine.sheet.impl;
import engine.sheet.coordinate.Coordinate;

public class SheetProperties {
    private final int numCols;
    private final int numRows;
    private final int colWidth;
    private final int rowHeight;

    public SheetProperties(int numCols, int numRows, int colWidth, int rowHeight) {
        this.numCols = numCols;
        this.numRows = numRows;
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

        return row >= 0 && row < numRows && column >= 0 && column < numCols;
    }
}
