package map;

import java.util.List;

/**
 * Interface for the Map ADT (Chapter 10.1).
 *
 * A map stores key-value entries where each key is unique.
 * Three concrete implementations are provided:
 *   - UnsortedTableMap   (ArrayList, O(n) everything)
 *   - ChainHashMap       (separate chaining, O(1) expected)
 *   - ProbeHashMap       (linear probing, O(1) expected)
 */
public interface Map<K, V> {

    /** Return the value associated with key k, or null if absent. */
    V get(K key);

    /**
     * Insert entry (key, value). If key already exists, replace its value.
     * Return the old value, or null if the key was new.
     */
    V put(K key, V value);

    /**
     * Remove the entry with key k and return its value.
     * Return null if the key was absent.
     */
    V remove(K key);

    /** Return the number of entries in the map. */
    int size();

    /** Return true if the map contains no entries. */
    boolean isEmpty();

    /** Return a list of all entries in the map. */
    List<Entry<K, V>> entrySet();
}
