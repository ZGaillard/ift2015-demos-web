# Démonstration 6 : Maps et Tables de Hachage

Cette démonstration couvre les **chapitres 10.1 (Maps)** et **10.2 (Hash Tables)** du livre *Data Structures and Algorithms in Java (6th ed.)*.

!!! abstract "Objectifs d'apprentissage"

    - Comprendre l'ADT Map et ses opérations fondamentales
    - Implémenter une map simple avec `UnsortedTableMap`
    - Maîtriser les fonctions de hachage (codes + compression)
    - Distinguer chaînage séparé et adressage ouvert
    - Analyser le rôle du facteur de charge et du rehashing
    - Comprendre le contrat `hashCode()`/`equals()` en Java

---

# Partie I — Théorie

## 1. ADT Map (§10.1.1)

Une **map** (tableau associatif) stocke des paires **clé–valeur** $(k, v)$ appelées **entrées** (*entries*).

Les clés sont **uniques** : l'association clé → valeur définit une *fonction* (au sens mathématique).

### Opérations fondamentales

| Opération     | Description                                                  |
|---------------|--------------------------------------------------------------|
| `get(k)`      | Retourne la valeur associée à $k$, ou `null` si absente      |
| `put(k, v)`   | Insère $(k,v)$ ou remplace la valeur existante ; retourne l'ancienne valeur ou `null` |
| `remove(k)`   | Supprime l'entrée et retourne sa valeur, ou `null`           |
| `size()`      | Nombre d'entrées                                             |
| `isEmpty()`   | Teste si la map est vide                                     |
| `keySet()`    | Collection itérable des clés                                 |
| `values()`    | Collection itérable des valeurs                              |
| `entrySet()`  | Collection itérable des paires $(k,v)$                       |

!!! warning "Ambiguïté de `null`"

    `get(k) == null` peut signifier deux choses :

    - la clé $k$ est **absente** de la map, ou
    - la clé est **présente** mais associée à la valeur `null`.

    En Java, `containsKey(k)` permet de lever l'ambiguïté.

### Exemple de trace (Exemple 10.1 du livre)

| Opération     | Retour  | État de la map                          |
|---------------|---------|-----------------------------------------|
| `put(5, A)`   | `null`  | {(5,A)}                                 |
| `put(7, B)`   | `null`  | {(5,A), (7,B)}                          |
| `put(2, C)`   | `null`  | {(5,A), (7,B), (2,C)}                   |
| `put(2, E)`   | `C`     | {(5,A), (7,B), (2,E)}                   |
| `get(7)`      | `B`     | inchangé                                |
| `get(4)`      | `null`  | inchangé                                |
| `remove(5)`   | `A`     | {(7,B), (2,E)}                          |

---

## 2. Implémentation simple : UnsortedTableMap (§10.1.4)

Stockage des entrées dans une `ArrayList` non triée.

Chaque opération nécessite un **parcours linéaire** pour chercher la clé.

| Méthode    | Complexité |
|------------|------------|
| `get(k)`   | $O(n)$     |
| `put(k,v)` | $O(n)$     |
| `remove(k)`| $O(n)$     |

!!! tip "Astuce suppression"

    Pour éviter un décalage $O(n)$ lors du `remove`, on remplace l'entrée supprimée par la **dernière entrée** du tableau, puis on retire la dernière position → suppression en $O(1)$ une fois l'index trouvé.

---

## 3. Tables de hachage (§10.2)

**Objectif** : obtenir `get`, `put`, `remove` en **$O(1)$ attendu**.

### Idée fondamentale

Utiliser une **fonction de hachage** $h$ pour transformer une clé $k$ en un indice $h(k) \in [0, N-1]$ dans un **bucket array** de capacité $N$.

$$\text{Clé } k \;\xrightarrow{\text{hashCode()}}\; \text{entier} \;\xrightarrow{\text{compression}}\; \text{indice } \in [0, N-1]$$

### Deux étapes distinctes (§10.2.1)

1. **Code de hachage** (*hash code*) : transforme la clé en entier — **indépendant** de $N$.
2. **Fonction de compression** : ramène l'entier dans $[0, N-1]$ — **dépend** de $N$.

L'avantage de cette séparation : si on redimensionne la table, seule la compression change ; le `hashCode()` de chaque objet reste le même.

