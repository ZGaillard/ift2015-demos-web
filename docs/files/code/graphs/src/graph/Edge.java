package graph;

/**
 * An edge in a graph, connecting two vertices with a label.
 *
 * Every edge stores an origin and a destination vertex:
 *   - In a DIRECTED graph, the direction matters: origin -> dest.
 *   - In an UNDIRECTED graph, origin and dest are interchangeable;
 *     they simply record the two endpoints.
 *
 * Like Vertex, edges are compared by object identity (==).
 */
public class Edge {

    public String label;
    public Vertex origin;
    public Vertex dest;

    public Edge(Vertex origin, Vertex dest, String label) {
        this.origin = origin;
        this.dest = dest;
        this.label = label;
    }

    public String toString() {
        return "(" + origin + ", " + dest + ", " + label + ")";
    }
}
