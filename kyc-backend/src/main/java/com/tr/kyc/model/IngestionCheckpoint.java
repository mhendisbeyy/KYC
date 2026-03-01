package com.tr.kyc.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
@Table(name = "ingestion_checkpoint")
public class IngestionCheckpoint {

    @Id
    private String id; // ör: "app-log"

    @Column(nullable = false)
    private String filePath;

    // byte offset = dosyada kaldığın yer
    @Column(nullable = false)
    private long byteOffset;

    // dosya değişti mi/rotate oldu mu anlamak için
    private long lastKnownSize;
    private LocalDateTime updatedAt;

    public IngestionCheckpoint() {}

    public IngestionCheckpoint(String id, String filePath) {
        this.id = id;
        this.filePath = filePath;
        this.byteOffset = 0L;
        this.lastKnownSize = 0L;
        this.updatedAt = LocalDateTime.now();
    }

    // getters/setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getFilePath() { return filePath; }
    public void setFilePath(String filePath) { this.filePath = filePath; }

    public long getByteOffset() { return byteOffset; }
    public void setByteOffset(long byteOffset) { this.byteOffset = byteOffset; }

    public long getLastKnownSize() { return lastKnownSize; }
    public void setLastKnownSize(long lastKnownSize) { this.lastKnownSize = lastKnownSize; }

    public LocalDateTime getUpdatedAt() { return updatedAt; }
    public void setUpdatedAt(LocalDateTime updatedAt) { this.updatedAt = updatedAt; }
}