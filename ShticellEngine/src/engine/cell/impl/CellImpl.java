package engine.cell.impl;

import engine.cell.api.EffectiveValue;
import engine.expression.api.Expression;
import engine.expression.impl.IdentityExpression;
import engine.expression.impl.numeric.NumericExpression;
import engine.expression.impl.ref.RefExpression;
import engine.expression.impl.string.StringExpression;
import engine.sheet.api.Sheet;
import engine.sheet.coordinate.Coordinate;
import engine.cell.api.Cell;
import engine.expression.parser.FunctionParser;
import engine.sheet.coordinate.CoordinateFactory;
import engine.sheet.coordinate.CoordinateFormatter;
//import engine.sheet.utils.FunctionParser;

import java.io.Serializable;
import java.util.*;

public class CellImpl implements Cell, Serializable {
    private final Sheet sheet;
    private final String ID;
    private final Coordinate coordinate;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int version;
    private final Set<Cell> dependsOn;
    private final Set<Cell> influencingOn;

//    public CellImpl(String cellId, Coordinate coordinate, String originalValue, int version) {
//        this.ID = cellId;
//        this.coordinate = coordinate;
//        this.originalValue = originalValue;
//        this.effectiveValue = null;
//        this.version = version;
//        this.dependsOn = null;
//        this.influencingOn = null;
//    }

    public CellImpl(int row, int column, String originalValue, int version, Sheet sheet) {
        this.sheet = sheet;
        this.coordinate = CoordinateFactory.createCoordinate(row, column);
        this.ID = CoordinateFormatter.indexToCellId(row, column);
        this.originalValue = originalValue;
        this.version = version;
        this.dependsOn = new HashSet<>();
        this.influencingOn = new HashSet<>();
    }

    @Override
    public String getId() {
        return ID;
    }

    @Override
    public Coordinate getCoordinate() {
        return coordinate;
    }

    @Override
    public String getOriginalValue() {
        return originalValue;
    }


    @Override
    public void setCellOriginalValue(String value) {
        this.originalValue = value;
    }

    @Override
    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }

//    @Override
//    public void calculateEffectiveValue(Sheet sheet) {
//        // build the expression object out of the original value...
//        // it can be {PLUS, 4, 5} OR {CONCAT, hello, world}
//        Expression expression = FunctionParser.parseExpression(originalValue, sheet);
//
//        // second question: what is the return type of eval() ?
//        effectiveValue = expression.eval();
//
//    }

    @Override
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    @Override
    public Set<Cell> getDependsOn() {
        return dependsOn;
    }

    @Override
    public Set<Cell> getInfluencingOn() {
        return influencingOn;
    }

    @Override
    public void deleteCell() {
        deleteMeFromInfluenceList();
        originalValue = null;
        effectiveValue = null;
        influencingOn.clear();
        dependsOn.clear();
    }

    public void deleteDependency(Cell deleteMe) {
        if (dependsOn != null)
            dependsOn.remove(deleteMe);
    }

    @Override
    public boolean calculateEffectiveValue() {

        Expression expression = FunctionParser.parseExpression(originalValue, sheet);

        if(!(expression instanceof IdentityExpression)) {
            collectDependenciesAndInfluences(expression);
        }
        EffectiveValue newEffectiveValue = expression.eval();

        if (newEffectiveValue.equals(effectiveValue)) {
            return false;
        }
        else {
            effectiveValue = newEffectiveValue;
            return true;
        }
    }

    private void collectDependenciesAndInfluences(Expression expression) {
        if (expression instanceof RefExpression) {
            // If the expression is a RefExpression, process it directly
            String refCellId = ((RefExpression) expression).getRefCellId();

            Cell refCell = sheet.getCell(refCellId);
            if (refCell != null) {
                // Add the current cell to the refCell's influence list
                if (!refCell.getInfluencingOn().contains(this)) {
                    refCell.addInfluence(this);
                    addDependency((refCell));

                    // Add the refCell to the current cell's dependency list
                    // if(!this.getDependsOn().contains(refCell)){
                    this.addDependency(refCell);
                }
            }
        }
        else {
            if(expression instanceof NumericExpression) {
                List<Expression> expressions = ((NumericExpression) expression).getExpressions();
                for(Expression e : expressions) {
                    collectDependenciesAndInfluences(e);
                }
            }
            if(expression instanceof StringExpression) {
                List<Expression> expressions = ((StringExpression) expression).getExpressions();
                for(Expression e : expressions) {
                    collectDependenciesAndInfluences(e);
                }
            }
        }
    }

    @Override
    public void addDependency(Cell cell) {
        if (cell != null && !cell.equals(this) && !isCellInList(cell, dependsOn)) {
            dependsOn.add(cell);
        }
    }

    @Override
    public void addInfluence(Cell cell) {
        if (cell != null && !cell.equals(this) && !isCellInList(cell, influencingOn))
            influencingOn.add(cell);
    }

    private boolean isCellInList(Cell cell, Set<Cell> cellSet ) {
        for (Cell dependentCell : cellSet) {
            if (dependentCell.getId().equals(cell.getId())) {
                return true;  // Cell with the same ID is already in the list
            }
        }
        return false;  // No cell with the same ID was found
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        CellImpl cell = (CellImpl) o;
        return Objects.equals(ID, cell.ID);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(ID);
    }

    @Override
    public void removeFromInfluenceOn(Cell originCell) {
        influencingOn.remove(originCell);
    }

    @Override
    public void deleteMeFromInfluenceList() {
        for (Cell cell : dependsOn)
            cell.removeFromInfluenceOn(this);
    }
}

