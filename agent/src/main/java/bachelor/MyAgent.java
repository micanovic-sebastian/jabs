package bachelor;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers;

import java.io.IOException;
import java.lang.instrument.Instrumentation;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;

import static net.bytebuddy.matcher.ElementMatchers.named;

public class MyAgent {

    private static final String FILE_ADVICE_CLASS = "bachelor.MyFileAdvice";
    private static final String FILES_WRITE_ADVICE_CLASS = "bachelor.MyFilesWriteAdvice"; // New advice class
    private static final String NETWORK_ADVICE_CLASS = "bachelor.MyNetworkAdvice"; // New advice class
    private static String agentJarPath;


    public static void premain(String agentArguments, Instrumentation instrumentation) throws IOException {

                // Get the path to the agent JAR.
        RuntimeMXBean runtimeMxBean = ManagementFactory.getRuntimeMXBean();
        for (String arg : runtimeMxBean.getInputArguments()) {
            if (arg.startsWith("-javaagent:")) {
                agentJarPath = arg.substring(arg.indexOf("=") + 1);
                break;
            }
        }

        // Configure AgentBuilder to transform java.io.File
        new AgentBuilder.Default()
            // Crucial: Do not ignore types loaded by the bootstrap class loader [5, 6, 7]
            .ignore(ElementMatchers.none())
            // Enable retransformation for already loaded classes (like core JDK classes) [8, 7, 9, 10]
            .with(AgentBuilder.RedefinitionStrategy.RETRANSFORMATION)
            // Use REDEFINE strategy to modify existing classes [7]
            .with(AgentBuilder.TypeStrategy.Default.REDEFINE)
            // Add a listener for verbose debugging output [11, 5, 12, 13, 14, 15, 16]
//               .with(AgentBuilder.Listener.StreamWriting.toSystemOut())
            // Target the specific class: java.io.File
            .type(named("java.io.File"))
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


            // Install the agent on the provided Instrumentation instance
            .installOn(instrumentation);

        // Request retransformation for java.io.File if it's already loaded
        // This is necessary for core JDK classes that might be loaded before the agent is fully active.
        try {
            Class<?> fileClass = Class.forName("java.io.File");
            Class<?> nioFilesClass = Class.forName("java.nio.file.Files");
            if (instrumentation.isModifiableClass(fileClass)) { // Check if the class can be modified
                instrumentation.retransformClasses(fileClass); // Request retransformation
                instrumentation.retransformClasses(nioFilesClass); // Request retransformation
                System.out.println("### Agent: Successfully requested retransformation for java.io.File. ###");
            } else {
                System.out.println("### Agent: java.io.File is not modifiable. This might be due to JVM restrictions. ###");
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
}