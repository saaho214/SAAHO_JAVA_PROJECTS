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
import java.util.UUID;
import java.time.Year;
import java.text.DecimalFormat;

/**
 * A comprehensive command-line Clinic Patient Management System in Java.
 * This system provides separate access modes for administrators and patients,
 * handles user authentication, manages patient data, history, and bills,
 * and persists information to text files.
 */
public class ClinicSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String PATIENTS_DATA_FILE = "patients.txt";
    private static final String HISTORY_DATA_FILE = "patient_history.txt";
    private static final String EXPENSES_DATA_FILE = "expenses.txt";
    private static final String ADMIN_DATA_FILE = "admins.txt";
    private static final String USER_DATA_FILE = "users.txt";
    private static final Random random = new Random();
    private static final DecimalFormat df = new DecimalFormat("#.00");

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
                        System.out.println("Thank you for using the Clinic Patient Management System. Goodbye!");
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
        System.out.println("  Clinic Patient Management System");
        System.out.println("------------------------------------");
        System.out.println("1. Login as Admin");
        System.out.println("2. Login as User (Patient)");
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
            System.out.println("1. Register as a new User (Patient)");
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
            if (accounts.contains(newRecord)) {
                System.out.println("This account already exists.");
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
     * Handles the general user (patient) login and menu.
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
                userMenu(username); // Pass the username for personalized data
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
            System.out.println("1. Manage Patients");
            System.out.println("2. View All Admins");
            System.out.println("3. View All Users");
            System.out.println("4. View All Patient History");
            System.out.println("5. View Patient Batch Years");
            System.out.println("6. Logout");
            System.out.println("--------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        managePatients();
                        break;
                    case 2:
                        viewAllAccounts(ADMIN_DATA_FILE, "Admin");
                        break;
                    case 3:
                        viewAllAccounts(USER_DATA_FILE, "User");
                        break;
                    case 4:
                        viewAllPatientHistory();
                        break;
                    case 5:
                        viewPatientBatchYears();
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
     * Manages all patient-related operations from the admin menu.
     */
    private static void managePatients() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Patients ---");
            System.out.println("1. Add a New Patient");
            System.out.println("2. View All Patients");
            System.out.println("3. Update a Patient's Details");
            System.out.println("4. Add a Patient History Record");
            System.out.println("5. View a Patient's History");
            System.out.println("6. Delete a Patient Record");
            System.out.println("7. Back to Admin Menu");
            System.out.println("-------------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addNewPatient();
                        break;
                    case 2:
                        viewAllPatients();
                        break;
                    case 3:
                        updatePatientDetails();
                        break;
                    case 4:
                        addPatientHistoryRecord();
                        break;
                    case 5:
                        viewPatientHistory();
                        break;
                    case 6:
                        deletePatientRecord();
                        break;
                    case 7:
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
     * Adds a new patient to the system.
     */
    private static void addNewPatient() {
        System.out.println("\n--- Add New Patient ---");
        System.out.print("Enter patient name: ");
        String name = scanner.nextLine();
        System.out.print("Enter date of birth (YYYY-MM-DD): ");
        String dob = scanner.nextLine();
        System.out.print("Enter contact number: ");
        String contact = scanner.nextLine();
        System.out.print("Enter last visit date (YYYY-MM-DD): ");
        String lastVisitDate = scanner.nextLine();

        try {
            // Format: patientId,name,dob,contact,lastVisitDate
            String patientId = generateUniquePatientId(name);
            String newPatient = patientId + "," + name + "," + dob + "," + contact + "," + lastVisitDate;
            List<String> records = loadDataFromFile(PATIENTS_DATA_FILE);
            records.add(newPatient);
            saveDataToFile(records, PATIENTS_DATA_FILE);
            System.out.println("New patient added successfully! Patient ID: " + patientId);
        } catch (IOException e) {
            System.out.println("An error occurred during patient addition.");
        }
    }

    /**
     * Adds a new history record for an existing patient.
     */
    private static void addPatientHistoryRecord() {
        System.out.println("\n--- Add Patient History Record ---");
        viewAllPatients();
        System.out.print("Enter Patient ID to add history for: ");
        String patientId = scanner.nextLine();

        try {
            List<String> patientRecords = loadDataFromFile(PATIENTS_DATA_FILE);
            boolean patientExists = patientRecords.stream().anyMatch(r -> r.split(",")[0].equals(patientId));

            if (!patientExists) {
                System.out.println("Patient with ID " + patientId + " not found.");
                return;
            }

            System.out.print("Enter date of visit (YYYY-MM-DD): ");
            String date = scanner.nextLine();
            System.out.print("Enter type of visit (e.g., Check-up, Consultation, Surgery): ");
            String visitType = scanner.nextLine();
            System.out.print("Enter notes/diagnosis: ");
            String notes = scanner.nextLine();
            System.out.print("Enter visit cost: ");
            String cost = scanner.nextLine();

            // Format: patientId,date,typeOfVisit,notes,cost
            String newHistoryRecord = patientId + "," + date + "," + visitType + "," + notes + "," + cost;
            List<String> historyRecords = loadDataFromFile(HISTORY_DATA_FILE);
            historyRecords.add(newHistoryRecord);
            saveDataToFile(historyRecords, HISTORY_DATA_FILE);
            System.out.println("Patient history record added successfully.");
        } catch (IOException e) {
            System.out.println("An error occurred while adding history record.");
        }
    }

    /**
     * Updates an existing patient's details.
     */
    private static void updatePatientDetails() {
        System.out.println("\n--- Update Patient Details ---");
        viewAllPatients();
        System.out.print("Enter Patient ID to update: ");
        String patientId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(PATIENTS_DATA_FILE);
            boolean found = false;
            for (int i = 0; i < records.size(); i++) {
                String record = records.get(i);
                String[] parts = record.split(",");

                if (parts[0].equals(patientId)) {
                    found = true;

                    System.out.println("Patient found. Current details:");
                    System.out.println("Name: " + parts[1]);
                    System.out.println("DOB: " + parts[2]);
                    System.out.println("Contact: " + parts[3]);
                    System.out.println("Last Visit: " + parts[4]);

                    System.out.print("Enter new patient name (or press Enter to keep current): ");
                    String newName = scanner.nextLine();
                    if (!newName.isEmpty()) parts[1] = newName;

                    System.out.print("Enter new date of birth (or press Enter to keep current): ");
                    String newDob = scanner.nextLine();
                    if (!newDob.isEmpty()) parts[2] = newDob;

                    System.out.print("Enter new contact number (or press Enter to keep current): ");
                    String newContact = scanner.nextLine();
                    if (!newContact.isEmpty()) parts[3] = newContact;

                    System.out.print("Enter new last visit date (or press Enter to keep current): ");
                    String newLastVisit = scanner.nextLine();
                    if (!newLastVisit.isEmpty()) parts[4] = newLastVisit;

                    records.set(i, String.join(",", parts));
                    saveDataToFile(records, PATIENTS_DATA_FILE);
                    System.out.println("Patient details updated successfully.");
                    break;
                }
            }
            if (!found) {
                System.out.println("Patient with ID " + patientId + " not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while updating the patient record.");
        }
    }

    /**
     * Deletes a patient record and all associated history/expenses from the data files.
     */
    private static void deletePatientRecord() {
        System.out.println("\n--- Delete Patient Record ---");
        viewAllPatients();
        System.out.print("Enter Patient ID to delete: ");
        String patientId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(PATIENTS_DATA_FILE);
            boolean removed = records.removeIf(record -> record.split(",")[0].equals(patientId));
            if (removed) {
                saveDataToFile(records, PATIENTS_DATA_FILE);
                System.out.println("Patient record for ID " + patientId + " deleted successfully.");

                // Also remove history and expenses
                List<String> historyRecords = loadDataFromFile(HISTORY_DATA_FILE);
                historyRecords.removeIf(record -> record.split(",")[0].equals(patientId));
                saveDataToFile(historyRecords, HISTORY_DATA_FILE);

                List<String> expenseRecords = loadDataFromFile(EXPENSES_DATA_FILE);
                expenseRecords.removeIf(record -> record.split(",")[0].equals(patientId));
                saveDataToFile(expenseRecords, EXPENSES_DATA_FILE);

                System.out.println("Associated history and expenses have also been removed.");
            } else {
                System.out.println("Patient record not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while deleting the record.");
        }
    }

    /**
     * Displays all patient records in a tabular format.
     */
    private static void viewAllPatients() {
        System.out.println("\n--- All Patients ---");
        try {
            List<String> records = loadDataFromFile(PATIENTS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No patient records found.");
                return;
            }

            // Print table header
            System.out.printf("%-15s%-25s%-15s%-20s%-15s\n", "Patient ID", "Name", "DOB", "Contact", "Last Visit");
            System.out.println("------------------------------------------------------------------------------------------");

            for (String recordData : records) {
                String[] parts = recordData.split(",");
                if (parts.length >= 5) {
                    System.out.printf("%-15s%-25s%-15s%-20s%-15s\n", parts[0], parts[1], parts[2], parts[3], parts[4]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing records.");
        }
    }

    /**
     * Displays a summary of all patient history records.
     */
    private static void viewAllPatientHistory() {
        System.out.println("\n--- All Patient History Records ---");
        try {
            List<String> records = loadDataFromFile(HISTORY_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No patient history records found.");
                return;
            }

            System.out.printf("%-15s%-15s%-25s%-50s%-15s\n", "Patient ID", "Date", "Visit Type", "Notes", "Cost");
            System.out.println("-------------------------------------------------------------------------------------------------------------------");
            for (String historyData : records) {
                String[] parts = historyData.split(",");
                System.out.printf("%-15s%-15s%-25s%-50s%-15s\n", parts[0], parts[1], parts[2], parts[3], parts[4]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing all patient history.");
        }
    }

    /**
     * Displays a specific patient's history records.
     */
    private static void viewPatientHistory() {
        System.out.println("\n--- View Patient History ---");
        viewAllPatients();
        System.out.print("Enter Patient ID to view history: ");
        String patientId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(HISTORY_DATA_FILE);
            List<String> patientHistory = records.stream()
                    .filter(record -> record.split(",")[0].equals(patientId))
                    .collect(Collectors.toList());

            if (patientHistory.isEmpty()) {
                System.out.println("No history records found for patient ID " + patientId + ".");
                return;
            }

            System.out.println("\n--- History for Patient ID: " + patientId + " ---");
            System.out.printf("%-15s%-25s%-50s%-15s\n", "Date", "Visit Type", "Notes", "Cost");
            System.out.println("-------------------------------------------------------------------------------------");
            for (String historyData : patientHistory) {
                String[] parts = historyData.split(",");
                if (parts.length >= 5) {
                    System.out.printf("%-15s%-25s%-50s%-15s\n", parts[1], parts[2], parts[3], parts[4]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing patient history.");
        }
    }

    /**
     * Displays a summary of patients grouped by their registration year.
     */
    private static void viewPatientBatchYears() {
        System.out.println("\n--- Patient Batch Years ---");
        try {
            List<String> records = loadDataFromFile(PATIENTS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No patient records found.");
                return;
            }

            Map<String, Long> yearCounts = records.stream()
                    .map(record -> record.split(",")[0]) // Get the patient ID
                    .map(id -> id.substring(id.length() - 6, id.length() - 2)) // Extract the year part
                    .collect(Collectors.groupingBy(patientYear -> patientYear, Collectors.counting()));

            System.out.printf("%-15s%-15s\n", "Patient Year", "Total Patients");
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

    // --- User (Patient) Functionalities ---

    /**
     * Displays and manages the user menu options.
     * @param currentUser The username of the currently logged-in user.
     */
    private static void userMenu(String currentUser) {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Patient Menu ---");
            System.out.println("1. View My History");
            System.out.println("2. Manage My Expenses");
            System.out.println("3. Checkout and Generate Bill");
            System.out.println("4. Logout");
            System.out.println("-------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        viewMyHistory(currentUser);
                        break;
                    case 2:
                        manageExpenses(currentUser);
                        break;
                    case 3:
                        checkoutAndGenerateBill(currentUser);
                        break;
                    case 4:
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
     * Displays a logged-in user's personal history.
     * @param currentUser The username of the user.
     */
    private static void viewMyHistory(String currentUser) {
        System.out.println("\n--- My History ---");
        try {
            // Find the patient ID associated with the current user's username
            String patientId = getPatientIdForUser(currentUser);
            if (patientId == null) {
                System.out.println("No patient record found for your username.");
                return;
            }

            List<String> allHistory = loadDataFromFile(HISTORY_DATA_FILE);
            List<String> myHistory = allHistory.stream()
                    .filter(record -> record.split(",")[0].equals(patientId))
                    .collect(Collectors.toList());

            if (myHistory.isEmpty()) {
                System.out.println("You have no history records.");
                return;
            }

            System.out.printf("%-15s%-25s%-30s%-15s\n", "Date", "Visit Type", "Notes", "Cost");
            System.out.println("-------------------------------------------------------------------------------------");
            for (String historyData : myHistory) {
                String[] parts = historyData.split(",");
                System.out.printf("%-15s%-25s%-30s%-15s\n", parts[1], parts[2], parts[3], parts[4]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing your history.");
        }
    }

    /**
     * Displays and manages the expense menu for a patient.
     * @param currentUser The username of the currently logged-in user.
     */
    private static void manageExpenses(String currentUser) {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage My Expenses ---");
            System.out.println("1. Add a New Expense");
            System.out.println("2. View My Expenses");
            System.out.println("3. Back to Patient Menu");
            System.out.println("-----------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addNewExpense(currentUser);
                        break;
                    case 2:
                        viewMyExpenses(currentUser);
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

    /**
     * Adds a new expense record for a patient.
     * @param currentUser The username of the user.
     */
    private static void addNewExpense(String currentUser) {
        System.out.println("\n--- Add New Expense ---");
        try {
            String patientId = getPatientIdForUser(currentUser);
            if (patientId == null) {
                System.out.println("No patient record found for your username.");
                return;
            }

            System.out.print("Enter expense type (e.g., Medication, Lab Fee, Procedure): ");
            String expenseType = scanner.nextLine();
            System.out.print("Enter expense amount: ");
            String amount = scanner.nextLine();

            // Format: patientId,expenseType,amount
            String newExpense = patientId + "," + expenseType + "," + amount;
            List<String> expenses = loadDataFromFile(EXPENSES_DATA_FILE);
            expenses.add(newExpense);
            saveDataToFile(expenses, EXPENSES_DATA_FILE);
            System.out.println("Expense added successfully for your record.");

        } catch (IOException e) {
            System.out.println("An error occurred while adding the expense.");
        }
    }

    /**
     * Displays all expense records for the current user.
     * @param currentUser The username of the user.
     */
    private static void viewMyExpenses(String currentUser) {
        System.out.println("\n--- My Expenses ---");
        try {
            String patientId = getPatientIdForUser(currentUser);
            if (patientId == null) {
                System.out.println("No patient record found for your username.");
                return;
            }

            List<String> expenseRecords = loadDataFromFile(EXPENSES_DATA_FILE);

            List<String> myExpenses = expenseRecords.stream()
                    .filter(record -> record.split(",")[0].equals(patientId))
                    .collect(Collectors.toList());

            if (myExpenses.isEmpty()) {
                System.out.println("No expenses found for your record.");
                return;
            }

            System.out.printf("%-20s%-15s\n", "Expense Type", "Amount");
            System.out.println("----------------------------------------");
            for (String expenseData : myExpenses) {
                String[] parts = expenseData.split(",");
                if (parts.length >= 3) {
                    System.out.printf("%-20s%-15s\n", parts[1], parts[2]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing your expenses.");
        }
    }

    /**
     * Handles the checkout process, generates a bill, and finalizes the patient record.
     * @param currentUser The username of the user performing the checkout.
     */
    private static void checkoutAndGenerateBill(String currentUser) {
        System.out.println("\n--- Checkout and Bill Generation ---");
        try {
            String patientId = getPatientIdForUser(currentUser);
            if (patientId == null) {
                System.out.println("No patient record found for your username.");
                return;
            }

            List<String> allHistory = loadDataFromFile(HISTORY_DATA_FILE);
            List<String> allExpenses = loadDataFromFile(EXPENSES_DATA_FILE);

            // Step 1: Calculate the total cost from history records
            List<String> patientHistory = allHistory.stream()
                    .filter(record -> record.split(",")[0].equals(patientId))
                    .collect(Collectors.toList());

            double historyTotal = 0.0;
            if (!patientHistory.isEmpty()) {
                historyTotal = patientHistory.stream()
                        .mapToDouble(record -> Double.parseDouble(record.split(",")[4]))
                        .sum();
            }

            // Step 2: Find and sum all additional expenses for this patient
            List<String[]> patientExpenses = allExpenses.stream()
                    .filter(record -> record.split(",")[0].equals(patientId))
                    .map(record -> record.split(","))
                    .collect(Collectors.toList());

            double totalExpenses = patientExpenses.stream()
                    .mapToDouble(record -> Double.parseDouble(record[2]))
                    .sum();

            double totalBill = historyTotal + totalExpenses;

            // Step 3: Generate and print the bill
            System.out.println("\n------------------------------------");
            System.out.println("          C L I N I C   B I L L       ");
            System.out.println("------------------------------------");
            System.out.println("Patient ID: " + patientId);
            System.out.println("------------------------------------");
            System.out.printf("Visit Costs: %-15s $%s\n", "", df.format(historyTotal));
            System.out.println("--- Additional Expenses ---");
            if (patientExpenses.isEmpty()) {
                System.out.println("No additional expenses found.");
            } else {
                for (String[] expense : patientExpenses) {
                    System.out.printf("%-20s $%s\n", expense[1] + ":", df.format(Double.parseDouble(expense[2])));
                }
            }
            System.out.println("------------------------------------");
            System.out.printf("Total Amount Due: %-14s $%s\n", "", df.format(totalBill));
            System.out.println("------------------------------------");

            // Step 4: Confirm checkout and update records
            System.out.print("\nConfirm checkout and pay bill? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("yes")) {
                // Remove history and expense records for this patient ID
                List<String> remainingHistory = allHistory.stream()
                        .filter(record -> !record.split(",")[0].equals(patientId))
                        .collect(Collectors.toList());
                saveDataToFile(remainingHistory, HISTORY_DATA_FILE);

                List<String> remainingExpenses = allExpenses.stream()
                        .filter(record -> !record.split(",")[0].equals(patientId))
                        .collect(Collectors.toList());
                saveDataToFile(remainingExpenses, EXPENSES_DATA_FILE);

                System.out.println("\nCheckout successful! Thank you for your payment.");
            } else {
                System.out.println("Checkout canceled.");
            }

        } catch (IOException e) {
            System.out.println("An error occurred during checkout.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error calculating bill. Invalid cost or amount data.");
        }
    }

    // --- File & Helper Methods ---

    /**
     * Finds the patient ID for a given username. Assumes a one-to-one mapping
     * between a user account and a patient record.
     * @param username The username of the user.
     * @return The patient ID, or null if not found.
     * @throws IOException
     */
    private static String getPatientIdForUser(String username) throws IOException {
        List<String> patients = loadDataFromFile(PATIENTS_DATA_FILE);
        for (String patientRecord : patients) {
            String[] parts = patientRecord.split(",");
            if (parts.length > 1 && parts[1].equals(username)) {
                return parts[0];
            }
        }
        return null;
    }

    /**
     * Generates a unique patient ID based on the required format.
     * Format: 4 letters of name + current year + random 2 digits.
     *
     * @param name The patient's name.
     * @return A unique patient ID as a String.
     * @throws IOException if there's an issue reading the data file.
     */
    private static String generateUniquePatientId(String name) throws IOException {
        String newId;
        boolean isUnique;
        int currentYear = Year.now().getValue();
        do {
            // Get the first 4 letters of the name, handling names shorter than 4 characters
            String namePart = name.length() > 3 ? name.substring(0, 4).toUpperCase() : (name + "XXXX").substring(0, 4).toUpperCase();
            String yearPart = String.valueOf(currentYear);
            String randomPart = String.format("%02d", random.nextInt(100)); // Generate a random 2-digit number with leading zero
            newId = namePart + yearPart + randomPart;
            isUnique = isIdUnique(newId, PATIENTS_DATA_FILE);
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
