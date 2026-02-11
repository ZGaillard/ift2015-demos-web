# Démonstration 5 : Révision Mi-Session

Cette démonstration est une **révision complète** couvrant les chapitres 6, 7, 9 et les sections 14.1-14.2 du livre *Data Structures and Algorithms in Java (6th ed.)*.

!!! abstract "Objectifs de la révision"

    À la fin de cette démonstration, vous devriez être capable de :

    * **Distinguer** les différentes structures de données et leurs compromis
    * **Choisir** la structure appropriée selon le contexte et les opérations dominantes
    * **Analyser** la complexité temporelle et spatiale des opérations
    * **Identifier** les pièges classiques et les misconceptions courantes
    * **Appliquer** vos connaissances à des scénarios réalistes
    * **Comparer** les représentations de graphes selon la densité et les opérations

---

## Matière couverte

| Thème | Contenu | Chapitres |
|-------|---------|-----------|
| **Introduction** | Structure = choix algorithmique, invariants, coûts cachés | — |
| **Listes** | ArrayList, LinkedList, listes positionnelles | 7.1, 7.2, 7.3-7.6 |
| **Liste de favoris** | Tri par fréquence, heuristique move-to-front | 7.7 |
| **Piles, Files, Deques** | LIFO, FIFO, implémentations, files circulaires | 6 |
| **Concurrence** | Files thread-safe, bloquantes, synchronisation | — |
| **Files à priorité** | Heaps, propriétés, priorités adaptables | 9 |
| **Graphes** | Définitions, ADT, représentations | 14.1, 14.2 |

---

## Première heure : Concepts et Rappels

### Section 1 — Vrai ou Faux thématique

Pour chaque énoncé, indiquez s'il est **vrai** ou **faux** et justifiez votre réponse.

#### Bloc A — Fondations et Philosophie

??? question "Question 1 — Complexité et longueur du code"
    Un algorithme avec plus de lignes de code est nécessairement plus lent qu'un algorithme plus court résolvant le même problème.

    ??? success "Réponse"
        **Faux.** La complexité algorithmique dépend du **nombre d'opérations en fonction de la taille de l'entrée**, pas du nombre de lignes de code.

        Exemple : Un tri par insertion (code court) est O(n²), tandis qu'un tri fusion (code plus long) est O(n log n). Pour de grandes entrées, le code plus long est beaucoup plus rapide.

        **Piège classique :** Penser que "plus de code = plus lent". La complexité asymptotique prime sur la longueur du code.

??? question "Question 2 — Coût des invariants"
    Maintenir un invariant (par exemple, garder une liste triée) a toujours un coût négligeable comparé aux bénéfices qu'il apporte.

    ??? success "Réponse"
        **Faux.** Le coût de maintenance d'un invariant peut être significatif et doit être mis en balance avec les bénéfices.

        Exemple : Une liste triée permet `min()` en O(1), mais chaque insertion devient O(n) pour maintenir l'ordre. Si les insertions sont fréquentes et les consultations du minimum rares, ce coût est prohibitif.

        **Le bon choix dépend des opérations dominantes.** C'est pourquoi on compare :

        | Structure | `insert` | `min` | Quand l'utiliser |
        |-----------|----------|-------|------------------|
        | Liste non triée | O(1) | O(n) | Peu de consultations |
        | Liste triée | O(n) | O(1) | Peu d'insertions |
        | Heap | O(log n) | O(1) | Usage équilibré |

??? question "Question 3 — Complexité amortie"
    La complexité amortie O(1) garantit que chaque opération individuelle prend un temps constant.

    ??? success "Réponse"
        **Faux.** La complexité **amortie** O(1) signifie que sur une longue séquence de n opérations, le coût total est O(n), donc **en moyenne** chaque opération coûte O(1).

        Cependant, certaines opérations individuelles peuvent prendre O(n). Par exemple, `ArrayList.add()` :

        - La plupart des ajouts : O(1)
        - Quand le tableau est plein : O(n) pour redimensionner et copier

        **Piège classique :** Confondre complexité amortie et complexité du pire cas. L'amortie est une moyenne, pas une garantie par opération.

---

#### Bloc B — Structures Linéaires

??? question "Question 4 — LinkedList et insertions"
    Pour une liste de 10 000 éléments avec des insertions fréquentes au milieu, `LinkedList` est toujours plus performante qu'`ArrayList`.

    ??? success "Réponse"
        **Faux.** Cette affirmation ignore un coût crucial : **trouver la position d'insertion**.

        Pour insérer au milieu :

        | Structure | Trouver la position | Insérer | Total |
        |-----------|---------------------|---------|-------|
        | ArrayList | O(1) par index | O(n) décalage | O(n) |
        | LinkedList | O(n) parcours | O(1) relinkage | O(n) |

        Les deux sont O(n) ! De plus, `ArrayList` bénéficie d'une meilleure **localité de cache** (éléments contigus en mémoire), ce qui la rend souvent plus rapide en pratique.

        **LinkedList est avantageuse seulement si vous avez déjà une référence à la position** (via un itérateur ou une position dans une liste positionnelle).

??? question "Question 5 — Optimisation de get(i) dans LinkedList"
    Dans une `LinkedList` Java, l'appel `list.get(n/2)` est optimisé pour partir du milieu de la liste.

    ??? success "Réponse"
        **Faux.** Java optimise en partant du **début ou de la fin** selon l'index :

        - Si `i < size/2` : parcourt depuis le début
        - Si `i >= size/2` : parcourt depuis la fin

        Mais il n'y a **pas d'accès direct au milieu**. Pour `get(n/2)`, Java parcourt environ n/2 éléments depuis une extrémité.

        ```java
        // Complexité de get(i) dans LinkedList
        // Meilleur cas : O(1) pour i=0 ou i=size-1
        // Pire cas : O(n/2) = O(n) pour i≈n/2
        ```

        **Piège classique :** Sous-estimer le coût de `get(i)` dans une liste chaînée. C'est toujours O(n) dans le pire cas, jamais O(1).

??? question "Question 6 — Stabilité des positions"
    Une `Position` dans une liste positionnelle devient invalide si on insère un élément juste avant elle.

    ??? success "Réponse"
        **Faux.** C'est précisément l'avantage des positions sur les indices !

        - **Index** : Si j'insère avant l'index 5, l'élément qui était à l'index 5 est maintenant à l'index 6. L'index 5 pointe vers un autre élément.

        - **Position** : Une position représente un emplacement **stable** dans la structure. L'insertion d'autres éléments ne l'affecte pas.

        Une position devient invalide **uniquement** lorsque **son propre élément** est supprimé de la liste.

        **Piège classique :** Confondre position et index. Une position est une référence stable au conteneur (nœud), pas à un rang numérique.

??? question "Question 7 — Positions dans les collections Java"
    Java n'expose pas les positions dans ses collections standard car c'est un oubli de conception qui sera corrigé dans une future version.

    ??? success "Réponse"
        **Faux.** C'est un **choix de conception délibéré** pour protéger les invariants.

        Si Java exposait les nœuds internes (`Position`), un utilisateur pourrait :

        - Garder une référence à un nœud supprimé
        - Modifier directement les liens `next`/`prev`
        - Corrompre la structure de données

        Java préfère exposer des **itérateurs** (qui sont invalidés après modification) plutôt que des positions persistantes. C'est un compromis entre flexibilité et sécurité.

        Les listes positionnelles sont utiles quand on **contrôle** l'environnement et qu'on a besoin de performances O(1) pour les insertions/suppressions avec positions connues.

