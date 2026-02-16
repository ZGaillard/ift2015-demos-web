package adjlist;

import graph.Edge;
import graph.Graph;
import graph.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Undirected graph stored as an adjacency list.
 *
 * This version follows the textbook adjacency-list model (Chapter 14.2):
 * each vertex has direct O(1) access to its incidence list, and each edge
 * stores O(1) location metadata so removeEdge runs in O(1).
 *
 * Complexity summary (n = |V|, m = |E|):
 *   getEdge        O(min(deg(u), deg(v)))
 *   insertEdge     O(1)
 *   removeEdge     O(1)
 *   insertVertex   O(1)
 *   removeVertex   O(deg(v))
 *   incidentEdges  O(deg(v))
 *   degree         O(1)
 *   Space          O(n + m)
 */
public class AdjacencyListGraph implements Graph {

    private static class ALVertex extends Vertex {
        int index;
        List<ALEdge> incident = new ArrayList<>();

        ALVertex(String label, int index) {
            super(label);
            this.index = index;
        }
    }

    private static class ALEdge extends Edge {
        int index;
        int originPos;
        int destPos;

        ALEdge(ALVertex origin, ALVertex dest, String label, int index) {
            super(origin, dest, label);
            this.index = index;
        }
    }

    private List<ALVertex> vertices = new ArrayList<>();
    private List<ALEdge> edges = new ArrayList<>();

    // O(1)
    public Vertex insertVertex(String label) {
        ALVertex v = new ALVertex(label, vertices.size());
        vertices.add(v);
        return v;
    }

    // O(1)
    public Edge insertEdge(Vertex u, Vertex v, String label) {
        ALVertex uu = validateVertex(u);
        ALVertex vv = validateVertex(v);

        ALEdge e = new ALEdge(uu, vv, label, edges.size());

        e.originPos = uu.incident.size();
        uu.incident.add(e);

        e.destPos = vv.incident.size();
        vv.incident.add(e);

        edges.add(e);
        return e;
    }

    // O(deg(v)) -- repeatedly remove incident edges in O(1) each
    public void removeVertex(Vertex v) {
        ALVertex vv = validateVertex(v);

        while (!vv.incident.isEmpty()) {
            removeEdge(vv.incident.get(vv.incident.size() - 1));
        }

        removeVertexAt(vv.index);
        vv.index = -1;
    }

    // O(1)
    public void removeEdge(Edge e) {
        ALEdge ee = validateEdge(e);
        ALVertex u = (ALVertex) ee.origin;
        ALVertex v = (ALVertex) ee.dest;

        if (u == v) {
            // Self-loop appears twice in the same incidence list.
            if (ee.originPos > ee.destPos) {
                removeIncidentAt(u, ee.originPos);
                removeIncidentAt(v, ee.destPos);
            } else {
                removeIncidentAt(v, ee.destPos);
                removeIncidentAt(u, ee.originPos);
            }
        } else {
            removeIncidentAt(u, ee.originPos);
            removeIncidentAt(v, ee.destPos);
        }

        removeEdgeAt(ee.index);
        ee.index = -1;
    }

    // O(min(deg(u), deg(v)))
    public Edge getEdge(Vertex u, Vertex v) {
        ALVertex uu = validateVertex(u);
        ALVertex vv = validateVertex(v);

        List<ALEdge> probe = (uu.incident.size() <= vv.incident.size()) ? uu.incident : vv.incident;
        for (ALEdge e : probe) {
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

    // O(deg(v))
    public List<Edge> incidentEdges(Vertex v) {
        ALVertex vv = validateVertex(v);
        return new ArrayList<>(vv.incident);
    }

    // O(1)
    public int degree(Vertex v) {
        return validateVertex(v).incident.size();
    }

    private void removeVertexAt(int removeIndex) {
        int lastIndex = vertices.size() - 1;
        ALVertex moved = vertices.get(lastIndex);
        if (removeIndex != lastIndex) {
            vertices.set(removeIndex, moved);
            moved.index = removeIndex;
        }
        vertices.remove(lastIndex);
    }

    private void removeEdgeAt(int removeIndex) {
        int lastIndex = edges.size() - 1;
        ALEdge moved = edges.get(lastIndex);
        if (removeIndex != lastIndex) {
            edges.set(removeIndex, moved);
            moved.index = removeIndex;
        }
        edges.remove(lastIndex);
    }

    private void removeIncidentAt(ALVertex vertex, int removePos) {
        int lastPos = vertex.incident.size() - 1;
        ALEdge moved = vertex.incident.get(lastPos);

        if (removePos != lastPos) {
            vertex.incident.set(removePos, moved);

            if (moved.origin == vertex && moved.originPos == lastPos) {
                moved.originPos = removePos;
            }
            if (moved.dest == vertex && moved.destPos == lastPos) {
                moved.destPos = removePos;
            }
        }
        vertex.incident.remove(lastPos);
    }

    private ALVertex validateVertex(Vertex v) {
        if (!(v instanceof ALVertex)) {
            throw new IllegalArgumentException("Vertex not from this graph: " + v);
        }
        ALVertex vv = (ALVertex) v;
        int i = vv.index;
        if (i < 0 || i >= vertices.size() || vertices.get(i) != vv) {
            throw new IllegalArgumentException("Vertex not in graph: " + v);
        }
        return vv;
    }

    private ALEdge validateEdge(Edge e) {
        if (!(e instanceof ALEdge)) {
            throw new IllegalArgumentException("Edge not from this graph: " + e);
        }
        ALEdge ee = (ALEdge) e;
        int i = ee.index;
        if (i < 0 || i >= edges.size() || edges.get(i) != ee) {
            throw new IllegalArgumentException("Edge not in graph: " + e);
        }
        return ee;
    }
}
