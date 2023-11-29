package com.somerdin.thesaurus.model;

import java.util.HashSet;
import java.util.Objects;
import java.util.Set;

/**
 * Implementation of an n-ary tree containing
 */
public class NaryTree {
    private static class Node {
        public final String word;
        public Set<Node> children = new HashSet<>();
        public boolean keep;

        public Node(String word) {
            this.word = word;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            Node node = (Node) o;
            return keep == node.keep
                    && Objects.equals(word, node.word)
                    && Objects.equals(children, node.children);
        }

        @Override
        public int hashCode() {
            return Objects.hash(word, children, keep);
        }
    }

    private WordGraph graph;
    private Node head;
    private String origin;

    public NaryTree(WordGraph graph, String origin, int depth) {
        this.graph = graph;
        head = new Node(origin);
        buildTree(head, new HashSet<>(), depth);
    }

    public Node head() {
        return head;
    }

    // TODO: use backtracking to remove elements from visited set, rather 
    //  than creating a new set
    //  TODO: use Pair class to remove wasteful boolean field from nodes
    // TODO: use iteration instead of recursion
    /**
     * Creates a tree of all paths from a word to
     *
     * @param root the current root node
     * @param visited every node along the path from the current root to the
     *                very top of the recursive call tree; every node's word
     *                must be unique
     * @param depth the depth at which to create all subtrees of the current
     *              root (zero means no subtrees are created)
     * @return the root node of an n-ary tree
     */
    private Node buildTree(Node root, Set<String> visited, int depth) {
        if (graph.isAddedWord(root.word)) {
            root.keep = true;
        }
        if (depth == 0) {
            return root;
        }

        Set<String> updatedVisited = new HashSet<>(visited);
        updatedVisited.add(root.word);

        for (String neighbor : graph.getNeighbors(root.word)) {
            // call
            if (visited.contains(neighbor)) {
                continue;
            }

            // recursive call to build all subtrees of parent node
            Node child = buildTree(new Node(neighbor), updatedVisited, depth - 1);

            // if a child node is marked as kept, it should be added as a 
            // subtree and all of its parents must also be marked as kept
            if (child.keep) {
                root.children.add(child);
                root.keep = true;
            }
        }
        return root;
    }
}
