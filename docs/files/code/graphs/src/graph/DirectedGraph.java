package graph;

import java.util.List;

/**
 * Interface for a directed graph (digraph).
 *
 * Extends Graph with operations that distinguish between incoming and
 * outgoing edges. In a directed graph, each edge has a specific direction:
 * an edge from u to v is NOT the same as an edge from v to u.
 *
 * The inherited methods behave as follows:
 *   - degree(v)        returns inDegree(v) + outDegree(v)
 *   - incidentEdges(v) returns incomingEdges(v) + outgoingEdges(v)
 *   - getEdge(u, v)    only matches edges directed from u to v
 */
public interface DirectedGraph extends Graph {

    /** Return all edges leaving vertex v (where v is the origin). */
    List<Edge> outgoingEdges(Vertex v);

    /** Return all edges arriving at vertex v (where v is the destination). */
    List<Edge> incomingEdges(Vertex v);

    /** Return the number of edges leaving vertex v. */
    int outDegree(Vertex v);

    /** Return the number of edges arriving at vertex v. */
    int inDegree(Vertex v);
}
