package org.basti;

import java.lang.instrument.Instrumentation;

public class MyAgent {
    public static void premain(String agentArguments, Instrumentation instrumentation) {
        instrumentation.addTransformer(new MyTransformer(), true);
    }
}