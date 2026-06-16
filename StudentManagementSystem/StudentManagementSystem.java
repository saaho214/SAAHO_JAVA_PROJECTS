import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.LinkedHashMap;

/**
 * A comprehensive command-line Student Record Management System in Java.
 * It features a menu-driven interface with separate modes for administration and students.
 * All data is handled through file I/O for persistence.
 */
public class StudentManagementSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String STUDENT_DATA_FILE = "student_data.txt";
    private static final String ADMIN_DATA_FILE = "admin_data.txt";
    private static final Random random = new Random();

    public static void main(String[] args) {
        // Main application loop
        boolean isRunning = true;
        createInitialAdmin();
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
                        studentLogin();
                        break;
                    case 3:
                        handleNewRegistration();
                        break;
                    case 4:
                        System.out.println("Thank you for using the Student Management System. Goodbye!");
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
     * Displays the main menu for selecting admin or student mode.
     */
    private static void displayMainMenu() {
        System.out.println("\n------------------------------------");
        System.out.println("  Student Record Management System");
        System.out.println("------------------------------------");
        System.out.println("1. Login as Admin");
        System.out.println("2. Login as Student");
        System.out.println("3. Register New Account");
        System.out.println("4. Exit");
        System.out.println("------------------------------------");
        System.out.print("Enter your choice: ");
    }

    /**
     * Handles the new registration menu, allowing users to register as a student or admin.
     */
    private static void handleNewRegistration() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- New Registration ---");
            System.out.println("1. Register as a new Student");
            System.out.println("2. Register as a new Admin");
            System.out.println("3. Go back to Main Menu");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        createStudentAccount();
                        break;
                    case 2:
                        createNewAdmin();
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

    // --- Admin Functionalities ---

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
     * Displays and manages the admin menu options.
     */
    private static void adminMenu() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Create New Student Account");
            System.out.println("2. View All Student Records");
            System.out.println("3. Delete Student Record");
            System.out.println("4. Search for a Student");
            System.out.println("5. View Student Batches");
            System.out.println("6. Create New Admin Account");
            System.out.println("7. Logout");
            System.out.println("--------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        createStudentAccount();
                        break;
                    case 2:
                        viewAllStudentRecords();
                        break;
                    case 3:
                        deleteStudentRecord();
                        break;
                    case 4:
                        searchStudentRecord();
                        break;
                    case 5:
                        viewStudentBatches();
                        break;
                    case 6:
                        createNewAdmin();
                        break;
                    case 7:
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
     * Creates a new student account and saves it to the student data file.
     * This method is called from both the admin menu and the new registration menu.
     */
    private static void createStudentAccount() {
        System.out.println("\n--- Create New Student Account ---");
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        System.out.print("Enter student PIN (4 digits): ");
        String pin = scanner.nextLine();
        System.out.print("Enter student's batch year (e.g., 2023): ");
        String batchYear = scanner.nextLine();

        // Simple validation for PIN
        if (pin.length() != 4) {
            System.out.println("PIN must be 4 digits. Account creation failed.");
            return;
        }

        try {
            String studentId = generateUniqueId(STUDENT_DATA_FILE, "student", name, batchYear);
            // Format: studentId,pin,name,batchYear
            String newRecord = studentId + "," + pin + "," + name + "," + batchYear;
            List<String> studentRecords = loadDataFromFile(STUDENT_DATA_FILE);
            studentRecords.add(newRecord);
            saveDataToFile(studentRecords, STUDENT_DATA_FILE);
            System.out.println("Student account created successfully!");
            System.out.println("Student ID: " + studentId);
        } catch (IOException e) {
            System.out.println("An error occurred during account creation.");
        }
    }

    /**
     * Deletes a student record from the data file.
     */
    private static void deleteStudentRecord() {
        System.out.println("\n--- Delete Student Record ---");
        System.out.print("Enter student ID to delete: ");
        String studentId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(STUDENT_DATA_FILE);
            boolean removed = records.removeIf(record -> record.startsWith(studentId + ","));
            if (removed) {
                saveDataToFile(records, STUDENT_DATA_FILE);
                System.out.println("Student record for ID " + studentId + " deleted successfully.");
            } else {
                System.out.println("Student record not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while deleting the record.");
        }
    }

    /**
     * Displays all student records in a tabular format.
     */
    private static void viewAllStudentRecords() {
        System.out.println("\n--- All Student Records ---");
        try {
            List<String> records = loadDataFromFile(STUDENT_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No student records found.");
                return;
            }

            // Print table header
            System.out.printf("%-15s%-10s%-25s%-15s\n", "Student ID", "PIN", "Name", "Batch Year");
            System.out.println("------------------------------------------------------------------");

            for (String recordData : records) {
                String[] parts = recordData.split(",");
                String studentId = parts[0];
                String pin = parts[1];
                String name = parts[2];
                String batchYear = parts[3];
                System.out.printf("%-15s%-10s%-25s%-15s\n", studentId, pin, name, batchYear);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing records.");
        }
    }

    /**
     * Searches for a specific student by student ID.
     */
    private static void searchStudentRecord() {
        System.out.println("\n--- Search Student Record ---");
        System.out.print("Enter student ID to search: ");
        String studentId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(STUDENT_DATA_FILE);
            for (String recordData : records) {
                if (recordData.startsWith(studentId + ",")) {
                    String[] parts = recordData.split(",");
                    System.out.println("\nStudent Found:");
                    System.out.printf("%-15s%-10s%-25s%-15s\n", "Student ID", "PIN", "Name", "Batch Year");
                    System.out.println("------------------------------------------------------------------");
                    System.out.printf("%-15s%-10s%-25s%-15s\n", parts[0], parts[1], parts[2], parts[3]);
                    return;
                }
            }
            System.out.println("Student record not found.");
        } catch (IOException e) {
            System.out.println("An error occurred while searching for the record.");
        }
    }

    /**
     * Displays a summary of students grouped by their batch year.
     */
    private static void viewStudentBatches() {
        System.out.println("\n--- Student Batches ---");
        try {
            List<String> records = loadDataFromFile(STUDENT_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No student records found.");
                return;
            }

            Map<String, Long> batchCounts = records.stream()
                    .map(record -> record.split(",")[3])
                    .collect(Collectors.groupingBy(batchYear -> batchYear, Collectors.counting()));

            System.out.printf("%-15s%-15s\n", "Batch Year", "Total Students");
            System.out.println("------------------------------");
            batchCounts.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> System.out.printf("%-15s%-15d\n", entry.getKey(), entry.getValue()));
        } catch (IOException e) {
            System.out.println("An error occurred while retrieving batch data.");
        }
    }

    /**
     * Creates a new admin account. This method is called from both the admin menu and the new registration menu.
     */
    private static void createNewAdmin() {
        System.out.println("\n--- Create New Admin Account ---");
        System.out.print("Enter new admin username: ");
        String username = scanner.nextLine();
        System.out.print("Enter new admin password: ");
        String password = scanner.nextLine();

        try {
            List<String> admins = loadDataFromFile(ADMIN_DATA_FILE);
            String newAdminRecord = username + "," + password;
            if (admins.contains(newAdminRecord)) {
                System.out.println("This admin account already exists.");
                return;
            }
            admins.add(newAdminRecord);
            saveDataToFile(admins, ADMIN_DATA_FILE);
            System.out.println("New admin account created successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while creating a new admin.");
        }
    }

    // --- Student Functionalities ---

    /**
     * Handles student login and directs to the student menu upon successful authentication.
     */
    private static void studentLogin() {
        System.out.println("\n--- Student Login ---");
        System.out.print("Enter your student ID: ");
        String studentId = scanner.nextLine();
        System.out.print("Enter your PIN: ");
        String pin = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(STUDENT_DATA_FILE);
            for (String recordData : records) {
                if (recordData.startsWith(studentId + "," + pin)) {
                    System.out.println("Login successful!");
                    studentMenu(studentId);
                    return;
                }
            }
            System.out.println("Invalid student ID or PIN. Please try again.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the student data file.");
        }
    }

    /**
     * Displays and manages the student menu options.
     *
     * @param studentId The ID of the logged-in student.
     */
    private static void studentMenu(String studentId) {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1. View My Record");
            System.out.println("2. Update My PIN");
            System.out.println("3. Logout");
            System.out.println("-------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        viewMyRecord(studentId);
                        break;
                    case 2:
                        updateMyPin(studentId);
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
     * Displays the record of the currently logged-in student.
     *
     * @param studentId The ID of the student.
     */
    private static void viewMyRecord(String studentId) {
        System.out.println("\n--- My Student Record ---");
        try {
            List<String> records = loadDataFromFile(STUDENT_DATA_FILE);
            for (String recordData : records) {
                if (recordData.startsWith(studentId + ",")) {
                    String[] parts = recordData.split(",");
                    System.out.println("Student ID: " + parts[0]);
                    System.out.println("Name: " + parts[2]);
                    System.out.println("Batch Year: " + parts[3]);
                    return;
                }
            }
            System.out.println("Your record could not be found.");
        } catch (IOException e) {
            System.out.println("An error occurred while retrieving your record.");
        }
    }

    /**
     * Allows the logged-in student to update their PIN.
     *
     * @param studentId The ID of the student.
     */
    private static void updateMyPin(String studentId) {
        System.out.println("\n--- Update My PIN ---");
        System.out.print("Enter your new PIN (4 digits): ");
        String newPin = scanner.nextLine();

        if (newPin.length() != 4) {
            System.out.println("PIN must be 4 digits. Update failed.");
            return;
        }

        try {
            List<String> records = loadDataFromFile(STUDENT_DATA_FILE);
            for (int i = 0; i < records.size(); i++) {
                if (records.get(i).startsWith(studentId + ",")) {
                    String[] parts = records.get(i).split(",");
                    String updatedRecord = parts[0] + "," + newPin + "," + parts[2] + "," + parts[3];
                    records.set(i, updatedRecord);
                    saveDataToFile(records, STUDENT_DATA_FILE);
                    System.out.println("Your PIN has been updated successfully.");
                    return;
                }
            }
            System.out.println("Your record could not be found.");
        } catch (IOException e) {
            System.out.println("An error occurred while updating your PIN.");
        }
    }

    // --- File & Helper Methods ---

    /**
     * Generates a unique ID based on type. For students, the ID is now a
     * combination of the first 4 letters of their name, the batch year, and a random 2-digit number.
     *
     * @param filename The file to check for uniqueness.
     * @param type The type of ID to generate (e.g., "student", "admin").
     * @param name The student's name (only used for student ID generation).
     * @param batchYear The student's batch year (only used for student ID generation).
     * @return A unique ID as a String.
     * @throws IOException if there's an issue reading the data file.
     */
    private static String generateUniqueId(String filename, String type, String name, String batchYear) throws IOException {
        String newId;
        boolean isUnique;
        do {
            if ("student".equals(type)) {
                // Get the first 4 letters of the name, handling names shorter than 4 characters
                String namePart = name.length() > 3 ? name.substring(0, 4) : name.toUpperCase().substring(0, Math.min(name.length(), 4));
                String randomPart = String.format("%02d", random.nextInt(100)); // Generate a random 2-digit number with leading zero
                newId = namePart + batchYear + randomPart;
            } else {
                newId = "admin-" + (1000 + random.nextInt(9000));
            }
            isUnique = isIdUnique(newId, filename);
        } while (!isUnique);
        return newId;
    }

    /**
     * Overloaded method for existing calls without name and batchYear.
     * @param filename
     * @param type
     * @return
     * @throws IOException
     */
    private static String generateUniqueId(String filename, String type) throws IOException {
        // This method will now only be used for the admin ID generation
        return generateUniqueId(filename, type, "", "");
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
