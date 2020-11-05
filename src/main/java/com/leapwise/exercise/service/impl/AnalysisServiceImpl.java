package com.leapwise.exercise.service.impl;

import com.leapwise.exercise.domain.Analysis;
import com.leapwise.exercise.repository.AnalysisRepository;
import com.leapwise.exercise.service.AnalysisService;
import com.sun.syndication.feed.synd.SyndEntry;
import com.sun.syndication.feed.synd.SyndFeed;
import com.sun.syndication.io.FeedException;
import com.sun.syndication.io.SyndFeedInput;
import com.sun.syndication.io.XmlReader;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.net.URL;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.stream.Collectors.toMap;

@Service
public class AnalysisServiceImpl implements AnalysisService {

    private final AnalysisRepository analysisRepository;

    public AnalysisServiceImpl(AnalysisRepository analysisRepository) {
        this.analysisRepository = analysisRepository;
    }

    @Override
    public String analyzeRSS(List<String> urls, String uuid) {
        List<SyndEntry> feedEntryList = new ArrayList<SyndEntry>();
        HashMap<String, Integer> analysisResult = new HashMap<String, Integer>();
        List<Analysis> analysisResultList = new ArrayList<Analysis>();
        
        for(String url : urls) {
            try {
                URL feedSource = new URL(url);
                SyndFeedInput input = new SyndFeedInput();
                SyndFeed feed = input.build(new XmlReader(feedSource));
                feedEntryList.addAll(feed.getEntries());
                countWords(feedEntryList, analysisResult);


            } catch (IOException | FeedException e) {
                e.printStackTrace();
            }
        }

        Map<String, Integer> repeatingWords = analysisResult.entrySet()
                .stream()
                .filter(map -> !map.getValue().equals(1))
                .collect(toMap(map -> map.getKey(), map -> map.getValue()));

        Map<String, Integer> repeatingWordsSorted = repeatingWords
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));

        List<Analysis> analysisList = new ArrayList<Analysis>();
        for (Map.Entry<String, Integer> entry : repeatingWordsSorted.entrySet()) {
            Analysis analysis = new Analysis();
            analysis.setUuid(uuid);
            analysis.setWord(entry.getKey());
            analysis.setCount(entry.getValue());
            analysisList.add(analysis);
        }

        analysisRepository.saveAll(analysisList);
        return null;
    }


    public HashMap<String, Integer> countWords(List<SyndEntry> feedEntryList, HashMap<String, Integer> wordsFrequency) {

        StringBuilder text = new StringBuilder();
        feedEntryList.forEach(entry -> {
            if (entry.getTitle() != null){
                text.append(entry.getTitle()).append("\n");
            }
            if (entry.getDescription() != null) {
                text.append(entry.getDescription().getValue()).append("\n");
            }
        });

        String[] keys = text.toString().split(" ");
        String[] uniqueKeys;
        int count = 0;

        uniqueKeys = getUniqueKeys(keys);
        for(String key: uniqueKeys)
        {
            if(null == key)
            {
                break;
            }
            for(String s : keys)
            {
                if(key.equals(s))
                {
                    count++;
                }
            }
            wordsFrequency.put(key, count);
            //System.out.println("Count of ["+key+"] is : "+count);
            count=0;
        }
        return wordsFrequency;
    }

    public static String[] getUniqueKeys(String[] keys)
    {
        String[] uniqueKeys = new String[keys.length];
        uniqueKeys[0] = keys[0];
        int uniqueKeyIndex = 1;
        boolean keyAlreadyExists = false;
        for(int i=1; i<keys.length ; i++)
        {
            for(int j=0; j<=uniqueKeyIndex; j++)
            {
                if(keys[i].equals(uniqueKeys[j]))
                {
                    keyAlreadyExists = true;
                }
            }
            if(!keyAlreadyExists)
            {
                uniqueKeys[uniqueKeyIndex] = keys[i];
                uniqueKeyIndex++;
            }
            keyAlreadyExists = false;
        }
        return uniqueKeys;
    }

    @Override
    public Map<String, Integer> fetchResult(String uuid) {

        List<Analysis> analysisList = analysisRepository.findAllByUuid(uuid);
        Map<String, Integer> analysisMap = analysisList.stream()
                .collect(Collectors.toMap(Analysis::getWord, Analysis::getCount));

        Map<String, Integer> analysisMapSorted = analysisMap
                .entrySet()
                .stream()
                .sorted(Collections.reverseOrder(Map.Entry.comparingByValue()))
                .limit(3)
                .collect(
                        toMap(Map.Entry::getKey, Map.Entry::getValue, (e1, e2) -> e2,
                                LinkedHashMap::new));
        return analysisMapSorted;
    }
}
