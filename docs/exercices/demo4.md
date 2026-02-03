# Démonstration 4 : Files avec priorités et Tas (Heaps)

Cette démonstration porte sur le **Chapitre 9** (*Priority Queues*) du livre *Data Structures and Algorithms in Java (6th ed.)*.

!!! abstract "Objectifs d'apprentissage"

    À la fin de cette démonstration, vous devriez être capable de :

    * Comprendre l'**invariant du tas** (heap-order property)
    * Distinguer un **tas** d'un **arbre binaire de recherche**
    * Analyser la complexité des opérations `insert` et `removeMin`
    * Implémenter un tas avec un **tableau** (représentation implicite)
    * Comparer les différentes implémentations de files à priorité
    * Comprendre les **priorités adaptables** et leur coût

---

## Rappels théoriques

### L'ADT File à priorité (Priority Queue)

Une **file à priorité** est une collection d'éléments où chaque élément possède une **clé** (priorité) et une **valeur**. Les opérations principales sont :

| Opération | Description | 
| --- | --- |
| `insert(k, v)` | Insère une entrée avec clé `k` et valeur `v` |
| `min()` | Retourne l'entrée avec la plus petite clé (sans la retirer) |
| `removeMin()` | Retire et retourne l'entrée avec la plus petite clé |
| `size()` | Retourne le nombre d'entrées |
| `isEmpty()` | Vérifie si la file est vide |

!!! info "Convention"

    Par défaut, on considère un **min-heap** : la plus petite clé a la plus haute priorité.  
    Un **max-heap** inverse cette relation (plus grande clé = plus haute priorité).

### Comparaison des implémentations

| Implémentation | `insert` | `removeMin` | `min` | Cas d'usage |
| --- | --- | --- | --- | --- |
| Liste non triée | O(1) | O(n) | O(n) | Peu d'extractions |
| Liste triée | O(n) | O(1) | O(1) | Peu d'insertions |
| **Tas (Heap)** | O(log n) | O(log n) | O(1) | Usage général |

---

### Le Tas binaire (Binary Heap)

Un **tas binaire** est un arbre binaire qui satisfait deux propriétés :

#### 1. Propriété structurelle : Arbre binaire complet

Un arbre binaire **complet** est rempli niveau par niveau, de gauche à droite :

```
         ✓ Complet                    ✗ Non complet
            4                              4
          /   \                          /   \
         9     7                        9     7
        / \   /                        / \     \
       15 12 6                        15 12     6
```

!!! note "Hauteur d'un tas"

    Un tas de **n** éléments a une hauteur **h = ⌊log₂ n⌋**.  
    C'est cette propriété qui garantit les opérations en O(log n).

#### 2. Propriété d'ordre : Heap-Order Property

Pour tout nœud **v** (sauf la racine), la clé de **v** est **supérieure ou égale** à la clé de son parent :

$$\forall v \neq \text{racine} : \text{clé}(\text{parent}(v)) \leq \text{clé}(v)$$

```
         Min-Heap valide              Min-Heap INVALIDE
              4                              4
            /   \                          /   \
           9     7                        2     7    ← 2 < 4 viole l'invariant !
          / \   /                        / \   /
         15 12 6                        15 12 6
```

!!! warning "Piège classique : Tas ≠ Arbre binaire de recherche"

    Dans un **tas**, le parent est plus petit que ses enfants, mais il n'y a **pas de relation** entre les enfants gauche et droite.

    Dans un **ABR**, l'enfant gauche < parent < enfant droit.

    ```
            Tas valide                    ABR équivalent
                4                              9
              /   \                          /   \
             9     7     ← 9 > 7 OK!        4    12
            / \   /                        / \     \
           15 12 6                        2   7    15
    ```

---

### Représentation par tableau

Un tas se représente efficacement dans un **tableau** grâce à la propriété de complétude :

```
Tas :           4
              /   \
             9     7
            / \   /
           15 12 6

Tableau : [4, 9, 7, 15, 12, 6]
Index :    0  1  2   3   4  5
```

Les relations parent-enfant se calculent par **arithmétique d'indices** :

