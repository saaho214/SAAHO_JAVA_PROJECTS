import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.Random;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * A comprehensive command-line Bank Management System in Java.
 * It features a menu-driven interface with separate modes for administration and users.
 * All data is handled through file I/O for persistence.
 */
public class BankManagementSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String DATA_FILE = "bank_data.txt";
    private static final String TRANSACTION_FILE = "transaction_history.txt";
    private static final Random random = new Random();

    public static void main(String[] args) {
        // Main application loop
        boolean isRunning = true;
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
                        System.out.println("Thank you for using the Bank Management System. Goodbye!");
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
     * Displays the main menu for selecting admin or user mode.
     */
    private static void displayMainMenu() {
        System.out.println("\n------------------------------------");
        System.out.println("      Bank Management System");
        System.out.println("------------------------------------");
        System.out.println("1. Login as Admin");
        System.out.println("2. Login as User");
        System.out.println("3. Exit");
        System.out.println("------------------------------------");
        System.out.print("Enter your choice: ");
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

        if (username.equals("admin") && password.equals("admin")) {
            System.out.println("Admin login successful!");
            adminMenu();
        } else {
            System.out.println("Invalid admin credentials. Access denied.");
        }
    }

    /**
     * Displays and manages the admin menu options.
     */
    private static void adminMenu() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Create New Account");
            System.out.println("2. View All Accounts");
            System.out.println("3. Delete Account");
            System.out.println("4. Search for an Account");
            System.out.println("5. Logout");
            System.out.println("--------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        createAccount();
                        break;
                    case 2:
                        viewAllAccounts();
                        break;
                    case 3:
                        deleteAccount();
                        break;
                    case 4:
                        searchAccount();
                        break;
                    case 5:
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
     * Handles user login and directs to the user menu upon successful authentication.
     */
    private static void userLogin() {
        System.out.println("\n--- User Login ---");
        System.out.print("Enter your account number: ");
        String accountNumber = scanner.nextLine();
        System.out.print("Enter your PIN: ");
        String pin = scanner.nextLine();

        try {
            List<String> accounts = loadAccountsFromFile();
            for (String accountData : accounts) {
                String[] parts = accountData.split(",");
                if (parts[0].equals(accountNumber) && parts[1].equals(pin)) {
                    System.out.println("Login successful!");
                    userMenu(accountNumber);
                    return;
                }
            }
            System.out.println("Invalid account number or PIN. Please try again.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the data file.");
        }
    }

    /**
     * Displays and manages the user menu options.
     *
     * @param accountNumber The account number of the logged-in user.
     */
    private static void userMenu(String accountNumber) {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. Check Balance");
            System.out.println("2. Deposit");
            System.out.println("3. Withdraw");
            System.out.println("4. Transfer");
            System.out.println("5. View Transaction History");
            System.out.println("6. Logout");
            System.out.println("-------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        checkBalance(accountNumber);
                        break;
                    case 2:
                        deposit(accountNumber);
                        break;
                    case 3:
                        withdraw(accountNumber);
                        break;
                    case 4:
                        transfer(accountNumber);
                        break;
                    case 5:
                        viewTransactionHistory(accountNumber);
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
     * Creates a new user account with a randomly generated, unique account number.
     */
    private static void createAccount() {
        System.out.println("\n--- Create New Account ---");
        System.out.print("Create a 4-digit PIN: ");
        String pin = scanner.nextLine();
        System.out.print("Enter account holder's name: ");
        String name = scanner.nextLine();
        System.out.print("Enter initial balance: ");
        double initialBalance = scanner.nextDouble();
        scanner.nextLine();

        // Simple validation for PIN
        if (pin.length() != 4) {
            System.out.println("PIN must be 4 digits. Account creation failed.");
            return;
        }

        try {
            String accountNumber = generateUniqueAccountNumber();
            // Format: accountNumber,pin,name,balance
            String newAccount = accountNumber + "," + pin + "," + name + "," + initialBalance;
            List<String> accounts = loadAccountsFromFile();
            accounts.add(newAccount);
            saveAccountsToFile(accounts);
            System.out.println("Account created successfully!");
            System.out.println("Your new account number is: " + accountNumber);
        } catch (IOException e) {
            System.out.println("An error occurred during account creation.");
        }
    }

    /**
     * Generates a unique, 10-digit account number.
     *
     * @return A unique 10-digit account number as a String.
     * @throws IOException if there's an issue reading the data file.
     */
    private static String generateUniqueAccountNumber() throws IOException {
        String newAccountNumber;
        boolean isUnique;
        do {
            // Generate a random 10-digit number
            long number = 1000000000L + (long)(random.nextDouble() * 9000000000L);
            newAccountNumber = String.valueOf(number);
            isUnique = isAccountNumberUnique(newAccountNumber);
        } while (!isUnique);
        return newAccountNumber;
    }

    /**
     * Checks if an account number is unique by looking it up in the data file.
     *
     * @param accountNumber The number to check for uniqueness.
     * @return true if the number is unique, false otherwise.
     * @throws IOException if there's an issue reading the data file.
     */
    private static boolean isAccountNumberUnique(String accountNumber) throws IOException {
        List<String> accounts = loadAccountsFromFile();
        for (String accountData : accounts) {
            if (accountData.startsWith(accountNumber + ",")) {
                return false;
            }
        }
        return true;
    }

    /**
     * Deletes a user account from the data file.
     */
    private static void deleteAccount() {
        System.out.println("\n--- Delete Account ---");
        System.out.print("Enter account number to delete: ");
        String accountNumber = scanner.nextLine();

        try {
            List<String> accounts = loadAccountsFromFile();
            boolean removed = accounts.removeIf(accountData -> accountData.startsWith(accountNumber + ","));
            if (removed) {
                saveAccountsToFile(accounts);
                System.out.println("Account " + accountNumber + " deleted successfully.");
            } else {
                System.out.println("Account not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while deleting the account.");
        }
    }

    /**
     * Displays all user accounts in a tabular format.
     */
    private static void viewAllAccounts() {
        System.out.println("\n--- All Bank Accounts ---");
        try {
            List<String> accounts = loadAccountsFromFile();
            if (accounts.isEmpty()) {
                System.out.println("No accounts found.");
                return;
            }

            // Print table header
            System.out.printf("%-15s%-10s%-25s%-15s\n", "Account No.", "PIN", "Name", "Balance ($)");
            System.out.println("------------------------------------------------------------------");

            for (String accountData : accounts) {
                String[] parts = accountData.split(",");
                String accountNumber = parts[0];
                String pin = parts[1];
                String name = parts[2];
                double balance = Double.parseDouble(parts[3]);
                System.out.printf("%-15s%-10s%-25s$%.2f\n", accountNumber, pin, name, balance);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing accounts.");
        }
    }

    /**
     * Searches for a specific account by account number.
     */
    private static void searchAccount() {
        System.out.println("\n--- Search Account ---");
        System.out.print("Enter account number to search: ");
        String accountNumber = scanner.nextLine();

        try {
            List<String> accounts = loadAccountsFromFile();
            for (String accountData : accounts) {
                if (accountData.startsWith(accountNumber + ",")) {
                    String[] parts = accountData.split(",");
                    System.out.println("\nAccount Found:");
                    System.out.printf("%-15s%-10s%-25s%-15s\n", "Account No.", "PIN", "Name", "Balance ($)");
                    System.out.println("------------------------------------------------------------------");
                    System.out.printf("%-15s%-10s%-25s$%.2f\n", parts[0], parts[1], parts[2], Double.parseDouble(parts[3]));
                    return;
                }
            }
            System.out.println("Account not found.");
        } catch (IOException e) {
            System.out.println("An error occurred while searching for the account.");
        }
    }

    /**
     * Checks the balance of the logged-in user.
     *
     * @param accountNumber The account number of the logged-in user.
     */
    private static void checkBalance(String accountNumber) {
        try {
            List<String> accounts = loadAccountsFromFile();
            for (String accountData : accounts) {
                if (accountData.startsWith(accountNumber + ",")) {
                    double balance = Double.parseDouble(accountData.split(",")[3]);
                    System.out.printf("Current balance for account %s: $%.2f\n", accountNumber, balance);
                    return;
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while checking the balance.");
        }
    }

    /**
     * Handles the deposit operation.
     *
     * @param accountNumber The account number of the logged-in user.
     */
    private static void deposit(String accountNumber) {
        System.out.println("\n--- Deposit ---");
        System.out.print("Enter amount to deposit: ");
        try {
            double amount = scanner.nextDouble();
            scanner.nextLine();

            if (amount <= 0) {
                System.out.println("Invalid amount. Please enter a positive value.");
                return;
            }

            try {
                List<String> accounts = loadAccountsFromFile();
                for (int i = 0; i < accounts.size(); i++) {
                    String accountData = accounts.get(i);
                    if (accountData.startsWith(accountNumber + ",")) {
                        String[] parts = accountData.split(",");
                        double currentBalance = Double.parseDouble(parts[3]);
                        double newBalance = currentBalance + amount;
                        accounts.set(i, parts[0] + "," + parts[1] + "," + parts[2] + "," + newBalance);
                        saveAccountsToFile(accounts);
                        System.out.printf("Deposit successful. New balance: $%.2f\n", newBalance);
                        recordTransaction(accountNumber, "Deposit", amount, "Cash deposit");
                        return;
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred during the deposit.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine();
        }
    }

    /**
     * Handles the withdrawal operation.
     *
     * @param accountNumber The account number of the logged-in user.
     */
    private static void withdraw(String accountNumber) {
        System.out.println("\n--- Withdraw ---");
        System.out.print("Enter amount to withdraw: ");
        try {
            double amount = scanner.nextDouble();
            scanner.nextLine();

            if (amount <= 0) {
                System.out.println("Invalid amount. Please enter a positive value.");
                return;
            }

            try {
                List<String> accounts = loadAccountsFromFile();
                for (int i = 0; i < accounts.size(); i++) {
                    String accountData = accounts.get(i);
                    if (accountData.startsWith(accountNumber + ",")) {
                        String[] parts = accountData.split(",");
                        double currentBalance = Double.parseDouble(parts[3]);
                        if (currentBalance >= amount) {
                            double newBalance = currentBalance - amount;
                            accounts.set(i, parts[0] + "," + parts[1] + "," + parts[2] + "," + newBalance);
                            saveAccountsToFile(accounts);
                            System.out.printf("Withdrawal successful. New balance: $%.2f\n", newBalance);
                            recordTransaction(accountNumber, "Withdrawal", amount, "Cash withdrawal");
                        } else {
                            System.out.println("Insufficient balance.");
                        }
                        return;
                    }
                }
            } catch (IOException e) {
                System.out.println("An error occurred during the withdrawal.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine();
        }
    }

    /**
     * Handles money transfer between two accounts.
     *
     * @param fromAccount The account number of the sender.
     */
    private static void transfer(String fromAccount) {
        System.out.println("\n--- Transfer ---");
        System.out.print("Enter destination account number: ");
        String toAccount = scanner.nextLine();
        System.out.print("Enter amount to transfer: ");
        try {
            double amount = scanner.nextDouble();
            scanner.nextLine();

            if (amount <= 0) {
                System.out.println("Invalid amount. Please enter a positive value.");
                return;
            }

            try {
                List<String> accounts = loadAccountsFromFile();
                String fromAccountData = null;
                String toAccountData = null;
                int fromIndex = -1;
                int toIndex = -1;

                // Find both accounts and their indices
                for (int i = 0; i < accounts.size(); i++) {
                    String data = accounts.get(i);
                    if (data.startsWith(fromAccount + ",")) {
                        fromAccountData = data;
                        fromIndex = i;
                    }
                    if (data.startsWith(toAccount + ",")) {
                        toAccountData = data;
                        toIndex = i;
                    }
                }

                if (fromAccountData == null) {
                    System.out.println("Sender account not found.");
                    return;
                }
                if (toAccountData == null) {
                    System.out.println("Destination account not found.");
                    return;
                }

                String[] fromParts = fromAccountData.split(",");
                double fromBalance = Double.parseDouble(fromParts[3]);

                if (fromBalance < amount) {
                    System.out.println("Insufficient balance for transfer.");
                    return;
                }

                // Update sender's balance
                double newFromBalance = fromBalance - amount;
                accounts.set(fromIndex, fromParts[0] + "," + fromParts[1] + "," + fromParts[2] + "," + newFromBalance);

                // Update receiver's balance
                String[] toParts = toAccountData.split(",");
                double toBalance = Double.parseDouble(toParts[3]);
                double newToBalance = toBalance + amount;
                accounts.set(toIndex, toParts[0] + "," + toParts[1] + "," + toParts[2] + "," + newToBalance);

                saveAccountsToFile(accounts);
                System.out.printf("Transfer successful. $%.2f transferred to account %s.\n", amount, toAccount);
                System.out.printf("Your new balance is $%.2f.\n", newFromBalance);

                // Record transactions for both accounts
                recordTransaction(fromAccount, "Transfer_Out", amount, "Transfer to " + toAccount);
                recordTransaction(toAccount, "Transfer_In", amount, "Transfer from " + fromAccount);

            } catch (IOException e) {
                System.out.println("An error occurred during the transfer.");
            }
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a valid number.");
            scanner.nextLine();
        }
    }

    /**
     * Records a transaction to the transaction history file.
     *
     * @param accountNumber The account number involved in the transaction.
     * @param type The type of transaction (e.g., Deposit, Withdrawal).
     * @param amount The amount of the transaction.
     * @param description A brief description of the transaction.
     */
    private static void recordTransaction(String accountNumber, String type, double amount, String description) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(TRANSACTION_FILE, true))) {
            // Format: accountNumber,type,amount,timestamp,description
            String timestamp = LocalDateTime.now().toString();
            writer.write(String.format("%s,%s,%.2f,%s,%s%n", accountNumber, type, amount, timestamp, description));
        } catch (IOException e) {
            System.err.println("Error recording transaction: " + e.getMessage());
        }
    }

    /**
     * Displays the transaction history for a specific user in a tabular format.
     *
     * @param accountNumber The account number of the user to view history for.
     */
    private static void viewTransactionHistory(String accountNumber) {
        System.out.println("\n--- Transaction History for " + accountNumber + " ---");
        try {
            File file = new File(TRANSACTION_FILE);
            if (!file.exists()) {
                System.out.println("No transactions found for this account.");
                return;
            }

            List<String> transactions = new ArrayList<>();
            try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
                String line;
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(accountNumber + ",")) {
                        transactions.add(line);
                    }
                }
            }

            if (transactions.isEmpty()) {
                System.out.println("No transactions found for this account.");
                return;
            }

            // Print table header
            System.out.printf("%-25s%-15s%-15s%-40s\n", "Date/Time", "Type", "Amount ($)", "Description");
            System.out.println("---------------------------------------------------------------------------------");

            for (String transactionData : transactions) {
                String[] parts = transactionData.split(",");
                LocalDateTime dateTime = LocalDateTime.parse(parts[3]);
                String formattedDate = dateTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"));
                String type = parts[1];
                double amount = Double.parseDouble(parts[2]);
                String description = parts[4];
                System.out.printf("%-25s%-15s$%-14.2f%s\n", formattedDate, type, amount, description);
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("An error occurred while reading the transaction history.");
        }
    }


    /**
     * Loads account data from the file into a List of strings.
     * Creates the file if it does not exist.
     *
     * @return A List of strings, where each string is an account record.
     * @throws IOException if there's an issue reading the file.
     */
    private static List<String> loadAccountsFromFile() throws IOException {
        File file = new File(DATA_FILE);
        if (!file.exists()) {
            file.createNewFile();
            return new ArrayList<>();
        }
        List<String> accounts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                accounts.add(line);
            }
        }
        return accounts;
    }

    /**
     * Saves the list of account data to the file, overwriting existing content.
     *
     * @param accounts The List of strings to save to the file.
     * @throws IOException if there's an issue writing to the file.
     */
    private static void saveAccountsToFile(List<String> accounts) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(DATA_FILE))) {
            for (String accountData : accounts) {
                writer.write(accountData);
                writer.newLine();
            }
        }
    }
}
