package com.tr.kyc.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;

@Entity
@Table(name = "log_event", indexes = {
        @Index(name = "idx_log_event_ts", columnList = "eventTime"),
        @Index(name = "idx_log_event_level", columnList = "level")
})
public class LogEvent {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    // İstersen log satırından parse edersin, istersen direkt ingest zamanı yazarsın
    private LocalDateTime eventTime;

    @Column(length = 20)
    private String level;

    @Column(length = 200)
    private String logger;

    @Column(length = 4000)
    private String message;

    // Ham satır (debug için)
    @Column(length = 6000)
    private String rawLine;

    // getters/setters
    public Long getId() { return id; }

    public LocalDateTime getEventTime() { return eventTime; }
    public void setEventTime(LocalDateTime eventTime) { this.eventTime = eventTime; }

    public String getLevel() { return level; }
    public void setLevel(String level) { this.level = level; }

    public String getLogger() { return logger; }
    public void setLogger(String logger) { this.logger = logger; }

    public String getMessage() { return message; }
    public void setMessage(String message) { this.message = message; }

    public String getRawLine() { return rawLine; }
    public void setRawLine(String rawLine) { this.rawLine = rawLine; }
}