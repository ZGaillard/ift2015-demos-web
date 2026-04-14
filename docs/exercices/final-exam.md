# Examen Final Pratique — IFT2015

Cet examen couvre la **deuxième moitié du cours** (démonstrations 6 à 10).
Il est conçu pour **2 heures** et se divise en 4 sections.

!!! abstract "Consignes"

    - Chaque section comporte une **Partie A** (questions courtes) et une **Partie B** (questions longues).
    - Les réponses sont masquées — tentez chaque question avant de les révéler.
    - Durées suggérées indiquées à titre indicatif.

---

## Section 1 — Maps, Tables de Hachage & Représentation de graphes

> **Durée suggérée : 25 min**

---

### Partie A — Questions courtes

??? question "Q1 — Ambiguïté de `get(k)`"

    Si `map.get(k)` retourne `null`, cela signifie nécessairement que la clé `k` est **absente** de la map.

    ??? success "Réponse"

        **Faux.** `get(k) == null` peut indiquer deux situations distinctes : soit la clé `k` est absente, soit la clé est présente mais associée à la valeur `null`. Pour lever l'ambiguïté, il faut utiliser `containsKey(k)` — une opération séparée qui retourne `true` si et seulement si la clé existe, quelle que soit sa valeur associée.

??? question "Q2 — Cuckoo hashing et $O(1)$ dans le pire cas"

    Cuckoo hashing garantit un coût $O(1)$ dans le pire cas aussi bien pour `get(k)` que pour `put(k, v)`.

    ??? success "Réponse"

        **Faux.** Cuckoo hashing garantit $O(1)$ dans le **pire cas** uniquement pour `get` et `remove` (chaque clé peut se trouver dans au plus 2 positions, donc on effectue au plus 2 sondes). En revanche, `put` peut déclencher une longue chaîne de cuckoo evictions qui, dans le pire cas, atteint $O(n)$ — voire oblige un rehashing complet si un cycle se forme. La complexité de `put` est seulement $O(1)$ en **amortissement** ou en **espérance**.

??? question "Q3 — Résolution de collision et $O(1)$ dans le pire cas"

    Quelle stratégie de résolution de collision garantit un coût $O(1)$ dans le **pire cas** pour l'opération `get(k)` ?

    - A. Separate chaining
    - B. Linear probing
    - C. Quadratic probing
    - D. Cuckoo hashing

    ??? success "Réponse"

        **Réponse : D.** Cuckoo hashing maintient l'invariant qu'une clé ne peut se trouver qu'en exactement une des deux positions calculées par ses deux fonctions de hachage. `get(k)` vérifie donc au plus **2 cases**, ce qui est $O(1)$ dans le pire cas.

        Les autres stratégies ont un pire cas $O(n)$ : avec le separate chaining, toutes les clés peuvent se retrouver dans le même bucket ; avec le linear probing ou quadratic probing, une longue chaîne de probes peut se former.

??? question "Q4 — Complexité spatiale de la représentation par adjacency map"

    Quelle est la complexité spatiale d'une représentation de graphe par **adjacency map**, pour un graphe à $n$ sommets et $m$ arêtes ?

    - A. $O(n)$
    - B. $O(m)$
    - C. $O(n + m)$
    - D. $O(n^2)$

    ??? success "Réponse"

        **Réponse : C.** La représentation par adjacency map alloue une entrée par sommet dans la structure principale ($O(n)$) et une entrée par arête dans les maps secondaires ($O(m)$, puisque chaque arête est référencée par ses deux extrémités dans un graphe non orienté, ou une fois dans un graphe orienté). L'espace total est donc $O(n + m)$.

        $O(n^2)$ correspond à l'**adjacency matrix**, qui alloue une case pour chaque paire de sommets même si l'arête est absente — coûteux pour les graphes creux.

