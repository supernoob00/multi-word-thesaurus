package com.somerdin.thesaurus.dao;

import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.Set;

@Component
public class JdbcWordDao implements WordDao {
    private final JdbcTemplate jdbcTemplate;

    public JdbcWordDao(JdbcTemplate jdbcTemplate) {
        this.jdbcTemplate = jdbcTemplate;
    }

    @Override
    public Set<String> getWordSynonyms(String word) {
        String sql = "SELECT synonym FROM word_synonyms WHERE word = ?;";
        SqlRowSet rs = jdbcTemplate.queryForRowSet(sql, word);
        
        Set<String> synonyms = new HashSet<>();
        while (rs.next()) {
            synonyms.add(rs.getString("synonym"));
        }
        return synonyms;
    }
}
