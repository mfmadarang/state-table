import java.util.Stack;

class BooleanEvaluator {
    public static int[] evaluateMultiple(String[] expressions, int[] inputs, int[] states) {
        // Input validation
        if (expressions == null) {
            throw new IllegalArgumentException("Expressions cannot be null");
        }
        if (inputs == null || states == null) {
            throw new IllegalArgumentException("Inputs and states arrays cannot be null");
        }

        int[] results = new int[expressions.length];

        try {
            for (int i = 0; i < expressions.length; i++) {
                if (expressions[i] == null) {
                    throw new IllegalArgumentException("Expression " + i + " cannot be null");
                }
                // Replace variables in the expression with actual values
                String evalExpression = replaceVariables(expressions[i], inputs, states);

                // If the expression is empty after all replacements, set result to 0
                if (evalExpression.isEmpty()) {
                    results[i] = 0;
                    continue;
                }

                // Evaluate the Boolean expression
                results[i] = evaluateExpression(evalExpression);
            }
            return results;
        } catch (Exception e) {
            throw new IllegalArgumentException("Error evaluating boolean expressions", e);
        }
    }

    // Original evaluate method for single expression backward compatibility
    public static int evaluate(String expression, int[] inputs, int[] states) {
        return evaluateMultiple(new String[]{expression}, inputs, states)[0];
    }

    private static String replaceVariables(String expression, int[] inputs, int[] states) {
        if (expression == null || expression.isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be null or empty");
        }

        String evalExpression = expression;

        try {
            // Replace input variables (X0, X1, etc.)
            for (int i = 0; i < inputs.length; i++) {
                validateBinaryValue(inputs[i], "Input X" + i);
                evalExpression = evalExpression.replace("X" + i, String.valueOf(inputs[i]));
            }

            // Replace state variables (Q0, Q1, etc.)
            for (int i = 0; i < states.length; i++) {
                validateBinaryValue(states[i], "State Q" + i);
                evalExpression = evalExpression.replace("Q" + i, String.valueOf(states[i]));
            }

            // Remove any whitespace
            evalExpression = evalExpression.replaceAll("\\s+", "");

        } catch (Exception e) {
            throw new IllegalArgumentException("Error replacing variables in expression: " + expression, e);
        }

        return evalExpression;
    }

    private static void validateBinaryValue(int value, String variableName) {
        if (value != 0 && value != 1) {
            throw new IllegalArgumentException(variableName + " must be 0 or 1, got: " + value);
        }
    }

    private static int evaluateExpression(String expression) {
        if (expression == null || expression.isEmpty()) {
            throw new IllegalArgumentException("Expression cannot be null or empty");
        }

        expression = expression.replace("_", "");
        Stack<Integer> values = new Stack<>();
        Stack<Character> operators = new Stack<>();

        try {
            for (int i = 0; i < expression.length(); i++) {
                char ch = expression.charAt(i);

                if (Character.isWhitespace(ch)) {
                    continue;
                }

                if (Character.isDigit(ch)) {
                    int value = ch - '0';
                    if (value != 0 && value != 1) {
                        throw new IllegalArgumentException("Invalid binary value in expression: " + value);
                    }
                    values.push(value);
                } else if (ch == '(') {
                    operators.push(ch);
                } else if (ch == ')') {
                    while (!operators.isEmpty() && operators.peek() != '(') {
                        evaluateTopOperator(values, operators);
                    }
                    if (operators.isEmpty()) {
                        throw new IllegalArgumentException("Mismatched parentheses in expression");
                    }
                    operators.pop(); // Pop '('
                } else if (isOperator(ch)) {
                    while (!operators.isEmpty() && precedence(operators.peek()) >= precedence(ch)) {
                        evaluateTopOperator(values, operators);
                    }
                    operators.push(ch);
                } else {
                    throw new IllegalArgumentException("Invalid character in expression: " + ch);
                }
            }

            while (!operators.isEmpty()) {
                if (operators.peek() == '(') {
                    throw new IllegalArgumentException("Mismatched parentheses in expression");
                }
                evaluateTopOperator(values, operators);
            }

            if (values.isEmpty()) {
                throw new IllegalArgumentException("Expression evaluation resulted in no value");
            }
            if (values.size() > 1) {
                throw new IllegalArgumentException("Invalid expression: too many operands");
            }

            int result = values.pop();
            validateBinaryValue(result, "Result");
            return result;

        } catch (Exception e) {
            throw new IllegalArgumentException("Error evaluating expression: " + expression, e);
        }
    }

    private static void evaluateTopOperator(Stack<Integer> values, Stack<Character> operators) {
        if (operators.isEmpty()) {
            throw new IllegalArgumentException("No operator to evaluate");
        }
        if (values.isEmpty()) {
            throw new IllegalArgumentException("Not enough operands for operator: " + operators.peek());
        }

        char operator = operators.pop();
        Integer b = values.pop();
        Integer a = operator == '-' ? null : (values.isEmpty() ? null : values.pop());

        int result = applyOperator(operator, b, a);
        values.push(result);
    }

    private static boolean isOperator(char ch) {
        return ch == '+' || ch == '*' || ch == '-';
    }

    private static int precedence(char operator) {
        return switch (operator) {
            case '-' -> 3; // NOT
            case '*' -> 2; // AND
            case '+' -> 1; // OR
            default -> 0;
        };
    }

    private static int applyOperator(char operator, Integer b, Integer a) {
        if (b == null) {
            throw new IllegalArgumentException("Missing operand for operator: " + operator);
        }

        validateBinaryValue(b, "Operand");
        if (a != null) {
            validateBinaryValue(a, "Operand");
        }

        return switch (operator) {
            case '+' -> (a != null) ? (a | b) : b; // OR
            case '*' -> (a != null) ? (a & b) : b; // AND
            case '-' -> (b == 0) ? 1 : 0; // NOT
            default -> throw new IllegalArgumentException("Invalid operator: " + operator);
        };
    }
}