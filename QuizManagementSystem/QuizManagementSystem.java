import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.Arrays;
import java.time.Year;
import java.text.DecimalFormat;
import java.util.UUID;
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Collections;

/**
 * A comprehensive command-line Quiz Management System in Java.
 * This system provides separate access modes for administrators and users,
 * handles user authentication, manages quizzes, and persists all data to
 * text files for easy storage.
 */
public class QuizManagementSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String QUIZZES_DATA_FILE = "quizzes.txt";
    private static final String RESULTS_DATA_FILE = "results.txt";
    private static final String ADMIN_DATA_FILE = "admins.txt";
    private static final String USER_DATA_FILE = "users.txt";
    private static final Random random = new Random();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        // Main application loop
        boolean isRunning = true;
        createInitialAdmin(); // Ensure at least one admin exists for initial login
        while (isRunning) {
            displayMainMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        adminLogin();
                        break;
                    case 2:
                        userLogin();
                        break;
                    case 3:
                        handleNewRegistration();
                        break;
                    case 4:
                        System.out.println("Thank you for using the Quiz Management System. Goodbye!");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }
        }
        scanner.close();
    }

    /**
     * Creates an initial default admin account if the admin data file is empty.
     */
    private static void createInitialAdmin() {
        try {
            File adminFile = new File(ADMIN_DATA_FILE);
            if (!adminFile.exists() || adminFile.length() == 0) {
                try (BufferedWriter writer = new BufferedWriter(new FileWriter(adminFile))) {
                    writer.write("admin,admin");
                    System.out.println("Initial admin account created: username 'admin', password 'admin'.");
                }
            }
        } catch (IOException e) {
            System.err.println("Error creating initial admin account.");
        }
    }

    /**
     * Displays the main menu for selecting admin or user mode.
     */
    private static void displayMainMenu() {
        System.out.println("\n------------------------------------");
        System.out.println("    Quiz Management System");
        System.out.println("------------------------------------");
        System.out.println("1. Login as Admin");
        System.out.println("2. Login as User");
        System.out.println("3. Register New Account");
        System.out.println("4. Exit");
        System.out.println("------------------------------------");
        System.out.print("Enter your choice: ");
    }

    /**
     * Handles the new registration menu, allowing users to register as a new user or admin.
     */
    private static void handleNewRegistration() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- New Registration ---");
            System.out.println("1. Register as a new User");
            System.out.println("2. Register as a new Admin");
            System.out.println("3. Go back to Main Menu");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        createNewAccount(USER_DATA_FILE);
                        break;
                    case 2:
                        createNewAccount(ADMIN_DATA_FILE);
                        break;
                    case 3:
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    // --- Authentication and Account Creation ---

    /**
     * Creates a new user or admin account and saves it to the specified file.
     *
     * @param filename The file to save the new account to (USER_DATA_FILE or ADMIN_DATA_FILE).
     */
    private static void createNewAccount(String filename) {
        String accountType = filename.equals(ADMIN_DATA_FILE) ? "Admin" : "User";
        System.out.println("\n--- Create New " + accountType + " Account ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            List<String> accounts = loadDataFromFile(filename);
            String newRecord = username + "," + password;
            if (accounts.stream().anyMatch(acc -> acc.split(",")[0].equals(username))) {
                System.out.println("This username already exists.");
                return;
            }
            accounts.add(newRecord);
            saveDataToFile(accounts, filename);
            System.out.println("New " + accountType + " account created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating a new account.");
        }
    }

    /**
     * Handles the administrator login and menu.
     */
    private static void adminLogin() {
        System.out.println("\n--- Admin Login ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            List<String> admins = loadDataFromFile(ADMIN_DATA_FILE);
            if (admins.contains(username + "," + password)) {
                System.out.println("Admin login successful!");
                adminMenu();
            } else {
                System.out.println("Invalid admin credentials. Access denied.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the admin data file.");
        }
    }

    /**
     * Handles the general user login and menu.
     */
    private static void userLogin() {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            List<String> users = loadDataFromFile(USER_DATA_FILE);
            if (users.contains(username + "," + password)) {
                System.out.println("User login successful!");
                userMenu(username); // Pass the username for personalized info
            } else {
                System.out.println("Invalid user credentials. Access denied.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the user data file.");
        }
    }

    // --- Admin Functionalities ---

    /**
     * Displays and manages the admin menu options.
     */
    private static void adminMenu() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Manage Quizzes");
            System.out.println("2. View All Admins");
            System.out.println("3. View All Users");
            System.out.println("4. View All Quiz History/Results");
            System.out.println("5. View Quiz History by Batch Year");
            System.out.println("6. Logout");
            System.out.println("--------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        manageQuizzes();
                        break;
                    case 2:
                        viewAllAccounts(ADMIN_DATA_FILE, "Admin");
                        break;
                    case 3:
                        viewAllAccounts(USER_DATA_FILE, "User");
                        break;
                    case 4:
                        viewAllQuizHistory();
                        break;
                    case 5:
                        viewHistoryBatchYears();
                        break;
                    case 6:
                        System.out.println("Logging out from Admin account.");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    /**
     * Manages all quiz-related operations from the admin menu.
     */
    private static void manageQuizzes() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Quizzes ---");
            System.out.println("1. Add a New Quiz");
            System.out.println("2. View All Quizzes");
            System.out.println("3. View Questions of a Quiz");
            System.out.println("4. Back to Admin Menu");
            System.out.println("-------------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addNewQuiz();
                        break;
                    case 2:
                        viewAllQuizzes(true);
                        break;
                    case 3:
                        viewQuizQuestions();
                        break;
                    case 4:
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    /**
     * Allows admin to add a new quiz with its questions.
     */
    private static void addNewQuiz() {
        System.out.println("\n--- Add New Quiz ---");
        System.out.print("Enter quiz topic (e.g., Java, C, Python, SQL): ");
        String topic = scanner.nextLine();
        int numQuestions = 0;
        int durationMinutes = 0;
        try {
            System.out.print("Enter number of questions: ");
            numQuestions = scanner.nextInt();
            scanner.nextLine();
            System.out.print("Enter quiz duration in minutes: ");
            durationMinutes = scanner.nextInt();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid number. Please enter a number.");
            scanner.nextLine();
            return;
        }

        String quizId = UUID.randomUUID().toString();

        try {
            // Save quiz metadata
            List<String> quizzes = loadDataFromFile(QUIZZES_DATA_FILE);
            quizzes.add(quizId + "," + topic + "," + durationMinutes + "," + numQuestions);
            saveDataToFile(quizzes, QUIZZES_DATA_FILE);

            // Save questions to a separate file
            String questionsFile = "quiz_" + quizId + "_questions.txt";
            List<String> questions = new ArrayList<>();
            for (int i = 0; i < numQuestions; i++) {
                System.out.println("\n--- Adding Question " + (i + 1) + " ---");
                System.out.print("Enter question text: ");
                String questionText = scanner.nextLine();
                System.out.print("Enter option 1: ");
                String opt1 = scanner.nextLine();
                System.out.print("Enter option 2: ");
                String opt2 = scanner.nextLine();
                System.out.print("Enter option 3: ");
                String opt3 = scanner.nextLine();
                System.out.print("Enter option 4: ");
                String opt4 = scanner.nextLine();
                System.out.print("Enter correct option number (1-4): ");
                int correctOpt = scanner.nextInt();
                scanner.nextLine();

                questions.add(questionText + "," + opt1 + "," + opt2 + "," + opt3 + "," + opt4 + "," + correctOpt);
            }
            saveDataToFile(questions, questionsFile);
            System.out.println("Quiz '" + topic + "' added successfully! Quiz ID: " + quizId);
        } catch (IOException e) {
            System.out.println("An error occurred while adding the quiz.");
        }
    }

    /**
     * Views all quizzes in a tabular format.
     * @param isAdmin A flag to control whether to show the quiz ID (for admins).
     */
    private static void viewAllQuizzes(boolean isAdmin) {
        System.out.println("\n--- All Quizzes ---");
        try {
            List<String> quizzes = loadDataFromFile(QUIZZES_DATA_FILE);
            if (quizzes.isEmpty()) {
                System.out.println("No quizzes found.");
                return;
            }

            if (isAdmin) {
                System.out.printf("%-40s%-15s%-10s%-15s\n", "Quiz ID", "Topic", "Duration", "Questions");
                System.out.println("-----------------------------------------------------------------------------");
                for (String quizData : quizzes) {
                    String[] parts = quizData.split(",");
                    System.out.printf("%-40s%-15s%-10s%-15s\n", parts[0], parts[1], parts[2] + " min", parts[3]);
                }
            } else {
                System.out.printf("%-15s%-10s%-15s\n", "Topic", "Duration", "Questions");
                System.out.println("--------------------------------------------------");
                for (String quizData : quizzes) {
                    String[] parts = quizData.split(",");
                    System.out.printf("%-15s%-10s%-15s\n", parts[1], parts[2] + " min", parts[3]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing quizzes.");
        }
    }

    /**
     * Allows admin to view all questions for a specific quiz.
     */
    private static void viewQuizQuestions() {
        viewAllQuizzes(true);
        System.out.print("Enter the Quiz ID to view its questions: ");
        String quizId = scanner.nextLine();

        try {
            String questionsFile = "quiz_" + quizId + "_questions.txt";
            List<String> questions = loadDataFromFile(questionsFile);

            if (questions.isEmpty()) {
                System.out.println("No questions found for this quiz ID.");
                return;
            }

            System.out.println("\n--- Questions for Quiz ID: " + quizId + " ---");
            for (String questionData : questions) {
                String[] parts = questionData.split(",");
                System.out.println("\nQuestion: " + parts[0]);
                System.out.println("1. " + parts[1]);
                System.out.println("2. " + parts[2]);
                System.out.println("3. " + parts[3]);
                System.out.println("4. " + parts[4]);
                System.out.println("Correct Answer: Option " + parts[5]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing quiz questions. Check the Quiz ID.");
        }
    }

    /**
     * Displays all quiz results in a tabular format (for Admin).
     */
    private static void viewAllQuizHistory() {
        System.out.println("\n--- All Quiz Results History ---");
        try {
            List<String> records = loadDataFromFile(RESULTS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No results found.");
                return;
            }

            System.out.printf("%-20s%-15s%-40s%-10s%-15s\n", "Result ID", "User", "Quiz ID", "Score", "Date");
            System.out.println("-------------------------------------------------------------------------------------------------");
            for (String resultData : records) {
                String[] parts = resultData.split(",");
                if (parts.length >= 5) {
                    System.out.printf("%-20s%-15s%-40s%-10s%-15s\n", parts[0], parts[1], parts[2], parts[3] + "/" + parts[4], parts[5]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing all history.");
        }
    }

    /**
     * Displays a summary of results grouped by their result year.
     */
    private static void viewHistoryBatchYears() {
        System.out.println("\n--- Quiz History Batch Years ---");
        try {
            List<String> records = loadDataFromFile(RESULTS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No results found.");
                return;
            }

            Map<String, Long> yearCounts = records.stream()
                    .map(record -> record.split(",")[0]) // Get the result ID
                    .map(id -> id.substring(id.length() - 6, id.length() - 2)) // Extract the year part
                    .collect(Collectors.groupingBy(resultYear -> resultYear, Collectors.counting()));

            System.out.printf("%-15s%-15s\n", "Result Year", "Total Results");
            System.out.println("------------------------------");
            yearCounts.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> System.out.printf("%-15s%-15d\n", entry.getKey(), entry.getValue()));
        } catch (IOException e) {
            System.out.println("An error occurred while retrieving year data.");
        }
    }

    /**
     * Displays a list of all accounts from the specified file.
     *
     * @param filename The file to read accounts from (ADMIN_DATA_FILE or USER_DATA_FILE).
     * @param accountType The type of account to display ("Admin" or "User").
     */
    private static void viewAllAccounts(String filename, String accountType) {
        System.out.println("\n--- All " + accountType + "s ---");
        try {
            List<String> accounts = loadDataFromFile(filename);
            if (accounts.isEmpty()) {
                System.out.println("No " + accountType + " accounts found.");
                return;
            }
            System.out.printf("%-20s%-20s\n", "Username", "Password");
            System.out.println("----------------------------------------");
            for (String account : accounts) {
                String[] parts = account.split(",");
                System.out.printf("%-20s%-20s\n", parts[0], parts[1]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while retrieving " + accountType + " data.");
        }
    }

    // --- User Functionalities ---

    /**
     * Displays and manages the user menu options.
     * @param currentUser The username of the currently logged-in user.
     */
    private static void userMenu(String currentUser) {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. Take a Quiz");
            System.out.println("2. View My Results");
            System.out.println("3. Logout");
            System.out.println("-------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        takeQuiz(currentUser);
                        break;
                    case 2:
                        viewMyResults(currentUser);
                        break;
                    case 3:
                        System.out.println("Logging out from your account.");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    /**
     * Allows a user to take an available quiz.
     * @param currentUser The username of the user taking the quiz.
     */
    private static void takeQuiz(String currentUser) {
        System.out.println("\n--- Take a Quiz ---");
        viewAllQuizzes(false);

        System.out.print("Enter the quiz topic to take: ");
        String topic = scanner.nextLine();

        try {
            List<String> allQuizzes = loadDataFromFile(QUIZZES_DATA_FILE);
            String quizData = allQuizzes.stream()
                    .filter(q -> q.split(",")[1].equalsIgnoreCase(topic))
                    .findFirst()
                    .orElse(null);

            if (quizData == null) {
                System.out.println("Quiz not found.");
                return;
            }

            String[] quizParts = quizData.split(",");
            String quizId = quizParts[0];
            int duration = Integer.parseInt(quizParts[2]);

            System.out.println("Starting quiz: " + topic + " | Duration: " + duration + " minutes");
            System.out.println("Press Enter to begin...");
            scanner.nextLine(); // Wait for user to start

            String questionsFile = "quiz_" + quizId + "_questions.txt";
            List<String> questions = loadDataFromFile(questionsFile);

            int score = 0;
            for (String questionData : questions) {
                String[] parts = questionData.split(",");
                System.out.println("\nQuestion: " + parts[0]);
                System.out.println("1. " + parts[1]);
                System.out.println("2. " + parts[2]);
                System.out.println("3. " + parts[3]);
                System.out.println("4. " + parts[4]);

                System.out.print("Your answer (1-4): ");
                try {
                    int userAnswer = scanner.nextInt();
                    scanner.nextLine();
                    if (userAnswer == Integer.parseInt(parts[5])) {
                        score++;
                        System.out.println("Correct!");
                    } else {
                        System.out.println("Incorrect. The correct answer was option " + parts[5]);
                    }
                } catch (InputMismatchException e) {
                    System.out.println("Invalid input. Answer ignored.");
                    scanner.nextLine();
                }
            }

            // Save the result
            saveResult(currentUser, quizId, score, questions.size());

            System.out.println("\nQuiz completed! Your final score is: " + score + " out of " + questions.size());

        } catch (IOException e) {
            System.out.println("An error occurred while trying to take the quiz.");
        } catch (NumberFormatException e) {
            System.out.println("Error parsing quiz data.");
        }
    }

    /**
     * Saves a quiz result to the results data file.
     * @param username The user who took the quiz.
     * @param quizId The ID of the quiz taken.
     * @param score The user's score.
     * @param totalQuestions The total number of questions.
     * @throws IOException
     */
    private static void saveResult(String username, String quizId, int score, int totalQuestions) throws IOException {
        String resultId = generateUniqueResultId(username);
        String date = dateFormat.format(new Date());
        String resultRecord = resultId + "," + username + "," + quizId + "," + score + "," + totalQuestions + "," + date;

        List<String> results = loadDataFromFile(RESULTS_DATA_FILE);
        results.add(resultRecord);
        saveDataToFile(results, RESULTS_DATA_FILE);
    }

    /**
     * Displays a logged-in user's personal quiz results.
     * @param currentUser The username of the user.
     */
    private static void viewMyResults(String currentUser) {
        System.out.println("\n--- My Quiz Results ---");
        try {
            List<String> resultRecords = loadDataFromFile(RESULTS_DATA_FILE);
            List<String> userResults = resultRecords.stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .collect(Collectors.toList());

            if (userResults.isEmpty()) {
                System.out.println("You have no results recorded yet.");
                return;
            }

            System.out.printf("%-20s%-40s%-10s%-15s\n", "Result ID", "Quiz ID", "Score", "Date");
            System.out.println("-----------------------------------------------------------------------------------");
            for (String resultData : userResults) {
                String[] parts = resultData.split(",");
                if (parts.length >= 5) {
                    System.out.printf("%-20s%-40s%-10s%-15s\n", parts[0], parts[2], parts[3] + "/" + parts[4], parts[5]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing your results.");
        }
    }

    // --- File & Helper Methods ---

    /**
     * Generates a unique result ID based on the required format.
     * Format: 4 letters of username + current year + random 2 digits.
     *
     * @param name The user's username.
     * @return A unique result ID as a String.
     * @throws IOException if there's an issue reading the data file.
     */
    private static String generateUniqueResultId(String name) throws IOException {
        String newId;
        boolean isUnique;
        int currentYear = Year.now().getValue();
        do {
            // Get the first 4 letters of the name, handling names shorter than 4 characters
            String namePart = name.length() > 3 ? name.substring(0, 4).toUpperCase() : (name + "XXXX").substring(0, 4).toUpperCase();
            String yearPart = String.valueOf(currentYear);
            String randomPart = String.format("%02d", random.nextInt(100)); // Generate a random 2-digit number with leading zero
            newId = namePart + yearPart + randomPart;
            isUnique = isIdUnique(newId, RESULTS_DATA_FILE);
        } while (!isUnique);
        return newId;
    }

    /**
     * Checks if an ID is unique by looking it up in the specified data file.
     *
     * @param id The ID to check for uniqueness.
     * @param filename The file to search within.
     * @return true if the ID is unique, false otherwise.
     * @throws IOException if there's an issue reading the data file.
     */
    private static boolean isIdUnique(String id, String filename) throws IOException {
        List<String> records = loadDataFromFile(filename);
        for (String recordData : records) {
            if (recordData.startsWith(id + ",")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Loads all records from a specified file into a List of strings.
     * Creates the file if it does not exist.
     *
     * @param filename The name of the file to read from.
     * @return A List of strings, where each string is a record.
     * @throws IOException if there's an issue reading the file.
     */
    private static List<String> loadDataFromFile(String filename) throws IOException {
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
            return new ArrayList<>();
        }
        List<String> records = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                records.add(line);
            }
        }
        return records;
    }

    /**
     * Saves a list of records to a file, overwriting existing content.
     *
     * @param records The List of strings to save to the file.
     * @param filename The name of the file to write to.
     * @throws IOException if there's an issue writing to the file.
     */
    private static void saveDataToFile(List<String> records, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String recordData : records) {
                writer.write(recordData);
                writer.newLine();
            }
        }
    }
}
