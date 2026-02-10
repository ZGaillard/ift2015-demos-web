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

??? question "Question 2 — Cache LRU"
    Vous implémentez un cache LRU (Least Recently Used) qui doit supporter ces opérations :

    - Accéder à un élément par clé en O(1)
    - Déplacer l'élément accédé en "tête" (plus récent) en O(1)
    - Évincer le plus ancien élément en O(1)

    Quelle combinaison de structures est optimale ?

    - [ ] A) ArrayList + HashMap
    - [ ] B) LinkedList + HashMap
    - [ ] C) Liste positionnelle doublement chaînée + HashMap<clé, Position>
    - [ ] D) Deux Stacks

    ??? success "Réponse"
        **C) Liste positionnelle doublement chaînée + HashMap<clé, Position>**

        Analyse de chaque option :

        | Option | Accès par clé | Déplacer en tête | Évincer ancien | Verdict |
        |--------|---------------|------------------|----------------|---------|
        | A) ArrayList + HashMap | O(1) | O(n) décalage | O(n) | ✗ |
        | B) LinkedList + HashMap | O(1) | O(n) trouver position | O(1) | ✗ |
        | C) Liste pos. + HashMap | O(1) | O(1) avec position | O(1) | ✓ |
        | D) Deux Stacks | O(n) | O(n) | O(n) | ✗ |

        La clé est de stocker les **positions** dans le HashMap, pas juste les valeurs. Ainsi, après avoir trouvé la position en O(1) via le HashMap, on peut la déplacer en O(1) grâce à la liste positionnelle.

        C'est exactement l'implémentation de `LinkedHashMap` en Java avec `accessOrder=true`.

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

??? question "Question 7 — Priorités adaptables"
    Dans une file à priorité adaptable, `replaceKey(entry, newKey)` est annoncé en O(log n).

    Cette complexité suppose :

    - [ ] A) Que le heap est toujours parfaitement équilibré
    - [ ] B) Qu'on maintient une structure annexe pour localiser l'entrée en O(1)
    - [ ] C) Que la nouvelle clé est toujours plus petite que l'ancienne
    - [ ] D) Qu'on utilise un arbre binaire de recherche plutôt qu'un heap

    ??? success "Réponse"
        **B) Qu'on maintient une structure annexe pour localiser l'entrée en O(1)**

        Sans mécanisme de localisation, trouver une entrée dans un heap nécessite un parcours en **O(n)**.

        Pour atteindre O(log n) pour `replaceKey`, il faut :

        1. **Localiser** l'entrée en O(1) — via un HashMap<Entry, Index> ou en stockant l'index dans l'objet Entry
        2. **Modifier** la clé en O(1)
        3. **Restaurer** l'invariant en O(log n) — up-heap ou down-heap selon que la clé augmente ou diminue

        ```java
        // Structure typique d'une PQ adaptable
        class AdaptablePQ<K, V> {
            Entry[] heap;
            Map<Entry, Integer> locator;  // Localise l'index de chaque entry

            void replaceKey(Entry e, K newKey) {
                int index = locator.get(e);  // O(1)
                K oldKey = e.getKey();
                e.setKey(newKey);
                if (newKey < oldKey) upHeap(index);    // O(log n)
                else downHeap(index);                   // O(log n)
            }
        }
        ```

        **Piège classique :** Oublier le coût de localisation. Sans structure annexe, `replaceKey` serait O(n), pas O(log n).

??? question "Question 8 — Système Undo/Redo"
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

    a) Dessinez la **matrice d'adjacence** (4×4)

    b) Donnez la **liste d'adjacence**

    c) Quel est le **degré entrant** et **sortant** de chaque sommet ?

    ??? success "Réponse"
        **a) Matrice d'adjacence :**

        |   | A | B | C | D |
        |---|---|---|---|---|
        | **A** | 0 | 1 | 1 | 0 |
        | **B** | 0 | 0 | 1 | 0 |
        | **C** | 1 | 0 | 0 | 0 |
        | **D** | 0 | 1 | 0 | 0 |

        *Lecture : ligne i, colonne j = 1 si arc (i→j) existe*

        ---

        **b) Liste d'adjacence :**

        ```
        A → [B, C]
        B → [C]
        C → [A]
        D → [B]
        ```

        ---

        **c) Degrés :**

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

