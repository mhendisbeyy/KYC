package com.tr.kyc.service;




import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.tr.kyc.model.LogEvent;

public class LogLineParser {

    private static final DateTimeFormatter TS =
            DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss.SSS");

    public LogEvent parse(String line) {
        LogEvent e = new LogEvent();
        e.setRawLine(line);

        // Çok basit parse (format farklıysa uyarlarsın)
        // [timestamp] [level] [logger] - [message]
        try {
            String ts = line.substring(0, 23);
            e.setEventTime(LocalDateTime.parse(ts, TS));

            String rest = line.substring(24).trim();
            String level = rest.split("\\s+")[0];
            e.setLevel(level);

            // logger ve message
            // "INFO  com.app.Service - Payment..."
            int dash = line.indexOf(" - ");
            if (dash > 0) {
                String before = line.substring(24, dash).trim();
                // before: "INFO  com.app.Service"
                String[] parts = before.split("\\s+");
                if (parts.length >= 2) e.setLogger(parts[1]);
                e.setMessage(line.substring(dash + 3).trim());
            } else {
                e.setMessage(line);
            }
        } catch (Exception ex) {
            // parse edemezsen yine de kaydet
            e.setEventTime(LocalDateTime.now());
            e.setLevel("UNKNOWN");
            e.setLogger("unknown");
            e.setMessage(line);
        }

        return e;
    }
}