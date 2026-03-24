# Démonstration 7 : Arbres BST, Maps ordonnées et Graphes II

Cette démonstration couvre les **chapitres 8** (Arbres et BST), **10** (Maps avancées), **13.3** (Trie) et **14.3** (Parcours de graphes) du livre *Data Structures and Algorithms in Java (6th ed.)*.

!!! abstract "Objectifs d'apprentissage"

    - Maîtriser la terminologie des arbres généraux et binaires (ch. 8.1)
    - Comprendre le Trie et ses avantages pour la recherche par préfixe (ch. 13.3)
    - Maîtriser les opérations sur un BST et leurs complexités (ch. 8.3–8.4)
    - Identifier les cas dégénérés et pièges classiques des BST
    - Distinguer Map ordonnée et non ordonnée, et choisir selon le contexte (ch. 10.1)
    - Comprendre le hachage cuckoo et sa garantie $O(1)$ dans le pire cas (ch. 10.2)
    - Implémenter et analyser DFS et BFS, relier DFS ↔ pile et BFS ↔ file (ch. 14.3)

!!! note "Structure de cette démonstration"

    La **Partie I — Théorie** sera ajoutée ultérieurement. Cette page présente uniquement les exercices.

---

# Partie II — Exercices

## 1. Vrai ou Faux

Pour chaque énoncé, indiquez s'il est **vrai** ou **faux** et justifiez brièvement.

### Bloc A — Arbres généraux et Trie (ch. 8.1, 13.3)

??? question "Q1 — Arêtes d'un arbre"

    Un arbre à $n$ nœuds contient exactement $n - 1$ arêtes.

    ??? success "Réponse"

        **Vrai.** Par définition, un arbre est un graphe **connexe acyclique**. Chaque nœud sauf la racine est relié à son parent par exactement une arête → $n$ nœuds, $n - 1$ arêtes. On peut le vérifier par induction : un arbre à 1 nœud a 0 arête ; ajouter un nœud feuille ajoute exactement 1 arête.

??? question "Q2 — Hauteur vs taille"

    Dans un arbre binaire, la hauteur et la taille (nombre de nœuds) sont deux mesures équivalentes.

    ??? success "Réponse"

        **Faux.** Ce sont deux mesures **indépendantes** :

        - La **taille** est le nombre total de nœuds dans l'arbre.
        - La **hauteur** est la longueur (en arêtes) du plus long chemin de la racine à une feuille.

        Pour un arbre de taille $n$ :

        - Hauteur **minimale** : $\lfloor \log_2 n \rfloor$ (arbre équilibré / complet)
        - Hauteur **maximale** : $n - 1$ (arbre dégénéré, linéaire)

        Exemple : deux arbres binaires de 7 nœuds peuvent avoir des hauteurs de 2 (équilibré) ou 6 (chaîne).

??? question "Q3 — Trie et stockage des préfixes"

    Dans un Trie, la clé complète associée à un nœud est stockée explicitement dans ce nœud.

    ??? success "Réponse"

        **Faux.** Dans un Trie, la clé est encodée **implicitement** par le chemin depuis la racine jusqu'au nœud. Chaque arête porte un caractère, et la clé est la concaténation des caractères le long du chemin. Les nœuds internes ne stockent généralement pas le préfixe complet — c'est l'un des avantages d'espace du Trie.

??? question "Q4 — Complexité de recherche dans un Trie"

    La recherche d'un mot dans un Trie prend un temps proportionnel au nombre total de mots stockés.

    ??? success "Réponse"

        **Faux.** La recherche dans un Trie prend un temps **$O(d)$** où $d$ est la longueur du mot recherché, **indépendamment** du nombre $n$ de mots stockés. C'est l'un des principaux avantages du Trie : le coût de recherche ne dépend que de la longueur de la clé, pas de la taille de la collection.

### Bloc B — Arbres binaires de recherche (ch. 8.3, 8.4)

??? question "Q5 — Parcours infixe d'un BST"

    Le parcours infixe (*in-order*) d'un arbre binaire de recherche (BST) produit les clés dans l'ordre croissant.

    ??? success "Réponse"

        **Vrai.** La propriété BST garantit que pour tout nœud $v$ : toutes les clés du sous-arbre gauche sont $\leq k(v)$, et toutes les clés du sous-arbre droit sont $\geq k(v)$. Le parcours infixe visite gauche → racine → droite, ce qui correspond exactement à un parcours en ordre croissant.

        Application : le *TreeSort* insère $n$ éléments puis effectue un parcours infixe → $O(n \log n)$ attendu, $O(n^2)$ pire cas.

