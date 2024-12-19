import java.util.*;

public class FlipFlopStateTable {
    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);

        try {
            // Input: Flip-Flop Type
            System.out.println("=== Flip-Flop State Table Generator ===");
            System.out.println("1. SR Flip-Flop");
            System.out.println("2. JK Flip-Flop");
            System.out.println("3. D Flip-Flop");
            System.out.println("4. T Flip-Flop");

            int choice = 0;
            String flipFlopType = "";
            while (true) {
                System.out.print("Select the flip-flop type (1-4): ");
                if (scanner.hasNextInt()) {
                    choice = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline
                    switch (choice) {
                        case 1 -> flipFlopType = "SR";
                        case 2 -> flipFlopType = "JK";
                        case 3 -> flipFlopType = "D";
                        case 4 -> flipFlopType = "T";
                        default -> {
                            System.out.println("Invalid choice! Please enter a number between 1 and 4.");
                            continue;
                        }
                    }
                    break;
                } else {
                    System.out.println("Invalid input! Please enter a valid integer.");
                    scanner.next(); // Clear invalid input
                }
            }

            // Input: Number of flip-flops
            int flipFlops = 0;
            while (true) {
                System.out.print("Enter the number of flip-flops (1 or 2): ");
                if (scanner.hasNextInt()) {
                    flipFlops = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline
                    if (flipFlops >= 1 && flipFlops <= 2) {
                        break;
                    } else {
                        System.out.println("Invalid number of flip-flops! Enter 1 or 2.");
                    }
                } else {
                    System.out.println("Invalid input! Please enter a valid integer.");
                    scanner.next(); // Clear invalid input
                }
            }

            // Input: Number of input variables
            int inputs = 0;
            while (true) {
                System.out.print("Enter the number of input variables (1 or 2): ");
                if (scanner.hasNextInt()) {
                    inputs = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline
                    if (inputs >= 1 && inputs <= 2) {
                        break;
                    } else {
                        System.out.println("Invalid number of input variables! Enter 1 or 2.");
                    }
                } else {
                    System.out.println("Invalid input! Please enter a valid integer.");
                    scanner.next(); // Clear invalid input
                }
            }

            // Input: Number of output variables
            int outputs = 0;
            while (true) {
                System.out.print("Enter the number of output variables (0 or 1): ");
                if (scanner.hasNextInt()) {
                    outputs = scanner.nextInt();
                    scanner.nextLine(); // Consume the newline
                    if (outputs == 0 || outputs == 1) {
                        break;
                    } else {
                        System.out.println("Invalid number of output variables! Enter 0 or 1.");
                    }
                } else {
                    System.out.println("Invalid input! Please enter a valid integer.");
                    scanner.next(); // Clear invalid input
                }
            }

            // Input: Flip-Flop Input Functions and Output Function
            String[] flipFlopFunctions = new String[flipFlops];
            for (int i = 0; i < flipFlops; i++) {
                StringBuilder functionBuilder = new StringBuilder();
                System.out.printf("Enter the functions for flip-flop %d:%n", i);

                if (flipFlopType.equals("SR") || flipFlopType.equals("JK")) {
                    System.out.printf("Enter %s function:%n", flipFlopType.charAt(0));
                    String firstFunc = scanner.nextLine().trim();
                    System.out.printf("Enter %s function:%n", flipFlopType.charAt(1));
                    String secondFunc = scanner.nextLine().trim();

                    if (firstFunc.isEmpty() || secondFunc.isEmpty()) {
                        System.out.println("Functions cannot be blank. Please try again.");
                        i--; // Retry this flip-flop
                        continue;
                    }

                    functionBuilder.append(firstFunc).append("\n").append(secondFunc);
                } else {
                    System.out.println("Enter the function:");
                    String function = scanner.nextLine().trim();
                    if (function.isEmpty()) {
                        System.out.println("Function cannot be blank. Please try again.");
                        i--; // Retry this flip-flop
                        continue;
                    }
                    functionBuilder.append(function);
                }

                flipFlopFunctions[i] = functionBuilder.toString();
            }

            String outputFunction = null;
            if (outputs == 1) {
                while (true) {
                    System.out.println("Enter the Boolean function for the output:");
                    outputFunction = scanner.nextLine();
                    if (!outputFunction.isBlank()) {
                        break;
                    } else {
                        System.out.println("Function cannot be blank. Please try again.");
                    }
                }
            }

            // Generate and print the state table
            StateTableGenerator generator = new StateTableGenerator(flipFlops, inputs, outputs, flipFlopType, flipFlopFunctions, outputFunction);
            generator.generateStateTable();
        } catch (IllegalArgumentException e) {
            System.err.println("Invalid input: " + e.getMessage());
        } catch (IllegalStateException e) {
            System.err.println("State table generation error: " + e.getMessage());
        } catch (Exception e) {
            System.err.println("Unexpected error: " + e.getMessage());
            e.printStackTrace();
        } finally {
            scanner.close();
        }
    }
}
