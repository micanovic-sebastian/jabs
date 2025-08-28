package bachelor;

import net.bytebuddy.asm.Advice;
import java.lang.reflect.Method;
import java.lang.Class;

public class MyReflectionAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.This Class<?> clazz, @Advice.Argument(0) String name) {
        // Intercepting methods like getMethod, getField, getConstructor etc.
        System.out.println("### ByteBuddy Intercept: ENTERING " + method.toString());
        System.out.println("REFLECTION ATTEMPT: Calling " + method.getName() + " on class " + clazz.getName() + " for member named '" + name + "'");
    }
}