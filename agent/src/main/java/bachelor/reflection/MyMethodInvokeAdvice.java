package bachelor.reflection;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class MyMethodInvokeAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.This Method targetMethod, @Advice.Argument(0) Object instance) {
        System.out.println("### ByteBuddy Intercept: ENTERING " + method.toString());
        System.out.println("METHOD INVOCATION ATTEMPT: Invoking method '" + targetMethod.getName() + "' on instance of " + (instance != null ? instance.getClass().getName() : "null"));
    }
}