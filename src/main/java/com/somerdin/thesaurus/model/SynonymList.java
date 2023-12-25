package com.somerdin.thesaurus.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class SynonymList {
    private String word;
    private List<String> synonyms;

    public SynonymList(String word, Collection<String> synonyms) {
        this.word = word;
        this.synonyms = new ArrayList<>(synonyms);
        Collections.sort(this.synonyms);
    }

    public String getWord() {
        return word;
    }

    public void setWord(String word) {
        this.word = word;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }
}
