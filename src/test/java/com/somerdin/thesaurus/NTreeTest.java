package com.somerdin.thesaurus;

import com.somerdin.thesaurus.model.NaryTree;
import com.somerdin.thesaurus.model.WordGraph;

public class NTreeTest {
    public static void main(String[] args) {
        WordGraph graph = new WordGraph(new MockWordDao());
        graph.addWord("happy", 2);
        graph.addWord("glad", 2);
        graph.addWord("excited", 2);
        NaryTree tree = new NaryTree(graph, "happy", 3);
        int x = 1;
    }
}