---

## 4. Codes de hachage (§10.2.1)

### Représentation binaire

Pour les types tenant sur $\leq$ 32 bits (`byte`, `short`, `int`, `char`), on utilise directement la valeur entière. Pour les types 64 bits (`long`, `double`), on combine les 32 bits de poids fort et de poids faible (somme ou XOR).

### Code polynomial

Pour les chaînes et objets de longueur variable $(x_0, x_1, \ldots, x_{n-1})$, on choisit une constante $a \neq 0, 1$ et on calcule :

$$x_0 \cdot a^{n-1} + x_1 \cdot a^{n-2} + \cdots + x_{n-2} \cdot a + x_{n-1}$$

Évaluation efficace par la **règle de Horner** :

$$x_{n-1} + a\bigl(x_{n-2} + a(x_{n-3} + \cdots + a(x_1 + a \cdot x_0) \cdots)\bigr)$$

Les valeurs $a \in \{33, 37, 39, 41\}$ sont particulièrement bonnes pour les chaînes anglaises (< 7 collisions sur 50 000 mots).

### Cyclic-shift

Variante qui remplace la multiplication par $a$ par un **décalage cyclique** des bits. Un décalage de **5 bits** est optimal (190 collisions totales sur 230 000 mots anglais, contre 234 735 pour un décalage de 0 qui revient à une simple somme).

```java
static int hashCode(String s) {
    int h = 0;
    for (int i = 0; i < s.length(); i++) {
        h = (h << 5) | (h >>> 27);   // 5-bit cyclic shift
        h += (int) s.charAt(i);
    }
    return h;
}
```

---

## 5. Fonctions de compression (§10.2.1)

### Méthode de division

$$h(k) = i \bmod N$$

Si $N$ est **premier**, la distribution est meilleure. Si $N$ n'est pas premier, des motifs réguliers dans les hash codes se répètent.

### Méthode MAD (*Multiply-Add-and-Divide*)

$$h(k) = \bigl[(a \cdot i + b) \bmod p\bigr] \bmod N$$

où $p$ est un premier $> N$, $a \in [1, p-1]$, $b \in [0, p-1]$, choisis aléatoirement.

MAD élimine les motifs réguliers mieux que la simple division.

---

## 6. Gestion des collisions (§10.2.2)

Une **collision** survient quand deux clés distinctes $k_1 \neq k_2$ ont le même hash : $h(k_1) = h(k_2)$.

### Chaînage séparé (*Separate Chaining*)

Chaque bucket $A[j]$ contient une **petite map secondaire** (typiquement `UnsortedTableMap`) stockant toutes les entrées dont le hash vaut $j$.

- Le facteur de charge $\lambda$ **peut dépasser 1**.
- Suppression simple (pas de sentinelle).
- Coût attendu : $O(1 + \lambda)$ par opération.

### Adressage ouvert (*Open Addressing*)

Les entrées sont stockées **directement dans le tableau**. Si le bucket $A[h(k)]$ est occupé, on sonde les buckets suivants.

| Stratégie           | Séquence de sonde $f(i)$              | Problème principal         |
|---------------------|---------------------------------------|----------------------------|
| Linéaire            | $f(i) = i$                            | Clustering primaire        |
| Quadratique         | $f(i) = i^2$                          | Clustering secondaire      |
| Double hachage      | $f(i) = i \cdot h'(k)$               | Plus coûteux en calcul     |

