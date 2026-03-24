# Démonstration 8 : Map ordonnée et Arbres de recherche

Cette démonstration couvre les **chapitres 10.3** (SortedMap), **10.4** (Listes à enjambements), **11.1** (Arbres binaires de recherche) et **11.2** (Arbres équilibrés — rotations et restructuration trinode) du livre *Data Structures and Algorithms in Java (6th ed.)*.

!!! abstract "Objectifs d'apprentissage"

    - Maîtriser l'ADT SortedMap et ses opérations (§10.3)
    - Comprendre la structure et les performances d'une liste à enjambements (§10.4)
    - Implémenter les opérations fondamentales d'un BST : recherche, insertion, suppression (§11.1)
    - Comprendre pourquoi un BST non équilibré est insuffisant et comment les rotations y remédient (§11.2)
    - Maîtriser la restructuration trinode et distinguer rotation simple (zig-zig) vs double (zig-zag) (§11.2)

---

# Partie I — Théorie

## 1. ADT SortedMap (§10.3)

Une **SortedMap** étend l'ADT Map en exigeant un **ordre total sur les clés**. En plus des opérations de base de Map, elle offre des requêtes par rapport à l'ordre.

### Opérations supplémentaires

| Opération | Description |
|-----------|-------------|
| `firstEntry()` | Entrée de clé minimale (ou `null`) |
| `lastEntry()` | Entrée de clé maximale (ou `null`) |
| `ceilingEntry(k)` | Entrée de plus petite clé $\geq k$ (ou `null`) |
| `floorEntry(k)` | Entrée de plus grande clé $\leq k$ (ou `null`) |
| `lowerEntry(k)` | Entrée de plus grande clé $< k$ (ou `null`) |
| `higherEntry(k)` | Entrée de plus petite clé $> k$ (ou `null`) |
| `subMap(k1, k2)` | Itération sur les entrées dont la clé est dans $[k_1, k_2)$ |

!!! warning "Intervalle semi-ouvert"

    `subMap(k1, k2)` inclut $k_1$ mais **exclut** $k_2$ : l'intervalle est $[k_1, k_2)$.
    Cette convention suit celle de Java (ex. `TreeMap.subMap`).

### Implémentation : SortedTableMap

Stockage des entrées dans un **tableau trié** (ArrayList). La recherche s'effectue par **dichotomie** (`findIndex`) en $O(\log n)$, mais les mises à jour nécessitent un **décalage** des éléments.

| Méthode | Complexité |
|---------|------------|
| `get`, `ceilingEntry`, `floorEntry`, `lowerEntry`, `higherEntry` | $O(\log n)$ |
| `put` (clé déjà présente) | $O(\log n)$ |
| `put` (nouvelle clé) | $O(n)$ — décalage du tableau |
| `remove` | $O(n)$ — décalage du tableau |
| `firstEntry`, `lastEntry` | $O(1)$ |
| `subMap` | $O(s + \log n)$ où $s$ = nombre de résultats |

!!! tip "Cas d'utilisation idéal"

    La `SortedTableMap` convient aux applications avec **beaucoup de lectures et peu d'insertions/suppressions** : annuaire téléphonique, table de constantes physiques, catalogue en lecture seule.

### Application : points maximaux

**Problème** : Parmi des offres (prix, qualité), quelles sont les offres non dominées ?
Une offre $(p_1, q_1)$ **domine** $(p_2, q_2)$ si $p_1 \leq p_2$ et $q_1 \geq q_2$.

Les **points maximaux** (offres non dominées) se calculent en $O(n \log n)$ avec une `SortedMap` en balayant les clés par ordre décroissant et en conservant un maximum courant de qualité.

---

## 2. Listes à enjambements (SkipList) (§10.4)

Une **liste à enjambements** (*skip list*) est une structure **probabiliste** qui implémente la `SortedMap` en $O(\log n)$ attendu pour toutes les opérations.

### Structure

La skip list $S$ est une série de listes $S_0, S_1, \ldots, S_h$ :

