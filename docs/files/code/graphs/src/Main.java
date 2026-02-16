import graph.*;
import edgelist.*;
import matrix.*;
import adjlist.*;

void main() {

    IO.println("========================================");
    IO.println("  Graph 1: Simple Undirected Graph");
    IO.println("  V = {a, b, c}  E = {α(a,b), β(b,c)}");
    IO.println("========================================");
    IO.println();

    // stores two flat lists: List<Vertex> and List<Edge>
    demonstrateSimpleGraph(new EdgeListGraph(), "EdgeListGraph");

    // stores a 2D array matrix[i][j] = edge between i and j
    demonstrateSimpleGraph(new AdjacencyMatrixGraph(), "AdjacencyMatrixGraph");

    // each vertex stores direct O(1) access to its incidence list
    demonstrateSimpleGraph(new AdjacencyListGraph(), "AdjacencyListGraph");


    IO.println("========================================");
    IO.println("  Graph 2: Full Whiteboard Directed Graph");
    IO.println("  V = {a, b, c}  E = {α(a→b), β(b→a), γ(b→c), δ(c→c)}");
    IO.println("========================================");
    IO.println();

    // stores two flat lists: List<Vertex> and List<Edge>
    demonstrateFullGraph(new DirectedEdgeListGraph(), "DirectedEdgeListGraph");

    // stores a 2D array matrix[i][j] = edge FROM i TO j only
    demonstrateFullGraph(new DirectedAdjacencyMatrixGraph(), "DirectedAdjacencyMatrixGraph");

    // each vertex stores two lists: outgoing and incoming
    demonstrateFullGraph(new DirectedAdjacencyListGraph(), "DirectedAdjacencyListGraph");
}

private static void demonstrateSimpleGraph(Graph g, String name) {
    Vertex a = g.insertVertex("a");
    Vertex b = g.insertVertex("b");
    Vertex c = g.insertVertex("c");
    Edge alpha = g.insertEdge(a, b, "α");
    Edge beta = g.insertEdge(b, c, "β");

    IO.println("--- " + name + " ---");
    IO.println("Vertices : " + g.vertices());
    IO.println("Edges    : " + g.edges());
    IO.println();

    IO.println("Vertex a | degree=" + g.degree(a) + " | incident: " + g.incidentEdges(a));
    IO.println("Vertex b | degree=" + g.degree(b) + " | incident: " + g.incidentEdges(b));
    IO.println("Vertex c | degree=" + g.degree(c) + " | incident: " + g.incidentEdges(c));
    IO.println();

    IO.println("getEdge(a, b) = " + g.getEdge(a, b));
    IO.println("getEdge(a, c) = " + g.getEdge(a, c));
    IO.println();
}

private static void demonstrateFullGraph(DirectedGraph g, String name) {
    Vertex a = g.insertVertex("a");
    Vertex b = g.insertVertex("b");
    Vertex c = g.insertVertex("c");
    Edge alpha = g.insertEdge(a, b, "α");
    Edge beta = g.insertEdge(b, a, "β");
    Edge gamma = g.insertEdge(b, c, "γ");
    Edge delta = g.insertEdge(c, c, "δ");  // self-loop

    IO.println("--- " + name + " ---");
    IO.println("Vertices : " + g.vertices());
    IO.println("Edges    : " + g.edges());
    IO.println();

    IO.println("Vertex a | outDegree=" + g.outDegree(a) + "  inDegree=" + g.inDegree(a)
            + " | out: " + g.outgoingEdges(a) + "  in: " + g.incomingEdges(a));
    IO.println("Vertex b | outDegree=" + g.outDegree(b) + "  inDegree=" + g.inDegree(b)
            + " | out: " + g.outgoingEdges(b) + "  in: " + g.incomingEdges(b));
    IO.println("Vertex c | outDegree=" + g.outDegree(c) + "  inDegree=" + g.inDegree(c)
            + " | out: " + g.outgoingEdges(c) + "  in: " + g.incomingEdges(c));
    IO.println();

    IO.println("getEdge(a, b) = " + g.getEdge(a, b));
    IO.println("getEdge(b, a) = " + g.getEdge(b, a));
    IO.println("getEdge(a, c) = " + g.getEdge(a, c));
    IO.println("getEdge(c, c) = " + g.getEdge(c, c));
    IO.println();
}
