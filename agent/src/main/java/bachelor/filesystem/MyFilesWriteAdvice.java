package bachelor.filesystem;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

public class MyFilesWriteAdvice {

    private static final List<String> LINUX_SENSITIVE_PATHS = Arrays.asList(
        "/etc/",
        "/root/",
        "/bin/",
        "/sbin/",
        "/usr/bin/",
        "/var/log/",
        "/home/",
        "/proc/",
        "/sys/",
        "/dev/"
    );

    // Windows sensitive paths
    private static final List<String> WINDOWS_SENSITIVE_PATHS = Arrays.asList(
        "C:\\Windows\\",
        "C:\\Program Files\\",
        "C:\\Program Files (x86)\\",
        "C:\\Users\\",
        "C:\\Documents and Settings\\",
        "C:\\System32\\",
        "C:\\$Recycle.Bin\\"
    );

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) java.nio.file.Path path, @Advice.Argument(1) byte[] bytes) {

        System.out.println("### ByteBuddy Intercept: ENTERING " + method.toString());
        String absolutePath = path.toAbsolutePath().toString();
        System.out.println(absolutePath);

        if (true) {
            System.err.println("### ByteBuddy Intercept: Writing to protected file " + path.toString() + " DENIED! ###");
//            throw new SecurityException("Deletion denied by agent!");
        } else {
            System.out.println("### ByteBuddy Intercept: Writing to file at path: " +
                               path.toAbsolutePath() + " byte array of length " + bytes.length);
        }

    }

    private static boolean containsSensitivePath(String pathToCheck) {
        for (String path : LINUX_SENSITIVE_PATHS) {
            if (pathToCheck.contains(path)) return true;
        }
        for (String path : WINDOWS_SENSITIVE_PATHS) {
            if (pathToCheck.contains(path)) return true;
        }
        return false;
    }
}
