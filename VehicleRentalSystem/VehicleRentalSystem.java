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
 * A comprehensive command-line Vehicle Rental Management System in Java.
 * This system provides separate access modes for administrators and general users,
 * handles user authentication, manages vehicle and rental data, and persists information
 * to text files.
 */
public class VehicleRentalSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String VEHICLES_DATA_FILE = "vehicles.txt";
    private static final String RENTALS_DATA_FILE = "rentals.txt";
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
                        System.out.println("Thank you for using the Vehicle Rental System. Goodbye!");
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
        System.out.println("  Vehicle Rental Management System");
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
                userMenu(username); // Pass the username for personalized order info
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
            System.out.println("1. Manage Vehicles");
            System.out.println("2. View All Admins");
            System.out.println("3. View All Users");
            System.out.println("4. View All Rentals");
            System.out.println("5. View Rental Batch Years");
            System.out.println("6. Logout");
            System.out.println("--------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        manageVehicles();
                        break;
                    case 2:
                        viewAllAccounts(ADMIN_DATA_FILE, "Admin");
                        break;
                    case 3:
                        viewAllAccounts(USER_DATA_FILE, "User");
                        break;
                    case 4:
                        viewAllRentals();
                        break;
                    case 5:
                        viewRentalBatchYears();
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
     * Manages all vehicle-related operations from the admin menu.
     */
    private static void manageVehicles() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Vehicles ---");
            System.out.println("1. Add a New Vehicle");
            System.out.println("2. View All Vehicles");
            System.out.println("3. Update a Vehicle's Details");
            System.out.println("4. Delete a Vehicle Record");
            System.out.println("5. Back to Admin Menu");
            System.out.println("-------------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addNewVehicle();
                        break;
                    case 2:
                        viewAllVehicles();
                        break;
                    case 3:
                        updateVehicleDetails();
                        break;
                    case 4:
                        deleteVehicleRecord();
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
     * Adds a new vehicle to the system.
     */
    private static void addNewVehicle() {
        System.out.println("\n--- Add New Vehicle ---");
        System.out.print("Enter vehicle type (e.g., Car, Van, Truck): ");
        String vehicleType = scanner.nextLine();
        System.out.print("Enter vehicle make: ");
        String make = scanner.nextLine();
        System.out.print("Enter vehicle model: ");
        String model = scanner.nextLine();
        System.out.print("Enter vehicle year: ");
        String year = scanner.nextLine();
        System.out.print("Enter price per day: ");
        String pricePerDay = scanner.nextLine();

        try {
            // Format: vehicleId,vehicleType,make,model,year,pricePerDay,status
            String newVehicle = UUID.randomUUID().toString() + "," + vehicleType + "," + make + "," + model + "," + year + "," + pricePerDay + "," + "Available";
            List<String> records = loadDataFromFile(VEHICLES_DATA_FILE);
            records.add(newVehicle);
            saveDataToFile(records, VEHICLES_DATA_FILE);
            System.out.println("New vehicle added successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred during vehicle addition.");
        }
    }

    /**
     * Updates an existing vehicle's details.
     */
    private static void updateVehicleDetails() {
        System.out.println("\n--- Update Vehicle Details ---");
        viewAllVehicles();
        System.out.print("Enter Vehicle ID to update: ");
        String vehicleId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(VEHICLES_DATA_FILE);
            boolean found = false;
            for (int i = 0; i < records.size(); i++) {
                String record = records.get(i);
                String[] parts = record.split(",");

                if (parts[0].equals(vehicleId)) {
                    found = true;

                    System.out.println("Vehicle found. Current details:");
                    System.out.println("Type: " + parts[1]);
                    System.out.println("Make: " + parts[2]);
                    System.out.println("Model: " + parts[3]);
                    System.out.println("Year: " + parts[4]);
                    System.out.println("Price: " + parts[5]);
                    System.out.println("Status: " + parts[6]);

                    System.out.print("Enter new vehicle type (or press Enter to keep current): ");
                    String newType = scanner.nextLine();
                    if (!newType.isEmpty()) parts[1] = newType;

                    System.out.print("Enter new make (or press Enter to keep current): ");
                    String newMake = scanner.nextLine();
                    if (!newMake.isEmpty()) parts[2] = newMake;

                    System.out.print("Enter new model (or press Enter to keep current): ");
                    String newModel = scanner.nextLine();
                    if (!newModel.isEmpty()) parts[3] = newModel;

                    System.out.print("Enter new year (or press Enter to keep current): ");
                    String newYear = scanner.nextLine();
                    if (!newYear.isEmpty()) parts[4] = newYear;

                    System.out.print("Enter new price per day (or press Enter to keep current): ");
                    String newPrice = scanner.nextLine();
                    if (!newPrice.isEmpty()) parts[5] = newPrice;

                    System.out.print("Enter new status (Available/Rented, or press Enter): ");
                    String newStatus = scanner.nextLine();
                    if (!newStatus.isEmpty()) parts[6] = newStatus;

                    // Reconstruct and save the updated record
                    records.set(i, String.join(",", parts));
                    saveDataToFile(records, VEHICLES_DATA_FILE);
                    System.out.println("Vehicle details updated successfully.");
                    break;
                }
            }
            if (!found) {
                System.out.println("Vehicle with ID " + vehicleId + " not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while updating the vehicle record.");
        }
    }

    /**
     * Deletes a vehicle record from the data file.
     */
    private static void deleteVehicleRecord() {
        System.out.println("\n--- Delete Vehicle Record ---");
        viewAllVehicles();
        System.out.print("Enter Vehicle ID to delete: ");
        String vehicleId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(VEHICLES_DATA_FILE);
            boolean removed = records.removeIf(record -> record.split(",")[0].equals(vehicleId));
            if (removed) {
                saveDataToFile(records, VEHICLES_DATA_FILE);
                System.out.println("Vehicle record for ID " + vehicleId + " deleted successfully.");
            } else {
                System.out.println("Vehicle record not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while deleting the record.");
        }
    }

    /**
     * Displays all vehicle records in a tabular format.
     */
    private static void viewAllVehicles() {
        System.out.println("\n--- All Vehicles ---");
        try {
            List<String> records = loadDataFromFile(VEHICLES_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No vehicle records found.");
                return;
            }

            // Print table header
            System.out.printf("%-40s%-15s%-15s%-15s%-10s%-15s%-15s\n", "Vehicle ID", "Type", "Make", "Model", "Year", "Price/Day", "Status");
            System.out.println("-------------------------------------------------------------------------------------------------------------------");

            for (String recordData : records) {
                String[] parts = recordData.split(",");
                if (parts.length >= 7) {
                    System.out.printf("%-40s%-15s%-15s%-15s%-10s%-15s%-15s\n", parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing records.");
        }
    }

    /**
     * Displays all rental records in a tabular format (for Admin).
     */
    private static void viewAllRentals() {
        System.out.println("\n--- All Rentals ---");
        try {
            List<String> records = loadDataFromFile(RENTALS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No rental records found.");
                return;
            }

            System.out.printf("%-15s%-15s%-40s%-15s\n", "Customer ID", "Username", "Vehicle ID", "Rental Days");
            System.out.println("-----------------------------------------------------------------------------");
            for (String rentalData : records) {
                String[] parts = rentalData.split(",");
                System.out.printf("%-15s%-15s%-40s%-15s\n", parts[0], parts[1], parts[2], parts[3]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing all rentals.");
        }
    }

    /**
     * Displays a summary of rentals grouped by their rental year.
     */
    private static void viewRentalBatchYears() {
        System.out.println("\n--- Rental Batch Years ---");
        try {
            List<String> records = loadDataFromFile(RENTALS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No rental records found.");
                return;
            }

            Map<String, Long> yearCounts = records.stream()
                    .map(record -> record.split(",")[0]) // Get the customer ID
                    .map(id -> id.substring(id.length() - 6, id.length() - 2)) // Extract the year part
                    .collect(Collectors.groupingBy(rentalYear -> rentalYear, Collectors.counting()));

            System.out.printf("%-15s%-15s\n", "Rental Year", "Total Rentals");
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
            System.out.println("1. View Available Vehicles");
            System.out.println("2. Rent a Vehicle");
            System.out.println("3. View My Rentals");
            System.out.println("4. Manage Expenses for my Rentals");
            System.out.println("5. Checkout and Generate Bill");
            System.out.println("6. Logout");
            System.out.println("-------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        viewAvailableVehicles();
                        break;
                    case 2:
                        rentVehicle(currentUser);
                        break;
                    case 3:
                        viewMyRentals(currentUser);
                        break;
                    case 4:
                        manageExpenses(currentUser);
                        break;
                    case 5:
                        checkoutAndGenerateBill(currentUser);
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
     * Displays only the available vehicles in a tabular format.
     */
    private static void viewAvailableVehicles() {
        System.out.println("\n--- Available Vehicles ---");
        try {
            List<String> records = loadDataFromFile(VEHICLES_DATA_FILE);
            List<String> availableVehicles = records.stream()
                    .filter(record -> record.split(",")[6].equals("Available"))
                    .collect(Collectors.toList());

            if (availableVehicles.isEmpty()) {
                System.out.println("No available vehicles found.");
                return;
            }

            System.out.printf("%-40s%-15s%-15s%-15s%-10s%-15s%-15s\n", "Vehicle ID", "Type", "Make", "Model", "Year", "Price/Day", "Status");
            System.out.println("-------------------------------------------------------------------------------------------------------------------");
            for (String recordData : availableVehicles) {
                String[] parts = recordData.split(",");
                System.out.printf("%-40s%-15s%-15s%-15s%-10s%-15s%-15s\n", parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing available vehicles.");
        }
    }

    /**
     * Allows a user to rent an available vehicle.
     * @param currentUser The username of the user making the rental.
     */
    private static void rentVehicle(String currentUser) {
        System.out.println("\n--- Rent a Vehicle ---");
        viewAvailableVehicles();
        System.out.print("Enter the Vehicle ID you want to rent: ");
        String vehicleId = scanner.nextLine();

        try {
            List<String> vehicleRecords = loadDataFromFile(VEHICLES_DATA_FILE);
            boolean found = false;
            for (int i = 0; i < vehicleRecords.size(); i++) {
                String record = vehicleRecords.get(i);
                String[] parts = record.split(",");

                if (parts[0].equals(vehicleId) && parts[6].equals("Available")) {
                    found = true;
                    System.out.print("Enter number of days for rental: ");
                    String rentalDays = scanner.nextLine();

                    // Change vehicle status to "Rented"
                    parts[6] = "Rented";
                    vehicleRecords.set(i, String.join(",", parts));
                    saveDataToFile(vehicleRecords, VEHICLES_DATA_FILE);

                    // Generate a customer ID and save the rental record
                    String customerId = generateUniqueCustomerId(currentUser);
                    // Format: customerId,username,vehicleId,rentalDays
                    String rentalRecord = customerId + "," + currentUser + "," + vehicleId + "," + rentalDays;
                    List<String> rentalRecords = loadDataFromFile(RENTALS_DATA_FILE);
                    rentalRecords.add(rentalRecord);
                    saveDataToFile(rentalRecords, RENTALS_DATA_FILE);

                    System.out.println("Vehicle " + vehicleId + " rented successfully!");
                    System.out.println("Your Customer ID is: " + customerId);
                    break;
                }
            }
            if (!found) {
                System.out.println("Vehicle " + vehicleId + " is not available or does not exist.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred during rental.");
        }
    }

    /**
     * Displays a logged-in user's personal rental history.
     * @param currentUser The username of the user.
     */
    private static void viewMyRentals(String currentUser) {
        System.out.println("\n--- My Rentals ---");
        try {
            List<String> rentalRecords = loadDataFromFile(RENTALS_DATA_FILE);
            List<String> userRentals = rentalRecords.stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .collect(Collectors.toList());

            if (userRentals.isEmpty()) {
                System.out.println("You have no rentals.");
                return;
            }

            System.out.printf("%-15s%-15s%-40s%-15s\n", "Customer ID", "Username", "Vehicle ID", "Rental Days");
            System.out.println("----------------------------------------------------------------------------");
            for (String rentalData : userRentals) {
                String[] parts = rentalData.split(",");
                System.out.printf("%-15s%-15s%-40s%-15s\n", parts[0], parts[1], parts[2], parts[3]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing your rentals.");
        }
    }

    /**
     * Displays and manages the expense menu for a user.
     * @param currentUser The username of the currently logged-in user.
     */
    private static void manageExpenses(String currentUser) {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Expenses ---");
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
     * Adds a new expense record for a user's rental.
     * @param currentUser The username of the user.
     */
    private static void addNewExpense(String currentUser) {
        System.out.println("\n--- Add New Expense ---");
        try {
            List<String> userRentals = loadDataFromFile(RENTALS_DATA_FILE).stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .collect(Collectors.toList());

            if (userRentals.isEmpty()) {
                System.out.println("You have no rentals to add expenses to.");
                return;
            }

            System.out.println("Your rentals:");
            viewMyRentals(currentUser);

            System.out.print("Enter the Customer ID for the rental you want to add an expense to: ");
            String customerId = scanner.nextLine();

            boolean rentalFound = userRentals.stream().anyMatch(record -> record.split(",")[0].equals(customerId));

            if (!rentalFound) {
                System.out.println("Rental not found or does not belong to you.");
                return;
            }

            System.out.print("Enter expense type (e.g., Fuel, Late Fee, Cleaning): ");
            String expenseType = scanner.nextLine();
            System.out.print("Enter expense amount: ");
            String amount = scanner.nextLine();

            // Format: customerId,expenseType,amount
            String newExpense = customerId + "," + expenseType + "," + amount;
            List<String> expenses = loadDataFromFile(EXPENSES_DATA_FILE);
            expenses.add(newExpense);
            saveDataToFile(expenses, EXPENSES_DATA_FILE);
            System.out.println("Expense added successfully for rental " + customerId + ".");

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
            List<String> allRentals = loadDataFromFile(RENTALS_DATA_FILE);
            List<String> expenseRecords = loadDataFromFile(EXPENSES_DATA_FILE);

            // Get all customer IDs for the current user
            List<String> userCustomerIds = allRentals.stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .map(record -> record.split(",")[0])
                    .collect(Collectors.toList());

            if (userCustomerIds.isEmpty()) {
                System.out.println("You have no rentals, and therefore no expenses.");
                return;
            }

            List<String> userExpenses = expenseRecords.stream()
                    .filter(record -> userCustomerIds.contains(record.split(",")[0]))
                    .collect(Collectors.toList());

            if (userExpenses.isEmpty()) {
                System.out.println("No expenses found for your rentals.");
                return;
            }

            System.out.printf("%-15s%-20s%-15s\n", "Customer ID", "Expense Type", "Amount");
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
     * Handles the checkout process, generates a bill, and finalizes the rental.
     * @param currentUser The username of the user performing the checkout.
     */
    private static void checkoutAndGenerateBill(String currentUser) {
        System.out.println("\n--- Checkout and Bill Generation ---");
        viewMyRentals(currentUser);
        System.out.print("Enter the Customer ID for the rental to check out: ");
        String customerId = scanner.nextLine();

        try {
            List<String> allRentals = loadDataFromFile(RENTALS_DATA_FILE);
            List<String> allVehicles = loadDataFromFile(VEHICLES_DATA_FILE);
            List<String> allExpenses = loadDataFromFile(EXPENSES_DATA_FILE);

            // Step 1: Find the rental record for the given customerId and user
            String rentalRecord = allRentals.stream()
                    .filter(record -> {
                        String[] parts = record.split(",");
                        return parts[0].equals(customerId) && parts[1].equals(currentUser);
                    })
                    .findFirst()
                    .orElse(null);

            if (rentalRecord == null) {
                System.out.println("Rental not found or does not belong to you.");
                return;
            }

            String[] rentalParts = rentalRecord.split(",");
            String vehicleId = rentalParts[2];
            int rentalDays = Integer.parseInt(rentalParts[3]);

            // Step 2: Find the vehicle's price per day
            String vehicleRecord = allVehicles.stream()
                    .filter(record -> record.split(",")[0].equals(vehicleId))
                    .findFirst()
                    .orElse(null);

            double pricePerDay = (vehicleRecord != null) ? Double.parseDouble(vehicleRecord.split(",")[5]) : 0.0;
            double rentalCost = pricePerDay * rentalDays;

            // Step 3: Find and sum all additional expenses for this customer ID
            List<String[]> customerExpenses = allExpenses.stream()
                    .filter(record -> record.split(",")[0].equals(customerId))
                    .map(record -> record.split(","))
                    .collect(Collectors.toList());

            double totalExpenses = customerExpenses.stream()
                    .mapToDouble(record -> Double.parseDouble(record[2]))
                    .sum();

            double totalBill = rentalCost + totalExpenses;

            // Step 4: Generate and print the bill
            System.out.println("\n------------------------------------");
            System.out.println("          R E N T A L   B I L L       ");
            System.out.println("------------------------------------");
            System.out.println("Customer ID: " + customerId);
            System.out.println("Vehicle ID: " + vehicleId);
            System.out.println("Rental Days: " + rentalDays);
            System.out.println("------------------------------------");
            System.out.printf("Rental Cost: %-20s $%s\n", "", df.format(rentalCost));
            System.out.println("--- Additional Expenses ---");
            if (customerExpenses.isEmpty()) {
                System.out.println("No additional expenses found.");
            } else {
                for (String[] expense : customerExpenses) {
                    System.out.printf("%-20s $%s\n", expense[1] + ":", df.format(Double.parseDouble(expense[2])));
                }
            }
            System.out.println("------------------------------------");
            System.out.printf("Total Amount Due: %-14s $%s\n", "", df.format(totalBill));
            System.out.println("------------------------------------");

            // Step 5: Confirm checkout and update records
            System.out.print("\nConfirm checkout and pay bill? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("yes")) {
                // Remove rental record
                allRentals.remove(rentalRecord);
                saveDataToFile(allRentals, RENTALS_DATA_FILE);

                // Remove expense records
                allExpenses.removeIf(record -> record.split(",")[0].equals(customerId));
                saveDataToFile(allExpenses, EXPENSES_DATA_FILE);

                // Update vehicle status back to "Available"
                for (int i = 0; i < allVehicles.size(); i++) {
                    String record = allVehicles.get(i);
                    String[] parts = record.split(",");
                    if (parts[0].equals(vehicleId)) {
                        parts[6] = "Available";
                        allVehicles.set(i, String.join(",", parts));
                        break;
                    }
                }
                saveDataToFile(allVehicles, VEHICLES_DATA_FILE);

                System.out.println("\nCheckout successful! Vehicle " + vehicleId + " is now available.");
            } else {
                System.out.println("Checkout canceled.");
            }

        } catch (IOException e) {
            System.out.println("An error occurred during checkout.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error calculating bill. Invalid price, rental days, or amount data.");
        }
    }

    // --- File & Helper Methods ---

    /**
     * Generates a unique customer ID based on the required format.
     * Format: 4 letters of name + current year + random 2 digits.
     *
     * @param name The customer's name.
     * @return A unique customer ID as a String.
     * @throws IOException if there's an issue reading the data file.
     */
    private static String generateUniqueCustomerId(String name) throws IOException {
        String newId;
        boolean isUnique;
        int currentYear = Year.now().getValue();
        do {
            // Get the first 4 letters of the name, handling names shorter than 4 characters
            String namePart = name.length() > 3 ? name.substring(0, 4).toUpperCase() : (name + "XXXX").substring(0, 4).toUpperCase();
            String yearPart = String.valueOf(currentYear);
            String randomPart = String.format("%02d", random.nextInt(100)); // Generate a random 2-digit number with leading zero
            newId = namePart + yearPart + randomPart;
            isUnique = isIdUnique(newId, RENTALS_DATA_FILE);
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