??? question "Question 8 — La classe Stack de Java"
    La classe `java.util.Stack` est recommandée pour implémenter une pile dans du code moderne.

    ??? success "Réponse"
        **Faux.** `java.util.Stack` est une classe **legacy** (héritée de Java 1.0) qui pose plusieurs problèmes :

        1. Elle hérite de `Vector`, donc toutes ses méthodes sont `synchronized` (surcoût inutile en contexte mono-thread)
        2. Elle expose des méthodes de `Vector` qui violent le principe LIFO (`add(index, element)`, `remove(index)`)

        **Recommandation officielle** (Javadoc) :

        ```java
        // À éviter
        Stack<String> stack = new Stack<>();

        // Recommandé
        Deque<String> stack = new ArrayDeque<>();
        stack.push("A");
        stack.pop();
        ```

        `ArrayDeque` est plus rapide et respecte strictement l'interface de pile.

---

#### Bloc C — Files et Priorités

??? question "Question 9 — Move-to-front vs liste triée"
    L'heuristique move-to-front est toujours supérieure à une liste triée par fréquence car `access(e)` est O(n) dans les deux cas.

    ??? success "Réponse"
        **Faux.** Bien que `access(e)` soit O(n) dans les deux cas (recherche linéaire), les performances diffèrent pour **`getFavorites(k)`** :

        | Structure | `access(e)` | `getFavorites(k)` |
        |-----------|-------------|-------------------|
        | Liste triée par fréquence | O(n) | **O(k)** — les k premiers sont déjà les favoris |
        | Move-to-front | O(n) | **O(kn)** — doit parcourir pour trouver les k maximums |

        Move-to-front est avantageux quand il y a **localité temporelle** (éléments récemment accédés susceptibles d'être réaccédés). Sinon, la liste triée est meilleure pour `getFavorites`.

        **Piège classique :** Optimiser la mauvaise opération. Analysez quelles opérations sont **dominantes** avant de choisir.

??? question "Question 10 — Synchronisation des files"
    Ajouter `synchronized` à toutes les méthodes d'une `Queue` la rend thread-safe et performante pour un usage concurrent intensif.

    ??? success "Réponse"
        **Faux.** `synchronized` rend la structure thread-safe, mais **pas performante** sous forte contention.

        Problèmes :

        1. **Contention** : Un seul thread peut accéder à la file à la fois. Les autres attendent.
        2. **Pas de blocage intelligent** : Si la file est vide, `dequeue` doit faire du polling (boucle active) ou retourner null.

        Solution : Utiliser `java.util.concurrent.BlockingQueue` :

        ```java
        BlockingQueue<Task> queue = new ArrayBlockingQueue<>(100);

        // Producteur : bloque si plein
        queue.put(task);

        // Consommateur : bloque si vide
        Task task = queue.take();
        ```

        **Piège classique :** Croire que `synchronized` suffit. Les structures concurrentes spécialisées sont conçues pour minimiser la contention.

??? question "Question 11 — Position du deuxième minimum dans un heap"
    Dans un min-heap, le deuxième plus petit élément est toujours à l'index 1 du tableau.

    ??? success "Réponse"
        **Faux.** Le deuxième plus petit élément est à l'index 1 **ou** 2 (l'un des deux enfants de la racine).

        L'invariant du heap garantit seulement que **chaque parent est ≤ ses enfants**. Il n'y a aucune relation d'ordre entre les enfants (index 1 et 2).

        ```
        Heap valide où le 2e minimum est à l'index 2 :

              1           Tableau : [1, 5, 2, 7, 8, 3, 4]
            /   \
           5     2   ← 2 est le 2e minimum, à l'index 2
          / \   / \
         7   8 3   4
        ```

        **Piège classique :** Croire qu'un heap est trié. Seule la relation parent-enfant est garantie.

??? question "Question 12 — Heap vs arbre binaire de recherche"
    Un heap binaire et un arbre binaire de recherche (BST) ont la même propriété d'ordre.

    ??? success "Réponse"
        **Faux.** Les propriétés d'ordre sont fondamentalement différentes :

        | Propriété | Heap (min) | BST |
        |-----------|------------|-----|
        | Relation | parent ≤ enfants | gauche < parent < droite |
        | Minimum | Racine (O(1)) | Nœud le plus à gauche (O(log n)) |
        | Recherche par clé | O(n) | O(log n) si équilibré |
        | Structure | Arbre complet (tableau) | Forme variable |

        ```
              Heap                    BST
               2                       5
             /   \                   /   \
            5     3                 3     7
           / \                     / \
          7   8                   2   4

        Heap: 5 > 3 mais 5 est à gauche (OK)
        BST: gauche < parent < droite (toujours)
        ```

        **Piège classique très fréquent :** Confondre ces deux structures. Un heap n'est PAS un BST !

---

#### Bloc D — Graphes

??? question "Question 13 — Arbres et graphes"
    Tout arbre est un graphe, mais tout graphe n'est pas un arbre.

    ??? success "Réponse"
        **Vrai.** Un arbre est un cas particulier de graphe avec des contraintes supplémentaires :

        **Définition d'un arbre** : Graphe connexe et acyclique.

        - **Connexe** : Il existe un chemin entre toute paire de sommets
        - **Acyclique** : Pas de cycle

        Propriétés dérivées (pour un arbre de n sommets) :

        - Exactement n-1 arêtes
        - Un seul chemin entre chaque paire de sommets

        ```
              Arbre                  Graphe (pas un arbre)
                A                          A
               /|\                        /|\
              B C D                      B-C-D
                                          \_/
                                        (cycle!)
        ```

        **Attention :** En théorie des graphes, on parle d'arbre non-enraciné. En structures de données, on travaille souvent avec des arbres enracinés (avec une racine désignée).

??? question "Question 14 — Matrice vs liste d'adjacence"
    Pour un graphe de 1000 sommets et 3000 arêtes, une matrice d'adjacence utilise moins de mémoire qu'une liste d'adjacence.

    ??? success "Réponse"
        **Faux.** Calculons :

        **Matrice d'adjacence** :

        - Taille : V × V = 1000 × 1000 = **1 000 000 entrées**
        - Même pour un graphe creux, toutes les cases existent

        **Liste d'adjacence** :

        - Chaque arête apparaît dans 2 listes (si non-orienté) : 3000 × 2 = **6000 entrées**
        - Plus les V = 1000 têtes de liste

        Ratio : 1 000 000 / 6000 ≈ **166× plus de mémoire pour la matrice !**

        **Règle pratique** :

        - Graphe **dense** (E ≈ V²) → Matrice
        - Graphe **creux** (E << V²) → Liste d'adjacence

        Ici, E = 3000 et V² = 1 000 000, donc E << V² : le graphe est creux.

        **Piège classique :** Utiliser une matrice par défaut. Analysez toujours la densité !