- $S_0$ contient **toutes** les entrées (plus les sentinelles $-\infty$ et $+\infty$)
- $S_i$ ($i \geq 1$) contient un **sous-ensemble aléatoire** de $S_{i-1}$, chaque entrée étant promue avec probabilité $\frac{1}{2}$ (pile ou face)
- $S_h$ ne contient que $-\infty$ et $+\infty$

On visualise la structure comme des **tours** (*towers*) : chaque entrée forme une tour verticale traversant les niveaux où elle est présente.

```
S3: -∞ ————————————————————————————————————— +∞
S2: -∞ ——— 17 ——————————— 48 ——————————————— +∞
S1: -∞ ——— 17 —— 25 ———— 48 ——————— 88 ————— +∞
S0: -∞ — 12— 17 — 20— 25 — 31 — 38 — 48 — 55 — 88 — +∞
```

### Recherche

Départ depuis la position la plus haute à gauche. On **descend** si la prochaine clé est trop grande, on **avance** sinon.

```
p = position de départ (sommet gauche)
tant que below(p) ≠ null :
    p = below(p)
    tant que key(next(p)) ≤ k :
        p = next(p)
retourner p
```

### Insertion et suppression

**Insertion** : on appelle d'abord la recherche, puis on détermine la hauteur de la tour du nouvel élément par **lancers de pièce** successifs (on continue à lancer tant qu'on obtient « face »).

**Suppression** : on localise l'entrée et on la retire de **tous les niveaux** de sa tour. Les niveaux qui deviennent vides (sauf sentinelles) sont supprimés.

### Analyse de la hauteur

La probabilité qu'une entrée ait une tour de hauteur $\geq i$ est $\frac{1}{2^{i-1}}$. Il en résulte que la hauteur attendue est $O(\log n)$, et qu'avec probabilité $\geq 1 - \frac{1}{n^2}$ la hauteur ne dépasse pas $3 \log n$.

### Performances

| Méthode | Complexité |
|---------|------------|
| `get`, `put`, `remove` | $O(\log n)$ **attendu** |
| `firstEntry`, `lastEntry` | $O(1)$ |
| `ceilingEntry`, `floorEntry`, `lowerEntry`, `higherEntry` | $O(\log n)$ attendu |
| `subMap` | $O(s + \log n)$ attendu |
| Espace | $O(n)$ attendu |

!!! warning "Attendu ≠ garanti"

    Toutes les complexités sont **probabilistes**. La skip list n'offre **aucune garantie dans le pire cas** : un adversaire capable de contrôler les lancers de pièce pourrait construire une liste de hauteur $n$. En pratique, la probabilité d'un tel événement est négligeable.

---

## 3. Arbres binaires de recherche (BST) (§11.1)

### Définition et invariant

Un **arbre binaire de recherche** (*BST*) est un arbre binaire propre où chaque nœud interne $p$ stocke une paire clé-valeur $(k, v)$ vérifiant :

- Toutes les clés dans le **sous-arbre gauche** de $p$ sont **strictement inférieures** à $k$
- Toutes les clés dans le **sous-arbre droit** de $p$ sont **strictement supérieures** à $k$

!!! tip "Propriété fondamentale"

    Le **parcours infixe** (gauche → nœud → droite) d'un BST visite les clés dans l'**ordre croissant**.

### Recherche — `treeSearch(p, k)`

```
si p est une feuille (externe) :
    retourner p  // clé absente
sinon si k == clé(p) :
    retourner p  // trouvé
sinon si k < clé(p) :
    retourner treeSearch(gauche(p), k)
sinon :
    retourner treeSearch(droite(p), k)
```

Complexité : $O(h)$ où $h$ est la hauteur de l'arbre.

### Insertion — `treeInsert(k, v)`

On appelle `treeSearch` pour trouver la position $p$ :

- Si $k = \text{clé}(p)$ : **remplacer** la valeur
- Sinon (p est une feuille) : **expandExternal(p, (k, v))** — p devient un nœud interne avec deux feuilles vides

