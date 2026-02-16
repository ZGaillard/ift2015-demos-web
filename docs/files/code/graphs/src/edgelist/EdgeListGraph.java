package edgelist;

import graph.Edge;
import graph.Graph;
import graph.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Undirected graph stored as edge list + vertex list.
 *
 * This matches the textbook edge-list profile: lookups by endpoint are O(m),
 * while insertion and explicit edge removal are O(1) via stored positions.
 *
 * Complexity summary (n = |V|, m = |E|):
 *   getEdge        O(m)
 *   insertEdge     O(1)
 *   removeEdge     O(1)
 *   insertVertex   O(1)
 *   removeVertex   O(m)
 *   incidentEdges  O(m)
 *   Space          O(n + m)
 */
public class EdgeListGraph implements Graph {

    private static class ELVertex extends Vertex {
        int index;

        ELVertex(String label, int index) {
            super(label);
            this.index = index;
        }
    }

    private static class ELEdge extends Edge {
        int index;

        ELEdge(ELVertex origin, ELVertex dest, String label, int index) {
            super(origin, dest, label);
            this.index = index;
        }
    }

    private List<ELVertex> vertices = new ArrayList<>();
    private List<ELEdge> edges = new ArrayList<>();

    // O(1)
    public Vertex insertVertex(String label) {
        ELVertex v = new ELVertex(label, vertices.size());
        vertices.add(v);
        return v;
    }

    // O(1)
    public Edge insertEdge(Vertex u, Vertex v, String label) {
        ELVertex uu = validateVertex(u);
        ELVertex vv = validateVertex(v);
        ELEdge e = new ELEdge(uu, vv, label, edges.size());
        edges.add(e);
        return e;
    }

    // O(m) -- remove all incident edges by scanning edge list
    public void removeVertex(Vertex v) {
        ELVertex vv = validateVertex(v);

        removeVertexAt(vv.index);
        vv.index = -1;

        for (int i = edges.size() - 1; i >= 0; i--) {
            ELEdge e = edges.get(i);
            if (e.origin == vv || e.dest == vv) {
                removeEdgeAt(i);
                e.index = -1;
            }
        }
    }

    // O(1)
    public void removeEdge(Edge e) {
        ELEdge ee = validateEdge(e);
        removeEdgeAt(ee.index);
        ee.index = -1;
    }

    // O(m)
    public Edge getEdge(Vertex u, Vertex v) {
        ELVertex uu = validateVertex(u);
        ELVertex vv = validateVertex(v);
        for (ELEdge e : edges) {
            if ((e.origin == uu && e.dest == vv) || (e.origin == vv && e.dest == uu)) {
                return e;
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

    // O(m)
    public List<Edge> incidentEdges(Vertex v) {
        ELVertex vv = validateVertex(v);
        List<Edge> result = new ArrayList<>();
        for (ELEdge e : edges) {
            if (e.origin == vv || e.dest == vv) {
                result.add(e);
            }
        }
        return result;
    }

    // O(m)
    public int degree(Vertex v) {
        return incidentEdges(v).size();
    }

    private void removeVertexAt(int removeIndex) {
        int lastIndex = vertices.size() - 1;
        ELVertex moved = vertices.get(lastIndex);
        if (removeIndex != lastIndex) {
            vertices.set(removeIndex, moved);
            moved.index = removeIndex;
        }
        vertices.remove(lastIndex);
    }

    private void removeEdgeAt(int removeIndex) {
        int lastIndex = edges.size() - 1;
        ELEdge moved = edges.get(lastIndex);
        if (removeIndex != lastIndex) {
            edges.set(removeIndex, moved);
            moved.index = removeIndex;
        }
        edges.remove(lastIndex);
    }

    private ELVertex validateVertex(Vertex v) {
        if (!(v instanceof ELVertex)) {
            throw new IllegalArgumentException("Vertex not from this graph: " + v);
        }
        ELVertex vv = (ELVertex) v;
        int i = vv.index;
        if (i < 0 || i >= vertices.size() || vertices.get(i) != vv) {
            throw new IllegalArgumentException("Vertex not in graph: " + v);
        }
        return vv;
    }

    private ELEdge validateEdge(Edge e) {
        if (!(e instanceof ELEdge)) {
            throw new IllegalArgumentException("Edge not from this graph: " + e);
        }
        ELEdge ee = (ELEdge) e;
        int i = ee.index;
        if (i < 0 || i >= edges.size() || edges.get(i) != ee) {
            throw new IllegalArgumentException("Edge not in graph: " + e);
        }
        return ee;
    }
}
