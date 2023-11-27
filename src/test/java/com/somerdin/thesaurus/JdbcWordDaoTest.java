package com.somerdin.thesaurus;

import com.somerdin.thesaurus.dao.JdbcWordDao;
import com.somerdin.thesaurus.dao.WordDao;
import org.junit.Assert;
import org.junit.Test;
import org.junit.jupiter.api.BeforeAll;
import org.postgresql.ds.PGSimpleDataSource;
import org.springframework.jdbc.core.JdbcTemplate;

import java.util.List;

public class JdbcWordDaoTest {
    private static final String TEST_WORD_1 = "ruction";
    private static final List<String> TEST_WORD_SYNONYMS_1 = List.of(
            "affray", "broil", "bustle", "clamor", "clatter", "donnybrook",
            "fight", "fracas", "fray", "free-for-all", "hassle", "hubbub",
            "hurly-burly", "melee", "out", "pother", "storm", "to-do"
    );
    private static final String TEST_WORD_2 = "cackle";

    private WordDao dao;

    public JdbcWordDaoTest() {
        setup();
    }

    @BeforeAll
    public void setup() {
        PGSimpleDataSource source = new PGSimpleDataSource();
        source.setServerName("localhost");
        source.setPortNumber(5432);
        source.setUser("postgres");
        source.setPassword("postgres1");
        source.setDatabaseName("thesaurus");
        dao = new JdbcWordDao(new JdbcTemplate(source));
    }

    @Test
    public void get_synonyms_returns_correct_synonyms() {
        List<String> actual = dao.getWordSynonyms(TEST_WORD_1);
        Assert.assertEquals(TEST_WORD_SYNONYMS_1, actual);
    }

    @Test
    public void get_synonyms_ignores_case() {
        List<String> actual = dao.getWordSynonyms("Ruction");
        Assert.assertEquals(TEST_WORD_SYNONYMS_1, actual);
    }

    @Test
    public void nonexistent_word_returns_zero_synonyms() {
        List<String> actual = dao.getWordSynonyms("ee");
        Assert.assertTrue(actual.isEmpty());
    }

    @Test
    public void same_word_on_multiple_lines_includes_all_words() {
        List<String> actual = dao.getWordSynonyms("cackle");
        Assert.assertTrue(actual.contains("carol"));
        Assert.assertTrue(actual.contains("Homeric laughter"));
    }
}
