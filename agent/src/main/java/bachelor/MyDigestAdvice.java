package bachelor;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class MyDigestAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) String algorithm) {
        System.out.println("### ByteBuddy Intercept: ENTERING " + method.toString());
        System.out.println("### Agent Log: HASHING ATTEMPT: Initializing MessageDigest with algorithm '" + algorithm + "' by method " + method.toString());
    }
}