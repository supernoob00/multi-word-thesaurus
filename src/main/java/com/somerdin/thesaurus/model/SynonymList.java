package com.somerdin.thesaurus.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class SynonymList {
    @NotNull
    @NotEmpty
    private List<String> words;
    @NotNull
    private List<String> synonyms;

    public SynonymList() {
    }

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }
}
