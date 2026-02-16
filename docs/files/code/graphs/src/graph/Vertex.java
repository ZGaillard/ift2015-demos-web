package graph;

/**
 * A vertex (node) in a graph.
 *
 * This is a simple container that holds a label. Two vertices are
 * distinguished by object identity (==), not by their label. This means
 * you can have two different Vertex objects both labelled "A" and they
 * will be treated as distinct vertices.
 */
public class Vertex {

    public String label;

    public Vertex(String label) {
        this.label = label;
    }

    public String toString() {
        return label;
    }
}
