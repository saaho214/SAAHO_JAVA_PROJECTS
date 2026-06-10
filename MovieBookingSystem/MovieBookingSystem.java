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
import java.util.Date;
import java.text.SimpleDateFormat;
import java.util.Collections;

public class MovieBookingSystem {

    private static final Scanner scanner = new Scanner(System.in);
    private static final String MOVIES_DATA_FILE = "movies.txt";
    private static final String BOOKINGS_DATA_FILE = "bookings.txt";
    private static final String ADMIN_DATA_FILE = "admins.txt";
    private static final String USER_DATA_FILE = "users.txt";
    private static final Random random = new Random();
    private static final SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    private static final DecimalFormat df = new DecimalFormat("#.00");

    public static void main(String[] args) {
        // Main application loop
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
                        System.out.println("Thank you for using the Movie Booking System. Goodbye!");
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
        System.out.println("  Movie Ticket Booking System");
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
            if (accounts.stream().anyMatch(acc -> acc.split(",")[0].equals(username))) {
                System.out.println("This username already exists.");
                return;
            }
            String newRecord = username + "," + password;
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
            System.out.println("1. Manage Movies");
            System.out.println("2. View All Admins");
            System.out.println("3. View All Users");
            System.out.println("4. View All Booking History");
            System.out.println("5. View History by Batch Year");
            System.out.println("6. Logout");
            System.out.println("--------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        manageMovies();
                        break;
                    case 2:
                        viewAllAccounts(ADMIN_DATA_FILE, "Admin");
                        break;
                    case 3:
                        viewAllAccounts(USER_DATA_FILE, "User");
                        break;
                    case 4:
                        viewAllBookingHistory();
                        break;
                    case 5:
                        viewHistoryBatchYears();
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
     * Manages all movie-related operations from the admin menu.
     */
    private static void manageMovies() {
        boolean isRunning = true;
        while (isRunning) {
            System.out.println("\n--- Manage Movies ---");
            System.out.println("1. Add a New Movie");
            System.out.println("2. View All Movies");
            System.out.println("3. Back to Admin Menu");
            System.out.println("-------------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addNewMovie();
                        break;
                    case 2:
                        viewAllMovies(true);
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
     * Allows admin to add a new movie with its details.
     */
    private static void addNewMovie() {
        System.out.println("\n--- Add New Movie ---");
        System.out.print("Enter movie title: ");
        String title = scanner.nextLine();
        System.out.print("Enter genre: ");
        String genre = scanner.nextLine();
        System.out.print("Enter showtimes (comma-separated, e.g., 10:00 AM,02:30 PM): ");
        String showtimes = scanner.nextLine();
        System.out.print("Enter price per ticket: ");
        double price = 0.0;
        try {
            price = scanner.nextDouble();
            scanner.nextLine();
        } catch (InputMismatchException e) {
            System.out.println("Invalid price. Please enter a number.");
            scanner.nextLine();
            return;
        }

        String movieId = UUID.randomUUID().toString();

        try {
            List<String> movies = loadDataFromFile(MOVIES_DATA_FILE);
            String newRecord = movieId + "," + title + "," + genre + "," + showtimes + "," + df.format(price);
            movies.add(newRecord);
            saveDataToFile(movies, MOVIES_DATA_FILE);
            System.out.println("Movie '" + title + "' added successfully! Movie ID: " + movieId);
        } catch (IOException e) {
            System.out.println("An error occurred while adding the movie.");
        }
    }

    // Views all movies in a tabular format.
   
    private static void viewAllMovies(boolean isAdmin) {
        System.out.println("\n--- All Movies ---");
        try {
            List<String> movies = loadDataFromFile(MOVIES_DATA_FILE);
            if (movies.isEmpty()) {
                System.out.println("No movies found.");
                return;
            }

            if (isAdmin) {
                System.out.printf("%-40s%-25s%-15s%-30s%-15s\n", "Movie ID", "Title", "Genre", "Showtimes", "Price");
                System.out.println("-----------------------------------------------------------------------------------------------");
                for (String movieData : movies) {
                    String[] parts = movieData.split(",");
                    System.out.printf("%-40s%-25s%-15s%-30s%-15s\n", parts[0], parts[1], parts[2], parts[3], "$" + parts[4]);
                }
            } else {
                System.out.printf("%-25s%-15s%-30s%-15s\n", "Title", "Genre", "Showtimes", "Price");
                System.out.println("----------------------------------------------------------------------");
                for (String movieData : movies) {
                    String[] parts = movieData.split(",");
                    System.out.printf("%-25s%-15s%-30s%-15s\n", parts[1], parts[2], parts[3], "$" + parts[4]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing movies.");
        }
    }

    /**
     * Displays all booking records in a tabular format (for Admin).
     */
    private static void viewAllBookingHistory() {
        System.out.println("\n--- All Booking History ---");
        try {
            List<String> records = loadDataFromFile(BOOKINGS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No bookings found.");
                return;
            }

            System.out.printf("%-20s%-15s%-40s%-15s%-10s%-10s%-15s\n", "Booking ID", "User", "Movie ID", "Showtime", "Tickets", "Total", "Date");
            System.out.println("-----------------------------------------------------------------------------------------------------------------");
            for (String bookingData : records) {
                String[] parts = bookingData.split(",");
                if (parts.length >= 7) {
                    System.out.printf("%-20s%-15s%-40s%-15s%-10s%-10s%-15s\n", parts[0], parts[1], parts[2], parts[3], parts[4], "$" + parts[5], parts[6]);
                }
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing all history.");
        }
    }

    /**
     * Displays a summary of bookings grouped by their booking year.
     */
    private static void viewHistoryBatchYears() {
        System.out.println("\n--- Booking History Batch Years ---");
        try {
            List<String> records = loadDataFromFile(BOOKINGS_DATA_FILE);
            if (records.isEmpty()) {
                System.out.println("No bookings found.");
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
            System.out.println("1. Book a Ticket");
            System.out.println("2. View My Bookings");
            System.out.println("3. Generate Bill");
            System.out.println("4. Logout");
            System.out.println("-------------------");
            System.out.print("Enter your choice: ");
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        bookTicket(currentUser);
                        break;
                    case 2:
                        viewMyBookings(currentUser);
                        break;
                    case 3:
                        generateBill(currentUser);
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
     * Allows a user to book a movie ticket.
     * @param currentUser The username of the user booking the ticket.
     */
    private static void bookTicket(String currentUser) {
        System.out.println("\n--- Book a Ticket ---");
        viewAllMovies(false);

        System.out.print("Enter the title of the movie you want to book: ");
        String movieTitle = scanner.nextLine();

        try {
            List<String> allMovies = loadDataFromFile(MOVIES_DATA_FILE);
            String movieData = allMovies.stream()
                    .filter(m -> m.split(",")[1].equalsIgnoreCase(movieTitle))
                    .findFirst()
                    .orElse(null);

            if (movieData == null) {
                System.out.println("Movie not found.");
                return;
            }

            String[] movieParts = movieData.split(",");
            String movieId = movieParts[0];
            String[] showtimes = movieParts[3].split(",");
            double price = Double.parseDouble(movieParts[4]);

            System.out.println("\nAvailable Showtimes for '" + movieTitle + "':");
            for (int i = 0; i < showtimes.length; i++) {
                System.out.println((i + 1) + ". " + showtimes[i].trim());
            }

            System.out.print("Enter your choice (1-" + showtimes.length + "): ");
            int showtimeChoice = scanner.nextInt();
            scanner.nextLine();

            if (showtimeChoice < 1 || showtimeChoice > showtimes.length) {
                System.out.println("Invalid showtime choice.");
                return;
            }
            String selectedShowtime = showtimes[showtimeChoice - 1].trim();

            System.out.print("Enter number of tickets: ");
            int numTickets = scanner.nextInt();
            scanner.nextLine();

            double totalPrice = numTickets * price;

            // Generate a booking ID and save the booking record
            String bookingId = generateUniqueBookingId(currentUser);
            // Format: bookingID,username,movieID,showtime,numTickets,totalPrice,date
            String bookingRecord = bookingId + "," + currentUser + "," + movieId + "," + selectedShowtime + "," + numTickets + "," + df.format(totalPrice) + "," + dateFormat.format(new Date());

            List<String> bookingRecords = loadDataFromFile(BOOKINGS_DATA_FILE);
            bookingRecords.add(bookingRecord);
            saveDataToFile(bookingRecords, BOOKINGS_DATA_FILE);

            System.out.println("\nBooking successful! Your booking ID is: " + bookingId);
            System.out.println("Total price: $" + df.format(totalPrice));

        } catch (IOException e) {
            System.out.println("An error occurred while trying to book a ticket.");
        } catch (NumberFormatException e) {
            System.out.println("Error parsing movie or price data.");
        } catch (InputMismatchException e) {
            System.out.println("Invalid input. Please enter a number.");
            scanner.nextLine();
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
                System.out.println("You have no bookings recorded yet.");
                return;
            }

            System.out.printf("%-20s%-40s%-15s%-10s%-10s%-15s\n", "Booking ID", "Movie ID", "Showtime", "Tickets", "Total", "Date");
            System.out.println("---------------------------------------------------------------------------------------------------");
            for (String bookingData : userBookings) {
                String[] parts = bookingData.split(",");
                System.out.printf("%-20s%-40s%-15s%-10s%-10s%-15s\n", parts[0], parts[2], parts[3], parts[4], "$" + parts[5], parts[6]);
            }
        } catch (IOException e) {
            System.out.println("An error occurred while viewing your bookings.");
        }
    }

    /**
     * Generates a bill/receipt for a specific booking.
     * @param currentUser The username of the user.
     */
    private static void generateBill(String currentUser) {
        System.out.println("\n--- Bill Generation ---");
        viewMyBookings(currentUser);
        System.out.print("Enter the Booking ID to generate a bill for: ");
        String bookingId = scanner.nextLine();

        try {
            List<String> allBookings = loadDataFromFile(BOOKINGS_DATA_FILE);
            List<String> allMovies = loadDataFromFile(MOVIES_DATA_FILE);

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
            String movieId = bookingParts[2];
            String showtime = bookingParts[3];
            int numTickets = Integer.parseInt(bookingParts[4]);
            double totalPrice = Double.parseDouble(bookingParts[5]);
            String date = bookingParts[6];

            String movieRecord = allMovies.stream()
                    .filter(record -> record.split(",")[0].equals(movieId))
                    .findFirst()
                    .orElse(null);

            String movieTitle = (movieRecord != null) ? movieRecord.split(",")[1] : "Unknown Movie";

            System.out.println("\n------------------------------------");
            System.out.println("        M O V I E   T I C K E T   B I L L       ");
            System.out.println("------------------------------------");
            System.out.println("Booking ID: " + bookingId);
            System.out.println("Username: " + currentUser);
            System.out.println("Date: " + date);
            System.out.println("------------------------------------");
            System.out.println("Movie: " + movieTitle);
            System.out.println("Showtime: " + showtime);
            System.out.println("Tickets: " + numTickets);
            System.out.println("Total Amount: $" + df.format(totalPrice));
            System.out.println("------------------------------------");
            System.out.println("Thank you for your booking!");
            System.out.println("------------------------------------");

        } catch (IOException e) {
            System.out.println("An error occurred during bill generation.");
        } catch (NumberFormatException e) {
            System.out.println("Error processing booking data.");
        }
    }

    // --- File & Helper Methods ---

    // Generates a unique booking ID based on the required format.
     // Format: 4 letters of username + current year + random 2 digits.
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