??? question "Q5 — Contrat `hashCode()` / `equals()` en Java"

    En Java, dans quel sens l'implication entre `equals()` et `hashCode()` doit-elle obligatoirement être respectée ? Expliquez ce qui se passe concrètement dans une `HashMap` si ce sens est violé.

    ??? success "Réponse"

        Le contrat impose : **si `x.equals(y)` est `true`, alors `x.hashCode() == y.hashCode()`** doit être vrai. L'implication inverse n'est pas requise (deux objets non-égaux peuvent partager le même hash code — c'est une collision ordinaire).

        Si on redéfinit `equals()` sans redéfinir `hashCode()`, deux objets logiquement « égaux » peuvent obtenir des hash codes différents (l'implémentation par défaut de `Object.hashCode()` est basée sur l'adresse mémoire). La `HashMap` les place alors dans des **buckets distincts** : un `put` suivi d'un `get` avec un objet « égal » renvoie `null`, et plusieurs entrées pour la même clé logique peuvent coexister, violant le contrat de la map.

---

### Partie B — Questions longues

??? question "Q6 — Quand `HashMap.get()` n'est plus $O(1)$"

    Un étudiant affirme : « J'utilise toujours une `HashMap` parce que `get()` est $O(1)$. » Dans quelles situations cette affirmation est-elle fausse ou trompeuse ? Comparez avec `TreeMap`. Justifiez votre réponse.

    ??? success "Réponse"

        **Situations où le $O(1)$ ne tient pas :**

        1. **Mauvaise fonction de hachage.** Si `hashCode()` renvoie une constante (ou distribue très mal les clés), toutes les entrées tombent dans le même bucket → les opérations dégradent à $O(n)$, équivalent à une liste non triée.

        2. **Facteur de charge non contrôlé.** Sans rehashing (ou avec une capacité initiale trop petite), $\lambda = n/N$ croît sans borne. Le coût attendu est $O(1 + \lambda)$, qui devient $O(n)$ quand $\lambda \to \infty$.

        3. **Attaque par collision (DoS).** Un adversaire peut générer des clés qui partagent le même hash code pour saturer un bucket et forcer $O(n)$ par opération.

        4. **Pire cas garanti absent.** Le $O(1)$ est **attendu** (probabiliste), pas garanti dans le pire cas. Certaines applications critiques ont besoin de garanties déterministes.

        **Comparaison avec `TreeMap` :**

        | Critère | `HashMap` | `TreeMap` |
        |---------|-----------|-----------|
        | `get`, `put`, `remove` (cas moyen) | $O(1)$ attendu | $O(\log n)$ garanti |
        | `get`, `put`, `remove` (pire cas) | $O(n)$ | $O(\log n)$ |
        | Ordre des clés | non | trié naturellement |
        | Opérations de plage (`subMap`, `floorKey`…) | non disponibles | $O(\log n)$ |
        | Exigences sur la clé | `hashCode` + `equals` | `Comparable` ou `Comparator` |

        `TreeMap` est préférable quand les clés doivent être parcourues en ordre, quand des opérations de plage sont nécessaires, ou quand on a besoin d'un pire cas logarithmique garanti. `HashMap` est préférable quand l'accès aléatoire rapide suffit et que les clés sont bien distribuées.

??? question "Q7 — Compléter `put(k, v)` dans `UnsortedTableMap`"

    Complétez la méthode Java suivante. Les commentaires `// TODO` indiquent les parties à remplir. La méthode `findIndex(k)` retourne l'indice de la clé `k` dans `table` si elle existe, ou `table.size()` sinon.

    ```java
    public class UnsortedTableMap<K, V> extends AbstractMap<K, V> {

        private ArrayList<MapEntry<K, V>> table = new ArrayList<>();

        // Retourne l'indice de k dans table, ou table.size() si absent.
        private int findIndex(K key) {
            int n = table.size();
            for (int j = 0; j < n; j++)
                if (table.get(j).getKey().equals(key)) return j;
            return n;
        }

        @Override
        public V put(K key, V value) {
            int j = findIndex(key);
            if (j < table.size()) {
                // TODO 1 : la clé existe déjà — mettre à jour la valeur et retourner l'ancienne
            }
            // TODO 2 : la clé est nouvelle — ajouter une nouvelle entrée et retourner null
        }
    }
    ```

    ??? success "Réponse"

        ```java
        @Override
        public V put(K key, V value) {
            int j = findIndex(key);
            if (j < table.size()) {
                return table.get(j).setValue(value);  // met à jour et retourne l'ancienne valeur
            }
            table.add(new MapEntry<>(key, value));    // nouvelle entrée ajoutée en fin de liste
            return null;
        }
        ```

        `setValue(value)` (défini dans `MapEntry`) remplace la valeur et retourne l'ancienne — exactement ce que `put` doit retourner quand la clé existe déjà. Quand la clé est nouvelle, on ajoute simplement en fin de liste (`add` en $O(1)$ amorti) et on retourne `null` conformément au contrat de l'ADT Map. La recherche préalable via `findIndex` domine à $O(n)$.

---

## Section 2 — Arbres, Trie, BST & Parcours de graphes

> **Durée suggérée : 25 min**

---

### Partie A — Questions courtes

??? question "Q1 — Nombre d'arêtes dans un arbre"

    Un arbre (graphe connexe acyclique non orienté) à $n$ nœuds possède exactement $n - 1$ arêtes.

    ??? success "Réponse"

        **Vrai.** Preuve par induction : un arbre à 1 nœud a 0 arête = $1 - 1$ ✓. Supposons la propriété vraie pour tout arbre à $k$ nœuds. Un arbre à $k + 1$ nœuds contient au moins une feuille ; retirer cette feuille et l'arête qui la relie donne un arbre à $k$ nœuds avec $k - 1$ arêtes (par H.I.), donc l'arbre original a $(k - 1) + 1 = k$ arêtes = $(k + 1) - 1$ ✓.

        **Piège classique :** un graphe connexe à $n$ nœuds et $n - 1$ arêtes est nécessairement un arbre (sans cycles). Supprimer une arête quelconque le déconnecte ; ajouter une arête quelconque crée un cycle.

??? question "Q2 — DFS récursif vs DFS itératif : même ordre de visite ?"

    Un DFS récursif et un DFS itératif utilisant une **Stack** explicite, lancés depuis le même sommet dans le même graphe, visitent nécessairement les sommets dans le même ordre.

    ??? success "Réponse"

        **Faux.** Les deux algorithmes produisent bien un parcours en profondeur et visitent le même ensemble de sommets, mais l'ordre de visite peut différer.

        Dans la version **récursive**, on traite le premier voisin non visité de $v$ *immédiatement* — c'est lui qui est exploré en profondeur avant les autres voisins de $v$. Dans la version **itérative**, on empile *tous* les voisins non visités de $v$ avant d'en traiter un. Comme la pile est LIFO, c'est le *dernier* voisin empilé qui est traité en premier — l'ordre effectif dépend de l'ordre d'empilement et peut différer de l'ordre récursif.

        **Exemple :** graphe avec les arêtes $(1,2)$, $(1,3)$, $(2,4)$, en partant de 1 avec voisins dans l'ordre $[2, 3]$ : la version récursive visite $1 \to 2 \to 4 \to 3$ ; une version itérative qui empile dans l'ordre $[2, 3]$ dépile 3 en premier et visite $1 \to 3 \to 2 \to 4$.

??? question "Q3 — Ordre du parcours in-order d'un BST"

    Le parcours **in-order** d'un arbre binaire de recherche (BST) non vide produit les clés dans quel ordre ?

    - A. Dans l'ordre d'insertion
    - B. Dans l'ordre décroissant
    - C. Dans l'ordre croissant
    - D. Dans un ordre qui dépend de la structure de l'arbre

    ??? success "Réponse"

        **Réponse : C.** La propriété BST garantit : toutes les clés du sous-arbre gauche de $v$ sont $\leq k(v)$, et toutes celles du sous-arbre droit sont $\geq k(v)$. Le parcours in-order (gauche → racine → droite) visite donc les clés en **ordre croissant**, quel que soit l'ordre d'insertion ou la forme de l'arbre.

        A est fausse : l'ordre in-order dépend des valeurs, pas de l'ordre d'insertion. B serait obtenu par un parcours in-order *inversé* (droite → racine → gauche). D est fausse : l'ordre croissant est garanti par la propriété BST, indépendamment de la structure.

??? question "Q4 — Complexité de la recherche dans un Trie"

    Quelle est la complexité de la recherche d'un mot de longueur $d$ dans un Trie contenant $n$ mots ?

    - A. $O(n)$
    - B. $O(d \cdot n)$
    - C. $O(d)$
    - D. $O(\log n)$

    ??? success "Réponse"

        **Réponse : C.** La recherche dans un Trie suit un chemin de la racine vers le bas, en descendant d'un niveau par caractère du mot. Avec $d$ caractères, on effectue exactement $d$ comparaisons, **indépendamment** du nombre $n$ de mots stockés dans le Trie.

        A ($O(n)$) serait le coût d'un parcours exhaustif dans une liste. D ($O(\log n)$) est le coût de la recherche dans un BST équilibré. L'avantage principal du Trie est que le coût de recherche ne dépend que de la longueur de la clé, pas de la taille de la collection.

??? question "Q5 — Hauteur minimale et maximale d'un BST"

    Décrivez la différence entre la hauteur **maximale** et la hauteur **minimale** d'un BST à $n$ clés distinctes. Donnez un exemple d'ordre d'insertion qui déclenche le cas le plus défavorable.

    ??? success "Réponse"

        **Hauteur minimale :** $\lfloor \log_2 n \rfloor$ — atteinte quand l'arbre est complet ou presque complet. Exemple : insérer $\{4, 2, 6, 1, 3, 5, 7\}$ (médiane en premier) produit un arbre équilibré de hauteur 2.

        **Hauteur maximale :** $n - 1$ — atteinte quand l'arbre dégénère en liste chaînée. **Exemple déclencheur :** insérer $1, 2, 3, 4, 5$ dans l'ordre croissant (ou toute séquence déjà triée). Chaque nouvelle clé devient l'enfant droit du dernier nœud inséré, produisant une chaîne de hauteur $n - 1 = 4$. Toutes les opérations (`get`, `put`, `remove`) dégradent alors à $O(n)$.

---

### Partie B — Questions longues

??? question "Q6 — Pourquoi BFS utilise une Queue et DFS une Stack"

    Expliquez pourquoi BFS utilise une **Queue** et DFS utilise une **Stack**. Quelle propriété de chaque structure de données rend le parcours correct ? Que se passerait-il si on inversait les structures ? Justifiez votre réponse.

    ??? success "Réponse"

        **BFS et la Queue (FIFO) :**

        BFS doit explorer tous les sommets à distance $d$ de la source avant ceux à distance $d + 1$. La Queue garantit l'ordre FIFO : les sommets sont traités dans l'**ordre où ils ont été découverts**. Quand on retire un sommet $v$, ses voisins non visités sont ajoutés en fin de Queue. Ainsi, tous les voisins directs (niveau 1) sont traités avant leurs propres voisins (niveau 2), etc. La propriété FIFO réalise exactement l'exploration *niveau par niveau*, ce qui garantit que le premier chemin trouvé vers tout sommet est de longueur minimale.

        **DFS et la Stack (LIFO) :**

        DFS doit explorer aussi loin que possible avant de faire du backtracking. La Stack garantit l'ordre LIFO : le **dernier sommet découvert est le premier traité**. Quand on empile les voisins de $v$, c'est le voisin le plus récemment empilé qui est exploré en premier, forçant la descente en profondeur. En version récursive, le call stack du système joue ce rôle implicitement — c'est pourquoi les deux versions sont équivalentes en termes d'arêtes explorées (bien que l'ordre exact de visite puisse différer selon l'implémentation).

        **Que se passe-t-il si on inverse les structures ?**

        - BFS avec une Stack (LIFO) : on ne ferait plus une exploration par niveaux — on irait en profondeur comme un DFS, perdant la garantie de trouver les plus courts chemins.
        - DFS avec une Queue (FIFO) : on ferait une exploration en largeur comme un BFS — on ne descendrait plus en profondeur avant de revenir.

        | Algorithme | Structure | Propriété exploitée |
        |-----------|-----------|---------------------|
        | BFS | Queue (FIFO) | Exploration par niveaux croissants |
        | DFS | Stack (LIFO) | Descente en profondeur avec backtracking |

??? question "Q7 — Compléter `treeSearch(key, p)` dans `TreeMap`"

    Complétez la méthode Java suivante. Les commentaires `// TODO` indiquent les parties à remplir. Un nœud **externe** (*external node*) est une sentinelle sans entrée, utilisée pour indiquer les emplacements d'insertion. Si la clé est trouvée, on retourne le nœud interne ; si elle est absente, on retourne le nœud externe atteint (position potentielle d'insertion).

    ```java
    public class TreeMap<K, V> extends AbstractSortedMap<K, V> {

        /**
         * Retourne la position du nœud contenant la clé key,
         * ou le nœud externe (sentinelle) à l'endroit où key devrait être inséré.
         */
        private Position<Entry<K,V>> treeSearch(K key, Position<Entry<K,V>> p) {
            if (isExternal(p))
                // TODO 1 : nœud sentinelle atteint — clé absente, retourner ce nœud
            int comp = compare(key, p.getElement());
            if (comp == 0)
                // TODO 2 : clé trouvée — retourner ce nœud
            else if (comp < 0)
                // TODO 3 : clé plus petite — récurser dans le sous-arbre gauche
            else
                // TODO 4 : clé plus grande — récurser dans le sous-arbre droit
        }
    }
    ```

    ??? success "Réponse"

        ```java
        private Position<Entry<K,V>> treeSearch(K key, Position<Entry<K,V>> p) {
            if (isExternal(p))
                return p;                          // sentinelle : clé absente
            int comp = compare(key, p.getElement());
            if (comp == 0)
                return p;                          // clé trouvée
            else if (comp < 0)
                return treeSearch(key, left(p));   // chercher dans le sous-arbre gauche
            else
                return treeSearch(key, right(p));  // chercher dans le sous-arbre droit
        }
        ```

        Retourner le nœud **externe** (et non `null`) quand la clé est absente est un choix de conception important : l'appelant (`get`, `put`, `remove`) peut directement utiliser cette position pour une insertion éventuelle, sans avoir à relancer une recherche. La complexité est $O(h)$ où $h$ est la hauteur de l'arbre — $O(\log n)$ pour un BST équilibré, $O(n)$ dans le pire cas dégénéré.

