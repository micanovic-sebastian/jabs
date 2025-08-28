package bachelor.filesystem;

import net.bytebuddy.asm.Advice;

import java.io.File;
import java.lang.reflect.Method;

public class MyFileDeleteAdvice {

    @Advice.OnMethodEnter
    public static void onEnter(@Advice.Origin Method method, @Advice.This File file) {
        System.out.println("### Agent Log: FILE DELETION ATTEMPT: Deleting file '" + file.getAbsolutePath() + "' by method " + method.toString());
    }

}
