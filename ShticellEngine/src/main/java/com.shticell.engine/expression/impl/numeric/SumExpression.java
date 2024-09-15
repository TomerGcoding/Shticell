package com.shticell.engine.expression.impl.numeric;

import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.EffectiveValueImpl;
import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.range.Range;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.cell.impl.CellType;

import java.util.ArrayList;
import java.util.List;

public class SumExpression extends NumericExpression {
    private final String rangeName;
    private final Sheet sheet;

    public SumExpression(String rangeName, Sheet sheet) {
        this.rangeName = rangeName;
        this.sheet = sheet;
    }

    @Override
    public EffectiveValue eval() {
        Range range = sheet.getRange(rangeName);
        if (range == null) {
            throw new IllegalArgumentException("Range not found: " + rangeName);
        }
        List<EffectiveValue> values = range.getRangeValues(sheet);
        double sum = values.stream()
                .filter(value -> value.getCellType() == CellType.NUMERIC)
                .mapToDouble(value -> (Double) value.getValue())
                .sum();
        return new EffectiveValueImpl(CellType.NUMERIC, sum);
    }


    @Override
    public List<Expression> getExpressions() {
        List<Expression> expressions = new ArrayList<>();
        expressions.add(this);
        return expressions;
    }

    @Override
    public boolean isDepndsOnSomeCell() {
        return true;
    }

   // public String getRangeName() {
    // return rangeName;
   // }

    public Range getRange() {
        return sheet != null ? sheet.getRange(rangeName) : null;
    }
}