---

## Section 3 — Map Ordonnée & Listes à Enjambements

> **Durée suggérée : 25 min**

---

### Partie A — Questions courtes

??? question "Q1 — Complexité de `put` dans `SortedTableMap`"

    Dans une `SortedTableMap`, l'opération `put(k, v)` s'exécute en $O(\log n)$ dans **tous** les cas, car la position d'insertion est trouvée par dichotomie.

    ??? success "Réponse"

        **Faux.** La dichotomie (`findIndex`) localise bien la position en $O(\log n)$. Cependant, si la clé est **nouvelle**, il faut insérer une entrée dans le tableau trié en déplaçant en moyenne $O(n)$ éléments vers la droite — le `put` pour une nouvelle clé est donc $O(n)$.

        Seul le cas où la clé **existe déjà** est $O(\log n)$ : on localise et on remplace la valeur en place, sans décalage. Ce comportement asymétrique est le principal inconvénient de `SortedTableMap` par rapport à `TreeMap`.

??? question "Q2 — Intervalle de `subMap(k1, k2)`"

    La méthode `subMap(k1, k2)` d'une `SortedMap` retourne toutes les entrées dont la clé satisfait $k_1 \leq k \leq k_2$, les deux bornes incluses.

    ??? success "Réponse"

        **Faux.** La convention de `SortedMap` (et de `TreeMap` en Java) est l'intervalle **semi-ouvert** $[k_1, k_2)$ : $k_1$ est **incluse** mais $k_2$ est **exclue**. Pour inclure $k_2$, il faut appeler `subMap(k1, successor(k2))` ou utiliser `headMap`/`tailMap` selon le cas.

        Cette convention est cohérente avec les intervalles standard en informatique (indices de tableau, `String.substring`, etc.) et facilite la composition : `subMap(a, b)` et `subMap(b, c)` couvrent des plages disjointes et contiguës.

