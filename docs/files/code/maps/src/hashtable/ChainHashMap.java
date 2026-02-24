package hashtable;

import map.Entry;
import unsorted.UnsortedTableMap;

import java.util.ArrayList;
import java.util.List;

/**
 * Hash map using separate chaining (Chapter 10.2.1).
 *
 * Each bucket is an UnsortedTableMap that stores all entries whose
 * hash value maps to that index. Buckets are created lazily.
 *
 * Complexity summary (expected, with good hash function):
 *   get        O(1)
 *   put        O(1) amortised
 *   remove     O(1)
 *   Space      O(n + N)   where N = capacity
 */
public class ChainHashMap<K, V> extends AbstractHashMap<K, V> {

    @SuppressWarnings("unchecked")
    private UnsortedTableMap<K, V>[] table;

    public ChainHashMap(int capacity, int prime) { super(capacity, prime); }
    public ChainHashMap(int capacity)            { super(capacity); }
    public ChainHashMap()                        { super(); }

    @SuppressWarnings("unchecked")
    protected void createTable() {
        table = new UnsortedTableMap[capacity];
    }

    protected V bucketGet(int h, K key) {
        UnsortedTableMap<K, V> bucket = table[h];
        if (bucket == null) return null;
        return bucket.get(key);
    }

    protected V bucketPut(int h, K key, V value) {
        if (table[h] == null) {
            table[h] = new UnsortedTableMap<>();
        }
        int oldSize = table[h].size();
        V old = table[h].put(key, value);
        n += (table[h].size() - oldSize);   // +1 if new entry, +0 if replaced
        return old;
    }

    protected V bucketRemove(int h, K key) {
        UnsortedTableMap<K, V> bucket = table[h];
        if (bucket == null) return null;
        int oldSize = bucket.size();
        V old = bucket.remove(key);
        n -= (oldSize - bucket.size());     // -1 if removed, -0 if absent
        return old;
    }

    public List<Entry<K, V>> entrySet() {
        List<Entry<K, V>> result = new ArrayList<>();
        for (int i = 0; i < capacity; i++) {
            if (table[i] != null) {
                result.addAll(table[i].entrySet());
            }
        }
        return result;
    }
}
