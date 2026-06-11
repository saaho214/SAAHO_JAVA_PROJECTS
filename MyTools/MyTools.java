import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.ArrayList;
import java.util.Collections;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

/**
 * A menu-driven Java program that combines multiple logical tools and mini-projects.
 * The program provides a main menu for the user to select from various functions
 * such as a Palindrome Checker, Factorial Calculator, and more.
 */
public class MyTools {

    private static Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        boolean isRunning = true;
        while (isRunning) {
            displayMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline left-over

                switch (choice) {
                    case 1:
                        palindromeChecker();
                        break;
                    case 2:
                        factorialCalculator();
                        break;
                    case 3:
                        simpleToDoList();
                        break;
                    case 4:
                        primeNumberChecker();
                        break;
                    case 5:
                        fibonacciSeriesGenerator();
                        break;
                    case 6:
                        countVowelsAndConsonants();
                        break;
                    case 7:
                        towerOfHanoi();
                        break;
                    case 8:
                        findPeakElement();
                        break;
                    case 9:
                        sumAveragePercentage();
                        break;
                    case 10:
                        sortNumbers();
                        break;
                    case 11:
                        reverseNumbers();
                        break;
                    case 12:
                        reverseString();
                        break;
                    case 13:
                        countAlphabets();
                        break;
                    case 14:
                        basicFileManager();
                        break;
                    case 15:
                        System.out.println("Exiting the program. Goodbye!");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number from 1 to 15.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input from the scanner
            }
        }
        scanner.close();
    }

    /**
     * Displays the main menu options to the user.
     */
    private static void displayMenu() {
        System.out.println("\n----------------------------------");
        System.out.println("         Main Menu");
        System.out.println("----------------------------------");
        System.out.println("1. Palindrome Checker (String & Number)");
        System.out.println("2. Factorial Calculator");
        System.out.println("3. Simple To-Do List");
        System.out.println("4. Prime Number Checker");
        System.out.println("5. Fibonacci Series Generator");
        System.out.println("6. Count Vowels and Consonants");
        System.out.println("7. Tower of Hanoi");
        System.out.println("8. Find Peak Element in an Array");
        System.out.println("9. Sum, Average & Percentage of Numbers");
        System.out.println("10. Sort Numbers");
        System.out.println("11. Reverse a Number");
        System.out.println("12. Reverse a String");
        System.out.println("13. Count Alphabets in a String");
        System.out.println("14. Basic File Manager");
        System.out.println("15. Exit");
        System.out.println("----------------------------------");
        System.out.print("Enter your choice: ");
    }

    /**
     * Checks if a string or number is a palindrome.
     */
    private static void palindromeChecker() {
        System.out.println("\n--- Palindrome Checker ---");
        System.out.print("Enter a string or number to check: ");
        String input = scanner.nextLine();
        String reversed = new StringBuilder(input).reverse().toString();

        if (input.equals(reversed)) {
            System.out.println("'" + input + "' is a palindrome.");
        } else {
            System.out.println("'" + input + "' is not a palindrome.");
        }
    }

    /**
     * Calculates the factorial of a given number.
     */
    private static void factorialCalculator() {
        System.out.println("\n--- Factorial Calculator ---");
        try {
            System.out.print("Enter a non-negative number: ");
            int number = scanner.nextInt();
            scanner.nextLine();

            if (number < 0) {
                System.out.println("Factorial is not defined for negative numbers.");
            } else {
                long factorial = 1;
                for (int i = 1; i <= number; i++) {
                    factorial *= i;
                }
                System.out.println("The factorial of " + number + " is " + factorial);
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid integer.");
            scanner.nextLine();
        }
    }

    /**
     * Manages a simple in-memory To-Do list.
     */
    private static void simpleToDoList() {
        ArrayList<String> tasks = new ArrayList<>();
        boolean isManaging = true;
        while (isManaging) {
            System.out.println("\n--- To-Do List ---");
            System.out.println("1. View Tasks");
            System.out.println("2. Add Task");
            System.out.println("3. Delete Task");
            System.out.println("4. Back to Main Menu");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        viewTasks(tasks);
                        break;
                    case 2:
                        System.out.print("Enter new task: ");
                        String newTask = scanner.nextLine();
                        tasks.add(newTask);
                        System.out.println("Task added: " + newTask);
                        break;
                    case 3:
                        viewTasks(tasks);
                        if (!tasks.isEmpty()) {
                            System.out.print("Enter the number of the task to delete: ");
                            int taskIndex = scanner.nextInt() - 1;
                            scanner.nextLine();
                            if (taskIndex >= 0 && taskIndex < tasks.size()) {
                                String removedTask = tasks.remove(taskIndex);
                                System.out.println("Task deleted: " + removedTask);
                            } else {
                                System.out.println("Invalid task number.");
                            }
                        }
                        break;
                    case 4:
                        isManaging = false;
                        break;
                    default:
                        System.out.println("Invalid choice.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    /**
     * Helper method to display tasks.
     */
    private static void viewTasks(ArrayList<String> tasks) {
        System.out.println("\n--- Your Tasks ---");
        if (tasks.isEmpty()) {
            System.out.println("No tasks to display.");
        } else {
            for (int i = 0; i < tasks.size(); i++) {
                System.out.println((i + 1) + ". " + tasks.get(i));
            }
        }
    }

    /**
     * Checks if a number is prime.
     */
    private static void primeNumberChecker() {
        System.out.println("\n--- Prime Number Checker ---");
        try {
            System.out.print("Enter a number: ");
            int number = scanner.nextInt();
            scanner.nextLine();

            if (number <= 1) {
                System.out.println(number + " is not a prime number.");
                return;
            }
            boolean isPrime = true;
            for (int i = 2; i <= Math.sqrt(number); i++) {
                if (number % i == 0) {
                    isPrime = false;
                    break;
                }
            }
            if (isPrime) {
                System.out.println(number + " is a prime number.");
            } else {
                System.out.println(number + " is not a prime number.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid integer.");
            scanner.nextLine();
        }
    }

    /**
     * Generates the Fibonacci series up to a given number of terms.
     */
    private static void fibonacciSeriesGenerator() {
        System.out.println("\n--- Fibonacci Series Generator ---");
        try {
            System.out.print("Enter the number of terms: ");
            int n = scanner.nextInt();
            scanner.nextLine();

            if (n <= 0) {
                System.out.println("Number of terms must be a positive integer.");
                return;
            }

            long a = 0, b = 1;
            System.out.print("Fibonacci Series: ");
            for (int i = 0; i < n; i++) {
                System.out.print(a + " ");
                long sum = a + b;
                a = b;
                b = sum;
            }
            System.out.println();
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid integer.");
            scanner.nextLine();
        }
    }

    /**
     * Counts vowels and consonants in a string.
     */
    private static void countVowelsAndConsonants() {
        System.out.println("\n--- Count Vowels and Consonants ---");
        System.out.print("Enter a string: ");
        String input = scanner.nextLine().toLowerCase();
        int vowels = 0;
        int consonants = 0;

        for (char ch : input.toCharArray()) {
            if (ch >= 'a' && ch <= 'z') {
                if (ch == 'a' || ch == 'e' || ch == 'i' || ch == 'o' || ch == 'u') {
                    vowels++;
                } else {
                    consonants++;
                }
            }
        }
        System.out.println("Number of vowels: " + vowels);
        System.out.println("Number of consonants: " + consonants);
    }

    /**
     * Implements the classic Tower of Hanoi problem using recursion.
     */
    private static void towerOfHanoi() {
        System.out.println("\n--- Tower of Hanoi ---");
        try {
            System.out.print("Enter the number of disks: ");
            int n = scanner.nextInt();
            scanner.nextLine();

            if (n <= 0) {
                System.out.println("Number of disks must be a positive integer.");
                return;
            }
            System.out.println("Steps to solve Tower of Hanoi with " + n + " disks:");
            solveTowerOfHanoi(n, 'A', 'B', 'C');
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid integer.");
            scanner.nextLine();
        }
    }

    /**
     * Recursive helper method for Tower of Hanoi.
     */
    private static void solveTowerOfHanoi(int n, char from_rod, char to_rod, char aux_rod) {
        if (n == 1) {
            System.out.println("Move disk 1 from rod " + from_rod + " to rod " + to_rod);
            return;
        }
        solveTowerOfHanoi(n - 1, from_rod, aux_rod, to_rod);
        System.out.println("Move disk " + n + " from rod " + from_rod + " to rod " + to_rod);
        solveTowerOfHanoi(n - 1, aux_rod, to_rod, from_rod);
    }

    /**
     * Finds a peak element in an array.
     */
    private static void findPeakElement() {
        System.out.println("\n--- Find Peak Element ---");
        try {
            System.out.print("Enter numbers separated by spaces (e.g., 1 2 3 4): ");
            String[] strNumbers = scanner.nextLine().split(" ");
            if (strNumbers.length == 0 || (strNumbers.length == 1 && strNumbers[0].isEmpty())) {
                System.out.println("Please enter at least one number.");
                return;
            }

            int[] numbers = new int[strNumbers.length];
            for (int i = 0; i < strNumbers.length; i++) {
                numbers[i] = Integer.parseInt(strNumbers[i]);
            }

            for (int i = 0; i < numbers.length; i++) {
                if ((i == 0 || numbers[i] >= numbers[i - 1]) && (i == numbers.length - 1 || numbers[i] >= numbers[i + 1])) {
                    System.out.println("A peak element found at index " + i + " with value " + numbers[i]);
                    return;
                }
            }
            System.out.println("No peak element found in the given array.");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter numbers only.");
        }
    }

    /**
     * Calculates sum, average, and percentage of a list of numbers.
     */
    private static void sumAveragePercentage() {
        System.out.println("\n--- Sum, Average & Percentage ---");
        try {
            System.out.print("Enter numbers separated by spaces (e.g., 10 20 30): ");
            String[] strNumbers = scanner.nextLine().split(" ");
            if (strNumbers.length == 0 || (strNumbers.length == 1 && strNumbers[0].isEmpty())) {
                System.out.println("Please enter at least one number.");
                return;
            }

            double sum = 0;
            int count = 0;
            for (String str : strNumbers) {
                sum += Double.parseDouble(str);
                count++;
            }

            double average = sum / count;

            System.out.println("Sum: " + sum);
            System.out.printf("Average: %.2f\n", average);
            // Percentage is calculated relative to the total sum of the numbers themselves.
            System.out.printf("Each number represents a percentage of the total sum.\n");

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
        }
    }

    /**
     * Sorts a list of numbers in ascending order.
     */
    private static void sortNumbers() {
        System.out.println("\n--- Sort Numbers ---");
        try {
            System.out.print("Enter numbers separated by spaces (e.g., 5 2 8 1): ");
            String[] strNumbers = scanner.nextLine().split(" ");
            if (strNumbers.length == 0 || (strNumbers.length == 1 && strNumbers[0].isEmpty())) {
                System.out.println("Please enter at least one number.");
                return;
            }

            ArrayList<Integer> numbers = new ArrayList<>();
            for (String str : strNumbers) {
                numbers.add(Integer.parseInt(str));
            }

            Collections.sort(numbers);

            System.out.println("Sorted numbers: " + numbers);

        } catch (NumberFormatException e) {
            System.out.println("Invalid input. Please enter valid integers.");
        }
    }

    /**
     * Reverses a given integer number.
     */
    private static void reverseNumbers() {
        System.out.println("\n--- Reverse Number ---");
        try {
            System.out.print("Enter a number to reverse: ");
            int number = scanner.nextInt();
            scanner.nextLine();
            int reversed = 0;

            while (number != 0) {
                int digit = number % 10;
                reversed = reversed * 10 + digit;
                number /= 10;
            }
            System.out.println("Reversed number: " + reversed);
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid integer.");
            scanner.nextLine();
        }
    }

    /**
     * Reverses a given string.
     */
    private static void reverseString() {
        System.out.println("\n--- Reverse String ---");
        System.out.print("Enter a string to reverse: ");
        String input = scanner.nextLine();
        String reversed = new StringBuilder(input).reverse().toString();
        System.out.println("Reversed string: " + reversed);
    }

    /**
     * Counts the number of alphabetic characters in a string.
     */
    private static void countAlphabets() {
        System.out.println("\n--- Count Alphabets ---");
        System.out.print("Enter a string: ");
        String input = scanner.nextLine();
        int count = 0;

        for (char ch : input.toCharArray()) {
            if (Character.isLetter(ch)) {
                count++;
            }
        }
        System.out.println("Number of alphabets: " + count);
    }

    /**
     * Provides a basic file management system with CRUD operations.
     */
    private static void basicFileManager() {
        boolean isManaging = true;
        while (isManaging) {
            System.out.println("\n--- Basic File Manager ---");
            System.out.println("1. List Files in Current Directory");
            System.out.println("2. Create a New File");
            System.out.println("3. Read from a File");
            System.out.println("4. Write to a File (Overwrites existing content)");
            System.out.println("5. Delete a File");
            System.out.println("6. Back to Main Menu");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

                switch (choice) {
                    case 1:
                        listFiles();
                        break;
                    case 2:
                        createFile();
                        break;
                    case 3:
                        readFile();
                        break;
                    case 4:
                        writeFile();
                        break;
                    case 5:
                        deleteFile();
                        break;
                    case 6:
                        isManaging = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number from 1 to 6.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    /**
     * Lists all files and directories in the current working directory.
     */
    private static void listFiles() {
        System.out.println("\n--- Listing Files ---");
        File currentDir = new File(".");
        File[] filesList = currentDir.listFiles();
        if (filesList != null && filesList.length > 0) {
            System.out.println("Contents of " + currentDir.getAbsolutePath() + ":");
            for (File file : filesList) {
                if (file.isDirectory()) {
                    System.out.println("[DIR] " + file.getName());
                } else {
                    System.out.println("[FILE] " + file.getName());
                }
            }
        } else {
            System.out.println("Current directory is empty or could not be accessed.");
        }
    }

    /**
     * Creates a new file with the specified name.
     */
    private static void createFile() {
        System.out.println("\n--- Create New File ---");
        System.out.print("Enter the name of the new file: ");
        String fileName = scanner.nextLine();
        Path filePath = Paths.get(fileName);

        try {
            if (Files.exists(filePath)) {
                System.out.println("Error: File already exists.");
            } else {
                Files.createFile(filePath);
                System.out.println("File created successfully: " + filePath.toAbsolutePath());
            }
        } catch (IOException e) {
            System.out.println("An error occurred while creating the file: " + e.getMessage());
        }
    }

    /**
     * Reads and displays the content of a specified file.
     */
    private static void readFile() {
        System.out.println("\n--- Read File ---");
        System.out.print("Enter the name of the file to read: ");
        String fileName = scanner.nextLine();
        Path filePath = Paths.get(fileName);

        if (!Files.exists(filePath) || Files.isDirectory(filePath)) {
            System.out.println("Error: File not found or is a directory.");
            return;
        }

        try {
            System.out.println("--- Content of " + fileName + " ---");
            List<String> lines = Files.readAllLines(filePath);
            for (String line : lines) {
                System.out.println(line);
            }
            System.out.println("-------------------------");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the file: " + e.getMessage());
        }
    }

    /**
     * Writes new content to a specified file, overwriting existing content.
     */
    private static void writeFile() {
        System.out.println("\n--- Write to File ---");
        System.out.print("Enter the name of the file to write to: ");
        String fileName = scanner.nextLine();
        Path filePath = Paths.get(fileName);

        System.out.println("Enter the content to write (press Enter on an empty line to finish):");
        StringBuilder content = new StringBuilder();
        String line;
        while (scanner.hasNextLine() && !(line = scanner.nextLine()).isEmpty()) {
            content.append(line).append(System.lineSeparator());
        }

        try {
            Files.write(filePath, content.toString().getBytes());
            System.out.println("Content successfully written to file: " + filePath.toAbsolutePath());
        } catch (IOException e) {
            System.out.println("An error occurred while writing to the file: " + e.getMessage());
        }
    }

    /**
     * Deletes a specified file.
     */
    private static void deleteFile() {
        System.out.println("\n--- Delete File ---");
        System.out.print("Enter the name of the file to delete: ");
        String fileName = scanner.nextLine();
        Path filePath = Paths.get(fileName);

        try {
            boolean wasDeleted = Files.deleteIfExists(filePath);
            if (wasDeleted) {
                System.out.println("File deleted successfully: " + filePath.toAbsolutePath());
            } else {
                System.out.println("Error: File not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while deleting the file: " + e.getMessage());
        }
    }
}
