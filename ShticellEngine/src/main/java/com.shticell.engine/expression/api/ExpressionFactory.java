package com.shticell.engine.expression.api;

@FunctionalInterface
public interface ExpressionFactory {
    Expression create(Expression... arguments);
}