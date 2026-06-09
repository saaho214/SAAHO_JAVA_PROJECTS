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

public class GymMembershipSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String MEMBERSHIPS_DATA_FILE = "memberships.txt";
    private static final String PAYMENTS_DATA_FILE = "payments.txt";
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
                        System.out.println("Thank you for using the Gym Management System. Goodbye!");
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
        System.out.println("  Gym Membership Management System");
        System.out.println("------------------------------------");
        System.out.println("1. Login as Admin");
        System.out.println("2. Login as Member");
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
            System.out.println("1. Register as a new Member");
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

    // Creates a new user or admin account and saves it to the specified file.
     
    private static void createNewAccount(String filename) {
        String accountType = filename.equals(ADMIN_DATA_FILE) ? "Admin" : "Member";
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
     * Handles the general member login and menu.
     */
    private static void userLogin() {
        System.out.println("\n--- Member Login ---");
        System.out.print("Enter username: ");
        String username = scanner.nextLine();
        System.out.print("Enter password: ");
        String password = scanner.nextLine();

        try {
            List<String> users = loadDataFromFile(USER_DATA_FILE);
            if (users.contains(username + "," + password)) {
                System.out.println("Member login successful!");
                userMenu(username); // Pass the username for personalized info
            } else {
                System.out.println("Invalid member credentials. Access denied.");
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
            System.out.println("1. Manage Memberships");
            System.out.println("2. View All Admins");
            System.out.println("3. View All Members");
            System.out.println("4. View All Payment History");
            System.out.println("5. View Payments by Batch Year");
            System.out.println("6. Logout");
            System.out.println("--------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        manageMemberships();
                        break;
                    case 2:
                        viewAllAccounts(ADMIN_DATA_FILE, "Admin");
                        break;
                    case 3:
                        viewAllAccounts(USER_DATA_FILE, "Member");
                        break;
                    case 4:
                        viewAllPaymentHistory();
                        break;
                    case 5:
                        viewPaymentBatchYears();
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
     * Manages all membership-related operations from the admin menu.
     */
    private static void manageMemberships() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Memberships ---");
            System.out.println("1. Add a New Membership");
            System.out.println("2. View All Memberships");
            System.out.println("3. Update a Membership's Details");
            System.out.println("4. Delete a Membership Record");
            System.out.println("5. Back to Admin Menu");
            System.out.println("-------------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addNewMembership();
                        break;
                    case 2:
                        viewAllMemberships();
                        break;
                    case 3:
                        updateMembershipDetails();
                        break;
                    case 4:
                        deleteMembershipRecord();
                        break;
                    case 5:
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
     * Adds a new membership to the system.
     */
    private static void addNewMembership() {
        System.out.println("\n--- Add New Membership ---");
        System.out.print("Enter member's username: ");
        String username = scanner.nextLine();
        System.out.print("Enter plan name (e.g., Monthly, Annual, Platinum): ");
        String planName = scanner.nextLine();
        System.out.print("Enter start date (MM-DD-YYYY): ");
        String startDate = scanner.nextLine();
        System.out.print("Enter monthly cost: ");
        String monthlyCost = scanner.nextLine();

        try {
            // Format: membershipId,username,planName,startDate,monthlyCost
            String membershipId = UUID.randomUUID().toString();
            String newMembership = membershipId + "," + username + "," + planName + "," + startDate + "," + monthlyCost;
            List<String> records = loadDataFromFile(MEMBERSHIPS_DATA_FILE);
            records.add(newMembership);
            saveDataToFile(records, MEMBERSHIPS_DATA_FILE);
            System.out.println("New membership added successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred during membership addition.");
        }
    }

    // Updates an existing membership's details.
     
    private static void updateMembershipDetails() {
        System.out.println("\n--- Update Membership Details ---");
        viewAllMemberships();
        System.out.print("Enter Membership ID to update: ");
        String membershipId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(MEMBERSHIPS_DATA_FILE);
            boolean found = false;
            for (int i = 0; i < records.size(); i++) {
                String record = records.get(i);
                String[] parts = record.split(",");

                if (parts[0].equals(membershipId)) {
                    found = true;

                    System.out.println("Membership found. Current details:");
                    System.out.println("Username: " + parts[1]);
                    System.out.println("Plan Name: " + parts[2]);
                    System.out.println("Start Date: " + parts[3]);
                    System.out.println("Monthly Cost: " + parts[4]);

                    System.out.print("Enter new plan name (or press Enter to keep current): ");
                    String newPlanName = scanner.nextLine();
                    if (!newPlanName.isEmpty()) parts[2] = newPlanName;

                    System.out.print("Enter new monthly cost (or press Enter to keep current): ");
                    String newMonthlyCost = scanner.nextLine();
                    if (!newMonthlyCost.isEmpty()) parts[4] = newMonthlyCost;

                    // Reconstruct and save the updated record
                    records.set(i, String.join(",", parts));
                    saveDataToFile(records, MEMBERSHIPS_DATA_FILE);
                    System.out.println("Membership details updated successfully.");
                    break;
                }
            }
            if (!found) {
                System.out.println("Membership with ID " + membershipId + " not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while updating the membership record.");
        }
    }

    //  Deletes a membership record from the data file.
     
    private static void deleteMembershipRecord() {
        System.out.println("\n--- Delete Membership Record ---");
        viewAllMemberships();
        System.out.print("Enter Membership ID to delete: ");
        String membershipId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(MEMBERSHIPS_DATA_FILE);
            boolean removed = records.removeIf(record -> record.split(",")[0].equals(membershipId));
            if (removed) {
                saveDataToFile(records, MEMBERSHIPS_DATA_FILE);
                System.out.println("Membership record for ID " + membershipId + " deleted successfully.");
            } else {
                System.out.println("Membership record not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while deleting the record.");
        }
    }

    // Displays all membership records in a tabular format.
     
    private static void viewAllMemberships() {
        System.out.println("\n--- All Memberships ---");
        try {
            List<String> records = loadDataFromFile(MEMBERSHIPS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No membership records found.");
                return;
            }

            // Print table header
            System.out.printf("%-40s%-25s%-15s%-15s%-15s\n", "Membership ID", "Username", "Plan", "Start Date", "Cost/Month");
            System.out.println("-----------------------------------------------------------------------------------------------------");

            for (String recordData : records) {
                String[] parts = recordData.split(",");
                if (parts.length >= 5) {
                    System.out.printf("%-40s%-25s%-15s%-15s%-15s\n", parts[0], parts[1], parts[2], parts[3], parts[4]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing records.");
        }
    }

    // Displays all payment records in a tabular format (for Admin).
     
    private static void viewAllPaymentHistory() {
        System.out.println("\n--- All Payment History ---");
        try {
            List<String> records = loadDataFromFile(PAYMENTS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No payment records found.");
                return;
            }

            System.out.printf("%-20s%-15s%-40s%-15s\n", "Payment ID", "Username", "Membership ID", "Amount Paid");
            System.out.println("------------------------------------------------------------------------------------");
            for (String paymentData : records) {
                String[] parts = paymentData.split(",");
                System.out.printf("%-20s%-15s%-40s%-15s\n", parts[0], parts[1], parts[2], parts[3]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing all payment history.");
        }
    }

    // Displays a summary of payments grouped by their payment year.

    private static void viewPaymentBatchYears() {
        System.out.println("\n--- Payment Batch Years ---");
        try {
            List<String> records = loadDataFromFile(PAYMENTS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No payment records found.");
                return;
            }

            Map<String, Long> yearCounts = records.stream()
                    .map(record -> record.split(",")[0]) // Get the payment ID
                    .map(id -> id.substring(id.length() - 6, id.length() - 2)) // Extract the year part
                    .collect(Collectors.groupingBy(paymentYear -> paymentYear, Collectors.counting()));

            System.out.printf("%-15s%-15s\n", "Payment Year", "Total Payments");
            System.out.println("------------------------------");
            yearCounts.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> System.out.printf("%-15s%-15d\n", entry.getKey(), entry.getValue()));
        } catch (IOException e) {
            System.out.println("An error occurred while retrieving year data.");
        }
    }

    // Displays a list of all accounts from the specified file.
    
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

    // Displays and manages the member menu options.
     
    private static void userMenu(String currentUser) {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Member Menu ---");
            System.out.println("1. View My Memberships");
            System.out.println("2. Make a Payment");
            System.out.println("3. View My Payment History");
            System.out.println("4. Manage Payment Expenses");
            System.out.println("5. Generate Bill");
            System.out.println("6. Logout");
            System.out.println("-------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        viewMyMemberships(currentUser);
                        break;
                    case 2:
                        makePayment(currentUser);
                        break;
                    case 3:
                        viewMyPaymentHistory(currentUser);
                        break;
                    case 4:
                        manageExpenses(currentUser);
                        break;
                    case 5:
                        generateBill(currentUser);
                        break;
                    case 6:
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

    // Displays a logged-in user's personal membership records.
     
    private static void viewMyMemberships(String currentUser) {
        System.out.println("\n--- My Memberships ---");
        try {
            List<String> membershipRecords = loadDataFromFile(MEMBERSHIPS_DATA_FILE);
            List<String> userMemberships = membershipRecords.stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .collect(Collectors.toList());

            if (userMemberships.isEmpty()) {
                System.out.println("You have no active memberships.");
                return;
            }

            System.out.printf("%-40s%-25s%-15s%-15s%-15s\n", "Membership ID", "Username", "Plan", "Start Date", "Cost/Month");
            System.out.println("-----------------------------------------------------------------------------------------------------");
            for (String membershipData : userMemberships) {
                String[] parts = membershipData.split(",");
                System.out.printf("%-40s%-25s%-15s%-15s%-15s\n", parts[0], parts[1], parts[2], parts[3], parts[4]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing your memberships.");
        }
    }

    // Allows a user to make a new payment for a membership.
     
    private static void makePayment(String currentUser) {
        System.out.println("\n--- Make a Payment ---");
        viewMyMemberships(currentUser);
        System.out.print("Enter the Membership ID you are paying for: ");
        String membershipId = scanner.nextLine();

        try {
            List<String> membershipRecords = loadDataFromFile(MEMBERSHIPS_DATA_FILE);
            String membershipRecord = membershipRecords.stream()
                    .filter(record -> record.split(",")[0].equals(membershipId) && record.split(",")[1].equals(currentUser))
                    .findFirst()
                    .orElse(null);

            if (membershipRecord == null) {
                System.out.println("Membership not found or does not belong to you.");
                return;
            }

            String[] membershipParts = membershipRecord.split(",");
            String monthlyCost = membershipParts[4];

            System.out.print("Enter amount to pay: ");
            String amountPaid = scanner.nextLine();

            // Generate a payment ID and save the payment record
            String paymentId = generateUniquePaymentId(currentUser);
            // Format: paymentId,username,membershipId,amountPaid
            String paymentRecord = paymentId + "," + currentUser + "," + membershipId + "," + amountPaid;
            List<String> paymentRecords = loadDataFromFile(PAYMENTS_DATA_FILE);
            paymentRecords.add(paymentRecord);
            saveDataToFile(paymentRecords, PAYMENTS_DATA_FILE);

            System.out.println("Payment for membership " + membershipId + " recorded successfully!");
            System.out.println("Your Payment ID is: " + paymentId);

        } catch (IOException e) {
            System.out.println("An error occurred during payment processing.");
        }
    }

    // Displays a logged-in user's personal payment history.
 
    private static void viewMyPaymentHistory(String currentUser) {
        System.out.println("\n--- My Payment History ---");
        try {
            List<String> paymentRecords = loadDataFromFile(PAYMENTS_DATA_FILE);
            List<String> userPayments = paymentRecords.stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .collect(Collectors.toList());

            if (userPayments.isEmpty()) {
                System.out.println("You have no payments recorded.");
                return;
            }

            System.out.printf("%-20s%-40s%-15s\n", "Payment ID", "Membership ID", "Amount Paid");
            System.out.println("-------------------------------------------------------------------");
            for (String paymentData : userPayments) {
                String[] parts = paymentData.split(",");
                System.out.printf("%-20s%-40s%-15s\n", parts[0], parts[2], parts[3]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing your payments.");
        }
    }

    // Displays and manages the expense menu for a user's payment.
    
    private static void manageExpenses(String currentUser) {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Payment Expenses ---");
            System.out.println("1. Add a New Expense");
            System.out.println("2. View My Expenses");
            System.out.println("3. Back to Member Menu");
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

    // Adds a new expense record for a user's payment.
   
    private static void addNewExpense(String currentUser) {
        System.out.println("\n--- Add New Expense ---");
        try {
            List<String> userPayments = loadDataFromFile(PAYMENTS_DATA_FILE).stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .collect(Collectors.toList());

            if (userPayments.isEmpty()) {
                System.out.println("You have no payments to add expenses to.");
                return;
            }

            System.out.println("Your payments:");
            viewMyPaymentHistory(currentUser);

            System.out.print("Enter the Payment ID for which you want to add an expense: ");
            String paymentId = scanner.nextLine();

            boolean paymentFound = userPayments.stream().anyMatch(record -> record.split(",")[0].equals(paymentId));

            if (!paymentFound) {
                System.out.println("Payment not found or does not belong to you.");
                return;
            }

            System.out.print("Enter expense type (e.g., Trainer Fee, Locker Rental): ");
            String expenseType = scanner.nextLine();
            System.out.print("Enter expense amount: ");
            String amount = scanner.nextLine();

            // Format: paymentId,expenseType,amount
            String newExpense = paymentId + "," + expenseType + "," + amount;
            List<String> expenses = loadDataFromFile(EXPENSES_DATA_FILE);
            expenses.add(newExpense);
            saveDataToFile(expenses, EXPENSES_DATA_FILE);
            System.out.println("Expense added successfully for payment " + paymentId + ".");

        } catch (IOException e) {
            System.out.println("An error occurred while adding the expense.");
        }
    }

    // Displays all expense records for the current user's payments.

    private static void viewMyExpenses(String currentUser) {
        System.out.println("\n--- My Expenses ---");
        try {
            List<String> allPayments = loadDataFromFile(PAYMENTS_DATA_FILE);
            List<String> expenseRecords = loadDataFromFile(EXPENSES_DATA_FILE);

            // Get all payment IDs for the current user
            List<String> userPaymentIds = allPayments.stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .map(record -> record.split(",")[0])
                    .collect(Collectors.toList());

            if (userPaymentIds.isEmpty()) {
                System.out.println("You have no payments, and therefore no expenses.");
                return;
            }

            List<String> userExpenses = expenseRecords.stream()
                    .filter(record -> userPaymentIds.contains(record.split(",")[0]))
                    .collect(Collectors.toList());

            if (userExpenses.isEmpty()) {
                System.out.println("No expenses found for your payments.");
                return;
            }

            System.out.printf("%-20s%-20s%-15s\n", "Payment ID", "Expense Type", "Amount");
            System.out.println("--------------------------------------------------");
            for (String expenseData : userExpenses) {
                String[] parts = expenseData.split(",");
                if (parts.length >= 3) {
                    System.out.printf("%-20s%-20s%-15s\n", parts[0], parts[1], parts[2]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing your expenses.");
        }
    }


    //  Generates a detailed bill summarizing membership cost, total payments made,

    private static void generateBill(String currentUser) {
        System.out.println("\n--- Generate Bill ---");
        try {
            List<String> membershipRecords = loadDataFromFile(MEMBERSHIPS_DATA_FILE);
            List<String> paymentRecords = loadDataFromFile(PAYMENTS_DATA_FILE);
            List<String> expenseRecords = loadDataFromFile(EXPENSES_DATA_FILE);

            // Find the user's active memberships
            List<String[]> userMemberships = membershipRecords.stream()
                    .map(record -> record.split(","))
                    .filter(parts -> parts[1].equals(currentUser))
                    .collect(Collectors.toList());

            if (userMemberships.isEmpty()) {
                System.out.println("You have no active memberships to generate a bill for.");
                return;
            }

            for (String[] membership : userMemberships) {
                String membershipId = membership[0];
                String planName = membership[2];
                double totalCost = Double.parseDouble(membership[4]); // Total Fee (e.g., 1000)

                // CRITICAL FIX: Sum up ALL partial payments matching this specific Membership ID
                double totalPaid = paymentRecords.stream()
                        .map(record -> record.split(","))
                        .filter(parts -> parts[2].equals(membershipId))
                        .mapToDouble(parts -> Double.parseDouble(parts[3]))
                        .sum();

                // Get all matching payments for tracking down related expenses (if any)
                List<String> userPaymentIds = paymentRecords.stream()
                        .map(record -> record.split(","))
                        .filter(parts -> parts[2].equals(membershipId))
                        .map(parts -> parts[0])
                        .collect(Collectors.toList());

                // Sum up additional individual expenses (Trainer fees, lockers, etc.)
                double totalExpenses = expenseRecords.stream()
                        .map(record -> record.split(","))
                        .filter(parts -> userPaymentIds.contains(parts[0]))
                        .mapToDouble(parts -> Double.parseDouble(parts[2]))
                        .sum();

                // Calculate exact outstanding remaining balance
                double grandTotalCost = totalCost + totalExpenses;
                double remainingBalance = grandTotalCost - totalPaid;

                // Displaying the final printed invoice
                System.out.println("\n================================================");
                System.out.println("               GYM MEMBERSHIP INVOICE           ");
                System.out.println("================================================");
                System.out.println("Member Username  : " + currentUser);
                System.out.println("Membership ID    : " + membershipId);
                System.out.println("Plan Type        : " + planName);
                System.out.println("------------------------------------------------");
                System.out.println("Base Membership Fee  : $" + df.format(totalCost));
                System.out.println("Additional Expenses  : $" + df.format(totalExpenses));
                System.out.println("Grand Total Cost     : $" + df.format(grandTotalCost));
                System.out.println("Total Amount Paid    : $" + df.format(totalPaid));
                System.out.println("------------------------------------------------");
                
                if (remainingBalance <= 0) {
                    System.out.println("Status               : PAID (Nil Balance Due)");
                } else {
                    System.out.println("Status               : PENDING");
                    System.out.println("Remaining Balance Due: $" + df.format(remainingBalance));
                }
                System.out.println("================================================");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while generating your bill summary.");
        } catch (NumberFormatException | ArrayIndexOutOfBoundsException e) {
            System.out.println("Data formatting parsing error. Please check your data file structure.");
        }
    }

    // --- File & Helper Methods ---

    private static String generateUniquePaymentId(String name) throws IOException {
        String newId;
        boolean isUnique;
        int currentYear = Year.now().getValue();
        do {
            String namePart = name.length() > 3 ? name.substring(0, 4).toUpperCase() : (name + "XXXX").substring(0, 4).toUpperCase();
            String yearPart = String.valueOf(currentYear);
            String randomPart = String.format("%02d", random.nextInt(100)); 
            newId = namePart + yearPart + randomPart;
            isUnique = isIdUnique(newId, PAYMENTS_DATA_FILE);
        } while (!isUnique);
        return newId;
    }

    private static boolean isIdUnique(String id, String filename) throws IOException {
        List<String> records = loadDataFromFile(filename);
        for (String recordData : records) {
            if (recordData.startsWith(id + ",")) {
                return false;
            }
        }
        return true;
    }

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

    private static void saveDataToFile(List<String> records, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String recordData : records) {
                writer.write(recordData);
                writer.newLine();
            }
        }
    }
}