??? question "Q6 — BST toujours équilibré"

    Un arbre binaire de recherche contenant $n$ clés distinctes a toujours une hauteur de $O(\log n)$.

    ??? success "Réponse"

        **Faux.** Cette garantie n'est valide que pour un BST **équilibré** (AVL, rouge-noir, etc.). Un BST ordinaire peut dégénérer si les insertions suivent un ordre défavorable.

        Exemple : insérer $1, 2, 3, 4, 5$ dans cet ordre produit une chaîne de hauteur $n - 1 = 4$, et toutes les opérations deviennent $O(n)$.

        **Piège classique** : supposer qu'un BST est toujours équilibré. Sur des données triées ou quasi-triées, un BST non équilibré est aussi lent qu'une liste chaînée.

??? question "Q7 — Suppression avec deux enfants"

    Pour supprimer un nœud $v$ ayant **deux enfants** dans un BST, on peut le remplacer soit par son prédécesseur infixe (le maximum du sous-arbre gauche), soit par son successeur infixe (le minimum du sous-arbre droit).

    ??? success "Réponse"

        **Vrai.** Les deux stratégies sont valides et maintiennent la propriété BST :

        - **Successeur infixe** : le plus petit élément du sous-arbre droit de $v$. Ce nœud a au plus un enfant (droit), ce qui simplifie sa suppression récursive.
        - **Prédécesseur infixe** : le plus grand élément du sous-arbre gauche de $v$. Raisonnement symétrique.

        Certaines implémentations alternent les deux stratégies pour équilibrer l'arbre heuristiquement.

??? question "Q8 — Minimum d'un BST est toujours une feuille"

    Dans tout BST non vide, l'élément de clé minimale est toujours une feuille.

    ??? success "Réponse"

        **Faux.** L'élément minimal est dans le nœud le plus à **gauche** (en descendant toujours à gauche depuis la racine). Ce nœud n'a **pas d'enfant gauche**, mais il peut avoir un **enfant droit**.

        Exemple : BST avec les clés $\{5, 3, 7, 4\}$ — le minimum 3 a un enfant droit (4), donc ce n'est pas une feuille.

### Bloc C — Maps et Hachage avancé (ch. 10.1, 10.2)

??? question "Q9 — Map ordonnée : coût de floorEntry"

    Une `SortedMap` supporte l'opération `floorEntry(k)` (la plus grande clé $\leq k$) en $O(1)$.

    ??? success "Réponse"

        **Faux.** `floorEntry(k)` coûte $O(\log n)$ dans une implémentation par BST équilibré (comme `TreeMap` en Java). Une `HashMap` non ordonnée ne supporte pas du tout cette opération efficacement — il faudrait parcourir toutes les entrées en $O(n)$.

        C'est le compromis fondamental : la `SortedMap` offre des opérations d'ordre (`floor`, `ceiling`, `first`, `last`, requêtes de plage) en $O(\log n)$, au prix de `get`/`put` en $O(\log n)$ au lieu de $O(1)$ attendu.

??? question "Q10 — Hachage cuckoo : garantie O(1)"

    Dans une table de hachage cuckoo avec deux tables $T_1$ et $T_2$, la recherche de la clé $k$ nécessite de vérifier au plus deux emplacements, garantissant $O(1)$ dans le **pire cas**.

    ??? success "Réponse"

        **Vrai.** Toute clé $k$ est stockée soit en $T_1[h_1(k)]$, soit en $T_2[h_2(k)]$ — jamais ailleurs. La recherche vérifie ces deux positions et s'arrête, quelle que soit la taille de la table → $O(1)$ pire cas garanti pour `get`.

        Comparez avec le sondage linéaire ou le chaînage séparé, où le pire cas est $O(n)$.

??? question "Q11 — Cycle dans l'insertion cuckoo"

    Si l'insertion d'une clé dans une table cuckoo provoque un cycle d'expulsions, on résout toujours le problème en déplaçant une clé vers un emplacement alternatif.

    ??? success "Réponse"

        **Faux.** Un cycle d'expulsions signifie que les clés impliquées forment un groupe impossible à placer avec les fonctions de hachage actuelles. La seule solution est un **rehachage** : choisir de nouvelles fonctions $h_1'$ et $h_2'$ et réinsérer toutes les clés. On ne peut pas résoudre un cycle en déplaçant une seule clé.

