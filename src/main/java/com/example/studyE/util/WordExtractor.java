package com.example.studyE.util;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class WordExtractor {
    private static final Pattern WORD = Pattern.compile("[A-Za-z]+(?:'[A-Za-z]+)?");
    private static final Set<String> STOP = Set.of(
            "the","a","an","to","of","and","or","is","are","am","was","were",
            "in","on","at","for","with","as","it","this","that","i","you","he","she","we","they"
    );

    public static List<String> topWords(List<String> sentences, int limit) {
        Map<String, Integer> freq = new HashMap<>();
        for (String s : sentences) {
            Matcher m = WORD.matcher(s == null ? "" : s);
            while (m.find()) {
                String w = m.group().toLowerCase();
                if (w.length() <= 2) continue;
                if (STOP.contains(w)) continue;
                freq.merge(w, 1, Integer::sum);
            }
        }
        return freq.entrySet().stream()
                .sorted((a,b) -> b.getValue().compareTo(a.getValue()))
                .limit(limit)
                .map(Map.Entry::getKey)
                .toList();
    }

    public static String pickExample(List<String> sentences, String word) {
        String w = word.toLowerCase();
        for (String s : sentences) {
            if (s == null) continue;

            String[] parts = s.split("(?<=[.!?])\\s+|\\n+");
            for (String p : parts) {
                if (p == null) continue;
                String cleaned = p.replaceAll("\\s+", " ").trim();
                if (cleaned.toLowerCase().contains(w)) {
                    if (cleaned.length() > 220) cleaned = cleaned.substring(0, 220) + "...";
                    return cleaned;
                }
            }
        }
        return "";
    }
}

