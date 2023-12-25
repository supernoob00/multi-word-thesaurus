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
public class WordGraph implements Iterable<Map.Entry<String, Set<String>>> {
    public static final int DEFAULT_SEARCH_DEPTH = 1;

    // the DAO which represents the thesaurus used to construct the graph
    private final WordDao wordDao;

    // all words
    private final Set<String> origins;
    // an undirected graph of words
    private final Map<String, Set<String>> graph;
    // map of all vertices to depth of search from that vertex; zero means it
    // is a terminal vertex
    public final Map<String, Integer> depths;
    private boolean terminalNodesRemoved;

    public WordGraph(WordDao wordDao) {
        this.wordDao = wordDao;
        this.origins = new HashSet<>();
        this.graph = new HashMap<>();
        this.depths = new HashMap<>();
    }

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

                    Set<String> synonyms = wordDao.getWordSynonyms(currWord);
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

    public boolean terminalNodesRemoved() {
        return terminalNodesRemoved;
    }

    public void removeTerminalNodes() {
        terminalNodesRemoved = true;
        Iterator<Map.Entry<String, Set<String>>> it = graph.entrySet().iterator();

        while (it.hasNext()) {
            Map.Entry<String, Set<String>> entry = it.next();
            String node = entry.getKey();
            Set<String> neighbors = entry.getValue();

            if (neighbors.size() == 1) {
                String wordToRemove = neighbors.iterator().next();
                graph.get(wordToRemove).remove(node);
                it.remove();
            }
        }
    }

    @Override
    public String toString() {
        return graph.toString();
    }

    /**
     * Returns the graph as a string in the DOT format; note that this method
     * mutates the graph by removing all terminal nodes (i.e. nodes with only
     * one connected edge)
     *
     * @return a string in DOT format representing the graph
     */
    public String toDotFormat() {
        final String highlightOption = "[fillcolor=\"orange\", style=\"filled\"]";

        Set<UnorderedPair<String>> connections = new HashSet<>();

        StringBuilder sb = new StringBuilder();
        sb.append("strict graph {\n");

        for (String originWord : origins) {
            sb.append(originWord).append(" " + highlightOption + "\n");
        }

        for (Map.Entry<String, Set<String>> e : graph.entrySet()) {
            String nodeWord = e.getKey();

            for (String neighborWord : e.getValue()) {
                UnorderedPair<String> wordPair = new UnorderedPair<>(nodeWord, neighborWord);

                if (!connections.contains(wordPair)) {

                    sb.append("\"").append(nodeWord).append("\"");
                    sb.append(" -- ");
                    sb.append("\"").append(neighborWord).append("\"\n");

                    connections.add(wordPair);
                }
            }
        }
        sb.append("}");
        return sb.toString();
    }

    @Override
    public Iterator<Map.Entry<String, Set<String>>> iterator() {
        return graph.entrySet().iterator();
    }
}