- $\lambda$ **doit rester $< 1$** (on ne peut pas avoir plus d'entrées que de cases).
- Suppression nécessite un marqueur **DEFUNCT** (sentinelle) pour ne pas casser les chaînes de sondage.

---

## 7. Facteur de charge et rehashing (§10.2.3)

Le **facteur de charge** est $\lambda = n / N$ ($n$ = nombre d'entrées, $N$ = capacité du tableau).

### Seuils recommandés

| Stratégie           | Seuil $\lambda$ max |
|---------------------|----------------------|
| Chaînage séparé     | $< 0.9$ (Java utilise 0.75 par défaut) |
| Adressage ouvert    | $< 0.5$ pour le sondage linéaire       |

### Rehashing

Quand $\lambda$ dépasse le seuil :

1. Créer un **nouveau tableau** de taille $\approx 2N$ (idéalement premier).
2. **Recalculer la compression** (pas le `hashCode()` !) pour chaque entrée.
3. Réinsérer toutes les entrées dans le nouveau tableau.

Le coût du rehashing est **amorti** en $O(1)$ par opération (même raisonnement que le redimensionnement d'un tableau dynamique).

---

## 8. Contrat `hashCode()` / `equals()` en Java (§10.2.1)

**Règle fondamentale** : si `x.equals(y)` retourne `true`, alors `x.hashCode() == y.hashCode()` **doit** être vrai.

La contraposée est **fausse** : deux objets non-égaux *peuvent* avoir le même hash code (c'est une collision).

Si on redéfinit `equals()` sans redéfinir `hashCode()`, des objets « égaux » peuvent atterrir dans des buckets différents → la map ne retrouve plus les entrées.

---

## 9. Complexités résumées (Table 10.2 du livre)

| Méthode                        | `UnsortedTableMap` | Hash Table (attendu) | Hash Table (pire cas) |
|--------------------------------|--------------------|-----------------------|-----------------------|
| `get`                          | $O(n)$             | $O(1)$                | $O(n)$                |
| `put`                          | $O(n)$             | $O(1)$                | $O(n)$                |
| `remove`                       | $O(n)$             | $O(1)$                | $O(n)$                |
| `size`, `isEmpty`              | $O(1)$             | $O(1)$                | $O(1)$                |
| `entrySet`, `keySet`, `values` | $O(n)$             | $O(n)$                | $O(n)$                |

Le pire cas $O(n)$ survient si toutes les clés ont le même hash code.

---

# Partie II — Exercices

## 1. Vrai ou Faux

??? question "Q1 — Unicité des clés"

    Dans une map, deux entrées différentes peuvent avoir la **même clé**.

    ??? success "Réponse"

        **Faux.** Les clés sont **uniques** par définition. Si on appelle `put(k, v2)` alors qu'une entrée $(k, v1)$ existe déjà, la valeur est **remplacée** et l'ancienne valeur $v1$ est retournée. La taille de la map ne change pas.

??? question "Q2 — Complexité de UnsortedTableMap"

    Dans une `UnsortedTableMap`, l'opération `put(k, v)` s'exécute toujours en $O(1)$.

    ??? success "Réponse"

        **Faux.** Avant d'insérer, `put` doit d'abord **chercher** si la clé $k$ existe déjà (via `findIndex`), ce qui nécessite un parcours linéaire → $O(n)$ dans le pire cas. L'insertion elle-même est $O(1)$ si la clé est nouvelle, mais la recherche préalable domine.

??? question "Q3 — Hash code et fonction de compression"

    Le code de hachage d'un objet dépend de la taille $N$ de la table de hachage.

    ??? success "Réponse"

        **Faux.** Le **hash code** est indépendant de $N$ — c'est la **fonction de compression** qui dépend de $N$. C'est l'avantage principal de séparer le hachage en deux étapes : si on redimensionne la table, on change la compression mais pas le hash code de chaque objet.

??? question "Q4 — Chaînage séparé et facteur de charge"

    Avec le chaînage séparé, le facteur de charge $\lambda$ ne peut jamais dépasser 1.

    ??? success "Réponse"

        **Faux.** Avec le chaînage séparé, $\lambda$ **peut dépasser 1** car chaque bucket contient une liste chaînée pouvant accueillir un nombre arbitraire d'entrées. C'est l'**adressage ouvert** qui impose $\lambda \leq 1$ (chaque case ne peut contenir qu'une seule entrée).

??? question "Q5 — Sentinelle DEFUNCT"

    Avec l'adressage ouvert (sondage linéaire), on peut simplement mettre `null` dans une case pour supprimer une entrée.

    ??? success "Réponse"

        **Faux.** Mettre `null` casserait les chaînes de sondage. Si la clé $k_1$ a été insérée après avoir sondé au-delà de la position de $k_2$, supprimer $k_2$ en mettant `null` ferait échouer la recherche de $k_1$ (le sondage s'arrêterait prématurément au `null`). Il faut utiliser un marqueur **DEFUNCT** pour distinguer « case supprimée » de « case jamais utilisée ».

??? question "Q6 — Clustering linéaire"

    Le sondage quadratique résout complètement le problème de clustering dans l'adressage ouvert.

    ??? success "Réponse"

        **Faux.** Le sondage quadratique élimine le **clustering primaire** (paquets contigus) propre au sondage linéaire, mais il introduit son propre problème appelé **clustering secondaire** : les clés qui hashent au même bucket suivent toujours la même séquence de sonde. Le **double hachage** est plus efficace pour éviter les deux formes de clustering.

??? question "Q7 — Contrat hashCode/equals"

    Si deux objets ont le même `hashCode()`, alors `equals()` retourne nécessairement `true`.

    ??? success "Réponse"

        **Faux.** Le contrat dit seulement que `equals() == true` **implique** même `hashCode()`. L'inverse est faux : deux objets non-égaux *peuvent* avoir le même hash code — c'est précisément ce qu'on appelle une **collision**. Seule la direction `equals → hashCode` est garantie.

---

## 2. Choix multiples

??? question "Q1 — Code polynomial"

    On calcule le hash code polynomial de la chaîne `"ABC"` avec $a = 33$, en utilisant les valeurs Unicode $A=65$, $B=66$, $C=67$.

    La formule est : $x_0 \cdot a^2 + x_1 \cdot a + x_2$

    Quel est le résultat ?

    - [ ] A) 198
    - [ ] B) 73 030
    - [ ] C) 2 211
    - [ ] D) 65 033

    ??? success "Réponse"

        **B) 73 030**

        Calcul :

        - $65 \times 33^2 + 66 \times 33 + 67$
        - $= 65 \times 1089 + 66 \times 33 + 67$
        - $= 70\,785 + 2\,178 + 67$
        - $= 73\,030$

        Par Horner : $67 + 33 \times (66 + 33 \times 65) = 67 + 33 \times (66 + 2145) = 67 + 33 \times 2211 = 67 + 72\,963 = 73\,030$ ✓