### Bloc D — DFS et BFS (ch. 14.3)

??? question "Q12 — DFS récursif vs itératif"

    Le DFS récursif et le DFS itératif (avec pile explicite) explorent toujours les sommets exactement dans le même ordre.

    ??? success "Réponse"

        **Faux.** L'ordre peut différer selon la manière dont les voisins sont empilés dans la version itérative.

        - En DFS **récursif**, on traite le premier voisin dans l'ordre d'itération, puis on revient aux suivants.
        - En DFS **itératif**, on empile *tous* les voisins d'un coup. Selon l'ordre d'empilement (direct ou inversé), l'ordre de visite peut changer.

        **Piège fréquent** : les deux algorithmes sont équivalents en structure (marquage, arêtes traversées), mais pas nécessairement en ordre de visite.

??? question "Q13 — BFS et plus court chemin"

    Dans un graphe **non pondéré** et **connexe**, BFS depuis un sommet $s$ garantit que le premier chemin trouvé vers tout sommet $t$ est de longueur minimale (en nombre d'arêtes).

    ??? success "Réponse"

        **Vrai.** BFS explore les sommets par niveaux croissants de distance à $s$ : niveau 0 ($s$ lui-même), niveau 1 (voisins directs), niveau 2 (voisins à distance 2), etc. Le premier moment où BFS atteint $t$, c'est au niveau correspondant à la **distance minimale** (en arêtes) de $s$ à $t$.

        **Attention** : cela ne fonctionne que pour les graphes **non pondérés**. Pour les graphes pondérés, il faut l'algorithme de Dijkstra.

??? question "Q14 — Graphe non connexe et DFS"

    Dans un graphe non connexe, un DFS lancé depuis un seul sommet peut ne pas visiter tous les sommets.

    ??? success "Réponse"

        **Vrai.** Les sommets appartenant à d'autres composantes connexes que celle du sommet de départ ne sont jamais atteints. Pour parcourir **tous** les sommets, il faut relancer le DFS depuis un sommet non encore visité, en répétant l'opération jusqu'à ce que tous les sommets soient marqués :

        ```java
        for (Vertex v : graph.vertices())
            if (!visited.contains(v))
                dfs(graph, v, visited);
        ```

---

## 2. Choix multiples

??? question "Q1 — Hauteur d'un BST dégénéré"

    On insère les clés $1, 2, 3, 4, 5$ dans cet ordre dans un BST initialement vide. Quelle est la hauteur de l'arbre résultant ?

    - [ ] A) $\lfloor \log_2 5 \rfloor = 2$
    - [ ] B) 3
    - [ ] C) 4
    - [ ] D) 5

    ??? success "Réponse"

        **C) 4**

        Insérer des clés en ordre croissant produit un BST **dégénéré** (chaîne vers la droite) :

        ```
        1
         \
          2
           \
            3
             \
              4
               \
                5
        ```

        La racine est à profondeur 0, la feuille 5 est à profondeur 4 → hauteur $= n - 1 = 4$.

        Toutes les opérations (`get`, `put`, `remove`) dégradent à $O(n)$ sur cet arbre.

??? question "Q2 — Avantage principal du Trie"

    Parmi les affirmations suivantes, laquelle décrit le principal avantage d'un Trie par rapport à une table de hachage pour stocker un ensemble de mots ?

    - [ ] A) Moins d'espace mémoire dans tous les cas
    - [ ] B) Recherche par **préfixe** efficace en $O(|p|)$ où $|p|$ est la longueur du préfixe
    - [ ] C) Insertions toujours en $O(1)$
    - [ ] D) Aucune collision possible

    ??? success "Réponse"

        **B)**

        Le Trie excelle pour les opérations de **préfixe** : trouver tous les mots commençant par `"pre"` nécessite de suivre le chemin du préfixe ($O(|p|)$) puis d'explorer le sous-arbre — ce que la table de hachage ne peut pas faire efficacement.

        - A) Faux : un Trie peut consommer plus d'espace (beaucoup de nœuds vides si l'alphabet est grand).
        - C) Faux : l'insertion dans un Trie coûte $O(d)$ où $d$ est la longueur du mot.
        - D) Partiellement vrai, mais ce n'est pas l'avantage *principal* — les tables de hachage bien conçues ont aussi peu de collisions.

