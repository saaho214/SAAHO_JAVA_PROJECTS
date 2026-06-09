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

public class HotelBookingSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String ROOMS_DATA_FILE = "rooms.txt";
    private static final String BOOKINGS_DATA_FILE = "bookings.txt";
    private static final String EXPENSES_DATA_FILE = "expenses.txt"; // New file for expenses
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
                        System.out.println("Thank you for using the Hotel Booking System. Goodbye!");
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
                    System.out.println("Initial admin account created: username 'admin', password 'admin'.");
                    writer.write("admin,admin");
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
        System.out.println("  Hotel Room Booking System");
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

    //   Creates a new user or admin account and saves it to the specified file.
    
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
                userMenu(username); // Pass the username for personalized booking info
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
        System.out.println("1. Manage Rooms");
        System.out.println("2. View All Admins");
        System.out.println("3. View All Users");
        System.out.println("4. View Booking Batch Years");
        System.out.println("5. Checkout and Generate Bill");
        System.out.println("6. Logout");
        System.out.println("--------------------");
        System.out.print("Enter your choice: ");
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    manageRooms();
                    break;
                case 2:
                    viewAllAccounts(ADMIN_DATA_FILE, "Admin");
                    break;
                case 3:
                    viewAllAccounts(USER_DATA_FILE, "User");
                    break;
                case 4:
                    viewBookingBatchYears();
                    break;
                case 5:
                    checkoutAndGenerateBill(); // Call without arguments
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
     * Manages all room-related operations from the admin menu.
     */
    private static void manageRooms() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Rooms ---");
            System.out.println("1. Add a New Room");
            System.out.println("2. View All Rooms");
            System.out.println("3. Update a Room's Details");
            System.out.println("4. Delete a Room Record");
            System.out.println("5. Back to Admin Menu");
            System.out.println("-------------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addNewRoom();
                        break;
                    case 2:
                        viewAllRooms();
                        break;
                    case 3:
                        updateRoomDetails();
                        break;
                    case 4:
                        deleteRoomRecord();
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
     * Adds a new room to the hotel system.
     */
    private static void addNewRoom() {
        System.out.println("\n--- Add New Room ---");
        System.out.print("Enter room number: ");
        String roomNumber = scanner.nextLine();
        System.out.print("Enter room type (e.g., Single, Double, Suite): ");
        String roomType = scanner.nextLine();
        System.out.print("Enter price per night: ");
        String price = scanner.nextLine();

        try {
            // Format: roomNumber,roomType,price,status
            String newRoom = roomNumber + "," + roomType + "," + price + "," + "Available";
            List<String> records = loadDataFromFile(ROOMS_DATA_FILE);
            records.add(newRoom);
            saveDataToFile(records, ROOMS_DATA_FILE);
            System.out.println("New room added successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred during room addition.");
        }
    }

    /**
     * Updates an existing room's details.
     */
    private static void updateRoomDetails() {
        System.out.println("\n--- Update Room Details ---");
        System.out.print("Enter Room Number to update: ");
        String roomNumber = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(ROOMS_DATA_FILE);
            boolean found = false;
            for (int i = 0; i < records.size(); i++) {
                String record = records.get(i);
                String[] parts = record.split(",");

                if (parts[0].equals(roomNumber)) {
                    found = true;

                    System.out.println("Room found. Current details:");
                    System.out.println("Type: " + parts[1]);
                    System.out.println("Price: " + parts[2]);
                    System.out.println("Status: " + parts[3]);

                    System.out.print("Enter new room type (or press Enter to keep current): ");
                    String newType = scanner.nextLine();
                    if (!newType.isEmpty()) parts[1] = newType;

                    System.out.print("Enter new price (or press Enter to keep current): ");
                    String newPrice = scanner.nextLine();
                    if (!newPrice.isEmpty()) parts[2] = newPrice;

                    System.out.print("Enter new status (Available/Booked, or press Enter): ");
                    String newStatus = scanner.nextLine();
                    if (!newStatus.isEmpty()) parts[3] = newStatus;

                    // Reconstruct and save the updated record
                    records.set(i, String.join(",", parts));
                    saveDataToFile(records, ROOMS_DATA_FILE);
                    System.out.println("Room details updated successfully.");
                    break;
                }
            }
            if (!found) {
                System.out.println("Room with number " + roomNumber + " not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while updating the room record.");
        }
    }

    /**
     * Deletes a room record from the data file.
     */
    private static void deleteRoomRecord() {
        System.out.println("\n--- Delete Room Record ---");
        System.out.print("Enter Room Number to delete: ");
        String roomNumber = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(ROOMS_DATA_FILE);
            boolean removed = records.removeIf(record -> record.split(",")[0].equals(roomNumber));
            if (removed) {
                saveDataToFile(records, ROOMS_DATA_FILE);
                System.out.println("Room record for number " + roomNumber + " deleted successfully.");
            } else {
                System.out.println("Room record not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while deleting the record.");
        }
    }

    /**
     * Displays all room records in a tabular format.
     */
    private static void viewAllRooms() {
        System.out.println("\n--- All Room Records ---");
        try {
            List<String> records = loadDataFromFile(ROOMS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No room records found.");
                return;
            }

            // Print table header
            System.out.printf("%-15s%-15s%-15s%-15s\n", "Room Number", "Room Type", "Price", "Status");
            System.out.println("------------------------------------------------------------------");

            for (String recordData : records) {
                String[] parts = recordData.split(",");
                if (parts.length >= 4) {
                    System.out.printf("%-15s%-15s%-15s%-15s\n", parts[0], parts[1], parts[2], parts[3]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing records.");
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
                    .map(record -> record.split(",")[0]) // Get the customer ID
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

    //   Displays a list of all accounts from the specified file.
     
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


    // Handles the checkout process, generates a bill, and frees up the room.
     

private static void checkoutAndGenerateBill() {
    System.out.println("\n--- Checkout and Bill Generation ---");
    
    // Optional: If you have a viewAllBookings() method, call it here so the admin sees options.
    
    System.out.print("Enter the Customer ID for the booking to check out: ");
    String customerId = scanner.nextLine();

    try {
        // Step 1: Find the booking record strictly by customerId (Admin overrides user context)
        List<String> allBookings = loadDataFromFile(BOOKINGS_DATA_FILE);
        String bookingRecord = allBookings.stream()
                .filter(record -> {
                    String[] parts = record.split(",");
                    return parts[0].equals(customerId);
                })
                .findFirst()
                .orElse(null);

        if (bookingRecord == null) {
            System.out.println("Booking not found for the provided Customer ID.");
            return;
        }

        String roomNumber = bookingRecord.split(",")[2];

        // Step 2: Find the room's price
        List<String> allRooms = loadDataFromFile(ROOMS_DATA_FILE);
        String roomRecord = allRooms.stream()
                .filter(record -> record.split(",")[0].equals(roomNumber))
                .findFirst()
                .orElse(null);

        double roomPrice = (roomRecord != null) ? Double.parseDouble(roomRecord.split(",")[2]) : 0.0;

        // Step 3: Find and sum all expenses for this customer ID
        List<String> allExpenses = loadDataFromFile(EXPENSES_DATA_FILE);
        List<String> customerExpenses = allExpenses.stream()
                .filter(record -> record.split(",")[0].equals(customerId))
                .collect(Collectors.toList());

        double totalExpenses = customerExpenses.stream()
                .mapToDouble(record -> Double.parseDouble(record.split(",")[2]))
                .sum();

        double totalBill = roomPrice + totalExpenses;

        // Step 4: Generate and print the bill
        System.out.println("\n------------------------------------");
        System.out.println("         H O T E L   B I L L        ");
        System.out.println("------------------------------------");
        System.out.println("Customer ID: " + customerId);
        System.out.println("Room Number: " + roomNumber);
        System.out.println("------------------------------------");
        System.out.printf("Room Price: %-20s $%s\n", "", df.format(roomPrice));
        System.out.println("--- Additional Expenses ---");
        if (customerExpenses.isEmpty()) {
            System.out.println("No additional expenses found.");
        } else {
            for (String expense : customerExpenses) {
                String[] parts = expense.split(",");
                System.out.printf("%-20s $%s\n", parts[1] + ":", df.format(Double.parseDouble(parts[2])));
            }
        }
        System.out.println("------------------------------------");
        System.out.printf("Total Amount Due: %-14s $%s\n", "", df.format(totalBill));
        System.out.println("------------------------------------");

        // Step 5: Confirm checkout and update records
        System.out.print("\nConfirm checkout and process payment? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            // Remove booking record
            allBookings.remove(bookingRecord);
            saveDataToFile(allBookings, BOOKINGS_DATA_FILE);

            // Remove expense records
            allExpenses.removeIf(record -> record.split(",")[0].equals(customerId));
            saveDataToFile(allExpenses, EXPENSES_DATA_FILE);

            // Update room status
            for (int i = 0; i < allRooms.size(); i++) {
                String record = allRooms.get(i);
                String[] parts = record.split(",");
                if (parts[0].equals(roomNumber)) {
                    parts[3] = "Available";
                    allRooms.set(i, String.join(",", parts));
                    break;
                }
            }
            saveDataToFile(allRooms, ROOMS_DATA_FILE);

            System.out.println("\nCheckout successful! Room " + roomNumber + " is now available.");
        } else {
            System.out.println("Checkout canceled.");
        }

    } catch (IOException e) {
        System.out.println("An error occurred during checkout.");
        e.printStackTrace();
    } catch (NumberFormatException e) {
        System.out.println("Error calculating bill. Invalid price or amount data.");
    }
}

    // --- User Functionalities ---

    // Displays and manages the user menu options.
    
private static void userMenu(String currentUser) {
    boolean isRunning = true;
    while (isRunning) {
        System.out.println("\n--- User Menu ---");
        System.out.println("1. View Available Rooms");
        System.out.println("2. Book a Room");
        System.out.println("3. View My Bookings");
        System.out.println("4. Manage Expenses");
        System.out.println("5. Logout"); 
        System.out.println("-------------------");
        System.out.print("Enter your choice: ");
        try {
            int choice = scanner.nextInt();
            scanner.nextLine(); // Consume newline

            switch (choice) {
                case 1:
                    viewAvailableRooms();
                    break;
                case 2:
                    bookRoom(currentUser);
                    break;
                case 3:
                    viewMyBookings(currentUser);
                    break;
                case 4:
                    manageExpenses(currentUser);
                    break;
                case 5:
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

    // Displays and manages the expense menu for a user.
     
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

    // Adds a new expense record for a user's booking.
     
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

            System.out.print("Enter the Customer ID for the booking you want to add an expense to: ");
            String customerId = scanner.nextLine();

            boolean bookingFound = userBookings.stream().anyMatch(record -> record.split(",")[0].equals(customerId));

            if (!bookingFound) {
                System.out.println("Booking not found or does not belong to you.");
                return;
            }

            System.out.print("Enter expense type (e.g., Taxi, Food Service, Sale Offer): ");
            String expenseType = scanner.nextLine();
            System.out.print("Enter expense amount: ");
            String amount = scanner.nextLine();

            // Format: customerId,expenseType,amount
            String newExpense = customerId + "," + expenseType + "," + amount;
            List<String> expenses = loadDataFromFile(EXPENSES_DATA_FILE);
            expenses.add(newExpense);
            saveDataToFile(expenses, EXPENSES_DATA_FILE);
            System.out.println("Expense added successfully for booking " + customerId + ".");

        } catch (IOException e) {
            System.out.println("An error occurred while adding the expense.");
        }
    }

    // Displays all expense records for the current user.
     
    private static void viewMyExpenses(String currentUser) {
        System.out.println("\n--- My Expenses ---");
        try {
            List<String> bookingRecords = loadDataFromFile(BOOKINGS_DATA_FILE);
            List<String> expenseRecords = loadDataFromFile(EXPENSES_DATA_FILE);

            // Get all booking IDs for the current user
            List<String> userBookingIds = bookingRecords.stream()
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
     * Displays only the available rooms in a tabular format.
     */
    private static void viewAvailableRooms() {
        System.out.println("\n--- Available Rooms ---");
        try {
            List<String> records = loadDataFromFile(ROOMS_DATA_FILE);
            List<String> availableRooms = records.stream()
                    .filter(record -> record.split(",")[3].equals("Available"))
                    .collect(Collectors.toList());

            if (availableRooms.isEmpty()) {
                System.out.println("No available rooms found.");
                return;
            }

            System.out.printf("%-15s%-15s%-15s%-15s\n", "Room Number", "Room Type", "Price", "Status");
            System.out.println("------------------------------------------------------------------");
            for (String recordData : availableRooms) {
                String[] parts = recordData.split(",");
                System.out.printf("%-15s%-15s%-15s%-15s\n", parts[0], parts[1], parts[2], parts[3]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing available rooms.");
        }
    }

    // Allows a user to book an available room.
     
    private static void bookRoom(String currentUser) {
        System.out.println("\n--- Book a Room ---");
        viewAvailableRooms(); // Show available rooms first
        System.out.print("Enter the Room Number you want to book: ");
        String roomNumber = scanner.nextLine();

        try {
            List<String> roomRecords = loadDataFromFile(ROOMS_DATA_FILE);
            boolean found = false;
            for (int i = 0; i < roomRecords.size(); i++) {
                String record = roomRecords.get(i);
                String[] parts = record.split(",");

                if (parts[0].equals(roomNumber) && parts[3].equals("Available")) {
                    found = true;
                    // Change room status to "Booked"
                    parts[3] = "Booked";
                    roomRecords.set(i, String.join(",", parts));
                    saveDataToFile(roomRecords, ROOMS_DATA_FILE);

                    // Generate a customer ID and save the booking record
                    String customerId = generateUniqueCustomerId(currentUser);
                    // Format: customerId,username,roomNumber
                    String bookingRecord = customerId + "," + currentUser + "," + roomNumber;
                    List<String> bookingRecords = loadDataFromFile(BOOKINGS_DATA_FILE);
                    bookingRecords.add(bookingRecord);
                    saveDataToFile(bookingRecords, BOOKINGS_DATA_FILE);

                    System.out.println("Room " + roomNumber + " booked successfully!");
                    System.out.println("Your Customer ID is: " + customerId);
                    break;
                }
            }
            if (!found) {
                System.out.println("Room " + roomNumber + " is not available or does not exist.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred during booking.");
        }
    }

    // Displays a logged-in user's personal booking history.
     
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

            System.out.printf("%-15s%-15s%-15s\n", "Customer ID", "Username", "Room Number");
            System.out.println("--------------------------------------------------");
            for (String bookingData : userBookings) {
                String[] parts = bookingData.split(",");
                System.out.printf("%-15s%-15s%-15s\n", parts[0], parts[1], parts[2]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing your bookings.");
        }
    }

    // --- File & Helper Methods ---

    // Generates a unique customer ID based on the required format.
    //  Format: 4 letters of name + current year + random 2 digits.
     
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
            isUnique = isIdUnique(newId, BOOKINGS_DATA_FILE);
        } while (!isUnique);
        return newId;
    }

    //      Checks if an ID is unique by looking it up in the specified data file.
   
    private static boolean isIdUnique(String id, String filename) throws IOException {
        List<String> records = loadDataFromFile(filename);
        for (String recordData : records) {
            if (recordData.startsWith(id + ",")) {
                return false;
            }
        }
        return true;
    }

    //      Loads all records from a specified file into a List of strings.
     
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

    //   Saves a list of records to a file, overwriting existing content.
     
    private static void saveDataToFile(List<String> records, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String recordData : records) {
                writer.write(recordData);
                writer.newLine();
            }
        }
    }
}
