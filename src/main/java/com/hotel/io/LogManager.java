package com.hotel.io;
import java.io.*;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
public class LogManager {
    private final String logFilePath;
    private static final DateTimeFormatter FMT =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public LogManager(String logFilePath) {
        this.logFilePath = logFilePath;
        ensureDirectoryExists(logFilePath);
    }
    public synchronized void log(String level, String message) {
        String timestamp = LocalDateTime.now().format(FMT);
        String entry = String.format("[%s] [%-5s] %s", timestamp, level, message);
        try (PrintWriter pw = new PrintWriter(new FileWriter(logFilePath, true))) {
            pw.println(entry); 
        } catch (IOException e) {
            System.err.println("LogManager error: " + e.getMessage());
        }
    }
    public void info(String message)  { log("INFO",  message); }
    public void warn(String message)  { log("WARN",  message); }
    public void error(String message) { log("ERROR", message); }
    private void ensureDirectoryExists(String path) {
        File parent = new File(path).getParentFile();
        if (parent != null && !parent.exists()) parent.mkdirs();
    }
}