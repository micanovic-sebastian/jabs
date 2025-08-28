package bachelor.system;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class MyCipherAdvice {

    private int numOfCalls;

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) String transformation) {
        System.out.println("### ByteBuddy Intercept: ENTERING " + method.toString());

        System.out.println("### Agent Log: CRYPTOGRAPHIC OPERATION ATTEMPT: Initializing Cipher with transformation '" + transformation + "' by method " + method.toString());
    }
}