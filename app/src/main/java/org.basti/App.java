package org.basti;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

public class App {
    public static void main(String[] args) throws IOException {
        System.out.println("Application started.");

        File testFile = new File("test_intercept.txt");
        File testFile1 = new File("test_intercept1.txt");
        testFile1.createNewFile();
        testFile1.delete();
        File testFile2 = new File("test_intercept2.txt");
        testFile2.delete();


        try {
            // Create the file first so delete() has something to delete
            boolean created = testFile.createNewFile();
            System.out.println("Test file created: " + created);

            if (created) {
                System.out.println("Attempting to delete test_intercept.txt...");
                // This is the call we expect to be intercepted
                boolean deleted = testFile.delete();
                System.out.println("Test file deleted: " + deleted);
            } else {
                System.out.println("Test file already exists or could not be created.");
                // If file exists, try to delete it anyway to test interception
                System.out.println("Attempting to delete existing test_intercept.txt...");
                boolean deleted = testFile.delete();
                System.out.println("Existing test file deleted: " + deleted);
            }

        } catch (IOException e) {
            e.printStackTrace();
        }

        System.out.println("Application finished.");
    }
}