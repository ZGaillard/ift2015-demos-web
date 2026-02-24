package hashtable;

import map.Entry;
import map.Map;

import java.util.List;
import java.util.Random;

/**
 * Base class for hash-table-based maps (Chapter 10.2).
 *
 * Provides the MAD (Multiply-Add-and-Divide) compression function
 * and automatic resizing when the load factor exceeds 0.5.
 *
 * Subclasses implement the bucket-level operations.
 *
 * Complexity summary (expected, with good hash function):
 *   get        O(1)
 *   put        O(1) amortised
 *   remove     O(1)
 */
public abstract class AbstractHashMap<K, V> implements Map<K, V> {

    protected int n = 0;        // number of entries
    protected int capacity;     // length of the bucket array
    private int prime;          // prime for MAD
    private long scale, shift;  // MAD parameters

    public AbstractHashMap(int capacity, int prime) {
        this.capacity = capacity;
        this.prime = prime;
        Random rand = new Random();
        scale = rand.nextInt(prime - 1) + 1;   // scale in [1, prime-1]
        shift = rand.nextInt(prime);            // shift in [0, prime-1]
        createTable();
    }

    public AbstractHashMap(int capacity) {
        this(capacity, 109345121);
    }

    public AbstractHashMap() {
        this(17);
    }

    /** MAD compression: ((|hashCode| * scale + shift) % prime) % capacity */
    protected int hashValue(K key) {
        return (int) ((Math.abs(key.hashCode()) * scale + shift) % prime) % capacity;
    }

    // O(1) expected
    public V get(K key) {
        return bucketGet(hashValue(key), key);
    }

    // O(1) amortised expected
    public V put(K key, V value) {
        V old = bucketPut(hashValue(key), key, value);
        if (n > capacity / 2) {             // load factor > 0.5
            resize(2 * capacity + 1);       // odd, often prime-ish
        }
        return old;
    }

    // O(1) expected
    public V remove(K key) {
        return bucketRemove(hashValue(key), key);
    }

    public int size() {
        return n;
    }

    public boolean isEmpty() {
        return n == 0;
    }

    /** Resize the table and reinsert all entries. */
    private void resize(int newCapacity) {
        List<Entry<K, V>> buffer = entrySet();
        capacity = newCapacity;
        n = 0;
        createTable();
        for (Entry<K, V> e : buffer) {
            put(e.key, e.value);
        }
    }

    // ---- abstract bucket-level operations ----

    protected abstract void createTable();

    protected abstract V bucketGet(int h, K key);

    protected abstract V bucketPut(int h, K key, V value);

    protected abstract V bucketRemove(int h, K key);
}
