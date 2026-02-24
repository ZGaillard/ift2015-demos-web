# Démonstration 6 : Maps et Tables de Hachage

Cette démonstration porte sur les **Chapitres 10.1 et 10.2** (*Maps* et *Hash Tables*) du livre *Data Structures and Algorithms in Java (6th ed.)*.

!!! abstract "Objectifs d'apprentissage"

    À la fin de cette démonstration, vous devriez être capable de :

    * Comprendre l'**ADT Map** et ses opérations fondamentales
    * Distinguer les différentes **implémentations** de maps (liste non triée vs table de hachage)
    * Expliquer le rôle d'une **fonction de hachage** (code de hachage + fonction de compression)
    * Comparer les **stratégies de gestion des collisions** (chaînage séparé vs adressage ouvert)
    * Analyser l'impact du **facteur de charge** sur les performances
    * Tracer les opérations d'insertion, recherche et suppression dans une table de hachage

---

## Ressources Code (Chapitre 10)

Implémentation Java de différentes structures de maps et tables de hachage :

* [Interface Map — `Map.java`](../files/code/maps/src/map/Map.java)
* [Entrée — `Entry.java`](../files/code/maps/src/map/Entry.java)
* [Liste non triée — `UnsortedTableMap.java`](../files/code/maps/src/unsorted/UnsortedTableMap.java)
* [Base table de hachage — `AbstractHashMap.java`](../files/code/maps/src/hashtable/AbstractHashMap.java)
* [Chaînage séparé — `ChainHashMap.java`](../files/code/maps/src/hashtable/ChainHashMap.java)
* [Sondage linéaire — `ProbeHashMap.java`](../files/code/maps/src/hashtable/ProbeHashMap.java)
* [Exemples de fonctions de hachage — `HashExamples.java`](../files/code/maps/src/hash/HashExamples.java)
* [Démonstration — `Main.java`](../files/code/maps/src/Main.java)

---

## Rappels théoriques

### L'ADT Map

Une **map** (ou tableau associatif) est une structure de données qui stocke des paires **clé-valeur** $(k, v)$ appelées **entrées**. Les clés doivent être **uniques** : chaque clé est associée à au plus une valeur.

| Opération | Description |
| --- | --- |
| `get(k)` | Retourne la valeur associée à la clé `k`, ou `null` si absente |
| `put(k, v)` | Insère `(k, v)`. Si `k` existe déjà, remplace la valeur et retourne l'ancienne |
| `remove(k)` | Retire l'entrée de clé `k` et retourne sa valeur, ou `null` si absente |
| `size()` | Retourne le nombre d'entrées |
| `isEmpty()` | Vérifie si la map est vide |
| `keySet()` | Retourne un itérable de toutes les clés |
| `values()` | Retourne un itérable de toutes les valeurs |
| `entrySet()` | Retourne un itérable de toutes les entrées `(k, v)` |

!!! info "Convention de retour"

    Les méthodes `get`, `put` et `remove` retournent la **valeur existante** associée à la clé, ou `null` si la clé n'existe pas. Cela peut créer une ambiguïté si `null` est une valeur légitime.

### Implémentation simple : UnsortedTableMap

L'implémentation la plus simple stocke les entrées dans une `ArrayList` **sans ordre particulier**.

- **`get(k)`** : Parcourt toute la liste pour trouver la clé → **O(n)**
- **`put(k, v)`** : Parcourt pour vérifier si la clé existe, puis insère ou remplace → **O(n)**
- **`remove(k)`** : Parcourt pour trouver la clé, puis échange avec le dernier élément → **O(n)**

!!! tip "Astuce de suppression"

    Pour éviter un décalage O(n) lors de la suppression, on remplace l'entrée supprimée par la **dernière entrée** de la liste, puis on retire la dernière. L'ordre n'importe pas dans une map non triée.

---

### Tables de hachage

Une **table de hachage** est une implémentation efficace de l'ADT Map qui vise des opérations en **O(1) en temps attendu**.

L'idée : utiliser une **fonction de hachage** $h(k)$ pour mapper chaque clé $k$ à un indice dans un tableau de taille $N$ appelé **bucket array**.

```
Clé k  →  h(k)  →  indice dans [0, N-1]  →  stockage dans A[h(k)]
```

#### Fonction de hachage = Code de hachage + Compression

La fonction de hachage se décompose en deux étapes :

```
Objet arbitraire  →  [Code de hachage]  →  entier quelconque  →  [Compression]  →  indice [0, N-1]
```

**Codes de hachage :**

| Méthode | Description |
| --- | --- |
| Représentation binaire | Utiliser directement les bits comme entier (pour `int`, `short`, `char`) |
| Polynomial | $x_0 a^{n-1} + x_1 a^{n-2} + \cdots + x_{n-1}$ avec $a \neq 1$ (bon pour les chaînes) |
| Cyclic-shift | Remplace la multiplication par un décalage cyclique de bits |

!!! note "Choix de la constante $a$"

    Pour les codes polynomiaux appliqués aux chaînes de caractères, les valeurs $a = 33, 37, 39, 41$ produisent expérimentalement très peu de collisions (< 7 sur 50 000 mots anglais).

**Fonctions de compression :**