??? question "Q3 — Suppression BST : successeur infixe"

    Dans un BST, on supprime le nœud de clé $50$ qui possède un sous-arbre gauche (racine $30$) et un sous-arbre droit (racine $70$). Le minimum du sous-arbre droit est $55$.

    En utilisant la stratégie du **successeur infixe**, quelle clé occupe la position de l'ancien nœud $50$ après la suppression ?

    - [ ] A) 30
    - [ ] B) 45 (prédécesseur infixe)
    - [ ] C) 55 (successeur infixe)
    - [ ] D) 70

    ??? success "Réponse"

        **C) 55**

        Le successeur infixe de $50$ est le **minimum du sous-arbre droit**, soit $55$. Étapes :

        1. Copier la clé $55$ dans le nœud $50$.
        2. Supprimer le nœud qui contenait $55$ dans le sous-arbre droit. Ce nœud a au plus un enfant droit (car s'il avait un enfant gauche, ce ne serait plus le minimum).

        La propriété BST est maintenue : $55 > 30$ (sous-arbre gauche) et $55 < 70$ (racine du sous-arbre droit).

??? question "Q4 — Hachage cuckoo : nombre d'accès pour get"

    Dans une table de hachage cuckoo avec deux tables $T_1$ et $T_2$, combien d'accès mémoire faut-il au **maximum** pour l'opération `get(k)` ?

    - [ ] A) 1
    - [ ] B) 2
    - [ ] C) $O(\log n)$
    - [ ] D) $O(n)$ dans le pire cas

    ??? success "Réponse"

        **B) 2**

        Toute clé $k$ est stockée soit en $T_1[h_1(k)]$, soit en $T_2[h_2(k)]$. La recherche vérifie ces deux emplacements → exactement **2 accès mémoire maximum**, quelle que soit la taille de la table. C'est la garantie $O(1)$ pire cas du hachage cuckoo.

        Cela contraste avec le sondage linéaire (jusqu'à $O(n)$ sondes) et le chaînage séparé (bucket de taille $O(n)$) dans le pire cas.

??? question "Q5 — DFS vs BFS : trouver le plus court chemin"

    Quelle structure de données est fondamentale à l'algorithme qui trouve le **plus court chemin** (en nombre d'arêtes) dans un graphe non pondéré ?

    - [ ] A) Une pile (*Stack*)
    - [ ] B) Une file (*Queue*)
    - [ ] C) Un arbre binaire de recherche
    - [ ] D) Un tas (*Heap*)

    ??? success "Réponse"

        **B) Une file (*Queue*)**

        C'est **BFS** qui trouve les plus courts chemins dans un graphe non pondéré. BFS utilise une **file** (FIFO) pour explorer les sommets niveau par niveau — le niveau $d$ contient exactement les sommets à distance $d$ de la source.

        Le DFS (avec une **pile**, option A) ne garantit pas les plus courts chemins. Le tas (D) est utilisé dans l'algorithme de Dijkstra pour les graphes **pondérés**.

??? question "Q6 — Ordre de visite BFS"

    On effectue un BFS depuis le sommet $1$ dans le graphe non orienté ci-dessous. Les voisins sont traités par ordre croissant.

    **Arêtes** : $(1,2)$, $(1,3)$, $(2,4)$, $(2,5)$, $(3,5)$, $(5,6)$

    Dans quel ordre les sommets sont-ils visités ?

    - [ ] A) $1, 2, 3, 4, 5, 6$
    - [ ] B) $1, 2, 4, 5, 3, 6$
    - [ ] C) $1, 3, 2, 5, 4, 6$
    - [ ] D) $1, 2, 3, 5, 4, 6$

    ??? success "Réponse"

        **A) $1, 2, 3, 4, 5, 6$**

        Trace BFS (les sommets sont marqués *découverts* dès leur entrée dans la file) :

        | Sommet dépilé | File après | Nouveaux voisins enfilés |
        |--------------|------------|--------------------------|
        | 1 | [2, 3] | 2, 3 |
        | 2 | [3, 4, 5] | 4, 5 |
        | 3 | [4, 5] | 5 déjà découvert — rien |
        | 4 | [5] | aucun |
        | 5 | [6] | 6 |
        | 6 | [] | aucun |

        Distances depuis $1$ : $d(2)=1$, $d(3)=1$, $d(4)=2$, $d(5)=2$, $d(6)=3$.