### Suppression — 3 cas

**Cas 1 — Nœud avec 0 ou 1 enfant interne**

On retire $p$ et on **remonte** son seul enfant interne (ou une feuille si $p$ est une feuille interne).

```
       z                    z
      / \       →          / \
     p   T3            enfant  T3
    / \
 feuille enfant
```

**Cas 2 — Nœud avec 2 enfants internes**

On ne peut pas retirer $p$ directement. On cherche le **prédécesseur** de $p$ : le nœud $r$ avec la plus grande clé strictement inférieure à celle de $p$. Ce nœud se trouve dans le **sous-arbre gauche de $p$, en allant le plus à droite possible**.

1. On copie l'entrée de $r$ dans $p$
2. On supprime $r$ (qui a au plus un enfant interne → Cas 1)

!!! warning "Pourquoi le prédécesseur ?"

    Le prédécesseur $r$ est le nœud le plus à droite du sous-arbre gauche. Par construction, il n'a **pas d'enfant droit interne** — la suppression de $r$ est donc toujours le Cas 1.

### TreeMap — performances

Toutes les opérations dépendent de $h$ :

| Méthode | Complexité |
|---------|------------|
| `get`, `put`, `remove` | $O(h)$ |
| `firstEntry`, `lastEntry` | $O(h)$ |
| `ceilingEntry`, `floorEntry`, `lowerEntry`, `higherEntry` | $O(h)$ |
| `subMap` | $O(s + h)$ |
| `size`, `isEmpty` | $O(1)$ |