??? question "Q3 — Hauteur attendue d'une liste à enjambements"

    Quelle est la hauteur **attendue** d'une liste à enjambements (*skip list*) contenant $n$ entrées ?

    - A. $O(1)$
    - B. $O(\log n)$
    - C. $O(n)$
    - D. $O(n \log n)$

    ??? success "Réponse"

        **Réponse : B.** Chaque entrée est promue au niveau $i$ avec probabilité $\frac{1}{2^{i-1}}$. La probabilité qu'une entrée atteigne une hauteur $\geq i$ décroît exponentiellement. Par analyse probabiliste, la hauteur maximale attendue pour $n$ entrées est $O(\log n)$, et elle ne dépasse $3\log n$ qu'avec probabilité $\frac{1}{n^2}$.

        A est fausse : la hauteur croît avec $n$. C serait la hauteur d'une liste dégénérée (si toutes les pièces tombent face en continu). D est fausse et correspondrait à un comportement pire que linéaire.

??? question "Q4 — Opération en $O(1)$ dans `SortedTableMap`"

    Laquelle des opérations suivantes s'exécute en **$O(1)$** dans une `SortedTableMap` ?

    - A. `get(k)`
    - B. `put(k, v)` pour une clé nouvelle
    - C. `remove(k)`
    - D. `firstEntry()`

    ??? success "Réponse"

        **Réponse : D.** `firstEntry()` retourne simplement `table.get(0)` — accès en $O(1)$ à la première position du tableau trié. De même, `lastEntry()` accède à `table.get(table.size()-1)` en $O(1)$.

        A : `get(k)` nécessite une dichotomie — $O(\log n)$. B : `put` pour une nouvelle clé nécessite de décaler des éléments — $O(n)$. C : `remove` supprime une entrée et décale les éléments suivants — $O(n)$.

