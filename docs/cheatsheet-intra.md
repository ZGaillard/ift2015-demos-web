# Cheatsheet — Intra IFT2015

Aide-mémoire de révision pour l'intra du cours IFT2015 — Structures de données. Couvre les **sections 1 à 4 (sauf 4.3)** du plan de cours, basé sur *Data Structures and Algorithms in Java, 6th Edition* (Goodrich, Tamassia, Goldwasser).

---

## 1. Introduction

!!! abstract "Objectifs"
    Comprendre pourquoi le choix d'une structure de données détermine la performance d'un algorithme.

- **Structure == Fonction** : une structure est définie par ses opérations dominantes et leur coût asymptotique, pas juste un conteneur.
- **Structure == Accélération** : les invariants maintenus par une structure permettent des algorithmes plus efficaces.

!!! warning "Pièges classiques"
    - Confondre **structure** et **algorithme** — une structure organise les données, un algorithme les manipule.
    - « Plus de code = plus lent » — la complexité asymptotique ne dépend pas du nombre de lignes.
    - « Une structure est meilleure en général » — chaque structure a un compromis ; le choix dépend du profil d'opérations.
    - Oublier le **coût de maintenance des invariants** — maintenir un ordre trié, un heap-order, etc. a un prix.

---

## 2. Types abstraits de données (ADT)

### 2.1 ArrayList et LinkedList

**Livre : §7.1, §7.2.1–7.2.3**

!!! abstract "Objectifs"
    Comparer les implémentations par tableau dynamique et par liste chaînée de l'ADT List.

- **ArrayList** : Tableau dynamique redimensionnable. Accès direct par index en $O(1)$. Insertion/suppression en $O(n)$ à cause du décalage d'éléments. Redimensionnement amorti $O(1)$ par doublement du tableau.
- **SinglyLinkedList** : Chaîne de nœuds avec pointeur `next`. Insertion/suppression en tête en $O(1)$. Accès par index en $O(n)$ — il faut traverser la liste.
- **DoublyLinkedList** : Pointeurs `prev` et `next`. Suppression en $O(1)$ si on a la référence du nœud. Utilise des sentinelles (header/trailer).

| Opération | ArrayList | DoublyLinkedList |
|---|---|---|
| `get(i)` | $O(1)$ | $O(n)$ |
| `set(i, e)` | $O(1)$ | $O(n)$ |
| `add(i, e)` | $O(n)$ | $O(n)$* |
| `remove(i)` | $O(n)$ | $O(n)$* |
| `size()`, `isEmpty()` | $O(1)$ | $O(1)$ |
| `addFirst` / `addLast` | $O(n)$ / $O(1)$† | $O(1)$ |
| `removeFirst` / `removeLast` | $O(n)$ / $O(1)$† | $O(1)$ |

*\* $O(n)$ pour trouver la position i, $O(1)$ si on a déjà la position.*
*† $O(1)$ en fin de tableau seulement, $O(n)$ en début.*

!!! tip "Complexité amortie du redimensionnement"
    Avec la stratégie de **doublement** du tableau, le coût total de $n$ insertions en fin est $O(n)$, soit un coût **amorti $O(1)$** par insertion. Le tableau n'est redimensionné que $O(\log n)$ fois.

---

### 2.2 Positional List

**Livre : §7.3, §7.4, §7.5, §7.6**

!!! abstract "Objectifs"
    Comprendre l'ADT PositionalList et la notion de position stable.

- **PositionalList** : ADT basé sur le concept de *position* plutôt que d'index. Une position est une abstraction stable — elle reste valide même après des insertions/suppressions ailleurs dans la liste. Implémentée idéalement avec une DoublyLinkedList.
- Opérations principales : `first()`, `last()`, `before(p)`, `after(p)`, `addBefore(p, e)`, `addAfter(p, e)`, `set(p, e)`, `remove(p)`.