??? question "Question 15 — Nombre maximum d'arêtes"
    Un graphe non-orienté avec n sommets peut avoir au maximum n(n-1) arêtes.

    ??? success "Réponse"
        **Faux.** Le maximum est **n(n-1)/2** pour un graphe non-orienté simple (sans boucles ni arêtes multiples).

        **Graphe non-orienté** : Une arête {u, v} est la même que {v, u}.

        - Nombre de paires possibles : C(n,2) = n(n-1)/2

        **Graphe orienté** : Un arc (u, v) est différent de (v, u).

        - Nombre d'arcs possibles (sans boucles) : n(n-1)

        | Type | Maximum d'arêtes/arcs |
        |------|----------------------|
        | Non-orienté simple | n(n-1)/2 |
        | Orienté simple | n(n-1) |
        | Avec boucles | Ajouter n aux valeurs ci-dessus |

        **Piège classique :** Ignorer la distinction orienté vs non-orienté. Elle change le nombre maximum d'arêtes par un facteur 2.

??? question "Question 16 — Liste d'arêtes et voisinage"
    Dans une représentation par liste d'arêtes (edge list), l'opération `incidentEdges(v)` (lister les arêtes incidentes à v) s'exécute en O(deg(v)).

    ??? success "Réponse"
        **Faux.** Dans une liste d'arêtes, `incidentEdges(v)` nécessite de **parcourir toutes les arêtes** pour trouver celles incidentes à v, ce qui donne **O(m)** où m est le nombre total d'arêtes.

        C'est la principale faiblesse de la liste d'arêtes par rapport à la liste d'adjacence :

        | Opération | Liste d'arêtes | Liste d'adjacence |
        |-----------|---------------|-------------------|
        | `incidentEdges(v)` | **O(m)** | **O(deg(v))** |
        | `areAdjacent(v, w)` | **O(m)** | **O(min(deg(v), deg(w)))** |
        | `insertEdge(e)` | **O(1)** | **O(1)** |
        | `removeEdge(e)` | **O(1)** | **O(1)** |
        | Espace | O(n + m) | O(n + m) |

        La liste d'arêtes est la structure la plus **simple** mais la moins performante pour les requêtes de voisinage. Elle est utile quand on a besoin principalement d'itérer sur toutes les arêtes.

        **Piège classique :** Confondre liste d'arêtes et liste d'adjacence. La liste d'arêtes ne stocke **pas** les arêtes par sommet, mais dans une collection globale.

---

### Section 2 — QCM Comparatifs

??? question "Question 1 — Tableau de complexité"
    Complétez le tableau de complexité pour une liste de n éléments :

    | Opération | ArrayList | LinkedList | Liste Positionnelle* |
    |-----------|-----------|------------|---------------------|
    | `get(k)` | ? | ? | ? |
    | `add(0, e)` | ? | ? | ? |
    | `add(k, e)` (milieu) | ? | ? | ? |
    | `remove(position)` | ? | ? | ? |

    *Position déjà obtenue au préalable

    ??? success "Réponse"
        | Opération | ArrayList | LinkedList | Liste Positionnelle* |
        |-----------|-----------|------------|---------------------|
        | `get(k)` | **O(1)** | **O(n)** | **O(n)** |
        | `add(0, e)` | **O(n)** | **O(1)** | **O(1)** |
        | `add(k, e)` | **O(n)** | **O(n)** | **O(1)*** |
        | `remove(position)` | **O(n)** | **O(1)*** | **O(1)** |

        *Avec position/itérateur déjà connu(e)

        **Points clés :**

        - `ArrayList` excelle pour l'accès indexé mais souffre pour les insertions/suppressions (décalages)
        - `LinkedList` a un accès O(n) mais insertion/suppression O(1) **si on a déjà la position**
        - La liste positionnelle combine le meilleur des deux **quand on garde les positions**

??? question "Question 2 — Représentations de graphes"
    Un réseau de transport a **500 stations** et **800 connexions** bidirectionnelles. On doit fréquemment lister toutes les stations voisines d'une station donnée et vérifier si deux stations sont directement connectées.

    Quelle représentation est la plus adaptée ?

    - [ ] A) Liste d'arêtes (edge list)
    - [ ] B) Matrice d'adjacence
    - [ ] C) Liste d'adjacence
    - [ ] D) Toutes sont équivalentes

    ??? success "Réponse"
        **C) Liste d'adjacence**

        Analyse pour ce graphe (n = 500, m = 800, graphe creux car m << n²) :

        | Opération | Liste d'arêtes | Matrice d'adjacence | Liste d'adjacence |
        |-----------|---------------|--------------------|--------------------|
        | `incidentEdges(v)` | **O(m)** = O(800) | **O(n)** = O(500) | **O(deg(v))** ≈ O(3) |
        | `areAdjacent(v, w)` | **O(m)** = O(800) | **O(1)** | **O(min(deg))** ≈ O(3) |
        | Espace | O(n + m) = 1 300 | **O(n²)** = 250 000 | O(n + m) = 1 300 |

        - **Liste d'arêtes** : Trop lente pour les requêtes de voisinage — il faut parcourir **toutes** les arêtes à chaque fois
        - **Matrice** : Gaspille 250 000 entrées pour seulement 800 connexions, et `incidentEdges` requiert O(n)
        - **Liste d'adjacence** : Espace compact et opérations proportionnelles au degré

        **Piège classique :** La liste d'arêtes est simple à implémenter mais devient prohibitive dès qu'on fait des requêtes de voisinage fréquentes.

??? question "Question 3 — Analyse de complexité"
    ```java
    void removeNegatives(List<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) < 0) {
                list.remove(i);
                i--;
            }
        }
    }
    ```

    Quelle est la complexité si `list` est une `LinkedList` de n éléments dont k sont négatifs ?

    - [ ] A) O(n)
    - [ ] B) O(n + k)
    - [ ] C) O(n²)
    - [ ] D) O(nk)

    ??? success "Réponse"
        **C) O(n²)**

        Analysons chaque opération dans la boucle :

        - `list.get(i)` : **O(i)** dans une LinkedList (parcours depuis le début ou la fin)
        - `list.remove(i)` : **O(i)** pour trouver + O(1) pour supprimer

        La boucle s'exécute n fois (moins les suppressions, mais restons sur n pour la borne supérieure).

        Coût total : Σ O(i) pour i de 0 à n ≈ O(n²)

        **Pourquoi pas O(nk) ?** Même les `get(i)` sur les éléments positifs coûtent O(i). Ce n'est pas seulement les k suppressions qui coûtent cher.

        **Solution optimale** : Utiliser un itérateur !

        ```java
        void removeNegatives(List<Integer> list) {
            Iterator<Integer> it = list.iterator();
            while (it.hasNext()) {
                if (it.next() < 0) {
                    it.remove();  // O(1) car l'itérateur a déjà la position
                }
            }
        }
        ```

        Complexité avec itérateur : **O(n)**.