---

## 3. Questions avancées

??? question "Q7 — SortedMap vs HashMap : choisir selon le contexte"

    Pour chacun des scénarios suivants, indiquez si une `SortedMap` ou une `HashMap` est plus appropriée et justifiez :

    1. Un répertoire téléphonique où on cherche un contact par son nom exact.
    2. Un système de réservations d'hôtel où on veut lister toutes les réservations entre deux dates.
    3. Un cache de pages web où la clé est l'URL.

    ??? success "Réponse"

        **1. Répertoire — `HashMap`**

        Les recherches sont des correspondances exactes (nom → numéro). `get` en $O(1)$ attendu contre $O(\log n)$ pour une `SortedMap`. L'ordre alphabétique n'est pas nécessaire pour la recherche par clé.

        **2. Réservations par plage de dates — `SortedMap`**

        L'opération clé est une **requête de plage** (*range query*) : trouver toutes les réservations entre le 1er et le 15 du mois. `TreeMap.subMap(from, to)` en Java s'exécute en $O(\log n + k)$ où $k$ est le nombre de résultats. Une `HashMap` ne permet pas cette opération efficacement.

        **3. Cache web — `HashMap`**

        L'accès au cache est très fréquent et porte sur une clé exacte. La vitesse ($O(1)$ attendu) prime. Une `SortedMap` serait un surcoût injustifié.

        **Règle générale** : utiliser `SortedMap` uniquement si des opérations d'ordre, de plage, ou d'itération triée sont réellement nécessaires.

??? question "Q8 — Coût de DFS/BFS selon la représentation du graphe"

    Un graphe $G = (V, E)$ est représenté soit par une **map d'adjacence** (liste d'adjacence), soit par une **matrice d'adjacence**. Comparez le coût total d'un DFS ou BFS complet dans les deux cas.

    ??? success "Réponse"

        | Représentation | Coût total DFS/BFS |
        |----------------|-------------------|
        | Map d'adjacence | $O(V + E)$ |
        | Matrice d'adjacence | $O(V^2)$ |

        **Avec la map d'adjacence** :

        - Chaque sommet est visité une fois : $O(V)$.
        - Pour chaque sommet $v$, on itère sur ses voisins (taille $\deg(v)$). La somme sur tous les sommets vaut $\sum_v \deg(v) = 2E$ → $O(E)$.
        - Total : $O(V + E)$.

        **Avec la matrice d'adjacence** :

        - Pour trouver les voisins de $v$, il faut parcourir toute la ligne $v$ de taille $V$, même si $v$ n'a que 2 voisins.
        - Total : $O(V^2)$.

        **Conséquence** : pour un graphe **creux** ($E \ll V^2$), la map d'adjacence est bien plus efficace. Pour un graphe **dense** ($E \approx V^2/2$), les deux approches ont la même complexité asymptotique.

??? question "Q9 — BST et données triées : éviter le cas dégénéré"

    On doit construire un BST à partir d'un tableau de $n$ entiers **déjà trié**.

    1. Quelle est la hauteur et la complexité de recherche si on insère dans l'ordre du tableau ?
    2. Proposez une stratégie d'insertion qui produit un arbre équilibré.
    3. Nommez deux structures auto-équilibrantes qui évitent ce problème automatiquement.

    ??? success "Réponse"

        **1. Insertion dans l'ordre trié — arbre dégénéré :**

        Hauteur : $n - 1$ (chaîne vers la droite ou la gauche).
        Complexité de `get`, `put`, `remove` : $O(n)$ — équivalent à une liste chaînée.

        **2. Stratégie équilibrée :**

        Insérer l'élément **médian** du tableau trié, puis récursivement les médianes des sous-tableaux gauche et droit :

        ```
        Tableau : [1, 2, 3, 4, 5, 6, 7]
        → insérer 4 (médian)
        → insérer 2 (médian gauche), 6 (médian droit)
        → insérer 1, 3, 5, 7 (feuilles)
        ```

        Résultat : arbre équilibré de hauteur $\lfloor \log_2 n \rfloor$.

        **3. Structures auto-équilibrantes :**

        - **Arbre AVL** : maintient le facteur d'équilibre (différence de hauteurs des sous-arbres) dans $\{-1, 0, 1\}$ via des rotations. Hauteur garantie $O(\log n)$.
        - **Arbre rouge-noir** : propriétés de coloration garantissant une hauteur $O(\log n)$. C'est l'implémentation de `TreeMap` en Java.