| Relation | Formule | Exemple (i=1, valeur 9) |
| --- | --- | --- |
| Parent de i | `(i - 1) / 2` | parent(1) = 0 → valeur 4 |
| Enfant gauche de i | `2i + 1` | left(1) = 3 → valeur 15 |
| Enfant droit de i | `2i + 2` | right(1) = 4 → valeur 12 |

!!! tip "Avantage du tableau"

    Pas de pointeurs → économie de mémoire et meilleure localité de cache.

---

### Opérations sur le tas

#### Insertion : Up-Heap Bubbling

1. Ajouter l'élément à la **fin** du tableau (prochaine position libre)
2. **Remonter** (up-heap) tant que l'élément est plus petit que son parent

```
Insertion de 3 dans [4, 9, 7, 15, 12, 6] :

Étape 1 : Ajouter à la fin
[4, 9, 7, 15, 12, 6, 3]
                    ↑ index 6

Étape 2 : Comparer avec parent (index 2, valeur 7)
3 < 7 → Échanger
[4, 9, 3, 15, 12, 6, 7]
       ↑

Étape 3 : Comparer avec parent (index 0, valeur 4)
3 < 4 → Échanger
[3, 9, 4, 15, 12, 6, 7]
 ↑ Nouvelle racine !
```

**Complexité** : O(log n) — au plus h échanges où h est la hauteur.

#### Suppression du minimum : Down-Heap Bubbling

1. Remplacer la racine par le **dernier** élément
2. **Descendre** (down-heap) en échangeant avec le plus petit enfant

```
removeMin() sur [3, 9, 4, 15, 12, 6, 7] :

Étape 1 : Remplacer racine par dernier élément
[7, 9, 4, 15, 12, 6]
 ↑ 7 remplace 3, on retire le dernier

Étape 2 : Comparer avec enfants (9 et 4)
min(9, 4) = 4, et 7 > 4 → Échanger avec 4
[4, 9, 7, 15, 12, 6]
    ↓
       7 descend à droite

Étape 3 : 7 a un enfant (6)
7 > 6 → Échanger
[4, 9, 6, 15, 12, 7]

Étape 4 : 7 n'a plus d'enfant → Terminé
```

**Complexité** : O(log n) — au plus h échanges.

---

### Heap-Sort

Le **tri par tas** exploite la propriété du tas pour trier en O(n log n) :

1. **Phase 1 (Construction)** : Construire un tas à partir des données — O(n)
2. **Phase 2 (Extraction)** : Extraire `removeMin()` n fois — O(n log n)

!!! note "Construction en O(n) — Bottom-up Heap Construction"

    Plutôt que n insertions en O(n log n), on peut construire un tas en O(n) avec la méthode **bottom-up** : commencer par les feuilles et appliquer down-heap vers la racine.

---

### File à priorité adaptable

Une **file à priorité adaptable** permet de modifier les entrées après insertion :

| Opération | Description | Complexité (tas) |
| --- | --- | --- |
| `remove(entry)` | Supprime une entrée arbitraire | O(log n)* |
| `replaceKey(entry, k)` | Change la clé d'une entrée | O(log n)* |
| `replaceValue(entry, v)` | Change la valeur d'une entrée | O(1) |

*\* Nécessite de connaître la **position** de l'entrée dans le tas.*

!!! warning "Piège : Coût de localisation"

    Sans mécanisme de localisation (comme un `Map` clé→position), trouver une entrée coûte **O(n)**.  
    Avec localisation, `replaceKey` nécessite potentiellement un up-heap **ou** un down-heap.

---

## Partie 1 — Exercices théoriques

### 1.1 Vrai ou Faux

Pour chaque énoncé, indiquez s'il est **vrai** ou **faux** et justifiez votre réponse.

??? question "Question 1 — Ordre dans un tas"
    Dans un min-heap, le deuxième plus petit élément se trouve toujours à l'index 1 ou 2 du tableau.

    ??? success "Réponse"
        **Vrai.** Le plus petit élément est à la racine (index 0). Le deuxième plus petit doit être un enfant de la racine, car tous les autres éléments ont un ancêtre plus petit qu'eux. Les enfants de la racine sont aux indices 1 (gauche) et 2 (droite).

        **Attention** : On ne peut pas savoir lequel des deux (index 1 ou 2) est le deuxième plus petit sans les comparer !

