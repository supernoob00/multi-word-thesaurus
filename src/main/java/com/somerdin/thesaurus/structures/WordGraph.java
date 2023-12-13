package com.somerdin.thesaurus.structures;

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
    public static final int DEFAULT_SEARCH_DEPTH = 1;

    public static String toDotFormat(WordGraph wg) {
        wg.removeTerminalNodes();
        StringBuilder sb = new StringBuilder();
        sb.append("strict graph {\n");
        for (Map.Entry<String, Set<String>> e : wg.graph.entrySet()) {

            Iterator<String> it = e.getValue().iterator();
            while (it.hasNext()) {
                sb.append("\"").append(e.getKey()).append("\"");
                sb.append(" -- ");
                sb.append("\"").append(it.next()).append("\"\n");
            }
        }
        sb.append("}");
        return sb.toString();
    }

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

        while (depth > 0 && !queue.isEmpty()) {
            int size = queue.size();

            for (int k = 0; k < size; k++) {
                String currWord = queue.remove();

                if (!searched.contains(currWord)
                        && (!depths.containsKey(currWord) || depths.get(currWord) > depth)) {
                    searched.add(currWord);
                    depths.put(currWord, depth);

                    Set<String> synonyms = dao.getWordSynonyms(currWord);
                    Set<String> currNeighbors = graph.get(currWord);
                    if (currNeighbors == null) {
                        graph.put(currWord, synonyms);
                    } else {
                        currNeighbors.addAll(synonyms);
                    }

                    for (String neighbor : graph.get(currWord)) {
                        Set<String> connections = graph.get(neighbor);
                        if (connections == null) {
                            Set<String> newSet = new HashSet<>();
                            newSet.add(currWord);
                            graph.put(neighbor, newSet);
                        } else {
                            connections.add(currWord);
                        }
                        queue.add(neighbor);
                    }
                }
            }
            depth--;
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
            return 0;
        }
        return depths.get(word);
    }

    public Collection<String> addedWords() {
        return origins;
    }

    public boolean isAddedWord(String word) {
        return origins.contains(word);
    }

    private void removeParallelEdges() {
        for (Map.Entry<String, Set<String>> e : graph.entrySet()) {
            for (String neighbor : e.getValue()) {
                Set<String> words = graph.get(neighbor);
                if (words != null) {
                    words.remove(e.getKey());
                }
            }
        }
    }

    private void removeTerminalNodes() {
        Iterator<Map.Entry<String, Set<String>>> entrySetIt = graph.entrySet().iterator();
        Set<String> toRemove = new HashSet<>();

        while (entrySetIt.hasNext()) {
            Map.Entry<String, Set<String>> entry = entrySetIt.next();
            Set<String> neighbors = entry.getValue();

            Iterator<String> neighborIt = neighbors.iterator();
            while (neighborIt.hasNext()) {
                String next = neighborIt.next();
                if (graph.get(next).size() <= 1) {
                    neighborIt.remove();
                    toRemove.add(next);
                }
            }
        }

        for (String remove : toRemove) {
            graph.remove(remove);
        }
    }

    private void removeTerminalNodes2() {
        Map<String, Integer> counts = new HashMap<>();

        for (Map.Entry<String, Set<String>> e : graph.entrySet()) {
            Integer count = counts.get(e.getKey());
            if (count == null) {
                counts.put(e.getKey(), e.getValue().size());
            } else {
                counts.replace(e.getKey(), count + e.getValue().size());
            }
            for (String neighbor : e.getValue()) {
                Integer count2 = counts.get(neighbor);
                if (count2 == null) {
                    counts.put(neighbor, 1);
                } else {
                    counts.replace(neighbor, count2 + 1);
                }
            }
        }

        for (Map.Entry<String, Integer> e : counts.entrySet()) {
            if (e.getValue() <= 1) {
                graph.remove(e.getKey());
            }
        }
        System.out.println(counts.toString());
        System.out.println(graph);
    }

    @Override
    public String toString() {
        return graph.toString();
    }
}