??? question "Q10 — Hachage cuckoo : coût d'insertion"

    Le hachage cuckoo garantit `get` en $O(1)$ pire cas. Qu'en est-il du coût d'**insertion** ? Décrivez le mécanisme et expliquez pourquoi la garantie $O(1)$ ne s'applique pas.

    ??? success "Réponse"

        **Mécanisme d'insertion :**

        Pour insérer une clé $k$ :

        1. Si $T_1[h_1(k)]$ est vide → insérer là.
        2. Sinon si $T_2[h_2(k)]$ est vide → insérer là.
        3. Sinon, **expulser** la clé $k'$ occupant $T_1[h_1(k)]$, placer $k$ à sa place, puis réinsérer $k'$ (qui peut à son tour déclencher une expulsion).

        Ce processus peut se **propager** en chaîne. Dans le pire cas, les expulsions forment un **cycle** — la chaîne ne se termine jamais → il faut **rehacher** avec de nouvelles fonctions $h_1'$ et $h_2'$.

        **Coût d'insertion :**

        - Coût **attendu** : $O(1)$ amorti sous des hypothèses probabilistes, avec facteur de charge $\lambda < 0.5$.
        - Coût **pire cas** : $O(n)$ si un cycle se produit et déclenche un rehachage complet.

        Le facteur de charge recommandé est $\lambda < 0.5$ pour garantir que la probabilité d'un cycle reste très faible.

---

## 4. Exercices de trace

??? question "Q11 — Insertions dans un BST"

    On insère les clés suivantes dans un BST initialement vide, dans cet ordre :

    $$40, \; 20, \; 60, \; 10, \; 30, \; 50, \; 70, \; 25$$

    1. Dessinez l'arbre résultant.
    2. Donnez le résultat du parcours **infixe** (*in-order*).
    3. Donnez le résultat du parcours **préfixe** (*pre-order*).

    ??? success "Réponse"

        **1. Arbre résultant :**

        ```
                40
               /  \
             20    60
            /  \  /  \
           10  30 50  70
              /
             25
        ```

        Trace des insertions :

        | Clé | Chemin parcouru | Position finale |
        |-----|----------------|-----------------|
        | 40 | — | racine |
        | 20 | 40↙ | enfant gauche de 40 |
        | 60 | 40↘ | enfant droit de 40 |
        | 10 | 40↙ 20↙ | enfant gauche de 20 |
        | 30 | 40↙ 20↘ | enfant droit de 20 |
        | 50 | 40↘ 60↙ | enfant gauche de 60 |
        | 70 | 40↘ 60↘ | enfant droit de 60 |
        | 25 | 40↙ 20↘ 30↙ | enfant gauche de 30 |

        **2. Parcours infixe** (gauche → racine → droite) :

        $$10, \; 20, \; 25, \; 30, \; 40, \; 50, \; 60, \; 70$$

        → Les clés sont dans l'ordre **croissant** ✓

        **3. Parcours préfixe** (racine → gauche → droite) :

        $$40, \; 20, \; 10, \; 30, \; 25, \; 60, \; 50, \; 70$$

??? question "Q12 — Suppression dans un BST"

    En reprenant le BST de Q11, supprimez la clé $20$.

    1. Quel cas de suppression s'applique (0, 1 ou 2 enfants) ?
    2. Quel nœud remplace $20$ si on utilise le **successeur infixe** ?
    3. Dessinez l'arbre après suppression et vérifiez avec le parcours infixe.

    ??? success "Réponse"

        **1. Cas de suppression :**

        Le nœud $20$ a **deux enfants** (gauche : $10$, droite : $30$) → cas à deux enfants.

        **2. Successeur infixe :**

        Le successeur infixe de $20$ est le **minimum du sous-arbre droit** de $20$, soit $25$ (le nœud le plus à gauche dans le sous-arbre enraciné en $30$).

        Étapes :

        1. Copier la clé $25$ dans le nœud $20$.
        2. Supprimer le nœud $25$ de sa position d'origine. Ce nœud n'a aucun enfant → suppression directe.

        **3. Arbre après suppression :**

        ```
                40
               /  \
             25    60
            /  \  /  \
           10  30 50  70
        ```

        Parcours infixe : $10, 25, 30, 40, 50, 60, 70$ ✓ (ordre croissant maintenu)

