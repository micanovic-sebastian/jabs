package bachelor;

import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Logger {

    private static final String LOG_FILE = System.getProperty("user.home") + "/agent.log";
    private static final DateTimeFormatter FORMATTER = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void log(String message) {
        try (FileWriter fileWriter = new FileWriter(LOG_FILE, true);
             PrintWriter printWriter = new PrintWriter(fileWriter)) {
            printWriter.println("[" + LocalDateTime.now().format(FORMATTER) + "] " + message);
        } catch (IOException e) {
            System.err.println("### Agent Log: Could not write to log file. " + e.getMessage());
        }
    }
}