package engine.sheet.utils;

import engine.expression.api.Expression;
import engine.expression.api.ExpressionFactory;
import engine.expression.impl.bool.BooleanExpression;
import engine.expression.impl.numeric.*;
import engine.expression.impl.ref.RefExpression;
import engine.expression.impl.string.ConcatExpression;
import engine.expression.impl.string.StringExpression;
import engine.expression.impl.string.SubExpression;
import engine.sheet.api.Sheet;

import java.util.HashMap;
import java.util.Map;
import java.util.List;
import java.util.ArrayList;

public class FunctionParser {

    private final Sheet sheet;  // Instance variable for Sheet

    // Non-static Map, initialized in the constructor
    private final Map<String, ExpressionFactory> functionFactories = new HashMap<>();

    public FunctionParser(Sheet sheet) {
        this.sheet = sheet;
        initializeFunctionFactories();
    }

    // Initialize the function factories within the constructor
    private void initializeFunctionFactories() {
        functionFactories.put("PLUS", (args) -> new PlusExpression(args[0], args[1]));
        functionFactories.put("MINUS", (args) -> new MinusExpression(args[0], args[1]));
        functionFactories.put("TIMES", (args) -> new TimesExpression(args[0], args[1]));
        functionFactories.put("DIVIDE", (args) -> new DivideExpression(args[0], args[1]));
        functionFactories.put("MOD", (args) -> new ModuloExpression(args[0], args[1]));
        functionFactories.put("POW", (args) -> new PowExpression(args[0], args[1]));
        functionFactories.put("ABS", (args) -> new AbsExpression(args[0]));
        functionFactories.put("REF", (args) -> new RefExpression(args[0].toString(), sheet));
        functionFactories.put("CONCAT", (args) -> new ConcatExpression(args[0], args[1]));
        functionFactories.put("SUB", (args) -> new SubExpression(args[0], args[1], args[2]));
    }

    public Expression parseFunction(String input) {
        input = input.trim();

        if (input.matches("-?\\d+(\\.\\d+)?")) {
            return new ConstantExpression(Double.parseDouble(input));
        }
        if (input.equals("TRUE") || input.equals("FALSE")) {
            return new BooleanExpression(input);
        }

        if (input.startsWith("{") && input.endsWith("}")) {
            input = input.substring(1, input.length() - 1).trim();
        } else {
            return new StringExpression(input);
        }

        List<String> parts = splitFunctionArguments(input);

        String functionName = parts.get(0).trim();
        ExpressionFactory factory = functionFactories.get(functionName);

        if (factory == null) {
            throw new IllegalArgumentException("Unknown function: " + functionName);
        }

        List<Expression> arguments = new ArrayList<>();
        for (int i = 1; i < parts.size(); i++) {
            arguments.add(parseFunction(parts.get(i).trim()));
        }

        return factory.create(arguments.toArray(new Expression[0]));
    }

    private List<String> splitFunctionArguments(String input) {
        List<String> parts = new ArrayList<>();
        int braceCount = 0;
        StringBuilder currentPart = new StringBuilder();

        for (char c : input.toCharArray()) {
            if (c == '{') {
                braceCount++;
            } else if (c == '}') {
                braceCount--;
            }

            if (c == ',' && braceCount == 0) {
                parts.add(currentPart.toString().trim());
                currentPart = new StringBuilder();
            } else {
                currentPart.append(c);
            }
        }

        if (currentPart.length() > 0) {
            parts.add(currentPart.toString().trim());
        }

        return parts;
    }
}
