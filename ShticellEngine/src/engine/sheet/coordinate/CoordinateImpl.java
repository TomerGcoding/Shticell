package engine.sheet.coordinate;

public class CoordinateImpl implements Coordinate {
    private final int row;
    private final int column;

    public CoordinateImpl(int row, int column) {
        this.row = row;
        this.column = column;
    }
//    public CoordinateImpl(String cellId) {
//        this(CoordinateFormatter.cellIdToIndex(cellId));
//    }
//    public CoordinateImpl(int[] indexArray) {
//        this(indexArray[0], indexArray[1]);
//    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

}
