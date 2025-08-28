package bachelor;

import net.bytebuddy.asm.Advice;
import java.lang.reflect.Method;
import java.io.Writer;

public class MyWriterAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.This Writer writer, @Advice.Argument(0) char[] buffer) {
        System.out.println("### ByteBuddy Intercept: ENTERING " + method.toString());
        System.out.println("WRITER WRITE ATTEMPT: Writing " + buffer.length + " characters to writer by method " + method.toString());
    }
}