??? question "Q13 — Trace DFS et BFS sur un graphe"

    Soit le graphe non orienté $G$ avec les sommets $\{1, 2, 3, 4, 5, 6\}$ et les arêtes :

    $$(1,2),\; (1,3),\; (2,4),\; (2,5),\; (3,5),\; (5,6)$$

    Les voisins sont toujours traités par **ordre croissant**.

    1. Effectuez un **DFS récursif** depuis le sommet $1$. Donnez l'ordre de visite, identifiez les **arêtes de l'arbre DFS** et les **arêtes de retour** (*back edges*).
    2. Effectuez un **BFS** depuis le sommet $1$. Donnez l'ordre de visite et la distance (en arêtes) de chaque sommet à $1$.
    3. Quelle structure de données (pile ou file) est associée à chaque algorithme, et pourquoi ?

    ??? success "Réponse"

        **Listes d'adjacence :**

        | Sommet | Voisins (ordre croissant) |
        |--------|--------------------------|
        | 1 | 2, 3 |
        | 2 | 1, 4, 5 |
        | 3 | 1, 5 |
        | 4 | 2 |
        | 5 | 2, 3, 6 |
        | 6 | 5 |

        ---

        **1. DFS récursif depuis $1$ :**

        ```
        dfs(1) — marquer 1
          → dfs(2) — marquer 2
              → dfs(4) — marquer 4
                  → voisin 2 déjà visité — retour
              → dfs(5) — marquer 5
                  → voisin 2 déjà visité
                  → dfs(3) — marquer 3
                      → voisin 1 déjà visité
                      → voisin 5 déjà visité — retour
                  → dfs(6) — marquer 6
                      → voisin 5 déjà visité — retour
          → voisin 3 déjà visité — retour
        ```

        **Ordre de visite** : $1, 2, 4, 5, 3, 6$

        **Arêtes de l'arbre DFS** (*tree edges*) : $(1,2)$, $(2,4)$, $(2,5)$, $(5,3)$, $(5,6)$

        **Arête de retour** (*back edge*) : $(1,3)$ — rencontrée lors du retour à $1$ ; le sommet $3$ est déjà visité (atteint via $2 \to 5 \to 3$), et $1$ est un ancêtre de $3$ dans l'arbre DFS.

        ---

        **2. BFS depuis $1$ :**

        | Étape | Sommet traité | File avant traitement | Nouveaux sommets enfilés |
        |-------|--------------|----------------------|--------------------------|
        | Init | — | [1] | — |
        | 1 | 1 | [2, 3] | 2, 3 |
        | 2 | 2 | [3, 4, 5] | 4, 5 |
        | 3 | 3 | [4, 5] | 5 déjà découvert — rien |
        | 4 | 4 | [5] | aucun (2 visité) |
        | 5 | 5 | [6] | 6 |
        | 6 | 6 | [] | aucun |

        **Ordre de visite** : $1, 2, 3, 4, 5, 6$

        **Distances depuis $1$** :

        | Sommet | 1 | 2 | 3 | 4 | 5 | 6 |
        |--------|---|---|---|---|---|---|
        | Distance | 0 | 1 | 1 | 2 | 2 | 3 |

        ---

        **3. Structures de données :**

        - **DFS → Pile (*Stack*, LIFO)** : on explore en profondeur, en revenant en arrière (*backtrack*) quand on atteint une impasse. La pile réalise ce comportement — le dernier sommet empilé est le premier exploré. En version récursive, la **pile d'appels** du système joue ce rôle implicitement.

        - **BFS → File (*Queue*, FIFO)** : on explore par niveaux, en traitant tous les voisins directs avant ceux à distance 2, etc. La file réalise ce comportement — les sommets sont visités dans l'ordre de leur découverte.

---

# Références

Goodrich, Tamassia, Goldwasser — *Data Structures and Algorithms in Java*, 6th ed. — Chapitres 8.1, 8.3–8.4, 10.1–10.2, 13.3, 14.3