??? question "Scénario 1 — Moteur de recherche documentaire"
    **Contexte** : Un système d'indexation pour 100 000 documents scientifiques. Chaque document contient en moyenne 500 mots-clés uniques.

    **Opérations requises** :

    1. Indexer un nouveau document (rare : ~10/jour)
    2. Rechercher tous les documents contenant un mot-clé (très fréquent : ~1000/min)
    3. Retourner les top-10 résultats par pertinence (score)

    **Questions** :

    1. Quelle structure pour l'index inversé (mot-clé → liste de documents) ?
    2. La relation document-mot-clé forme-t-elle un graphe ? Si oui, lequel ?
    3. Pour les top-10 résultats, comparez : trier toute la liste vs utiliser un heap. Complexité si une recherche retourne m documents ?
    4. Quel invariant maintenez-vous ? Quel est son coût ?

    ??? success "Réponse"
        **1. Structure pour l'index inversé :**

        Un **HashMap<String, List<DocumentInfo>>** où :

        - Clé : mot-clé
        - Valeur : liste des documents contenant ce mot, avec métadonnées (id, score de pertinence)

        ```java
        Map<String, List<DocEntry>> index = new HashMap<>();

        class DocEntry {
            int docId;
            double relevanceScore;  // TF-IDF ou autre
        }
        ```

        Complexité de recherche par mot-clé : **O(1)** pour accéder à la liste.

        ---

        **2. Graphe biparti :**

        Oui, la relation document-mot-clé forme un **graphe biparti** :

        - Ensemble U : documents (100 000 sommets)
        - Ensemble V : mots-clés (nombre variable, disons 50 000 uniques)
        - Arêtes : (doc, mot) si le document contient le mot

        Nombre d'arêtes : ~100 000 × 500 = 50 millions

        Cependant, pour la recherche, l'index inversé (HashMap) est plus efficace qu'un parcours de graphe.

        ---

        **3. Top-10 : Tri vs Heap**

        | Approche | Complexité | Quand l'utiliser |
        |----------|------------|------------------|
        | Trier toute la liste | O(m log m) | Jamais pour top-k avec k << m |
        | **Min-heap de taille k** | **O(m log k)** | Optimal pour top-k |
        | Sélection (QuickSelect) | O(m) en moyenne | Si on n'a pas besoin de l'ordre |

        Pour m = 10 000 documents et k = 10 :

        - Tri : O(10 000 × 13) ≈ 130 000 opérations
        - Heap : O(10 000 × 3) ≈ 30 000 opérations

        **Le heap de taille k est ~4× plus efficace.**

        ```java
        PriorityQueue<DocEntry> topK = new PriorityQueue<>(
            Comparator.comparingDouble(d -> d.score)  // Min-heap
        );
        for (DocEntry doc : results) {
            topK.offer(doc);
            if (topK.size() > k) topK.poll();  // Éjecter le minimum
        }
        ```

        ---

        **4. Invariant et coût :**

        **Invariant** : L'index inversé est toujours à jour (chaque mot-clé pointe vers tous les documents qui le contiennent).

        **Coût de maintenance** :

        - Indexer un nouveau document : O(W) où W = nombre de mots-clés du document (~500)
        - Pour chaque mot : O(1) pour ajouter à la liste du HashMap

        Ce coût est acceptable car l'indexation est rare (10/jour) et la recherche est fréquente (1000/min). On optimise pour l'opération dominante.

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

        En pratique, Git utilise aussi un **HashMap<id, Commit>** pour accéder rapidement à n'importe quel commit par son hash.

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

