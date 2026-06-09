import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.UUID;

public class InventoryManagementSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String INVENTORY_DATA_FILE = "inventory.txt";
    private static final String ADMIN_DATA_FILE = "admins.txt";
    private static final String USER_DATA_FILE = "users.txt";
    private static final String SALES_DATA_FILE = "sales.txt"; // New file to track transactions
    private static final Random random = new Random();
    private static String currentLoggedInUser = ""; // Track who is logged in

    public static void main(String[] args) {
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
                        userLogin();
                        break;
                    case 3:
                        handleNewRegistration();
                        break;
                    case 4:
                        System.out.println("Thank you for using the Inventory Management System. Goodbye!");
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

    private static void displayMainMenu() {
        System.out.println("\n------------------------------------");
        System.out.println("   Inventory Management System");
        System.out.println("------------------------------------");
        System.out.println("1. Login as Admin");
        System.out.println("2. Login as User");
        System.out.println("3. Register New Account");
        System.out.println("4. Exit");
        System.out.println("------------------------------------");
        System.out.print("Enter your choice: ");
    }

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
                scanner.nextLine(); 

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
                currentLoggedInUser = username;
                adminMenu();
            } else {
                System.out.println("Invalid admin credentials. Access denied.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the admin data file.");
        }
    }

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
                currentLoggedInUser = username;
                userMenu();
            } else {
                System.out.println("Invalid user credentials. Access denied.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while reading the user data file.");
        }
    }

    // --- Admin Functionalities ---

    private static void adminMenu() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Manage Inventory");
            System.out.println("2. View All Admins");
            System.out.println("3. View All Users");
            System.out.println("4. View Inventory Batch Years");
            System.out.println("5. Generate Bill / Print Receipt");
            System.out.println("6. Sales Report & Total Revenue");
            System.out.println("7. Restock Low Stock Items");
            System.out.println("8. Logout");
            System.out.println("--------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1: manageInventory(); break;
                    case 2: viewAllAccounts(ADMIN_DATA_FILE, "Admin"); break;
                    case 3: viewAllAccounts(USER_DATA_FILE, "User"); break;
                    case 4: viewInventoryBatchYears(); break;
                    case 5: generateReceipt(); break;
                    case 6: viewSalesReport(); break;
                    case 7: restockLowStockItems(); break;
                    case 8:
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

    private static void manageInventory() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Inventory ---");
            System.out.println("1. Add a New Item");
            System.out.println("2. View All Inventory");
            System.out.println("3. Update an Item's Details");
            System.out.println("4. Delete an Item Record");
            System.out.println("5. Back to Admin Menu");
            System.out.println("-------------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1: addNewItem(); break;
                    case 2: viewAllInventory(); break;
                    case 3: updateItemDetails(); break;
                    case 4: deleteItemRecord(); break;
                    case 5: isRunning = false; break;
                    default: System.out.println("Invalid choice. Please try again.");
                }
            } catch (InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine();
            }
        }
    }

    private static void addNewItem() {
        System.out.println("\n--- Add New Inventory Item ---");
        System.out.print("Enter item name: ");
        String name = scanner.nextLine();
        System.out.print("Enter item category: ");
        String category = scanner.nextLine();
        System.out.print("Enter quantity: ");
        String quantity = scanner.nextLine();
        System.out.print("Enter price: ");
        String price = scanner.nextLine();
        System.out.print("Enter batch/joining year (e.g., 2024): ");
        String batchYear = scanner.nextLine();

        try {
            String itemId = generateUniqueId(INVENTORY_DATA_FILE, "item", name, batchYear);
            String newItem = itemId + "," + name + "," + category + "," + quantity + "," + price + "," + batchYear;
            List<String> records = loadDataFromFile(INVENTORY_DATA_FILE);
            records.add(newItem);
            saveDataToFile(records, INVENTORY_DATA_FILE);
            System.out.println("New item added successfully! Item ID: " + itemId);
        } catch (IOException e) {
            System.out.println("An error occurred during item addition.");
        }
    }

    private static void updateItemDetails() {
        System.out.println("\n--- Update Item Details ---");
        System.out.print("Enter Item ID to update: ");
        String itemId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(INVENTORY_DATA_FILE);
            boolean found = false;
            for (int i = 0; i < records.size(); i++) {
                String record = records.get(i);
                if (record.startsWith(itemId + ",")) {
                    found = true;
                    String[] parts = record.split(",");

                    System.out.println("Item found. Current details:");
                    System.out.println("Name: " + parts[1] + " | Category: " + parts[2] + " | Qty: " + parts[3] + " | Price: " + parts[4]);

                    System.out.print("Enter new name (or press Enter to keep current): ");
                    String newName = scanner.nextLine();
                    if (!newName.isEmpty()) parts[1] = newName;

                    System.out.print("Enter new category (or press Enter to keep current): ");
                    String newCategory = scanner.nextLine();
                    if (!newCategory.isEmpty()) parts[2] = newCategory;

                    System.out.print("Enter new quantity (or press Enter to keep current): ");
                    String newQuantity = scanner.nextLine();
                    if (!newQuantity.isEmpty()) parts[3] = newQuantity;

                    System.out.print("Enter new price (or press Enter to keep current): ");
                    String newPrice = scanner.nextLine();
                    if (!newPrice.isEmpty()) parts[4] = newPrice;

                    records.set(i, String.join(",", parts));
                    saveDataToFile(records, INVENTORY_DATA_FILE);
                    System.out.println("Item details updated successfully.");
                    break;
                }
            }
            if (!found) System.out.println("Item with ID " + itemId + " not found.");
        } catch (IOException e) {
            System.out.println("An error occurred while updating the record.");
        }
    }

    private static void deleteItemRecord() {
        System.out.println("\n--- Delete Item Record ---");
        System.out.print("Enter Item ID to delete: ");
        String itemId = scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(INVENTORY_DATA_FILE);
            boolean removed = records.removeIf(record -> record.startsWith(itemId + ","));
            if (removed) {
                saveDataToFile(records, INVENTORY_DATA_FILE);
                System.out.println("Item record for ID " + itemId + " deleted successfully.");
            } else {
                System.out.println("Item record not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while deleting the record.");
        }
    }

    private static void viewAllInventory() {
        System.out.println("\n--- All Inventory Records ---");
        try {
            List<String> records = loadDataFromFile(INVENTORY_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No inventory records found.");
                return;
            }
            System.out.printf("%-15s%-25s%-15s%-10s%-10s%-15s\n", "Item ID", "Name", "Category", "Qty", "Price", "Batch Year");
            System.out.println("------------------------------------------------------------------------------------");
            for (String recordData : records) {
                String[] parts = recordData.split(",");
                if (parts.length >= 6) {
                    System.out.printf("%-15s%-25s%-15s%-10s%-10s%-15s\n", parts[0], parts[1], parts[2], parts[3], parts[4], parts[5]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing records.");
        }
    }

    private static void viewInventoryBatchYears() {
        System.out.println("\n--- Inventory Batch Years ---");
        try {
            List<String> records = loadDataFromFile(INVENTORY_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No inventory records found.");
                return;
            }
            Map<String, Long> yearCounts = records.stream()
                    .map(record -> record.split(",")[5])
                    .collect(Collectors.groupingBy(batchYear -> batchYear, Collectors.counting()));

            System.out.printf("%-15s%-15s\n", "Batch Year", "Total Items");
            System.out.println("------------------------------");
            yearCounts.entrySet().stream()
                    .sorted(Map.Entry.comparingByKey())
                    .forEach(entry -> System.out.printf("%-15s%-15d\n", entry.getKey(), entry.getValue()));
        } catch (IOException e) {
            System.out.println("An error occurred while retrieving year data.");
        }
    }

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
            System.out.println("An error occurred while retrieving data.");
        }
    }

    // --- NEW ADMIN FEATURES ---

    private static void generateReceipt() {
        System.out.println("\n--- Generate Bill / Print Receipt ---");
        System.out.print("Enter Sale/Transaction ID: ");
        String saleId = scanner.nextLine();
        try {
            List<String> sales = loadDataFromFile(SALES_DATA_FILE);
            boolean found = false;
            for (String sale : sales) {
                String[] parts = sale.split(",");
                if (parts[0].equals(saleId)) {
                    found = true;
                    System.out.println("\n=================================");
                    System.out.println("        OFFICIAL RECEIPT         ");
                    System.out.println("=================================");
                    System.out.println("Transaction ID: " + parts[0]);
                    System.out.println("Customer User:  " + parts[1]);
                    System.out.println("Item ID:        " + parts[2]);
                    System.out.println("Item Name:      " + parts[3]);
                    System.out.println("Quantity Bought: " + parts[4]);
                    System.out.println("Unit Price:     $" + parts[5]);
                    System.out.println("---------------------------------");
                    System.out.printf("TOTAL AMOUNT:   $%s\n", parts[6]);
                    System.out.println("=================================");
                    break;
                }
            }
            if (!found) System.out.println("Transaction record not found.");
        } catch (IOException e) {
            System.out.println("Error reading sales records.");
        }
    }

    private static void viewSalesReport() {
        System.out.println("\n--- Sales Report & Financial Performance ---");
        try {
            List<String> sales = loadDataFromFile(SALES_DATA_FILE);
            if (sales.isEmpty()) {
                System.out.println("No sales transactions recorded yet.");
                return;
            }
            double totalRevenue = 0;
            int totalItemsSold = 0;

            System.out.printf("%-15s%-15s%-10s%-10s%-10s\n", "Txn ID", "Buyer", "Item", "Qty", "Total ($)");
            System.out.println("------------------------------------------------------------");
            for (String sale : sales) {
                String[] parts = sale.split(",");
                System.out.printf("%-15s%-15s%-10s%-10s%-10s\n", parts[0], parts[1], parts[3], parts[4], parts[6]);
                totalItemsSold += Integer.parseInt(parts[4]);
                totalRevenue += Double.parseDouble(parts[6]);
            }
            System.out.println("------------------------------------------------------------");
            System.out.printf("Total Units Sold: %d\n", totalItemsSold);
            System.out.printf("Gross Revenue   : $%.2f\n", totalRevenue);
        } catch (IOException e) {
            System.out.println("Error processing financial data.");
        }
    }

    private static void restockLowStockItems() {
        System.out.println("\n--- Restock Low Stock Alert ---");
        System.out.print("Enter minimum safe inventory threshold quantity: ");
        int threshold = scanner.nextInt();
        scanner.nextLine();

        try {
            List<String> records = loadDataFromFile(INVENTORY_DATA_FILE);
            List<Integer> lowStockIndices = new ArrayList<>();

            System.out.printf("\n%-10s%-15s%-25s%-10s\n", "Index", "Item ID", "Name", "Current Qty");
            System.out.println("------------------------------------------------------------");
            for (int i = 0; i < records.size(); i++) {
                String[] parts = records.get(i).split(",");
                int qty = Integer.parseInt(parts[3]);
                if (qty <= threshold) {
                    System.out.printf("%-10d%-15s%-25s%-10d\n", i, parts[0], parts[1], qty);
                    lowStockIndices.add(i);
                }
            }

            if (lowStockIndices.isEmpty()) {
                System.out.println("All product stocks are well above standard thresholds.");
                return;
            }

            System.out.print("\nEnter the product system Index value to restock (or -1 to exit): ");
            int targetIdx = scanner.nextInt();
            scanner.nextLine();

            if (lowStockIndices.contains(targetIdx)) {
                System.out.print("Enter additional inventory quantity units to add: ");
                int addedStock = scanner.nextInt();
                scanner.nextLine();

                String[] parts = records.get(targetIdx).split(",");
                int finalQty = Integer.parseInt(parts[3]) + addedStock;
                parts[3] = String.valueOf(finalQty);

                records.set(targetIdx, String.join(",", parts));
                saveDataToFile(records, INVENTORY_DATA_FILE);
                System.out.println("Stock updated successfully.");
            }
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error modifying target resource values.");
        }
    }


    // --- User Functionalities ---

    private static void userMenu() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- User Menu ---");
            System.out.println("1. View All Inventory");
            System.out.println("2. Purchase a Product");
            System.out.println("3. Search Product Portfolio");
            System.out.println("4. View Personal Purchase History");
            System.out.println("5. Logout");
            System.out.println("-------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1: viewAllInventory(); break;
                    case 2: purchaseProduct(); break;
                    case 3: searchProduct(); break;
                    case 4: viewUserPurchaseHistory(); break;
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

    // --- NEW USER FEATURES ---

    private static void purchaseProduct() {
        System.out.println("\n--- Purchase Product ---");
        System.out.print("Enter Item ID to buy: ");
        String itemId = scanner.nextLine();
        System.out.print("Enter quantity to purchase: ");
        int qtyToBuy = scanner.nextInt();
        scanner.nextLine(); 

        if (qtyToBuy <= 0) {
            System.out.println("Quantity must be greater than zero.");
            return;
        }

        try {
            List<String> records = loadDataFromFile(INVENTORY_DATA_FILE);
            boolean found = false;
            for (int i = 0; i < records.size(); i++) {
                String record = records.get(i);
                if (record.startsWith(itemId + ",")) {
                    found = true;
                    String[] parts = record.split(",");
                    int currentQty = Integer.parseInt(parts[3]);
                    double unitPrice = Double.parseDouble(parts[4]);

                    if (currentQty < qtyToBuy) {
                        System.out.println("Insufficient stock available! Current stock: " + currentQty);
                        return;
                    }

                    // Deduct stock quantity
                    int totalRemaining = currentQty - qtyToBuy;
                    parts[3] = String.valueOf(totalRemaining);
                    records.set(i, String.join(",", parts));
                    saveDataToFile(records, INVENTORY_DATA_FILE);

                    // Document the invoice
                    double totalCost = qtyToBuy * unitPrice;
                    String transactionId = "TXN" + random.nextInt(100000);
                    // Format: transactionId, user, itemId, itemName, qtyPurchased, unitPrice, totalCost
                    String saleRecord = transactionId + "," + currentLoggedInUser + "," + parts[0] + "," + parts[1] + "," + qtyToBuy + "," + unitPrice + "," + totalCost;
                    
                    List<String> sales = loadDataFromFile(SALES_DATA_FILE);
                    sales.add(saleRecord);
                    saveDataToFile(sales, SALES_DATA_FILE);

                    System.out.println("\nPurchase completed successfully!");
                    System.out.printf("Total Cost Charged: $%.2f\n", totalCost);
                    System.out.println("Your Transaction ID is: " + transactionId + " (Share with Admin to generate bill)");
                    break;
                }
            }
            if (!found) System.out.println("Target product index record was not matched.");
        } catch (IOException | NumberFormatException e) {
            System.out.println("Error processing standard market transaction operations.");
        }
    }

    private static void searchProduct() {
        System.out.println("\n--- Search Inventory Portfolio ---");
        System.out.print("Enter product search query term (Name or Category matching): ");
        String query = scanner.nextLine().toLowerCase();

        try {
            List<String> records = loadDataFromFile(INVENTORY_DATA_FILE);
            System.out.printf("\n%-15s%-25s%-15s%-10s%-10s\n", "Item ID", "Name", "Category", "Qty", "Price");
            System.out.println("-----------------------------------------------------------------------------");
            boolean found = false;
            for (String record : records) {
                String[] parts = record.split(",");
                if (parts[1].toLowerCase().contains(query) || parts[2].toLowerCase().contains(query)) {
                    System.out.printf("%-15s%-25s%-15s%-10s%-10s\n", parts[0], parts[1], parts[2], parts[3], parts[4]);
                    found = true;
                }
            }
            if (!found) System.out.println("No products matched the search query parameters.");
        } catch (IOException e) {
            System.out.println("Failed to perform target query search matching safely.");
        }
    }

    private static void viewUserPurchaseHistory() {
        System.out.println("\n--- Your Purchase History ---");
        try {
            List<String> sales = loadDataFromFile(SALES_DATA_FILE);
            System.out.printf("%-15s%-15s%-10s%-10s\n", "Transaction ID", "Product", "Qty", "Total Paid ($)");
            System.out.println("------------------------------------------------------------");
            boolean found = false;
            for (String sale : sales) {
                String[] parts = sale.split(",");
                if (parts[1].equals(currentLoggedInUser)) {
                    System.out.printf("%-15s%-15s%-10s%-10s\n", parts[0], parts[3], parts[4], parts[6]);
                    found = true;
                }
            }
            if (!found) System.out.println("No matching sales history records found for account user: " + currentLoggedInUser);
        } catch (IOException e) {
            System.out.println("Error reading the underlying execution history database logs.");
        }
    }

    // --- File & Helper Methods ---

    private static String generateUniqueId(String filename, String type, String name, String batchYear) throws IOException {
        String newId;
        boolean isUnique;
        do {
            if ("item".equals(type)) {
                String namePart = name.length() > 3 ? name.substring(0, 4).toUpperCase() : (name + "XXXX").substring(0, 4).toUpperCase();
                String randomPart = String.format("%02d", random.nextInt(100)); 
                newId = namePart + batchYear + randomPart;
            } else {
                newId = UUID.randomUUID().toString(); 
            }
            isUnique = isIdUnique(newId, filename);
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