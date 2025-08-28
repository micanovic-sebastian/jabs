package bachelor.system;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;

public class MyExecAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) String command) {
        System.out.println("### Agent Log: COMMAND EXECUTION ATTEMPT: Executing command '" + command + "' by method " + method.toString());
    }

}
