package com.focuswall.util;

import java.io.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class BreakLogger {

    // Dynamic Filename: log_2025-11-25.txt
    private static String getLogFileName() {
        String date = LocalDate.now().toString();
        return "log_" + date + ".txt";
    }

    // Standard event logger
    public static void log(String event) {
        writeLine(event);
    }

    // Record daily total duration
    public static void logTimeChunk(double minutes, String type) {
        double currentTotal = calculateDailyTotal();
        double newTotal = currentTotal + minutes;

        // Format to 1 decimal place
        String msg = String.format("%s: +%.1f min. (Daily Total: %.1f min)", type, minutes, newTotal);
        writeLine(msg);
    }

    private static void writeLine(String event) {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("HH:mm:ss"));
        String entry = "[" + timestamp + "] " + event;

        try (FileWriter fw = new FileWriter(getLogFileName(), true);
             PrintWriter out = new PrintWriter(fw)) {
            out.println(entry);
            System.out.println("LOGGED: " + entry);
        } catch (IOException e) {
            System.err.println("Error writing to log: " + e.getMessage());
        }
    }

    // Reads the file to find the last recorded "Daily Total"
    private static double calculateDailyTotal() {
        File file = new File(getLogFileName());
        if (!file.exists()) return 0.0;

        double total = 0.0;
        // Regex looks for "Daily Total: 123.4"
        Pattern p = Pattern.compile("Daily Total: (\\d+\\.?\\d*)");

        try (BufferedReader br = new BufferedReader(new FileReader(file))) {
            String line;
            while ((line = br.readLine()) != null) {
                Matcher m = p.matcher(line);
                if (m.find()) {
                    try {
                        total = Double.parseDouble(m.group(1));
                    } catch (NumberFormatException ignored) {}
                }
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return total;
    }
}