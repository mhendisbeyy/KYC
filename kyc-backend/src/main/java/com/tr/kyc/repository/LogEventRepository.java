package com.tr.kyc.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.tr.kyc.model.LogEvent;

public interface LogEventRepository extends JpaRepository<LogEvent, Long> {}