| Opération | Complexité (DoublyLinkedList) |
|---|---|
| `size()`, `isEmpty()` | $O(1)$ |
| `first()`, `last()`, `before(p)`, `after(p)` | $O(1)$ |
| `addFirst(e)`, `addLast(e)` | $O(1)$ |
| `addBefore(p, e)`, `addAfter(p, e)` | $O(1)$ |
| `set(p, e)` | $O(1)$ |
| `remove(p)` | $O(1)$ |
| Espace | $O(n)$ |

!!! warning "Pièges classiques"
    - **Position ≠ index.** Un itérateur n'est pas une position persistante.
    - Java n'expose pas les positions dans `java.util.LinkedList` pour protéger les invariants internes.
    - Toutes les opérations sont $O(1)$ **seulement si on a déjà la position** — obtenir une position par recherche reste $O(n)$.

---

### 2.3 Favorite List

**Livre : §7.7**

!!! abstract "Objectifs"
    Comparer deux stratégies de gestion de fréquences d'accès.

- **FavoritesList (triée par compteur)** : Éléments triés par fréquence d'accès décroissante. `access(e)` incrémente le compteur et repositionne l'élément (insertion-sort partielle). `getFavorites(k)` retourne les k premiers en $O(k)$.
- **FavoritesListMTF (Move-to-Front)** : Chaque accès déplace l'élément en tête de liste. Exploite la **localité de référence**. `getFavorites(k)` nécessite un tri partiel en $O(kn)$ car la liste n'est pas triée.

| Opération | Triée | Move-to-Front |
|---|---|---|
| `access(e)` | $O(n)$ | $O(n)$ pour chercher, $O(1)$ pour déplacer |
| `remove(e)` | $O(n)$ | $O(n)$ |
| `getFavorites(k)` | $O(k)$ | $O(kn)$ |

!!! tip "Compromis Triée vs MTF"
    **MTF** est meilleur quand la **localité de référence est forte** (séquences répétitives — les éléments récents sont en tête). La version **triée** est meilleure pour des **accès uniformes** où l'ordre par fréquence est plus stable.

---

### 2.4 Stack, Queue, Deque

**Livre : Chapitre 6 (§6.1–6.3)**

!!! abstract "Objectifs"
    Maîtriser les trois ADT à accès restreint et leurs implémentations.

- **Stack (Pile — LIFO)** : Accès uniquement au sommet. `push(e)`, `pop()`, `top()`. Analogie : pile d'assiettes.
- **Queue (File — FIFO)** : Ajout à l'arrière, retrait à l'avant. `enqueue(e)`, `dequeue()`, `first()`. Analogie : file d'attente.
- **Deque (Double-ended Queue)** : Insertion et suppression aux deux extrémités. Généralise Stack et Queue. `addFirst`, `addLast`, `removeFirst`, `removeLast`.

| Opération | Stack (Array) | Queue (Circular Array) | Deque (DLL ou Circular Array) |
|---|---|---|---|
| `push` / `enqueue` / `addFirst` | $O(1)$ | $O(1)$ | $O(1)$ |
| `pop` / `dequeue` / `removeFirst` | $O(1)$ | $O(1)$ | $O(1)$ |
| `top` / `first` / `last` | $O(1)$ | $O(1)$ | $O(1)$ |
| `addLast` / `removeLast` | — | — | $O(1)$ |
| `size`, `isEmpty` | $O(1)$ | $O(1)$ | $O(1)$ |
| Espace | $O(N)$* | $O(N)$* | $O(N)$* ou $O(n)$ |

*\* $N$ = taille du tableau alloué, $n$ = nombre d'éléments effectifs.*

**Correspondance Java (`java.util.Deque`) :**

| Notre ADT | Exceptions | Valeur spéciale |
|---|---|---|
| `first()` | `getFirst()` | `peekFirst()` |
| `last()` | `getLast()` | `peekLast()` |
| `addFirst(e)` | `addFirst(e)` | `offerFirst(e)` |
| `addLast(e)` | `addLast(e)` | `offerLast(e)` |
| `removeFirst()` | `removeFirst()` | `pollFirst()` |
| `removeLast()` | `removeLast()` | `pollLast()` |