| Méthode | Formule | Notes |
| --- | --- | --- |
| Division | $i \bmod N$ | Préférer $N$ premier pour mieux répartir |
| MAD (Multiply-Add-and-Divide) | $[(ai + b) \bmod p] \bmod N$ | $p$ premier > $N$, $a > 0$, $a,b \in [0, p-1]$ |

!!! warning "Pourquoi $N$ premier ?"

    Si $N$ n'est pas premier, des motifs dans les codes de hachage se répètent dans la table. Par exemple, si $N = 100$ et les codes sont $\{200, 205, 210, \ldots, 600\}$, chaque code entre en collision avec 3 autres. Avec $N = 101$, il n'y a aucune collision.

---

### Gestion des collisions

Une **collision** survient quand deux clés distinctes $k_1 \neq k_2$ ont le même hash : $h(k_1) = h(k_2)$.

#### Chaînage séparé (Separate Chaining)

Chaque bucket $A[j]$ contient une **map secondaire** (typiquement une `UnsortedTableMap`) stockant toutes les entrées dont le hash vaut $j$.

```
     0    1    2    3    4    5    6    7    8    9   10   11   12
A  [   ][ ● ][   ][ ● ][   ][ ● ][   ][   ][   ][   ][ ● ][   ][ ● ]
          |         |         |                   |         |
         54        25        18                  10        25
         28         3                            36        38
         41        14                                      12
                                                           90
```

- **Avantage** : Simple, le facteur de charge $\lambda$ peut dépasser 1
- **Taille attendue d'un bucket** : $n/N$ (si bonne fonction de hachage)

#### Adressage ouvert (Open Addressing)

Les entrées sont stockées **directement** dans le tableau (pas de structure secondaire). Le facteur de charge doit rester $< 1$.

**Sondage linéaire (Linear Probing)** : Si $A[h(k)]$ est occupé, essayer $A[(h(k)+1) \bmod N]$, puis $A[(h(k)+2) \bmod N]$, etc.

```
Insertion de clé 15 avec h(k) = k mod 11 :

     0    1    2    3    4    5    6    7    8    9   10
   [   ][   ][ 13][   ][ 26][ 5 ][ 37][ 16][   ][   ][ 21]

   h(15) = 4 → occupé (26)
   essai 5 → occupé (5)
   essai 6 → occupé (37)
   essai 7 → occupé (16)
   essai 8 → LIBRE → insérer 15 à l'index 8
```

!!! warning "Problème du clustering"

    Le sondage linéaire tend à former des **groupes contigus** de cases occupées (clusters). Plus un cluster est grand, plus il est probable qu'il grossisse encore, ce qui dégrade les performances.

**Suppression avec sentinelle DEFUNCT** : On ne peut pas simplement vider une case (cela briserait la chaîne de sondage). On la marque avec un objet spécial `DEFUNCT` qui est ignoré lors de la recherche mais peut être réutilisé lors d'une insertion.

**Autres stratégies d'adressage ouvert :**

| Stratégie | Séquence de sondage | Avantage |
| --- | --- | --- |
| Linéaire | $h(k) + i$ | Simple |
| Quadratique | $h(k) + i^2$ | Évite le clustering primaire |
| Double hachage | $h(k) + i \cdot h'(k)$ | Évite les deux types de clustering |

---

### Facteur de charge et rehashing

Le **facteur de charge** $\lambda = n/N$ mesure le taux de remplissage de la table.

| Stratégie | Seuil recommandé | Raison |
| --- | --- | --- |
| Chaînage séparé | $\lambda < 0.9$ (Java : $< 0.75$) | Au-delà, les buckets deviennent trop longs |
| Adressage ouvert | $\lambda < 0.5$ | Au-delà, le clustering dégrade fortement les performances |

**Rehashing** : Quand $\lambda$ dépasse le seuil, on :

1. Crée un nouveau tableau de taille $\approx 2N$ (idéalement un nombre premier)
2. Recalcule la compression pour chaque entrée (le hash code ne change pas)
3. Réinsère toutes les entrées dans le nouveau tableau

Le coût du rehashing est **O(n)**, mais amorti sur les insertions, il ajoute seulement **O(1) amorti** par opération.

---

### Comparaison des complexités

| Méthode | Liste non triée | Table de hachage (attendu) | Table de hachage (pire cas) |
| --- | --- | --- | --- |
| `get` | O(n) | **O(1)** | O(n) |
| `put` | O(n) | **O(1)** | O(n) |
| `remove` | O(n) | **O(1)** | O(n) |
| `size`, `isEmpty` | O(1) | O(1) | O(1) |
| `entrySet`, `keySet`, `values` | O(n) | O(n) | O(n) |

!!! info "Quand le pire cas survient-il ?"

    Le pire cas O(n) se produit quand **toutes les clés ont le même hash** (collision totale). Cela peut arriver avec une mauvaise fonction de hachage ou lors d'une attaque délibérée (voir l'anecdote sur la sécurité dans le livre, p. 421).

---

## Partie 1 — Exercices théoriques

### 1.1 Vrai ou Faux

Pour chaque énoncé, indiquez s'il est **vrai** ou **faux** et justifiez votre réponse.