??? question "Q5 — Quand préférer `SortedTableMap` à `TreeMap` ?"

    Donnez un cas d'utilisation concret où `SortedTableMap` est préférable à `TreeMap`, et expliquez pourquoi.

    ??? success "Réponse"

        `SortedTableMap` est préférable lorsque la collection est **construite une fois puis consultée de nombreuses fois sans modification** — par exemple : un annuaire téléphonique, une table de constantes physiques, un dictionnaire de traduction chargé au démarrage de l'application, ou un catalogue de produits en lecture seule.

        Dans ce scénario, les $O(n)$ d'insertion initiale sont absorbés par le grand nombre de requêtes `get`, `ceilingEntry`, `floorEntry`, ou `subMap`, toutes en $O(\log n)$. De plus, `SortedTableMap` offre un meilleur **facteur constant** que `TreeMap` (accès tableau contigu vs pointeurs dans un arbre), ce qui améliore les performances de cache.

---

### Partie B — Questions longues

??? question "Q6 — Mécanisme probabiliste de la skip list"

    Une liste à enjambements atteint une complexité $O(\log n)$ attendue pour la recherche sans recourir à des rotations ni à un rééquilibrage explicite. Expliquez le **mécanisme probabiliste** qui garantit ce comportement. Identifiez ensuite le **principal inconvénient** de la skip list par rapport à un BST équilibré déterministe (AVL ou rouge-noir). Justifiez votre réponse.

    ??? success "Réponse"

        **Mécanisme probabiliste :**

        La skip list maintient plusieurs listes imbriquées $S_0 \supseteq S_1 \supseteq \cdots \supseteq S_h$. $S_0$ contient toutes les entrées ; chaque niveau supérieur $S_i$ est construit en promouvant indépendamment chaque entrée de $S_{i-1}$ avec probabilité $\frac{1}{2}$ (lancer de pièce).

        Lors d'une recherche, on démarre du coin supérieur gauche et on **avance** horizontalement tant que la prochaine clé ne dépasse pas $k$, puis on **descend** d'un niveau. Ce mécanisme est analogue à une recherche binaire : chaque niveau supérieur permet de « sauter » en moyenne la moitié des entrées restantes. En espérance, on effectue $O(\log n)$ sauts horizontaux par niveau et $O(\log n)$ descentes, soit $O(\log n)$ opérations au total.

        La hauteur de la structure est $O(\log n)$ en espérance, car la probabilité qu'une tour atteigne la hauteur $i$ est $\frac{1}{2^{i-1}}$ — la taille de chaque niveau est divisée par deux en espérance.

        **Principal inconvénient :**

        La skip list ne donne **aucune garantie dans le pire cas**. Si les lancers de pièce sont défavorables (improbable mais possible), la hauteur peut atteindre $n$ et toutes les opérations dégradent à $O(n)$. Un adversaire qui contrôle les aléas (ou un PRNG prévisible) peut exploiter cette faiblesse.

        En revanche, les BST équilibrés déterministes (AVL, rouge-noir) garantissent $O(\log n)$ dans **tous les cas**, ce qui est essentiel dans les applications temps-réel ou à haute criticité (bases de données, systèmes embarqués).

        | Critère | Skip list | BST équilibré (AVL/R-N) |
        |---------|-----------|------------------------|
        | Complexité recherche | $O(\log n)$ **attendu** | $O(\log n)$ **garanti** |
        | Complexité pire cas | $O(n)$ | $O(\log n)$ |
        | Implémentation | Simple (pas de rotations) | Complexe (rotations, rééquilibrage) |
        | Mémoire | $O(n)$ attendu (2n nœuds en moyenne) | $O(n)$ exact |
        | Résilience face à un adversaire | Non | Oui |

