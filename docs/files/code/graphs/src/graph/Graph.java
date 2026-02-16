package graph;

import java.util.List;

/**
 * Interface for an undirected graph.
 *
 * An undirected graph is a set of vertices connected by edges that have
 * no direction: if there is an edge between u and v, you can traverse
 * it from u to v or from v to u.
 *
 * Three concrete implementations are provided, each with different
 * performance trade-offs:
 *   - EdgeListGraph        (simple, but most queries are O(m))
 *   - AdjacencyMatrixGraph (O(1) edge lookup, but O(n^2) space)
 *   - AdjacencyListGraph   (good balance for sparse graphs)
 */
public interface Graph {

    // ==================== Mutation ====================

    /** Add a new vertex with the given label to the graph. */
    Vertex insertVertex(String label);

    /** Add a new edge between vertices u and v with the given label. */
    Edge insertEdge(Vertex u, Vertex v, String label);

    /** Remove vertex v and all its incident edges from the graph. */
    void removeVertex(Vertex v);

    /** Remove edge e from the graph. */
    void removeEdge(Edge e);

    // ===================== Query =====================

    /** Return the edge between u and v, or null if none exists. */
    Edge getEdge(Vertex u, Vertex v);

    /** Return a list of all vertices in the graph. */
    List<Vertex> vertices();

    /** Return a list of all edges in the graph. */
    List<Edge> edges();

    /** Return all edges that touch vertex v. */
    List<Edge> incidentEdges(Vertex v);

    /** Return the number of edges touching vertex v. */
    int degree(Vertex v);
}