??? question "Question 1 — Unicité des clés"
    Dans une map, si on appelle `put(k, v1)` puis `put(k, v2)` avec la même clé `k`, la map contiendra deux entrées pour la clé `k`.

    ??? success "Réponse"
        **Faux.** Les clés d'une map sont **uniques**. Le deuxième appel `put(k, v2)` **remplace** la valeur existante `v1` par `v2` et retourne `v1` (l'ancienne valeur).

        Après les deux appels, la map contient une seule entrée `(k, v2)`.

        ```java
        Map<String, Integer> m = new HashMap<>();
        m.put("a", 1);    // retourne null (clé nouvelle)
        m.put("a", 2);    // retourne 1 (ancienne valeur remplacée)
        // m contient uniquement {("a", 2)}
        ```

??? question "Question 2 — Complexité de UnsortedTableMap"
    Dans une `UnsortedTableMap`, l'opération `put(k, v)` s'exécute toujours en O(1) puisqu'il suffit d'ajouter à la fin de la liste.

    ??? success "Réponse"
        **Faux.** Avant d'ajouter, `put` doit d'abord **vérifier si la clé existe déjà** dans la liste, ce qui nécessite un parcours complet en **O(n)**.

        - Si la clé n'existe pas : parcours O(n) + ajout O(1) = **O(n)**
        - Si la clé existe déjà : parcours O(n) + remplacement O(1) = **O(n)**

        Dans les deux cas, la complexité est **O(n)**.

        **Piège classique :** Oublier le coût de vérification de l'unicité des clés.

??? question "Question 3 — Garantie d'absence de collisions"
    Une bonne fonction de hachage garantit qu'il n'y aura jamais de collision.

    ??? success "Réponse"
        **Faux.** Par le **principe des tiroirs** (pigeonhole principle), si le nombre de clés possibles est supérieur à la taille $N$ de la table, des collisions sont **inévitables**.

        Une bonne fonction de hachage **minimise** les collisions en répartissant les clés uniformément, mais ne peut pas les éliminer complètement.

        Par exemple, pour des chaînes de caractères (infinité de clés possibles) dans une table de taille $N = 1000$, au moins deux chaînes auront forcément le même hash.

??? question "Question 4 — Contrat hashCode et equals"
    Si deux objets Java ont le même `hashCode()`, alors ils sont nécessairement égaux selon `equals()`.

    ??? success "Réponse"
        **Faux.** Le contrat Java est **asymétrique** :

        - Si `a.equals(b)` est `true`, alors `a.hashCode() == b.hashCode()` **doit** être `true`
        - Si `a.hashCode() == b.hashCode()`, `a.equals(b)` **peut** être `true` ou `false` (collision de hash)

        Deux objets différents peuvent avoir le même hash code — c'est précisément ce qu'on appelle une **collision**. L'implication ne va que dans un sens :

        $$\text{equals} \Rightarrow \text{même hashCode} \quad \text{(obligatoire)}$$
        $$\text{même hashCode} \not\Rightarrow \text{equals} \quad \text{(collision possible)}$$

        **Piège classique :** Confondre les deux directions du contrat. Le hash code est une **condition nécessaire** mais pas suffisante pour l'égalité.

??? question "Question 5 — Facteur de charge et rehashing"
    Le rehashing est déclenché quand la table de hachage est complètement pleine ($\lambda = 1$).

    ??? success "Réponse"
        **Faux.** Le rehashing est déclenché **bien avant** que la table ne soit pleine, car les performances se dégradent à mesure que $\lambda$ augmente.

        - **Chaînage séparé** : rehashing typiquement quand $\lambda > 0.75$ (Java `HashMap`)
        - **Adressage ouvert** : rehashing quand $\lambda > 0.5$

        Attendre $\lambda = 1$ serait désastreux :

        - Avec chaînage séparé, les buckets seraient en moyenne de taille 1, mais certains bien plus longs
        - Avec adressage ouvert, $\lambda = 1$ signifie que la table est **pleine** — impossible d'insérer !

??? question "Question 6 — Clustering et sondage linéaire"
    Le sondage quadratique élimine complètement le problème de clustering du sondage linéaire.

    ??? success "Réponse"
        **Faux.** Le sondage quadratique élimine le **clustering primaire** (groupes contigus de cases occupées), mais crée un **clustering secondaire** : les clés qui ont le même hash initial suivent exactement la même séquence de sondage.

        | Stratégie | Clustering primaire | Clustering secondaire |
        |-----------|--------------------|-----------------------|
        | Linéaire | Oui | Oui |
        | Quadratique | Non | Oui |
        | Double hachage | Non | Non |

        Seul le **double hachage** évite les deux types de clustering, car la séquence de sondage dépend d'une deuxième fonction de hachage $h'(k)$.

??? question "Question 7 — Taille du bucket en chaînage séparé"
    Avec une table de hachage de capacité $N = 10$ contenant $n = 30$ entrées et utilisant le chaînage séparé, chaque bucket contient exactement 3 entrées.

    ??? success "Réponse"
        **Faux.** La taille **attendue** (moyenne) d'un bucket est $n/N = 30/10 = 3$, mais la distribution réelle dépend de la fonction de hachage et des données.

        Certains buckets peuvent être vides et d'autres contenir bien plus de 3 entrées. Avec une bonne fonction de hachage, la distribution se rapproche d'une distribution uniforme, mais les variations existent.

        **Point clé :** $n/N$ est une moyenne statistique, pas une garantie par bucket.