??? question "Scénario 3 — Matchmaking de jeu en ligne"
    **Contexte** : Un jeu en ligne avec matchmaking basé sur le niveau (ELO). 10 000 joueurs en file simultanément aux heures de pointe.

    **Opérations requises** :

    1. Joueur rejoint la file avec son ELO (très fréquent)
    2. Trouver deux joueurs avec ELO similaire (différence < 100) et les retirer (très fréquent)
    3. Joueur quitte la file avant d'être matché (occasionnel)
    4. ELO d'un joueur change pendant l'attente (rare mais possible)

    **Questions** :

    1. Une simple Queue FIFO suffit-elle ? Pourquoi ?
    2. Proposez une structure qui supporte efficacement les opérations 1-3.
    3. Comment gérer le changement d'ELO (opération 4) ? Quel est le coût ?
    4. Avec 10 000 joueurs et multiples threads serveur, quels problèmes de concurrence anticipez-vous ?

    ??? success "Réponse"
        **1. Queue FIFO insuffisante :**

        Non, une FIFO ne suffit pas car :

        - Le matchmaking cherche des joueurs de **niveau similaire**, pas les plus anciens
        - Trouver deux joueurs avec ELO proche nécessiterait de parcourir toute la file O(n)

        Une FIFO serait injuste : un joueur ELO 2000 attendrait qu'un autre joueur ELO 2000 arrive, même si des joueurs ELO 1000 attendent depuis longtemps.

        ---

        **2. Structure proposée — TreeMap ou structure triée par ELO :**

        ```java
        // TreeMap : ELO → Liste de joueurs avec cet ELO
        TreeMap<Integer, Queue<Player>> waitingByElo = new TreeMap<>();
        ```

        **Opération 1 (rejoindre)** : O(log n)
        ```java
        void join(Player p) {
            waitingByElo.computeIfAbsent(p.elo, k -> new LinkedList<>())
                        .add(p);
        }
        ```

        **Opération 2 (matcher)** : O(log n)
        ```java
        Match findMatch() {
            for (Integer elo : waitingByElo.keySet()) {
                // Chercher un joueur avec ELO dans [elo-100, elo+100]
                SortedMap<Integer, Queue<Player>> range =
                    waitingByElo.subMap(elo - 100, elo + 101);
                // Si deux joueurs trouvés, les retirer et retourner le match
            }
        }
        ```

        **Opération 3 (quitter)** : O(log n) pour trouver + O(k) pour retirer de la queue

        Pour améliorer l'opération 3, on peut ajouter un **HashMap<PlayerId, Position>** pour localiser instantanément un joueur.

        ---

        **3. Changement d'ELO — File à priorité adaptable :**

        Avec un TreeMap simple, changer l'ELO nécessite :

        1. Retirer le joueur de son ancienne position O(log n + k)
        2. Le réinsérer à sa nouvelle position O(log n)

        Avec une **structure adaptable** (ex: TreeMap + HashMap<Player, Entry>) :

        1. Localiser en O(1)
        2. Retirer et réinsérer en O(log n)

        **Coût total : O(log n)** au lieu de O(n) sans localisation.

        ---

        **4. Problèmes de concurrence :**

        - **Race condition** : Deux threads trouvent le même joueur comme "meilleur match" simultanément

        - **Incohérence** : Un joueur est matché pendant qu'un autre thread essaie de le retirer (opération 3)

        - **Contention** : Si tous les threads accèdent au même TreeMap, goulot d'étranglement

        **Solutions** :

        1. **Verrouillage par segment** : Diviser les joueurs par tranche d'ELO (0-500, 500-1000, etc.) avec un verrou par segment

        2. **Structures concurrentes** : `ConcurrentSkipListMap` au lieu de TreeMap

        3. **Atomic operations** : Utiliser `computeIfAbsent`, `remove` atomiques

        4. **Optimistic locking** : Essayer l'opération, réessayer si conflit détecté

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