??? question "Q2 — Compression MAD"

    Soit la compression MAD avec $a = 3$, $b = 5$, $p = 11$, $N = 7$.

    Quel est l'indice de la clé dont le hash code est $i = 15$ ?

    - [ ] A) 1
    - [ ] B) 3
    - [ ] C) 5
    - [ ] D) 6

    ??? success "Réponse"

        **D) 6**

        Calcul : $[(3 \times 15 + 5) \bmod 11] \bmod 7 = [50 \bmod 11] \bmod 7 = 6 \bmod 7 = 6$

??? question "Q3 — Chaînage séparé : bucket le plus chargé"

    Table de taille $N = 13$, compression $h(k) = k \bmod 13$.

    On insère les clés : $\{18, 41, 22, 44, 59, 32, 31, 73\}$.

    Quel bucket contient le plus d'entrées, et combien ?

    - [ ] A) Bucket 9, avec 2 entrées
    - [ ] B) Bucket 5, avec 3 entrées
    - [ ] C) Bucket 6, avec 3 entrées
    - [ ] D) Bucket 2, avec 2 entrées

    ??? success "Réponse"

        **B) Bucket 5, avec 3 entrées**

        Distribution :

        | Clé | $k \bmod 13$ | Bucket |
        |-----|-------------|--------|
        | 18  | 5           | 5      |
        | 41  | 2           | 2      |
        | 22  | 9           | 9      |
        | 44  | 5           | 5      |
        | 59  | 7           | 7      |
        | 32  | 6           | 6      |
        | 31  | 5           | 5      |
        | 73  | 8           | 8      |

        Bucket 5 contient $\{18, 44, 31\}$ → **3 entrées**.

??? question "Q4 — Quand la division suffit-elle ?"

    Dans quelle situation la compression $h(k) = k \bmod N$ est-elle **généralement suffisante** ?

    - [ ] A) Les clés sont des entiers consécutifs (1001, 1002, 1003, …)
    - [ ] B) $N$ est premier et les hash codes sont déjà bien distribués
    - [ ] C) $N$ est une puissance de 2 et les clés ne diffèrent que dans les bits de poids fort
    - [ ] D) La table est presque pleine ($\lambda \approx 1$)

    ??? success "Réponse"

        **B)**

        La méthode de division fonctionne bien quand $N$ est premier et que les hash codes n'ont pas de motif régulier. Le choix premier de $N$ aide à « étaler » la distribution. L'option A fonctionne aussi correctement si $N$ est premier, mais l'option B est plus générale. L'option C est problématique car une puissance de 2 ne conserve que les bits de poids faible — les différences dans les bits de poids fort sont perdues. L'option D concerne la gestion du facteur de charge, pas la qualité de la compression.

