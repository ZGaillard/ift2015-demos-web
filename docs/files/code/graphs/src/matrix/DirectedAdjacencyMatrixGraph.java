package matrix;

import graph.DirectedGraph;
import graph.Edge;
import graph.Vertex;

import java.util.ArrayList;
import java.util.List;

/**
 * Directed graph stored as a 2D matrix.
 *
 * matrix[i][j] stores the edge from i to j (or null).
 *
 * Complexity summary (n = |V|, m = |E|):
 *   getEdge        O(1)
 *   insertEdge     O(1)
 *   removeEdge     O(1)
 *   insertVertex   O(n^2)
 *   removeVertex   O(n^2)
 *   outgoingEdges  O(n)
 *   incomingEdges  O(n)
 *   Space          O(n^2)
 */
public class DirectedAdjacencyMatrixGraph implements DirectedGraph {

    private static class MVertex extends Vertex {
        int index;

        MVertex(String label, int index) {
            super(label);
            this.index = index;
        }
    }

    private List<MVertex> vertices = new ArrayList<>();
    private Edge[][] matrix = new Edge[0][0];

    // O(n^2)
    public Vertex insertVertex(String label) {
        MVertex v = new MVertex(label, vertices.size());
        vertices.add(v);

        int n = vertices.size();
        Edge[][] newMatrix = new Edge[n][n];
        for (int i = 0; i < n - 1; i++) {
            for (int j = 0; j < n - 1; j++) {
                newMatrix[i][j] = matrix[i][j];
            }
        }
        matrix = newMatrix;
        return v;
    }

    // O(1)
    public Edge insertEdge(Vertex u, Vertex v, String label) {
        MVertex uu = validateVertex(u);
        MVertex vv = validateVertex(v);
        Edge e = new Edge(uu, vv, label);
        matrix[uu.index][vv.index] = e;
        return e;
    }

    // O(n^2)
    public void removeVertex(Vertex v) {
        MVertex vv = validateVertex(v);
        int removed = vv.index;

        vertices.remove(removed);
        vv.index = -1;
        for (int i = removed; i < vertices.size(); i++) {
            vertices.get(i).index = i;
        }

        int n = vertices.size();
        Edge[][] newMatrix = new Edge[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                int oldI = (i < removed) ? i : i + 1;
                int oldJ = (j < removed) ? j : j + 1;
                newMatrix[i][j] = matrix[oldI][oldJ];
            }
        }
        matrix = newMatrix;
    }

    // O(1)
    public void removeEdge(Edge e) {
        MVertex u = validateVertex(e.origin);
        MVertex v = validateVertex(e.dest);
        matrix[u.index][v.index] = null;
    }

    // O(1)
    public Edge getEdge(Vertex u, Vertex v) {
        MVertex uu = validateVertex(u);
        MVertex vv = validateVertex(v);
        return matrix[uu.index][vv.index];
    }

    // O(n)
    public List<Vertex> vertices() {
        return new ArrayList<>(vertices);
    }

    // O(n^2) scan
    public List<Edge> edges() {
        List<Edge> result = new ArrayList<>();
        int n = vertices.size();
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (matrix[i][j] != null) {
                    result.add(matrix[i][j]);
                }
            }
        }
        return result;
    }

    // O(n)
    public List<Edge> incidentEdges(Vertex v) {
        MVertex vv = validateVertex(v);
        List<Edge> result = new ArrayList<>();
        result.addAll(outgoingEdges(vv));
        result.addAll(incomingEdges(vv));
        return result;
    }

    // O(n)
    public List<Edge> outgoingEdges(Vertex v) {
        MVertex vv = validateVertex(v);
        List<Edge> result = new ArrayList<>();
        int i = vv.index;
        int n = vertices.size();
        for (int j = 0; j < n; j++) {
            if (matrix[i][j] != null) {
                result.add(matrix[i][j]);
            }
        }
        return result;
    }

    // O(n)
    public List<Edge> incomingEdges(Vertex v) {
        MVertex vv = validateVertex(v);
        List<Edge> result = new ArrayList<>();
        int i = vv.index;
        int n = vertices.size();
        for (int j = 0; j < n; j++) {
            if (matrix[j][i] != null) {
                result.add(matrix[j][i]);
            }
        }
        return result;
    }

    // O(n)
    public int outDegree(Vertex v) {
        return outgoingEdges(v).size();
    }

    // O(n)
    public int inDegree(Vertex v) {
        return incomingEdges(v).size();
    }

    // O(n)
    public int degree(Vertex v) {
        return outDegree(v) + inDegree(v);
    }

    private MVertex validateVertex(Vertex v) {
        if (!(v instanceof MVertex)) {
            throw new IllegalArgumentException("Vertex not from this graph: " + v);
        }
        MVertex vv = (MVertex) v;
        int i = vv.index;
        if (i < 0 || i >= vertices.size() || vertices.get(i) != vv) {
            throw new IllegalArgumentException("Vertex not in graph: " + v);
        }
        return vv;
    }
}