**Meilleur cas** (arbre parfaitement équilibré) : $h = \lfloor \log n \rfloor$, toutes les opérations en $O(\log n)$.
**Pire cas** (insertions dans l'ordre trié) : $h = n - 1$, toutes les opérations en $O(n)$.

---

## 4. Arbres équilibrés — rotations et restructuration trinode (§11.2)

### Motivation

Insérer les clés $1, 2, 3, 4, 5$ dans un BST standard produit une chaîne de hauteur 4 :

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

Toutes les opérations deviennent $O(n)$. L'objectif est de **maintenir automatiquement** $h = O(\log n)$.

### L'opération `rotate(p)`

Une **rotation** déplace un nœud $x$ au-dessus de son parent $y$ en réarrangeant **3 références** (O(1)). La propriété BST est conservée car le sous-arbre médian $T_2$ (les clés entre $x$ et $y$) change de parent mais pas de position dans l'ordre.

**Rotation gauche** (x est enfant droit de y) :

```
    y                x
   / \              / \
  T1   x    →      y   T3
      / \         / \
     T2  T3      T1  T2
```

**Rotation droite** (x est enfant gauche de y) :

```
      y              x
     / \            / \
    x   T3   →    T1   y
   / \                / \
  T1  T2             T2  T3
```

### Restructuration trinode — `restructure(x)`

La **restructuration trinode** prend un nœud $x$, son parent $y$ et son grand-parent $z$, et réorganise ce sous-arbre de hauteur 2 pour **remettre le nœud médian à la racine**.

**Convention** : on renomme $x, y, z$ en $a < b < c$ selon l'ordre infixe. Les quatre sous-arbres de $x$, $y$, $z$ (hors $x$, $y$, $z$ eux-mêmes) sont $T_1, T_2, T_3, T_4$ de gauche à droite.

**Résultat universel** : $b$ devient racine du sous-arbre, avec $a$ comme fils gauche et $c$ comme fils droit.

```
       b
      / \
     a   c
    / \ / \
   T1 T2 T3 T4
```

### Quatre cas

**Cas 1 — Gauche-Gauche (zig-zig gauche)**
$y$ est enfant gauche de $z$, $x$ est enfant gauche de $y$ → $a = x$, $b = y$, $c = z$

Opération : **rotation droite simple** autour de $z$ → $y$ devient racine.

```
        z (c)               y (b)
       / \                 / \
     y(b)  T4   →        x(a)  z(c)
     / \                 / \   / \
   x(a) T3              T1 T2 T3  T4
   / \
  T1  T2
```

**Cas 2 — Droite-Droite (zig-zig droit)**
$y$ est enfant droit de $z$, $x$ est enfant droit de $y$ → $a = z$, $b = y$, $c = x$

Opération : **rotation gauche simple** autour de $z$ → $y$ devient racine.

```
  z (a)                    y (b)
  / \                     / \
T1   y(b)       →       z(a)  x(c)
     / \                / \   / \
    T2  x(c)           T1 T2 T3  T4
        / \
       T3  T4
```

**Cas 3 — Gauche-Droite (zig-zag)**
$y$ est enfant gauche de $z$, $x$ est enfant droit de $y$ → $a = y$, $b = x$, $c = z$

Opération : **double rotation** (rotation gauche sur $y$, puis rotation droite sur $z$) → $x$ devient racine.

```
     z (c)                 x (b)
    / \                   / \
  y(a)  T4    →         y(a)  z(c)
  / \                   / \   / \
 T1  x(b)              T1 T2 T3  T4
     / \
    T2  T3
```

**Cas 4 — Droite-Gauche (zig-zag)**
$y$ est enfant droit de $z$, $x$ est enfant gauche de $y$ → $a = z$, $b = x$, $c = y$

Opération : **double rotation** (rotation droite sur $y$, puis rotation gauche sur $z$) → $x$ devient racine.

```
  z (a)                   x (b)
  / \                    / \
T1  y(c)      →        z(a)  y(c)
    / \                / \   / \
  x(b) T4             T1 T2 T3  T4
  / \
 T2  T3
```

### Résumé — qui devient racine ?

| Configuration | Nom | Rotations | Nouvelle racine |
|---------------|-----|-----------|-----------------|
| $y$ gauche de $z$, $x$ gauche de $y$ | zig-zig gauche | Simple (droite) | $y$ |
| $y$ droit de $z$, $x$ droit de $y$ | zig-zig droit | Simple (gauche) | $y$ |
| $y$ gauche de $z$, $x$ droit de $y$ | zig-zag | Double | $x$ |
| $y$ droit de $z$, $x$ gauche de $y$ | zig-zag | Double | $x$ |

!!! danger "Piège classique"

    En **zig-zig**, c'est **$y$ (le parent)** qui devient la nouvelle racine du sous-arbre, pas $x$.
    En **zig-zag**, c'est **$x$** qui devient racine.

### Framework `BalanceableBinaryTree`

Le livre introduit une classe `BalanceableBinaryTree` qui :

- Étend `LinkedBinaryTree` en ajoutant un champ `aux` (entier auxiliaire) à chaque nœud — utile pour stocker la hauteur (AVL), la couleur (Rouge-Noir), etc.
- Expose les méthodes protégées `rotate(p)` et `restructure(x)` aux sous-classes
- Définit trois **points d'extension** (méthodes vides par défaut) :
  - `rebalanceInsert(p)` — appelé après chaque insertion
  - `rebalanceDelete(p)` — appelé après chaque suppression
  - `rebalanceAccess(p)` — appelé après chaque accès (pour les arbres Splay)

Les classes `AVLTreeMap`, `SplayTreeMap` et `RedBlackTreeMap` héritent de ce framework et redéfinissent uniquement les méthodes de rééquilibrage.

---

# Partie II — Exercices

## 1. Vrai ou Faux

### Bloc A — SortedMap et SkipList

??? question "Q1 — Intervalle de `subMap`"

    `subMap(3, 7)` retourne l'entrée de clé 7 si elle existe dans la map.

    ??? success "Réponse"

        **Faux.** L'intervalle de `subMap(k1, k2)` est **$[k_1, k_2)$** : $k_1$ inclus, $k_2$ **exclu**. Pour récupérer la clé 7, il faudrait appeler `subMap(3, 8)` (en supposant des clés entières).

        Cette convention suit celle de Java (`TreeMap.subMap`), mais c'est un piège courant : on associe naturellement un intervalle fermé aux deux bouts.

??? question "Q2 — `put` dans `SortedTableMap` : clé existante"

    Dans une `SortedTableMap`, appeler `put(k, v)` sur une clé $k$ **déjà présente** s'exécute en $O(\log n)$.

    ??? success "Réponse"

        **Vrai.** Quand la clé existe, `findIndex` la localise par dichotomie en $O(\log n)$, puis met à jour la valeur **sur place**. Aucun élément n'est déplacé dans le tableau → pas de décalage, coût total $O(\log n)$.

??? question "Q3 — `put` dans `SortedTableMap` : nouvelle clé"

    Dans une `SortedTableMap`, appeler `put(k, v)` sur une clé $k$ **absente** s'exécute aussi en $O(\log n)$.

    ??? success "Réponse"

        **Faux.** La recherche par dichotomie est bien $O(\log n)$, mais l'insertion d'une **nouvelle** clé exige d'insérer une entrée à la bonne position dans le tableau trié : tous les éléments suivants doivent être **décalés d'une case vers la droite** → $O(n)$ dans le pire cas.

        Les Q2 et Q3 ensemble illustrent la distinction cruciale : la dichotomie accélère la *recherche*, pas l'*écriture*.

??? question "Q4 — Hauteur d'une skip list"

    Si une skip list contient $n$ entrées, sa hauteur est **au plus** $3 \log n$.

    ??? success "Réponse"

        **Faux.** C'est une borne **probabiliste**, non déterministe. Le livre démontre que la hauteur dépasse $3 \log n$ avec probabilité $\leq 1/n^2$ — ce qui est extrêmement improbable en pratique, mais pas impossible. Dans le pire cas théorique (toutes les promotions tombent « face »), la hauteur peut atteindre $n$.

### Bloc B — BST

??? question "Q5 — Localisation du prédécesseur"

    Dans un BST, le prédécesseur d'un nœud $p$ (plus grande clé strictement inférieure à celle de $p$) est le nœud le plus à droite du **sous-arbre droit** de $p$.

    ??? success "Réponse"

        **Faux.** C'est le nœud le plus à droite du **sous-arbre gauche** de $p$.

        Le sous-arbre droit contient des clés *supérieures* à celle de $p$ — son nœud le plus à gauche serait le *successeur*. On confond souvent les deux directions : prédécesseur ↔ sous-arbre gauche → descendre à droite ; successeur ↔ sous-arbre droit → descendre à gauche.

??? question "Q6 — Insertions croissantes et hauteur"

    Insérer les clés $1, 2, 3, \ldots, n$ dans cet ordre dans un BST vide produit un arbre de hauteur $\lfloor \log n \rfloor$.

    ??? success "Réponse"

        **Faux.** Chaque nouvelle clé est supérieure à toutes les précédentes, donc elle est toujours insérée comme **enfant droit le plus profond**. L'arbre dégénère en chaîne verticale de hauteur $n - 1$.

        $\lfloor \log n \rfloor$ est la hauteur *minimale* (arbre parfaitement équilibré). Un BST standard ne se rééquilibre pas automatiquement.

??? question "Q7 — Suppression d'un nœud à deux enfants"

    Dans un BST, supprimer un nœud $p$ possédant deux enfants internes peut se faire en remplaçant $p$ directement par son enfant gauche immédiat.

    ??? success "Réponse"

        **Faux.** Promouvoir l'enfant gauche immédiat $l$ à la place de $p$ laisse orphelin le sous-arbre droit de $l$ : ces clés (comprises entre $l$ et $p$) n'ont plus de place valide dans l'arbre sans restructuration supplémentaire.

        Le bon remplacement est le **prédécesseur** de $p$ (nœud le plus à droite du sous-arbre gauche), qui satisfait deux propriétés clés : (1) sa clé est correctement inférieure à toute clé du sous-arbre droit de $p$, et (2) il n'a pas d'enfant droit interne, donc sa suppression est toujours le Cas 1.

### Bloc C — Rotations et restructuration trinode

??? question "Q8 — Qui devient racine en zig-zig ?"

    Après `restructure(x)` dans un cas **zig-zig**, c'est $x$ (le petit-fils) qui devient la racine du sous-arbre restructuré.

    ??? success "Réponse"

        **Faux.** En zig-zig, la restructuration est une **rotation simple** autour de $z$ (le grand-parent). C'est $y$ — le parent de $x$, noté $b$ dans la notation inordre — qui monte à la racine.

        C'est en **zig-zag** que $x$ devient racine (rotation double). Le nom `restructure(x)` induit souvent en erreur : $x$ est l'argument mais pas nécessairement le résultat.

??? question "Q9 — Résultat universel de `restructure(x)`"

    Que le cas soit zig-zig ou zig-zag, `restructure(x)` place toujours le nœud **médian** de $\{x, y, z\}$ (en ordre infixe) à la racine du sous-arbre restructuré.

    ??? success "Réponse"

        **Vrai.** C'est le principe unificateur de la restructuration trinode. On renomme $x, y, z$ en $a < b < c$ selon l'ordre infixe : $b$ (le médian) est toujours placé à la racine, $a$ en fils gauche, $c$ en fils droit.

        En zig-zig, $b = y$ (le parent). En zig-zag, $b = x$ (le petit-fils). Le nombre de rotations diffère (1 vs 2), mais le résultat structurel est identique.

??? question "Q10 — `BalanceableBinaryTree` et rééquilibrage par défaut"

    Dans `BalanceableBinaryTree`, les méthodes `rebalanceInsert(p)` et `rebalanceDelete(p)` fournissent déjà une implémentation de rééquilibrage par défaut que les sous-classes (`AVLTreeMap`, `SplayTreeMap`, etc.) peuvent optionnellement redéfinir.

    ??? success "Réponse"

        **Faux.** Les implémentations par défaut sont **vides** — elles ne font rien. `TreeMap` reste un BST non équilibré. Les sous-classes doivent impérativement redéfinir ces méthodes pour obtenir un comportement équilibré.

        C'est le patron de méthode (*Template Method*) : `TreeMap` définit le squelette de l'algorithme (recherche, insertion, suppression BST) et appelle ces méthodes à des points précis, mais délègue entièrement la logique de rééquilibrage à chaque sous-classe.

---

## 2. Choix multiples

??? question "Q11 — Garantie dans le pire cas"

    Laquelle de ces implémentations garantit `get(k)` en $O(\log n)$ dans le **pire cas** (et non seulement en moyenne) ?

    - [ ] A) `SortedTableMap` seulement
    - [ ] B) `SkipListMap` seulement
    - [ ] C) Les deux
    - [ ] D) Aucune des deux

    ??? success "Réponse"

        **A) `SortedTableMap` seulement.**

        `SortedTableMap` utilise la **dichotomie** sur un tableau trié : $O(\log n)$ est une garantie déterministe.

        `SkipListMap` offre $O(\log n)$ **attendu** (probabiliste). Dans un scénario adversarial où les lancers de pièce sont défavorables, la hauteur peut atteindre $n$ et `get` dégrade à $O(n)$.

