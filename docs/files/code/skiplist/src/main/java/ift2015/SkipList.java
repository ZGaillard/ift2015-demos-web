package ift2015;

import java.util.Random;

/**
 * Liste à enjambements (Skip List) — implémentation simplifiée (§10.4).
 *
 * <p>Structure en niveaux S_0, S_1, ..., S_h :
 * <ul>
 *   <li>S_0 contient <em>toutes</em> les entrées, encadrées par deux
 *       sentinelles −∞ et +∞.</li>
 *   <li>Chaque entrée de S_i est copiée dans S_{i+1} avec probabilité 1/2
 *       (« lancer de pièce »).</li>
 *   <li>Les copies d'une même entrée forment une <em>tour</em> verticale
 *       reliée par les liens {@code above} / {@code below}.</li>
 * </ul>
 *
 * <p>Complexités attendues (probabilistes) :
 * <pre>
 *   get, put, remove  →  O(log n)
 *   espace            →  O(n)
 * </pre>
 *
 * <p><b>Travail demandé :</b> compléter les méthodes marquées {@code TODO} :
 * {@link #skipSearch}, {@link #get}, {@link #put}, {@link #remove}.
 *
 * @param <K> type des clés — doit être {@link Comparable}
 * @param <V> type des valeurs
 */
public class SkipList<K extends Comparable<K>, V> {

    // =========================================================================
    // Nœud interne
    // =========================================================================

    /**
     * Nœud de la skip list.
     *
     * <p>Chaque nœud possède quatre liens :
     * <pre>
     *          above
     *            │
     *   prev ← node → next
     *            │
     *          below
     * </pre>
     *
     * Les nœuds sentinelles (−∞ et +∞) utilisent les drapeaux
     * {@code isNegInf} / {@code isPosInf} à la place d'une clé réelle.
     */
    class Node {

        K    key;
        V    value;
        Node above, below, prev, next;

        final boolean isNegInf;   // true pour la sentinelle gauche (−∞)
        final boolean isPosInf;   // true pour la sentinelle droite (+∞)

        /** Nœud ordinaire portant une entrée (k, v). */
        Node(K key, V value) {
            this.key      = key;
            this.value    = value;
            this.isNegInf = false;
            this.isPosInf = false;
        }

        /** Nœud sentinelle. Passer (true, false) pour −∞, (false, true) pour +∞. */
        Node(boolean isNegInf, boolean isPosInf) {
            this.key      = null;
            this.value    = null;
            this.isNegInf = isNegInf;
            this.isPosInf = isPosInf;
        }

        @Override
        public String toString() {
            if (isNegInf) return "-∞";
            if (isPosInf) return "+∞";
            return String.valueOf(key);
        }
    }

    // =========================================================================
    // Champs
    // =========================================================================

    /** Sentinelle −∞ du niveau le plus haut (point de départ de toute recherche). */
    private Node head;

    /** Sentinelle +∞ du niveau le plus haut. */
    private Node tail;

    /** Nombre d'entrées (hors sentinelles). */
    private int size;

    /**
     * Nombre total de niveaux.
     * Après construction : {@code height == 1} (seul S_0 existe).
     * Chaque nouveau niveau ajouté incrémente ce compteur.
     */
    private int height;

    private final Random rng;

    // =========================================================================
    // Constructeur
    // =========================================================================

    /**
     * Construit une skip list vide avec un unique niveau S_0.
     *
     * <p>État initial :
     * <pre>
     *   S_0 :  −∞ ←→ +∞
     * </pre>
     */
    public SkipList() {
        head   = new Node(true,  false);   // −∞
        tail   = new Node(false, true);    // +∞
        head.next = tail;
        tail.prev = head;
        size   = 0;
        height = 1;
        rng    = new Random();
    }

    // =========================================================================
    // Méthodes fournies — ne pas modifier
    // =========================================================================

    /** Retourne le nombre d'entrées. */
    public int size() { return size; }

    /** Retourne {@code true} si la liste ne contient aucune entrée. */
    public boolean isEmpty() { return size == 0; }

