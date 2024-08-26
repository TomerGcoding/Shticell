package engine.expression.impl.ref;

import engine.expression.api.Expression;
import engine.sheet.cell.api.Cell;
import engine.sheet.api.Sheet;
import engine.sheet.cell.impl.CellType;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.coordinate.Coordinate;

public class RefExpression implements Expression {

    private final String refCellId;
    private final Sheet sheet;

    public RefExpression(String cellId, Sheet sheet) {
        this.refCellId = cellId;
        this.sheet = sheet;
    }

    @Override
    public EffectiveValue eval() {
        Coordinate refCoordinate = sheet.getCoordinateFromCellId(refCellId);
            if (refCoordinate == null)
                throw new IllegalArgumentException("Referenced cell does not exist: " + refCellId);
        Cell referencedCell = sheet.getCell(refCoordinate);
//        Cell thisCell = sheet.getCell(refCellId);
//        thisCell.addDependency(referencedCell);
       // referencedCell.addInfluence(thisCell);

//        if (referencedCell == null) {
//            throw new IllegalArgumentException("Referenced cell does not exist: " + cellId);
//        }

        return referencedCell.getEffectiveValue();  // Return the effective value of the referenced cell
    }

    @Override
    public CellType getFunctionResultType ()
    {
        Coordinate coordinate = sheet.getCoordinateFromCellId(refCellId);  // Get the coordinate from the cell ID
        Cell referencedCell = sheet.getCell(coordinate);
        return referencedCell.getEffectiveValue().getCellType();
    }

    public String getRefCellId () { return refCellId; }

}
