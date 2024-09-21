package com.shticell.engine.expression.parser;

import com.shticell.engine.cell.api.EffectiveValue;
import com.shticell.engine.exceptions.IllegalNumberOfArgumentsException;
import com.shticell.engine.expression.api.Expression;
import com.shticell.engine.expression.impl.IdentityExpression;
import com.shticell.engine.expression.impl.bool.*;
import com.shticell.engine.expression.impl.numeric.*;
import com.shticell.engine.expression.impl.ref.RefExpression;
import com.shticell.engine.expression.impl.string.*;
import com.shticell.engine.sheet.api.Sheet;
import com.shticell.engine.cell.impl.CellType;


import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public enum FunctionParser {
    IDENTITY {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 1, "IDENTITY");

            String actualValue = arguments.getFirst().trim();
            if (isBoolean(actualValue)) {
                return new IdentityExpression(Boolean.parseBoolean(actualValue), CellType.BOOLEAN);
            } else if (isNumeric(actualValue)) {
                return new IdentityExpression(Double.parseDouble(actualValue), CellType.NUMERIC);
            } else {
                return new IdentityExpression(actualValue, CellType.STRING);
            }
        }
    },
    PLUS {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "PLUS");

            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);


            return new PlusExpression(left, right);
        }
    },
    MINUS {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "MINUS");

            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);


            return new MinusExpression(left, right);
        }
    },
    TIMES {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "TIMES");

            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);


            return new TimesExpression(left, right);
        }
    },
    DIVIDE {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "DIVIDE");

            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);


            return new DivideExpression(left, right);
        }
    },
    POW {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "POW");

            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);


            return new PowExpression(left, right);
        }
    },
    ABS {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 1, "ABS");

            Expression arg = parseExpression(arguments.getFirst().trim(), sheet);


            return new AbsExpression(arg);
        }
    },
    MOD {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "MOD");

            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);


            return new ModuloExpression(left, right);
        }
    },
    REF {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 1, "REF");

            return new RefExpression(arguments.getFirst(), sheet);
        }
    },
    CONCAT {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "CONCAT");

            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);


            return new ConcatExpression(left, right);
        }
    },
    SUB {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 3, "SUB");

            Expression source = parseExpression(arguments.get(0).trim(), sheet);
            Expression start = parseExpression(arguments.get(1).trim(), sheet);
            Expression end = parseExpression(arguments.get(2).trim(), sheet);


            return new SubExpression(source, start, end);
        }
    },
    SUM {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 1, "SUM");
            String rangeName = arguments.get(0).trim();
            return new SumExpression(rangeName, sheet);
        }
    },
    AVERAGE {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 1, "AVERAGE");
            String rangeName = arguments.get(0).trim();
            return new AverageExpression(rangeName, sheet);
        }
    },
    NOT {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 1, "NOT");
            Expression exp = parseExpression(arguments.get(0).trim(), sheet);
            validateArgumentType(exp, CellType.BOOLEAN, "NOT");
            return new NotExpression(exp);
        }
    },
    AND {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "AND");
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            return new AndExpression(left, right);
        }
    },
    OR {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "OR");
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            return new OrExpression(left, right);
        }
    },
    EQUALS {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            validateArgumentCount(arguments, 2, "EQUALS");
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            return new EqualsExpression(left, right);
        }
    };

    abstract public Expression parse(List<String> arguments, Sheet sheet);

    public static Expression parseExpression(String input, Sheet sheet) {
        if (input.startsWith("{") && input.endsWith("}")) {
            String functionContent = input.substring(1, input.length() - 1);
            List<String> topLevelParts = parseMainParts(functionContent);
            String functionName = topLevelParts.getFirst().trim().toUpperCase();
            topLevelParts.removeFirst();
            try {
                return FunctionParser.valueOf(functionName).parse(topLevelParts, sheet);
            }
            catch (IllegalArgumentException e) {
                throw new IllegalArgumentException(" function " + topLevelParts.getFirst().trim() + " doesn't exists in Shticell.");
            }
        }

        return FunctionParser.IDENTITY.parse(List.of(input.trim()), sheet);
    }

    private static List<String> parseMainParts(String input) {
        List<String> parts = new ArrayList<>();
        StringBuilder buffer = new StringBuilder();
        Stack<Character> stack = new Stack<>();

        for (char c : input.toCharArray()) {
            if (c == '{') {
                stack.push(c);
            } else if (c == '}') {
                stack.pop();
            }

            if (c == ',' && stack.isEmpty()) {
                parts.add(buffer.toString().trim());
                buffer.setLength(0);
            } else {
                buffer.append(c);
            }
        }

        if (!buffer.isEmpty()) {
            parts.add(buffer.toString().trim());
        }

        return parts;
    }

    private static void validateArgumentCount(List<String> arguments, int expected, String functionName) {
        if (arguments.size() != expected) {
            throw new IllegalNumberOfArgumentsException("Invalid number of arguments for " + functionName + " function. Expected " + expected + ", but got " + arguments.size());
        }
    }

    private static void validateArgumentType(Expression expression, CellType expectedType, String functionName) {
        if (!expression.getFunctionResultType().equals(expectedType) && !expression.getFunctionResultType().equals(CellType.UNKNOWN)) {
            throw new IllegalArgumentException("Invalid argument type for " + functionName + " function. Expected " + expectedType + ", but got " + expression.getFunctionResultType());
        }
    }

    private static void validateArgumentTypes(List<Expression> expressions, CellType expectedType, String functionName) {
        for (Expression expression : expressions) {
            validateArgumentType(expression, expectedType, functionName);
        }
    }

    private static boolean isBoolean(String value) {
        return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
    }


    private static boolean isNumeric(String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

}