??? question "Question 8 — Avantage de la méthode MAD"
    La méthode de compression MAD ($[(ai + b) \bmod p] \bmod N$) est toujours préférable à la méthode par division ($i \bmod N$).

    ??? success "Réponse"
        **Vrai** (en général). La méthode MAD est plus robuste car elle élimine les **motifs répétés** dans les codes de hachage grâce à la multiplication et l'addition aléatoires.

        Avec la division simple, si les codes de hachage suivent un motif régulier (par exemple $\{200, 210, 220, \ldots\}$), ils peuvent tous tomber dans les mêmes buckets. La méthode MAD brise ces motifs.

        Cependant, la méthode par division reste acceptable si :

        - $N$ est un nombre premier
        - Les codes de hachage sont déjà bien distribués

        **En pratique**, Java utilise une variante de MAD dans `HashMap` pour la compression.

---

### 1.2 Questions à choix multiples

??? question "Question 1 — Trace d'opérations Map"
    On exécute les opérations suivantes sur une map initialement vide. Quel est l'état final de la map ?

    ```
    put(3, A), put(7, B), put(2, C), put(7, D), put(5, E),
    remove(3), put(2, F), remove(9), get(7)
    ```

    - [ ] A) `{(2,F), (5,E), (7,D)}`
    - [ ] B) `{(2,C), (5,E), (7,B)}`
    - [ ] C) `{(2,F), (3,A), (5,E), (7,D)}`
    - [ ] D) `{(2,F), (5,E), (7,B)}`

    ??? success "Réponse"
        **A) `{(2,F), (5,E), (7,D)}`**

        Traçons chaque opération :

        | Opération | Retour | État de la map |
        |-----------|--------|----------------|
        | `put(3,A)` | `null` | `{(3,A)}` |
        | `put(7,B)` | `null` | `{(3,A), (7,B)}` |
        | `put(2,C)` | `null` | `{(3,A), (7,B), (2,C)}` |
        | `put(7,D)` | `B` | `{(3,A), (7,D), (2,C)}` — remplace B par D |
        | `put(5,E)` | `null` | `{(3,A), (7,D), (2,C), (5,E)}` |
        | `remove(3)` | `A` | `{(7,D), (2,C), (5,E)}` |
        | `put(2,F)` | `C` | `{(7,D), (2,F), (5,E)}` — remplace C par F |
        | `remove(9)` | `null` | `{(7,D), (2,F), (5,E)}` — clé 9 absente |
        | `get(7)` | `D` | `{(7,D), (2,F), (5,E)}` — lecture seule |

        **Points clés :**

        - `put(7,D)` remplace l'ancienne valeur B et retourne B
        - `remove(9)` retourne `null` car la clé 9 n'existe pas (pas d'erreur)
        - `get` ne modifie pas la map

??? question "Question 2 — Code de hachage polynomial"
    On calcule le code de hachage polynomial de la chaîne `"ABC"` avec $a = 33$.

    Les valeurs Unicode sont : A = 65, B = 66, C = 67.

    La formule est : $x_0 \cdot a^{n-1} + x_1 \cdot a^{n-2} + \cdots + x_{n-1}$

    Quel est le code de hachage ?

    - [ ] A) 198
    - [ ] B) 73 174
    - [ ] C) 72 843
    - [ ] D) 73 810

    ??? success "Réponse"
        **B) 73 174**

        Calcul avec la méthode de Horner (plus efficace) :

        $$h = x_{n-1} + a(x_{n-2} + a(\cdots + a \cdot x_0)\cdots)$$

        Pour `"ABC"` ($x_0 = 65, x_1 = 66, x_2 = 67$) :

        1. Commencer avec $x_0 = 65$
        2. $65 \times 33 + 66 = 2145 + 66 = 2211$
        3. $2211 \times 33 + 67 = 72963 + 67 = 73030$

        Attendez, vérifions avec la formule directe :

        $$h = 65 \times 33^2 + 66 \times 33 + 67 = 65 \times 1089 + 2178 + 67 = 70785 + 2178 + 67 = 73030$$

        Hmm, recalculons. En fait, avec Horner dans l'ordre $x_0, x_1, x_2$ :

        $$h = ((x_0 \cdot a) + x_1) \cdot a + x_2 = ((65 \times 33) + 66) \times 33 + 67$$
        $$= (2145 + 66) \times 33 + 67 = 2211 \times 33 + 67 = 72963 + 67 = 73030$$

        Vérifions la formule explicite : $65 \times 33^2 + 66 \times 33^1 + 67 \times 33^0 = 70785 + 2178 + 67 = 73030$.

        Aucune des réponses ne correspond exactement. Recalculons avec l'autre convention (ordre inversé) :

        $$h = 67 \times 33^2 + 66 \times 33 + 65 = 67 \times 1089 + 2178 + 65 = 72963 + 2178 + 65 = 75206$$

        Hmm, prenons plutôt les bonnes valeurs. En réalité :

        $$h = 65 \times 33^2 + 66 \times 33 + 67 = 70785 + 2178 + 67 = 73030$$

        La réponse la plus proche est **B) 73 174**. En fait, rectifions les choix :

        Le calcul exact donne **73 030**. Corrigeons :

        - [ ] A) 198
        - [ ] B) 73 030
        - [ ] C) 72 843
        - [ ] D) 75 206

        **B) 73 030**

        **Méthode de Horner** (évite de calculer les puissances) :

        ```
        h = 0
        h = h × 33 + 65 = 65
        h = h × 33 + 66 = 2211
        h = h × 33 + 67 = 73030
        ```

        C'est exactement la même technique que l'évaluation d'un polynôme : on multiplie le résultat courant par $a$ et on ajoute le prochain coefficient.

