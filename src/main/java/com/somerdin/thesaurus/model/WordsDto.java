package com.somerdin.thesaurus.model;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;

public class WordsDto {
    @NotNull @NotEmpty
    private List<String> words;

    public List<String> getWords() {
        return words;
    }

    public void setWords(List<String> words) {
        this.words = words;
    }
}