??? question "Q5 — Que recalcule-t-on lors d'un rehashing ?"

    Quand on redimensionne une table de hachage, qu'est-ce qui doit être recalculé pour chaque entrée ?

    - [ ] A) Le `hashCode()` de l'objet
    - [ ] B) Le résultat de la fonction de compression (l'indice dans le tableau)
    - [ ] C) La méthode `equals()`
    - [ ] D) La définition du facteur de charge

    ??? success "Réponse"

        **B)**

        Le `hashCode()` d'un objet est une propriété intrinsèque — il ne change pas lors d'un rehashing. Par contre, la **fonction de compression** dépend de la taille $N$ du tableau (par exemple $i \bmod N$). Comme $N$ change, l'indice de chaque entrée dans le nouveau tableau doit être recalculé. C'est d'ailleurs pour cela que la séparation hash code / compression est utile.

??? question "Q6 — Cache web : quelle stratégie ?"

    On conçoit un cache web (clé = URL, valeur = contenu). Les **suppressions sont fréquentes** (les pages expirent régulièrement). Quelle stratégie de collision est la plus adaptée ?

    - [ ] A) Adressage ouvert avec sondage linéaire
    - [ ] B) Adressage ouvert avec double hachage
    - [ ] C) Chaînage séparé
    - [ ] D) Aucune — une `UnsortedTableMap` suffit

    ??? success "Réponse"

        **C) Chaînage séparé**

        La **suppression fréquente** est le critère clé. Avec l'adressage ouvert, chaque suppression nécessite une sentinelle DEFUNCT, ce qui dégrade progressivement les performances (les chaînes de sondage s'allongent avec les DEFUNCT accumulés). Avec le chaînage séparé, la suppression est simple et directe — on retire un nœud de la liste secondaire sans effet de bord sur les autres entrées.

---

## 3. Questions avancées

??? question "Q7 — Facteur de charge numérique"

    Une table de hachage utilise le **chaînage séparé** avec $N = 100$ buckets et contient $n = 250$ entrées. On suppose un hachage **uniforme** (chaque bucket est aussi probable qu'un autre).

    Quel est le nombre **attendu** d'éléments examinés lorsque que l'on appelle `get(k)` ?

    - [ ] A) 1
    - [ ] B) 2.5
    - [ ] C) 25
    - [ ] D) 250

    ??? success "Réponse"

        **B) 2.5**

        Le facteur de charge est $\lambda = n/N = 250/100 = 2.5$.

        Avec un hachage uniforme, la taille attendue de chaque bucket est $\lambda = 2.5$.  Le coût d'un `get` réussi est proportionnel à la taille du bucket contenant la clé → en moyenne **2.5 éléments** à examiner.

??? question "Q8 — Quand le $O(1)$ disparaît"

    On utilise une table de hachage avec une capacité **fixe** $N$ (pas de rehashing). On insère des entrées sans jamais redimensionner.

    Que devient la complexité attendue de `get(k)` quand $n \to \infty$ ? Justifiez.

    ??? success "Réponse"

        Le coût attendu est $O(1 + \lambda) = O(1 + n/N)$.

        Si $N$ est fixe et $n \to \infty$, alors $\lambda \to \infty$ et le coût devient **$O(n)$** — on perd l'avantage du hachage.

        C'est exactement pour cela que le **rehashing** est nécessaire : il maintient $\lambda$ borné par une constante, garantissant le $O(1)$ attendu. Le coût du rehashing lui-même est amorti en $O(1)$ par opération.

