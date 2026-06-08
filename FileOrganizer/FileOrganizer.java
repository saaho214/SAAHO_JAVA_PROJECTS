import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Scanner;

/**
 * A command-line utility to organize files in a specified folder.
 * It sorts files into subdirectories based on their file extension.
 */
public class FileOrganizer {

    private static final Scanner scanner = new Scanner(System.in);

    public static void main(String[] args) {
        System.out.println("----------------------------------------");
        System.out.println("      Java File Organizer Utility      ");
        System.out.println("----------------------------------------");
        System.out.print("Enter the full path of the folder to organize: ");
        String folderPath = scanner.nextLine();

        File targetFolder = new File(folderPath);

        // Validate the folder path
        if (!targetFolder.exists() || !targetFolder.isDirectory()) {
            System.out.println("Error: The provided path is not a valid directory or does not exist.");
            scanner.close();
            return;
        }

        System.out.println("Organizing files in: " + targetFolder.getAbsolutePath());

        // Get all files and subdirectories in the target folder
        File[] allFilesAndDirs = targetFolder.listFiles();

        if (allFilesAndDirs == null || allFilesAndDirs.length == 0) {
            System.out.println("The folder is empty. Nothing to organize.");
            scanner.close();
            return;
        }

        // Iterate through each item in the folder
        for (File file : allFilesAndDirs) {
            // We only want to organize actual files, not sub-directories
            if (file.isFile()) {
                // Get the file's name
                String fileName = file.getName();

                // Find the index of the last dot to get the file extension
                int dotIndex = fileName.lastIndexOf('.');

                // Skip files without an extension
                if (dotIndex > 0) {
                    // Extract the extension (e.g., "txt", "pdf")
                    String extension = fileName.substring(dotIndex + 1).toLowerCase();

                    // Create the path for the new subdirectory
                    Path destinationDirPath = Paths.get(targetFolder.getAbsolutePath(), extension);

                    try {
                        // Create the new subdirectory if it doesn't already exist
                        Files.createDirectories(destinationDirPath);

                        // Create the full path for the destination file
                        Path destinationFilePath = Paths.get(destinationDirPath.toString(), fileName);

                        // Move the file to the new subdirectory
                        Files.move(file.toPath(), destinationFilePath);
                        System.out.println("Moved: " + fileName + " -> /" + extension);

                    } catch (IOException e) {
                        System.out.println("Error moving file " + fileName + ": " + e.getMessage());
                    }
                }
            }
        }

        System.out.println("\nFile organization complete!");
        scanner.close();
    }
}
