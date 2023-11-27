package com.somerdin.thesaurus.model;

import com.somerdin.thesaurus.dao.WordDao;

import java.util.*;

public class WordGraph {
    public static final int SEARCH_DEPTH = 2;
    /**
     * Data class for a graph vertex, which holds both the word as well as
     * the id (determined by the origin vertex). The id is kept so that only
     * connections between vertices with different ids are made.
     */

    private WordDao dao;
    private Map<String, Set<String>> graph;
    // map of all vertices to depth of search from that vertex (zero means
    // it is a terminal vertex)
    public Map<String, Integer> depths;

    public WordGraph(WordDao dao) {
        this.dao = dao;
        this.graph = new HashMap<>();
        this.depths = new HashMap<>();
    }

    public void addWord(String word, int depth) {
        if (depths.containsKey(word) && depths.get(word) == 0) {
            return;
        }

        Set<String> searched = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();

        queue.add(word);
        depths.put(word, depth);

        for (; depth > 0 && !queue.isEmpty(); depth--) {
            int size = queue.size();
            for (int k = 0; k < size; k++) {
                word = queue.remove();

                Collection<String> synonyms = dao.getWordSynonyms(word);
                graph.merge(word, new HashSet<>(synonyms), (strings, strings2) -> {
                    strings.addAll(strings2);
                    return strings;
                });

                if (!searched.contains(word)
                        || (!depths.containsKey(word) || depths.get(word) < depth)) {
                    searched.add(word);

                    for (String neighbor : graph.get(word)) {
                        graph.merge(neighbor, new HashSet<>(List.of(word)), (s, w) -> {
                            s.addAll(w);
                            return s;
                        });
                        depths.merge(neighbor, depth - 1, Math::min);
                        queue.add(neighbor);
                    }
                }
            }
        }
    }

    public Set<String> getNeighbors(String word) {
        Set<String> neighbors = graph.get(word);
        return neighbors == null ? new HashSet<>() : neighbors;
    }

    public int size() {
        return graph.size();
    }

    public int depthFrom(String word) throws NoSuchElementException {
        if (!depths.containsKey(word)) {
            throw new NoSuchElementException();
        }
        return depths.get(word);
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