??? question "Question 4 — Trace de heap"
    On insère successivement les clés **8, 5, 10, 3, 7, 2** dans un min-heap vide.

    Quel est l'état du tableau après toutes les insertions ?

    - [ ] A) `[2, 3, 5, 8, 7, 10]`
    - [ ] B) `[2, 5, 3, 8, 7, 10]`
    - [ ] C) `[2, 3, 5, 7, 8, 10]`
    - [ ] D) `[2, 5, 3, 10, 7, 8]`

    ??? success "Réponse"
        **B) `[2, 5, 3, 8, 7, 10]`**

        Traçons chaque insertion avec up-heap :

        1. **insert(8)** : `[8]`

        2. **insert(5)** : `[8, 5]` → 5 < 8, swap → `[5, 8]`

        3. **insert(10)** : `[5, 8, 10]` → 10 > 5, OK

        4. **insert(3)** : `[5, 8, 10, 3]`
           - 3 à l'index 3, parent = index 1 (valeur 8)
           - 3 < 8 → swap → `[5, 3, 10, 8]`
           - 3 à l'index 1, parent = index 0 (valeur 5)
           - 3 < 5 → swap → `[3, 5, 10, 8]`

        5. **insert(7)** : `[3, 5, 10, 8, 7]`
           - 7 à l'index 4, parent = index 1 (valeur 5)
           - 7 > 5 → OK

        6. **insert(2)** : `[3, 5, 10, 8, 7, 2]`
           - 2 à l'index 5, parent = index 2 (valeur 10)
           - 2 < 10 → swap → `[3, 5, 2, 8, 7, 10]`
           - 2 à l'index 2, parent = index 0 (valeur 3)
           - 2 < 3 → swap → `[2, 5, 3, 8, 7, 10]`

        **Résultat final : `[2, 5, 3, 8, 7, 10]`** (réponse B)

        ```
              2
            /   \
           5     3
          / \   /
         8   7 10
        ```

??? question "Question 5 — File concurrente"
    Pour un pattern producteur-consommateur avec buffer borné :

    - 4 threads producteurs
    - 2 threads consommateurs
    - Le consommateur doit **bloquer** si le buffer est vide

    Quelle structure Java est la plus appropriée ?

    - [ ] A) LinkedList avec `synchronized`
    - [ ] B) ArrayDeque
    - [ ] C) ArrayBlockingQueue
    - [ ] D) PriorityQueue

    ??? success "Réponse"
        **C) ArrayBlockingQueue**

        Analyse :

        | Option | Thread-safe | Bloquant | Buffer borné | Verdict |
        |--------|-------------|----------|--------------|---------|
        | A) LinkedList + synchronized | ✓ (manuel) | ✗ | ✗ | Insuffisant |
        | B) ArrayDeque | ✗ | ✗ | ✗ | Pas thread-safe |
        | C) ArrayBlockingQueue | ✓ | ✓ | ✓ | **Parfait** |
        | D) PriorityQueue | ✗ | ✗ | ✗ | Pas adapté |

        `ArrayBlockingQueue` offre :

        - `put(e)` : bloque si le buffer est plein
        - `take()` : bloque si le buffer est vide
        - Capacité fixe (buffer borné)
        - Thread-safe par conception

        ```java
        BlockingQueue<Task> buffer = new ArrayBlockingQueue<>(100);

        // Producteur
        buffer.put(task);  // Bloque si plein

        // Consommateur
        Task task = buffer.take();  // Bloque si vide
        ```

??? question "Question 6 — Mémoire des représentations de graphe"
    Un réseau social a **1 million d'utilisateurs**. Chaque utilisateur a en moyenne **200 amis** (arêtes non-orientées).

    Quelle est la mémoire approximative (en entrées) pour chaque représentation ?

    - [ ] A) Matrice : 1 million, Liste : 200 millions
    - [ ] B) Matrice : 1 000 milliards, Liste : 200 millions
    - [ ] C) Matrice : 1 000 milliards, Liste : 400 millions
    - [ ] D) Les deux utilisent la même mémoire

    ??? success "Réponse"
        **B) Matrice : 1 000 milliards, Liste : 200 millions**

        **Matrice d'adjacence** :

        - Taille : V² = (10⁶)² = 10¹² = 1 000 milliards d'entrées
        - Indépendant du nombre d'arêtes !

        **Liste d'adjacence** :

        - Chaque utilisateur stocke sa liste de 200 amis
        - Total des entrées voisins : 10⁶ × 200 = **200 millions**
        - Plus V = 1 million de têtes de liste (négligeable)

        **Ratio** : La matrice utilise environ **5000× plus de mémoire** !

        Ce graphe est très creux : E ≈ 100M vs V² = 10¹². La liste d'adjacence est clairement le bon choix.

??? question "Question 7 — Système Undo/Redo"
    Vous implémentez un système d'annulation (Undo) avec les contraintes :

    - Undo illimité
    - Chaque action peut être "rejouée" (Redo) après Undo
    - Nouvelle action après Undo efface l'historique Redo

    Quelle structure minimale est nécessaire ?

    - [ ] A) Une seule Stack
    - [ ] B) Deux Stacks
    - [ ] C) Une Deque
    - [ ] D) Une Liste positionnelle

    ??? success "Réponse"
        **B) Deux Stacks**

        Le pattern classique utilise deux piles :

        - **undoStack** : actions effectuées (LIFO — dernière action = première à annuler)
        - **redoStack** : actions annulées (LIFO — dernière annulée = première à refaire)

        ```java
        class UndoManager {
            Deque<Action> undoStack = new ArrayDeque<>();
            Deque<Action> redoStack = new ArrayDeque<>();

            void doAction(Action a) {
                a.execute();
                undoStack.push(a);
                redoStack.clear();  // Nouvelle action efface le redo
            }

            void undo() {
                if (!undoStack.isEmpty()) {
                    Action a = undoStack.pop();
                    a.reverse();
                    redoStack.push(a);
                }
            }

            void redo() {
                if (!redoStack.isEmpty()) {
                    Action a = redoStack.pop();
                    a.execute();
                    undoStack.push(a);
                }
            }
        }
        ```

        **Pourquoi pas une Deque ?** Une Deque pourrait techniquement fonctionner, mais elle n'offre aucun avantage et est moins claire conceptuellement. Deux stacks explicites rendent l'intention évidente.

---

### Section 3 — Exercices de Trace

??? question "Exercice 1 — Stack et Queue combinées"
    On a une Stack `S` et une Queue `Q`, toutes deux vides.

    Exécutez les opérations suivantes et donnez l'état final de chaque structure :

    ```
    S.push(1), Q.enqueue(1), S.push(2), Q.enqueue(2),
    S.push(S.pop() + Q.dequeue()), Q.enqueue(S.top()),
    S.pop(), Q.enqueue(3), S.push(Q.dequeue())
    ```

    ??? success "Réponse"
        Traçons étape par étape :

        | Opération | Stack S (bas→haut) | Queue Q (avant→arrière) |
        |-----------|-------------------|------------------------|
        | Initial | `[]` | `[]` |
        | `S.push(1)` | `[1]` | `[]` |
        | `Q.enqueue(1)` | `[1]` | `[1]` |
        | `S.push(2)` | `[1, 2]` | `[1]` |
        | `Q.enqueue(2)` | `[1, 2]` | `[1, 2]` |
        | `S.pop()` retourne 2 | `[1]` | `[1, 2]` |
        | `Q.dequeue()` retourne 1 | `[1]` | `[2]` |
        | `S.push(2+1=3)` | `[1, 3]` | `[2]` |
        | `S.top()` retourne 3 | `[1, 3]` | `[2]` |
        | `Q.enqueue(3)` | `[1, 3]` | `[2, 3]` |
        | `S.pop()` retourne 3 | `[1]` | `[2, 3]` |
        | `Q.enqueue(3)` | `[1]` | `[2, 3, 3]` |
        | `Q.dequeue()` retourne 2 | `[1]` | `[3, 3]` |
        | `S.push(2)` | `[1, 2]` | `[3, 3]` |

        **État final :**

        - **S** = `[1, 2]` (1 au fond, 2 au sommet)
        - **Q** = `[3, 3]` (3 à l'avant, 3 à l'arrière)

