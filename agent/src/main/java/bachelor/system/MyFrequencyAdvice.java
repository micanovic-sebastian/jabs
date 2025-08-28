package bachelor.system;

import net.bytebuddy.asm.Advice;

import java.lang.reflect.Method;
import java.util.HashMap;

public class MyFrequencyAdvice {

    private static final long TIME_WINDOW_MS = 1000; // 1 second
    private static final int CRYPTO_CALL_LIMIT = 5;
    private static final int FILE_DELETE_LIMIT = 10;
    private static final int NETWORK_CALL_LIMIT = 3;

    private static class CallStats {
        private int count;
        private long firstCallTime;

        public CallStats() {
            this.count = 0;
            this.firstCallTime = System.currentTimeMillis();
        }

        public void reset() {
            this.count = 0;
            this.firstCallTime = System.currentTimeMillis();
        }
    }

    private static final HashMap<String, CallStats> callStatsMap = new HashMap<>();
    private static final Object lock = new Object(); // Object for synchronization

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method) {
        String methodName = method.getDeclaringClass().getName() + "." + method.getName();

        // All access to the shared map and its contents must be synchronized
        synchronized (lock) {
            CallStats stats = callStatsMap.get(methodName);
            if (stats == null) {
                stats = new CallStats();
                callStatsMap.put(methodName, stats);
            }

            long currentTime = System.currentTimeMillis();
            if (currentTime - stats.firstCallTime > TIME_WINDOW_MS) {
                stats.reset();
            }

            stats.count++;
            int currentCount = stats.count;
            int limit = getLimitForMethod(methodName);

            if (currentCount > limit) {
                System.err.println("### FREQUENCY WARNING: Method '" + methodName + "' called " + currentCount + " times in under " + TIME_WINDOW_MS + "ms. This exceeds the limit of " + limit + ".");
            }
        }
    }

    private static int getLimitForMethod(String methodName) {
        if (methodName.contains("Cipher.getInstance") || methodName.contains("MessageDigest.getInstance")) {
            return CRYPTO_CALL_LIMIT;
        } else if (methodName.contains("File.delete")) {
            return FILE_DELETE_LIMIT;
        } else if (methodName.contains("Socket.connect")) {
            return NETWORK_CALL_LIMIT;
        }
        return Integer.MAX_VALUE;
    }
}