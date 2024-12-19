import javax.script.ScriptException;

class StateTableGenerator {
    private final int flipFlops;
    private final int inputs;
    private final int outputs;
    private final String flipFlopType;
    private final String[] flipFlopFunctions;
    private final String outputFunction;

    public StateTableGenerator(int flipFlops, int inputs, int outputs, String flipFlopType, String[] flipFlopFunctions, String outputFunction) {
        // Validate constructor parameters
        if (flipFlops < 1 || flipFlops > 2) {
            throw new IllegalArgumentException("Number of flip-flops must be 1 or 2");
        }
        if (inputs < 1 || inputs > 2) {
            throw new IllegalArgumentException("Number of inputs must be 1 or 2");
        }
        if (outputs < 0 || outputs > 1) {
            throw new IllegalArgumentException("Number of outputs must be 0 or 1");
        }
        if (flipFlopType == null || flipFlopFunctions == null) {
            throw new IllegalArgumentException("Flip-flop type and functions cannot be null");
        }
        if (flipFlopFunctions.length != flipFlops) {
            throw new IllegalArgumentException("Number of flip-flop functions must match number of flip-flops");
        }
        if (outputs == 1 && outputFunction == null) {
            throw new IllegalArgumentException("Output function required when outputs = 1");
        }

        // Validate that each flip-flop function contains both parts for SR and JK
        if (flipFlopType.equals("SR") || flipFlopType.equals("JK")) {
            for (String func : flipFlopFunctions) {
                String[] parts = func.split("\n");
                if (parts.length != 2) {
                    throw new IllegalArgumentException("Each " + flipFlopType + " flip-flop requires two functions separated by newline");
                }
            }
        }

        this.flipFlops = flipFlops;
        this.inputs = inputs;
        this.outputs = outputs;
        this.flipFlopType = flipFlopType;
        this.flipFlopFunctions = flipFlopFunctions;
        this.outputFunction = outputFunction;
    }

    public void generateStateTable() throws ScriptException {
        System.out.printf("%n%s Flip-Flop State Table%n", flipFlopType);

        int columnWidth = 10;
        String format = "%-" + columnWidth + "s";

        // Table Header
        StringBuilder header = new StringBuilder();
        for (int i = 0; i < inputs; i++) header.append(String.format(format, "X" + i));
        for (int i = 0; i < flipFlops; i++) header.append(String.format(format, "Q" + i + " (P.S.)"));
        for (int i = 0; i < flipFlops; i++) header.append(String.format(format, "Q" + i + " (N.S.)"));
        if (outputs == 1) header.append(String.format(format, "Output"));
        System.out.println(header);

        // State Table Rows
        int totalRows = (int) Math.pow(2, inputs + flipFlops);
        for (int i = 0; i < totalRows; i++) {
            int[] inputsArr = decodeBinary(i, inputs);
            int[] currentState = decodeBinary(i >> inputs, flipFlops);

            // Calculate next state for each flip-flop
            int[] nextState = new int[flipFlops];
            for (int j = 0; j < flipFlops; j++) {
                nextState[j] = FlipFlopLogic.getNextState(flipFlopType, inputsArr, currentState, flipFlopFunctions[j]);
            }

            // Calculate output (if any)
            int output = outputs == 1 ? BooleanEvaluator.evaluate(outputFunction, inputsArr, nextState) : -1;

            // Print the row
            StringBuilder row = new StringBuilder();
            for (int in : inputsArr) row.append(String.format(format, in));
            for (int q : currentState) row.append(String.format(format, q));
            for (int qNext : nextState) row.append(String.format(format, qNext));
            if (outputs == 1) row.append(String.format(format, output));
            System.out.println(row);
        }
    }

    private int[] decodeBinary(int value, int size) {
        int[] result = new int[size];
        for (int i = size - 1; i >= 0; i--) {
            result[i] = value % 2;
            value /= 2;
        }
        return result;
    }
}