!!! warning "Pièges classiques"
    - **Ne pas utiliser `java.util.Stack`** — classe legacy, non thread-safe, hérite de `Vector`.
    - Stack et Queue sont des **politiques d'accès**, pas des structures fondamentalement différentes — les deux peuvent être implémentées par un Deque.
    - File circulaire : `front`, `rear = (front + size) % capacity`.

---

### 2.5 Files concurrentes

**Pas couvert dans le livre.**

!!! abstract "Objectifs"
    Comprendre les enjeux de concurrence pour les files et les solutions Java.

- En contexte concurrent (multi-threaded), les structures classiques ne sont pas thread-safe.
- **Thread-safe vs non thread-safe** : accès simultanés peuvent corrompre les données.
- **Bloquant vs non bloquant** : `BlockingQueue` bloque le thread si la file est pleine/vide.
- **`synchronized`** : verrou simple mais coûteux (contention, risque d'interblocage).
- **`java.util.concurrent`** : `ArrayBlockingQueue`, `ConcurrentLinkedQueue` — structures optimisées pour la concurrence.

!!! warning "Pièges classiques"
    - `synchronized` ne suffit pas toujours — granularité trop grossière.
    - Thread-safe ≠ toujours préférable (coût de synchronisation).
    - Attention aux **interblocages** (deadlocks) et à la **contention**.

---

### 2.6 Priority Queue et Heap

**Livre : Chapitre 9 (§9.1–9.5)**

!!! abstract "Objectifs"
    Comprendre l'ADT Priority Queue et ses implémentations, en particulier le Heap.

- **Priority Queue (ADT)** : Collection d'entrées (clé, valeur) où l'élément de clé minimale est toujours accessible. Opérations : `insert(k, v)`, `min()`, `removeMin()`.
- **Unsorted List PQ** : Insertion en $O(1)$, recherche du min en $O(n)$.
- **Sorted List PQ** : Insertion en $O(n)$ (maintien de l'ordre), min/removeMin en $O(1)$.
- **Heap (Binary Heap)** : Arbre binaire **complet** respectant l'invariant du tas : la clé de chaque nœud $\leq$ clés de ses enfants. Stocké dans un tableau. Insertion et suppression en $O(\log n)$ via upheap/downheap.
- **Adaptable PQ (location-aware)** : Étend le heap pour supporter `remove(entry)`, `replaceKey(entry, k)`, `replaceValue(entry, v)` en $O(\log n)$, grâce à un champ `index` dans chaque entrée.

| Opération | Unsorted List | Sorted List | Heap | Adaptable Heap |
|---|---|---|---|---|
| `size`, `isEmpty` | $O(1)$ | $O(1)$ | $O(1)$ | $O(1)$ |
| `insert` | $O(1)$ | $O(n)$ | $O(\log n)$* | $O(\log n)$ |
| `min` | $O(n)$ | $O(1)$ | $O(1)$ | $O(1)$ |
| `removeMin` | $O(n)$ | $O(1)$ | $O(\log n)$* | $O(\log n)$ |
| `remove(entry)` | — | — | — | $O(\log n)$ |
| `replaceKey(entry, k)` | — | — | — | $O(\log n)$ |
| `replaceValue(entry, v)` | — | — | — | $O(1)$ |
| Espace | $O(n)$ | $O(n)$ | $O(n)$ | $O(n)$ |

*\* Amorti si tableau dynamique.*

!!! tip "Propriétés du Heap"
    - **Invariant du tas (heap-order)** : Pour tout nœud $v$ (sauf la racine), $\text{key}(v) \geq \text{key}(\text{parent}(v))$.
    - **Propriété structurelle** : Arbre binaire complet — tous les niveaux sont pleins sauf le dernier, rempli de gauche à droite.
    - **Hauteur** : $O(\log n)$ car complet.
    - **Stockage en tableau** : pour un nœud à l'index $i$ — enfant gauche = $2i + 1$, enfant droit = $2i + 2$, parent = $\lfloor(i-1)/2\rfloor$.
    - **upheap** : après insertion (en dernière position), remonter tant que la clé est plus petite que le parent.
    - **downheap** : après removeMin (remplacement de la racine par le dernier élément), descendre en échangeant avec le plus petit enfant.

!!! warning "Pièges classiques"
    - **Heap ≠ BST** : un heap n'est pas trié, il garantit seulement que le min est à la racine.
    - Un heap **ne permet pas** la recherche d'un élément arbitraire en $O(\log n)$.

---

## 3. Graphes I

**Livre : §14.1, §14.2 (sauf §14.2.3)**

!!! abstract "Objectifs"
    Connaître l'ADT Graph et comparer les quatre représentations classiques.

- **Graphe (ADT)** : Ensemble de sommets (vertices) $V$ et d'arêtes (edges) $E$. Peut être orienté (digraph) ou non orienté. Opérations : `numVertices()`, `numEdges()`, `vertices()`, `edges()`, `getEdge(u,v)`, `outgoingEdges(v)`, `incomingEdges(v)`, `insertVertex(x)`, `insertEdge(u,v,x)`, `removeVertex(v)`, `removeEdge(e)`, `outDegree(v)`, `inDegree(v)`.
- **Edge List** : Deux listes non ordonnées — une pour les sommets, une pour les arêtes. Simple mais inefficace pour les requêtes.
- **Adjacency List** : Chaque sommet maintient une liste de ses arêtes incidentes. Bon compromis espace/temps.
- **Adjacency Map** : Comme l'adjacency list mais utilise une map (hashing) pour les arêtes incidentes. Accès à une arête spécifique en $O(1)$ attendu.
- **Adjacency Matrix** : Matrice $n \times n$ où `matrix[i][j]` stocke l'arête $(i,j)$. Accès $O(1)$ mais espace $O(n^2)$.

| Opération | Edge List | Adj. List | Adj. Map | Adj. Matrix |
|---|---|---|---|---|
| `numVertices()`, `numEdges()` | $O(1)$ | $O(1)$ | $O(1)$ | $O(1)$ |
| `vertices()` | $O(n)$ | $O(n)$ | $O(n)$ | $O(n)$ |
| `edges()` | $O(m)$ | $O(m)$ | $O(m)$ | $O(m)$ |
| `getEdge(u,v)` | $O(m)$ | $O(\min(d_u, d_v))$ | $O(1)$ exp. | $O(1)$ |
| `outDegree(v)`, `inDegree(v)` | $O(m)$ | $O(1)$ | $O(1)$ | $O(n)$ |
| `outgoingEdges(v)`, `incomingEdges(v)` | $O(m)$ | $O(d_v)$ | $O(d_v)$ | $O(n)$ |
| `insertVertex(x)` | $O(1)$ | $O(1)$ | $O(1)$ | $O(n^2)$ |
| `removeVertex(v)` | $O(m)$ | $O(d_v)$ | $O(d_v)$ | $O(n^2)$ |
| `insertEdge(u,v,x)` | $O(1)$ | $O(1)$ | $O(1)$ exp. | $O(1)$ |
| `removeEdge(e)` | $O(1)$ | $O(1)$ | $O(1)$ exp. | $O(1)$ |
| **Espace** | $O(n+m)$ | $O(n+m)$ | $O(n+m)$ | $O(n^2)$ |

*$n$ = nombre de sommets, $m$ = nombre d'arêtes, $d_v$ = degré de $v$.*

!!! tip "Comment choisir la représentation ?"
    - **Dense** ($m \approx n^2$) → **Matrice d'adjacence**.
    - **Sparse** ($m \ll n^2$) → **Liste ou Map d'adjacence**.
    - **Besoin de vérifier si une arête existe** → **Adj. Map** ou **Matrice**.
    - **Parcours d'arêtes incidentes** → **Adj. List/Map**.

!!! warning "Pièges classiques"
    - **Graphe ≠ arbre** — un arbre est un graphe connexe acyclique.
    - Ne pas utiliser une matrice par défaut — $O(n^2)$ en espace est souvent inutile.
    - Distinguer graphe **orienté** et **non orienté** (symétrie dans la matrice, double entrée dans l'adj. list).

---

## 4. Arbres et Trie

### 4.1 Arbres et Trie

**Livre : §8.1, §13.3**

!!! abstract "Objectifs"
    Connaître les ADT Tree et Binary Tree, les parcours, et la structure Trie.

- **Arbre (Tree — ADT)** : Structure hiérarchique avec une racine, des nœuds internes et des feuilles. Chaque nœud a un parent (sauf la racine) et zéro ou plusieurs enfants. Opérations : `root()`, `parent(p)`, `children(p)`, `numChildren(p)`, `isInternal(p)`, `isExternal(p)`, `isRoot(p)`, `size()`, `isEmpty()`.
- **Arbre binaire (Binary Tree — ADT)** : Arbre où chaque nœud a au plus 2 enfants (gauche et droit). Opérations supplémentaires : `left(p)`, `right(p)`, `sibling(p)`.
- **Trie** : Arbre où chaque arête est étiquetée par un caractère. Les chemins de la racine aux feuilles représentent des chaînes. Utilisé pour la recherche de préfixes et l'autocomplétion.

**Complexité — Arbre binaire (linked structure) :**

| Opération | Complexité |
|---|---|
| `size`, `isEmpty` | $O(1)$ |
| `root`, `parent`, `left`, `right`, `sibling`, `children`, `numChildren` | $O(1)$ |
| `isInternal`, `isExternal`, `isRoot` | $O(1)$ |
| `addRoot`, `addLeft`, `addRight`, `set`, `attach`, `remove` | $O(1)$ |
| `depth(p)` | $O(d_p + 1)$ |
| `height` | $O(n)$ |

**Complexité — Arbre général (linked structure) :**

| Opération | Complexité |
|---|---|
| `size`, `isEmpty` | $O(1)$ |
| `root`, `parent`, `isRoot`, `isInternal`, `isExternal` | $O(1)$ |
| `numChildren(p)` | $O(1)$ |
| `children(p)` | $O(c_p + 1)$ |
| `depth(p)` | $O(d_p + 1)$ |
| `height` | $O(n)$ |

*$c_p$ = nombre d'enfants de $p$, $d_p$ = profondeur de $p$.*

**Parcours d'arbres :**

| Parcours | Ordre | Cas d'usage |
|---|---|---|
| **Preorder** | Racine → Enfants | Table des matières, copie d'arbre |
| **Postorder** | Enfants → Racine | Calcul d'espace disque, suppression |
| **Inorder** (binaire) | Gauche → Racine → Droit | Visite triée d'un BST |
| **BFS (par niveau)** | Niveau par niveau | Plus court chemin |

!!! tip "Propriétés du Trie"
    - Espace : au plus $n+1$ nœuds ($n$ = somme des longueurs de toutes les chaînes).
    - Recherche d'un mot de longueur $m$ : $O(m \times |\Sigma|)$ dans le pire cas, $O(m)$ si enfants stockés dans une map.
    - Variantes : Trie compressé (compressed trie), Trie de suffixes (suffix trie).

---

### 4.2 Arbres binaires de recherche (BST)

**Livre : §8.3, §8.4, §11.1**

!!! abstract "Objectifs"
    Comprendre l'invariant BST, les opérations de recherche/insertion/suppression, et les cas dégénérés.

- **BST** : Arbre binaire où pour chaque nœud $v$ : toutes les clés du sous-arbre gauche $<$ clé($v$) $<$ toutes les clés du sous-arbre droit. Un parcours inorder donne les éléments en ordre croissant.
- Recherche, insertion, suppression : $O(h)$ où $h$ est la hauteur.
- Meilleur cas : $h = O(\log n)$ (arbre équilibré).
- Pire cas : $h = O(n)$ (arbre dégénéré — insertions en ordre croissant/décroissant).

| Opération | Complexité |
|---|---|
| `size`, `isEmpty` | $O(1)$ |
| `get(k)`, `put(k,v)`, `remove(k)` | $O(h)$ |
| `firstEntry`, `lastEntry` | $O(h)$ |
| `ceilingEntry`, `floorEntry`, `lowerEntry`, `higherEntry` | $O(h)$ |
| `subMap` | $O(s + h)$ |
| `entrySet`, `keySet`, `values` | $O(n)$ |

*$h$ = hauteur de l'arbre, $s$ = nombre de résultats dans subMap.*

!!! warning "Pièges classiques"
    - Ne jamais **supposer** qu'un BST est équilibré — la hauteur $h$ peut être $n$.
    - **Hauteur ≠ taille** : un arbre de taille $n$ peut avoir une hauteur allant de $\lfloor\log n\rfloor$ à $n-1$.
    - Cas dégénéré : insertions en ordre → liste chaînée → $h = n$.
    - **Suppression d'un nœud avec 2 enfants** : remplacer par le successeur inorder (min du sous-arbre droit) ou le prédécesseur (max du sous-arbre gauche).

---

## Récapitulatif — Catégorisation des structures

### Listes

| Structure | Type d'accès | Cas d'usage principal |
|---|---|---|
| ArrayList | Index | Accès fréquent par position |
| SinglyLinkedList | Séquentiel | Insertion/suppression en tête |
| DoublyLinkedList | Séquentiel bidirectionnel | Base pour PositionalList, Deque |
| PositionalList | Position | Insertion/suppression avec curseur stable |
| FavoritesList | Fréquence | Top-k éléments (trié par compteur) |
| FavoritesListMTF | Fréquence + localité | Cache adaptatif (move-to-front) |

### Piles, Files, Deques

| Structure | Discipline | Implémentations |
|---|---|---|
| Stack | LIFO | ArrayStack, LinkedStack |
| Queue | FIFO | ArrayQueue (circulaire), LinkedQueue |
| Deque | Double-ended | ArrayDeque (circulaire), LinkedDeque |

### Files avec priorités

| Structure | Invariant | Cas d'usage |
|---|---|---|
| UnsortedPriorityQueue | Aucun | Insertion rapide, peu de removeMin |
| SortedPriorityQueue | Liste triée | removeMin fréquent, peu d'insertions |
| HeapPriorityQueue | Heap-order + complet | Usage général (insert + removeMin équilibrés) |
| HeapAdaptablePQ | Heap + location-aware | Mise à jour de priorités (Dijkstra, scheduling) |

### Graphes

| Structure | Espace | Cas d'usage |
|---|---|---|
| EdgeList | $O(n+m)$ | Prototypage, petit graphe |
| AdjacencyList | $O(n+m)$ | Usage général, graphes sparse |
| AdjacencyMap | $O(n+m)$ | Requêtes fréquentes getEdge |
| AdjacencyMatrix | $O(n^2)$ | Graphes denses, getEdge en $O(1)$ |

### Arbres

| Structure | Propriété | Cas d'usage |
|---|---|---|
| General Tree | Hiérarchie, n-aire | Systèmes de fichiers, DOM |
| Binary Tree | Max 2 enfants | Base pour BST, heap |
| BST | Ordre gauche < racine < droit | Recherche, tri, map ordonnée |
| Trie | Chemins = chaînes | Autocomplétion, dictionnaire, recherche de préfixes |
