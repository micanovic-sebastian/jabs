package bachelor;

import net.bytebuddy.asm.Advice;
import java.lang.reflect.Method;
import java.io.InputStream;

public class MyInputStreamAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.This InputStream is) {
        System.out.println("### ByteBuddy Intercept: ENTERING " + method.toString());
        System.out.println("INPUT STREAM READ ATTEMPT: Reading from input stream by method " + method.toString());
    }
}