??? question "Q9 — Construire un `hashCode()` pathologique"

    Donnez une implémentation Java valide de `hashCode()` qui **respecte le contrat** `equals`/`hashCode` mais qui cause le **pire cas** pour une table de hachage. Expliquez l'effet sur les performances.

    ??? success "Réponse"

        ```java
        @Override
        public int hashCode() {
            return 1;  // constante — valide mais catastrophique
        }
        ```

        **Pourquoi c'est valide** : le contrat exige que `equals == true` implique même `hashCode`. Ici, *tous* les objets ont le même hash code, ce qui satisfait trivialement la condition (même `hashCode` pour tout le monde, a fortiori pour les objets égaux).

        **Effet** : toutes les entrées atterrissent dans le **même bucket**. Avec le chaînage séparé, ce bucket devient une liste de taille $n$ → les opérations `get`, `put`, `remove` dégradent à **$O(n)$** (le même comportement qu'une `UnsortedTableMap`).

        C'est d'ailleurs le vecteur d'attaque DoS décrit dans le livre (§10.2.3) : un attaquant peut précalculer des chaînes qui partagent le même hash code pour saturer un serveur web.

??? question "Q10 — Compromis mémoire : chaînage vs adressage ouvert"

    Comparez le surcoût mémoire du **chaînage séparé** et de l'**adressage ouvert** dans les deux scénarios suivants :

    1. Les objets stockés sont **très petits** (~8 octets).
    2. Les objets stockés sont **très gros** (~2 Ko).

    ??? success "Réponse"

        **Scénario 1 — Petits objets (~8 octets) :**

        Avec le chaînage séparé, chaque entrée a un surcoût de **nœud/référence** (typiquement 16–32 octets par entrée dans la liste secondaire). Pour un payload de 8 octets, le surcoût peut **doubler ou tripler** la mémoire totale.

        Avec l'adressage ouvert, les entrées sont stockées directement dans le tableau — pas de nœud auxiliaire. Le surcoût est uniquement dû aux cases vides (puisqu'on doit maintenir $\lambda < 0.5$, environ la moitié du tableau est vide).

        **→ L'adressage ouvert est plus compact pour les petits objets.**

        **Scénario 2 — Gros objets (~2 Ko) :**

        Le surcoût par nœud du chaînage (16–32 octets) est **négligeable** comparé aux 2 Ko de payload (< 2 %). En revanche, l'adressage ouvert gaspille $\approx$ 50 % de l'espace tableau en cases vides, et chaque case vide occupe la taille d'une entrée complète (ou d'une référence, selon l'implémentation).

        **→ Pour les gros objets, le surcoût relatif du chaînage est minime, et le chaînage offre en plus des suppressions plus simples.**

