package com.somerdin.thesaurus.model;

import com.somerdin.thesaurus.dao.WordDao;

import java.util.*;

public class WordGraph {
    /**
     * Data class for a graph vertex, which holds both the word as well as
     * the id (determined by the origin vertex). The id is kept so that only
     * connections between vertices with different ids are made.
     */

    private WordDao dao;
    private Map<String, Set<String>> graph;
    // map of all vertices to minimum depth it took to reach from an origin
    private Map<String, Integer> depths;

    public void addWord(String word, int searchDepth) {
        if (graph.containsKey(word)) {
            return;
        }
        Queue<String> queue = new ArrayDeque<>();
        queue.add(word);
        depths.put(word, 0);

        for (int i = 1; i <= searchDepth && !queue.isEmpty(); i++) {
            int size = queue.size();
            for (int k = 0; k < size; k++) {
                word = queue.remove();

                if (!graph.containsKey(word)) {
                    graph.put(word, new HashSet<>(dao.getWordSynonyms(word)));
                }
                if (!depths.containsKey(word) || depths.get(word) > i) {
                    for (String neighbor : graph.get(word)) {
                        if (!graph.containsKey(neighbor)) {
                            graph.put(neighbor, new HashSet<>(List.of(word)));
                        } else {
                            graph.get(neighbor).add(word);
                        }
                        queue.add(neighbor);
                        depths.put(neighbor, i);
                    }
                }
            }
        }
    }

    private boolean add(String origin, String dest) {
        if (!graph.containsKey(origin)) {
            throw new IllegalArgumentException("Origin node does not exist in" +
                    " graph.");
        }
        return true;
    }

    @Override
    public String toString() {
        return graph.toString();
    }
}
