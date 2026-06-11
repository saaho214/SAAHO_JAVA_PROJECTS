import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.Scanner;
import java.util.HashMap;

public class MyProject {

    // Scanner for all user input throughout the program
    private static Scanner scanner = new Scanner(System.in);
    private static final String TODO_FILE = "tasks.txt";

    public static void main(String[] args) {
        // Main menu loop
        boolean isRunning = true;
        while (isRunning) {
            displayMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline left-over

                switch (choice) {
                    case 1:
                        calculator();
                        break;
                    case 2:
                        toDoListManager();
                        break;
                    case 3:
                        countdownTimer();
                        break;
                    case 4:
                        unitConverter();
                        break;
                    case 5:
                        numberGuessingGame();
                        break;
                    case 6:
                        simpleQuiz();
                        break;
                    case 7:
                        passwordGenerator();
                        break;
                    case 8:
                        currencyConverter();
                        break;
                    case 9:
                        System.out.println("Exiting the program. Goodbye!");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number from 1 to 9.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a valid number.");
                scanner.nextLine(); // Clear the invalid input from the scanner
            }
        }
        scanner.close(); // Close the scanner when the program exits
    }

    /**
     * Displays the main menu options to the user.
     */
    private static void displayMenu() {
        System.out.println("\n----------------------------------");
        System.out.println("         Main Menu");
        System.out.println("----------------------------------");
        System.out.println("1. Calculator");
        System.out.println("2. To-Do List Manager");
        System.out.println("3. Countdown Timer");
        System.out.println("4. Unit Converter");
        System.out.println("5. Number Guessing Game");
        System.out.println("6. Simple Quiz (Java Topics)");
        System.out.println("7. Password Generator");
        System.out.println("8. Currency Converter");
        System.out.println("9. Exit");
        System.out.println("----------------------------------");
        System.out.print("Enter your choice: ");
    }

