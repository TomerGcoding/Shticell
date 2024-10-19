package dto;

public class CoordinateDTO {
    private int row;
    private int column;

    public CoordinateDTO() {
    }

    public CoordinateDTO(int row, int column) {
        this.row = row;
        this.column = column;
    }

    public int getRow() {
        return row;
    }

    public int getColumn() {
        return column;
    }

    @Override
    public String toString() {
        return row + "," + column;  // Format the coordinate as a string
    }
}
