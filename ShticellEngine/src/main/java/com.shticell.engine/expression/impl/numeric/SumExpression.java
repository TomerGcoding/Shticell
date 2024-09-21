package com.shticell.engine.expression.impl.numeric;

import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.EffectiveValueImpl;
import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.range.Range;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.cell.impl.CellType;

import java.util.List;

public class SumExpression implements Expression {
    private final String rangeName;
    private final Sheet sheet;

    public SumExpression(String rangeName, Sheet sheet) {
        this.rangeName = rangeName;
        this.sheet = sheet;
    }

    @Override
    public EffectiveValue eval() {
        Range range = sheet.getRange(rangeName);
        List<EffectiveValue> values = range.getRangeValues(sheet);
        double sum = values.stream()
                .filter(value -> value.getCellType() == CellType.NUMERIC)
                .mapToDouble(value -> (Double) value.getValue())
                .sum();
        return new EffectiveValueImpl(CellType.NUMERIC, sum);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }
    @Override
    public boolean isDepndsOnSomeCell() {
        return true;
    }

    public String getRangeName() {
        return rangeName;
    }

    public Range getRange() {
        return sheet != null ? sheet.getRange(rangeName) : null;
    }
}