??? question "Exercice 2 — Heap removeMin"
    Soit le min-heap représenté par le tableau : `[3, 5, 4, 9, 8, 7, 6]`

    ```
          3
        /   \
       5     4
      / \   / \
     9   8 7   6
    ```

    Exécutez **deux** opérations `removeMin()` successives. Montrez l'état du tableau après chaque opération en détaillant les étapes de down-heap.

    ??? success "Réponse"
        **Premier removeMin() :**

        1. Retirer la racine (3), remplacer par le dernier élément (6)
           - `[6, 5, 4, 9, 8, 7]`

        2. Down-heap depuis la racine :
           - 6 à l'index 0, enfants : 5 (index 1) et 4 (index 2)
           - min(5, 4) = 4, et 6 > 4 → swap avec index 2
           - `[4, 5, 6, 9, 8, 7]`

        3. Continuer down-heap :
           - 6 à l'index 2, enfant : 7 (index 5), pas d'enfant droit
           - 6 < 7 → OK, terminé

        **Après premier removeMin : `[4, 5, 6, 9, 8, 7]`**

        ```
              4
            /   \
           5     6
          / \   /
         9   8 7
        ```

        ---

        **Deuxième removeMin() :**

        1. Retirer la racine (4), remplacer par le dernier élément (7)
           - `[7, 5, 6, 9, 8]`

        2. Down-heap depuis la racine :
           - 7 à l'index 0, enfants : 5 (index 1) et 6 (index 2)
           - min(5, 6) = 5, et 7 > 5 → swap avec index 1
           - `[5, 7, 6, 9, 8]`

        3. Continuer down-heap :
           - 7 à l'index 1, enfants : 9 (index 3) et 8 (index 4)
           - min(9, 8) = 8, et 7 < 8 → OK, terminé

        **Après deuxième removeMin : `[5, 7, 6, 9, 8]`**

        ```
              5
            /   \
           7     6
          / \
         9   8
        ```

??? question "Exercice 3 — Conversion de graphe"
    Soit le graphe **orienté** défini par les arcs : (A→B), (A→C), (B→C), (C→A), (D→B)

    a) Donnez la **liste d'arêtes** (edge list)

    b) Dessinez la **matrice d'adjacence** (4×4)

    c) Donnez la **liste d'adjacence**

    d) Quel est le **degré entrant** et **sortant** de chaque sommet ?

    ??? success "Réponse"
        **a) Liste d'arêtes :**

        ```
        E = [(A,B), (A,C), (B,C), (C,A), (D,B)]
        ```

        Chaque arc est un objet stockant ses deux endpoints (origine, destination). C'est la représentation la plus simple : une collection globale d'arêtes.

        ---

        **b) Matrice d'adjacence :**

        |   | A | B | C | D |
        |---|---|---|---|---|
        | **A** | 0 | 1 | 1 | 0 |
        | **B** | 0 | 0 | 1 | 0 |
        | **C** | 1 | 0 | 0 | 0 |
        | **D** | 0 | 1 | 0 | 0 |

        *Lecture : ligne i, colonne j = 1 si arc (i→j) existe*

        ---

        **c) Liste d'adjacence :**

        ```
        A → [B, C]
        B → [C]
        C → [A]
        D → [B]
        ```

        ---

        **d) Degrés :**

        | Sommet | Degré sortant (out) | Degré entrant (in) |
        |--------|--------------------|--------------------|
        | A | 2 (→B, →C) | 1 (C→) |
        | B | 1 (→C) | 2 (A→, D→) |
        | C | 1 (→A) | 2 (A→, B→) |
        | D | 1 (→B) | 0 |

        **Vérification :** Σ deg_out = Σ deg_in = nombre d'arcs = 5 ✓

---

## Deuxième heure : Application et Synthèse

### Section 4 — Scénarios de Conception

??? question "Scénario 1 — Réseau de capteurs IoT"
    **Contexte** : Un réseau de 200 capteurs environnementaux. Les capteurs communiquent entre eux par liaison radio (portée limitée). Le réseau a environ 600 liaisons bidirectionnelles.

    **Opérations requises** :

    1. Ajouter/retirer un capteur (rare : maintenance)
    2. Ajouter/retirer une liaison quand un capteur change de portée (occasionnel)
    3. Lister toutes les liaisons du réseau pour calculer le coût total de maintenance (fréquent)
    4. Trouver tous les voisins d'un capteur pour le routage de données (très fréquent)

    **Questions** :

    1. Pour l'opération 3 (lister toutes les liaisons), quelle représentation est la plus naturelle ? Complexité ?
    2. Pour l'opération 4 (trouver les voisins), comparez les trois représentations (liste d'arêtes, liste d'adjacence, matrice). Laquelle est la plus efficace ?
    3. Peut-on combiner deux représentations ? Quel est le compromis ?
    4. Ce graphe est-il dense ou creux ? Justifiez et déduisez la représentation à éviter.

    ??? success "Réponse"
        **1. Lister toutes les liaisons — Liste d'arêtes :**

        La **liste d'arêtes** est la plus naturelle pour itérer sur toutes les arêtes :

        - Chaque arête est un objet stocké dans une collection
        - Itération : **O(m)** — on parcourt directement la liste

        Avec une liste d'adjacence, il faut parcourir tous les sommets et leurs listes de voisins, ce qui donne aussi O(n + m) mais avec plus d'overhead. De plus, chaque arête apparaît dans deux listes de voisins, nécessitant un mécanisme pour éviter les doublons.

        Avec une matrice, il faut scanner toute la matrice O(n²) pour trouver les entrées non-nulles, ce qui est bien pire.

        ```
        Liste d'arêtes :
        [(A,B), (A,C), (B,D), (C,D), (D,E), ...]
        → Parcours direct en O(m)
        ```

        ---

        **2. Trouver les voisins — Liste d'adjacence :**

        | Représentation | `incidentEdges(v)` | Coût pour ce réseau |
        |---------------|-------------------|---------------------|
        | Liste d'arêtes | O(m) | O(600) à chaque requête |
        | Matrice d'adjacence | O(n) | O(200) |
        | **Liste d'adjacence** | **O(deg(v))** | **O(6)** en moyenne |

        La liste d'adjacence est **100× plus efficace** que la liste d'arêtes pour cette opération, car le degré moyen est 2m/n = 1200/200 = 6.

        ---

        **3. Combinaison de représentations :**

        Oui, on peut maintenir **deux représentations simultanément** :

        - **Liste d'arêtes** pour l'opération 3 (itération globale sur les arêtes)
        - **Liste d'adjacence** pour l'opération 4 (voisinage rapide)

        **Compromis** :

        | Aspect | Une seule structure | Deux structures |
        |--------|-------------------|-----------------|
        | Espace | O(n + m) | O(n + m) × 2 |
        | Insertion d'arête | O(1) | O(1) × 2 opérations |
        | Cohérence | Automatique | Doit maintenir les deux en synchronisation |

        Le surcoût est acceptable si les deux opérations sont fréquentes. C'est d'ailleurs l'approche recommandée dans le livre (Section 14.2).

        ---

        **4. Dense ou creux ?**

        - n = 200, m = 600
        - Maximum d'arêtes (non-orienté) : n(n-1)/2 = 19 900
        - Ratio : 600 / 19 900 ≈ **3%** → **graphe creux**

        **Représentation à éviter** : La **matrice d'adjacence** qui utiliserait 200² = 40 000 entrées pour seulement 600 arêtes (66× plus de mémoire que nécessaire).