??? question "Q11 — `equals()` redéfini sans `hashCode()`"

    On crée une classe `Étudiant` avec une redéfinition de `equals()` basée sur le matricule, mais on **oublie** de redéfinir `hashCode()`. On utilise des instances de `Étudiant` comme clés dans une `HashMap` Java.

    Qu'est-ce qui risque de se produire ? Pourquoi ?

    ??? success "Réponse"

        **Le contrat Java est violé** : `equals()` retourne `true` mais `hashCode()` retourne des valeurs différentes (car la version par défaut héritée de `Object` est basée sur l'adresse mémoire).

        **Conséquences concrètes :**

        1. Deux objets `Étudiant` avec le même matricule seront considérés « égaux » par `equals()` mais pourront atterrir dans des **buckets différents** de la `HashMap`.
        2. `map.put(etudiant1, note)` puis `map.get(etudiant2)` (avec `etudiant1.equals(etudiant2)` → `true`) peut retourner `null`, car `etudiant2` est cherché dans un bucket différent de celui où `etudiant1` a été inséré.
        3. On peut se retrouver avec **plusieurs entrées** pour la « même » clé logique, ce qui viole le contrat de la map.

        **Morale** : toute redéfinition de `equals()` doit s'accompagner d'une redéfinition cohérente de `hashCode()`.

---

## 4. Exercices de trace

??? question "Q12 — Trace complète (chaînage séparé)"

    On utilise une table de hachage avec **chaînage séparé** de capacité $N = 7$.

    Les clés sont des chaînes de 2 caractères. Pour une clé $s = s_0 s_1$ :

    - **Hash code** : $hc(s) = 1 \cdot code(s_0) + 2 \cdot code(s_1)$
    - **Compression** : $h(s) = hc(s) \bmod 7$

    On insère en **tête de liste** dans chaque bucket.

    Exécutez la trace suivante et donnez :

    1. la valeur retournée par chaque opération;
    2. l'état final de la table (contenu de chaque bucket non vide).

    Trace :

    1. `put("AX", 10)`
    2. `put("HQ", 20)`
    3. `put("BY", 30)`
    4. `put("CZ", 40)`
    5. `put("DX", 50)`
    6. `get("HQ")`
    7. `remove("BY")`
    8. `put("EY", 60)`
    9. `get("BY")`

    ??? success "Réponse"

        **Calculs de buckets**

        | Clé  | $hc(s)$ | $h(s)=hc \bmod 7$ |
        |------|---------|-------------------|
        | AX   | $65 + 2 \cdot 88 = 241$ | 3 |
        | HQ   | $72 + 2 \cdot 81 = 234$ | 3 |
        | BY   | $66 + 2 \cdot 89 = 244$ | 6 |
        | CZ   | $67 + 2 \cdot 90 = 247$ | 2 |
        | DX   | $68 + 2 \cdot 88 = 244$ | 6 |
        | EY   | $69 + 2 \cdot 89 = 247$ | 2 |

        **Retours des opérations**

        1. `put("AX", 10)` → `null`
        2. `put("HQ", 20)` → `null`
        3. `put("BY", 30)` → `null`
        4. `put("CZ", 40)` → `null`
        5. `put("DX", 50)` → `null`
        6. `get("HQ")` → `20`
        7. `remove("BY")` → `30`
        8. `put("EY", 60)` → `null`
        9. `get("BY")` → `null`

        **État final**

        - Bucket 2 : `("EY",60) -> ("CZ",40)`
        - Bucket 3 : `("HQ",20) -> ("AX",10)`
        - Bucket 6 : `("DX",50)`

??? question "Q13 — Trace complète (adressage ouvert)"

    On utilise une table de hachage avec **adressage ouvert** (sondage linéaire), de capacité $N = 11$.

    Pour une clé entière $k$ :

    - **Hash code** : $hc(k) = 2k + 5$
    - **Compression MAD** : $h(k) = \bigl[(3 \cdot hc(k) + 7) \bmod 31\bigr] \bmod 11$
    - **Sondage** : $p_t(k) = (h(k) + t) \bmod 11$

    Suppression avec sentinelle `DEFUNCT`.

    Exécutez la trace suivante et donnez :

    1. la valeur retournée par chaque opération;
    2. l'état final de la table (indices non vides).

    Trace :

    1. `put(10, "A")`
    2. `put(41, "B")`
    3. `put(72, "C")`
    4. `put(21, "D")`
    5. `remove(41)`
    6. `get(72)`
    7. `put(103, "E")`
    8. `get(41)`
    9. `put(54, "F")`

    ??? success "Réponse"

        **Indices initiaux**

        | $k$ | $hc(k)=2k+5$ | $h(k)=((3hc+7)\bmod 31)\bmod 11$ |
        |-----|--------------|-----------------------------------|
        | 10  | 25           | 9 |
        | 41  | 87           | 9 |
        | 72  | 149          | 9 |
        | 21  | 47           | 2 |
        | 103 | 211          | 9 |
        | 54  | 113          | 5 |

        Les clés 10, 41, 72, 103 partent toutes de l'indice 9, donc on sonde linéairement.

        **Retours des opérations**

        1. `put(10, "A")` → `null`
        2. `put(41, "B")` → `null`
        3. `put(72, "C")` → `null`
        4. `put(21, "D")` → `null`
        5. `remove(41)` → `"B"` (case marquée `DEFUNCT`)
        6. `get(72)` → `"C"` (la recherche traverse la case `DEFUNCT`)
        7. `put(103, "E")` → `null` (réutilise la première case `DEFUNCT`)
        8. `get(41)` → `null`
        9. `put(54, "F")` → `null`

        **État final (indices non vides)**

        - 0 : `(72,"C")`
        - 2 : `(21,"D")`
        - 5 : `(54,"F")`
        - 9 : `(10,"A")`
        - 10 : `(103,"E")`

---

# Références

Goodrich, Tamassia, Goldwasser — *Data Structures and Algorithms in Java*, 6th ed. — Chapitres 10.1 et 10.2
