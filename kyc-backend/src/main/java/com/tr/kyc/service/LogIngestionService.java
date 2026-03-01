package com.tr.kyc.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.tr.kyc.model.IngestionCheckpoint;
import com.tr.kyc.model.LogEvent;
import com.tr.kyc.repository.IngestionCheckpointRepository;
import com.tr.kyc.repository.LogEventRepository;

import java.io.RandomAccessFile;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Service
public class LogIngestionService {

    private final LogEventRepository logEventRepository;
    private final IngestionCheckpointRepository checkpointRepository;

    private final LogLineParser parser = new LogLineParser();

    @Value("${app.ingestion.log-path}")
    private String logPath;

    @Value("${app.ingestion.batch-size:200}")
    private int batchSize;

    public LogIngestionService(LogEventRepository logEventRepository,
                               IngestionCheckpointRepository checkpointRepository) {
        this.logEventRepository = logEventRepository;
        this.checkpointRepository = checkpointRepository;
    }

    /**
     * poll-ms application.properties'ten ayarlanır
     */
    @Scheduled(fixedDelayString = "${app.ingestion.poll-ms:1000}")
    public void tick() {
        ingestOnceSafely();
    }

    private void ingestOnceSafely() {
        try {
            ingestOnce();
        } catch (Exception e) {
            // Burada logla; hata olursa offset ilerletmediğimiz için
            // bir sonraki tick'te kaldığı yerden devam eder.
            System.err.println("Ingestion error: " + e.getMessage());
        }
    }

    @Transactional
    public void ingestOnce() throws Exception {
        Path path = Path.of(logPath);
        if (!Files.exists(path)) return;

        // checkpoint al / yoksa oluştur
        IngestionCheckpoint cp = checkpointRepository.findById("app-log")
                .orElseGet(() -> checkpointRepository.save(new IngestionCheckpoint("app-log", logPath)));

        // log rotate / truncate senaryosu: dosya küçülmüşse offset sıfırlanır
        long size = Files.size(path);
        if (size < cp.getByteOffset()) {
            cp.setByteOffset(0L);
        }

        List<LogEvent> buffer = new ArrayList<>(batchSize);

        try (RandomAccessFile raf = new RandomAccessFile(path.toFile(), "r")) {
            raf.seek(cp.getByteOffset());

            while (true) {
                String line = raf.readLine(); // ISO-8859-1 döner; aşağıda UTF-8'e çevireceğiz
                if (line == null) break;

                // readLine latin1 gibi okur; UTF-8 için dönüşüm:
                String utf8 = new String(line.getBytes(StandardCharsets.ISO_8859_1), StandardCharsets.UTF_8);

                buffer.add(parser.parse(utf8));

                if (buffer.size() >= batchSize) {
                    flushBatchAndCheckpoint(buffer, cp, raf.getFilePointer(), size);
                    buffer.clear();
                }
            }

            if (!buffer.isEmpty()) {
                flushBatchAndCheckpoint(buffer, cp, raf.getFilePointer(), size);
                buffer.clear();
            }
        }
    }

    private void flushBatchAndCheckpoint(List<LogEvent> batch,
                                         IngestionCheckpoint cp,
                                         long newOffset,
                                         long fileSize) {

        // 1) Önce logları yaz
        logEventRepository.saveAll(batch);

        // 2) Sonra checkpoint’i ilerlet
        cp.setByteOffset(newOffset);
        cp.setLastKnownSize(fileSize);
        cp.setUpdatedAt(LocalDateTime.now());
        checkpointRepository.save(cp);
    }
}