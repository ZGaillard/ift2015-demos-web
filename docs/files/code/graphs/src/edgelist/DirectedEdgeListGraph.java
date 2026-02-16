package edgelist;

import graph.DirectedGraph;
import graph.Edge;
import graph.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Directed graph stored as edge list + vertex list.
 *
 * Complexity summary (n = |V|, m = |E|):
 *   getEdge        O(m)
 *   insertEdge     O(1)
 *   removeEdge     O(1)
 *   insertVertex   O(1)
 *   removeVertex   O(m)
 *   outgoingEdges  O(m)
 *   incomingEdges  O(m)
 *   Space          O(n + m)
 */
public class DirectedEdgeListGraph implements DirectedGraph {

    private static class DELVertex extends Vertex {
        int index;

        DELVertex(String label, int index) {
            super(label);
            this.index = index;
        }
    }

    private static class DELEdge extends Edge {
        int index;

        DELEdge(DELVertex origin, DELVertex dest, String label, int index) {
            super(origin, dest, label);
            this.index = index;
        }
    }

    private List<DELVertex> vertices = new ArrayList<>();
    private List<DELEdge> edges = new ArrayList<>();

    // O(1)
    public Vertex insertVertex(String label) {
        DELVertex v = new DELVertex(label, vertices.size());
        vertices.add(v);
        return v;
    }

    // O(1)
    public Edge insertEdge(Vertex u, Vertex v, String label) {
        DELVertex uu = validateVertex(u);
        DELVertex vv = validateVertex(v);
        DELEdge e = new DELEdge(uu, vv, label, edges.size());
        edges.add(e);
        return e;
    }

    // O(m)
    public void removeVertex(Vertex v) {
        DELVertex vv = validateVertex(v);

        removeVertexAt(vv.index);
        vv.index = -1;

        for (int i = edges.size() - 1; i >= 0; i--) {
            DELEdge e = edges.get(i);
            if (e.origin == vv || e.dest == vv) {
                removeEdgeAt(i);
                e.index = -1;
            }
        }
    }

    // O(1)
    public void removeEdge(Edge e) {
        DELEdge ee = validateEdge(e);
        removeEdgeAt(ee.index);
        ee.index = -1;
    }

    // O(m) -- directed: match only origin==u and dest==v
    public Edge getEdge(Vertex u, Vertex v) {
        DELVertex uu = validateVertex(u);
        DELVertex vv = validateVertex(v);
        for (DELEdge e : edges) {
            if (e.origin == uu && e.dest == vv) {
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
        DELVertex vv = validateVertex(v);
        List<Edge> result = new ArrayList<>();
        for (DELEdge e : edges) {
            if (e.origin == vv || e.dest == vv) {
                result.add(e);
            }
        }
        return result;
    }

    // O(m)
    public List<Edge> outgoingEdges(Vertex v) {
        DELVertex vv = validateVertex(v);
        List<Edge> result = new ArrayList<>();
        for (DELEdge e : edges) {
            if (e.origin == vv) {
                result.add(e);
            }
        }
        return result;
    }

    // O(m)
    public List<Edge> incomingEdges(Vertex v) {
        DELVertex vv = validateVertex(v);
        List<Edge> result = new ArrayList<>();
        for (DELEdge e : edges) {
            if (e.dest == vv) {
                result.add(e);
            }
        }
        return result;
    }

    // O(m)
    public int outDegree(Vertex v) {
        return outgoingEdges(v).size();
    }

    // O(m)
    public int inDegree(Vertex v) {
        return incomingEdges(v).size();
    }

    // O(m)
    public int degree(Vertex v) {
        return outDegree(v) + inDegree(v);
    }

    private void removeVertexAt(int removeIndex) {
        int lastIndex = vertices.size() - 1;
        DELVertex moved = vertices.get(lastIndex);
        if (removeIndex != lastIndex) {
            vertices.set(removeIndex, moved);
            moved.index = removeIndex;
        }
        vertices.remove(lastIndex);
    }

    private void removeEdgeAt(int removeIndex) {
        int lastIndex = edges.size() - 1;
        DELEdge moved = edges.get(lastIndex);
        if (removeIndex != lastIndex) {
            edges.set(removeIndex, moved);
            moved.index = removeIndex;
        }
        edges.remove(lastIndex);
    }

    private DELVertex validateVertex(Vertex v) {
        if (!(v instanceof DELVertex)) {
            throw new IllegalArgumentException("Vertex not from this graph: " + v);
        }
        DELVertex vv = (DELVertex) v;
        int i = vv.index;
        if (i < 0 || i >= vertices.size() || vertices.get(i) != vv) {
            throw new IllegalArgumentException("Vertex not in graph: " + v);
        }
        return vv;
    }

    private DELEdge validateEdge(Edge e) {
        if (!(e instanceof DELEdge)) {
            throw new IllegalArgumentException("Edge not from this graph: " + e);
        }
        DELEdge ee = (DELEdge) e;
        int i = ee.index;
        if (i < 0 || i >= edges.size() || edges.get(i) != ee) {
            throw new IllegalArgumentException("Edge not in graph: " + e);
        }
        return ee;
    }
}
