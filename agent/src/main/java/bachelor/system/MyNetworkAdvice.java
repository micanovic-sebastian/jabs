package bachelor.system;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;
import java.net.InetSocketAddress;

public class MyNetworkAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.Argument(0) InetSocketAddress address) {
        // Use your custom Logger to log the network connection details
        System.out.println("### ByteBuddy Intercept: ENTERING " + method.toString());
        System.out.println("NETWORK CONNECTION ATTEMPT: " + address.getAddress().getHostAddress() + ":" + address.getPort() + " by method " + method.toString());
    }
}