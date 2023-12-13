package com.somerdin.thesaurus;

import com.somerdin.thesaurus.dao.JdbcWordDao;
import com.somerdin.thesaurus.dao.WordDao;
import com.somerdin.thesaurus.structures.WordConnectionFinder;
import com.somerdin.thesaurus.util.SqlDataSourceUtil;
import org.junit.Assert;
import org.junit.Test;
import org.springframework.jdbc.core.JdbcTemplate;

import javax.sql.DataSource;
import java.util.Collections;
import java.util.List;

public class WordConnectionFinderTest {

    private WordDao dao;

    public WordConnectionFinderTest() {
        DataSource source = SqlDataSourceUtil.getDataSource();
        dao = new JdbcWordDao(new JdbcTemplate(source));
    }

    @Test
    public void single_word_added_to_connection_finder_returns_valid() {
        WordConnectionFinder finder = new WordConnectionFinder(new MockWordDao());
        finder.addWord("happy");

        List<String> connectedWords = finder.getConnectedWords();
        Assert.assertEquals(Collections.EMPTY_LIST, connectedWords);

        finder.addWord("excited");
        Assert.assertEquals(
                List.of("glad", "mirthful"),
                finder.getConnectedWords());
    }
}