??? question "Question 2 — Tas et tri"
    Un min-heap de n éléments stocke les éléments dans l'ordre croissant dans le tableau sous-jacent.

    ??? success "Réponse"
        **Faux.** C'est un piège classique ! Un tas n'est **pas** trié. La seule garantie est que chaque parent est plus petit que ses enfants, mais il n'y a pas de relation d'ordre entre les éléments d'un même niveau ou entre cousins.

        Exemple de tas valide : `[1, 5, 2, 7, 6, 3, 4]`
        
        - 1 < 5 et 1 < 2 ✓
        - 5 < 7 et 5 < 6 ✓
        - 2 < 3 et 2 < 4 ✓
        
        Mais le tableau n'est clairement pas trié (5 > 2).

??? question "Question 3 — Complexité de la construction"
    Construire un tas de n éléments en faisant n insertions successives a une complexité de O(n).

    ??? success "Réponse"
        **Faux.** Faire n insertions successives coûte O(n log n) car chaque insertion coûte O(log n) dans le pire cas.

        En revanche, la méthode **bottom-up** (heapify) permet de construire un tas en O(n). Cette méthode commence par les feuilles et applique down-heap de bas en haut. L'analyse montre que la somme des travaux est linéaire car les nœuds aux niveaux inférieurs (qui sont nombreux) font peu de travail.

??? question "Question 4 — Hauteur du tas"
    Un tas contenant 100 éléments a une hauteur de 7.

    ??? success "Réponse"
        **Faux.** La hauteur d'un tas de n éléments est h = ⌊log₂ n⌋.

        Pour n = 100 : h = ⌊log₂ 100⌋ = ⌊6.64...⌋ = **6**

        Vérifions : 
        - Niveau 0 : 1 nœud (total : 1)
        - Niveau 1 : 2 nœuds (total : 3)
        - Niveau 2 : 4 nœuds (total : 7)
        - Niveau 3 : 8 nœuds (total : 15)
        - Niveau 4 : 16 nœuds (total : 31)
        - Niveau 5 : 32 nœuds (total : 63)
        - Niveau 6 : 37 nœuds (total : 100) ✓

??? question "Question 5 — Localisation des éléments"
    Dans un min-heap représenté par tableau, l'élément maximum se trouve nécessairement parmi les feuilles.

    ??? success "Réponse"
        **Vrai.** Si l'élément maximum n'était pas une feuille, il aurait au moins un enfant. Mais dans un min-heap, chaque parent est plus petit que ses enfants, donc cet enfant serait plus grand que le maximum — contradiction.

        Les feuilles d'un tas de n éléments sont aux indices ⌊n/2⌋ à n-1.

??? question "Question 6 — Up-heap et down-heap"
    Lors d'une opération `replaceKey(entry, newKey)` dans un tas adaptable, il peut être nécessaire d'effectuer à la fois un up-heap ET un down-heap.

    ??? success "Réponse"
        **Faux.** Une seule direction est nécessaire :

        - Si `newKey < oldKey` : la nouvelle clé est plus petite, donc l'élément peut violer l'invariant avec son parent → **up-heap seulement**
        - Si `newKey > oldKey` : la nouvelle clé est plus grande, donc l'élément peut violer l'invariant avec ses enfants → **down-heap seulement**
        - Si `newKey == oldKey` : aucune opération nécessaire

        On ne peut jamais avoir besoin des deux car si on respecte l'invariant vers le haut, on le respecte forcément vers le bas, et vice-versa (par transitivité).

---

### 1.2 Questions à choix multiples

