# Programme du cours

Cette page présente les grands thèmes du cours IFT2015, les objectifs d'apprentissage associés et les pièges classiques à éviter. Les liens vers les démonstrations pratiques correspondantes sont indiqués pour chaque module.

---

## 1. Introduction (2h)

### 1.1 Structure == fonction

!!! abstract "Objectifs"

    - Comprendre qu'une structure de données est un **choix algorithmique**, pas un simple conteneur
    - Relier une structure à un ensemble d'opérations dominantes et à un coût asymptotique

### 1.2 Structure == accélération

!!! abstract "Objectifs"

    - Comprendre qu'une structure repose sur des **invariants**
    - Accepter qu'un algorithme « plus compliqué » peut être plus rapide

!!! warning "Pièges classiques"

    - Confondre structure et algorithme
    - Penser que « plus de code = plus lent »
    - Croire qu'une structure est « meilleure en général »
    - Oublier le coût de maintenance des invariants

---

## 2. Types abstraits de données (ADT) (9h)

### 2.1 ArrayList et LinkedList (§7.1, 7.2.1–7.2.3)

!!! abstract "Objectifs"

    - Distinguer l'ADT List de ses implémentations
    - Comparer les coûts des accès, insertions et suppressions
    - Choisir une implémentation en fonction de l'usage

!!! warning "Pièges classiques"

    - Croire que `LinkedList` est toujours plus rapide
    - Sous-estimer le coût de `get(i)` dans une liste chaînée
    - Confondre complexité amortie et coût constant
    - Raisonner uniquement en termes de mémoire

:material-arrow-right-circle: [Démonstration 2 — Listes et listes positionnelles](exercices/demo2.md)

### 2.2 Positional list (§7.3–7.6)

!!! abstract "Objectifs"

    - Comprendre ce qu'est une **position**
    - Expliquer pourquoi une position peut accélérer certaines opérations
    - Comprendre pourquoi Java n'expose pas les positions nativement
    - Faire le lien avec les itérateurs

!!! warning "Pièges classiques"

    - Confondre position et index
    - Penser qu'un itérateur est une position persistante
    - Croire que Java « a oublié » les positions
    - Ne pas voir le lien avec la protection des invariants

:material-arrow-right-circle: [Démonstration 2 — Listes et listes positionnelles](exercices/demo2.md)

### 2.3 Favorite list (§7.7)

!!! abstract "Objectifs"

    - Adapter une structure à un objectif précis
    - Identifier les opérations dominantes
    - Comprendre le compromis insertion vs consultation

!!! warning "Pièges classiques"

    - Utiliser une structure générique par habitude
    - Optimiser la mauvaise opération
    - Penser qu'un tri fréquent est toujours acceptable

:material-arrow-right-circle: [Démonstration 3 — Piles, Files, Deques et Liste de Favoris](exercices/demo3.md)

### 2.4 ADT Stack, Deque et Queue (§ch. 6)

!!! abstract "Objectifs"

    - Comprendre les **disciplines d'accès** (LIFO, FIFO)
    - Implémenter stack et queue à partir de Deque
    - Distinguer interface, discipline et structure sous-jacente

!!! warning "Pièges classiques"

    - Utiliser `Stack` (classe legacy Java)
    - Mélanger LIFO et FIFO
    - Penser que stack et queue sont fondamentalement différentes
    - Confondre structure et politique d'accès

:material-arrow-right-circle: [Démonstration 3 — Piles, Files, Deques et Liste de Favoris](exercices/demo3.md)

### 2.5 Files avec plusieurs threads (concurrence)

!!! abstract "Objectifs"

    - Comprendre l'impact de la concurrence sur les structures de données
    - Distinguer bloquant / non bloquant et thread-safe / non thread-safe
    - Expliquer pourquoi certaines structures changent en contexte concurrent

!!! warning "Pièges classiques"

    - Croire que `synchronized` suffit dans tous les cas
    - Ignorer le coût de la synchronisation
    - Penser qu'une structure thread-safe est toujours préférable
    - Oublier les interblocages et la contention

### 2.6 Files avec priorités — Heap et priorités adaptables (§ch. 9)

!!! abstract "Objectifs"

    - Comprendre l'**invariant du tas**
    - Utiliser un heap pour la sélection et l'ordonnancement
    - Comparer heap, liste triée et tri global

!!! warning "Pièges classiques"

    - Confondre heap et arbre binaire de recherche
    - Croire qu'un heap est totalement trié
    - Sous-estimer le coût de mise à jour des priorités
    - Mal comprendre les priorités adaptables

