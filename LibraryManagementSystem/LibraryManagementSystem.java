import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.InputMismatchException;
import java.util.Random;
import java.util.stream.Collectors;
import java.util.Map;
import java.util.LinkedHashMap;
import java.util.UUID;
import java.util.Arrays;

public class LibraryManagementSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String STUDENT_DATA_FILE = "students.txt";
    private static final String ADMIN_DATA_FILE = "admins.txt";
    private static final String BOOK_DATA_FILE = "books.txt";
    private static final Random random = new Random();
    
    // Standard parameters for our new billing feature
    private static final double BASE_RENTAL_FEE = 2.00;
    private static final double LATE_FEE_PER_DAY = 0.50;

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
                        studentLogin();
                        break;
                    case 3:
                        handleNewRegistration();
                        break;
                    case 4:
                        System.out.println("Thank you for using the Library Management System. Goodbye!");
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
        System.out.println("  Library Management System");
        System.out.println("------------------------------------");
        System.out.println("1. Login as Admin");
        System.out.println("2. Login as Student");
        System.out.println("3. Register New Account");
        System.out.println("4. Exit");
        System.out.println("------------------------------------");
        System.out.print("Enter your choice: ");
    }

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
                scanner.nextLine(); 

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

    private static void adminMenu() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Admin Menu ---");
            System.out.println("1. Manage Books");
            System.out.println("2. Manage Students");
            System.out.println("3. View Admins");
            System.out.println("4. Track Book Borrowers (Who has what?)");
            System.out.println("5. View/Generate Bill for a Student");
            System.out.println("6. Logout");
            System.out.println("--------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        manageBooks();
                        break;
                    case 2:
                        manageStudents();
                        break;
                    case 3:
                        viewAllAdmins();
                        break;
                    case 4:
                        trackBookBorrowers();
                        break;
                    case 5:
                        adminGenerateBillView();
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

    private static void manageBooks() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Books ---");
            System.out.println("1. Add a New Book");
            System.out.println("2. View All Books");
            System.out.println("3. Delete a Book");
            System.out.println("4. Back to Admin Menu");
            System.out.println("----------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        addBook();
                        break;
                    case 2:
                        viewAllBooks();
                        break;
                    case 3:
                        deleteBook();
                        break;
                    case 4:
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

    private static void addBook() {
        System.out.println("\n--- Add New Book ---");
        System.out.print("Enter book title: ");
        String title = scanner.nextLine();
        System.out.print("Enter book author: ");
        String author = scanner.nextLine();
        String bookId = "BOOK-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        String newRecord = bookId + "," + title + "," + author + "," + "Available";

        try {
            List<String> books = loadDataFromFile(BOOK_DATA_FILE);
            books.add(newRecord);
            saveDataToFile(books, BOOK_DATA_FILE);
            System.out.println("Book added successfully!");
            System.out.println("Book ID: " + bookId);
        } catch (IOException e) {
            System.out.println("An error occurred while adding the book.");
        }
    }

    private static void deleteBook() {
        System.out.println("\n--- Delete Book ---");
        System.out.print("Enter book ID to delete: ");
        String bookId = scanner.nextLine();

        try {
            List<String> books = loadDataFromFile(BOOK_DATA_FILE);
            boolean removed = books.removeIf(book -> book.startsWith(bookId + ","));
            if (removed) {
                saveDataToFile(books, BOOK_DATA_FILE);
                System.out.println("Book with ID " + bookId + " deleted successfully.");
            } else {
                System.out.println("Book not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while deleting the book.");
        }
    }

    private static void manageStudents() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Students ---");
            System.out.println("1. Create New Student Account");
            System.out.println("2. View All Student Records");
            System.out.println("3. Delete Student Record");
            System.out.println("4. View Student Batches");
            System.out.println("5. Back to Admin Menu");
            System.out.println("-------------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); 

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
                        viewStudentBatches();
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

    private static void createStudentAccount() {
        System.out.println("\n--- Create New Student Account ---");
        System.out.print("Enter student name: ");
        String name = scanner.nextLine();
        System.out.print("Enter student PIN (4 digits): ");
        String pin = scanner.nextLine();
        System.out.print("Enter student's batch year (e.g., 2023): ");
        String batchYear = scanner.nextLine();

        if (pin.length() != 4) {
            System.out.println("PIN must be 4 digits. Account creation failed.");
            return;
        }

        try {
            String studentId = generateUniqueId(STUDENT_DATA_FILE, "student", name, batchYear);
            // Ensuring terminating comma structure for initialization of borrowed string
            String newRecord = studentId + "," + pin + "," + name + "," + batchYear + ",";
            List<String> studentRecords = loadDataFromFile(STUDENT_DATA_FILE);
            studentRecords.add(newRecord);
            saveDataToFile(studentRecords, STUDENT_DATA_FILE);
            System.out.println("Student account created successfully!");
            System.out.println("Student ID: " + studentId);
        } catch (IOException e) {
            System.out.println("An error occurred during account creation.");
        }
    }

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

    private static void viewAllStudentRecords() {
        System.out.println("\n--- All Student Records ---");
        try {
            List<String> records = loadDataFromFile(STUDENT_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No student records found.");
                return;
            }

            System.out.printf("%-15s%-10s%-25s%-15s\n", "Student ID", "PIN", "Name", "Batch Year");
            System.out.println("------------------------------------------------------------------");

            for (String recordData : records) {
                String[] parts = recordData.split(",", -1);
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

    private static void viewStudentBatches() {
        System.out.println("\n--- Student Batches ---");
        try {
            List<String> records = loadDataFromFile(STUDENT_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No student records found.");
                return;
            }

            Map<String, Long> batchCounts = records.stream()
                    .map(record -> record.split(",", -1)[3])
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

    private static void viewAllAdmins() {
        System.out.println("\n--- All Admins ---");
        try {
            List<String> admins = loadDataFromFile(ADMIN_DATA_FILE);
            if (admins.isEmpty()) {
                System.out.println("No admin accounts found.");
                return;
            }
            System.out.printf("%-20s%-20s\n", "Username", "Password");
            System.out.println("----------------------------------------");
            for (String admin : admins) {
                String[] parts = admin.split(",", -1);
                System.out.printf("%-20s%-20s\n", parts[0], parts[1]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while retrieving admin data.");
        }
    }

    /**
     * Admin Side: Displays which user has borrowed which book.
     */
    private static void trackBookBorrowers() {
        System.out.println("\n--- Book Rental Tracker (Active Loans) ---");
        try {
            List<String> students = loadDataFromFile(STUDENT_DATA_FILE);
            List<String> books = loadDataFromFile(BOOK_DATA_FILE);
            
            // Map books for quick access/lookup definitions
            Map<String, String> bookMap = new LinkedHashMap<>();
            for (String b : books) {
                String[] bParts = b.split(",", -1);
                if (bParts.length >= 3) {
                    bookMap.put(bParts[0], bParts[1] + " by " + bParts[2]);
                }
            }

            boolean trackFound = false;
            System.out.printf("%-15s%-20s%-15s%-40s\n", "Student ID", "Student Name", "Book ID", "Book Details");
            System.out.println("------------------------------------------------------------------------------------");

            for (String sRecord : students) {
                String[] sParts = sRecord.split(",", -1);
                if (sParts.length > 4 && !sParts[4].trim().isEmpty()) {
                    String studentId = sParts[0];
                    String studentName = sParts[2];
                    String[] borrowedBookIds = sParts[4].split(";");

                    for (String bId : borrowedBookIds) {
                        if (!bId.trim().isEmpty()) {
                            String bookInfo = bookMap.getOrDefault(bId, "Unknown Title (Book record might have changed)");
                            System.out.printf("%-15s%-20s%-15s%-40s\n", studentId, studentName, bId, bookInfo);
                            trackFound = true;
                        }
                    }
                }
            }

            if (!trackFound) {
                System.out.println("No books are currently checked out by any student.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred while building tracking lists.");
        }
    }

    /**
     * Admin Side: Interface logic selection layer to prompt search parameter and construct invoice
     */
    private static void adminGenerateBillView() {
        System.out.println("\n--- Admin Billing Desk ---");
        System.out.print("Enter the Student ID to generate bill: ");
        String inputId = scanner.nextLine();
        generateBillComputation(inputId);
    }


    // --- Student Functionalities ---

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

    private static void studentMenu(String studentId) {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Student Menu ---");
            System.out.println("1. View All Books");
            System.out.println("2. Borrow a Book");
            System.out.println("3. Return a Book");
            System.out.println("4. View My Borrowed Books");
            System.out.println("5. View My Record");
            System.out.println("6. Generate My Current Bill Statement");
            System.out.println("7. Logout");
            System.out.println("-------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); 

                switch (choice) {
                    case 1:
                        viewAllBooks();
                        break;
                    case 2:
                        borrowBook(studentId);
                        break;
                    case 3:
                        returnBook(studentId);
                        break;
                    case 4:
                        viewMyBorrowedBooks(studentId);
                        break;
                    case 5:
                        viewMyRecord(studentId);
                        break;
                    case 6:
                        generateBillComputation(studentId);
                        break;
                    case 7:
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

    private static void viewAllBooks() {
        System.out.println("\n--- All Books ---");
        try {
            List<String> records = loadDataFromFile(BOOK_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No book records found.");
                return;
            }
            System.out.printf("%-15s%-25s%-25s%-15s\n", "Book ID", "Title", "Author", "Status");
            System.out.println("------------------------------------------------------------------------");
            for (String recordData : records) {
                String[] parts = recordData.split(",", -1);
                String bookId = parts[0];
                String title = parts[1];
                String author = parts[2];
                String status = parts[3];
                System.out.printf("%-15s%-25s%-25s%-15s\n", bookId, title, author, status);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing book records.");
        }
    }

    private static void borrowBook(String studentId) {
        System.out.println("\n--- Borrow a Book ---");
        System.out.print("Enter the Book ID you want to borrow: ");
        String bookIdToBorrow = scanner.nextLine();

        try {
            List<String> books = loadDataFromFile(BOOK_DATA_FILE);
            List<String> updatedBooks = new ArrayList<>();
            boolean bookFound = false;

            for (String bookRecord : books) {
                String[] parts = bookRecord.split(",", -1);
                if (parts[0].equals(bookIdToBorrow)) {
                    bookFound = true;
                    if (parts[3].equalsIgnoreCase("Available")) {
                        String updatedBookRecord = parts[0] + "," + parts[1] + "," + parts[2] + "," + "Borrowed";
                        updatedBooks.add(updatedBookRecord);

                        List<String> students = loadDataFromFile(STUDENT_DATA_FILE);
                        List<String> updatedStudents = new ArrayList<>();
                        for (String studentRecord : students) {
                            String[] studentParts = studentRecord.split(",", -1);
                            if (studentParts[0].equals(studentId)) {
                                String borrowedBooks = studentParts.length > 4 ? studentParts[4] : "";
                                if (!borrowedBooks.isEmpty()) {
                                    borrowedBooks += ";";
                                }
                                borrowedBooks += bookIdToBorrow;
                                String updatedStudentRecord = studentParts[0] + "," + studentParts[1] + "," + studentParts[2] + "," + studentParts[3] + "," + borrowedBooks;
                                updatedStudents.add(updatedStudentRecord);
                            } else {
                                updatedStudents.add(studentRecord);
                            }
                        }
                        saveDataToFile(updatedStudents, STUDENT_DATA_FILE);
                        System.out.println("Book borrowed successfully!");
                    } else {
                        System.out.println("This book is currently not available.");
                        updatedBooks.add(bookRecord);
                    }
                } else {
                    updatedBooks.add(bookRecord);
                }
            }

            if (!bookFound) {
                System.out.println("Book with ID " + bookIdToBorrow + " not found.");
            }
            saveDataToFile(updatedBooks, BOOK_DATA_FILE);

        } catch (IOException e) {
            System.out.println("An error occurred during the borrowing process.");
        }
    }

    private static void returnBook(String studentId) {
        System.out.println("\n--- Return a Book ---");
        System.out.print("Enter the Book ID you want to return: ");
        String bookIdToReturn = scanner.nextLine();

        try {
            List<String> students = loadDataFromFile(STUDENT_DATA_FILE);
            String studentRecord = null;
            for (String s : students) {
                if (s.startsWith(studentId + ",")) {
                    studentRecord = s;
                    break;
                }
            }

            if (studentRecord != null) {
                String[] studentParts = studentRecord.split(",", -1);
                String borrowedBooksStr = studentParts.length > 4 ? studentParts[4] : "";
                List<String> borrowedBooks = new ArrayList<>(Arrays.asList(borrowedBooksStr.split(";")));

                if (borrowedBooks.contains(bookIdToReturn)) {
                    // Show a quick return statement statement bill summary prior to wipe out
                    System.out.println("\nProcessing return invoices...");
                    int simulatedDaysHeld = random.nextInt(20) + 1; // Generates mock ownership days 
                    double dynamicLateFee = 0.0;
                    if (simulatedDaysHeld > 14) { // over 14 days baseline threshold 
                        dynamicLateFee = (simulatedDaysHeld - 14) * LATE_FEE_PER_DAY;
                    }
                    System.out.println("=========================================");
                    System.out.println("       RETURN BOOK BILL RECEIPT          ");
                    System.out.println("=========================================");
                    System.out.println("Returned Book ID : " + bookIdToReturn);
                    System.out.println("Days Borrowed    : " + simulatedDaysHeld + " Days");
                    System.out.printf("Base Processing Fee: $%.2f\n", BASE_RENTAL_FEE);
                    System.out.printf("Overdue Accrual Late Fee: $%.2f\n", dynamicLateFee);
                    System.out.printf("Total Paid at Counter: $%.2f\n", (BASE_RENTAL_FEE + dynamicLateFee));
                    System.out.println("=========================================");

                    borrowedBooks.remove(bookIdToReturn);
                    String updatedBorrowedBooksStr = String.join(";", borrowedBooks);
                    String updatedStudentRecord = studentParts[0] + "," + studentParts[1] + "," + studentParts[2] + "," + studentParts[3] + "," + updatedBorrowedBooksStr;

                    List<String> updatedStudents = new ArrayList<>();
                    for (String s : students) {
                        if (s.startsWith(studentId + ",")) {
                            updatedStudents.add(updatedStudentRecord);
                        } else {
                            updatedStudents.add(s);
                        }
                    }
                    saveDataToFile(updatedStudents, STUDENT_DATA_FILE);

                    List<String> books = loadDataFromFile(BOOK_DATA_FILE);
                    List<String> updatedBooks = new ArrayList<>();
                    boolean bookFound = false;
                    for (String bookRecord : books) {
                        String[] bookParts = bookRecord.split(",", -1);
                        if (bookParts[0].equals(bookIdToReturn)) {
                            String updatedBookRecord = bookParts[0] + "," + bookParts[1] + "," + bookParts[2] + "," + "Available";
                            updatedBooks.add(updatedBookRecord);
                            bookFound = true;
                        } else {
                            updatedBooks.add(bookRecord);
                        }
                    }
                    saveDataToFile(updatedBooks, BOOK_DATA_FILE);

                    if (bookFound) {
                        System.out.println("Book returned successfully!");
                    } else {
                        System.out.println("Book was not found in the library's records.");
                    }
                } else {
                    System.out.println("You have not borrowed this book.");
                }
            } else {
                System.out.println("Student record not found.");
            }
        } catch (IOException e) {
            System.out.println("An error occurred during the return process.");
        }
    }

    private static void viewMyBorrowedBooks(String studentId) {
        System.out.println("\n--- My Borrowed Books ---");
        try {
            List<String> students = loadDataFromFile(STUDENT_DATA_FILE);
            String borrowedBooksStr = "";
            for (String studentRecord : students) {
                if (studentRecord.startsWith(studentId + ",")) {
                    String[] parts = studentRecord.split(",", -1);
                    if (parts.length > 4) {
                        borrowedBooksStr = parts[4];
                    }
                    break;
                }
            }

            if (borrowedBooksStr.isEmpty()) {
                System.out.println("You have not borrowed any books.");
                return;
            }

            List<String> borrowedBookIds = Arrays.asList(borrowedBooksStr.split(";"));
            List<String> books = loadDataFromFile(BOOK_DATA_FILE);

            System.out.printf("%-15s%-30s%-25s\n", "Book ID", "Title", "Author");
            System.out.println("----------------------------------------------------------------");

            for (String bookRecord : books) {
                String bookId = bookRecord.split(",", -1)[0];
                if (borrowedBookIds.contains(bookId)) {
                    String[] parts = bookRecord.split(",", -1);
                    System.out.printf("%-15s%-30s%-25s\n", parts[0], parts[1], parts[2]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while retrieving your borrowed books.");
        }
    }

    private static void viewMyRecord(String studentId) {
        System.out.println("\n--- My Student Record ---");
        try {
            List<String> records = loadDataFromFile(STUDENT_DATA_FILE);
            for (String recordData : records) {
                if (recordData.startsWith(studentId + ",")) {
                    String[] parts = recordData.split(",", -1);
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
     * Shared Shared Method Engine: Shared by Admin & User components.
     * Computes real time outstanding values and structures clean statement tables.
     */
    private static void generateBillComputation(String studentId) {
        try {
            List<String> students = loadDataFromFile(STUDENT_DATA_FILE);
            String targetRecord = null;
            
            for (String s : students) {
                if (s.startsWith(studentId + ",")) {
                    targetRecord = s;
                    break;
                }
            }

            if (targetRecord == null) {
                System.out.println("Error: Student record matching ID '" + studentId + "' could not be found.");
                return;
            }

            String[] sParts = targetRecord.split(",", -1);
            String studentName = sParts[2];
            String borrowedBooksStr = sParts.length > 4 ? sParts[4] : "";

            System.out.println("\n========================================================");
            System.out.println("             LIBRARY SYSTEM ACCOUNT STATEMENT           ");
            System.out.println("========================================================");
            System.out.println("Student ID   : " + studentId);
            System.out.println("Student Name : " + studentName);
            System.out.println("Invoice Date : Current Session");
            System.out.println("--------------------------------------------------------");

            if (borrowedBooksStr.trim().isEmpty()) {
                System.out.println(" No outstanding balances or active books borrowed.");
                System.out.println(" Current Pending Outstanding Due: $0.00");
                System.out.println("========================================================");
                return;
            }

            String[] borrowedIds = borrowedBooksStr.split(";");
            List<String> books = loadDataFromFile(BOOK_DATA_FILE);
            Map<String, String> bookTitles = new LinkedHashMap<>();
            for (String b : books) {
                String[] bParts = b.split(",", -1);
                bookTitles.put(bParts[0], bParts[1]);
            }

            double compoundTotal = 0.0;
            System.out.printf("%-12s%-20s%-12s%-10s\n", "Book ID", "Title", "Days (Mock)", "Charges");
            System.out.println("--------------------------------------------------------");

            for (String bId : borrowedIds) {
                if (!bId.trim().isEmpty()) {
                    String title = bookTitles.getOrDefault(bId, "Unknown Title Item");
                    
                    // Simulating different dates held for open outstanding accounts
                    int mockDaysHeld = random.nextInt(25) + 2; 
                    double lateFeeItem = 0.0;
                    if (mockDaysHeld > 14) {
                        lateFeeItem = (mockDaysHeld - 14) * LATE_FEE_PER_DAY;
                    }
                    double totalItemCost = BASE_RENTAL_FEE + lateFeeItem;
                    compoundTotal += totalItemCost;

                    String truncatedTitle = title.length() > 18 ? title.substring(0, 15) + "..." : title;
                    System.out.printf("%-12s%-20s%-12d$%-10.2f\n", bId, truncatedTitle, mockDaysHeld, totalItemCost);
                }
            }

            System.out.println("--------------------------------------------------------");
            System.out.printf(" TOTAL ACCRUED OUTSTANDING BALANCE: $%.2f\n", compoundTotal);
            System.out.println("========================================================");
            System.out.println("Notice: Please clear overdue values to prevent service limits.");

        } catch (IOException e) {
            System.out.println("An error occurred during computing operational bill statement entries.");
        }
    }


    // --- File & Helper Methods ---

    private static String generateUniqueId(String filename, String type, String name, String batchYear) throws IOException {
        String newId;
        boolean isUnique;
        do {
            if ("student".equals(type)) {
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
        List<String> lines = loadDataFromFile(filename);
        for (String line : lines) {
            if (line.startsWith(id + ",")) {
                return false; 
            }
        }
        return true; 
    }

    private static List<String> loadDataFromFile(String filename) throws IOException {
        List<String> lines = new ArrayList<>();
        File file = new File(filename);
        if (!file.exists()) {
            file.createNewFile();
            return lines;
        }
        try (BufferedReader reader = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = reader.readLine()) != null) {
                if (!line.trim().isEmpty()) {
                    lines.add(line);
                }
            }
        }
        return lines;
    }

    private static void saveDataToFile(List<String> data, String filename) throws IOException {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (String line : data) {
                writer.write(line);
                writer.newLine();
            }
        }
    }
}