??? question "Question 7 — Trace d'insertion"
    On insère successivement les clés **5, 3, 8, 1, 4, 7, 2** dans un min-heap initialement vide.

    Quel est le contenu du tableau après toutes les insertions ?

    - [ ] A) `[1, 3, 2, 5, 4, 8, 7]`
    - [ ] B) `[1, 2, 3, 4, 5, 7, 8]`
    - [ ] C) `[1, 3, 2, 5, 4, 7, 8]`
    - [ ] D) `[1, 4, 2, 5, 3, 8, 7]`

    ??? success "Réponse"
        **A) `[1, 3, 2, 5, 4, 8, 7]`**

        Traçons les insertions avec up-heap :

        1. **insert(5)** : `[5]`
        2. **insert(3)** : `[5, 3]` → 3 < 5, up-heap → `[3, 5]`
        3. **insert(8)** : `[3, 5, 8]` → 8 > 3, OK
        4. **insert(1)** : `[3, 5, 8, 1]` → 1 < 5, swap → `[3, 1, 8, 5]` → 1 < 3, swap → `[1, 3, 8, 5]`
        5. **insert(4)** : `[1, 3, 8, 5, 4]` → 4 > 3, OK
        6. **insert(7)** : `[1, 3, 8, 5, 4, 7]` → 7 < 8, swap → `[1, 3, 7, 5, 4, 8]`
        
        Attendez, revérifions l'étape 6 :
        - Insertion à l'index 5, parent = (5-1)/2 = 2 (valeur 8)
        - 7 < 8 → swap → `[1, 3, 7, 5, 4, 8]`
        - Nouveau parent = (2-1)/2 = 0 (valeur 1)
        - 7 > 1 → OK

        7. **insert(2)** : `[1, 3, 7, 5, 4, 8, 2]`
        - Index 6, parent = (6-1)/2 = 2 (valeur 7)
        - 2 < 7 → swap → `[1, 3, 2, 5, 4, 8, 7]`
        - Nouveau parent = (2-1)/2 = 0 (valeur 1)
        - 2 > 1 → OK

        Résultat final : **`[1, 3, 2, 5, 4, 8, 7]`** ✓

??? question "Question 8 — Trace de removeMin"
    On effectue **deux** opérations `removeMin()` sur le tas `[2, 4, 3, 7, 5, 9, 6]`.

    Quel est le contenu du tableau après ces deux suppressions ?

    - [ ] A) `[4, 5, 6, 7, 9]`
    - [ ] B) `[4, 5, 3, 7, 9]`
    - [ ] C) `[3, 4, 6, 7, 5]`
    - [ ] D) `[4, 5, 6, 7, 9, 3]`

    ??? success "Réponse"
        **A) `[4, 5, 6, 7, 9]`**

        **Premier removeMin()** sur `[2, 4, 3, 7, 5, 9, 6]` :
        
        1. Retirer 2, remplacer par dernier (6) : `[6, 4, 3, 7, 5, 9]`
        2. Down-heap : enfants de 6 sont 4 et 3, min = 3
        3. 6 > 3 → swap : `[3, 4, 6, 7, 5, 9]`
        4. Enfants de 6 (index 2) sont 9 (index 5), pas d'enfant droit
        5. 6 < 9 → OK, terminé

        Après premier removeMin : `[3, 4, 6, 7, 5, 9]`

        **Deuxième removeMin()** sur `[3, 4, 6, 7, 5, 9]` :
        
        1. Retirer 3, remplacer par dernier (9) : `[9, 4, 6, 7, 5]`
        2. Down-heap : enfants de 9 sont 4 et 6, min = 4
        3. 9 > 4 → swap : `[4, 9, 6, 7, 5]`
        4. Enfants de 9 (index 1) sont 7 et 5, min = 5
        5. 9 > 5 → swap : `[4, 5, 6, 7, 9]`
        6. 9 n'a plus d'enfant → terminé

        Résultat final : **`[4, 5, 6, 7, 9]`** ✓

??? question "Question 9 — Choix de structure"
    Vous devez implémenter un système de gestion de tâches où :
    
    - Des milliers de tâches arrivent en continu
    - On doit toujours traiter la tâche la plus urgente en premier
    - Les priorités des tâches changent fréquemment
    
    Quelle structure est la plus appropriée ?

    - [ ] A) Liste triée par priorité
    - [ ] B) Tas binaire simple (sans adaptabilité)
    - [ ] C) Tas binaire avec file à priorité adaptable
    - [ ] D) Table de hachage avec priorité comme clé

    ??? success "Réponse"
        **C) Tas binaire avec file à priorité adaptable**

        Analysons chaque option :

        - **Liste triée (A)** : `insert` en O(n), `removeMin` en O(1). Avec des milliers de tâches arrivant en continu, les insertions O(n) deviennent un goulot d'étranglement.

        - **Tas simple (B)** : `insert` et `removeMin` en O(log n), mais changer une priorité nécessite de chercher l'élément en **O(n)** puis de le repositionner.

        - **Tas adaptable (C)** : Combine O(log n) pour insert/removeMin ET O(log n) pour `replaceKey` grâce à un mécanisme de localisation (souvent un `Map` entrée→index).

        - **Table de hachage (D)** : Accès O(1) par clé, mais trouver le minimum nécessite de parcourir toute la table en O(n).

        Avec des changements fréquents de priorité, la file adaptable est clairement le meilleur choix.

