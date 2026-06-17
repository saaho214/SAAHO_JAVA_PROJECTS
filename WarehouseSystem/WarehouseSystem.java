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
 * A comprehensive command-line Warehouse Management System in Java.
 * This system provides separate access modes for administrators and users,
 * handles user authentication, manages inventory, transactions, and bills,
 * and persists information to text files.
 */
public class WarehouseSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String ITEMS_DATA_FILE = "items.txt";
    private static final String TRANSACTIONS_DATA_FILE = "transactions.txt";
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
                        System.out.println("Thank you for using the Warehouse Management System. Goodbye!");
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
        System.out.println("  Warehouse Management System");
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
            System.out.println("1. Manage Items");
            System.out.println("2. View All Admins");
            System.out.println("3. View All Users");
            System.out.println("4. View All Transactions (History)");
            System.out.println("5. View Transaction Batch Years");
            System.out.println("6. Logout");
            System.out.println("--------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        manageItems();
                        break;
                    case 2:
                        viewAllAccounts(ADMIN_DATA_FILE, "Admin");
                        break;
                    case 3:
                        viewAllAccounts(USER_DATA_FILE, "User");
                        break;
                    case 4:
                        viewAllTransactions();
                        break;
                    case 5:
                        viewTransactionBatchYears();
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
     * Manages all item-related operations from the admin menu.
     */
    private static void manageItems() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Items ---");
            System.out.println("1. Add a New Item");
            System.out.println("2. View All Items");
            System.out.println("3. Update an Item's Details");
            System.out.println("4. Delete an Item Record");
            System.out.println("5. Back to Admin Menu");
            System.out.println("-------------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addNewItem();
                        break;
                    case 2:
                        viewAllItems();
                        break;
                    case 3:
                        updateItemDetails();
                        break;
                    case 4:
                        deleteItemRecord();
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
     * Adds a new item to the system.
     */
    private static void addNewItem() {
        System.out.println("\n--- Add New Item ---");
        System.out.print("Enter item name: ");
        String itemName = scanner.nextLine();
        System.out.print("Enter item category (e.g., Electronics, Food, Tools): ");
        String category = scanner.nextLine();
        System.out.print("Enter quantity in stock: ");
        String quantity = scanner.nextLine();
        System.out.print("Enter price per unit: ");
        String pricePerUnit = scanner.nextLine();

        try {
            // Format: itemId,itemName,category,quantity,pricePerUnit
            String newItem = UUID.randomUUID().toString() + "," + itemName + "," + category + "," + quantity + "," + pricePerUnit;
            List<String> records = loadDataFromFile(ITEMS_DATA_FILE);
            records.add(newItem);
            saveDataToFile(records, ITEMS_DATA_FILE);
            System.out.println("New item added successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred during item addition.");
        }
    }

    /**
     * Updates an existing item's details.
     */
    private static void updateItemDetails() {
        System.out.println("\n--- Update Item Details ---");
        viewAllItems();
        System.out.print("Enter Item ID to update: ");
        String itemId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(ITEMS_DATA_FILE);
            boolean found = false;
            for (int i = 0; i < records.size(); i++) {
                String record = records.get(i);
                String[] parts = record.split(",");

                if (parts[0].equals(itemId)) {
                    found = true;

                    System.out.println("Item found. Current details:");
                    System.out.println("Name: " + parts[1]);
                    System.out.println("Category: " + parts[2]);
                    System.out.println("Quantity: " + parts[3]);
                    System.out.println("Price: " + parts[4]);

                    System.out.print("Enter new item name (or press Enter to keep current): ");
                    String newName = scanner.nextLine();
                    if (!newName.isEmpty()) parts[1] = newName;

                    System.out.print("Enter new category (or press Enter to keep current): ");
                    String newCategory = scanner.nextLine();
                    if (!newCategory.isEmpty()) parts[2] = newCategory;

                    System.out.print("Enter new quantity (or press Enter to keep current): ");
                    String newQuantity = scanner.nextLine();
                    if (!newQuantity.isEmpty()) parts[3] = newQuantity;

                    System.out.print("Enter new price per unit (or press Enter to keep current): ");
                    String newPrice = scanner.nextLine();
                    if (!newPrice.isEmpty()) parts[4] = newPrice;

                    // Reconstruct and save the updated record
                    records.set(i, String.join(",", parts));
                    saveDataToFile(records, ITEMS_DATA_FILE);
                    System.out.println("Item details updated successfully.");
                    break;
                }
            }
            if (!found) {
                System.out.println("Item with ID " + itemId + " not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while updating the item record.");
        }
    }

    /**
     * Deletes an item record from the data file.
     */
    private static void deleteItemRecord() {
        System.out.println("\n--- Delete Item Record ---");
        viewAllItems();
        System.out.print("Enter Item ID to delete: ");
        String itemId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(ITEMS_DATA_FILE);
            boolean removed = records.removeIf(record -> record.split(",")[0].equals(itemId));
            if (removed) {
                saveDataToFile(records, ITEMS_DATA_FILE);
                System.out.println("Item record for ID " + itemId + " deleted successfully.");
            } else {
                System.out.println("Item record not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while deleting the record.");
        }
    }

    /**
     * Displays all item records in a tabular format.
     */
    private static void viewAllItems() {
        System.out.println("\n--- All Items ---");
        try {
            List<String> records = loadDataFromFile(ITEMS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No item records found.");
                return;
            }

            // Print table header
            System.out.printf("%-40s%-25s%-15s%-15s%-15s\n", "Item ID", "Name", "Category", "Quantity", "Price/Unit");
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

    /**
     * Displays all transaction records in a tabular format (for Admin).
     */
    private static void viewAllTransactions() {
        System.out.println("\n--- All Transactions ---");
        try {
            List<String> records = loadDataFromFile(TRANSACTIONS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No transaction records found.");
                return;
            }

            System.out.printf("%-15s%-15s%-40s%-15s\n", "Transaction ID", "Username", "Item ID", "Quantity");
            System.out.println("-----------------------------------------------------------------------------");
            for (String transactionData : records) {
                String[] parts = transactionData.split(",");
                System.out.printf("%-15s%-15s%-40s%-15s\n", parts[0], parts[1], parts[2], parts[3]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing all transactions.");
        }
    }

    /**
     * Displays a summary of transactions grouped by their transaction year.
     */
    private static void viewTransactionBatchYears() {
        System.out.println("\n--- Transaction Batch Years ---");
        try {
            List<String> records = loadDataFromFile(TRANSACTIONS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No transaction records found.");
                return;
            }

            Map<String, Long> yearCounts = records.stream()
                    .map(record -> record.split(",")[0]) // Get the transaction ID
                    .map(id -> id.substring(id.length() - 6, id.length() - 2)) // Extract the year part
                    .collect(Collectors.groupingBy(transactionYear -> transactionYear, Collectors.counting()));

            System.out.printf("%-15s%-15s\n", "Transaction Year", "Total Transactions");
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
            System.out.println("1. View Available Items");
            System.out.println("2. Create a Transaction");
            System.out.println("3. View My Transactions");
            System.out.println("4. Manage Expenses for my Transactions");
            System.out.println("5. Generate Invoice");
            System.out.println("6. Logout");
            System.out.println("-------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        viewAvailableItems();
                        break;
                    case 2:
                        createTransaction(currentUser);
                        break;
                    case 3:
                        viewMyTransactions(currentUser);
                        break;
                    case 4:
                        manageExpenses(currentUser);
                        break;
                    case 5:
                        generateInvoice(currentUser);
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

    /**
     * Displays only the available items in a tabular format.
     */
    private static void viewAvailableItems() {
        System.out.println("\n--- Available Items ---");
        try {
            List<String> records = loadDataFromFile(ITEMS_DATA_FILE);
            List<String> availableItems = records.stream()
                    .filter(record -> Integer.parseInt(record.split(",")[3]) > 0)
                    .collect(Collectors.toList());

            if (availableItems.isEmpty()) {
                System.out.println("No available items found.");
                return;
            }

            System.out.printf("%-40s%-25s%-15s%-15s%-15s\n", "Item ID", "Name", "Category", "Quantity", "Price/Unit");
            System.out.println("-----------------------------------------------------------------------------------------------------");
            for (String recordData : availableItems) {
                String[] parts = recordData.split(",");
                System.out.printf("%-40s%-25s%-15s%-15s%-15s\n", parts[0], parts[1], parts[2], parts[3], parts[4]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing available items.");
        }
    }

    /**
     * Allows a user to create a new transaction for an item.
     * @param currentUser The username of the user creating the transaction.
     */
    private static void createTransaction(String currentUser) {
        System.out.println("\n--- Create a Transaction ---");
        viewAvailableItems();
        System.out.print("Enter the Item ID you want to transact: ");
        String itemId = scanner.nextLine();

        try {
            List<String> itemRecords = loadDataFromFile(ITEMS_DATA_FILE);
            boolean found = false;
            for (int i = 0; i < itemRecords.size(); i++) {
                String record = itemRecords.get(i);
                String[] parts = record.split(",");
                int currentQuantity = Integer.parseInt(parts[3]);

                if (parts[0].equals(itemId) && currentQuantity > 0) {
                    found = true;
                    System.out.print("Enter quantity for transaction: ");
                    int transactionQuantity = scanner.nextInt();
                    scanner.nextLine(); // Consume newline

                    if (transactionQuantity <= 0 || transactionQuantity > currentQuantity) {
                        System.out.println("Invalid quantity. Transaction failed.");
                        return;
                    }

                    // Update item stock
                    int newQuantity = currentQuantity - transactionQuantity;
                    parts[3] = String.valueOf(newQuantity);
                    itemRecords.set(i, String.join(",", parts));
                    saveDataToFile(itemRecords, ITEMS_DATA_FILE);

                    // Generate a transaction ID and save the transaction record
                    String transactionId = generateUniqueTransactionId(currentUser);
                    // Format: transactionId,username,itemId,quantity
                    String transactionRecord = transactionId + "," + currentUser + "," + itemId + "," + transactionQuantity;
                    List<String> transactionRecords = loadDataFromFile(TRANSACTIONS_DATA_FILE);
                    transactionRecords.add(transactionRecord);
                    saveDataToFile(transactionRecords, TRANSACTIONS_DATA_FILE);

                    System.out.println("Transaction for item " + itemId + " created successfully!");
                    System.out.println("Your Transaction ID is: " + transactionId);
                    break;
                }
            }
            if (!found) {
                System.out.println("Item " + itemId + " is out of stock or does not exist.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred during transaction.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number for quantity.");
            scanner.nextLine();
        }
    }

    /**
     * Displays a logged-in user's personal transaction history.
     * @param currentUser The username of the user.
     */
    private static void viewMyTransactions(String currentUser) {
        System.out.println("\n--- My Transactions ---");
        try {
            List<String> transactionRecords = loadDataFromFile(TRANSACTIONS_DATA_FILE);
            List<String> userTransactions = transactionRecords.stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .collect(Collectors.toList());

            if (userTransactions.isEmpty()) {
                System.out.println("You have no transactions.");
                return;
            }

            System.out.printf("%-15s%-15s%-40s%-15s\n", "Transaction ID", "Username", "Item ID", "Quantity");
            System.out.println("----------------------------------------------------------------------------");
            for (String transactionData : userTransactions) {
                String[] parts = transactionData.split(",");
                System.out.printf("%-15s%-15s%-40s%-15s\n", parts[0], parts[1], parts[2], parts[3]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing your transactions.");
        }
    }

    /**
     * Displays and manages the expense menu for a user.
     * @param currentUser The username of the currently logged-in user.
     */
    private static void manageExpenses(String currentUser) {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Transaction Expenses ---");
            System.out.println("1. Add a New Expense");
            System.out.println("2. View My Expenses");
            System.out.println("3. Back to User Menu");
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
     * Adds a new expense record for a user's transaction.
     * @param currentUser The username of the user.
     */
    private static void addNewExpense(String currentUser) {
        System.out.println("\n--- Add New Expense ---");
        try {
            List<String> userTransactions = loadDataFromFile(TRANSACTIONS_DATA_FILE).stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .collect(Collectors.toList());

            if (userTransactions.isEmpty()) {
                System.out.println("You have no transactions to add expenses to.");
                return;
            }

            System.out.println("Your transactions:");
            viewMyTransactions(currentUser);

            System.out.print("Enter the Transaction ID for which you want to add an expense: ");
            String transactionId = scanner.nextLine();

            boolean transactionFound = userTransactions.stream().anyMatch(record -> record.split(",")[0].equals(transactionId));

            if (!transactionFound) {
                System.out.println("Transaction not found or does not belong to you.");
                return;
            }

            System.out.print("Enter expense type (e.g., Shipping, Handling, Tax): ");
            String expenseType = scanner.nextLine();
            System.out.print("Enter expense amount: ");
            String amount = scanner.nextLine();

            // Format: transactionId,expenseType,amount
            String newExpense = transactionId + "," + expenseType + "," + amount;
            List<String> expenses = loadDataFromFile(EXPENSES_DATA_FILE);
            expenses.add(newExpense);
            saveDataToFile(expenses, EXPENSES_DATA_FILE);
            System.out.println("Expense added successfully for transaction " + transactionId + ".");

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
            List<String> allTransactions = loadDataFromFile(TRANSACTIONS_DATA_FILE);
            List<String> expenseRecords = loadDataFromFile(EXPENSES_DATA_FILE);

            // Get all transaction IDs for the current user
            List<String> userTransactionIds = allTransactions.stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .map(record -> record.split(",")[0])
                    .collect(Collectors.toList());

            if (userTransactionIds.isEmpty()) {
                System.out.println("You have no transactions, and therefore no expenses.");
                return;
            }

            List<String> userExpenses = expenseRecords.stream()
                    .filter(record -> userTransactionIds.contains(record.split(",")[0]))
                    .collect(Collectors.toList());

            if (userExpenses.isEmpty()) {
                System.out.println("No expenses found for your transactions.");
                return;
            }

            System.out.printf("%-15s%-20s%-15s\n", "Transaction ID", "Expense Type", "Amount");
            System.out.println("--------------------------------------------------");
            for (String expenseData : userExpenses) {
                String[] parts = expenseData.split(",");
                if (parts.length >= 3) {
                    System.out.printf("%-15s%-20s%-15s\n", parts[0], parts[1], parts[2]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing your expenses.");
        }
    }

    /**
     * Handles the invoice generation process and finalizes the transaction.
     * @param currentUser The username of the user performing the checkout.
     */
    private static void generateInvoice(String currentUser) {
        System.out.println("\n--- Invoice Generation ---");
        viewMyTransactions(currentUser);
        System.out.print("Enter the Transaction ID to generate an invoice for: ");
        String transactionId = scanner.nextLine();

        try {
            List<String> allTransactions = loadDataFromFile(TRANSACTIONS_DATA_FILE);
            List<String> allItems = loadDataFromFile(ITEMS_DATA_FILE);
            List<String> allExpenses = loadDataFromFile(EXPENSES_DATA_FILE);

            // Step 1: Find the transaction record for the given transactionId and user
            String transactionRecord = allTransactions.stream()
                    .filter(record -> {
                        String[] parts = record.split(",");
                        return parts[0].equals(transactionId) && parts[1].equals(currentUser);
                    })
                    .findFirst()
                    .orElse(null);

            if (transactionRecord == null) {
                System.out.println("Transaction not found or does not belong to you.");
                return;
            }

            String[] transactionParts = transactionRecord.split(",");
            String itemId = transactionParts[2];
            int transactionQuantity = Integer.parseInt(transactionParts[3]);

            // Step 2: Find the item's price per unit
            String itemRecord = allItems.stream()
                    .filter(record -> record.split(",")[0].equals(itemId))
                    .findFirst()
                    .orElse(null);

            double pricePerUnit = (itemRecord != null) ? Double.parseDouble(itemRecord.split(",")[4]) : 0.0;
            double subtotalCost = pricePerUnit * transactionQuantity;

            // Step 3: Find and sum all additional expenses for this transaction ID
            List<String[]> transactionExpenses = allExpenses.stream()
                    .filter(record -> record.split(",")[0].equals(transactionId))
                    .map(record -> record.split(","))
                    .collect(Collectors.toList());

            double totalExpenses = transactionExpenses.stream()
                    .mapToDouble(record -> Double.parseDouble(record[2]))
                    .sum();

            double totalInvoice = subtotalCost + totalExpenses;

            // Step 4: Generate and print the invoice
            System.out.println("\n------------------------------------");
            System.out.println("            I N V O I C E       ");
            System.out.println("------------------------------------");
            System.out.println("Transaction ID: " + transactionId);
            System.out.println("Item ID: " + itemId);
            System.out.println("Quantity: " + transactionQuantity);
            System.out.println("------------------------------------");
            System.out.printf("Subtotal: %-22s $%s\n", "", df.format(subtotalCost));
            System.out.println("--- Additional Expenses ---");
            if (transactionExpenses.isEmpty()) {
                System.out.println("No additional expenses found.");
            } else {
                for (String[] expense : transactionExpenses) {
                    System.out.printf("%-20s $%s\n", expense[1] + ":", df.format(Double.parseDouble(expense[2])));
                }
            }
            System.out.println("------------------------------------");
            System.out.printf("Total Amount Due: %-14s $%s\n", "", df.format(totalInvoice));
            System.out.println("------------------------------------");

            // Step 5: Confirm and finalize
            System.out.print("\nConfirm payment and finalize? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("yes")) {
                // Remove transaction record
                allTransactions.remove(transactionRecord);
                saveDataToFile(allTransactions, TRANSACTIONS_DATA_FILE);

                // Remove expense records
                allExpenses.removeIf(record -> record.split(",")[0].equals(transactionId));
                saveDataToFile(allExpenses, EXPENSES_DATA_FILE);

                System.out.println("\nInvoice paid. Transaction " + transactionId + " finalized.");
            } else {
                System.out.println("Finalization canceled.");
            }

        } catch (IOException e) {
            System.out.println("An error occurred during invoice generation.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error calculating invoice. Invalid price or quantity data.");
        }
    }

    // --- File & Helper Methods ---

    /**
     * Generates a unique transaction ID based on the required format.
     * Format: 4 letters of username + current year + random 2 digits.
     *
     * @param name The user's username.
     * @return A unique transaction ID as a String.
     * @throws IOException if there's an issue reading the data file.
     */
    private static String generateUniqueTransactionId(String name) throws IOException {
        String newId;
        boolean isUnique;
        int currentYear = Year.now().getValue();
        do {
            // Get the first 4 letters of the name, handling names shorter than 4 characters
            String namePart = name.length() > 3 ? name.substring(0, 4).toUpperCase() : (name + "XXXX").substring(0, 4).toUpperCase();
            String yearPart = String.valueOf(currentYear);
            String randomPart = String.format("%02d", random.nextInt(100)); // Generate a random 2-digit number with leading zero
            newId = namePart + yearPart + randomPart;
            isUnique = isIdUnique(newId, TRANSACTIONS_DATA_FILE);
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
