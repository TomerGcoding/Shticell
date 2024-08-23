package engine.sheet.coordinate;

import java.io.Serializable;
import java.util.Objects;

public class CoordinateImpl implements Coordinate, Serializable {
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
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CoordinateImpl that = (CoordinateImpl) o;
        return row == that.row && column == that.column;
    }

    @Override
    public int hashCode() {
        int result = 17; // Start with a non-zero constant prime number
        result = 31 * result + row; // Multiply by 31 and add the first field
        result = 31 * result + column; // Multiply by 31 and add the second field
        return result;
    }

    @Override
    public int getRow() {
        return row;
    }

    @Override
    public int getColumn() {
        return column;
    }

}
