package com.leapwise.exercise.controller;

import com.leapwise.exercise.service.AnalysisService;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;


import javax.validation.constraints.Size;
import java.util.List;
import java.util.Map;
import java.util.UUID;


@Validated
@RestController
public class ExerciseController {

    private final AnalysisService analysisService;

    public ExerciseController(AnalysisService analysisService) {
        this.analysisService = analysisService;
    }

    @GetMapping("/analyse/new")
    public ResponseEntity<String> analyzeData(@RequestParam("urls") @Size(min = 2) List<String> urls) {

        String uuid = UUID.randomUUID().toString();
        analysisService.analyzeRSS(urls, uuid);
        return ResponseEntity.ok(uuid);
    }

    @GetMapping("/frequency/{id}")
    public ResponseEntity<Map<String, Integer>> getResults(@PathVariable("id") String uuid) {
        return ResponseEntity.ok(analysisService.fetchResult(uuid));
    }




}
