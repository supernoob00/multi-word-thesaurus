package com.somerdin.thesaurus;

import com.somerdin.thesaurus.dao.JdbcWordDao;
import com.somerdin.thesaurus.dao.WordDao;
import com.somerdin.thesaurus.structures.PathTree;
import com.somerdin.thesaurus.structures.WordGraph;
import com.somerdin.thesaurus.util.SqlDataSourceUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Set;

public class PathTreeTest {
    private WordDao dao;

    public PathTreeTest() {
        DataSource source = SqlDataSourceUtil.getDataSource();
        dao = new JdbcWordDao(new JdbcTemplate(source));
    }

    @Test
    public void single_word_path_tree_has_only_one_node() {
        WordGraph graph = new WordGraph(new MockWordDao());
        graph.addWord(TestWords.TEST_WORD_1, 1);

        PathTree tree = new PathTree(graph, TestWords.TEST_WORD_1, 2);
        Assert.assertEquals(Set.of(TestWords.TEST_WORD_1), tree.getConnectedWords());
    }

    @Test
    public void two_words_added_path_trees_correct() {
        WordGraph graph = new WordGraph(new MockWordDao());
        graph.addWord("happy", WordGraph.DEFAULT_SEARCH_DEPTH);
        graph.addWord("excited", WordGraph.DEFAULT_SEARCH_DEPTH);

        PathTree tree1 = new PathTree(graph, "happy", 3);
        Assert.assertEquals(
                Set.of("happy", "excited", "glad", "mirthful"),
                tree1.getConnectedWords());

        PathTree tree2 = new PathTree(graph, "anticipating", 3);
        Assert.assertEquals(
                Set.of("anticipating", "excited", "glad", "happy"),
                tree2.getConnectedWords());
    }
}
