package com.shticell.engine.expression.impl;

import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.cell.impl.CellType;
import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.cell.impl.EffectiveValueImpl;

public class IdentityExpression implements Expression {

    private final Object value;
    private final CellType type;

    public IdentityExpression(Object value, CellType type) {
        this.value = value;
        this.type = type;
    }

    @Override
    public EffectiveValue eval() {
        return new EffectiveValueImpl(type, value);
    }

    @Override
    public CellType getFunctionResultType() {return type;}

    @Override
    public boolean isDepndsOnSomeCell() {
        return false;
    }
}
