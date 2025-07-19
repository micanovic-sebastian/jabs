package org.basti;

import net.bytebuddy.agent.builder.AgentBuilder;
import net.bytebuddy.matcher.ElementMatchers; // For matching classes and methods [3, 4]
import java.lang.instrument.Instrumentation;

public class MyAgent {

    public static void premain(String agentArguments, Instrumentation instrumentation) {
        System.out.println("### Java Agent 'MyAgent' LOADED! ### Arg: " + agentArguments);

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
               .type(ElementMatchers.named("java.io.File"))
                // Apply the advice to the 'delete' method
               .transform(new AgentBuilder.Transformer.ForAdvice()
                        // Ensure the agent's own classes (like FileDeleteAdvice) are visible to the transformer
                        // This helps ByteBuddy inject the advice correctly, even into bootstrap classes.
                       .include(MyAgent.class.getClassLoader())
                        // Apply the advice to the 'delete' method that takes no arguments and returns a boolean
                       .advice(ElementMatchers.nameContains("e").and(ElementMatchers.takesArguments(0)).and(ElementMatchers.returns(boolean.class)),
                                "org.basti.MyAdvice")) // Fully qualified name of your advice class
                // Install the agent on the provided Instrumentation instance
               .installOn(instrumentation);

        // Request retransformation for java.io.File if it's already loaded
        // This is necessary for core JDK classes that might be loaded before the agent is fully active.
        try {
            Class<?> fileClass = Class.forName("java.io.File");
            if (instrumentation.isModifiableClass(fileClass)) { // Check if the class can be modified
                instrumentation.retransformClasses(fileClass); // Request retransformation
                System.out.println("### Agent: Successfully requested retransformation for java.io.File. ###");
            } else {
                System.out.println("### Agent: java.io.File is not modifiable. This might be due to JVM restrictions. ###");
            }
        } catch (ClassNotFoundException e) {
            System.err.println("### Agent: Could not find java.io.File class. This is unexpected. ###");
            e.printStackTrace();
        } catch (Exception e) {
            System.err.println("### Agent: Error during retransformation request for java.io.File: " + e.getMessage() + " ###");
            e.printStackTrace();
        }
    }
}