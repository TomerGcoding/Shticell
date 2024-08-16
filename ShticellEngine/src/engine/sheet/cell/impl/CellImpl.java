package engine.sheet.cell.impl;

import engine.expression.api.Expression;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.coordinate.CoordinateImpl;
import engine.expression.impl.string.UpperCaseExpression;
import engine.sheet.api.EffectiveValue;
import engine.sheet.cell.api.Cell;
import engine.sheet.utils.FunctionParser;

import java.util.List;
import java.util.Map;

public class CellImpl implements Cell {

    private final Coordinate coordinate;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int version;
    private final List<Cell> dependsOn;
    private final List<Cell> influencingOn;

    public CellImpl(int row, int column, String originalValue,int version) {
        this.coordinate = new CoordinateImpl(row, column);
        this.originalValue = originalValue;
        this.effectiveValue = null;
        this.version = version;
        this.dependsOn = null;
        this.influencingOn = null;
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

    @Override
    public void calculateEffectiveValue() {
        // build the expression object out of the original value...
        // it can be {PLUS, 4, 5} OR {CONCAT, "hello", "world"}

        FunctionParser parser = new FunctionParser();
        // first question: what is the generic type of Expression ?
        Expression expression = parser.parseFunction(originalValue);

        // second question: what is the return type of eval() ?
        effectiveValue = expression.eval();
    }

    @Override
    public int getVersion() {
        return version;
    }

    @Override
    public List<Cell> getDependsOn() {
        return dependsOn;
    }

    @Override
    public List<Cell> getInfluencingOn() {
        return influencingOn;
    }

}