??? question "Plateforme de livraison — Architecture de données"
    Une application de livraison de repas doit gérer :

    **Entités** :

    - **Restaurants** : id, position (x, y), temps de préparation moyen
    - **Livreurs** : id, position actuelle, statut (disponible/en course), note moyenne
    - **Commandes** : id, restaurant, position client, heure de commande, statut

    **Opérations critiques** (par ordre de fréquence) :

    1. **Nouvelle commande** : trouver les 5 restaurants les plus proches du client
    2. **Assigner livreur** : parmi les livreurs disponibles, trouver le meilleur (proche du restaurant + bonne note)
    3. **Mise à jour position** : livreur envoie sa position GPS toutes les 30 secondes
    4. **Compléter livraison** : marquer commande terminée, libérer livreur

    ---

    **Question 1** : Structure pour les livreurs disponibles

    Proposez une structure pour trouver rapidement le "meilleur" livreur disponible. La note et la distance sont deux critères — comment les combiner ?

    **Question 2** : Gestion des mises à jour de position

    Si un livreur met à jour sa position, son "score" change. Impact sur votre structure ?

    **Question 3** : File des commandes en attente

    Une commande attend qu'un livreur soit disponible. FIFO simple ou avec priorités ?

    **Question 4** : Scalabilité

    À 1000 livreurs et 100 mises à jour de position/seconde, votre structure tient-elle ?

    ??? success "Réponse"
        **Question 1 : Structure pour les livreurs**

        **Fonction de score combiné :**

        ```java
        double score(Delivery d, Restaurant r) {
            double distance = distance(d.position, r.position);
            double rating = d.rating;  // 1-5

            // Score : privilégier proximité, bonus pour bonne note
            return distance - (rating * RATING_WEIGHT);
            // Plus le score est bas, meilleur est le livreur
        }
        ```

        **Structure : Min-heap avec score dynamique**

        Problème : le score dépend du restaurant de la commande, donc change à chaque requête !

        **Solution pragmatique :**

        ```java
        class DeliveryService {
            // Livreurs disponibles, accès O(1) par id
            Map<Integer, Delivery> availableDrivers = new HashMap<>();

            Delivery findBest(Restaurant r) {
                return availableDrivers.values().stream()
                    .min(Comparator.comparingDouble(d -> score(d, r)))
                    .orElse(null);
            }
        }
        ```

        **Complexité** : O(n) où n = nombre de livreurs disponibles.

        Pour améliorer, on pourrait utiliser une structure spatiale (QuadTree, R-Tree) pour filtrer d'abord les livreurs proches, puis scorer parmi ceux-là.

        ---

        **Question 2 : Mises à jour de position**

        Avec la structure HashMap ci-dessus :

        - Mise à jour : O(1) — juste modifier l'objet `Delivery`
        - Pas de restructuration nécessaire car le score est calculé à la demande

        Si on utilisait un heap pré-calculé :

        - Chaque mise à jour de position nécessiterait de recalculer le score et repositionner dans le heap
        - Coût : O(log n) avec file adaptable, O(n) sans
        - Avec 100 mises à jour/seconde, c'est potentiellement 100 × log(1000) ≈ 1000 opérations/seconde — acceptable

        **Trade-off :**

        | Approche | Mise à jour | Recherche meilleur |
        |----------|-------------|-------------------|
        | HashMap + calcul à la demande | O(1) | O(n) |
        | Heap adaptable | O(log n) | O(1) extraction |

        Choix selon le ratio mises à jour / recherches.

        ---

        **Question 3 : File des commandes en attente**

        **FIFO avec priorité** (pas FIFO simple) :

        Raisons d'ajouter des priorités :

        1. **Temps d'attente** : commandes anciennes deviennent plus urgentes
        2. **Type de client** : clients premium pourraient être prioritaires
        3. **Taille de commande** : éviter que les petites commandes attendent derrière une grosse

        **Structure : PriorityQueue avec priorité dynamique**

        ```java
        class WaitingOrder implements Comparable<WaitingOrder> {
            Order order;
            Instant arrivalTime;

            int priority() {
                long waitMinutes = Duration.between(arrivalTime, Instant.now()).toMinutes();
                return (int) waitMinutes + order.premiumBonus;
            }

            public int compareTo(WaitingOrder other) {
                return Integer.compare(other.priority(), this.priority());  // Max first
            }
        }

        PriorityQueue<WaitingOrder> waitingOrders = new PriorityQueue<>();
        ```

        **Attention** : La priorité change avec le temps ! Il faut soit :

        - Recalculer à chaque extraction (acceptable si extractions peu fréquentes)
        - Utiliser une file adaptable avec mise à jour périodique

        ---

        **Question 4 : Scalabilité**

        **Charge :**

        - 1000 livreurs
        - 100 mises à jour position/seconde
        - Disons 10 recherches de livreur/seconde

        **Avec HashMap + calcul à la demande :**

        - Mises à jour : 100 × O(1) = négligeable
        - Recherches : 10 × O(1000) = 10 000 comparaisons/seconde — acceptable

        **Goulot d'étranglement potentiel** : Concurrence !

        - 100 mises à jour/seconde + 10 recherches = 110 opérations/seconde sur la même structure
        - Avec `synchronized`, contention modérée
        - Solution : `ConcurrentHashMap` avec opérations atomiques

        **Optimisation si nécessaire :**

        1. **Partitionnement géographique** : diviser la ville en zones, chaque zone a sa propre structure de livreurs

        2. **Index spatial** : QuadTree pour trouver rapidement les livreurs dans un rayon

        3. **Cache** : précalculer les "bons livreurs" pour les restaurants populaires

        ```java
        // Partitionnement par zone
        Map<Zone, Map<Integer, Delivery>> driversByZone = new ConcurrentHashMap<>();

        Delivery findBest(Restaurant r) {
            Zone zone = getZone(r.position);
            // Chercher d'abord dans la zone, puis zones adjacentes
            return searchInZones(zone, r);
        }
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
        └─► Dense (E ≈ V²) ?
            ├─► Oui → Matrice d'adjacence
            └─► Non → Liste d'adjacence
```

#### Pièges classiques — Résumé

| Piège | Réalité |
|-------|---------|
| LinkedList toujours plus rapide pour insertions | Seulement si on a déjà la position ! `get(i)` = O(n) |
| Heap = tableau trié | Non ! Seule garantie : parent ≤ enfants |
| `synchronized` = thread-safe performant | Thread-safe oui, mais contention sous charge |
| Position = index stable | Position invalide après suppression de **son** élément |
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

| Représentation graphe | Espace | Ajouter arête | Vérifier arête | Lister voisins |
|-----------------------|--------|---------------|----------------|----------------|
| Matrice d'adjacence | O(V²) | O(1) | O(1) | O(V) |
| Liste d'adjacence | O(V + E) | O(1) | O(deg) | O(deg) |

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
