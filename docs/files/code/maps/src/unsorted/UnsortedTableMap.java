package unsorted;

import map.Entry;
import map.Map;

import java.util.ArrayList;
import java.util.List;

/**
 * Map implemented as an unsorted ArrayList of entries.
 *
 * The simplest possible map: every operation scans the whole list.
 * Remove uses the swap-with-last trick (order does not matter).
 *
 * Complexity summary (n = number of entries):
 *   get        O(n)
 *   put        O(n)
 *   remove     O(n)
 *   size       O(1)
 *   entrySet   O(n)
 */
public class UnsortedTableMap<K, V> implements Map<K, V> {

    private List<Entry<K, V>> table = new ArrayList<>();

    /** Return the index of the entry with key k, or -1 if not found. */
    private int findIndex(K key) {
        for (int i = 0; i < table.size(); i++) {
            if (table.get(i).key.equals(key)) {
                return i;
            }
        }
        return -1;
    }

    // O(n)
    public V get(K key) {
        int i = findIndex(key);
        if (i == -1) return null;
        return table.get(i).value;
    }

    // O(n) — must check if key already exists
    public V put(K key, V value) {
        int i = findIndex(key);
        if (i != -1) {
            V old = table.get(i).value;
            table.get(i).value = value;
            return old;
        }
        table.add(new Entry<>(key, value));
        return null;
    }

    // O(n) — swap with last to avoid shifting
    public V remove(K key) {
        int i = findIndex(key);
        if (i == -1) return null;
        V old = table.get(i).value;
        int last = table.size() - 1;
        if (i != last) {
            table.set(i, table.get(last));  // swap with last
        }
        table.remove(last);
        return old;
    }

    public int size() {
        return table.size();
    }

    public boolean isEmpty() {
        return table.isEmpty();
    }

    public List<Entry<K, V>> entrySet() {
        return new ArrayList<>(table);
    }
}
