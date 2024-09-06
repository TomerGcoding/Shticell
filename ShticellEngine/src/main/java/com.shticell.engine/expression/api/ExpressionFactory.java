package com.shticell.engine.expression.api;

import com.shticell.engine.expression.api.Expression;

@FunctionalInterface
public interface ExpressionFactory {
    Expression create(Expression... arguments);
}