??? question "Question 3 — Compression MAD"
    On utilise la méthode MAD avec $a = 3$, $b = 5$, $p = 11$, $N = 7$.

    Quel est l'indice dans la table pour un code de hachage $i = 15$ ?

    Formule : $[(ai + b) \bmod p] \bmod N$

    - [ ] A) 1
    - [ ] B) 3
    - [ ] C) 5
    - [ ] D) 6

    ??? success "Réponse"
        **C) 5**

        Calcul étape par étape :

        1. $ai + b = 3 \times 15 + 5 = 50$
        2. $50 \bmod 11 = 50 - 4 \times 11 = 50 - 44 = 6$
        3. $6 \bmod 7 = 6$

        Attendez, vérifions : $50 / 11 = 4$ reste $6$. Donc $50 \bmod 11 = 6$.

        $6 \bmod 7 = 6$.

        La réponse est **D) 6**.

        Corrigeons les choix. Calcul pour d'autres valeurs de $i$ :

        | $i$ | $ai + b$ | $\bmod p$ | $\bmod N$ |
        |-----|----------|-----------|-----------|
        | 15 | 50 | 6 | **6** |
        | 10 | 35 | 2 | **2** |
        | 22 | 71 | 5 | **5** |
        | 8 | 29 | 7 | **0** |

        Notez comment la méthode MAD distribue des codes de hachage variés à travers différents indices de la table.

??? question "Question 4 — Collision en chaînage séparé"
    On insère les clés $\{18, 41, 22, 44, 59, 32, 31, 73\}$ dans une table de hachage de taille $N = 13$ avec chaînage séparé. La fonction de compression est $h(k) = k \bmod 13$.

    Quel bucket contient le plus d'entrées ?

    - [ ] A) Bucket 5 (3 entrées)
    - [ ] B) Bucket 6 (3 entrées)
    - [ ] C) Bucket 5 (2 entrées)
    - [ ] D) Bucket 9 (2 entrées)

    ??? success "Réponse"
        **C) Bucket 5 (2 entrées)**

        Calculons le hash de chaque clé :

        | Clé | $k \bmod 13$ | Bucket |
        |-----|-------------|--------|
        | 18 | 5 | 5 |
        | 41 | 2 | 2 |
        | 22 | 9 | 9 |
        | 44 | 5 | 5 |
        | 59 | 7 | 7 |
        | 32 | 6 | 6 |
        | 31 | 5 | 5 |
        | 73 | 8 | 8 |

        Distribution :

        ```
        Bucket 2 : [41]
        Bucket 5 : [18, 44, 31]    ← 3 entrées (le plus chargé)
        Bucket 6 : [32]
        Bucket 7 : [59]
        Bucket 8 : [73]
        Bucket 9 : [22]
        ```

        En fait, le bucket 5 contient **3 entrées** (18, 44 et 31 ont tous un reste de 5 mod 13).

        La bonne réponse est **A) Bucket 5 (3 entrées)**.

        **Facteur de charge** : $\lambda = 8/13 \approx 0.62$ — acceptable pour le chaînage séparé.

        Notez que malgré un $\lambda$ raisonnable, un bucket a 3 entrées alors que la moyenne est $8/13 \approx 0.6$. C'est normal — la distribution n'est jamais parfaitement uniforme avec des données réelles.

??? question "Question 5 — Choix d'implémentation"
    Un système de cache web doit stocker des paires (URL → contenu de page). Le cache contient environ 10 000 entrées. Les opérations les plus fréquentes sont la recherche d'une URL et l'ajout de nouvelles pages.

    Quelle implémentation de map est la plus adaptée ?

    - [ ] A) `UnsortedTableMap` (liste non triée)
    - [ ] B) `SortedTableMap` (liste triée par clé)
    - [ ] C) `ChainHashMap` (table de hachage avec chaînage)
    - [ ] D) `TreeMap` (arbre binaire de recherche)

    ??? success "Réponse"
        **C) `ChainHashMap` (table de hachage avec chaînage)**

        Analyse des options pour n = 10 000 :

        | Implémentation | `get` | `put` | Justification |
        |---------------|-------|-------|---------------|
        | UnsortedTableMap | O(n) = O(10 000) | O(n) | Beaucoup trop lent |
        | SortedTableMap | O(log n) ≈ O(14) | O(n) | `put` trop lent (décalage) |
        | **ChainHashMap** | **O(1) attendu** | **O(1) attendu** | Optimal pour ce cas |
        | TreeMap | O(log n) ≈ O(14) | O(log n) | Bon, mais plus lent que le hash |

        La table de hachage est le choix naturel car :

        - Les opérations dominantes (`get` et `put`) sont en **O(1) attendu**
        - Les URLs sont de bonnes clés pour le hachage (la classe `String` a un excellent `hashCode()`)
        - On n'a pas besoin d'itérer dans un ordre particulier

        On préférerait un `TreeMap` seulement si on avait besoin de parcourir les URLs en ordre alphabétique ou de faire des requêtes par plage.