??? question "Q7 — Compléter `findIndex(k, low, high)` dans `SortedTableMap`"

    Complétez la méthode Java suivante. Les commentaires `// TODO` indiquent les parties à remplir. Cette méthode est l'helper de dichotomie de `SortedTableMap` : elle retourne le plus petit indice `i` dans `[low, high]` tel que `key(i) >= k`, ou `high + 1` si aucune entrée ne satisfait cette condition.

    ```java
    public class SortedTableMap<K, V> extends AbstractSortedMap<K, V> {

        private ArrayList<MapEntry<K, V>> table = new ArrayList<>();

        private K safe(int j) { return table.get(j).getKey(); }

        /**
         * Retourne le plus petit indice i dans [low, high] tel que key(i) >= key,
         * ou high+1 si toutes les clés dans [low, high] sont strictement inférieures à key.
         */
        private int findIndex(K key, int low, int high) {
            if (high < low)
                // TODO 1 : aucune clé ne qualifie — retourner la valeur sentinelle appropriée
            int mid = (low + high) / 2;
            int comp = compare(key, safe(mid));
            if (comp == 0)
                // TODO 2 : clé trouvée exactement à mid — retourner mid
            else if (comp < 0)
                // TODO 3 : la clé cherchée est plus petite — récurser à gauche
            else
                // TODO 4 : la clé cherchée est plus grande — récurser à droite
        }
    }
    ```

    ??? success "Réponse"

        ```java
        private int findIndex(K key, int low, int high) {
            if (high < low)
                return high + 1;                        // aucune entrée ne qualifie
            int mid = (low + high) / 2;
            int comp = compare(key, safe(mid));
            if (comp == 0)
                return mid;                             // correspondance exacte
            else if (comp < 0)
                return findIndex(key, low, mid - 1);   // chercher dans la moitié gauche
            else
                return findIndex(key, mid + 1, high);  // chercher dans la moitié droite
        }
        ```

        Retourner `high + 1` (et non `low` ou `-1`) quand `high < low` est crucial : cela indique que toutes les clés de la plage sont strictement inférieures à `key`, et que `key` devrait être insérée à la position `high + 1`. Les appelants de `findIndex` (`get`, `put`, `remove`, `ceilingEntry`…) s'appuient sur cette valeur sentinelle pour décider si une entrée correspondante existe ou non — retourner `low` causerait des insertions au mauvais endroit ou des accès hors bornes.

---

## Section 4 — Arbres Équilibrés

> **Durée suggérée : 45 min**

---

### Partie A — Questions courtes

??? question "Q1 — Facteur d'équilibre AVL"

    Dans un arbre AVL valide, le **facteur d'équilibre** de tout nœud (défini comme la hauteur du sous-arbre droit moins la hauteur du sous-arbre gauche) appartient toujours à l'ensemble $\{-1, 0, 1\}$.

    ??? success "Réponse"

        **Vrai.** La propriété AVL exige précisément que, pour tout nœud $v$ : $|\text{hauteur}(\text{droite}(v)) - \text{hauteur}(\text{gauche}(v))| \leq 1$, ce qui est équivalent à dire que la valeur signée (droite $-$ gauche) appartient à $\{-1, 0, 1\}$. Dès qu'une insertion ou suppression provoque un facteur de $-2$ ou $+2$ sur un ancêtre, une restructuration trinode est déclenchée pour le ramener dans $\{-1, 0, 1\}$.

        **Piège classique :** le champ `aux` de `AVLTreeMap` stocke la **hauteur**, pas le facteur d'équilibre. Ce dernier est recalculé à la demande depuis les hauteurs des deux enfants.

??? question "Q2 — Garantie amortie de l'arbre Splay"

    Un arbre Splay garantit une complexité $O(\log n)$ **amortie** par opération sur une séquence quelconque, mais une opération individuelle peut prendre $O(n)$ dans le pire cas.

    ??? success "Réponse"

        **Vrai.** La garantie de l'arbre Splay est amortie : toute séquence de $m$ opérations sur un arbre de $n$ éléments s'exécute en $O(m \log n)$ au total. Cependant, une opération isolée peut coûter $O(n)$ — par exemple, accéder à la feuille la plus profonde d'un arbre dégénéré.

        L'intuition est que le splay « rembourse » le coût d'un accès profond en rapprochant le nœud de la racine, réduisant ainsi le coût des accès futurs. Cette redistribution du coût dans le temps est la définition même de la complexité amortie.

??? question "Q3 — Hauteur noire dans un arbre Rouge-Noir"

    Dans un arbre rouge-noir, tous les chemins simples d'un nœud quelconque vers ses nœuds descendants externes traversent le **même nombre de nœuds noirs**.

    ??? success "Réponse"

        **Vrai.** C'est exactement la propriété **P4** des arbres rouge-noir. Ce nombre commun s'appelle la **hauteur noire** (*black-height*) du nœud. P4 ne garantit pas que tous les chemins ont la même longueur totale — certains peuvent contenir des nœuds rouges supplémentaires — mais uniquement le même nombre de nœuds noirs.

        Cette propriété, combinée avec P3 (pas de rouge-rouge consécutif), implique que la hauteur totale $h \leq 2 \cdot bh$, ce qui garantit $h = O(\log n)$.

??? question "Q4 — Nombre de rotations en zig-zag AVL"

    Lors d'un rééquilibrage AVL par **restructuration trinode**, un cas **zig-zag** requiert combien de rotations élémentaires ?

    - A. 0
    - B. 1
    - C. 2
    - D. 3

    ??? success "Réponse"

        **Réponse : C.** Un zig-zag se produit quand $x$ et $y$ sont de **côtés opposés** (ex. $y$ enfant gauche de $z$, $x$ enfant droit de $y$). La restructuration applique une **double rotation** : d'abord une rotation qui amène $x$ au-dessus de $y$, puis une seconde qui amène $x$ au-dessus de $z$. Résultat : $x$ (le médian $b$) devient la nouvelle racine du sous-arbre.

        En zig-zig (même côté), une **seule rotation** suffit pour amener $y$ au-dessus de $z$. C'est le piège classique : zig-zig = 1 rotation, zig-zag = 2 rotations.

