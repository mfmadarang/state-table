import java.util.Scanner;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlipFlopStateTable {
    // Enhanced Scanner with more robust input handling
    private static final Scanner sc = new Scanner(System.in);

    // Expanded Flip-Flop Type enum with additional metadata
    enum FlipFlopType {
        SR("Set-Reset", "Allows setting and resetting of state"),
        JK("Jack-Kilby", "Provides toggle and hold functionality"),
        D("Data/Delay", "Stores and transfers input directly"),
        T("Toggle", "Inverts state on specific condition");

        private final String fullName;
        private final String description;

        FlipFlopType(String fullName, String description) {
            this.fullName = fullName;
            this.description = description;
        }

        public String getFullName() {
            return fullName;
        }

        public String getDescription() {
            return description;
        }
    }

    public static void main(String[] args) {
        try {
            System.out.println("=== Advanced Flip-Flop State Table Generator ===");

            // Improved type selection with description
            FlipFlopType flipFlopType = chooseFlipFlopType();
            System.out.println("Selected: " + flipFlopType.getFullName() + 
                               " - " + flipFlopType.getDescription());

            // Enhanced input validation with more context
            int numFlipFlops = getValidatedInput(
                "Number of Flip-Flops", 1, 2, 
                "Limited to 1-2 flip-flops for comprehensive analysis"
            );

            int numInputs = getValidatedInput(
                "Input Variables", 1, 2, 
                "Supports 1-2 input variables for state complexity"
            );

            int numOutputs = getValidatedInput(
                "Output Variables", 0, 1, 
                "Supports 0-1 output variables"
            );

            // Advanced input function validation
            String inputFunction = getValidatedBooleanExpression("Input Function");
            String outputFunction = numOutputs == 1 ? 
                getValidatedBooleanExpression("Output Function") : "";

            // Generate state table with enhanced logging
            generateAdvancedStateTable(
                flipFlopType, 
                numFlipFlops, 
                numInputs, 
                inputFunction, 
                outputFunction
            );

        } catch (Exception e) {
            System.err.println("Error in state table generation: " + e.getMessage());
            e.printStackTrace();
        } finally {
            sc.close();
        }
    }

    /**
     * Enhanced type selection with more informative prompt
     */
    private static FlipFlopType chooseFlipFlopType() {
        while (true) {
            System.out.println("\nAvailable Flip-Flop Types:");
            for (FlipFlopType type : FlipFlopType.values()) {
                System.out.printf("%s: %s\n", type.name(), type.getDescription());
            }
            System.out.print("Enter Flip-Flop Type: ");
            
            try {
                return FlipFlopType.valueOf(sc.nextLine().toUpperCase().trim());
            } catch (IllegalArgumentException e) {
                System.out.println("Invalid type. Please choose from the listed types.");
            }
        }
    }

    /**
     * More robust input validation with descriptive messages
     */
    private static int getValidatedInput(
        String prompt, 
        int min, 
        int max, 
        String helpText
    ) {
        while (true) {
            System.out.printf("%s (%d-%d): %s\n", prompt, min, max, helpText);
            System.out.print("Enter value: ");
            
            try {
                int value = Integer.parseInt(sc.nextLine().trim());
                if (value >= min && value <= max) {
                    return value;
                }
                System.out.println("Value must be between " + min + " and " + max);
            } catch (NumberFormatException e) {
                System.out.println("Invalid input. Please enter a numeric value.");
            }
        }
    }

    /**
     * Basic boolean expression validation
     */
    private static String getValidatedBooleanExpression(String type) {
        while (true) {
            System.out.printf("Enter %s (Boolean Expression): ", type);
            String expression = sc.nextLine().trim();
            
            if (isValidBooleanExpression(expression)) {
                return expression;
            }
            System.out.println("Invalid boolean expression. Use variables and logical operators.");
        }
    }

    /**
     * Simple boolean expression validation
     */
    private static boolean isValidBooleanExpression(String expression) {
        if (expression.isEmpty()) return true;
        
        // Basic regex to check for valid boolean expression syntax
        String booleanPattern = "^[a-zA-Z0-9\\s+*!&|()]+$";
        return expression.matches(booleanPattern);
    }

    /**
     * Advanced state table generation with more comprehensive analysis
     */
    private static void generateAdvancedStateTable(
        FlipFlopType type, 
        int flipFlops, 
        int inputs, 
        String inputFunction, 
        String outputFunction
    ) {
        int totalStates = (int) Math.pow(2, flipFlops + inputs);
        
        System.out.println("\n--- Detailed State Table Analysis ---");
        printTableHeader();
        
        for (int stateIndex = 0; stateIndex < totalStates; stateIndex++) {
            String binaryState = generatePaddedBinaryState(stateIndex, flipFlops + inputs);
            
            String nextState = calculateAdvancedNextState(type, binaryState);
            String output = generateOutput(outputFunction, binaryState);
            
            printTableRow(binaryState, nextState, output);
        }
        
        printTableFooter();
    }

    /**
     * More sophisticated next state calculation
     */
    private static String calculateAdvancedNextState(FlipFlopType type, String currentState) {
        switch (type) {
            case SR: return calculateSRNextState(currentState);
            case JK: return calculateJKNextState(currentState);
            case D:  return calculateDNextState(currentState);
            case T:  return calculateTNextState(currentState);
            default: throw new IllegalArgumentException("Unsupported flip-flop type");
        }
    }

    // Existing next state calculation methods remain the same
    private static String calculateSRNextState(String currentState) {
        return currentState.contains("1") ? "1" + currentState.substring(1) : "0" + currentState.substring(1);
    }

    private static String calculateJKNextState(String currentState) {
        return currentState.endsWith("1") ? 
            "0" + currentState.substring(1) : 
            "1" + currentState.substring(1);
    }

    private static String calculateDNextState(String currentState) {
        return currentState.charAt(currentState.length() - 1) + 
               currentState.substring(1, currentState.length() - 1);
    }

    private static String calculateTNextState(String currentState) {
        return (currentState.charAt(0) == '0' ? "1" : "0") + 
               currentState.substring(1);
    }

    /**
     * Utility methods for table generation
     */
    private static String generatePaddedBinaryState(int stateIndex, int totalWidth) {
        String binaryState = Integer.toBinaryString(stateIndex);
        return String.format("%" + totalWidth + "s", binaryState)
               .replace(' ', '0');
    }

    private static String generateOutput(String outputFunction, String state) {
        return outputFunction.isEmpty() ? "-" : outputFunction;
    }

    private static void printTableHeader() {
        System.out.println("+--------------+-------+------------+--------+");
        System.out.println("| Current State| Input | Next State | Output |");
        System.out.println("+--------------+-------+------------+--------+");
    }

    private static void printTableRow(String state, String nextState, String output) {
        System.out.printf("| %12s | %5s | %10s | %6s |\n", 
            state, 
            state.substring(state.length() / 2), 
            nextState, 
            output
        );
    }

    private static void printTableFooter() {
        System.out.println("+--------------+-------+------------+--------+");
    }
}