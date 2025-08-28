package bachelor;

import net.bytebuddy.asm.Advice;
import java.lang.reflect.Method;

public class MyClassLoaderAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) String className) {
        System.out.println("### Agent Log: CLASS LOADING ATTEMPT: Dynamically loading class '" + className + "' by method " + method.toString());
    }

}