---

### 1.3 Exercices de Trace

??? question "Exercice 1 — Trace complète d'une Map"
    On exécute les opérations suivantes sur une map initialement vide. Pour chaque opération, donnez la valeur de retour et l'état de la map.

    ```
    put(5,A), put(7,B), put(2,C), put(8,D), put(2,E),
    get(7), get(4), get(2), size(), remove(5), remove(2),
    get(2), isEmpty(), put(9,F), size()
    ```

    ??? success "Réponse"
        | # | Opération | Retour | État de la map |
        |---|-----------|--------|----------------|
        | 1 | `put(5,A)` | `null` | `{(5,A)}` |
        | 2 | `put(7,B)` | `null` | `{(5,A), (7,B)}` |
        | 3 | `put(2,C)` | `null` | `{(5,A), (7,B), (2,C)}` |
        | 4 | `put(8,D)` | `null` | `{(5,A), (7,B), (2,C), (8,D)}` |
        | 5 | `put(2,E)` | `C` | `{(5,A), (7,B), (2,E), (8,D)}` |
        | 6 | `get(7)` | `B` | inchangé |
        | 7 | `get(4)` | `null` | inchangé |
        | 8 | `get(2)` | `E` | inchangé |
        | 9 | `size()` | `4` | inchangé |
        | 10 | `remove(5)` | `A` | `{(7,B), (2,E), (8,D)}` |
        | 11 | `remove(2)` | `E` | `{(7,B), (8,D)}` |
        | 12 | `get(2)` | `null` | inchangé |
        | 13 | `isEmpty()` | `false` | inchangé |
        | 14 | `put(9,F)` | `null` | `{(7,B), (8,D), (9,F)}` |
        | 15 | `size()` | `3` | inchangé |

        **Points clés :**

        - `put(2,E)` à l'étape 5 retourne `C` (l'ancienne valeur) — la clé 2 existait déjà
        - `get(4)` retourne `null` — la clé 4 n'a jamais été insérée
        - `get(2)` à l'étape 12 retourne `null` — la clé 2 a été supprimée à l'étape 11
        - `remove` sur une clé absente (si c'était le cas) retournerait `null` sans erreur

??? question "Exercice 2 — Insertion dans une table avec chaînage séparé"
    Soit une table de hachage de taille $N = 7$ utilisant le chaînage séparé et la fonction de compression $h(k) = k \bmod 7$.

    Insérez les clés suivantes dans l'ordre : **12, 44, 13, 88, 23, 94, 11, 39, 20, 16, 5**.

    Pour chaque bucket non vide, listez les clés qu'il contient (dans l'ordre d'insertion).

    ??? success "Réponse"
        Calculons le hash de chaque clé :

        | Clé | $k \bmod 7$ | Bucket |
        |-----|------------|--------|
        | 12 | 5 | 5 |
        | 44 | 2 | 2 |
        | 13 | 6 | 6 |
        | 88 | 4 | 4 |
        | 23 | 2 | 2 |
        | 94 | 3 | 3 |
        | 11 | 4 | 4 |
        | 39 | 4 | 4 |
        | 20 | 6 | 6 |
        | 16 | 2 | 2 |
        | 5 | 5 | 5 |

        **État final de la table :**

        ```
        Bucket 0 : [ ]
        Bucket 1 : [ ]
        Bucket 2 : [44] → [23] → [16]     (3 entrées)
        Bucket 3 : [94]                     (1 entrée)
        Bucket 4 : [88] → [11] → [39]      (3 entrées)
        Bucket 5 : [12] → [5]              (2 entrées)
        Bucket 6 : [13] → [20]             (2 entrées)
        ```

        **Analyse :**

        - Facteur de charge : $\lambda = 11/7 \approx 1.57$ — **trop élevé !**
        - En pratique, un rehashing aurait dû être déclenché (seuil Java = 0.75)
        - Le rehashing aurait lieu après l'insertion de $\lceil 0.75 \times 7 \rceil = 6$ entrées (après l'insertion de 94)
        - Nouvelle taille : $\approx 2 \times 7 = 14$ (ou le prochain premier, soit 17)

        **Sans rehashing** (pour l'exercice), les buckets 2 et 4 ont 3 entrées chacun, ce qui est bien au-dessus de la moyenne attendue.

??? question "Exercice 3 — Insertion et suppression avec sondage linéaire"
    Soit une table de hachage de taille $N = 11$ utilisant le sondage linéaire et la fonction de compression $h(k) = k \bmod 11$.

    **Partie A :** Insérez les clés suivantes dans l'ordre : **20, 36, 42, 5, 16, 53, 10**.

    **Partie B :** Supprimez la clé **42**, puis insérez la clé **31**. Montrez l'état final du tableau.

    ??? success "Réponse"
        **Partie A — Insertions :**

        | Clé | $h(k)$ | Sondage | Index final |
        |-----|--------|---------|-------------|
        | 20 | 9 | libre | 9 |
        | 36 | 3 | libre | 3 |
        | 42 | 9 | 9 occupé → 10 | 10 |
        | 5 | 5 | libre | 5 |
        | 16 | 5 | 5 occupé → 6 | 6 |
        | 53 | 9 | 9 occupé → 10 occupé → 0 | 0 |
        | 10 | 10 | 10 occupé → 0 occupé → 1 | 1 |

        ```
        Index :  0    1    2    3    4    5    6    7    8    9   10
               [53] [10] [  ] [36] [  ] [ 5] [16] [  ] [  ] [20] [42]
        ```

        ---

        **Partie B — Suppression de 42, insertion de 31 :**

        **Suppression de 42 :**

        - $h(42) = 9$ → index 9 contient 20 (pas 42)
        - Sonde index 10 → contient 42 → trouvé !
        - Marquer index 10 comme `DEFUNCT`

        ```
        Index :  0    1    2    3    4    5    6    7    8    9   10
               [53] [10] [  ] [36] [  ] [ 5] [16] [  ] [  ] [20] [DEF]
        ```

        **Insertion de 31 :**

        - $h(31) = 9$ → index 9 occupé (20)
        - Sonde 10 → `DEFUNCT` → on **note** cet emplacement disponible (avail = 10)
        - Sonde 0 → occupé (53), pas la clé 31
        - Sonde 1 → occupé (10), pas la clé 31
        - Sonde 2 → **vide** (`null`) → la clé 31 n'existe pas dans la table
        - On insère à l'index `avail = 10` (le premier slot DEFUNCT rencontré)

        ```
        Index :  0    1    2    3    4    5    6    7    8    9   10
               [53] [10] [  ] [36] [  ] [ 5] [16] [  ] [  ] [20] [31]
        ```

        **Points clés :**

        - On ne peut pas s'arrêter au premier `DEFUNCT` lors d'une **recherche** — la clé cherchée pourrait être plus loin
        - Mais on peut **réutiliser** un slot `DEFUNCT` pour une insertion, si la clé n'existe pas au-delà
        - On s'arrête seulement quand on trouve un slot **vide** (`null`), qui marque la fin de la chaîne de sondage

---

## Partie 2 — Application et Synthèse

### Scénarios de conception

??? question "Scénario 1 — Annuaire téléphonique"
    **Contexte :** Vous devez implémenter un annuaire téléphonique pour une petite entreprise de 50 employés. Les opérations principales sont :

    1. Rechercher le numéro d'un employé par son nom (très fréquent)
    2. Ajouter un nouvel employé (rare)
    3. Modifier le numéro d'un employé (occasionnel)
    4. Lister tous les employés par ordre alphabétique (mensuel)

    **Questions :**

    1. Quelle implémentation de map choisir pour les opérations 1-3 ?
    2. Pour l'opération 4 (liste ordonnée), est-ce que votre choix reste optimal ? Quelle alternative ?
    3. Si l'entreprise grandit à 50 000 employés, votre choix change-t-il ?

    ??? success "Réponse"
        **1. Pour les opérations 1-3 :**

        Avec seulement 50 employés, **toutes les implémentations** sont acceptables — la différence entre O(1) et O(50) est négligeable en pratique.

        Cependant, pour de bonnes habitudes :

        | Implémentation | `get` (recherche) | `put` (ajout/modif) |
        |---------------|-------------------|---------------------|
        | UnsortedTableMap | O(50) | O(50) |
        | **HashMap** | **O(1)** | **O(1)** |
        | TreeMap | O(log 50) ≈ O(6) | O(log 50) |

        La `HashMap` est le choix naturel pour les opérations dominantes (recherche par nom).

        ---

        **2. Pour l'opération 4 (liste ordonnée) :**

        La `HashMap` ne maintient **aucun ordre**. Pour lister par ordre alphabétique, il faudrait trier les clés en O(n log n) à chaque rapport.

        Alternatives :

        - **TreeMap** : Maintient les clés en ordre → `keySet()` donne directement l'ordre alphabétique
        - **HashMap + tri mensuel** : Garder la HashMap pour les opérations fréquentes, et trier une fois par mois

        Comme l'opération 4 est rare (mensuelle), le compromis **HashMap + tri occasionnel** est préférable.

        ---

        **3. Avec 50 000 employés :**

        Le choix de la HashMap devient **critique** :

        | Implémentation | `get` pour n = 50 000 |
        |---------------|----------------------|
        | UnsortedTableMap | O(50 000) — inacceptable |
        | **HashMap** | **O(1)** — optimal |
        | TreeMap | O(log 50 000) ≈ O(16) — acceptable |

        Avec 50 000 entrées, la UnsortedTableMap est clairement à éviter. La HashMap reste le meilleur choix si l'ordre n'est pas nécessaire.

??? question "Scénario 2 — Conception d'une fonction de hachage"
    **Contexte :** Vous implémentez une table de hachage pour stocker des objets `Étudiant` identifiés par leur **matricule** (entier de 7 chiffres, ex: 2012345). La table a une capacité initiale de $N = 17$.

    **Questions :**

    1. Proposez une fonction de compression simple. Testez-la avec les matricules : 2012345, 2012346, 2012347, 2012350, 2055555.
    2. Y a-t-il un risque de clustering avec ces données ? Pourquoi ?
    3. Proposez une meilleure fonction de compression avec la méthode MAD. Choisissez $p = 19$, $a = 7$, $b = 3$. Recalculez les indices.
    4. Si la table contient actuellement 10 entrées et qu'on utilise le chaînage séparé, faut-il rehash ? Et avec le sondage linéaire ?

    ??? success "Réponse"
        **1. Compression simple ($h(k) = k \bmod 17$) :**

        | Matricule | $k \bmod 17$ |
        |-----------|-------------|
        | 2012345 | $2012345 \bmod 17 = 2012345 - 118373 \times 17 = 2012345 - 2012341 = 4$ |
        | 2012346 | 5 |
        | 2012347 | 6 |
        | 2012350 | 9 |
        | 2055555 | $2055555 \bmod 17 = 14$ |

        ---

        **2. Risque de clustering :**

        **Oui !** Les matricules consécutifs (2012345, 2012346, 2012347) produisent des indices consécutifs (4, 5, 6). Avec le sondage linéaire, ces entrées formeraient un cluster.

        C'est un problème courant avec la division simple quand les clés sont **séquentielles**.

        ---

        **3. Compression MAD ($[(7i + 3) \bmod 19] \bmod 17$) :**

        | Matricule $i$ | $7i + 3$ | $\bmod 19$ | $\bmod 17$ |
        |--------------|----------|-----------|-----------|
        | 2012345 | 14 086 418 | $14086418 \bmod 19 = 5$ | 5 |
        | 2012346 | 14 086 425 | $14086425 \bmod 19 = 12$ | 12 |
        | 2012347 | 14 086 432 | $14086432 \bmod 19 = 0$ | 0 |
        | 2012350 | 14 086 453 | $14086453 \bmod 19 = 2$ | 2 |
        | 2055555 | 14 388 888 | $14388888 \bmod 19 = 8$ | 8 |

        Les indices (5, 12, 0, 2, 8) sont **bien distribués** — plus de clustering entre matricules consécutifs ! La méthode MAD a brisé le motif séquentiel.

        ---

        **4. Faut-il rehash ? ($n = 10$, $N = 17$)**

        $$\lambda = 10/17 \approx 0.59$$

        - **Chaînage séparé** ($\lambda < 0.75$) : $0.59 < 0.75$ → **pas de rehashing nécessaire**
        - **Sondage linéaire** ($\lambda < 0.5$) : $0.59 > 0.5$ → **rehashing nécessaire !**

        Avec le sondage linéaire, on devrait redimensionner à environ $2 \times 17 = 34$ (ou le prochain premier, soit 37).

---

## Récapitulatif

### Composantes d'une fonction de hachage

```
Clé k  →  hashCode()  →  entier h  →  compression(h)  →  indice [0, N-1]
              │                              │
    Indépendant de N              Dépend de la taille N
    (portable entre tables)       (change lors du rehashing)
```

### Comparaison des stratégies de collision

| Critère | Chaînage séparé | Sondage linéaire | Double hachage |
|---------|----------------|------------------|----------------|
| Structure secondaire | Oui (map par bucket) | Non | Non |
| $\lambda$ peut dépasser 1 | Oui | Non | Non |
| Seuil de rehash | $\lambda < 0.75$ | $\lambda < 0.5$ | $\lambda < 0.5$ |
| Clustering | Non | Oui (primaire) | Non |
| Suppression | Simple | Nécessite DEFUNCT | Nécessite DEFUNCT |
| Implémentation | Plus simple | Plus compacte | Plus complexe |

### Pièges classiques — Résumé

| Piège | Réalité |
|-------|---------|
| `put(k,v)` ajoute toujours une nouvelle entrée | Non — remplace la valeur si la clé existe déjà |
| UnsortedTableMap.put est O(1) | Non — O(n) car il faut vérifier l'existence de la clé |
| Une bonne fonction de hachage élimine les collisions | Non — elle les minimise, mais ne peut pas les éliminer |
| `hashCode()` identique ⇒ `equals()` vrai | Non — la réciproque n'est pas vraie (collision possible) |
| Le rehashing se fait quand la table est pleine | Non — il se fait quand $\lambda$ dépasse un seuil (0.5 à 0.75) |
| Sondage quadratique élimine tout clustering | Non — il élimine le clustering primaire mais pas secondaire |
| Table de hachage O(1) garanti | Non — O(1) **attendu**, O(n) dans le pire cas |

### Complexités à connaître

| Opération | UnsortedTableMap | Table de hachage (attendu) | Table de hachage (pire cas) |
|-----------|-----------------|---------------------------|---------------------------|
| `get(k)` | O(n) | **O(1)** | O(n) |
| `put(k, v)` | O(n) | **O(1)** | O(n) |
| `remove(k)` | O(n) | **O(1)** | O(n) |
| `size()`, `isEmpty()` | O(1) | O(1) | O(1) |
| `entrySet()`, `keySet()`, `values()` | O(n) | O(n) | O(n) |

---

## Références

* Goodrich, Tamassia, Goldwasser. *Data Structures and Algorithms in Java*, 6th Edition.
    * Chapitre 10.1 : Maps
    * Chapitre 10.2 : Hash Tables
* Documentation Java :
    * [`java.util.Map`](https://docs.oracle.com/javase/8/docs/api/java/util/Map.html)
    * [`java.util.HashMap`](https://docs.oracle.com/javase/8/docs/api/java/util/HashMap.html)
