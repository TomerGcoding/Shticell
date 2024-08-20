package engine.expression.api;

import engine.expression.api.Expression;

@FunctionalInterface
public interface ExpressionFactory {
    Expression create(Expression... arguments);
}