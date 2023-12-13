package com.somerdin.thesaurus;

import com.somerdin.thesaurus.dao.WordDao;

import java.util.*;

public class MockWordDao implements WordDao {
    private Map<String, Set<String>> thesaurus = new HashMap<>();

    public MockWordDao() {
        thesaurus.put("happy", new HashSet<>(List.of("glad", "mirthful", "in good " +
                "spirits")));
        thesaurus.put("glad", new HashSet<>(List.of("happy", "mirthful",
                "excited")));
        thesaurus.put("excited", new HashSet<>(List.of("anticipating")));
        thesaurus.put("nervous", new HashSet<>(List.of("anxious", "worried")));
        thesaurus.put("anxious", new HashSet<>(List.of("excited", "scared")));
        thesaurus.put("scared", new HashSet<>(List.of("nervous",
                "frightened")));
    }

    @Override
    public Set<String> getWordSynonyms(String word) {
        Set<String> synonyms = thesaurus.get(word);
        return synonyms == null ? Collections.EMPTY_SET : synonyms;
    }
}