    /**
     * Retourne l'entrée de clé minimale, ou {@code null} si la liste est vide.
     *
     * <p>Descend jusqu'à S_0 et retourne le premier nœud non-sentinelle.
     */
    public Entry<K, V> firstEntry() {
        Node p = head;
        while (p.below != null) p = p.below;   // descendre à S_0
        Node first = p.next;
        return first.isPosInf ? null : new Entry<>(first.key, first.value);
    }

    /**
     * Retourne l'entrée de clé maximale, ou {@code null} si la liste est vide.
     */
    public Entry<K, V> lastEntry() {
        Node p = tail;
        while (p.below != null) p = p.below;   // descendre à S_0
        Node last = p.prev;
        return last.isNegInf ? null : new Entry<>(last.key, last.value);
    }

    /**
     * Compare la clé d'un nœud avec une clé donnée, en gérant les sentinelles.
     *
     * @return valeur négative si {@code n < key}, 0 si égaux, positive si {@code n > key}
     */
    private int compareToKey(Node n, K key) {
        if (n.isNegInf) return -1;
        if (n.isPosInf) return  1;
        return n.key.compareTo(key);
    }

    /**
     * Insère un nouveau nœud de clé {@code k} et valeur {@code v} :
     * <ul>
     *   <li>horizontalement : <em>après</em> {@code p} (même niveau) ;</li>
     *   <li>verticalement   : <em>au-dessus</em> de {@code q}
     *       ({@code q.above = nouveau}, {@code nouveau.below = q}).</li>
     * </ul>
     *
     * <p>Cette méthode gère toute la plomberie des pointeurs — vous n'avez
     * qu'à l'appeler avec les bons arguments.
     *
     * @param p  nœud après lequel insérer (jamais {@code null})
     * @param q  nœud juste en dessous du nouveau nœud, ou {@code null} si S_0
     * @param k  clé
     * @param v  valeur
     * @return   le nouveau nœud créé
     */
    private Node insertAfterAbove(Node p, Node q, K k, V v) {
        Node n   = new Node(k, v);
        // Liens horizontaux
        n.prev   = p;
        n.next   = p.next;
        p.next.prev = n;
        p.next   = n;
        // Liens verticaux
        n.below  = q;
        if (q != null) q.above = n;
        return n;
    }

    /**
     * Retourne {@code true} avec probabilité 1/2 (face) ou {@code false} (pile).
     *
     * <p>Utilisé dans {@link #put} pour décider si la tour monte d'un niveau.
     */
    private boolean coinFlip() { return rng.nextBoolean(); }

