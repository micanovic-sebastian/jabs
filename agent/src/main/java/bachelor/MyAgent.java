package bachelor;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.util.Arrays;
import java.util.List;

import static net.bytebuddy.agent.builder.AgentBuilder.RedefinitionStrategy.RETRANSFORMATION;
import static net.bytebuddy.matcher.ElementMatchers.*;

public class MyAgent {

    private static final String FILE_ADVICE_CLASS = "bachelor.filesystem.MyFileAdvice";
    private static final String FILES_WRITE_ADVICE_CLASS = "bachelor.filesystem.MyFilesWriteAdvice";
    private static final String NETWORK_ADVICE_CLASS = "bachelor.system.MyNetworkAdvice";
    private static final String OUTPUT_STREAM_ADVICE_CLASS = "bachelor.filesystem.MyOutputStreamAdvice";
    private static final String WRITER_ADVICE_CLASS = "bachelor.filesystem.MyWriterAdvice";
    private static final String INPUT_STREAM_ADVICE_CLASS = "bachelor.filesystem.MyInputStreamAdvice";
    private static final String REFLECTION_ADVICE_CLASS = "bachelor.reflection.MyReflectionAdvice";
    private static final String METHOD_INVOKE_ADVICE_CLASS = "bachelor.reflection.MyMethodInvokeAdvice";
    private static final String CIPHER_ADVICE_CLASS = "bachelor.system.MyCipherAdvice";
    private static final String DIGEST_ADVICE_CLASS = "bachelor.system.MyDigestAdvice"; // Ne
    private static final String FREQUENCY_ADVICE_CLASS = "bachelor.system.MyFrequencyAdvice"; // New advice clas
    private static String agentJarPath;
    private static List<String> classesToRetransform = Arrays.asList("java.io.File", "java.nio.file.Files", "java.util.Arrays", "java.util.List",
                                                                     "java.lang.reflect.Method", "java.io.OutputStream", "sun.nio.ch.ChannelOutputStream",
                                                                     "java.io.Writer", "java.net.InetSocketAddress", "bachelor.Logger",
                                                                     "bachelor.MyFrequencyAdvice", "javax.crypto.Cipher", "java.security.MessageDigest",
                                                                     "java.net.Socket");



    public static void premain(String agentArguments, Instrumentation instrumentation) throws IOException {

        // --- FIX: Correctly parse the agent's JAR path from JVM arguments ---
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        for (String arg : runtimeMxBean.getInputArguments()) {
            if (arg.startsWith("-javaagent:")) {
                // Correctly extract the path by removing the "-javaagent:" prefix
                String fullArgument = arg.substring(11); // "javaagent:" is 10 characters, so index 11 is the path start
                // On Windows, the path might contain a colon (e.g., C:\...)
                // We need to handle this to avoid the InvalidPathException.
                // A robust way to handle it is to simply use the substring without any additional logic.
                agentJarPath = fullArgument;
                break;
            }
        }
//        instrumentation.appendToBootstrapClassLoaderSearch(new JarFile(agentJarPath));

        // Configure AgentBuilder to transform java.io.File
        new AgentBuilder.Default()
            // Crucial: Do not ignore types loaded by the bootstrap class loader [5, 6, 7]
            .ignore(ElementMatchers.nameStartsWith("java.util.concurrent"))
            // Enable retransformation for already loaded classes (like core JDK classes) [8, 7, 9, 10]
            .with(RETRANSFORMATION)
            // Use REDEFINE strategy to modify existing classes [7]
            .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
                                // --- APPLY MyFrequencyAdvice ---
            // Add a listener for verbose debugging output [11, 5, 12, 13, 14, 15, 16]
//               .with(AgentBuilder.Listener.StreamWriting.toSystemOut())
            // Target the specific class: java.io.File
//            .type(named("java.io.File"))
            .type(isSubTypeOf(java.io.File.class).and(ElementMatchers.not(isAbstract())))
            // Apply the advice to the 'delete' method
            .transform(new AgentBuilder.Transformer.ForAdvice()
                        // Ensure the agent's own classes (like FileDeleteAdvice) are visible to the transformer
                        // This helps ByteBuddy inject the advice correctly, even into bootstrap classes.
                       .include(MyAgent.class.getClassLoader())
                        // Apply the advice to the 'delete' method that takes no arguments and returns a boolean
                       .advice(named("delete").and(ElementMatchers.takesArguments(0))
                                              .and(ElementMatchers.returns(boolean.class)), FILE_ADVICE_CLASS)
               )
            .type(named("java.nio.file.Files"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                               .include(MyAgent.class.getClassLoader())
                        .advice(named("write")
                                .and(ElementMatchers.takesArgument(0, java.nio.file.Path.class))
                                .and(ElementMatchers.takesArgument(1, byte[].class)),
                                FILES_WRITE_ADVICE_CLASS))
            .type(named("java.net.Socket"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("connect")
                                    .and(ElementMatchers.takesArgument(0, java.net.SocketAddress.class)),
                                    NETWORK_ADVICE_CLASS))
            .type(isSubTypeOf(java.io.OutputStream.class).and(ElementMatchers.not(isAbstract())))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("write")
                                    .and(ElementMatchers.takesArgument(0, byte[].class)),
                                    OUTPUT_STREAM_ADVICE_CLASS))
                    // New transformation for Writer
            .type(isSubTypeOf(java.io.Writer.class).and(ElementMatchers.not(isAbstract())))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("write")
                                    .and(ElementMatchers.takesArgument(0, char[].class)),
                                    WRITER_ADVICE_CLASS))
                    // New transformation for InputStream
            .type(isSubTypeOf(java.io.InputStream.class).and(ElementMatchers.not(isAbstract())))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("read")
                                    .and(ElementMatchers.takesArgument(0, byte[].class)),
                                    INPUT_STREAM_ADVICE_CLASS))
            .type(named("java.lang.Class"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("getMethod")
                                    .or(named("getField"))
                                    .or(named("getConstructor")),
                                    REFLECTION_ADVICE_CLASS))
                    // New transformation for Method.invoke
            .type(named("java.lang.reflect.Method"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("invoke"), METHOD_INVOKE_ADVICE_CLASS))
            .type(named("javax.crypto.Cipher"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("getInstance"), CIPHER_ADVICE_CLASS))
            .type(named("java.security.MessageDigest"))
            .transform(new AgentBuilder.Transformer.ForAdvice()
                            .include(MyAgent.class.getClassLoader())
                            .advice(named("getInstance"), DIGEST_ADVICE_CLASS))

            .installOn(instrumentation);




        // Request retransformation for java.io.File if it's already loaded
        // This is necessary for core JDK classes that might be loaded before the agent is fully active.
        try {

            for (String clazzString : classesToRetransform) {
                Class<?> clazz = Class.forName(clazzString);
                instrumentation.retransformClasses(clazz); // Request retransformation
            }
        } catch (ClassNotFoundException e) {
            System.err.println("### Agent: Could not find java.io.File class. This is unexpected. ###");
            e.printStackTrace();
        }
        catch (RuntimeException e) {}
        catch (Exception e) {
            System.err.println("### Agent: Error during retransformation request for java.io.File: " + e.getMessage() + " ###");
            e.printStackTrace();
        }
    }

    public static String getAgentJarPath() {
        return agentJarPath;
    }

    public static void setAgentJarPath(String agentJarPath) {
        MyAgent.agentJarPath = agentJarPath;
    }
}