??? question "Scénario 2 — Système de contrôle de versions"
    **Contexte** : Un système Git-like pour gérer l'historique d'un projet.

    **Opérations requises** :

    1. `commit` : sauvegarder l'état actuel
    2. `checkout` : revenir à un commit précédent
    3. `branch` : créer une branche à partir du commit courant
    4. `log` : afficher l'historique depuis le commit courant jusqu'au commit initial

    **Questions** :

    1. Pour un historique linéaire (sans branches), quelle structure ? Stack ? Liste ?
    2. L'ajout de branches transforme la structure en quoi ?
    3. Quelle représentation pour ce graphe ? Justifiez.
    4. L'opération `log` correspond à quel parcours ? Complexité ?

    ??? success "Réponse"
        **1. Historique linéaire :**

        Une **liste simplement chaînée** où chaque commit pointe vers son parent :

        ```
        HEAD → C3 → C2 → C1 → null
        ```

        Pourquoi pas une Stack ?

        - Une stack ne permet pas de naviguer dans l'historique (on ne peut que pop)
        - On a besoin de parcourir l'historique (log) sans le détruire
        - Checkout nécessite d'accéder à un commit arbitraire

        Chaque commit stocke :

        ```java
        class Commit {
            String id;
            Commit parent;
            Snapshot content;
            String message;
        }
        ```

        ---

        **2. Avec branches — DAG (Directed Acyclic Graph) :**

        Les branches créent un **graphe orienté acyclique** (DAG) :

        ```
                    C5 (feature)
                   /
        C1 ← C2 ← C3 ← C4 (main)
        ```

        - **Orienté** : chaque commit pointe vers son(ses) parent(s)
        - **Acyclique** : un commit ne peut pas être son propre ancêtre
        - **Pas un arbre** : les merges créent des nœuds avec 2 parents

        ```java
        class Commit {
            String id;
            List<Commit> parents;  // 1 parent normalement, 2 pour un merge
            // ...
        }
        ```

        ---

        **3. Représentation — Liste d'adjacence :**

        Chaque commit stocke directement ses références aux parents (liste d'adjacence implicite).

        Pourquoi pas une matrice ?

        - Graphe très creux : chaque sommet a 1-2 arcs sortants seulement
        - Nombre de commits peut être très grand (millions)
        - Matrice V² serait énorme et majoritairement vide

        ---

        **4. Opération log — Parcours :**

        `log` effectue un **parcours des ancêtres** depuis HEAD :

        ```java
        void log(Commit head) {
            Commit current = head;
            while (current != null) {
                print(current);
                current = current.parent;  // Cas linéaire
            }
        }
        ```

        Avec branches et merges, c'est un **parcours en largeur (BFS)** ou **DFS** sur le DAG des ancêtres, avec détection des commits déjà visités.

        **Complexité** : O(n) où n = nombre de commits ancêtres, car chaque commit est visité une seule fois.

??? question "Scénario 3 — Gestion d'une salle d'urgences"
    **Contexte** : Une salle d'urgences doit gérer la file d'attente de patients. Environ 50 patients attendent en permanence aux heures de pointe.

    **Opérations requises** :

    1. Nouveau patient arrive avec un score de sévérité (1 = mineur, 10 = critique) — fréquent
    2. Appeler le prochain patient à traiter (le plus urgent) — fréquent
    3. La condition d'un patient change pendant l'attente (mise à jour du score) — occasionnel
    4. Patient quitte avant d'être traité — rare

    **Questions** :

    1. Une file FIFO simple convient-elle ? Pourquoi ?
    2. Quelle structure de données est la plus adaptée pour les opérations 1 et 2 ? Complexité ?
    3. L'opération 3 nécessite de modifier la priorité d'un élément déjà dans la structure. Quelle variante de votre structure le supporte ? Complexité ?
    4. Comparez l'approche heap avec l'approche liste triée pour ce scénario.

    ??? success "Réponse"
        **1. File FIFO insuffisante :**

        Non, une FIFO traite les patients par **ordre d'arrivée**, pas par urgence. Un patient critique arrivé en dernier devrait être traité avant un patient mineur arrivé en premier.

        Exception : à sévérité égale, l'ordre d'arrivée est un bon critère de départage (FIFO au sein de chaque niveau de priorité).

        ---

        **2. File à priorité (heap) :**

        Un **max-heap** basé sur le score de sévérité :

        ```java
        PriorityQueue<Patient> urgences = new PriorityQueue<>(
            Comparator.comparingInt(Patient::severity).reversed()  // Max-heap
        );
        ```

        | Opération | Complexité |
        |-----------|------------|
        | Insertion (nouveau patient) | O(log n) |
        | Extraction du max (prochain patient) | O(log n) |

        Avec n ≈ 50, log₂(50) ≈ 6. Chaque opération est très rapide.

        ---

        **3. File à priorité adaptable :**

        Une **file à priorité adaptable** (Section 9.5 du livre) permet de modifier la clé d'une entrée existante :

        ```java
        // Avec file adaptable
        Entry<Integer, Patient> entry = pq.insert(severity, patient);

        // Plus tard, si la condition change :
        pq.replaceKey(entry, newSeverity);  // O(log n)
        ```

        Le mécanisme repose sur un **locator** : chaque entrée connaît sa position dans le heap, permettant un accès direct pour la mise à jour.

        | Opération | Heap simple | Heap adaptable |
        |-----------|-------------|----------------|
        | `insert` | O(log n) | O(log n) |
        | `removeMin/Max` | O(log n) | O(log n) |
        | `replaceKey` | **O(n)** (chercher + restructurer) | **O(log n)** |
        | `remove(entry)` | **O(n)** | **O(log n)** |

        Sans file adaptable, modifier la priorité nécessite de parcourir tout le heap pour trouver l'élément : O(n).

        ---

        **4. Heap vs liste triée :**

        | Critère | Heap | Liste triée |
        |---------|------|-------------|
        | Insertion | **O(log n)** | O(n) — trouver la position |
        | Extraction du max | **O(log n)** | **O(1)** — dernier élément |
        | Mise à jour priorité | **O(log n)** (adaptable) | O(n) — repositionner |
        | Espace | O(n) tableau | O(n) |

        Pour ce scénario avec insertions et extractions fréquentes, le **heap est supérieur**. La liste triée n'est avantageuse que si les extractions sont beaucoup plus fréquentes que les insertions.

---

### Section 5 — Exercices de Code

??? question "Exercice A — Analyse et correction"
    Le code suivant implémente une détection de cycle dans un graphe orienté. Il contient **au moins deux erreurs** (logique ou performance).

    Identifiez-les et proposez des corrections.

    ```java
    public class CycleDetector {
        private Map<Integer, List<Integer>> adjList;

        public boolean hasCycle() {
            Set<Integer> visited = new HashSet<>();
            for (Integer vertex : adjList.keySet()) {
                if (hasCycleFrom(vertex, visited)) {
                    return true;
                }
            }
            return false;
        }

        private boolean hasCycleFrom(Integer current, Set<Integer> visited) {
            if (visited.contains(current)) {
                return true;  // Cycle trouvé!
            }
            visited.add(current);

            for (Integer neighbor : adjList.get(current)) {
                if (hasCycleFrom(neighbor, visited)) {
                    return true;
                }
            }
            return false;
        }
    }
    ```

    **Questions** :

    1. Identifiez les erreurs et expliquez pourquoi elles sont problématiques.
    2. Corrigez le code.
    3. Quelle est la complexité de l'algorithme corrigé ?

    ??? success "Réponse"
        **Erreur 1 : Faux positifs — confusion entre "visité globalement" et "dans le chemin actuel"**

        Le code actuel marque un sommet comme visité et ne le démarque jamais. Cela cause des **faux positifs** :

        ```
        A → B → C
        A → C
        ```

        1. On explore A → B → C, marquant tous comme visités
        2. On revient à A, on essaie A → C
        3. C est déjà visité → **faux positif !** (ce n'est pas un cycle)

        Un cycle existe seulement si on revisite un sommet **dans le chemin actuel** (pile de récursion), pas juste un sommet déjà exploré.

        ---

        **Erreur 2 : Travail redondant**

        Sans distinction entre "en cours d'exploration" et "complètement exploré", on ré-explore des sous-graphes déjà analysés, causant une complexité exponentielle dans le pire cas.

        ---

        **Code corrigé :**

        ```java
        public class CycleDetector {
            private Map<Integer, List<Integer>> adjList;

            public boolean hasCycle() {
                Set<Integer> visited = new HashSet<>();      // Exploration terminée
                Set<Integer> inStack = new HashSet<>();      // Dans le chemin actuel

                for (Integer vertex : adjList.keySet()) {
                    if (!visited.contains(vertex)) {
                        if (hasCycleFrom(vertex, visited, inStack)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            private boolean hasCycleFrom(Integer current,
                                         Set<Integer> visited,
                                         Set<Integer> inStack) {
                visited.add(current);
                inStack.add(current);  // Entrer dans le chemin

                for (Integer neighbor : adjList.getOrDefault(current, List.of())) {
                    if (inStack.contains(neighbor)) {
                        return true;  // Cycle : on revient sur le chemin actuel
                    }
                    if (!visited.contains(neighbor)) {
                        if (hasCycleFrom(neighbor, visited, inStack)) {
                            return true;
                        }
                    }
                }

                inStack.remove(current);  // Sortir du chemin
                return false;
            }
        }
        ```

        ---

        **Complexité de l'algorithme corrigé :**

        - Chaque sommet est visité **une seule fois** grâce au set `visited`
        - Chaque arête est examinée **une seule fois**
        - Complexité : **O(V + E)** où V = sommets, E = arêtes

        C'est optimal pour la détection de cycle dans un graphe orienté.

??? question "Exercice B — Implémentation"
    ```java
    /**
     * Étant donné un tableau d'entiers et un entier k,
     * retournez les k plus grands éléments (ordre quelconque).
     *
     * Contrainte: Complexité O(n log k), pas O(n log n).
     *
     * Indice: Quel type de heap utiliser et de quelle taille?
     */
    public static int[] topK(int[] array, int k) {
        // À implémenter
    }
    ```

    ??? success "Réponse"
        **Idée clé :** Utiliser un **min-heap de taille k**.

        - Parcourir le tableau
        - Si le heap a moins de k éléments, ajouter
        - Sinon, si l'élément actuel > minimum du heap, remplacer le minimum

        À la fin, le heap contient exactement les k plus grands éléments.

        ```java
        public static int[] topK(int[] array, int k) {
            if (k <= 0) return new int[0];
            if (k >= array.length) return array.clone();

            // Min-heap : le plus petit des k plus grands est à la racine
            PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);

            for (int num : array) {
                if (minHeap.size() < k) {
                    minHeap.offer(num);
                } else if (num > minHeap.peek()) {
                    minHeap.poll();   // Retirer le minimum
                    minHeap.offer(num);  // Ajouter le nouveau
                }
            }

            // Convertir en tableau
            int[] result = new int[minHeap.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = minHeap.poll();
            }
            return result;
        }
        ```

        **Analyse de complexité :**

        - Parcours du tableau : n itérations
        - Chaque opération heap (`offer`, `poll`, `peek`) : O(log k)
        - Nombre d'opérations heap : au plus 2n (offer + poll pour chaque élément)
        - **Total : O(n log k)**

        **Pourquoi min-heap et pas max-heap ?**

        Avec un min-heap de taille k :

        - La racine est le **plus petit des k plus grands** (le "seuil")
        - On compare chaque élément au seuil en O(1)
        - Si plus grand, on éjecte le seuil et on insère

        Avec un max-heap, on n'aurait pas accès efficacement au minimum, donc pas de seuil.

        **Comparaison avec le tri :**

        | Approche | Complexité | Pour n=1M, k=10 |
        |----------|------------|-----------------|
        | Tri complet | O(n log n) | ~20M opérations |
        | Min-heap taille k | O(n log k) | ~3.3M opérations |

        Le heap est **6× plus efficace** pour ce cas.

---

### Section 6 — Exercice de Synthèse

??? question "Réseau de distribution d'énergie — Architecture de données"
    Un réseau électrique régional doit gérer :

    **Entités** :

    - **Nœuds** : 500 points (centrales, sous-stations, transformateurs, points de consommation)
    - **Lignes** : 1200 lignes de transmission, chacune avec une capacité maximale (MW) et un coût d'entretien
    - Le réseau est **non-orienté** (l'électricité peut circuler dans les deux sens)

    **Opérations critiques** (par ordre de fréquence) :

    1. **Surveillance** : Pour un nœud donné, lister toutes les lignes connectées et leur charge actuelle (très fréquent)
    2. **Calcul de coût** : Parcourir toutes les lignes pour calculer le coût total de maintenance (quotidien)
    3. **Panne** : Retirer une ligne défectueuse et vérifier si le réseau reste connexe (occasionnel)
    4. **Extension** : Ajouter une nouvelle ligne entre deux nœuds (rare)

    ---

    **Question 1** : Choix de représentation principale

    Comparez les trois représentations (liste d'arêtes, liste d'adjacence, matrice) pour ce réseau. Quelle est la plus adaptée comme structure principale ?

    **Question 2** : Stockage des attributs des lignes

    Chaque ligne a des attributs (capacité, coût, charge actuelle). Comment les stocker dans chaque représentation ?

    **Question 3** : Opération de calcul de coût

    Pour l'opération 2, est-il avantageux de maintenir une liste d'arêtes en plus de la structure principale ? Justifiez.

    **Question 4** : Détection de connexité après panne

    Après le retrait d'une ligne, comment vérifier que le réseau reste connexe ? Quelle représentation facilite cette vérification ?

    ??? success "Réponse"
        **Question 1 : Choix de représentation**

        | Critère | Liste d'arêtes | Liste d'adjacence | Matrice |
        |---------|---------------|-------------------|---------|
        | Espace | O(n + m) = 1 700 | O(n + m) = 1 700 | O(n²) = 250 000 |
        | Op. 1 : voisins d'un nœud | O(m) = O(1200) | **O(deg(v))** ≈ O(5) | O(n) = O(500) |
        | Op. 2 : toutes les lignes | **O(m)** = O(1200) | O(n + m) = O(1700) | O(n²) = O(250 000) |
        | Op. 3 : retirer une ligne | O(1)* | O(deg(v)) | O(1) |
        | Op. 4 : ajouter une ligne | O(1) | O(1) | O(1) |

        **La liste d'adjacence** est la meilleure structure principale car l'opération 1 (la plus fréquente) est O(deg(v)) au lieu de O(m).

        Le graphe est creux : m = 1200 vs n² = 250 000, donc la matrice est à éviter.

        ---

        **Question 2 : Stockage des attributs**

        - **Liste d'arêtes** : Chaque objet Edge stocke directement ses attributs — c'est le plus naturel.

        ```java
        class Edge {
            Vertex u, v;
            double capacity;
            double cost;
            double currentLoad;
        }
        ```

        - **Liste d'adjacence** : Chaque entrée dans la liste de voisins doit référencer l'objet Edge (ou dupliquer les données).

        ```
        A → [(B, edge1), (D, edge2)]  // Référence à l'objet Edge
        ```

        - **Matrice** : Les attributs sont stockés dans les cellules de la matrice au lieu de simples booléens.

        ```
        M[A][B] = Edge(capacity=100, cost=50, load=75)
        ```

        **La liste d'arêtes est la plus naturelle** pour stocker les attributs. C'est un avantage clé de cette représentation.

        ---

        **Question 3 : Maintenir une liste d'arêtes en plus ?**

        **Oui**, c'est avantageux. Avec une liste d'adjacence seule, le calcul de coût total nécessite :

        ```
        Pour chaque sommet v :
            Pour chaque arête incidente à v :
                accumuler le coût
        ```

        Problème : chaque arête est comptée **deux fois** (une par chaque extrémité dans un graphe non-orienté). Il faut soit diviser par 2, soit marquer les arêtes visitées.

        Avec une **liste d'arêtes auxiliaire** :

        ```
        coût_total = 0
        Pour chaque arête e dans la liste d'arêtes :
            coût_total += e.cost
        ```

        Simple, direct, pas de doublons. Complexité : **O(m)**.

        **Compromis** : Maintenir les deux structures en synchronisation lors des ajouts/retraits. Coût supplémentaire : O(1) par opération de modification.

        ---

        **Question 4 : Vérification de connexité**

        Après le retrait d'une arête, on vérifie la connexité par un **parcours** (BFS ou DFS) depuis un sommet quelconque :

        - Si le parcours visite **tous les n sommets** → le réseau est encore connexe
        - Sinon → la suppression a déconnecté le réseau

        **Complexité** : O(n + m) pour le parcours.

        **La liste d'adjacence** facilite cette vérification car le parcours BFS/DFS a besoin de `incidentEdges(v)` à chaque sommet, opération qui est O(deg(v)) avec la liste d'adjacence vs O(m) avec la liste d'arêtes.

        Avec la liste d'arêtes seule, le parcours serait O(n × m) au total — beaucoup trop lent.

        ```
        Parcours BFS pour vérifier la connexité :

        visités = {sommet_départ}
        file = [sommet_départ]
        tant que file non vide :
            v = file.défiler()
            pour chaque voisin w de v :    ← O(deg(v)) avec liste d'adjacence
                si w pas dans visités :
                    visités.ajouter(w)
                    file.enfiler(w)
        return |visités| == n
        ```

---

### Section 7 — Récapitulatif

#### Arbre de décision : Choisir sa structure

```
ACCÈS PRINCIPAL ?
│
├─► Par index/position numérique
│   └─► Modifications fréquentes au milieu ?
│       ├─► Oui, avec position connue → Liste positionnelle
│       ├─► Oui, sans position → LinkedList (attention O(n) recherche!)
│       └─► Non → ArrayList
│
├─► Par priorité
│   └─► Priorités changent après insertion ?
│       ├─► Oui → File à priorité adaptable (heap + locator)
│       └─► Non → Heap simple
│
├─► LIFO (dernier arrivé, premier sorti)
│   └─► Stack (utiliser ArrayDeque)
│
├─► FIFO (premier arrivé, premier sorti)
│   └─► Concurrent ?
│       ├─► Oui + bloquant → BlockingQueue
│       └─► Non → ArrayDeque ou LinkedList
│
└─► Relations entre entités
    └─► GRAPHE
        └─► Opération dominante ?
            ├─► Itérer sur toutes les arêtes → Liste d'arêtes
            ├─► Voisinage / parcours → Liste d'adjacence
            └─► Vérifier adjacence en O(1) → Matrice d'adjacence (si graphe dense)
```

#### Pièges classiques — Résumé

| Piège | Réalité |
|-------|---------|
| LinkedList toujours plus rapide pour insertions | Seulement si on a déjà la position ! `get(i)` = O(n) |
| Heap = tableau trié | Non ! Seule garantie : parent ≤ enfants |
| `synchronized` = thread-safe performant | Thread-safe oui, mais contention sous charge |
| Position = index stable | Position invalide après suppression de **son** élément |
| Liste d'arêtes = rapide pour voisinage | Non ! `incidentEdges(v)` = O(m), pas O(deg(v)) |
| Matrice = choix par défaut pour graphes | Gaspille O(V²) mémoire pour graphes creux |
| Complexité amortie = chaque opération | Non, c'est une moyenne sur n opérations |
| Plus de code = plus lent | La complexité algorithmique prime |

#### Complexités à connaître

| Structure | Insertion | Suppression | Accès/Recherche | Min/Max |
|-----------|-----------|-------------|-----------------|---------|
| ArrayList | O(n)* | O(n) | O(1) par index | O(n) |
| LinkedList | O(1)** | O(1)** | O(n) | O(n) |
| Stack (ArrayDeque) | O(1) | O(1) | O(1) top seulement | — |
| Queue (ArrayDeque) | O(1) | O(1) | O(1) front seulement | — |
| Heap | O(log n) | O(log n) | O(n) | O(1) |
| Heap adaptable | O(log n) | O(log n) | O(1)*** | O(1) |

*O(1) amorti en fin de liste

**Avec position/itérateur déjà connu

***Avec structure de localisation

| Représentation graphe | Espace | Ajouter arête | Vérifier arête | Lister voisins | Lister toutes arêtes |
|-----------------------|--------|---------------|----------------|----------------|---------------------|
| Liste d'arêtes | O(V + E) | O(1) | O(E) | O(E) | **O(E)** |
| Liste d'adjacence | O(V + E) | O(1) | O(deg) | O(deg) | O(V + E) |
| Matrice d'adjacence | O(V²) | O(1) | O(1) | O(V) | O(V²) |

---

## Références

* Goodrich, Tamassia, Goldwasser. *Data Structures and Algorithms in Java*, 6th Edition.
    * Chapitre 6 : Stacks, Queues, and Deques
    * Chapitre 7 : List and Iterator ADTs
    * Chapitre 9 : Priority Queues
    * Chapitre 14.1-14.2 : Graphs
* Documentation Java :
    * [`java.util.Deque`](https://docs.oracle.com/javase/8/docs/api/java/util/Deque.html)
    * [`java.util.PriorityQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html)
    * [`java.util.concurrent.BlockingQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html)
