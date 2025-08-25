package bachelor;

import javax.crypto.Cipher;
import javax.crypto.NoSuchPaddingException;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigInteger;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;
import java.util.Base64;

public class App {
    public static void main(String[] args) throws IOException, NoSuchMethodException,
                                                  InvocationTargetException, IllegalAccessException {

//        Class<?> clazz = Class.forName("idk");
//        clazz.accessFlags();
//        RandomAccessFile raf = new RandomAccessFile(new File("/bin"), "a");
//        raf.write(3);
//        Socket socket = new Socket();
//        socket.connect(new InetSocketAddress("127.0.0.1", 5555));
//        Runtime.getRuntime().exec("aa");
//        Files.delete(Paths.get("aaa"));
//        Files.write(Paths.get("Program Files"), new byte[50]);
//        Files.write(Paths.get("/media"), new byte[50]);

        try {
            Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
            Base64.Decoder base64 = Base64.getDecoder();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        } catch (NoSuchPaddingException e) {
            throw new RuntimeException(e);
        }
        BigInteger bigInteger1 = BigInteger.ONE;
        for (int i = 0; i < 1; i++) {
            BigInteger bigInteger = BigInteger.ONE;
        }
        Method method = Integer.class.getMethod("toString");
        method.invoke(3);
        method.toString();


        System.out.println("Application started.");
        String originalInput = "test_intercept.txt";
        String encodedString = Base64.getEncoder().encodeToString(originalInput.getBytes());
        String decodedString = Arrays.toString(Base64.getDecoder().decode(encodedString));

        File testFile = new File(decodedString);
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