??? question "Question 10 — Analyse de complexité"
    On utilise un tas pour trier n éléments (heap-sort). Quelle est la complexité totale ?

    - [ ] A) O(n)
    - [ ] B) O(n log n)
    - [ ] C) O(n²)
    - [ ] D) O(log n)

    ??? success "Réponse"
        **B) O(n log n)**

        Le heap-sort se décompose en deux phases :

        1. **Construction du tas** : O(n) avec la méthode bottom-up
        2. **n extractions `removeMin()`** : chaque extraction coûte O(log n), donc n × O(log n) = O(n log n)

        Total : O(n) + O(n log n) = **O(n log n)**

        C'est un tri optimal en termes de complexité asymptotique (comme merge-sort et quick-sort en moyenne), avec l'avantage d'être **in-place** (espace O(1) supplémentaire).

??? question "Question 11 — Propriété du tas"
    Lequel de ces tableaux représente un min-heap valide ?

    - [ ] A) `[1, 2, 3, 4, 5, 6, 7]`
    - [ ] B) `[1, 3, 2, 4, 5, 7, 6]`
    - [ ] C) `[1, 2, 3, 5, 4, 6, 7]`
    - [ ] D) Tous les tableaux ci-dessus

    ??? success "Réponse"
        **D) Tous les tableaux ci-dessus**

        Vérifions chaque tableau en contrôlant que parent ≤ enfants :

        **A) `[1, 2, 3, 4, 5, 6, 7]`**
        ```
              1
            /   \
           2     3
          / \   / \
         4   5 6   7
        ```
        - 1 ≤ 2, 1 ≤ 3 ✓
        - 2 ≤ 4, 2 ≤ 5 ✓
        - 3 ≤ 6, 3 ≤ 7 ✓ → **Valide**

        **B) `[1, 3, 2, 4, 5, 7, 6]`**
        ```
              1
            /   \
           3     2
          / \   / \
         4   5 7   6
        ```
        - 1 ≤ 3, 1 ≤ 2 ✓
        - 3 ≤ 4, 3 ≤ 5 ✓
        - 2 ≤ 7, 2 ≤ 6 ✓ → **Valide**

        **C) `[1, 2, 3, 5, 4, 6, 7]`**
        ```
              1
            /   \
           2     3
          / \   / \
         5   4 6   7
        ```
        - 1 ≤ 2, 1 ≤ 3 ✓
        - 2 ≤ 5, 2 ≤ 4 ✓
        - 3 ≤ 6, 3 ≤ 7 ✓ → **Valide**

        Tous respectent l'invariant du tas. Cela illustre qu'**un tas n'est pas unique** pour un ensemble de clés donné.

---

### 1.3 Questions de réflexion