??? question "Q12 — Zig-zag : qui devient racine ?"

    Soit un BST avec $z$ à la racine, $y$ en enfant **gauche** de $z$, et $x$ en enfant **droit** de $y$. Après `restructure(x)`, qui devient la nouvelle racine du sous-arbre ?

    - [ ] A) $z$ (le grand-parent)
    - [ ] B) $y$ (le parent)
    - [ ] C) $x$ (le petit-fils)
    - [ ] D) Le nœud de plus petite clé parmi $\{x, y, z\}$

    ??? success "Réponse"

        **C) $x$ (le petit-fils).**

        Les orientations sont opposées ($y$ gauche de $z$, $x$ droit de $y$) → zig-zag. En ordre infixe : $y < x < z$, donc $a = y$, $b = x$, $c = z$. Le médian $b = x$ devient racine.

        Si $x$ était à gauche de $y$ (même orientation que $y$ par rapport à $z$), ce serait un zig-zig et $y$ deviendrait racine.

??? question "Q13 — Rotation gauche : où va $T_2$ ?"

    On effectue une **rotation gauche** : $x$ (enfant droit de $y$) monte au-dessus de $y$. Le sous-arbre $T_2$ (enfant gauche de $x$ avant la rotation) devient :

    - [ ] A) L'enfant gauche de $x$ après la rotation
    - [ ] B) L'enfant droit de $x$ après la rotation
    - [ ] C) L'enfant gauche de $y$ après la rotation
    - [ ] D) L'enfant droit de $y$ après la rotation

    ??? success "Réponse"

        **D) L'enfant droit de $y$.**

        Les clés de $T_2$ sont toutes **entre** celles de $y$ et de $x$. Après la rotation, $y$ est devenu enfant gauche de $x$ : pour respecter l'ordre BST, $T_2$ doit se retrouver à droite de $y$ (plus grand que $y$) et à gauche de $x$ (plus petit que $x$) → enfant droit de $y$.