??? question "Q5 — Champ `aux` dans `AVLTreeMap`"

    Dans `AVLTreeMap`, le champ `aux` hérité de `BalanceableBinaryTree` stocke :

    - A. La couleur du nœud (0 = noir, 1 = rouge)
    - B. La hauteur du nœud
    - C. Le facteur d'équilibre du nœud (différence de hauteurs gauche-droite)
    - D. Un pointeur entier vers le parent du nœud

    ??? success "Réponse"

        **Réponse : B.** Dans `AVLTreeMap`, `aux` stocke la **hauteur** du nœud (longueur du plus long chemin vers une feuille sentinelle). La méthode `recomputeHeight(p)` met à jour `aux` via `tree.setAux(p, 1 + Math.max(height(left(p)), height(right(p))))`.

        A est incorrect : la couleur (0/1) est utilisée par `RedBlackTreeMap`. C est incorrect : le facteur d'équilibre est calculé à la demande à partir des hauteurs des deux enfants, il n'est pas stocké. D est incorrect : les liens parents sont gérés séparément par la structure de l'arbre.

??? question "Q6 — Hauteur maximale d'un arbre Rouge-Noir"

    Soit un arbre rouge-noir dont la **hauteur noire** est $bh$ (nombre de nœuds noirs sur tout chemin racine-feuille). Quelle est la **hauteur maximale** $h$ de l'arbre ?

    - A. $bh$
    - B. $bh + 1$
    - C. $2 \cdot bh$
    - D. $2 \cdot bh + 1$

    ??? success "Réponse"

        **Réponse : C.** La propriété P3 interdit deux nœuds rouges consécutifs. Ainsi, sur tout chemin de longueur $h$, au plus la moitié des nœuds peuvent être rouges → le chemin alterne au pire rouge-noir-rouge-noir… ce qui donne au plus $2 \cdot bh$ nœuds au total. D'où $h \leq 2 \cdot bh$.

        A serait la hauteur si tous les nœuds étaient noirs. D ($2 \cdot bh + 1$) est légèrement supérieur au maximum réel. La borne $h \leq 2 \log_2(n+1)$ du théorème découle de cette inégalité combinée à $n \geq 2^{bh} - 1$.

