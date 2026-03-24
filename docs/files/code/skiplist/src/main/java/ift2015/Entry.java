package ift2015;

/**
 * Paire clé-valeur retournée par les opérations de la SkipList.
 *
 * @param <K> type de la clé
 * @param <V> type de la valeur
 */
public class Entry<K, V> {

    public final K key;
    public final V value;

    public Entry(K key, V value) {
        this.key   = key;
        this.value = value;
    }

    @Override
    public String toString() {
        return "(" + key + ", " + value + ")";
    }
}