??? question "Q14 — `lastEntry()` dans `TreeMap`"

    Quelle est la complexité de `lastEntry()` dans un `TreeMap` (BST standard, non équilibré) ?

    - [ ] A) $O(1)$
    - [ ] B) $O(\log n)$
    - [ ] C) $O(h)$
    - [ ] D) $O(n)$

    ??? success "Réponse"

        **C) $O(h)$.**

        La clé maximale est le nœud **le plus à droite** de l'arbre. Il faut descendre depuis la racine en prenant toujours l'enfant droit, soit $h$ étapes. Dans le pire cas (arbre dégénéré), $h = n - 1$ → $O(n)$.

        Comparez avec `SortedTableMap` où `lastEntry()` est $O(1)$ : la clé maximale est simplement `table.get(table.size()-1)` — accès direct au dernier élément du tableau trié.

??? question "Q15 — Espace total d'une skip list"

    Quel est l'espace total **attendu** d'une skip list contenant $n$ entrées (toutes niveaux confondus) ?

    - [ ] A) $O(\log n)$
    - [ ] B) $O(n)$
    - [ ] C) $O(n \log n)$
    - [ ] D) $O(n \cdot h)$ où $h$ est la hauteur

    ??? success "Réponse"

        **B) $O(n)$.**

        Le niveau $S_i$ contient en moyenne $n / 2^i$ entrées (chaque entrée est promue avec probabilité $1/2$ à chaque niveau). L'espace total est :

        $$\sum_{i=0}^{h} \frac{n}{2^i} = n \sum_{i=0}^{h} \frac{1}{2^i} < n \cdot 2 = 2n = O(n)$$

        Malgré $O(\log n)$ niveaux, la série géométrique garantit que le total reste linéaire.