??? question "Q7 — Zig-zig dans l'arbre Splay"

    Décrivez l'opération **zig-zig** dans un arbre Splay : combien de rotations effectue-t-elle, dans quel ordre, et sur quels nœuds ? En quoi diffère-t-elle de la restructuration trinode zig-zig utilisée dans AVL ?

    ??? success "Réponse"

        **Splay zig-zig :** se produit quand $p$ et son parent $q$ sont du **même côté** (tous les deux enfants gauches, ou tous les deux enfants droits). L'opération effectue **2 rotations** dans cet ordre : d'abord `rotate(q)` (le parent monte), puis `rotate(p)` (le petit-fils monte). Résultat : $p$ atteint la racine du sous-arbre, $q$ devient son enfant, et $r$ (l'ancien grand-parent) devient son petit-fils.

        **Restructuration trinode zig-zig :** effectue **1 seule rotation** autour de $z$ (le grand-parent). Résultat : c'est $y$ (le parent, noté $b$ en ordre infixe) qui devient la nouvelle racine — pas $p$.

        | | Splay zig-zig | Trinode zig-zig |
        |--|--------------|-----------------|
        | Rotations | 2 (`rotate(q)` puis `rotate(p)`) | 1 (autour de $z$) |
        | Nouvelle racine | $p$ (le petit-fils) | $y$ (le parent) |

        Le splay doit impérativement amener $p$ jusqu'à la racine de l'arbre entier : la double rotation en zig-zig est indispensable pour progresser de 2 niveaux en une étape. La trinode n'a pas cet objectif — elle cherche uniquement à rétablir l'équilibre local.

??? question "Q8 — Résolution du double-rouge en arbre Rouge-Noir"

    Dans un arbre rouge-noir, le nœud nouvellement inséré $p$ est rouge et son parent $q$ est également rouge (violation double-rouge). Nommez les **deux cas** de résolution et décrivez la condition qui détermine lequel appliquer.

    ??? success "Réponse"

        Soient $r = \text{parent}(q)$ (le grand-parent de $p$, nécessairement noir) et $s = \text{sibling}(q)$ (l'oncle de $p$).

        **Cas 1 — Recoloriage** (*recoloring*) : l'oncle $s$ est **rouge**.
        On colorie $q$ et $s$ en noir, et $r$ en rouge. Si $r$ est la racine, on la recolorie en noir et on termine. Sinon, $r$ peut maintenant être en double-rouge avec son propre parent → on répète depuis $r$. Ce cas peut se propager $O(\log n)$ fois jusqu'à la racine.

        **Cas 2 — Restructuration** (*restructuring*) : l'oncle $s$ est **noir**.
        On appelle `restructure(p)` → le médian $b$ parmi $\{p, q, r\}$ devient racine du sous-arbre. On colorie $b$ en noir et ses deux enfants $a$, $c$ en rouge. La violation est résolue **immédiatement**, sans propagation (au plus 1 restructuration par insertion).

        **Règle de décision :** la couleur de l'**oncle** $s$ détermine le cas, pas la couleur du parent $q$ (qui est toujours rouge pour déclencher la procédure).

---

### Partie B — Questions longues

??? question "Q9 — Comparer AVL, Splay et Rouge-Noir"

    Comparez les arbres **AVL**, **Splay** et **Rouge-Noir** selon les trois critères suivants : (1) hauteur maximale garantie, (2) coût dans le pire cas par opération, (3) cas d'utilisation idéal. Présentez votre réponse sous forme de tableau. Justifiez les cas d'utilisation.

    ??? success "Réponse"

        | Critère | AVL | Splay | Rouge-Noir |
        |---------|-----|-------|------------|
        | Hauteur maximale | $\approx 1.44 \log_2 n$ | $O(n)$ (dégénéré possible) | $2 \log_2(n+1)$ |
        | Coût par opération (pire cas) | $O(\log n)$ **garanti** | $O(n)$ (pire cas) | $O(\log n)$ **garanti** |
        | Coût par opération (amorti) | $O(\log n)$ | $O(\log n)$ | $O(\log n)$ |
        | Restructurations à l'insertion | $\leq 1$ | $O(\log n)$ amorti | $\leq 1$ |
        | Restructurations à la suppression | $O(\log n)$ | $O(\log n)$ amorti | $\leq 3$ rotations |
        | Champ `aux` | Hauteur | Inutilisé | Couleur (0/1) |
        | Cas d'utilisation idéal | Lectures fréquentes | Accès non uniformes | Usages généraux |

        **Justification des cas d'utilisation :**

        - **AVL** : hauteur minimale parmi les trois ($\approx 1.44 \log_2 n$, strictement inférieure au $2\log_2(n+1)$ des RBT). Idéal quand les **lectures dominent** et que chaque nanoseconde compte (ex. tables de routage, indexation en base de données en lecture seule). La contrainte stricte de hauteur permet des recherches légèrement plus rapides.

        - **Splay** : aucune garantie par opération, mais exploite la **localité temporelle** — les nœuds récemment accédés migrent vers la racine. Idéal pour les caches, les compilateurs (table de symboles), ou toute application avec des **accès répétitifs sur un sous-ensemble de clés**. Implémentation plus simple (pas de champ `aux`).

        - **Rouge-Noir** : compromis équilibré entre AVL (insertion légèrement plus complexe) et Splay (garanties pire cas). Les **suppressions ne nécessitent que ≤ 3 rotations** (contre $O(\log n)$ pour AVL), ce qui en fait le choix standard pour les structures à usage général (`TreeMap` en Java, `std::map` en C++, noyau Linux).

??? question "Q10 — Compléter `rebalanceInsert(p)` dans `AVLTreeMap`"

    Complétez la méthode Java suivante. Les commentaires `// TODO` indiquent les parties à remplir. Les méthodes utilitaires `height(p)`, `recomputeHeight(p)`, `isBalanced(p)`, `tallerChild(p)`, `restructure(x)`, `left(p)`, `right(p)`, `parent(p)` et `isRoot(p)` sont disponibles et correctement implémentées.

    ```java
    // AVLTreeMap (extrait)

    @Override
    protected void rebalanceInsert(Position<Entry<K,V>> p) {
        // p est le nœud nouvellement inséré.
        // Remonter vers la racine en recalculant les hauteurs.
        // Si un ancêtre déséquilibré est trouvé, restructurer et s'arrêter.

        while (!isRoot(p)) {
            p = parent(p);
            // TODO 1 : recalculer la hauteur de p

            if (!isBalanced(p)) {
                // TODO 2 : identifier x, le petit-fils sur lequel appeler restructure
                Position<Entry<K,V>> x = /* TODO 2 */;

                // TODO 3 : restructurer le sous-arbre et recalculer les hauteurs
                //          (enfant gauche, enfant droit, puis nouvelle racine)



                // Une seule restructuration suffit après une insertion AVL
                break;
            }
        }
    }
    ```

    ??? success "Réponse"

        ```java
        @Override
        protected void rebalanceInsert(Position<Entry<K,V>> p) {
            while (!isRoot(p)) {
                p = parent(p);
                recomputeHeight(p);                              // TODO 1

                if (!isBalanced(p)) {
                    Position<Entry<K,V>> x =
                        tallerChild(tallerChild(p));             // TODO 2

                    p = restructure(x);                          // TODO 3a
                    recomputeHeight(left(p));                    // TODO 3b
                    recomputeHeight(right(p));                   // TODO 3c
                    recomputeHeight(p);                          // TODO 3d
                    break;
                }
            }
        }
        ```

        **Explications des choix clés :**

        - **TODO 1 — `recomputeHeight(p)` à chaque étape :** l'insertion dans un sous-arbre peut augmenter la hauteur de tous ses ancêtres. Il faut remettre à jour `aux` (qui stocke la hauteur) au fur et à mesure de la remontée, sinon `isBalanced` travaillerait sur des valeurs périmées.

        - **TODO 2 — `tallerChild(tallerChild(p))` :** pour identifier le cas zig-zig ou zig-zag, on cherche d'abord l'enfant le plus haut de $p$ (c'est $y$), puis l'enfant le plus haut de $y$ (c'est $x$). `restructure(x)` détermine ensuite automatiquement la configuration et effectue 1 ou 2 rotations.

        - **TODO 3 — ordre de `recomputeHeight` :** après `restructure(x)`, $p$ pointe vers la nouvelle racine du sous-arbre. Il faut recalculer les hauteurs de bas en haut : d'abord `left(p)` et `right(p)` (enfants), puis `p` lui-même. Inverser l'ordre produirait des hauteurs incorrectes.

        - **`break` après une restructuration :** après l'insertion, la restructuration restaure la hauteur du sous-arbre à sa valeur d'**avant** l'insertion. Aucun ancêtre ne peut donc être devenu déséquilibré — il suffit d'une seule restructuration. (Contrairement à la suppression, qui peut en nécessiter $O(\log n)$.)
