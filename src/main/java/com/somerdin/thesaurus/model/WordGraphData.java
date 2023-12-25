package com.somerdin.thesaurus.model;

import com.somerdin.thesaurus.structures.WordGraph;

import java.util.*;

public class WordGraphData {
    public static class NodeEdgeCount {
        private String word;
        private int edgeCount;

        public NodeEdgeCount(String word, int edgeCount) {
            this.word = word;
            this.edgeCount = edgeCount;
        }

        public String getWord() {
            return word;
        }

        public int getEdgeCount() {
            return edgeCount;
        }
    }

    private final Collection<String> words;
    private final String graph;
    private List<NodeEdgeCount> edgeCounts;

    public WordGraphData(WordGraph wg) {
        this.words = wg.addedWords();
        this.graph = wg.toDotFormat();

        this.edgeCounts = new ArrayList<>();
        for (Map.Entry<String, Set<String>> entry : wg) {
            edgeCounts.add(new NodeEdgeCount(entry.getKey(),
                    entry.getValue().size()));
        }
        Comparator<NodeEdgeCount> comparator = Comparator
                .comparing(NodeEdgeCount::getEdgeCount)
                .thenComparing(NodeEdgeCount::getWord);
        edgeCounts.sort(comparator);
    }

    public Collection<String> getWords() {
        return words;
    }

    public String getGraph() {
        return graph;
    }

    public List<NodeEdgeCount> getEdgeCounts() {
        return edgeCounts;
    }
}