:material-arrow-right-circle: [Démonstration 4 — Files avec priorités et Tas](exercices/demo4.md)

---

## 3. Graphes I (3h)

!!! abstract "Objectifs"

    - Comprendre ce qu'est un graphe formellement (§14.1, 14.2 sauf 14.2.3)
    - Maîtriser les définitions et l'ADT Graph
    - Comparer les représentations : matrice, liste d'arêtes, liste d'adjacence
    - Choisir une représentation selon la densité et le type d'algorithme

!!! warning "Pièges classiques"

    - Confondre graphe et arbre
    - Utiliser une matrice d'adjacence par défaut
    - Oublier le coût mémoire de la matrice
    - Ignorer la distinction graphe orienté vs non orienté

:material-arrow-right-circle: [Démonstration 5 — Révision Mi-Session](exercices/demo5.md)

---

## 4. Arbres de recherche et tables (8h)

### 4.1 Arbres et Trie (§8.1, 13.3)

!!! abstract "Objectifs"

    - Maîtriser les définitions et la terminologie des arbres
    - Comprendre l'ADT Tree
    - Comprendre le Trie et ses avantages pour la recherche par préfixe

:material-arrow-right-circle: [Démonstration 7 — BST, Maps ordonnées et Graphes II](exercices/demo7.md)

### 4.2 Arbres binaires de recherche (BST) (§8.3, 8.4)

!!! abstract "Objectifs"

    - Maîtriser l'arbre binaire et l'ADT BinaryTree
    - Comprendre et implémenter les parcours (infixe, préfixe, postfixe)
    - Analyser les opérations BST et leurs complexités

!!! warning "Pièges classiques"

    - Supposer qu'un BST est toujours équilibré
    - Confondre hauteur et taille
    - Oublier les cas dégénérés (insertions dans l'ordre trié)
    - Mal gérer la suppression d'un nœud à deux enfants

:material-arrow-right-circle: [Démonstration 7 — BST, Maps ordonnées et Graphes II](exercices/demo7.md)

### 4.3 ADT Map et table de hachage (§10.1, 10.2)

!!! abstract "Objectifs"

    - Maîtriser l'ADT Map et ses opérations fondamentales
    - Comparer Map ordonnée et Map non ordonnée
    - Comprendre le rôle de la fonction de hachage (code + compression)
    - Distinguer chaînage séparé et adressage ouvert
    - Comprendre le hachage cuckoo et sa garantie $O(1)$ dans le pire cas
    - Saisir les limites du hachage

!!! warning "Pièges classiques"

    - Croire que le hachage est $O(1)$ garanti dans tous les cas
    - Négliger les collisions et leur impact
    - Confondre égalité (`equals`) et identité (`==`)
    - Oublier le coût du redimensionnement

:material-arrow-right-circle: [Démonstration 6 — Maps et Tables de Hachage](exercices/demo6.md)

---

## 5. Graphes II (4h)

!!! abstract "Objectifs"

    - Comprendre la map d'adjacence (§14.2.3)
    - Implémenter et analyser DFS et BFS (§14.3)
    - Relier DFS ↔ pile et BFS ↔ file
    - Analyser les coûts selon la représentation

!!! warning "Pièges classiques"

    - Oublier de marquer les sommets visités
    - Confondre DFS récursif et DFS itératif (ordre de visite différent possible)
    - Mélanger parcours et plus court chemin (BFS ≠ Dijkstra)
    - Mauvaise gestion des graphes non connexes

:material-arrow-right-circle: [Démonstration 7 — BST, Maps ordonnées et Graphes II](exercices/demo7.md)

---

## 6. Map ordonnée — Arbres équilibrés (6h)

!!! abstract "Objectifs"

    - Comprendre pourquoi et comment équilibrer un BST
    - Maîtriser les rotations et la restructuration trinode (§11.2)
    - Comprendre les arbres AVL, Splay et Rouge-Noir (§11.3, 11.4, 11.6)
    - Application : Maximaset avec SortedMap
    - Comparer les trois structures selon le contexte d'usage
    - Relier arbres équilibrés, ensembles triés et multimaps

!!! warning "Pièges classiques"

    - Penser qu'un arbre équilibré est simple à maintenir
    - Confondre le zig-zig splay avec la rotation simple AVL
    - Mémoriser les cas de rotation sans comprendre le déclencheur
    - Ne pas voir le lien entre arbres équilibrés et SortedMap

:material-arrow-right-circle: [Démonstration 8 — Map ordonnée et Arbres de recherche](exercices/demo8.md)
