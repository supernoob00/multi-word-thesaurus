package com.somerdin.thesaurus.model;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.somerdin.thesaurus.structures.WordGraph;

import java.util.*;

public class WordGraphData {
    public static class NodeEdgeCount {
        private final String word;
        private final int edgeCount;

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

        @Override
        public String toString() {
            return "NodeEdgeCount{" +
                    "word='" + word + '\'' +
                    ", edgeCount=" + edgeCount +
                    '}';
        }
    }

    @JsonProperty("words")
    private final Collection<String> addedWords;
    private final String graph;
    private final int nodeCount;
    private final int edgeCount;
    private final List<NodeEdgeCount> edgeCounts;

    public WordGraphData(WordGraph wg) {
        this.addedWords = wg.addedWords();
        this.graph = wg.toDotFormat();
        this.nodeCount = wg.size();
        this.edgeCount = wg.edgeCount();
        this.edgeCounts = new ArrayList<>();

        for (Map.Entry<String, Set<String>> entry : wg) {
            edgeCounts.add(new NodeEdgeCount(entry.getKey(),
                    entry.getValue().size()));
        }

        // sort node edge counts
        Comparator<NodeEdgeCount> comparator = Comparator
                .comparing(NodeEdgeCount::getWord, (w1, w2) -> {
                    if (addedWords.contains(w1) && addedWords.contains(w2)) {
                        return 0;
                    } else if (addedWords.contains(w1)) {
                        return -1;
                    } else if (addedWords.contains(w2)){
                        return 1;
                    } else {
                        return 0;
                    }
                })
                .thenComparing(NodeEdgeCount::getEdgeCount, Comparator.reverseOrder())
                .thenComparing(NodeEdgeCount::getWord);
        edgeCounts.sort(comparator);
        System.out.println(edgeCounts);
    }

    public Collection<String> getAddedWords() {
        return addedWords;
    }

    public String getGraph() {
        return graph;
    }

    public int getNodeCount() {
        return nodeCount;
    }

    public int getEdgeCount() {
        return edgeCount;
    }

    public List<NodeEdgeCount> getEdgeCounts() {
        return edgeCounts;
    }
}
