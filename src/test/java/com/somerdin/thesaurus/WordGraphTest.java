package com.somerdin.thesaurus;

import com.somerdin.thesaurus.dao.JdbcWordDao;
import com.somerdin.thesaurus.dao.WordDao;
import com.somerdin.thesaurus.model.WordGraph;
import com.somerdin.thesaurus.util.SqlDataSourceUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class WordGraphTest {
    private static final String TEST_WORD_1 = "ruction";
    private static final List<String> TEST_WORD_SYNONYMS_1 = List.of(
            "affray", "broil", "bustle", "clamor", "clatter", "donnybrook",
            "fight", "fracas", "fray", "free-for-all", "hassle", "hubbub",
            "hurly-burly", "melee", "out", "pother", "storm", "to-do"
    );

    private WordDao dao;

    public WordGraphTest() {
        DataSource source = SqlDataSourceUtil.getDataSource();
        dao = new JdbcWordDao(new JdbcTemplate(source));
    }

    @Test
    public void single_word_graph_valid() {
        WordGraph graph = new WordGraph(dao);
        graph.addWord(TEST_WORD_1);

        // check graph size is correct
        Assert.assertEquals(TEST_WORD_SYNONYMS_1.size() + 1, graph.size());

        // check added word has all its neighbors
        Set<String> expected = new HashSet<>(TEST_WORD_SYNONYMS_1);
        Set<String> actual = graph.getNeighbors(TEST_WORD_1);
        Assert.assertEquals(expected, actual);

        // check each neighbor has added word as its neighbor
        for (String synonym : TEST_WORD_SYNONYMS_1) {
            Set<String> originalWord = graph.getNeighbors(synonym);
            Assert.assertEquals(1, originalWord.size());
            Assert.assertTrue(originalWord.contains(TEST_WORD_1));
        }
    }
}
