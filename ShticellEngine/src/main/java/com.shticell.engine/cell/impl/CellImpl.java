package com.shticell.engine.cell.impl;

import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.expression.impl.IdentityExpression;
import com.shticell.engine.expression.impl.numeric.NumericExpression;
import com.shticell.engine.expression.impl.ref.RefExpression;
import com.shticell.engine.expression.impl.string.StringExpression;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.sheet.coordinate.Coordinate;
import com.shticell.engine.cell.api.Cell;
import com.shticell.engine.expression.parser.FunctionParser;
import com.shticell.engine.sheet.coordinate.CoordinateFactory;
import com.shticell.engine.sheet.coordinate.CoordinateFormatter;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
//import engine.sheet.utils.FunctionParser;

import java.io.Serializable;
import java.util.*;

public class CellImpl implements Cell, Serializable {
    private final Sheet sheet;
    private final String ID;
    private final Coordinate coordinate;
    private final String originalValue;
    private EffectiveValue effectiveValue;
    private int version;
    private final List<Cell> dependsOn;
    private final List<Cell> influencingOn;


    public CellImpl(int row, int column, String originalValue, int version, Sheet sheet) {
        this.sheet = sheet;
        this.coordinate = CoordinateFactory.createCoordinate(row, column);
        this.ID = CoordinateFormatter.indexToCellId(row, column);
        this.originalValue = originalValue;
        this.version = version;
        this.dependsOn = new ArrayList<>();
        this.influencingOn = new ArrayList<>();
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
    public EffectiveValue getEffectiveValue() {
        return effectiveValue;
    }


    @Override
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {this.version = version;}


    @Override
    public List<Cell> getDependsOn() {
        return dependsOn;
    }

    @Override
    public List<Cell> getInfluencingOn() {
        return influencingOn;
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
            String refCellId = ((RefExpression) expression).getRefCellId();

            Cell refCell = sheet.getCell(refCellId);
            if (refCell != null) {
                if (!refCell.getInfluencingOn().contains(this)) {
                    refCell.addInfluence(this);
                    addDependency((refCell));

                    // this.addDependency(refCell);
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

    private boolean isCellInList(Cell cell, List<Cell> cellSet ) {
        for (Cell dependentCell : cellSet) {
            if (dependentCell.getId().equals(cell.getId())) {
                return true;
            }
        }
        return false;
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
    public void removeFromInfluenceOn(Cell cell) {
        influencingOn.remove(cell);
    }

    @Override
    public void deleteMeFromInfluenceList() {
        for (Cell cell : dependsOn) {
            cell.removeFromInfluenceOn(this);
        }
    }
}

