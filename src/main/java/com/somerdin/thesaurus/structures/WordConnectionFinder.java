package com.somerdin.thesaurus.structures;

import com.somerdin.thesaurus.dao.WordDao;

import java.util.*;

public class WordConnectionFinder {
    public static final int MAX_WORDS_ADD = 5;

    private WordGraph graph;

    public WordConnectionFinder(WordDao dao) {
        graph = new WordGraph(dao);
    }

    public boolean addWord(String word) {
        if (graph.addedWords().size() == MAX_WORDS_ADD) {
            return false;
        }
        graph.addWord(word, WordGraph.DEFAULT_SEARCH_DEPTH);
        return true;
    }

    public int addWords(Collection<String> words) {
        int added = 0;
        for (String word : words) {
            if (!addWord(word)) {
                return added;
            }
            added++;
        }
        return added;
    }

    public List<String> getConnectedWords() throws IllegalStateException {
        if (graph.addedWords().isEmpty()) {
            throw new IllegalStateException();
        }
        // choose arbitrary origin point
        String origin = graph.addedWords().iterator().next();
        PathTree tree = new PathTree(graph, origin, PathTree.DEFAULT_TREE_DEPTH);

        Set<String> wordSet = tree.getConnectedWords();
        wordSet.removeAll(graph.addedWords());

        List<String> wordList = new ArrayList<>(wordSet);
        Collections.sort(wordList);
        return wordList;
    }
}
