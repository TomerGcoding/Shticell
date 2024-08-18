package engine.sheet.cell.impl;

import engine.expression.api.Expression;
import engine.sheet.api.Sheet;
import engine.sheet.coordinate.Coordinate;
import engine.sheet.cell.api.EffectiveValue;
import engine.sheet.cell.api.Cell;
import engine.sheet.utils.FunctionParser;

import java.util.List;

public class CellImpl implements Cell {

    private final Coordinate coordinate;
    private String originalValue;
    private EffectiveValue effectiveValue;
    private int version;
    private final List<Cell> dependsOn;
    private final List<Cell> influencingOn;

    public CellImpl(Coordinate coordinate, String originalValue,int version) {
        this.coordinate = coordinate;
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
    public void calculateEffectiveValue(Sheet sheet) {
        FunctionParser parser = new FunctionParser(sheet);
        // first question: what is the generic type of Expression ?
        Expression expression = parser.parseFunction(originalValue);
        try {
            effectiveValue = expression.eval();
        }
        catch (IllegalArgumentException e) {
            effectiveValue = null;
            throw e;
        }
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

//    void updateCell(String str)
//    {
//        setOriginalValue(str);
//        calculateEffectiveValue();
//
//
//    }

}
