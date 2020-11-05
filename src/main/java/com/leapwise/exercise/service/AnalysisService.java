package com.leapwise.exercise.service;

import com.sun.syndication.feed.synd.SyndEntry;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


public interface AnalysisService {

    public String analyzeRSS(List<String> urls, String uuid);
    public Map<String, Integer> fetchResult(String uuid);
}
