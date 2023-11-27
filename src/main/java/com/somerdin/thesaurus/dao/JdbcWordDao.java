package com.somerdin.thesaurus.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

@Component
public class JdbcWordDao implements WordDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcWordDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Collection<String> getWordSynonyms(String word) {
        String sql = "SELECT synonym FROM word_synonyms WHERE word ILIKE ?;";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, word);
        
        List<String> synonyms = new ArrayList<>();
        while (rs.next()) {
            synonyms.add(rs.getString("synonym"));
        }
        return synonyms;
    }
}