??? question "Q16 — Complexité de `subMap` dans `SortedTableMap`"

    `subMap(k1, k2)` retourne $s$ entrées dans une `SortedTableMap`. Quelle est sa complexité ?

    - [ ] A) $O(s)$
    - [ ] B) $O(n)$
    - [ ] C) $O(s + \log n)$
    - [ ] D) $O(s + n)$

    ??? success "Réponse"

        **C) $O(s + \log n)$.**

        Deux phases : (1) **Localiser** la première entrée $\geq k_1$ par dichotomie : $O(\log n)$. (2) **Itérer** les $s$ entrées consécutives dans le tableau trié : $O(s)$.

        Si $s \gg \log n$ (beaucoup de résultats), le coût est dominé par $O(s)$. Si $s = 0$ (aucun résultat dans l'intervalle), le coût est $O(\log n)$.

---

## 3. Courtes réponses

??? question "Q17 — Pourquoi le prédécesseur a-t-il toujours ≤ 1 enfant interne ?"

    Lors de la suppression d'un nœud $p$ avec deux enfants internes dans un BST, on utilise son prédécesseur $r$ (nœud le plus à droite du sous-arbre gauche de $p$).

    Expliquez en 2–3 phrases pourquoi $r$ n'a **jamais d'enfant droit interne**.

    ??? success "Réponse"

        Si $r$ avait un enfant droit interne $r'$, alors $r' > r$ et $r' < p$ (car $r'$ est dans le sous-arbre gauche de $p$). Cela contredirait le fait que $r$ est le nœud **le plus à droite** du sous-arbre gauche : $r'$ serait encore plus à droite que $r$.

        Par conséquent, l'enfant droit de $r$ est toujours une feuille. Sa suppression tombe donc dans le **Cas 1** (≤ 1 enfant interne), ce qui simplifie l'algorithme.

