package com.tr.kyc.controller;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.web.bind.annotation.*;

import com.tr.kyc.model.LogEvent;
import com.tr.kyc.repository.LogEventRepository;

@RestController
@RequestMapping("/api/logs")
@CrossOrigin
public class LogController {

    private final LogEventRepository repo;

    public LogController(LogEventRepository repo) {
        this.repo = repo;
    }

    @GetMapping
    public Page<LogEvent> list(@RequestParam(defaultValue = "0") int page,
                              @RequestParam(defaultValue = "20") int size) {
        return repo.findAll(PageRequest.of(page, size));
    }
}