    /**
     * Représentation textuelle de la skip list, du niveau le plus haut vers S_0.
     *
     * <p>Exemple pour une liste contenant 12, 17, 20 :
     * <pre>
     * S2: -∞ — +∞
     * S1: -∞ — 17 — +∞
     * S0: -∞ — 12 — 17 — 20 — +∞
     * </pre>
     */
    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        Node levelHead = head;
        int  lvl       = height - 1;
        while (levelHead != null) {
            sb.append("S").append(lvl).append(": -∞");
            Node curr = levelHead.next;
            while (!curr.isPosInf) {
                sb.append(" — ").append(curr.key);
                curr = curr.next;
            }
            sb.append(" — +∞\n");
            levelHead = levelHead.below;
            lvl--;
        }
        return sb.toString();
    }

    // =========================================================================
    // À IMPLÉMENTER
    // =========================================================================

    /**
     * Algorithme de recherche de base — cœur de la skip list (§10.4, SkipSearch).
     *
     * <p>Retourne le nœud {@code p} dans S_0 tel que :
     * <pre>
     *   key(p) ≤ k  et  key(p.next) > k
     * </pre>
     * Si {@code k} est inférieur à toutes les clés, retourne la sentinelle −∞ de S_0.
     *
     * <p><b>Pseudocode :</b>
     * <pre>
     *   p = head                              // coin supérieur gauche
     *   tant que p.below != null :
     *       p = p.below                       // descendre d'un niveau
     *       tant que compareToKey(p.next, k) <= 0 :
     *           p = p.next                    // avancer vers la droite
     *   retourner p                           // p est dans S_0
     * </pre>
     *
     * <p><b>Invariant :</b> à la fin de chaque passage dans la boucle externe,
     * {@code p} est le nœud le plus à droite du niveau courant dont la clé est ≤ k.
     *
     * @param k clé recherchée
     * @return  nœud p dans S_0 avec key(p) ≤ k, ou la sentinelle −∞ si k est minimal
     */
    private Node skipSearch(K k) {
        // TODO
        throw new UnsupportedOperationException("À implémenter : skipSearch");
    }

    /**
     * Retourne la valeur associée à la clé {@code k}, ou {@code null} si absente.
     *
     * <p><b>Indice :</b> appelez {@link #skipSearch(Comparable)}, puis vérifiez si
     * la clé du nœud retourné correspond exactement à {@code k}.
     *
     * @param k clé à rechercher
     * @return  valeur associée, ou {@code null}
     */
    public V get(K k) {
        // TODO
        throw new UnsupportedOperationException("À implémenter : get");
    }

    /**
     * Associe la valeur {@code v} à la clé {@code k}.
     *
     * <ul>
     *   <li>Si {@code k} est déjà présente : remplace la valeur à tous les niveaux
     *       de la tour et retourne l'ancienne valeur.</li>
     *   <li>Si {@code k} est absente : insère la nouvelle entrée, construit la tour
     *       par lancers de pièce successifs, et retourne {@code null}.</li>
     * </ul>
     *
     * <p><b>Pseudocode — cas clé absente :</b>
     * <pre>
     *   p = skipSearch(k)
     *   q = insertAfterAbove(p, null, k, v)    // insérer à S_0, q pointe sur ce nœud
     *
     *   level = 1
     *   tant que coinFlip() == vrai :           // face → monter d'un niveau
     *
     *       si level >= height :                // S_level n'existe pas encore
     *           newHead = nouveau nœud sentinelle −∞
     *           newTail = nouveau nœud sentinelle +∞
     *           newHead.next = newTail  ;  newTail.prev = newHead
     *           newHead.below = head    ;  head.above   = newHead
     *           newTail.below = tail    ;  tail.above   = newTail
     *           head = newHead  ;  tail = newTail  ;  height++
     *
     *       // Trouver le prédécesseur au niveau `level`
     *       // Reculer depuis p jusqu'à trouver un nœud ayant un lien vers le haut,
     *       // puis monter via ce lien.
     *       tant que p.above == null : p = p.prev
     *       p = p.above
     *
     *       q = insertAfterAbove(p, q, k, v)    // insérer au-dessus de q
     *       level++
     *
     *   size++
     *   retourner null
     * </pre>
     *
     * @param k clé
     * @param v valeur
     * @return  ancienne valeur si la clé existait, {@code null} sinon
     */
    public V put(K k, V v) {
        // TODO
        throw new UnsupportedOperationException("À implémenter : put");
    }

    /**
     * Supprime l'entrée de clé {@code k} et retourne sa valeur.
     * Retourne {@code null} si {@code k} est absente.
     *
     * <p><b>Pseudocode :</b>
     * <pre>
     *   p = skipSearch(k)
     *   si compareToKey(p, k) != 0 : retourner null   // clé absente
     *
     *   old = p.value
     *   curr = p
     *   tant que curr != null :            // parcourir la tour vers le haut
     *       curr.prev.next = curr.next      // délier curr horizontalement
     *       curr.next.prev = curr.prev
     *       curr = curr.above
     *
     *   // Supprimer les niveaux supérieurs devenus vides (optionnel mais propre)
     *   tant que height > 1 et head.next.isPosInf :
     *       head = head.below  ;  head.above = null
     *       tail = tail.below  ;  tail.above = null
     *       height--
     *
     *   size--
     *   retourner old
     * </pre>
     *
     * @param k clé à supprimer
     * @return  valeur supprimée, ou {@code null}
     */
    public V remove(K k) {
        // TODO
        throw new UnsupportedOperationException("À implémenter : remove");
    }
}
