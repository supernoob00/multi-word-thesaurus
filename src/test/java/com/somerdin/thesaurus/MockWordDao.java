package com.somerdin.thesaurus;

import com.somerdin.thesaurus.dao.WordDao;

import java.util.*;

public class MockWordDao implements WordDao {
    private Map<String, Set<String>> thesaurus = new HashMap<>();

    public MockWordDao() {
        thesaurus.put("happy", Set.of("glad", "mirthful", "in good spirits"));
        thesaurus.put("glad", Set.of("happy", "mirthful", "excited"));
        thesaurus.put("excited", Set.of("anticipating"));
        thesaurus.put("nervous", Set.of("anxious", "worried"));
        thesaurus.put("anxious", Set.of("excited", "scared"));
        thesaurus.put("scared", Set.of("nervous", "frightened"));
    }

    @Override
    public Collection<String> getWordSynonyms(String word) {
        Collection<String> synonyms = thesaurus.get(word);
        return synonyms == null ? Collections.EMPTY_SET : synonyms;
    }
}
