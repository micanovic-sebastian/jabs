package bachelor;

import java.io.*;
import java.lang.reflect.Method;
import java.nio.file.Files;
import java.nio.file.Paths;

public class App {
    public static void main(String[] args) throws Exception {

        System.out.println("Application started.");

        // --- Test File I/O Interception (MyFilesWriteAdvice, MyOutputStreamAdvice, MyInputStreamAdvice, MyWriterAdvice) ---
        String testFilePath = "c:/users/basti/documents/test-file-io.txt";
        String protectedPath = "c:\\windows\\system32\\test.txt";
        String nonExistentPath = "nonexistent/file.txt";

        // Test FileOutputStream (OutputStream.write)
        try (FileOutputStream fos = new FileOutputStream(testFilePath)) {
            fos.write("Testing FileOutputStream".getBytes());
        } catch (IOException e) {
            System.err.println("File write failed: " + e.getMessage());
        }

        // Test FileInputStream (InputStream.read)
        try (FileInputStream fis = new FileInputStream(testFilePath)) {
            byte[] data = new byte[100];
            fis.read(data);
        } catch (IOException e) {
            System.err.println("File read failed: " + e.getMessage());
        }

        // Test BufferedWriter (Writer.write)
        try (FileWriter fw = new FileWriter(testFilePath);
             BufferedWriter bw = new BufferedWriter(fw)) {
            bw.write("Testing BufferedWriter.");
        } catch (IOException e) {
            System.err.println("Writer failed: " + e.getMessage());
        }

        // Test Files.write to a protected path (should be logged but the method will throw an exception)
        try {
            Files.write(Paths.get(protectedPath), new byte[] {1, 2, 3});
        } catch (IOException e) {
            System.err.println("Attempted write to protected path failed as expected: " + e.getClass().getName());
        }

        // Test File.delete on an existing and non-existent file
        File existingFile = new File("c:/users/basti/documents/test_intercept.txt");
        Files.write(Paths.get(existingFile.getName()), "content".getBytes());
        existingFile.delete(); // This call should be intercepted

        File nonExistentFile = new File(nonExistentPath);
        nonExistentFile.delete(); // This is an edge case: delete on a file that doesn't exist

        // --- Test Network Interception (MyNetworkAdvice) ---
        try (java.net.Socket socket = new java.net.Socket()) {
            // Using a public DNS server for testing
            socket.connect(new java.net.InetSocketAddress("8.8.8.8", 53), 5000);
            System.out.println("Network connection to 8.8.8.8:53 successful.");
        } catch (IOException e) {
            System.err.println("Network connection test failed: " + e.getMessage());
        }

        // --- Test Reflection Interception (MyReflectionAdvice, MyMethodInvokeAdvice) ---
        Class<?> clazz = String.class;

        // Test getting a method by name (Class.getMethod)
        Method stringMethod = clazz.getMethod("toLowerCase");

        // Test invoking the method (Method.invoke)
        String myString = "Hello, World!";
        Object result = stringMethod.invoke(myString);
        System.out.println("Reflection invocation result: " + result);

        // Edge case: get a method that doesn't exist
        try {
            clazz.getMethod("nonExistentMethod");
        } catch (NoSuchMethodException e) {
            System.err.println("Reflection: Correctly failed to find nonExistentMethod.");
        }

        System.out.println("Application finished.");
    }
}