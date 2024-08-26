package engine.expression.parser;

import engine.expression.api.Expression;
import engine.expression.impl.*;
import engine.expression.impl.numeric.*;
import engine.expression.impl.ref.*;
import engine.expression.impl.string.*;
//import engine.expression.impl.bool.*;
import engine.sheet.api.Sheet;
import engine.sheet.cell.api.Cell;
import engine.sheet.cell.impl.CellType;
import engine.sheet.cell.api.EffectiveValue;

import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public enum FunctionParser {
    IDENTITY {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            // validations of the function. it should have exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for IDENTITY function. Expected 1, but got " + arguments.size());
            }

            // all is good. create the relevant function instance
            String actualValue = arguments.getFirst().trim();
            if (isBoolean(actualValue)) {
                return new IdentityExpression(Boolean.parseBoolean(actualValue), CellType.BOOLEAN);
            } else if (isNumeric(actualValue)) {
                return new IdentityExpression(Double.parseDouble(actualValue), CellType.NUMERIC);
            } else {
                return new IdentityExpression(actualValue, CellType.STRING);
            }
        }

        private boolean isBoolean(String value) {
            return "true".equalsIgnoreCase(value) || "false".equalsIgnoreCase(value);
        }

        private boolean isNumeric(String value) {
            try {
                Double.parseDouble(value);
                return true;
            } catch (NumberFormatException e) {
                return false;
            }
        }
    },
    PLUS {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            // validations of the function (e.g. number of arguments)
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for PLUS function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            // more validations on the expected argument types
            if (!left.getFunctionResultType().equals(CellType.NUMERIC) || !right.getFunctionResultType().equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for PLUS function. Expected NUMERIC, but got " + left.getFunctionResultType() + " and " + right.getFunctionResultType());
            }

            // all is good. create the relevant function instance
            return new PlusExpression(left, right);
        }
    },
    MINUS {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for MINUS function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            // more validations on the expected argument types
            if (!left.getFunctionResultType().equals(CellType.NUMERIC) || !right.getFunctionResultType().equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for MINUS function. Expected NUMERIC, but got " + left.getFunctionResultType() + " and " + right.getFunctionResultType());
            }

            // all is good. create the relevant function instance
            return new MinusExpression(left, right);
        }
    },
    TIMES {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for MINUS function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            // more validations on the expected argument types
            if (!left.getFunctionResultType().equals(CellType.NUMERIC) || !right.getFunctionResultType().equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for MINUS function. Expected NUMERIC, but got " + left.getFunctionResultType() + " and " + right.getFunctionResultType());
            }

            // all is good. create the relevant function instance
            return new TimesExpression(left, right);
        }
    },
    DIVIDE {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for MINUS function. Expected 2, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            // more validations on the expected argument types
            if (!left.getFunctionResultType().equals(CellType.NUMERIC) || !right.getFunctionResultType().equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for MINUS function. Expected NUMERIC, but got " + left.getFunctionResultType() + " and " + right.getFunctionResultType());
            }

            // all is good. create the relevant function instance
            return new DivideExpression(left, right);
        }
    },
    POW {
        public Expression parse(List<String> arguments, Sheet sheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for POW function. Expected 2, but got " + arguments.size());
            }
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);
            // more validations on the expected argument types
            if (!left.getFunctionResultType().equals(CellType.NUMERIC) || !right.getFunctionResultType().equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for POW function. Expected NUMERIC, but got " + left.getFunctionResultType() + " and " + right.getFunctionResultType());
            }

            // all is good. create the relevant function instance
            return new PowExpression(left, right);
        }
    },
    ABS{
        public Expression parse(List<String> arguments, Sheet sheet) {
            // validations of the function. it should have exactly two arguments
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for ABS function. Expected 2, but got " + arguments.size());
            }
            Expression arg = parseExpression(arguments.getFirst().trim(), sheet);
            // more validations on the expected argument types
            if (!arg.getFunctionResultType().equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for ABS function. Expected NUMERIC, but got " + arg.getFunctionResultType());
            }

            // all is good. create the relevant function instance
            return new AbsExpression(arg);
        }
    },
    MOD{
        public Expression parse(List<String> arguments, Sheet sheet) {
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for MOD function. Expected 2, but got " + arguments.size());
            }
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);
            if (!left.getFunctionResultType().equals(CellType.NUMERIC) || !right.getFunctionResultType().equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for POW function. Expected NUMERIC, but got" + left.getFunctionResultType() + " and" + right.getFunctionResultType());
            }
            return new ModuloExpression(left,right);
        }
    },
    UPPER_CASE {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            // validations of the function. it should have exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for UPPER_CASE function. Expected 1, but got " + arguments.size());
            }

            // structure is good. parse arguments
            Expression arg = parseExpression(arguments.getFirst().trim(), sheet);

            // more validations on the expected argument types
            if (!arg.getFunctionResultType().equals(CellType.STRING)) {
                throw new IllegalArgumentException("Invalid argument types for UPPER_CASE function. Expected STRING, but got " + arg.getFunctionResultType());
            }

            // all is good. create the relevant function instance
            return new UpperCaseExpression(arg.toString());
        }
    },
    REF {
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            // validations of the function. it should have exactly one argument
            if (arguments.size() != 1) {
                throw new IllegalArgumentException("Invalid number of arguments for REF function. Expected 1, but got " + arguments.size());
            }

            // all is good. create the relevant function instance
            return new RefExpression(arguments.getFirst(), sheet);
        }
    },
    CONCAT{
        @Override
        public Expression parse(List<String> arguments, Sheet sheet) {
            if (arguments.size() != 2) {
                throw new IllegalArgumentException("Invalid number of arguments for CONCAT function. Expected 2, but got " + arguments.size());
            }
            Expression left = parseExpression(arguments.get(0).trim(), sheet);
            Expression right = parseExpression(arguments.get(1).trim(), sheet);

            if(!left.getFunctionResultType().equals(CellType.STRING) || !right.getFunctionResultType().equals(CellType.STRING)) {
                throw new IllegalArgumentException("Invalid argument types for CONCAT function. Expected STRING, but got " + left.getFunctionResultType() + " and " + right.getFunctionResultType());
            }
            return new ConcatExpression(left, right);
        }
    },
    SUB{
        public Expression parse(List<String> arguments, Sheet sheet) {
            if (arguments.size() != 3) {
                throw new IllegalArgumentException("Invalid number of arguments for SUB function. Expected 3, but got " + arguments.size());
            }
            Expression source = parseExpression(arguments.get(0).trim(), sheet);
            Expression start = parseExpression(arguments.get(1).trim(), sheet);
            Expression end = parseExpression(arguments.get(2).trim(), sheet);

            if(!source.getFunctionResultType().equals(CellType.STRING)|| !start.getFunctionResultType().equals(CellType.NUMERIC)
                    || !end.getFunctionResultType().equals(CellType.NUMERIC)) {
                throw new IllegalArgumentException("Invalid argument types for CONCAT function. Expected STRING,NUMERIC,NUMERIC but got " + source.getFunctionResultType()
                        + " , " + start.getFunctionResultType() + " and " + end.getFunctionResultType());
            }
            return new SubExpression(source, start, end);
        }
    }

    ;

    abstract public Expression parse(List<String> arguments, Sheet sheet);

    public static Expression parseExpression(String input, Sheet sheet) {

        if (input.startsWith("{") && input.endsWith("}")) {

            String functionContent = input.substring(1, input.length() - 1);
            List<String> topLevelParts = parseMainParts(functionContent);


            String functionName = topLevelParts.getFirst().trim().toUpperCase();

            //remove the first element from the array
            topLevelParts.removeFirst();
            return FunctionParser.valueOf(functionName).parse(topLevelParts, sheet);
        }

        // handle identity expression
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
                // If we are at a comma and the stack is empty, it's a separator for top-level parts
                parts.add(buffer.toString().trim());
                buffer.setLength(0); // Clear the buffer for the next part
            } else {
                buffer.append(c);
            }
        }

        // Add the last part
        if (!buffer.isEmpty()) {
            parts.add(buffer.toString().trim());
        }

        return parts;
    }

    public static void main(String[] args) {

        //String input = "plus, {plus, 1, 2}, {plus, 1, {plus, 1, 2}}";
//        String input = "1";
//        parseMainParts(input).forEach(System.out::println);

//        String input = "{plus, 1, 2}";
        String input = "{plus, {minus, 44, 22}, {plus, 1, 2}}";
//        String input = "{upper_case, hello world}";
//        String input = "4";
        Expression expression = parseExpression(input, null);
        EffectiveValue result = expression.eval();
        System.out.println("result: " + result.getValue() + " of type " + result.getCellType());
    }

}