package bachelor;

import net.bytebuddy.asm.Advice;
import java.lang.reflect.Method;
import java.io.OutputStream;

public class MyOutputStreamAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.This OutputStream os, @Advice.Argument(0) byte[] buffer) {
        System.out.println("### ByteBuddy Intercept: ENTERING " + method.toString());
        System.out.println("OUTPUT STREAM WRITE ATTEMPT: Writing " + buffer.length + " bytes to output stream by method " + method.toString());
    }
}