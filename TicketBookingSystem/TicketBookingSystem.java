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

/**
 * A comprehensive command-line Ticket Booking Management System in Java.
 * This system provides separate access modes for administrators and users,
 * handles user authentication, manages vehicle schedules, bookings, and bills,
 * and persists information to text files.
 */
public class TicketBookingSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String VEHICLES_DATA_FILE = "vehicles.txt";
    private static final String BOOKINGS_DATA_FILE = "bookings.txt";
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
                        System.out.println("Thank you for using the Ticket Booking System. Goodbye!");
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
        System.out.println("  Ticket Booking Management System");
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
            System.out.println("1. Manage Vehicles (Bus/Plane)");
            System.out.println("2. View All Admins");
            System.out.println("3. View All Users");
            System.out.println("4. View All Booking History");
            System.out.println("5. View Bookings by Batch Year");
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
                        viewAllBookings();
                        break;
                    case 5:
                        viewBookingBatchYears();
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
     * Adds a new vehicle (bus or plane) to the system.
     */
    private static void addNewVehicle() {
        System.out.println("\n--- Add New Vehicle ---");
        System.out.print("Enter vehicle type (Bus or Plane): ");
        String type = scanner.nextLine();
        System.out.print("Enter vehicle name (e.g., ABC Bus, Delta 747): ");
        String name = scanner.nextLine();
        System.out.print("Enter origin: ");
        String origin = scanner.nextLine();
        System.out.print("Enter destination: ");
        String destination = scanner.nextLine();
        System.out.print("Enter total number of seats: ");
        int totalSeats = scanner.nextInt();
        scanner.nextLine(); // Consume newline
        System.out.print("Enter price per seat: ");
        double price = scanner.nextDouble();
        scanner.nextLine(); // Consume newline

        try {
            // Format: vehicleId,type,name,origin,destination,totalSeats,availableSeats,price
            String vehicleId = UUID.randomUUID().toString();
            String newVehicle = vehicleId + "," + type + "," + name + "," + origin + "," + destination + "," + totalSeats + "," + totalSeats + "," + price;
            List<String> records = loadDataFromFile(VEHICLES_DATA_FILE);
            records.add(newVehicle);
            saveDataToFile(records, VEHICLES_DATA_FILE);
            System.out.println("New vehicle added successfully! Vehicle ID: " + vehicleId);
        } catch (IOException e) {
            System.out.println("An error occurred during vehicle addition.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input for seats or price. Please enter a number.");
            scanner.nextLine();
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
                    System.out.println("Name: " + parts[2]);
                    System.out.println("Origin: " + parts[3]);
                    System.out.println("Destination: " + parts[4]);
                    System.out.println("Seats: " + parts[5] + " (Available: " + parts[6] + ")");
                    System.out.println("Price: $" + parts[7]);

                    System.out.print("Enter new vehicle type (or press Enter to keep current): ");
                    String newType = scanner.nextLine();
                    if (!newType.isEmpty()) parts[1] = newType;

                    System.out.print("Enter new vehicle name (or press Enter to keep current): ");
                    String newName = scanner.nextLine();
                    if (!newName.isEmpty()) parts[2] = newName;

                    System.out.print("Enter new origin (or press Enter to keep current): ");
                    String newOrigin = scanner.nextLine();
                    if (!newOrigin.isEmpty()) parts[3] = newOrigin;

                    System.out.print("Enter new destination (or press Enter to keep current): ");
                    String newDestination = scanner.nextLine();
                    if (!newDestination.isEmpty()) parts[4] = newDestination;

                    System.out.print("Enter new price per seat (or press Enter to keep current): ");
                    String newPrice = scanner.nextLine();
                    if (!newPrice.isEmpty()) parts[7] = newPrice;

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
            System.out.printf("%-40s%-10s%-25s%-25s%-25s%-10s%-15s%-10s\n", "Vehicle ID", "Type", "Name", "Origin", "Destination", "Seats", "Available", "Price");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------");

            for (String recordData : records) {
                String[] parts = recordData.split(",");
                if (parts.length >= 8) {
                    System.out.printf("%-40s%-10s%-25s%-25s%-25s%-10s%-15s%-10s\n", parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing records.");
        }
    }

    /**
     * Displays all booking records in a tabular format (for Admin).
     */
    private static void viewAllBookings() {
        System.out.println("\n--- All Booking History ---");
        try {
            List<String> records = loadDataFromFile(BOOKINGS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No booking records found.");
                return;
            }

            System.out.printf("%-20s%-15s%-40s%-15s%-15s\n", "Booking ID", "Username", "Vehicle ID", "Seat", "Cost");
            System.out.println("----------------------------------------------------------------------------------------------");
            for (String bookingData : records) {
                String[] parts = bookingData.split(",");
                System.out.printf("%-20s%-15s%-40s%-15s%-15s\n", parts[0], parts[1], parts[2], parts[3], parts[4]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing all booking history.");
        }
    }

    /**
     * Displays a summary of bookings grouped by their booking year.
     */
    private static void viewBookingBatchYears() {
        System.out.println("\n--- Booking Batch Years ---");
        try {
            List<String> records = loadDataFromFile(BOOKINGS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No booking records found.");
                return;
            }

            Map<String, Long> yearCounts = records.stream()
                    .map(record -> record.split(",")[0]) // Get the booking ID
                    .map(id -> id.substring(id.length() - 6, id.length() - 2)) // Extract the year part
                    .collect(Collectors.groupingBy(bookingYear -> bookingYear, Collectors.counting()));

            System.out.printf("%-15s%-15s\n", "Booking Year", "Total Bookings");
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
            System.out.println("1. View Available Schedules");
            System.out.println("2. Book a Ticket");
            System.out.println("3. View My Bookings");
            System.out.println("4. Manage Booking Expenses");
            System.out.println("5. Generate Bill");
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
                        bookTicket(currentUser);
                        break;
                    case 3:
                        viewMyBookings(currentUser);
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

    /**
     * Displays only the available vehicles in a tabular format.
     */
    private static void viewAvailableVehicles() {
        System.out.println("\n--- Available Schedules ---");
        try {
            List<String> records = loadDataFromFile(VEHICLES_DATA_FILE);
            List<String> availableVehicles = records.stream()
                    .filter(record -> Integer.parseInt(record.split(",")[6]) > 0)
                    .collect(Collectors.toList());

            if (availableVehicles.isEmpty()) {
                System.out.println("No available schedules found.");
                return;
            }

            System.out.printf("%-40s%-10s%-25s%-25s%-25s%-10s%-15s%-10s\n", "Vehicle ID", "Type", "Name", "Origin", "Destination", "Total Seats", "Available Seats", "Price");
            System.out.println("---------------------------------------------------------------------------------------------------------------------------------");
            for (String recordData : availableVehicles) {
                String[] parts = recordData.split(",");
                System.out.printf("%-40s%-10s%-25s%-25s%-25s%-10s%-15s%-10s\n", parts[0], parts[1], parts[2], parts[3], parts[4], parts[5], parts[6], parts[7]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing available schedules.");
        }
    }

    /**
     * Allows a user to book a ticket for a vehicle.
     * @param currentUser The username of the user creating the booking.
     */
    private static void bookTicket(String currentUser) {
        System.out.println("\n--- Book a Ticket ---");
        viewAvailableVehicles();
        System.out.print("Enter the Vehicle ID you want to book a ticket for: ");
        String vehicleId = scanner.nextLine();

        try {
            List<String> vehicleRecords = loadDataFromFile(VEHICLES_DATA_FILE);
            boolean found = false;
            for (int i = 0; i < vehicleRecords.size(); i++) {
                String record = vehicleRecords.get(i);
                String[] parts = record.split(",");
                int availableSeats = Integer.parseInt(parts[6]);

                if (parts[0].equals(vehicleId) && availableSeats > 0) {
                    found = true;

                    // Update available seats
                    int newAvailableSeats = availableSeats - 1;
                    parts[6] = String.valueOf(newAvailableSeats);
                    vehicleRecords.set(i, String.join(",", parts));
                    saveDataToFile(vehicleRecords, VEHICLES_DATA_FILE);

                    // Generate a random seat number
                    int seatNumber = Integer.parseInt(parts[5]) - newAvailableSeats;

                    // Generate a booking ID and save the booking record
                    String bookingId = generateUniqueBookingId(currentUser);
                    // Format: bookingId,username,vehicleId,seatNumber,baseCost
                    String bookingRecord = bookingId + "," + currentUser + "," + vehicleId + "," + seatNumber + "," + parts[7];
                    List<String> bookingRecords = loadDataFromFile(BOOKINGS_DATA_FILE);
                    bookingRecords.add(bookingRecord);
                    saveDataToFile(bookingRecords, BOOKINGS_DATA_FILE);

                    System.out.println("Ticket booked successfully!");
                    System.out.println("Your Booking ID is: " + bookingId);
                    System.out.println("Your Seat Number is: " + seatNumber);
                    break;
                }
            }
            if (!found) {
                System.out.println("Vehicle " + vehicleId + " is full or does not exist.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred during booking.");
        } catch (NumberFormatException e) {
            System.out.println("Invalid data format for seats or price.");
        }
    }

    /**
     * Displays a logged-in user's personal booking history.
     * @param currentUser The username of the user.
     */
    private static void viewMyBookings(String currentUser) {
        System.out.println("\n--- My Bookings ---");
        try {
            List<String> bookingRecords = loadDataFromFile(BOOKINGS_DATA_FILE);
            List<String> userBookings = bookingRecords.stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .collect(Collectors.toList());

            if (userBookings.isEmpty()) {
                System.out.println("You have no bookings.");
                return;
            }

            System.out.printf("%-20s%-40s%-15s%-15s\n", "Booking ID", "Vehicle ID", "Seat Number", "Base Cost");
            System.out.println("-----------------------------------------------------------------------------------");
            for (String bookingData : userBookings) {
                String[] parts = bookingData.split(",");
                System.out.printf("%-20s%-40s%-15s%-15s\n", parts[0], parts[2], parts[3], parts[4]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing your bookings.");
        }
    }

    /**
     * Displays and manages the expense menu for a user's booking.
     * @param currentUser The username of the currently logged-in user.
     */
    private static void manageExpenses(String currentUser) {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Booking Expenses ---");
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
     * Adds a new expense record for a user's booking.
     * @param currentUser The username of the user.
     */
    private static void addNewExpense(String currentUser) {
        System.out.println("\n--- Add New Expense ---");
        try {
            List<String> userBookings = loadDataFromFile(BOOKINGS_DATA_FILE).stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .collect(Collectors.toList());

            if (userBookings.isEmpty()) {
                System.out.println("You have no bookings to add expenses to.");
                return;
            }

            System.out.println("Your bookings:");
            viewMyBookings(currentUser);

            System.out.print("Enter the Booking ID for which you want to add an expense: ");
            String bookingId = scanner.nextLine();

            boolean bookingFound = userBookings.stream().anyMatch(record -> record.split(",")[0].equals(bookingId));

            if (!bookingFound) {
                System.out.println("Booking not found or does not belong to you.");
                return;
            }

            System.out.print("Enter expense type (e.g., Luggage Fee, Special Services, Meal): ");
            String expenseType = scanner.nextLine();
            System.out.print("Enter expense amount: ");
            String amount = scanner.nextLine();

            // Format: bookingId,expenseType,amount
            String newExpense = bookingId + "," + expenseType + "," + amount;
            List<String> expenses = loadDataFromFile(EXPENSES_DATA_FILE);
            expenses.add(newExpense);
            saveDataToFile(expenses, EXPENSES_DATA_FILE);
            System.out.println("Expense added successfully for booking " + bookingId + ".");

        } catch (IOException e) {
            System.out.println("An error occurred while adding the expense.");
        }
    }

    /**
     * Displays all expense records for the current user's bookings.
     * @param currentUser The username of the user.
     */
    private static void viewMyExpenses(String currentUser) {
        System.out.println("\n--- My Expenses ---");
        try {
            List<String> allBookings = loadDataFromFile(BOOKINGS_DATA_FILE);
            List<String> expenseRecords = loadDataFromFile(EXPENSES_DATA_FILE);

            // Get all booking IDs for the current user
            List<String> userBookingIds = allBookings.stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .map(record -> record.split(",")[0])
                    .collect(Collectors.toList());

            if (userBookingIds.isEmpty()) {
                System.out.println("You have no bookings, and therefore no expenses.");
                return;
            }

            List<String> userExpenses = expenseRecords.stream()
                    .filter(record -> userBookingIds.contains(record.split(",")[0]))
                    .collect(Collectors.toList());

            if (userExpenses.isEmpty()) {
                System.out.println("No expenses found for your bookings.");
                return;
            }

            System.out.printf("%-20s%-20s%-15s\n", "Booking ID", "Expense Type", "Amount");
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

    /**
     * Handles the bill generation process and finalizes the booking.
     * @param currentUser The username of the user performing the checkout.
     */
    private static void generateBill(String currentUser) {
        System.out.println("\n--- Bill Generation ---");
        viewMyBookings(currentUser);
        System.out.print("Enter the Booking ID to generate a bill for: ");
        String bookingId = scanner.nextLine();

        try {
            List<String> allBookings = loadDataFromFile(BOOKINGS_DATA_FILE);
            List<String> allExpenses = loadDataFromFile(EXPENSES_DATA_FILE);

            // Step 1: Find the booking record for the given bookingId and user
            String bookingRecord = allBookings.stream()
                    .filter(record -> {
                        String[] parts = record.split(",");
                        return parts[0].equals(bookingId) && parts[1].equals(currentUser);
                    })
                    .findFirst()
                    .orElse(null);

            if (bookingRecord == null) {
                System.out.println("Booking not found or does not belong to you.");
                return;
            }

            String[] bookingParts = bookingRecord.split(",");
            String vehicleId = bookingParts[2];
            String seatNumber = bookingParts[3];
            double baseCost = Double.parseDouble(bookingParts[4]);

            // Step 2: Find and sum all additional expenses for this booking ID
            List<String[]> bookingExpenses = allExpenses.stream()
                    .filter(record -> record.split(",")[0].equals(bookingId))
                    .map(record -> record.split(","))
                    .collect(Collectors.toList());

            double totalExpenses = bookingExpenses.stream()
                    .mapToDouble(record -> Double.parseDouble(record[2]))
                    .sum();

            double totalBill = baseCost + totalExpenses;

            // Step 3: Generate and print the bill
            System.out.println("\n------------------------------------");
            System.out.println("            T I C K E T   B I L L       ");
            System.out.println("------------------------------------");
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Vehicle ID: " + vehicleId);
            System.out.println("Seat Number: " + seatNumber);
            System.out.println("------------------------------------");
            System.out.printf("Base Ticket Cost: %-14s $%s\n", "", df.format(baseCost));
            System.out.println("--- Additional Expenses ---");
            if (bookingExpenses.isEmpty()) {
                System.out.println("No additional expenses found.");
            } else {
                for (String[] expense : bookingExpenses) {
                    System.out.printf("%-20s $%s\n", expense[1] + ":", df.format(Double.parseDouble(expense[2])));
                }
            }
            System.out.println("------------------------------------");
            System.out.printf("Total Amount Due: %-14s $%s\n", "", df.format(totalBill));
            System.out.println("------------------------------------");

            // Step 4: Confirm and finalize
            System.out.print("\nConfirm payment and finalize? (yes/no): ");
            String confirmation = scanner.nextLine().trim().toLowerCase();

            if (confirmation.equals("yes")) {
                // Remove booking record
                allBookings.remove(bookingRecord);
                saveDataToFile(allBookings, BOOKINGS_DATA_FILE);

                // Remove expense records
                allExpenses.removeIf(record -> record.split(",")[0].equals(bookingId));
                saveDataToFile(allExpenses, EXPENSES_DATA_FILE);

                System.out.println("\nBill paid. Booking " + bookingId + " finalized.");
            } else {
                System.out.println("Finalization canceled.");
            }

        } catch (IOException e) {
            System.out.println("An error occurred during bill generation.");
            e.printStackTrace();
        } catch (NumberFormatException e) {
            System.out.println("Error calculating bill. Invalid cost or amount data.");
        }
    }

    // --- File & Helper Methods ---

    /**
     * Generates a unique booking ID based on the required format.
     * Format: 4 letters of username + current year + random 2 digits.
     *
     * @param name The user's username.
     * @return A unique booking ID as a String.
     * @throws IOException if there's an issue reading the data file.
     */
    private static String generateUniqueBookingId(String name) throws IOException {
        String newId;
        boolean isUnique;
        int currentYear = Year.now().getValue();
        do {
            // Get the first 4 letters of the name, handling names shorter than 4 characters
            String namePart = name.length() > 3 ? name.substring(0, 4).toUpperCase() : (name + "XXXX").substring(0, 4).toUpperCase();
            String yearPart = String.valueOf(currentYear);
            String randomPart = String.format("%02d", random.nextInt(100)); // Generate a random 2-digit number with leading zero
            newId = namePart + yearPart + randomPart;
            isUnique = isIdUnique(newId, BOOKINGS_DATA_FILE);
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
