package org.basti;

import net.bytebuddy.implementation.bind.annotation.SuperCall;

import java.util.concurrent.Callable;

public class Interceptor {
    public static boolean intercept(@SuperCall Callable<Boolean> callable) throws Exception {
        System.out.println("### delete() Intercepted! ###");
        return callable.call(); // Call the original method's implementation
    }
}
