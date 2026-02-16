package adjlist;

import graph.DirectedGraph;
import graph.Edge;
import graph.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Directed graph stored as adjacency lists.
 *
 * Each vertex stores two incidence lists:
 *   - outgoing edges
 *   - incoming edges
 *
 * Each edge stores O(1) location metadata in those two lists, so removeEdge
 * is O(1), matching the textbook adjacency-list bounds.
 *
 * Complexity summary (n = |V|, m = |E|):
 *   getEdge        O(min(outdeg(u), indeg(v)))
 *   insertEdge     O(1)
 *   removeEdge     O(1)
 *   insertVertex   O(1)
 *   removeVertex   O(deg(v))
 *   outgoingEdges  O(outdeg(v))
 *   incomingEdges  O(indeg(v))
 *   outDegree      O(1)
 *   inDegree       O(1)
 *   Space          O(n + m)
 */
public class DirectedAdjacencyListGraph implements DirectedGraph {

    private static class DLVertex extends Vertex {
        int index;
        List<DLEdge> outgoing = new ArrayList<>();
        List<DLEdge> incoming = new ArrayList<>();

        DLVertex(String label, int index) {
            super(label);
            this.index = index;
        }
    }

    private static class DLEdge extends Edge {
        int index;
        int outPos;
        int inPos;

        DLEdge(DLVertex origin, DLVertex dest, String label, int index) {
            super(origin, dest, label);
            this.index = index;
        }
    }

    private List<DLVertex> vertices = new ArrayList<>();
    private List<DLEdge> edges = new ArrayList<>();

    // O(1)
    public Vertex insertVertex(String label) {
        DLVertex v = new DLVertex(label, vertices.size());
        vertices.add(v);
        return v;
    }

    // O(1)
    public Edge insertEdge(Vertex u, Vertex v, String label) {
        DLVertex uu = validateVertex(u);
        DLVertex vv = validateVertex(v);

        DLEdge e = new DLEdge(uu, vv, label, edges.size());
        e.outPos = uu.outgoing.size();
        uu.outgoing.add(e);
        e.inPos = vv.incoming.size();
        vv.incoming.add(e);
        edges.add(e);
        return e;
    }

    // O(deg(v))
    public void removeVertex(Vertex v) {
        DLVertex vv = validateVertex(v);

        while (!vv.outgoing.isEmpty()) {
            removeEdge(vv.outgoing.get(vv.outgoing.size() - 1));
        }
        while (!vv.incoming.isEmpty()) {
            removeEdge(vv.incoming.get(vv.incoming.size() - 1));
        }

        removeVertexAt(vv.index);
        vv.index = -1;
    }

    // O(1)
    public void removeEdge(Edge e) {
        DLEdge ee = validateEdge(e);
        DLVertex u = (DLVertex) ee.origin;
        DLVertex v = (DLVertex) ee.dest;

        removeOutgoingAt(u, ee.outPos);
        removeIncomingAt(v, ee.inPos);
        removeEdgeAt(ee.index);
        ee.index = -1;
    }

    // O(min(outdeg(u), indeg(v)))
    public Edge getEdge(Vertex u, Vertex v) {
        DLVertex uu = validateVertex(u);
        DLVertex vv = validateVertex(v);

        if (uu.outgoing.size() <= vv.incoming.size()) {
            for (DLEdge e : uu.outgoing) {
                if (e.dest == vv) {
                    return e;
                }
            }
        } else {
            for (DLEdge e : vv.incoming) {
                if (e.origin == uu) {
                    return e;
                }
            }
        }
        return null;
    }

    // O(n)
    public List<Vertex> vertices() {
        return new ArrayList<>(vertices);
    }

    // O(m)
    public List<Edge> edges() {
        return new ArrayList<>(edges);
    }

    // O(deg(v))
    public List<Edge> incidentEdges(Vertex v) {
        DLVertex vv = validateVertex(v);
        List<Edge> result = new ArrayList<>(vv.outgoing.size() + vv.incoming.size());
        result.addAll(vv.outgoing);
        for (DLEdge e : vv.incoming) {
            // Skip self-loops here to avoid reporting the same edge twice.
            if (e.origin != vv) {
                result.add(e);
            }
        }
        return result;
    }

    // O(outdeg(v))
    public List<Edge> outgoingEdges(Vertex v) {
        return new ArrayList<>(validateVertex(v).outgoing);
    }

    // O(indeg(v))
    public List<Edge> incomingEdges(Vertex v) {
        return new ArrayList<>(validateVertex(v).incoming);
    }

    // O(1)
    public int outDegree(Vertex v) {
        return validateVertex(v).outgoing.size();
    }

    // O(1)
    public int inDegree(Vertex v) {
        return validateVertex(v).incoming.size();
    }

    // O(1)
    public int degree(Vertex v) {
        DLVertex vv = validateVertex(v);
        return vv.outgoing.size() + vv.incoming.size();
    }

    private void removeVertexAt(int removeIndex) {
        int lastIndex = vertices.size() - 1;
        DLVertex moved = vertices.get(lastIndex);
        if (removeIndex != lastIndex) {
            vertices.set(removeIndex, moved);
            moved.index = removeIndex;
        }
        vertices.remove(lastIndex);
    }

    private void removeEdgeAt(int removeIndex) {
        int lastIndex = edges.size() - 1;
        DLEdge moved = edges.get(lastIndex);
        if (removeIndex != lastIndex) {
            edges.set(removeIndex, moved);
            moved.index = removeIndex;
        }
        edges.remove(lastIndex);
    }

    private void removeOutgoingAt(DLVertex vertex, int removePos) {
        int lastPos = vertex.outgoing.size() - 1;
        DLEdge moved = vertex.outgoing.get(lastPos);
        if (removePos != lastPos) {
            vertex.outgoing.set(removePos, moved);
            moved.outPos = removePos;
        }
        vertex.outgoing.remove(lastPos);
    }

    private void removeIncomingAt(DLVertex vertex, int removePos) {
        int lastPos = vertex.incoming.size() - 1;
        DLEdge moved = vertex.incoming.get(lastPos);
        if (removePos != lastPos) {
            vertex.incoming.set(removePos, moved);
            moved.inPos = removePos;
        }
        vertex.incoming.remove(lastPos);
    }

    private DLVertex validateVertex(Vertex v) {
        if (!(v instanceof DLVertex)) {
            throw new IllegalArgumentException("Vertex not from this graph: " + v);
        }
        DLVertex vv = (DLVertex) v;
        int i = vv.index;
        if (i < 0 || i >= vertices.size() || vertices.get(i) != vv) {
            throw new IllegalArgumentException("Vertex not in graph: " + v);
        }
        return vv;
    }

    private DLEdge validateEdge(Edge e) {
        if (!(e instanceof DLEdge)) {
            throw new IllegalArgumentException("Edge not from this graph: " + e);
        }
        DLEdge ee = (DLEdge) e;
        int i = ee.index;
        if (i < 0 || i >= edges.size() || edges.get(i) != ee) {
            throw new IllegalArgumentException("Edge not in graph: " + e);
        }
        return ee;
    }
}
