package bachelor;

import net.bytebuddy.asm.Advice;
import java.lang.management.ManagementFactory;
import java.lang.management.MemoryMXBean;
import java.lang.reflect.Method;

public class MyPerformanceAdvice {

    private static final long CPU_TIME_LIMIT_NS = 100_000_000; // 100ms in nanoseconds
    private static final long MEMORY_LIMIT_MB = 100; // 100MB
    private static final long BYTES_PER_MB = 1024 * 1024;

    private static final ThreadLocal<Long> startTime = new ThreadLocal<>();
    private static final ThreadLocal<Long> initialMemory = new ThreadLocal<>();

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method) {
        startTime.set(System.nanoTime());
        // Approximate memory usage by checking heap memory
        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        initialMemory.set(memoryBean.getHeapMemoryUsage().getUsed());

        System.out.println("### Agent Log: Entering " + method.getName() + " for performance analysis.");
    }

    @Advice.OnMethodExit(onThrowable = Throwable.class)
    public static void onExit(@Advice.Origin Method method, @Advice.Thrown Throwable thrown) {
        System.out.println("### ByteBuddy Intercept: ENTERING " + method.toString());
        long duration = System.nanoTime() - startTime.get();
        startTime.remove(); // Clean up thread-local

        MemoryMXBean memoryBean = ManagementFactory.getMemoryMXBean();
        long finalMemory = memoryBean.getHeapMemoryUsage().getUsed();
        long memoryUsed = finalMemory - initialMemory.get();
        initialMemory.remove(); // Clean up thread-local

        if (thrown == null) {
            System.out.println("### Agent Log: Method " + method.getName() + " completed in " + duration + " ns. Memory used: " + (memoryUsed / BYTES_PER_MB) + " MB");
        } else {
            System.out.println("### Agent Log: Method " + method.getName() + " failed after " + duration + " ns. Memory used: " + (memoryUsed / BYTES_PER_MB) + " MB");
        }

        // Check for limits and log warnings
        if (duration > CPU_TIME_LIMIT_NS) {
            System.err.println("### WARNING: Method " + method.getName() + " exceeded CPU time limit! Duration: " + (duration / 1_000_000) + " ms");
        }

        if (memoryUsed > MEMORY_LIMIT_MB * BYTES_PER_MB) {
            System.err.println("### WARNING: Method " + method.getName() + " exceeded memory limit! Used: " + (memoryUsed / BYTES_PER_MB) + " MB");
        }
    }
}