??? question "Question 12 — Tas vs ABR"
    Expliquez pourquoi on préfère utiliser un **tas** plutôt qu'un **arbre binaire de recherche équilibré** (comme un AVL ou Red-Black tree) pour implémenter une file à priorité.

    ??? success "Réponse"
        Plusieurs raisons justifient ce choix :

        **1. Simplicité d'implémentation**
        
        - Un tas se représente dans un simple tableau sans pointeurs
        - Pas besoin de maintenir des invariants complexes (facteur d'équilibre, couleurs)
        - Les opérations up-heap/down-heap sont plus simples que les rotations

        **2. Efficacité en pratique**
        
        - Meilleure **localité de cache** : les éléments adjacents dans le tableau sont souvent utilisés ensemble
        - Pas d'allocation dynamique de nœuds : moins de fragmentation mémoire
        - Constantes multiplicatives plus faibles dans les opérations

        **3. Fonctionnalité suffisante**
        
        - Une file à priorité n'a besoin que de `insert`, `min`, et `removeMin`
        - Un ABR offre des opérations supplémentaires (recherche par clé, parcours ordonné) qui ne sont pas nécessaires ici

        **Quand préférer un ABR ?**
        
        - Si on a besoin de rechercher des éléments par clé
        - Si on veut parcourir les éléments dans l'ordre
        - Si on a besoin de `floor`, `ceiling`, `range queries`

??? question "Question 13 — Coût caché des priorités adaptables"
    Dans une file à priorité adaptable, l'opération `replaceKey` a une complexité annoncée de O(log n). Expliquez pourquoi cette complexité suppose un mécanisme de localisation, et quel est le coût de ce mécanisme.

    ??? success "Réponse"
        **Le problème de la localisation**

        Pour modifier la clé d'une entrée, il faut d'abord **trouver sa position** dans le tas. Sans mécanisme spécial, cela nécessite un parcours en O(n).

        **Solution : Entry avec localisation**

        On utilise un objet `Entry` (ou `Location`) qui :
        
        1. Stocke la clé, la valeur, et **l'index actuel** dans le tableau
        2. Est mis à jour à chaque échange dans le tas

        Avec ce mécanisme :
        - Accéder à la position d'une entrée : **O(1)**
        - Mettre à jour l'index lors d'un swap : **O(1)** supplémentaire par swap

        **Coût du mécanisme**

        | Aspect | Coût |
        | --- | --- |
        | Espace | O(n) pour stocker les entrées |
        | Mise à jour des indices | O(1) par swap (ajouté aux opérations existantes) |
        | Accès à une entrée | O(1) si on a gardé la référence |

        **Alternative : Map clé→position**

        Si les clés sont uniques, on peut utiliser une `HashMap<Key, Integer>` pour localiser les entrées. Mais attention :
        
        - Cela suppose des clés uniques
        - Chaque swap nécessite une mise à jour du Map : O(1) amorti mais avec une constante plus élevée

??? question "Question 14 — Bottom-up vs Top-down"
    La construction d'un tas peut se faire en O(n) avec la méthode bottom-up, contre O(n log n) avec des insertions successives. Expliquez intuitivement pourquoi la méthode bottom-up est plus efficace.

    ??? success "Réponse"
        **Méthode top-down (n insertions)**

        Chaque insertion effectue un up-heap qui peut remonter jusqu'à la racine :
        
        - 1ère insertion : 0 swap max
        - 2ème insertion : 1 swap max
        - n-ème insertion : log n swaps max

        Total approximatif : Σ log(i) pour i de 1 à n ≈ **O(n log n)**

        **Méthode bottom-up**

        On part du dernier nœud non-feuille et on applique down-heap vers la racine :

        - Les feuilles (n/2 nœuds) : 0 swap
        - Niveau h-1 (n/4 nœuds) : 1 swap max chacun
        - Niveau h-2 (n/8 nœuds) : 2 swaps max chacun
        - ...
        - Racine (1 nœud) : h swaps max

        **L'intuition clé** : les nœuds les plus nombreux (les feuilles et niveaux bas) font **peu ou pas** de travail, tandis que seule la racine fait potentiellement log n swaps.

        Total : Σ (nombre de nœuds au niveau k) × (hauteur sous ce niveau)
        = n/4 × 1 + n/8 × 2 + n/16 × 3 + ... 
        = n × (1/4 + 2/8 + 3/16 + ...)
        = n × Σ (k/2^(k+1)) pour k ≥ 1
        = **O(n)**

        La série converge vers une constante (~2), d'où la complexité linéaire.

---

## Résumé — Récapitulatif du Module 2

Ce module a couvert les **Types Abstraits de Données (ADT)** fondamentaux :

| ADT | Implémentations principales | Opérations clés | Complexité typique |
| --- | --- | --- | --- |
| **Liste** | ArrayList, LinkedList | get, add, remove | O(1) à O(n) selon position |
| **Liste positionnelle** | DoublyLinkedList avec sentinelles | addAfter, addBefore, remove | O(1) avec position connue |
| **Pile (Stack)** | Array, LinkedList | push, pop, top | O(1) |
| **File (Queue)** | Array circulaire, LinkedList | enqueue, dequeue | O(1) |
| **Deque** | Array circulaire, DoublyLinkedList | addFirst/Last, removeFirst/Last | O(1) |
| **File à priorité** | Liste, Heap | insert, min, removeMin | O(log n) pour heap |

!!! success "Points clés à retenir"

    1. **Choisissez la structure selon les opérations dominantes** : Un tas est optimal pour les files à priorité, mais pas pour la recherche par clé.
    
    2. **Un tas n'est PAS trié** : Il garantit seulement que le parent est plus petit que ses enfants.
    
    3. **La représentation par tableau est efficace** : Localité de cache, pas de pointeurs, arithmétique simple.
    
    4. **Les priorités adaptables ont un coût** : Le mécanisme de localisation ajoute de la complexité.
    
    5. **Bottom-up bat top-down** : O(n) vs O(n log n) pour construire un tas.

---

## Exercice pratique : Ordonnanceur de tâches (Scheduler)

### Énoncé

On souhaite implémenter un **ordonnanceur de tâches** (scheduler) simplifié qui simule le fonctionnement d'un processeur. Le système doit gérer plusieurs tâches avec des priorités différentes et des temps d'arrivée variés.

**Spécifications :**

- Chaque tâche possède :
    - Un **identifiant** unique
    - Une **durée** (nombre d'unités de temps nécessaires)
    - Un **temps d'arrivée** (moment où la tâche devient disponible)
    - Une **priorité** (plus petit = plus prioritaire)

- Le processeur :
    - Exécute une tâche à la fois
    - Choisit toujours la tâche de **plus haute priorité** parmi celles en attente
    - Une fois qu'une tâche commence, elle s'exécute jusqu'à complétion (non-préemptif)

**Objectif :** Utiliser une `PriorityQueue` (file à priorité basée sur un tas) pour gérer efficacement la sélection de la prochaine tâche à exécuter.

[:material-download: Télécharger le code](../files/code/Scheduler.zip){ .md-button .md-button--primary }

---

### Améliorations possibles

Voici quelques extensions pour approfondir votre compréhension des files à priorité :

??? tip "Round-Robin préemptif"

    **Problème actuel :** Une tâche longue avec haute priorité monopolise le processeur, faisant attendre toutes les autres tâches.

    **Solution :** Implémenter un ordonnancement **Round-Robin préemptif** où chaque tâche s'exécute pendant un **quantum de temps** (ex: 2 unités) avant d'être remise dans la file.

    **Modifications suggérées :**

    1. Ajouter un paramètre `quantum` au `Processor`
    2. Compter le temps d'exécution de la tâche courante
    3. Si le quantum est atteint et la tâche n'est pas terminée :
        - Remettre la tâche dans la `PriorityQueue`
        - Passer à la prochaine tâche

    **Réflexion :** Comment garantir l'équité entre tâches de même priorité ?

??? tip "Priorité basée sur le temps restant (Shortest Remaining Time First)"

    **Problème actuel :** La priorité est fixe et définie à la création de la tâche.

    **Solution :** Utiliser le **temps restant** comme critère de priorité. La tâche avec le moins de temps restant est toujours prioritaire.

    **Modifications suggérées :**

    1. Modifier `Task.compareTo()` pour comparer par `remainingTime`
    2. **Attention :** Comme le `remainingTime` change pendant l'exécution, il faut utiliser une **file à priorité adaptable** ou retirer/réinsérer la tâche après chaque unité de temps

    **Réflexion :** Quel est l'avantage de SRTF ? Quel est son inconvénient principal (indice : *starvation*) ?

??? tip "Combinaison : Priorité dynamique avec vieillissement (Aging)"

    **Problème :** Avec SRTF ou priorités fixes, les tâches longues ou de basse priorité peuvent attendre indéfiniment (*starvation*).

    **Solution :** Implémenter le **vieillissement** (*aging*) : augmenter progressivement la priorité des tâches qui attendent longtemps.

    **Idée :**
    ```java
    // À chaque tick, pour les tâches en attente :
    effectivePriority = basePriority - (currentTime - arrivalTime) / agingFactor;
    ```

    Cela nécessite une file à priorité adaptable avec `replaceKey`.

---

## Références

* Goodrich, Tamassia, Goldwasser. *Data Structures and Algorithms in Java*, 6th Edition.
    * Chapitre 9 : Priority Queues
* Documentation Java : [`java.util.PriorityQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html)
