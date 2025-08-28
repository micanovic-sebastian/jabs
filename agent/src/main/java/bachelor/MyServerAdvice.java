package bachelor;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

public class MyServerAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) InetSocketAddress address) {
        System.out.println("### Agent Log: SERVER ATTEMPT: Binding server to port " + address.getPort() + " by method " + method.toString());
    }

}
