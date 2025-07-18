package org.basti;


import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.IllegalClassFormatException;
import java.security.ProtectionDomain;

public class MyTransformer implements ClassFileTransformer {

    public byte[] transform(ClassLoader loader, String fullClassName, Class<?> classBeingRedefined,
                            ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        String className = fullClassName.replaceAll(".*/", "");
        String pkg = fullClassName.replaceAll("/[a-zA-Z$0-9_]*$", "");
        System.out.printf("Class: %s in: %s\n", className, pkg);
        return null;
    }
}