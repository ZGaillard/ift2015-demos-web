package hashtable;

import map.Entry;

import java.util.ArrayList;
import java.util.List;

/**
 * Hash map using linear probing with a DEFUNCT sentinel (Chapter 10.2.2).
 *
 * Entries are stored directly in a flat array. On collision, we probe
 * linearly: (h+1), (h+2), ... wrapping around. Deleted slots are marked
 * DEFUNCT so that probe chains are not broken.
 *
 * Complexity summary (expected, with good hash function and low load):
 *   get        O(1)
 *   put        O(1) amortised
 *   remove     O(1)
 *   Space      O(N)   where N = capacity
 */
public class ProbeHashMap<K, V> extends AbstractHashMap<K, V> {

    /** Sentinel marking a formerly-occupied slot. */
    private static final Entry<?, ?> DEFUNCT = new Entry<>(null, null);

    @SuppressWarnings("unchecked")
    private Entry<K, V>[] table;

    public ProbeHashMap(int capacity, int prime) { super(capacity, prime); }
    public ProbeHashMap(int capacity)            { super(capacity); }
    public ProbeHashMap()                        { super(); }

    @SuppressWarnings("unchecked")
    protected void createTable() {
        table = new Entry[capacity];
    }

    /** True if slot j is empty or DEFUNCT (available for insertion). */
    private boolean isAvailable(int j) {
        return table[j] == null || table[j] == DEFUNCT;
    }

    /**
     * Search for key k starting from hash index h using linear probing.
     *
     * Returns:
     *   positive index  — key found at that index
     *   negative value  — key not found; -(availableSlot + 1) encodes
     *                     the first slot where a new entry could go
     */
    private int findSlot(int h, K key) {
        int avail = -1;                    // first DEFUNCT slot seen
        int j = h;
        do {
            if (table[j] == null) {
                // end of probe chain
                return -(avail != -1 ? avail : j) - 1;
            } else if (table[j] == DEFUNCT) {
                if (avail == -1) avail = j;
            } else if (table[j].key.equals(key)) {
                return j;                  // found
            }
            j = (j + 1) % capacity;
        } while (j != h);
        return -(avail != -1 ? avail : j) - 1;
    }

    protected V bucketGet(int h, K key) {
        int j = findSlot(h, key);
        if (j < 0) return null;
        return table[j].value;
    }

    protected V bucketPut(int h, K key, V value) {
        int j = findSlot(h, key);
        if (j >= 0) {
            V old = table[j].value;
            table[j].value = value;
            return old;
        }
        // key not found — insert at the available slot
        int slot = -(j + 1);
        table[slot] = new Entry<>(key, value);
        n++;
        return null;
    }

    @SuppressWarnings("unchecked")
    protected V bucketRemove(int h, K key) {
        int j = findSlot(h, key);
        if (j < 0) return null;
        V old = table[j].value;
        table[j] = (Entry<K, V>) DEFUNCT;  // mark as defunct
        n--;
        return old;
    }

    public List<Entry<K, V>> entrySet() {
        List<Entry<K, V>> result = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            if (!isAvailable(i)) {
                result.add(table[i]);
            }
        }
        return result;
    }
}
