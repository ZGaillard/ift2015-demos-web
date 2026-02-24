package map;

/**
 * A key-value pair stored in a map.
 *
 * This is a simple container analogous to graph.Vertex: two entries are
 * distinguished by object identity (==), not by their key or value.
 */
public class Entry<K, V> {

    public K key;
    public V value;

    public Entry(K key, V value) {
        this.key = key;
        this.value = value;
    }

    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}
