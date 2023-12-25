package com.somerdin.thesaurus.dao;

import java.util.List;
import java.util.Set;

public interface WordDao {
    Set<String> getWordSynonyms(String word);
}
