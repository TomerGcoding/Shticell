package engine.expression.impl.ref;

import engine.cell.impl.EffectiveValueImpl;
import engine.expression.api.Expression;
import engine.cell.api.Cell;
import engine.sheet.api.Sheet;
import engine.cell.impl.CellType;
import engine.cell.api.EffectiveValue;
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

        if(referencedCell == null) {
            return new EffectiveValueImpl(CellType.UNKNOWN, "");
        }

        return referencedCell.getEffectiveValue();  // Return the effective value of the referenced cell
    }


    @Override
    public CellType getFunctionResultType () { return CellType.UNKNOWN; }

    public String getRefCellId () { return refCellId; }

}
