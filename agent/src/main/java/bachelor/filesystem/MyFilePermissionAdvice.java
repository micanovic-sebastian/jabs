package bachelor.filesystem;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;
import java.nio.file.Path;

public class MyFilePermissionAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) Path path) {
        System.out.println("### Agent Log: FILE PERMISSION CHANGE ATTEMPT: Changing permissions for '" + path.toAbsolutePath() + "' by method " + method.toString());
    }

}
