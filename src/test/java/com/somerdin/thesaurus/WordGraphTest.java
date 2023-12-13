package com.somerdin.thesaurus;

import com.somerdin.thesaurus.dao.JdbcWordDao;
import com.somerdin.thesaurus.dao.WordDao;
import com.somerdin.thesaurus.structures.WordGraph;
import com.somerdin.thesaurus.util.SqlDataSourceUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;

import javax.sql.DataSource;
import java.util.HashSet;
import java.util.Set;

import static com.somerdin.thesaurus.TestWords.TEST_WORD_1;
import static com.somerdin.thesaurus.TestWords.TEST_WORD_SYNONYMS_1;

public class WordGraphTest {

    private WordDao dao;

    public WordGraphTest() {
        DataSource source = SqlDataSourceUtil.getDataSource();
        dao = new JdbcWordDao(new JdbcTemplate(source));
    }

    @Test
    public void single_word_graph_valid() {
        WordGraph graph = new WordGraph(dao);
        graph.addWord(TEST_WORD_1, 1);

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

        // check all search depth values are correct
        Assert.assertEquals(1, graph.depthFrom(TEST_WORD_1));
        for (String synonym : TEST_WORD_SYNONYMS_1) {
            Assert.assertEquals(0, graph.depthFrom(synonym));
        }
    }

    @Test
    public void graph_single_word_added_with_depth_2_valid() {
        WordGraph graph = new WordGraph(new MockWordDao());
        graph.addWord("happy", 2);

        // check graph size is correct
        // Assert.assertEquals(graph.depths.size(), 5);
        // Assert.assertEquals(graph.size(), 5);

        // check each vertex has expected neighbors
        Assert.assertEquals(graph.getNeighbors("happy"), Set.of("glad",
                "mirthful", "in good spirits"));
        Assert.assertEquals(graph.getNeighbors("glad"), Set.of("happy",
                "mirthful", "excited"));
        Assert.assertEquals(graph.getNeighbors("mirthful"), Set.of("happy",
                "glad"));
        Assert.assertEquals(graph.getNeighbors("in good spirits"), Set.of(
                "happy"));
        Assert.assertEquals(graph.getNeighbors("excited"), Set.of("glad"));
    }

    @Test
    public void graph_two_words_added_with_depth_2_valid() {
        WordGraph graph = new WordGraph(new MockWordDao());
        graph.addWord("happy", 2);
        graph.addWord("nervous", 2);

        // check graph size is correct
        Assert.assertEquals(9, graph.size());

        // check neighbors of each vertex are correct
        Assert.assertEquals(graph.getNeighbors("happy"), Set.of("glad",
                "mirthful", "in good spirits"));
        Assert.assertEquals(graph.getNeighbors("glad"), Set.of("happy",
                "mirthful", "excited"));
        Assert.assertEquals(graph.getNeighbors("mirthful"), Set.of("happy",
                "glad"));
        Assert.assertEquals(graph.getNeighbors("in good spirits"), Set.of(
                "happy"));
        Assert.assertEquals(graph.getNeighbors("excited"), Set.of("glad",
                "anxious"));
        Assert.assertEquals(graph.getNeighbors("anxious"), Set.of("excited",
                "scared", "nervous"));
        Assert.assertEquals(graph.getNeighbors("nervous"), Set.of("anxious",
                "worried"));
        Assert.assertEquals(graph.getNeighbors("worried"), Set.of("nervous"));
        Assert.assertEquals(graph.getNeighbors("scared"), Set.of("anxious"));
    }

    @Test
    public void multiple_word_test_graph_real_database() {
        SingleConnectionDataSource source = SqlDataSourceUtil.getDataSource();
        JdbcTemplate template = new JdbcTemplate();
        template.setDataSource(source);
        WordGraph graph = new WordGraph(new JdbcWordDao(template));

        graph.addWord("melancholy", 2);
        graph.addWord("pensive", WordGraph.DEFAULT_SEARCH_DEPTH);
        graph.addWord("depressed", WordGraph.DEFAULT_SEARCH_DEPTH);
        graph.addWord("happy", WordGraph.DEFAULT_SEARCH_DEPTH);
        System.out.println(graph.size());
        System.out.println(graph);
    }
}
