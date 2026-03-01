package com.tr.kyc.repository;
import org.springframework.data.jpa.repository.JpaRepository;

import com.tr.kyc.model.IngestionCheckpoint;

public interface IngestionCheckpointRepository extends JpaRepository<IngestionCheckpoint, String> {}