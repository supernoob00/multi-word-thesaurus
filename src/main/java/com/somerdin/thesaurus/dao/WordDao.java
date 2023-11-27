package com.somerdin.thesaurus.dao;

import java.util.Collection;

public interface WordDao {
    Collection<String> getWordSynonyms(String word);
}