    /**
     * Implements a simple calculator with basic arithmetic operations.
     */
    private static void calculator() {
        System.out.println("\n--- Calculator ---");
        try {
            System.out.print("Enter first number: ");
            double num1 = scanner.nextDouble();
            System.out.print("Enter operator (+, -, *, /): ");
            String operator = scanner.next();
            System.out.print("Enter second number: ");
            double num2 = scanner.nextDouble();

            double result = 0;
            boolean isValid = true;

            switch (operator) {
                case "+":
                    result = num1 + num2;
                    break;
                case "-":
                    result = num1 - num2;
                    break;
                case "*":
                    result = num1 * num2;
                    break;
                case "/":
                    if (num2 != 0) {
                        result = num1 / num2;
                    } else {
                        System.out.println("Error: Cannot divide by zero.");
                        isValid = false;
                    }
                    break;
                default:
                    System.out.println("Error: Invalid operator.");
                    isValid = false;
            }

            if (isValid) {
                System.out.println("Result: " + result);
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter valid numbers.");
        } finally {
            scanner.nextLine(); // Consume newline
        }
    }

    /**
     * Manages a to-do list, with tasks stored in a text file.
     */
    private static void toDoListManager() {
        System.out.println("\n--- To-Do List Manager ---");
        try {
            ArrayList<String> tasks = loadTasks();
            boolean isManaging = true;
            while (isManaging) {
                System.out.println("\n1. View Tasks");
                System.out.println("2. Add Task");
                System.out.println("3. Delete Task");
                System.out.println("4. Back to Main Menu");
                System.out.print("Enter your choice: ");

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
                        saveTasks(tasks);
                        System.out.println("Task added successfully.");
                        break;
                    case 3:
                        viewTasks(tasks);
                        if (!tasks.isEmpty()) {
                            System.out.print("Enter the number of the task to delete: ");
                            int taskIndex = scanner.nextInt() - 1;
                            scanner.nextLine();
                            if (taskIndex >= 0 && taskIndex < tasks.size()) {
                                String removedTask = tasks.remove(taskIndex);
                                saveTasks(tasks);
                                System.out.println("Task '" + removedTask + "' deleted.");
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
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        }
    }

    /**
     * Loads tasks from the tasks.txt file.
     * @return an ArrayList of tasks.
     */
    private static ArrayList<String> loadTasks() {
        ArrayList<String> tasks = new ArrayList<>();
        File file = new File(TODO_FILE);
        if (file.exists()) {
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    tasks.add(line);
                }
            } catch (IOException e) {
                System.out.println("Error loading tasks: " + e.getMessage());
            }
        }
        return tasks;
    }

    /**
     * Saves tasks to the tasks.txt file.
     * @param tasks The ArrayList of tasks to save.
     */
    private static void saveTasks(ArrayList<String> tasks) {
        try (FileWriter writer = new FileWriter(TODO_FILE)) {
            for (String task : tasks) {
                writer.write(task + System.lineSeparator());
            }
        } catch (IOException e) {
            System.out.println("Error saving tasks: " + e.getMessage());
        }
    }

    /**
     * Displays all tasks in the list.
     * @param tasks The ArrayList of tasks to display.
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
     * Implements a countdown timer.
     */
    private static void countdownTimer() {
        System.out.println("\n--- Countdown Timer ---");
        try {
            System.out.print("Enter time in seconds: ");
            int seconds = scanner.nextInt();
            scanner.nextLine();

            while (seconds >= 0) {
                System.out.print(seconds + "...\r");
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                    System.out.println("Timer interrupted.");
                    return;
                }
                seconds--;
            }
            System.out.println("\nTime's up!");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a whole number.");
            scanner.nextLine();
        }
    }

    /**
     * Implements a unit converter.
     */
    private static void unitConverter() {
        System.out.println("\n--- Unit Converter ---");
        try {
            System.out.println("1. Kilometers to Miles");
            System.out.println("2. Celsius to Fahrenheit");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            switch (choice) {
                case 1:
                    System.out.print("Enter kilometers: ");
                    double km = scanner.nextDouble();
                    double miles = km * 0.621371;
                    System.out.printf("%.2f km is equal to %.2f miles.\n", km, miles);
                    break;
                case 2:
                    System.out.print("Enter Celsius: ");
                    double celsius = scanner.nextDouble();
                    double fahrenheit = (celsius * 9/5) + 32;
                    System.out.printf("%.2f°C is equal to %.2f°F.\n", celsius, fahrenheit);
                    break;
                default:
                    System.out.println("Invalid choice.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } finally {
            scanner.nextLine();
        }
    }

    /**
     * Implements a number guessing game.
     */
    private static void numberGuessingGame() {
        System.out.println("\n--- Number Guessing Game ---");
        Random random = new Random();
        int numberToGuess = random.nextInt(100) + 1; // Number between 1 and 100
        int numberOfTries = 0;
        int guess;
        boolean hasGuessedCorrectly = false;

        System.out.println("I have a randomly generated number between 1 and 100.");
        System.out.println("Can you guess it?");

        while (!hasGuessedCorrectly) {
            System.out.print("Enter your guess: ");
            try {
                guess = scanner.nextInt();
                numberOfTries++;

                if (guess < 1 || guess > 100) {
                    System.out.println("Your guess is out of range. Try again.");
                } else if (guess < numberToGuess) {
                    System.out.println("Too low! Try again.");
                } else if (guess > numberToGuess) {
                    System.out.println("Too high! Try again.");
                } else {
                    System.out.println("Congratulations! You guessed the number in " + numberOfTries + " tries.");
                    hasGuessedCorrectly = true;
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a whole number.");
                scanner.nextLine();
            }
        }
    }

    /**
     * Implements a simple quiz on Java topics.
     */
    private static void simpleQuiz() {
        System.out.println("\n--- Simple Java Quiz ---");

        // Define questions and answers
        String[] questions = {
                "What is the entry point for a Java application? a) main() b) start() c) run() d) go()",
                "Which keyword is used to declare a constant in Java? a) final b) const c) static d) constant",
                "What is the default value of a boolean variable? a) true b) false c) 0 d) null",
                "Which data type is used to create a variable that should store text? a) string b) char c) String d) text"
        };

        String[] answers = {"a", "a", "b", "c"};
        int score = 0;

        for (int i = 0; i < questions.length; i++) {
            System.out.println("\nQuestion " + (i + 1) + ": " + questions[i]);
            System.out.print("Your answer (a, b, c, or d): ");
            String userAnswer = scanner.nextLine().trim().toLowerCase();

            if (userAnswer.equals(answers[i])) {
                System.out.println("Correct!");
                score++;
            } else {
                System.out.println("Incorrect. The correct answer is " + answers[i] + ".");
            }
        }

        System.out.println("\nQuiz finished. Your final score is: " + score + " out of " + questions.length);
    }

    /**
     * Generates a random, strong password.
     */
    private static void passwordGenerator() {
        System.out.println("\n--- Password Generator ---");
        System.out.print("Enter the desired password length (min 8): ");
        int length;
        try {
            length = scanner.nextInt();
            scanner.nextLine();
            if (length < 8) {
                System.out.println("Password length must be at least 8. Defaulting to 12.");
                length = 12;
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Defaulting to a password length of 12.");
            scanner.nextLine();
            length = 12;
        }

        String lowerCaseChars = "abcdefghijklmnopqrstuvwxyz";
        String upperCaseChars = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
        String numbers = "0123456789";
        String symbols = "!@#$%^&*()-_=+[]{}|;:,.<>?";
        String allChars = lowerCaseChars + upperCaseChars + numbers + symbols;

        Random random = new Random();
        StringBuilder password = new StringBuilder();

        // Ensure at least one of each character type is included
        password.append(lowerCaseChars.charAt(random.nextInt(lowerCaseChars.length())));
        password.append(upperCaseChars.charAt(random.nextInt(upperCaseChars.length())));
        password.append(numbers.charAt(random.nextInt(numbers.length())));
        password.append(symbols.charAt(random.nextInt(symbols.length())));

        // Fill the rest of the password with random characters
        for (int i = 4; i < length; i++) {
            password.append(allChars.charAt(random.nextInt(allChars.length())));
        }

        // Shuffle the password to ensure randomness
        String shuffledPassword = shuffleString(password.toString());

        System.out.println("Generated Password: " + shuffledPassword);
    }

    /**
     * Shuffles the characters of a string.
     * @param input The string to shuffle.
     * @return The shuffled string.
     */
    private static String shuffleString(String input) {
        ArrayList<Character> chars = new ArrayList<>();
        for (char c : input.toCharArray()) {
            chars.add(c);
        }
        StringBuilder output = new StringBuilder();
        while (chars.size() != 0) {
            int randomIndex = new Random().nextInt(chars.size());
            output.append(chars.remove(randomIndex));
        }
        return output.toString();
    }

    /**
     * Implements a simple currency converter.
     */
    private static void currencyConverter() {
        System.out.println("\n--- Currency Converter ---");
        try {
            // Using a HashMap for a more flexible and scalable approach
            HashMap<String, Double> rates = new HashMap<>();
            rates.put("USD_to_EUR", 0.93);
            rates.put("EUR_to_USD", 1.07);
            rates.put("USD_to_INR", 83.43);
            rates.put("INR_to_USD", 0.012);
            rates.put("EUR_to_INR", 89.69);
            rates.put("INR_to_EUR", 0.011);

            System.out.println("Available conversions:");
            System.out.println("1. USD to EUR");
            System.out.println("2. EUR to USD");
            System.out.println("3. USD to INR");
            System.out.println("4. INR to USD");
            System.out.println("5. EUR to INR");
            System.out.println("6. INR to EUR");
            System.out.print("Enter your choice: ");
            int choice = scanner.nextInt();
            scanner.nextLine();

            String conversionKey = "";
            String fromCurrency = "";
            String toCurrency = "";

            switch(choice) {
                case 1:
                    conversionKey = "USD_to_EUR";
                    fromCurrency = "USD";
                    toCurrency = "EUR";
                    break;
                case 2:
                    conversionKey = "EUR_to_USD";
                    fromCurrency = "EUR";
                    toCurrency = "USD";
                    break;
                case 3:
                    conversionKey = "USD_to_INR";
                    fromCurrency = "USD";
                    toCurrency = "INR";
                    break;
                case 4:
                    conversionKey = "INR_to_USD";
                    fromCurrency = "INR";
                    toCurrency = "USD";
                    break;
                case 5:
                    conversionKey = "EUR_to_INR";
                    fromCurrency = "EUR";
                    toCurrency = "INR";
                    break;
                case 6:
                    conversionKey = "INR_to_EUR";
                    fromCurrency = "INR";
                    toCurrency = "EUR";
                    break;
                default:
                    System.out.println("Invalid choice. Returning to main menu.");
                    return;
            }

            System.out.print("Enter amount in " + fromCurrency + ": ");
            double amount = scanner.nextDouble();
            double result = amount * rates.get(conversionKey);
            System.out.printf("%.2f %s is equal to %.2f %s.\n", amount, fromCurrency, result, toCurrency);

        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
        } finally {
            scanner.nextLine();
        }
    }
}