??? question "Q18 — `restructure(x)` est O(1), peut-on équilibrer un BST en O(1) par insertion ?"

    Un étudiant affirme : *« Puisque `restructure(x)` est $O(1)$, il suffit d'appeler `restructure` une fois après chaque insertion pour maintenir un BST parfaitement équilibré. »*

    Cette affirmation est-elle correcte ? Expliquez en 3–4 phrases.

    ??? success "Réponse"

        **Non.** L'argument confond le coût de l'opération de restructuration et le coût du rééquilibrage global.

        `restructure(x)` corrige *un seul* sous-arbre local (le sous-arbre enraciné en $z$). Après une insertion, le déséquilibre peut se propager le long du chemin de la racine jusqu'au nœud inséré. **Détecter** le premier nœud déséquilibré nécessite de remonter ce chemin de longueur $O(h)$.

        De plus, une seule restructuration peut ne pas suffire : la suppression dans un arbre AVL peut nécessiter jusqu'à $O(\log n)$ restructurations. Le $O(1)$ de `restructure` est le coût par appel, pas le coût total du rééquilibrage.

??? question "Q19 — Entrées au niveau $S_3$ d'une skip list"

    Une skip list contient $n = 1000$ entrées. En supposant que chaque entrée est promue d'un niveau à l'autre avec probabilité $\frac{1}{2}$, quel est le **nombre attendu** d'entrées au niveau $S_3$ (le 4e niveau en partant du bas, $S_0$ étant le bas) ?

    ??? success "Réponse"

        Pour être présente au niveau $S_3$, une entrée doit avoir été promue **3 fois de suite**, chaque promotion ayant probabilité $\frac{1}{2}$.

        $$P(\text{entrée dans } S_3) = \left(\frac{1}{2}\right)^3 = \frac{1}{8}$$

        Nombre attendu d'entrées dans $S_3$ : $\;1000 \times \frac{1}{8} = \mathbf{125}$

??? question "Q20 — Zig-zig et zig-zag : même résultat, mécanismes différents"

    En zig-zig et en zig-zag, `restructure(x)` place toujours le nœud médian $b$ à la racine. Pourtant, le zig-zig utilise 1 rotation et le zig-zag en utilise 2.

    Expliquez en 3–4 phrases pourquoi le zig-zig n'a besoin que d'une seule rotation.

    ??? success "Réponse"

        En zig-zig, $y$ est déjà le nœud médian ($b$). Il suffit d'une seule rotation autour de $z$ pour hisser $y$ directement à la racine du sous-arbre : $y$ prend la place de $z$, et les quatre sous-arbres se replacent naturellement sous $a = x$ et $c = z$.

        En zig-zag, $x$ est le nœud médian ($b$), mais $x$ est « enclavé » entre $y$ et $z$ dans des directions opposées. Une seule rotation ne peut pas l'amener directement à la racine sans briser l'ordre BST. Il faut d'abord une rotation pour placer $x$ au-dessus de $y$, puis une seconde pour le placer au-dessus de $z$.

        En résumé : en zig-zig le médian est déjà adjacent au grand-parent, en zig-zag il ne l'est pas.


# Références

Goodrich, Tamassia, Goldwasser — *Data Structures and Algorithms in Java*, 6th ed. — Chapitres 10.3, 10.4, 11.1 et 11.2
