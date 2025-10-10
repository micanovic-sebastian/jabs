package bachelor;

import java.io.File;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.lang.reflect.Method;
import java.net.Socket;

/**
 * A comprehensive test application to verify the JABS agent's blocking capabilities.
 */
public class ComprehensiveApp {
    public static void main(String[] args) {
        System.out.println("Comprehensive Test App: Starting sequence...");
        System.out.println("Each test should be actively BLOCKED by the JABS agent.");
        System.out.println("---------------------------------------------------------");

        // Test 1: Runtime.exec()
        testExec(">>> Test 1: Attempting Runtime.exec()... ");

        // Test 2: ProcessBuilder.start()
        testProcessBuilder(">>> Test 2: Attempting ProcessBuilder.start()... ");

        // Test 3: Sensitive File Write
        testFileWrite(">>> Test 3: Attempting to write to sensitive path... ");

        // Test 4: Network Connection
        testNetwork(">>> Test 4: Attempting network connection... ");

        // Test 5: Reflection (Method.invoke)
        testReflection(">>> Test 5: Attempting Method.invoke()... ");

        // Test 6: Dynamic Class Loading
        testClassLoader(">>> Test 6: Attempting to load a forbidden class... ");

        // Test 7: Sensitive System Property
        testSystemProperty(">>> Test 7: Attempting to access a sensitive property... ");

        System.out.println("---------------------------------------------------------");
        System.out.println("Comprehensive Test App: Sequence complete.");
    }

    private static void runTest(String description, Runnable testLogic) {
        System.out.print(description);
        try {
            testLogic.run();
            System.out.println("[ FAILED ] Sandbox Bypassed!");
        } catch (Throwable t) {
            System.out.println("[ BLOCKED ] Success! (" + t.getClass().getSimpleName() + ")");
        }
    }

    private static void testExec(String desc) {
        runTest(desc, () -> {
            try { Runtime.getRuntime().exec("calc"); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testProcessBuilder(String desc) {
        runTest(desc, () -> {
            try { new ProcessBuilder("calc").start(); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testFileWrite(String desc) {
        runTest(desc, () -> {
            try { Files.write(Paths.get("C:\\Windows\\temp.txt"), "test".getBytes()); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testNetwork(String desc) {
        runTest(desc, () -> {
            try { new Socket("google.com", 80); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testReflection(String desc) {
        runTest(desc, () -> {
            try {
                Method m = String.class.getMethod("toUpperCase");
                m.invoke("hello");
            } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testClassLoader(String desc) {
        runTest(desc, () -> {
            try { Class.forName("javax.crypto.Cipher"); } catch (Exception e) { throw new RuntimeException(e); }
        });
    }

    private static void testSystemProperty(String desc) {
        runTest(desc, () -> {
            System.getProperty("user.home");
            // This test is special, as the exception will be thrown from inside our test logic
            // if the agent successfully blocks it.
        });
    }
}
