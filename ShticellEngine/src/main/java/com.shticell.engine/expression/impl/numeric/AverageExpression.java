package com.shticell.engine.expression.impl.numeric;

import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.cell.impl.EffectiveValueImpl;
import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.range.Range;
import com.shticell.engine.sheet.api.Sheet;

import java.util.List;

public class AverageExpression implements Expression {
    private final String rangeName;
    private final Sheet sheet;

    public AverageExpression(String rangeName, Sheet sheet) {
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
        long count = values.stream()
                .filter(value -> value.getCellType() == CellType.NUMERIC)
                .count();
        double average = count == 0 ? 0 : sum / count;
        return new EffectiveValueImpl(CellType.NUMERIC, average);
    }

    @Override
    public CellType getFunctionResultType() {
        return CellType.NUMERIC;
    }

    @Override
    public boolean isDepndsOnSomeCell() {
        return true;
    }

    public Range getRange(){
        return sheet != null ? sheet.getRange(rangeName) : null;
    }
}
