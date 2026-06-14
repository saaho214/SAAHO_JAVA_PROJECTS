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

public class RestaurantSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String MENU_DATA_FILE = "menu.txt";
    private static final String ORDERS_DATA_FILE = "orders.txt";
    private static final String EXPENSES_DATA_FILE = "expenses.txt";
    private static final String ADMIN_DATA_FILE = "admins.txt";
    private static final String USER_DATA_FILE = "users.txt";
    private static final Random random = new Random();
    private static final DecimalFormat df = new DecimalFormat("#.00");

    public static void main(String[] args) {
        boolean isRunning = true;
        createInitialAdmin();
        while (isRunning) {
            displayMainMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine();

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
                        System.out.println("Thank you for using the Restaurant Order Management System. Goodbye!");
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
        scanner.close();
    }

    // Creates an initial default admin account if the admin data file is empty.

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
        System.out.println("  Restaurant Order Management System");
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
            System.out.println("1. Manage Menu Items");
            System.out.println("2. View All Admins");
            System.out.println("3. View All Users");
            System.out.println("4. View All Orders");
            System.out.println("5. View Order Batch Years");
            System.out.println("6. Logout");
            System.out.println("--------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        manageMenuItems();
                        break;
                    case 2:
                        viewAllAccounts(ADMIN_DATA_FILE, "Admin");
                        break;
                    case 3:
                        viewAllAccounts(USER_DATA_FILE, "User");
                        break;
                    case 4:
                        viewAllOrders();
                        break;
                    case 5:
                        viewOrderBatchYears();
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
     * Manages all menu-related operations from the admin menu.
     */
    private static void manageMenuItems() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Menu Items ---");
            System.out.println("1. Add a New Menu Item");
            System.out.println("2. View All Menu Items");
            System.out.println("3. Update a Menu Item's Details");
            System.out.println("4. Delete a Menu Item Record");
            System.out.println("5. Back to Admin Menu");
            System.out.println("-------------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addNewMenuItem();
                        break;
                    case 2:
                        viewAllMenuItems();
                        break;
                    case 3:
                        updateMenuItemDetails();
                        break;
                    case 4:
                        deleteMenuItemRecord();
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
     * Adds a new menu item to the restaurant system.
     */
    private static void addNewMenuItem() {
        System.out.println("\n--- Add New Menu Item ---");
        System.out.print("Enter item name: ");
        String itemName = scanner.nextLine();
        System.out.print("Enter item category (e.g., Appetizer, Main Course, Dessert, Drink): ");
        String itemCategory = scanner.nextLine();
        System.out.print("Enter price: ");
        String price = scanner.nextLine();

        try {
            // Format: itemId,itemName,itemCategory,price
            String newMenuItem = UUID.randomUUID().toString() + "," + itemName + "," + itemCategory + "," + price;
            List<String> records = loadDataFromFile(MENU_DATA_FILE);
            records.add(newMenuItem);
            saveDataToFile(records, MENU_DATA_FILE);
            System.out.println("New menu item added successfully!");
        } catch (IOException e) {
            System.out.println("An error occurred during menu item addition.");
        }
    }

    /**
     * Updates an existing menu item's details.
     */
    private static void updateMenuItemDetails() {
        System.out.println("\n--- Update Menu Item Details ---");
        viewAllMenuItems();
        System.out.print("Enter Item ID to update: ");
        String itemId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(MENU_DATA_FILE);
            boolean found = false;
            for (int i = 0; i < records.size(); i++) {
                String record = records.get(i);
                String[] parts = record.split(",");

                if (parts[0].equals(itemId)) {
                    found = true;

                    System.out.println("Item found. Current details:");
                    System.out.println("Name: " + parts[1]);
                    System.out.println("Category: " + parts[2]);
                    System.out.println("Price: " + parts[3]);

                    System.out.print("Enter new item name (or press Enter to keep current): ");
                    String newName = scanner.nextLine();
                    if (!newName.isEmpty()) parts[1] = newName;

                    System.out.print("Enter new category (or press Enter to keep current): ");
                    String newCategory = scanner.nextLine();
                    if (!newCategory.isEmpty()) parts[2] = newCategory;

                    System.out.print("Enter new price (or press Enter to keep current): ");
                    String newPrice = scanner.nextLine();
                    if (!newPrice.isEmpty()) parts[3] = newPrice;

                    // Reconstruct and save the updated record
                    records.set(i, String.join(",", parts));
                    saveDataToFile(records, MENU_DATA_FILE);
                    System.out.println("Menu item details updated successfully.");
                    break;
                }
            }
            if (!found) {
                System.out.println("Menu item with ID " + itemId + " not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while updating the menu item record.");
        }
    }

    /**
     * Deletes a menu item record from the data file.
     */
    private static void deleteMenuItemRecord() {
        System.out.println("\n--- Delete Menu Item Record ---");
        viewAllMenuItems();
        System.out.print("Enter Item ID to delete: ");
        String itemId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(MENU_DATA_FILE);
            boolean removed = records.removeIf(record -> record.split(",")[0].equals(itemId));
            if (removed) {
                saveDataToFile(records, MENU_DATA_FILE);
                System.out.println("Menu item record for ID " + itemId + " deleted successfully.");
            } else {
                System.out.println("Menu item record not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while deleting the record.");
        }
    }

    /**
     * Displays all menu item records in a tabular format.
     */
    private static void viewAllMenuItems() {
        System.out.println("\n--- All Menu Items ---");
        try {
            List<String> records = loadDataFromFile(MENU_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No menu items found.");
                return;
            }

            // Print table header
            System.out.printf("%-40s%-25s%-15s%-15s\n", "Item ID", "Item Name", "Category", "Price");
            System.out.println("------------------------------------------------------------------------------------------");

            for (String recordData : records) {
                String[] parts = recordData.split(",");
                if (parts.length >= 4) {
                    System.out.printf("%-40s%-25s%-15s%-15s\n", parts[0], parts[1], parts[2], parts[3]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing records.");
        }
    }

    // Displays all order records in a tabular format (for Admin).
     
    private static void viewAllOrders() {
        System.out.println("\n--- All Orders ---");
        try {
            List<String> records = loadDataFromFile(ORDERS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No order records found.");
                return;
            }

            System.out.printf("%-15s%-15s%-25s\n", "Customer ID", "Username", "Item ID");
            System.out.println("---------------------------------------------------------");
            for (String orderData : records) {
                String[] parts = orderData.split(",");
                System.out.printf("%-15s%-15s%-25s\n", parts[0], parts[1], parts[2]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing all orders.");
        }
    }

    // Displays a summary of orders grouped by their booking year.
     
    private static void viewOrderBatchYears() {
        System.out.println("\n--- Order Batch Years ---");
        try {
            List<String> records = loadDataFromFile(ORDERS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No order records found.");
                return;
            }

            Map<String, Long> yearCounts = records.stream()
                    .map(record -> record.split(",")[0]) // Get the customer ID
                    .map(id -> id.substring(id.length() - 6, id.length() - 2)) // Extract the year part
                    .collect(Collectors.groupingBy(orderYear -> orderYear, Collectors.counting()));

            System.out.printf("%-15s%-15s\n", "Order Year", "Total Orders");
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

    // Displays and manages the user menu options.

    private static void userMenu(String currentUser) {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. View Menu");
            System.out.println("2. Place an Order");
            System.out.println("3. View My Orders");
            System.out.println("4. Manage Expenses for my Orders");
            System.out.println("5. Checkout and Generate Bill");
            System.out.println("6. Logout");
            System.out.println("-------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        viewAllMenuItems();
                        break;
                    case 2:
                        placeOrder(currentUser);
                        break;
                    case 3:
                        viewMyOrders(currentUser);
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

    // Allows a user to place a new order.
private static void placeOrder(String currentUser) {
    System.out.println("\n--- Place a New Order ---");
    try {
        viewAllMenuItems();
        List<String> menuRecords = loadDataFromFile(MENU_DATA_FILE);
        if (menuRecords.isEmpty()) {
            System.out.println("The menu is empty. No items to order.");
            return;
        }

        // Create a new order with a unique customer ID
        String customerId = generateUniqueCustomerId(currentUser);
        List<String> newOrderItems = new ArrayList<>();
        boolean isOrdering = true;

        while (isOrdering) {
            System.out.print("Enter Item ID to add to order (or 'done' to finish): ");
            String itemId = scanner.nextLine().trim();

            if (itemId.equalsIgnoreCase("done")) {
                isOrdering = false;
                continue;
            }

            String menuItem = menuRecords.stream()
                    .filter(record -> record.split(",")[0].equals(itemId))
                    .findFirst()
                    .orElse(null);

            if (menuItem != null) {
                // Format: customerId,username,itemId
                newOrderItems.add(customerId + "," + currentUser + "," + itemId);
                System.out.println("Item added to your order.");
            } else {
                System.out.println("Invalid Item ID. Please try again.");
            }
        }

        if (!newOrderItems.isEmpty()) {
            // Directly saving without loading old orders—safer and faster!
            saveDataToFile(newOrderItems, ORDERS_DATA_FILE);
            System.out.println("\nOrder placed successfully!");
            System.out.println("Your Customer ID for this order is: " + customerId);
        } else {
            System.out.println("Order was not placed. No items were added.");
        }

    } catch (IOException e) {
        System.out.println("An error occurred during placing the order.");
    }
}

    // Displays a logged-in user's personal order history.
     
    private static void viewMyOrders(String currentUser) {
        System.out.println("\n--- My Orders ---");
        try {
            List<String> allOrders = loadDataFromFile(ORDERS_DATA_FILE);
            List<String> userOrders = allOrders.stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .collect(Collectors.toList());

            if (userOrders.isEmpty()) {
                System.out.println("You have no orders.");
                return;
            }

            System.out.printf("%-15s%-15s%-25s\n", "Customer ID", "Username", "Item ID");
            System.out.println("--------------------------------------------------");
            for (String orderData : userOrders) {
                String[] parts = orderData.split(",");
                System.out.printf("%-15s%-15s%-25s\n", parts[0], parts[1], parts[2]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing your orders.");
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
                scanner.nextLine();

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

    // Adds a new expense record for a user's order.

    private static void addNewExpense(String currentUser) {
        System.out.println("\n--- Add New Expense ---");
        try {
            List<String> userOrders = loadDataFromFile(ORDERS_DATA_FILE).stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .collect(Collectors.toList());

            if (userOrders.isEmpty()) {
                System.out.println("You have no orders to add expenses to.");
                return;
            }

            System.out.println("Your orders:");
            viewMyOrders(currentUser);

            System.out.print("Enter the Customer ID for the order you want to add an expense to: ");
            String customerId = scanner.nextLine();

            boolean orderFound = userOrders.stream().anyMatch(record -> record.split(",")[0].equals(customerId));

            if (!orderFound) {
                System.out.println("Order not found or does not belong to you.");
                return;
            }

            System.out.print("Enter expense type (e.g., Delivery Fee, Tip, Discount): ");
            String expenseType = scanner.nextLine();
            System.out.print("Enter expense amount: ");
            String amount = scanner.nextLine();

            // Format: customerId,expenseType,amount
            String newExpense = customerId + "," + expenseType + "," + amount;
            List<String> expenses = loadDataFromFile(EXPENSES_DATA_FILE);
            expenses.add(newExpense);
            saveDataToFile(expenses, EXPENSES_DATA_FILE);
            System.out.println("Expense added successfully for order " + customerId + ".");

        } catch (IOException e) {
            System.out.println("An error occurred while adding the expense.");
        }
    }

    // Displays all expense records for the current user.

    private static void viewMyExpenses(String currentUser) {
        System.out.println("\n--- My Expenses ---");
        try {
            List<String> allOrders = loadDataFromFile(ORDERS_DATA_FILE);
            List<String> expenseRecords = loadDataFromFile(EXPENSES_DATA_FILE);

            // Get all customer IDs for the current user
            List<String> userCustomerIds = allOrders.stream()
                    .filter(record -> record.split(",")[1].equals(currentUser))
                    .map(record -> record.split(",")[0])
                    .collect(Collectors.toList());

            if (userCustomerIds.isEmpty()) {
                System.out.println("You have no orders, and therefore no expenses.");
                return;
            }

            List<String> userExpenses = expenseRecords.stream()
                    .filter(record -> userCustomerIds.contains(record.split(",")[0]))
                    .collect(Collectors.toList());

            if (userExpenses.isEmpty()) {
                System.out.println("No expenses found for your orders.");
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

    // Handles the checkout process, generates a bill, and finalizes the order.
    
private static void checkoutAndGenerateBill(String currentUser) {
    System.out.println("\n--- Checkout and Bill Generation ---");
    viewMyOrders(currentUser);
    System.out.print("Enter the Customer ID for the order to check out: ");
    String customerId = scanner.nextLine().trim();

    try {
        List<String> allOrders = loadDataFromFile(ORDERS_DATA_FILE);
        List<String> allMenu = loadDataFromFile(MENU_DATA_FILE);
        List<String> allExpenses = loadDataFromFile(EXPENSES_DATA_FILE);

        // Step 1: Find all items in the order for the given customerId
        List<String[]> orderItems = allOrders.stream()
                .filter(record -> {
                    String[] parts = record.split(",");
                    return parts[0].equals(customerId) && parts[1].equals(currentUser);
                })
                .map(record -> record.split(","))
                .collect(Collectors.toList());

        if (orderItems.isEmpty()) {
            System.out.println("Order not found or does not belong to you.");
            return;
        }

        // Step 2: Calculate the subtotal from the menu items
        double subtotal = 0.0;
        Map<String, Double> itemPrices = new LinkedHashMap<>();

        for (String[] orderItem : orderItems) {
            String itemId = orderItem[2];
            String menuRecord = allMenu.stream()
                    .filter(record -> record.split(",")[0].equals(itemId))
                    .findFirst()
                    .orElse(null);

            if (menuRecord != null) {
                double price = Double.parseDouble(menuRecord.split(",")[3]);
                String itemName = menuRecord.split(",")[1];
                subtotal += price;
                itemPrices.put(itemName, itemPrices.getOrDefault(itemName, 0.0) + price);
            }
        }

        // Step 3: Find and sum all additional expenses for this customer ID
        List<String[]> customerExpenses = allExpenses.stream()
                .filter(record -> record.split(",")[0].equals(customerId))
                .map(record -> record.split(","))
                .collect(Collectors.toList());

        double totalExpenses = customerExpenses.stream()
                .mapToDouble(record -> Double.parseDouble(record[2]))
                .sum();

        double totalBill = subtotal + totalExpenses;

        // Step 4: Generate and print the bill
        System.out.println("\n------------------------------------");
        System.out.println("         R E S T A U R A N T   B I L L       ");
        System.out.println("------------------------------------");
        System.out.println("Customer ID: " + customerId);
        System.out.println("------------------------------------");
        System.out.println("--- Order Items ---");
        for (Map.Entry<String, Double> entry : itemPrices.entrySet()) {
            System.out.printf("%-20s $%s\n", entry.getKey(), df.format(entry.getValue()));
        }
        System.out.printf("Subtotal: %-20s $%s\n", "", df.format(subtotal));
        System.out.println("------------------------------------");
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

        // Step 5: FIXED - Confirm checkout and retain records
        System.out.print("\nConfirm checkout and pay bill? (yes/no): ");
        String confirmation = scanner.nextLine().trim().toLowerCase();

        if (confirmation.equals("yes")) {
            // Data deletion code has been removed. Old information stays safe.
            System.out.println("\nCheckout successful! Thank you for your business.");
        } else {
            System.out.println("Checkout canceled.");
        }

    } catch (IOException e) {
        System.out.println("An error occurred during checkout.");
    } catch (NumberFormatException e) {
        System.out.println("Error calculating bill. Invalid price or amount data.");
    }
}

   // --- File & Helper Methods ---

// Generates a unique customer ID based on the required format.
private static String generateUniqueCustomerId(String name) throws IOException {
    String newId;
    boolean isUnique;
    int currentYear = Year.now().getValue();
    do {
        String namePart = name.length() > 3 ? name.substring(0, 4).toUpperCase() : (name + "XXXX").substring(0, 4).toUpperCase();
        String yearPart = String.valueOf(currentYear);
        String randomPart = String.format("%02d", random.nextInt(100)); 
        newId = namePart + yearPart + randomPart;
        isUnique = isIdUnique(newId, ORDERS_DATA_FILE);
    } while (!isUnique);
    return newId;
}

// Checks if an ID is unique by looking it up in the specified data file.
private static boolean isIdUnique(String id, String filename) throws IOException {
    List<String> records = loadDataFromFile(filename);
    for (String recordData : records) {
        if (recordData.startsWith(id + ",")) {
            return false;
        }
    }
    return true;
}

// Loads all records from a specified file into a List of strings.
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

// FIXED: Saves a list of records to a file, APPENDING content instead of wiping it.
private static void saveDataToFile(List<String> records, String filename) throws IOException {
    // Passing 'true' guarantees that old data is never cleared automatically
    try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename, true))) {
        for (String recordData : records) {
            writer.write(recordData);
            writer.newLine();
        }
    }
}
}