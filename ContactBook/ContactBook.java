

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.stream.Collectors;

/**
 * A command-line application for a Contact Book.
 * It allows users to save, search, update, and delete contacts.
 * Contact data is persisted to a simple file (acting as a CSV/JSON file).
 */
public class ContactBook {

    // File name for storing contacts
    private static final String CONTACTS_FILE = "contacts.txt";
    private static final Scanner scanner = new Scanner(System.in);

    // Main application method
    public static void main(String[] args) {
        System.out.println("----------------------------------------");
        System.out.println("      Java CLI Contact Book App      ");
        System.out.println("----------------------------------------");

        boolean isRunning = true;
        while (isRunning) {
            displayMainMenu();
            try {
                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline left-over

                switch (choice) {
                    case 1:
                        addContact();
                        break;
                    case 2:
                        viewAllContacts();
                        break;
                    case 3:
                        searchContact();
                        break;
                    case 4:
                        updateContact();
                        break;
                    case 5:
                        deleteContact();
                        break;
                    case 6:
                        System.out.println("Exiting Contact Book. Goodbye!");
                        isRunning = false;
                        break;
                    default:
                        System.out.println("Invalid choice. Please enter a number between 1 and 6.");
                }
            } catch (java.util.InputMismatchException e) {
                System.out.println("Invalid input. Please enter a number.");
                scanner.nextLine(); // Clear the invalid input
            }
            System.out.println();
        }
        scanner.close();
    }

    /**
     * Displays the main menu options to the user.
     */
    private static void displayMainMenu() {
        System.out.println("1. Add a new contact");
        System.out.println("2. View all contacts");
        System.out.println("3. Search for a contact");
        System.out.println("4. Update an existing contact");
        System.out.println("5. Delete a contact");
        System.out.println("6. Exit");
        System.out.print("Enter your choice: ");
    }

    /**
     * Loads all contacts from the file into a list of strings.
     * @return A list of strings, each representing a contact record.
     */
    private static List<String> loadContacts() {
        List<String> contacts = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(CONTACTS_FILE))) {
            String line;
            while ((line = reader.readLine()) != null) {
                contacts.add(line);
            }
        } catch (FileNotFoundException e) {
            System.out.println("No contacts file found. A new one will be created.");
        } catch (IOException e) {
            System.out.println("An error occurred while reading the contacts file.");
        }
        return contacts;
    }

    /**
     * Saves a list of contacts back to the file, overwriting old data.
     * @param contacts The list of contact records to save.
     */
    private static void saveContacts(List<String> contacts) {
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(CONTACTS_FILE))) {
            for (String contact : contacts) {
                writer.write(contact);
                writer.newLine();
            }
        } catch (IOException e) {
            System.out.println("An error occurred while saving contacts.");
        }
    }

    /**
     * Adds a new contact to the system.
     */
    private static void addContact() {
        System.out.println("\n--- Add New Contact ---");
        System.out.print("Enter name: ");
        String name = scanner.nextLine();
        System.out.print("Enter phone number: ");
        String phone = scanner.nextLine();
        System.out.print("Enter email: ");
        String email = scanner.nextLine();

        // Create a record string in a simple CSV format
        String newContact = name + "," + phone + "," + email;

        List<String> contacts = loadContacts();
        contacts.add(newContact);
        saveContacts(contacts);
        System.out.println("Contact added successfully!");
    }

    /**
     * Displays all contacts in a tabular format.
     */
    private static void viewAllContacts() {
        System.out.println("\n--- All Contacts ---");
        List<String> contacts = loadContacts();
        if (contacts.isEmpty()) {
            System.out.println("No contacts found.");
            return;
        }

        // Print header
        System.out.printf("%-25s%-20s%-30s\n", "Name", "Phone", "Email");
        System.out.println("-----------------------------------------------------------------");

        // Print each contact record
        for (String contact : contacts) {
            String[] parts = contact.split(",");
            if (parts.length == 3) {
                System.out.printf("%-25s%-20s%-30s\n", parts[0], parts[1], parts[2]);
            }
        }
    }

    /**
     * Searches for a contact by name and displays the result.
     */
    private static void searchContact() {
        System.out.println("\n--- Search Contact ---");
        System.out.print("Enter name to search: ");
        String searchTerm = scanner.nextLine().toLowerCase();

        List<String> contacts = loadContacts();
        List<String> results = contacts.stream()
                .filter(c -> c.split(",")[0].toLowerCase().contains(searchTerm))
                .collect(Collectors.toList());

        if (results.isEmpty()) {
            System.out.println("No contacts found matching the search term.");
            return;
        }

        System.out.println("\nSearch Results:");
        System.out.printf("%-25s%-20s%-30s\n", "Name", "Phone", "Email");
        System.out.println("-----------------------------------------------------------------");
        for (String contact : results) {
            String[] parts = contact.split(",");
            if (parts.length == 3) {
                System.out.printf("%-25s%-20s%-30s\n", parts[0], parts[1], parts[2]);
            }
        }
    }

    /**
     * Updates an existing contact.
     */
    private static void updateContact() {
        System.out.println("\n--- Update Contact ---");
        viewAllContacts(); // Show all contacts to help user choose
        System.out.print("Enter the exact name of the contact to update: ");
        String nameToUpdate = scanner.nextLine();

        List<String> contacts = loadContacts();
        int indexToUpdate = -1;
        for (int i = 0; i < contacts.size(); i++) {
            if (contacts.get(i).startsWith(nameToUpdate + ",")) {
                indexToUpdate = i;
                break;
            }
        }

        if (indexToUpdate == -1) {
            System.out.println("Contact not found.");
            return;
        }

        System.out.println("Found contact. Enter new details:");
        System.out.print("Enter new name: ");
        String newName = scanner.nextLine();
        System.out.print("Enter new phone number: ");
        String newPhone = scanner.nextLine();
        System.out.print("Enter new email: ");
        String newEmail = scanner.nextLine();

        String updatedContact = newName + "," + newPhone + "," + newEmail;
        contacts.set(indexToUpdate, updatedContact);
        saveContacts(contacts);
        System.out.println("Contact updated successfully!");
    }

    /**
     * Deletes a contact from the system.
     */
    private static void deleteContact() {
        System.out.println("\n--- Delete Contact ---");
        viewAllContacts();
        System.out.print("Enter the exact name of the contact to delete: ");
        String nameToDelete = scanner.nextLine();

        List<String> contacts = loadContacts();
        boolean removed = contacts.removeIf(c -> c.startsWith(nameToDelete + ","));

        if (removed) {
            saveContacts(contacts);
            System.out.println("Contact deleted successfully!");
        } else {
            System.out.println("Contact not found.");
        }
    }
}
