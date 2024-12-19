class FlipFlopLogic {
        public static int getNextState(String flipFlopType, int[] inputs, int[] currentState, String function) {
            if (flipFlopType == null || function == null) {
                throw new IllegalArgumentException("Flip-flop type and function cannot be null");
            }
            if (inputs == null || currentState == null) {
                throw new IllegalArgumentException("Inputs and current state arrays cannot be null");
            }
            if (currentState.length == 0) {
                throw new IllegalArgumentException("Current state array cannot be empty");
            }

            if (!isValidFlipFlopType(flipFlopType)) {
                throw new IllegalArgumentException("Invalid flip-flop type: " + flipFlopType);
            }

            try {
                return switch (flipFlopType) {
                    case "SR" -> evaluateSR(inputs, currentState, function);
                    case "JK" -> evaluateJK(inputs, currentState, function);
                    case "D" -> evaluateD(inputs, currentState, function);
                    case "T" -> evaluateT(inputs, currentState, function);
                    default -> throw new IllegalArgumentException("Unsupported flip-flop type: " + flipFlopType);
                };
            } catch (Exception e) {
                throw new IllegalStateException("Error evaluating flip-flop state: " + e.getMessage(), e);
            }
        }

        private static boolean isValidFlipFlopType(String type) {
            return type != null && (type.equals("SR") || type.equals("JK") || type.equals("D") || type.equals("T"));
        }

        private static int evaluateSR(int[] inputs, int[] currentState, String function) {
            validateArrays(inputs, currentState);

            // Split the function into S and R parts
            String[] parts = function.split("\n");
            if (parts.length != 2) {
                throw new IllegalArgumentException("SR flip-flop requires both S and R functions");
            }

            // Evaluate both S and R functions
            int[] results = BooleanEvaluator.evaluateMultiple(parts, inputs, currentState);
            int S = results[0];
            int R = results[1];

            validateBinaryValue(S, "S");
            validateBinaryValue(R, "R");

            // SR logic
            if (S == 1 && R == 1) return currentState[0]; // Hold state instead of error
            if (S == 1) return 1;
            if (R == 1) return 0;
            return currentState[0];
        }

        private static int evaluateJK(int[] inputs, int[] currentState, String function) {
            validateArrays(inputs, currentState);

            // Split the function into J and K parts
            String[] parts = function.split("\n");
            if (parts.length != 2) {
                throw new IllegalArgumentException("JK flip-flop requires both J and K functions");
            }

            // Evaluate both J and K functions
            int[] results = BooleanEvaluator.evaluateMultiple(parts, inputs, currentState);
            int J = results[0];
            int K = results[1];

            validateBinaryValue(J, "J");
            validateBinaryValue(K, "K");

            // JK logic
            if (J == 1 && K == 1) return 1 - currentState[0];
            if (J == 1) return 1;
            if (K == 1) return 0;
            return currentState[0];
        }

    private static int evaluateD(int[] inputs, int[] currentState, String function) {
        validateArrays(inputs, currentState);
        int D = BooleanEvaluator.evaluate(function, inputs, currentState);
        validateBinaryValue(D, "D");
        return D;
    }

    private static int evaluateT(int[] inputs, int[] currentState, String function) {
        validateArrays(inputs, currentState);
        int T = BooleanEvaluator.evaluate(function, inputs, currentState);
        validateBinaryValue(T, "T");
        return T == 1 ? 1 - currentState[0] : currentState[0];
    }

    private static void validateArrays(int[] inputs, int[] currentState) {
        if (inputs == null || currentState == null) {
            throw new IllegalArgumentException("Input and current state arrays cannot be null");
        }
        if (inputs.length == 0 || currentState.length == 0) {
            throw new IllegalArgumentException("Input and current state arrays cannot be empty");
        }
    }

    private static void validateBinaryValue(int value, String inputName) {
        if (value != 0 && value != 1) {
            throw new IllegalStateException("Invalid " + inputName + " input: must be 0 or 1, got " + value);
        }
    }
}