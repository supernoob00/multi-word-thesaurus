package com.somerdin.thesaurus.model;

import com.somerdin.thesaurus.dao.WordDao;

import java.util.*;

/**
 * Represents a undirected graph of words, where each connection means that
 * one or both word(s) is a synonym of the other. The graph can be created
 * and expanded by adding one or more "origin" words. When an origin word is
 * added to the graph, all connected words, as well as connections to existing
 * words in the graph, are created to a given depth.
 */
public class WordGraph {
    public static final int DEFAULT_SEARCH_DEPTH = 2;

    // the DAO which represents the thesaurus used to construct the graph
    private WordDao dao;

    // all words
    private Set<String> origins;
    // an undirected graph of words
    private Map<String, Set<String>> graph;
    // map of all vertices to depth of search from that vertex; zero means it
    // is a terminal vertex
    public Map<String, Integer> depths;

    public WordGraph(WordDao dao) {
        this.dao = dao;
        this.origins = new HashSet<>();
        this.graph = new HashMap<>();
        this.depths = new HashMap<>();
    }

    /**
     *
     * @param word
     * @param depth
     * @return false if word is already in set of added words, otherwise true
     */
    public boolean addWord(String word, int depth) {
        if (origins.contains(word)) {
            return false;
        }
        origins.add(word);

        Set<String> searched = new HashSet<>();
        Queue<String> queue = new ArrayDeque<>();

        queue.add(word);
        depths.put(word, depth);

        // TODO: change for loop to while loop
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
        return true;
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

    public boolean isAddedWord(String word) {
        return origins.contains(word);
    }

    @Override
    public String toString() {
        return graph.toString();
    }
}
