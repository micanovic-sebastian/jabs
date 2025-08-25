package bachelor;
import net.bytebuddy.asm.Advice;
import java.io.File; // Import java.io.File to access its methods via @Advice.This
import java.lang.reflect.Method;



public class MyFileAdvice {

    /**
     * This method is executed before the target method (File.delete()).
     * @param file The instance of the File class on which delete() is called.
     */

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.This File file) {
        System.out.println("### ByteBuddy Intercept: ENTERING " + method.toString());
        // Add your custom pre-execution logic here
        // Example: Prevent deletion of a specific file
        // if (file.getName().equals("protected.txt")) {
        //     System.err.println("### ByteBuddy Intercept: Deletion of protected.txt DENIED! ###");
        //     throw new SecurityException("Deletion denied by agent!");
        // }
    }

    /**
     * This method is executed after the target method (File.delete()).
     * It can capture the return value or any thrown exception.
     * @param file The instance of the File class on which delete() was called.
     * @param result The boolean result of the original File.delete() method.
     * @param thrown Any Throwable that was thrown by the original method.
     */
    @Advice.OnMethodExit(onThrowable = Throwable.class) // Capture exceptions if thrown [1]
    public static void onExit(@Advice.Origin Method method, @Advice.This File file,
                              @Advice.Return boolean result, @Advice.Thrown Throwable thrown) {
        if (thrown != null) {
            System.err.println("### ByteBuddy Intercept: TEST " + method.toString() + " FAILURE ###");
        } else {
            System.out.println("### ByteBuddy Intercept: EXITING " + method.toString() + " SUCCESS ###");
        }
        // Add your custom post-execution logic here
    }
}