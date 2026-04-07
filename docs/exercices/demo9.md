# Démonstration 9 : Arbres AVL et Splay

Cette démonstration couvre le **chapitre 11** (Arbres de recherche équilibrés) du livre *Data Structures and Algorithms in Java (6th ed.)* : le code de `BalanceableBinaryTree` et `TreeMap` (§11.2), les arbres AVL (§11.3) et les arbres Splay (§11.4).

!!! abstract "Objectifs d'apprentissage"

    - Comprendre le rôle du champ `aux` et des méthodes crochets dans `BalanceableBinaryTree` et `TreeMap` (§11.2)
    - Définir le facteur d'équilibre AVL et expliquer pourquoi la hauteur est $O(\log n)$ (§11.3)
    - Appliquer `rebalanceInsert` et `rebalanceDelete` sur un arbre AVL en identifiant zig-zig vs zig-zag (§11.3)
    - Distinguer le splay zig-zig (deux rotations, parent d'abord) du zig-zig trinode (une rotation) (§11.4)
    - Effectuer une trace complète du splay et expliquer la garantie $O(\log n)$ amortie (§11.4)

---

# Partie I — Théorie

## 1. Code de `BalanceableBinaryTree` et `TreeMap` (§11.2)

### Champ auxiliaire `aux`

`BalanceableBinaryTree` étend `LinkedBinaryTree` en ajoutant un entier `aux` à chaque nœud. Ce champ est générique : chaque sous-classe l'utilise à sa façon.

| Sous-classe | Contenu de `aux` |
|-------------|-----------------|
| `AVLTreeMap` | Hauteur du nœud |
| `SplayTreeMap` | Non utilisé (toujours 0) |
| `RedBlackTreeMap` | Couleur (0 = noir, 1 = rouge) |

### Méthodes `rotate(p)` et `restructure(x)` — O(1)

`rotate(p)` déplace $p$ au-dessus de son parent en modifiant **3 références** — opération O(1). Elle ne touche **pas** le champ `aux`.

`restructure(x)` appelle `rotate` une fois (zig-zig) ou deux fois (zig-zag). Résultat : le médian $b$ parmi $\{x, y, z\}$ devient racine du sous-arbre.

!!! warning "Piège"

    `restructure(x)` ne met **pas** à jour les hauteurs ni aucun autre champ `aux`. C'est la responsabilité exclusive de la méthode de rééquilibrage de chaque sous-classe.

### Patron de méthode (*Template Method*)

`TreeMap` définit le squelette des opérations BST et appelle trois **méthodes crochets** à des points précis :

```java
// Appelée après chaque insertion (le paramètre p est le nouveau nœud)
protected void rebalanceInsert(Position<Entry<K,V>> p) { }

// Appelée après chaque suppression (p = parent du nœud retiré)
protected void rebalanceDelete(Position<Entry<K,V>> p) { }

// Appelée après chaque accès (get ou recherche infructueuse)
protected void rebalanceAccess(Position<Entry<K,V>> p) { }
```

Ces implémentations sont **vides** dans `TreeMap` (BST non équilibré). Les sous-classes les redéfinissent pour ajouter leur logique de rééquilibrage.

---

## 2. Arbres AVL (§11.3)

### Propriété AVL

Un arbre AVL est un BST tel que pour **tout nœud** $v$ :

$$|\text{hauteur}(\text{gauche}(v)) - \text{hauteur}(\text{droite}(v))| \leq 1$$

On définit la hauteur d'un sous-arbre vide comme $-1$.

### Borne sur la hauteur

En notant $n(h)$ le nombre **minimum** de nœuds dans un arbre AVL de hauteur $h$ :

$$n(0) = 1, \quad n(1) = 2, \quad n(h) = 1 + n(h-1) + n(h-2)$$

Cette récurrence suit les **nombres de Fibonacci** : $n(h) = F(h+3) - 1$. Puisque $F(k) \approx \phi^k / \sqrt{5}$ (avec $\phi \approx 1{,}618$), on obtient $n(h) \geq 2^{h/2}$, d'où :

$$h \leq 2 \log_2 n$$

### Code AVL : méthodes clés

```java
// AVLTreeMap (simplifié)
protected int height(Position<Entry<K,V>> p) {
    return tree.getAux(p);      // aux stocke la hauteur
}

protected void recomputeHeight(Position<Entry<K,V>> p) {
    tree.setAux(p, 1 + Math.max(height(left(p)), height(right(p))));
}

protected boolean isBalanced(Position<Entry<K,V>> p) {
    return Math.abs(height(left(p)) - height(right(p))) <= 1;
}

// Retourne le fils le plus haut de p (tiebreak : même côté que p par rapport à son parent)
protected Position<Entry<K,V>> tallerChild(Position<Entry<K,V>> p) { ... }

protected void rebalance(Position<Entry<K,V>> p) {
    do {
        if (!isBalanced(p)) {
            // x = tallerChild(tallerChild(p)) détermine le cas zig-zig ou zig-zag
            p = restructure(tallerChild(tallerChild(p)));
            recomputeHeight(left(p));
            recomputeHeight(right(p));
        }
        recomputeHeight(p);
        p = parent(p);
    } while (p != null);       // simplifié : s'arrête quand la hauteur est stable
}

@Override
public void rebalanceInsert(Position<Entry<K,V>> p) { rebalance(p); }

@Override
public void rebalanceDelete(Position<Entry<K,V>> p) { rebalance(p); }
```

### Nombre de restructurations

| Opération | Restructurations trinodes |
|-----------|--------------------------|
| Insertion | **Au plus 1** |
| Suppression | **Au plus $O(\log n)$** |

**Pourquoi l'insertion n'a besoin que d'une restructuration ?** Après la restructuration, la hauteur du sous-arbre restructuré redevient égale à ce qu'elle était **avant** l'insertion. Aucun ancêtre ne peut donc devenir déséquilibré — on continue uniquement à remettre à jour les hauteurs.

**Pourquoi la suppression peut en nécessiter plusieurs ?** La restructuration après une suppression peut *réduire* la hauteur du sous-arbre. Cette réduction peut déséquilibrer l'ancêtre immédiat, qui nécessite à son tour une restructuration, et ainsi de suite jusqu'à la racine.

---

## 3. Arbres Splay (§11.4)

### Principe

Un arbre Splay est un BST dans lequel **tout accès** (réussi ou non) déplace le nœud concerné jusqu'à la racine via l'opération **splay**. Aucun champ `aux` n'est nécessaire.

### Opération splay

Le splay de $p$ remonte $p$ jusqu'à la racine par une série d'étapes. Soit $q = \text{parent}(p)$ et $r = \text{parent}(q)$ :

| Cas | Condition | Opérations | Résultat |
|-----|-----------|------------|----------|
| **Zig** | $q$ est la racine | `rotate(p)` | $p$ devient racine |
| **Zig-zig** | $p$ et $q$ sont du **même côté** (GG ou DD) | `rotate(q)` puis `rotate(p)` | $p$ monte de 2 niveaux |
| **Zig-zag** | $p$ et $q$ sont de **côtés opposés** (GD ou DG) | `rotate(p)` puis `rotate(p)` | $p$ monte de 2 niveaux |

!!! danger "Zig-zig splay ≠ zig-zig trinode"

    - **Trinode zig-zig** : une seule rotation autour de $z$ → c'est **$y$ (le parent)** qui monte.
    - **Splay zig-zig** : deux rotations (`rotate(q)` puis `rotate(p)`) → c'est **$p$ (le petit-fils)** qui monte au sommet.

    Le zig-zag est identique dans les deux cas : `rotate(p)` deux fois → $p$ monte au sommet.

### Code Splay

```java
// SplayTreeMap (simplifié)
private void splay(Position<Entry<K,V>> p) {
    while (!isRoot(p)) {
        Position<Entry<K,V>> q = parent(p);
        Position<Entry<K,V>> r = parent(q);
        if (r == null || isRoot(q)) {
            rotate(p);                      // zig
        } else if ((q == left(r)) == (p == left(q))) {
            rotate(q); rotate(p);           // zig-zig : rotate parent d'abord
        } else {
            rotate(p); rotate(p);           // zig-zag : rotate p deux fois
        }
    }
}

@Override
public void rebalanceInsert(Position<Entry<K,V>> p) { splay(p); }

@Override
public void rebalanceDelete(Position<Entry<K,V>> p) {
    if (!isRoot(p)) splay(parent(p));
}

@Override
public void rebalanceAccess(Position<Entry<K,V>> p) {
    if (isExternal(p)) p = parent(p);
    if (p != null) splay(p);
}
```

### Complexité amortie

La garantie est $O(\log n)$ **amorti** — pas dans le pire cas.

| Opération | Pire cas | Amorti |
|-----------|----------|--------|
| `get`, `put`, `remove` | $O(n)$ | $O(\log n)$ |
| Suite de $m$ opérations | $O(mn)$ | $O(m \log n)$ |

**Intuition** : le splay transfère le coût d'une opération lente (accès profond) en "remboursement" pour de futures opérations rapides (le nœud est maintenant près de la racine). La fonction de potentiel utilisée est $\Phi = \sum_v \log(\text{taille}(v))$.

**Avantage pratique** : les nœuds fréquemment accédés migrent vers la racine et restent à faible profondeur — comportement optimal pour les distributions d'accès non uniformes.

---

# Partie II — Exercices

## 1. Vrai ou Faux

### Bloc A — Code `BalanceableBinaryTree` et `TreeMap` (§11.2)

??? question "Q1 — `restructure(x)` et le champ `aux`"

    Après un appel à `restructure(x)`, le champ `aux` des nœuds $x$, $y$ et $z$ est automatiquement mis à jour par `BalanceableBinaryTree`.

    ??? success "Réponse"

        **Faux.** `restructure(x)` ne fait que **réorganiser les pointeurs** parent-enfant. Elle ne connaît pas la sémantique de `aux` (qui peut être une hauteur, une couleur, etc., selon la sous-classe).

        La mise à jour de `aux` est toujours la responsabilité de la méthode de rééquilibrage : `rebalanceInsert`, `rebalanceDelete` ou `rebalanceAccess`. En AVL, on appelle `recomputeHeight` après chaque `restructure`.

??? question "Q2 — Hook dans `TreeMap`"

    Dans `TreeMap` (BST de base non équilibré), la méthode `rebalanceInsert(p)` n'est **pas** appelée lors d'une insertion.

    ??? success "Réponse"

        **Faux.** `rebalanceInsert(p)` est **toujours appelée** par le code d'insertion de `TreeMap` — c'est le principe du patron de méthode. Son implémentation par défaut est simplement **vide** (`{}`), donc elle n'effectue aucune opération. Les sous-classes la redéfinissent pour ajouter le rééquilibrage.

        Retenir : *appel systématique, comportement configurable par héritage*.

??? question "Q3 — Complexité de `rotate(p)`"

    La méthode `rotate(p)` s'exécute en $O(\log n)$ car elle doit localiser $p$ dans l'arbre avant de pouvoir effectuer la rotation.

    ??? success "Réponse"

        **Faux.** `rotate(p)` reçoit **directement la référence** au nœud $p$ — aucune recherche n'est nécessaire. Elle effectue exactement **3 modifications de pointeurs** (le parent de $p$, l'enfant de l'ancien parent, et le sous-arbre médian $T_2$) → $O(1)$.

        La localisation préalable (descente dans l'arbre) est faite par l'algorithme BST qui appelle ensuite `rebalanceInsert` ou `rebalanceAccess` en passant la position déjà trouvée.

### Bloc B — Arbres AVL (§11.3)

??? question "Q4 — Borne sur la hauteur AVL"

    Un arbre AVL contenant $n$ nœuds a toujours une hauteur **strictement inférieure** à $2 \log_2 n$.

    ??? success "Réponse"

        **Vrai.** La preuve par les arbres de Fibonacci montre que $n \geq 2^{h/2}$, d'où $h \leq 2 \log_2 n$. La borne exacte est $h < 2 \log_2(n+2) - 1$.

        À comparer avec un BST ordinaire dont la hauteur peut atteindre $n - 1$. L'arbre AVL garantit donc que **toutes** les opérations BST restent en $O(\log n)$.

??? question "Q5 — Nombre de restructurations après une insertion AVL"

    Après une insertion dans un arbre AVL, il est possible que plusieurs restructurations trinodes soient nécessaires pour rétablir l'équilibre.

    ??? success "Réponse"

        **Faux.** **Au plus une** restructuration trinode est nécessaire après une insertion.

        Après la restructuration au premier ancêtre déséquilibré $z$, la hauteur du sous-arbre restructuré revient à sa valeur d'avant l'insertion. Aucun ancêtre de $z$ ne peut donc être devenu déséquilibré. On continue cependant à remonter jusqu'à la racine pour **recalculer les hauteurs**.

??? question "Q6 — Nombre de restructurations après une suppression AVL"

    Après une suppression dans un arbre AVL, il peut être nécessaire d'effectuer jusqu'à $O(\log n)$ restructurations trinodes.

    ??? success "Réponse"

        **Vrai.** Contrairement à l'insertion, une restructuration après suppression peut **diminuer** la hauteur du sous-arbre restructuré. Cette diminution peut rendre l'ancêtre immédiat déséquilibré, déclenchant une nouvelle restructuration, et ainsi de suite jusqu'à la racine.

        **Exemple** : dans un arbre AVL très équilibré, supprimer une feuille peut provoquer une cascade de $\Theta(\log n)$ restructurations.

??? question "Q7 — Champ `aux` dans `AVLTreeMap`"

    Dans `AVLTreeMap`, le champ `aux` stocke le **facteur d'équilibre** (différence de hauteurs des sous-arbres gauche et droit) du nœud.

    ??? success "Réponse"

        **Faux.** `aux` stocke la **hauteur** du nœud (longueur du plus long chemin vers une feuille), pas le facteur d'équilibre.

        Le facteur d'équilibre se calcule à la demande : $\text{bf}(p) = \text{height}(\text{right}(p)) - \text{height}(\text{left}(p))$. Stocker la hauteur est préférable car elle permet de recalculer le facteur d'équilibre de tout ancêtre en $O(1)$ lors de la remontée.

### Bloc C — Arbres Splay (§11.4)

??? question "Q8 — Garantie de l'arbre Splay dans le pire cas"

    L'arbre Splay garantit que chaque opération individuelle (`get`, `put`, `remove`) s'exécute en $O(\log n)$ dans le **pire cas**.

    ??? success "Réponse"

        **Faux.** La garantie est $O(\log n)$ **amorti**. Dans le pire cas, une seule opération peut prendre $O(n)$ : par exemple, accéder à la feuille la plus profonde d'un arbre dégénéré.

        La garantie amortie signifie que **sur une séquence de $m$ opérations**, le coût total est $O(m \log n)$, même si certaines opérations individuelles sont coûteuses.

??? question "Q9 — Splay sur une recherche infructueuse"

    Dans un arbre Splay, si la clé recherchée est **absente**, aucun splay n'est effectué.

    ??? success "Réponse"

        **Faux.** `rebalanceAccess(p)` est appelée même en cas d'échec de la recherche. Le nœud $p$ passé est le **dernier nœud interne visité** avant d'atteindre une feuille externe (le nœud à partir duquel on sait que la clé est absente). Ce nœud est splaté jusqu'à la racine.

        Cela maintient la propriété des accès récents (la région visitée se retrouve près de la racine) même quand la clé est absente.

??? question "Q10 — Splay zig-zig vs restructuration trinode zig-zig"

    Dans un zig-zig, le splay et la restructuration trinode effectuent les mêmes rotations dans le même ordre et produisent le même résultat.

    ??? success "Réponse"

        **Faux.** Les deux opérations **diffèrent** en zig-zig :

        - **Trinode zig-zig** : une seule rotation autour de $z$ → **$y$ (le parent)** devient racine du sous-arbre.
        - **Splay zig-zig** : `rotate(y)` puis `rotate(x)` → **$x$ (le petit-fils)** devient racine du sous-arbre.

        Le zig-zag est identique dans les deux cas : `rotate(x)` deux fois → $x$ monte au sommet.

        Cette différence dans le zig-zig est précisément ce qui permet à l'arbre Splay de déplacer $x$ jusqu'à la racine en une seule passe (vs $O(\log n)$ appels à trinode restructuring).

---

## 2. Choix multiples

??? question "Q11 — Quel nœud monte en zig-zig splay ?"

    Dans un arbre Splay, on effectue un zig-zig sur le nœud $p$ dont le parent est $q$ et le grand-parent est $r$. Quel nœud se retrouve à la racine du sous-arbre après l'opération ?

    - [ ] A) $r$ (le grand-parent)
    - [ ] B) $q$ (le parent)
    - [ ] C) $p$ (le petit-fils)
    - [ ] D) Dépend de si c'est gauche-gauche ou droite-droite

    ??? success "Réponse"

        **C) $p$ (le petit-fils).**

        Le splay zig-zig effectue `rotate(q)` puis `rotate(p)`. Après `rotate(q)`, $q$ monte à la place de $r$. Après `rotate(p)`, $p$ monte à la place de $q$. Résultat : $p$ est au sommet, $q$ est son enfant, $r$ est le petit-fils.

        C'est la différence cruciale avec la restructuration trinode zig-zig où c'est $y = q$ qui monte. Le splay doit amener $p$ au sommet pour éventuellement en faire la racine de l'arbre entier.

??? question "Q12 — Nombre de restructurations AVL à la suppression"

    On supprime une clé dans un arbre AVL de hauteur $h$. Combien de restructurations trinodes peuvent être nécessaires au **maximum** ?

    - [ ] A) 0
    - [ ] B) 1
    - [ ] C) $\lfloor h/2 \rfloor$
    - [ ] D) $h$

    ??? success "Réponse"

        **D) $h$.**

        Après la suppression, on remonte depuis la feuille supprimée jusqu'à la racine, soit $h$ niveaux. À chaque niveau, une restructuration peut être nécessaire. Dans le pire cas, toutes les $h = O(\log n)$ étapes nécessitent une restructuration.

        C'est à comparer avec l'insertion qui n'en nécessite qu'une seule. La raison : après une restructuration due à une suppression, la hauteur du sous-arbre peut *diminuer*, propageant le déséquilibre vers les ancêtres.

??? question "Q13 — `tallerChild` et le choix de zig-zig vs zig-zag"

    Dans `AVLTreeMap`, `restructure(tallerChild(tallerChild(p)))` est appelée pour rééquilibrer au nœud déséquilibré $p = z$. Que représente `tallerChild(tallerChild(p))` ?

    - [ ] A) Le fils le plus haut de $z$ (c'est-à-dire $y$)
    - [ ] B) Le petit-fils le plus haut de $z$ (c'est-à-dire $x$)
    - [ ] C) Le parent de $z$
    - [ ] D) Le successeur infixe de $z$

    ??? success "Réponse"

        **B) Le petit-fils le plus haut de $z$ (c'est-à-dire $x$).**

        - `tallerChild(p)` retourne le fils ($y$) dont la hauteur est la plus grande parmi les deux fils de $p$.
        - `tallerChild(tallerChild(p))` applique la même logique à $y$ → retourne le fils $x$ de $y$ avec la hauteur la plus grande.

        C'est ce nœud $x$ qui est passé à `restructure(x)`. La configuration de $x$ par rapport à $y$ et $z$ détermine si c'est un cas zig-zig ou zig-zag.

??? question "Q14 — Complexité amortie d'une séquence d'accès sur un Splay"

    On effectue $m$ opérations `get` successives sur un arbre Splay contenant $n$ entrées. Quelle est la complexité **totale** de ces $m$ opérations (dans le pire cas amorti) ?

    - [ ] A) $O(m)$
    - [ ] B) $O(n)$
    - [ ] C) $O(m \log n)$
    - [ ] D) $O(mn)$

    ??? success "Réponse"

        **C) $O(m \log n)$.**

        La complexité amortie de chaque opération sur un arbre Splay est $O(\log n)$. Sur $m$ opérations, le coût total est donc $O(m \log n)$.

        Note : $O(mn)$ est le pire cas **non amorti** si on imagine $m$ accès au nœud le plus profond chaque fois — mais le splay empêche précisément ce scénario (après le premier accès, le nœud est à la racine).

??? question "Q15 — `rebalanceDelete` dans `SplayTreeMap`"

    Dans `SplayTreeMap`, qu'effectue `rebalanceDelete(p)` (où $p$ est le parent du nœud supprimé) ?

    - [ ] A) Splater le successeur infixe du nœud supprimé
    - [ ] B) Splater $p$ jusqu'à la racine
    - [ ] C) Ne rien faire ($p$ est déjà suffisamment haut)
    - [ ] D) Effectuer une rotation simple autour de $p$

    ??? success "Réponse"

        **B) Splater $p$ jusqu'à la racine.**

        Après la suppression BST, `rebalanceDelete(p)` appelle `splay(p)` si $p$ n'est pas déjà la racine. Cela remonte le parent du nœud supprimé jusqu'à la racine, maintenant la propriété que les nœuds récemment accédés sont proches de la racine.

??? question "Q16 — Arbre de Fibonacci et AVL"

    Quel est le nombre **minimum** de nœuds dans un arbre AVL de hauteur 4 ?

    - [ ] A) 4
    - [ ] B) 7
    - [ ] C) 12
    - [ ] D) 15

    ??? success "Réponse"

        **C) 12.**

        En utilisant la récurrence $n(0)=1$, $n(1)=2$, $n(h) = 1 + n(h-1) + n(h-2)$ :

        | $h$ | $n(h)$ |
        |-----|--------|
        | 0 | 1 |
        | 1 | 2 |
        | 2 | 4 |
        | 3 | 7 |
        | 4 | 12 |

        L'arbre atteignant ce minimum est appelé **arbre de Fibonacci** : à chaque nœud, l'un des sous-arbres a hauteur $h-1$ et l'autre $h-2$ (différence maximale autorisée par AVL).

---

## 3. Courtes réponses

??? question "Q17 — Pourquoi l'arbre AVL n'a-t-il besoin que d'une restructuration lors d'une insertion ?"

    Expliquez en 3–4 phrases pourquoi **au plus une** restructuration trinode suffit pour rétablir l'équilibre AVL après une insertion.

    ??? success "Réponse"

        Avant l'insertion, le sous-arbre enraciné en $z$ (premier ancêtre déséquilibré) a une certaine hauteur $h$. La restructuration (`restructure(x)`) remet le médian $b$ à la racine du sous-arbre ; la hauteur de ce sous-arbre **redevient $h$**, identique à la valeur d'avant l'insertion.

        Puisque la hauteur du sous-arbre restructuré n'a pas changé par rapport à l'état avant insertion, aucun ancêtre de $z$ ne peut avoir vu sa balance affectée. Il suffit de remonter jusqu'à la racine pour mettre à jour les hauteurs, sans qu'aucune autre restructuration soit nécessaire.

        En résumé : la restructuration **annule** localement l'effet de l'insertion sur la hauteur du sous-arbre, neutralisant toute propagation vers le haut.

??? question "Q18 — Différence AVL vs Splay pour les accès répétitifs"

    Un cache implémenté avec un arbre Splay effectue $1\,000$ fois l'accès à la même clé $k$ dans une collection de $n = 10^6$ entrées. Comparez le coût total avec un arbre AVL.

    ??? success "Réponse"

        **Arbre AVL** : chaque `get(k)` coûte $O(\log n) = O(20)$ opérations, sans modifier la structure. Coût total : $1\,000 \times O(\log n) = O(1\,000 \log n)$.

        **Arbre Splay** : le **premier** accès à $k$ peut coûter jusqu'à $O(n)$ si $k$ est profond. Mais après ce premier accès, $k$ est à la racine. Chaque accès suivant coûte $O(1)$ (la racine est immédiatement accessible). Coût total : $O(n) + 999 \times O(1) = O(n)$.

        **Conclusion** : pour des accès **très répétitifs**, le Splay est imbattable une fois le nœud en haut. Pour des accès **uniformément distribués** sur $n$ clés, l'AVL est préférable (garantie $O(\log n)$ par opération sans surprises).

??? question "Q19 — `tallerChild` en cas d'égalité de hauteurs"

    Dans `AVLTreeMap`, `tallerChild(p)` doit gérer le cas où les deux fils de $p$ ont la **même** hauteur (ce qui peut arriver après une suppression). Expliquez quel choix est fait et pourquoi.

    ??? success "Réponse"

        Quand les deux fils ont la même hauteur, `tallerChild` choisit le fils du **même côté** que $p$ par rapport à son parent : si $p$ est le fils gauche de son parent, on retourne le fils gauche de $p$ ; sinon le fils droit.

        Ce choix garantit que la configuration résultante est **zig-zig** (même côté) plutôt que **zig-zag** (côtés opposés). Les deux cas produisent un résultat valide, mais préférer zig-zig évite une double rotation inutile quand une simple rotation suffit.

        Si $p$ est la racine (sans parent), on retourne arbitrairement le fils gauche.

??? question "Q20 — Splay et structure initiale"

    Deux arbres Splay $T_1$ et $T_2$ contiennent les mêmes $n$ clés mais dans des configurations différentes (structures d'arbres différentes). On effectue la même séquence de $m$ opérations sur chacun. Peut-on affirmer que les coûts totaux sont identiques ?

    ??? success "Réponse"

        **Non, pas en général.** Le coût d'une opération sur un arbre Splay dépend de la **profondeur actuelle** du nœud accédé, qui elle-même dépend de la structure courante de l'arbre. Deux configurations initiales différentes peuvent donner des coûts totaux différents pour la même séquence d'opérations.

        Cependant, la garantie amortie $O(m \log n)$ s'applique aux **deux** arbres, quelle que soit la configuration initiale. La structure initiale n'influence que la constante cachée dans le $O$, pas l'ordre de grandeur asymptotique.

        En pratique : une structure initiale défavorable ne coûte qu'un "surcoût" ponctuel absorbé par l'analyse amortie — l'arbre Splay se corrige lui-même au fil des accès.

---

## 4. Exercices de trace

??? question "Q21 — Insertions dans un arbre AVL : zig-zig"

    On insère les clés $3, 2, 1, 4, 5, 6$ dans cet ordre dans un arbre AVL initialement vide. On définit la hauteur d'un sous-arbre vide comme $-1$.

    1. Donnez l'état de l'arbre et les facteurs d'équilibre après chaque insertion.
    2. Pour chaque déséquilibre, identifiez $x$, $y$, $z$ et le cas (zig-zig ou zig-zag).
    3. Dessinez l'arbre final.

    ??? success "Réponse"

        **Insertions 3, 2** : aucun déséquilibre.

        ```
        Après 3 :     3       BF(3)=0
        Après 2 :     3       BF(3)=-1, BF(2)=0
                     /
                    2
        ```

        ---

        **Insertion 1 → déséquilibre en $z=3$**

        ```
              3      h(2)=1, h(3)=2
             /        BF(3) = -1 - 1 = -2  ← DÉSÉQUILIBRE
            2
           /
          1
        ```

        $z=3$, $y=2$ (fils gauche de $z$, tallerChild), $x=1$ (fils gauche de $y$, tallerChild).
        $y$ GAUCHE de $z$, $x$ GAUCHE de $y$ → **zig-zig gauche**.

        `restructure(x=1)` : $a=1$, $b=2$, $c=3$. Rotation droite autour de $z=3$ → $b=2$ devient racine.

        ```
           2       h=1, BF(2)=0 ✓
          / \
         1   3
        ```

        ---

        **Insertion 4** : 4 à droite de 3. BF(3)=1, BF(2)=1. Aucun déséquilibre.

        ```
           2
          / \
         1   3
              \
               4
        ```

        ---

        **Insertion 5 → déséquilibre en $z=3$**

        ```
           2
          / \
         1   3       h(3)=2 (avec 4 puis 5 à droite)
              \       BF(3) = h(4) - (-1) = 1 - (-1) = 2 ← DÉSÉQUILIBRE
               4
                \
                 5
        ```

        $z=3$, $y=4$ (fils droit), $x=5$ (fils droit de $y$).
        $y$ DROIT de $z$, $x$ DROIT de $y$ → **zig-zig droit**.

        `restructure(x=5)` : $a=3$, $b=4$, $c=5$. Rotation gauche autour de $z=3$ → $b=4$ monte.

        ```
           2
          / \
         1   4      BF(2)=1, BF(4)=0 ✓
            / \
           3   5
        ```

        ---

        **Insertion 6 → déséquilibre en $z=2$**

        ```
           2
          / \
         1   4        h(4)=2, h(1)=0 → BF(2) = 2 - 0 = 2 ← DÉSÉQUILIBRE
            / \
           3   5
                \
                 6
        ```

        $z=2$, $y=4$ (fils droit, h=2 > h(1)=0), $x=5$ (fils droit de $y$, h(5)=1 > h(3)=0).
        $y$ DROIT de $z$, $x$ DROIT de $y$ → **zig-zig droit**.

        `restructure(x=5)` : $a=2$, $b=4$, $c=5$. Rotation gauche autour de $z=2$ → $b=4$ monte. Le fils gauche de $b$ ($3$) devient fils droit de $a=2$.

        ```
              4          BF(4) = h(5) - h(2) = 1 - 1 = 0 ✓
             / \
            2   5
           / \   \
          1   3   6
        ```

        **Arbre final :**

        ```
              4
             / \
            2   5
           / \   \
          1   3   6
        ```

        Toutes les hauteurs et facteurs d'équilibre sont valides. Les trois déséquilibres rencontrés étaient tous des **zig-zig** (aucun zig-zag dans cette séquence).

??? question "Q22 — Insertions dans un arbre AVL : zig-zag"

    On insère les clés $5, 3, 4$ dans un arbre AVL vide.

    1. Après l'insertion de $4$, identifiez $x$, $y$, $z$ et le cas (zig-zig ou zig-zag).
    2. Décrivez les **deux** rotations effectuées par `restructure(x)` et donnez l'arbre résultant.

    ??? success "Réponse"

        **Insertions 5, 3** : aucun déséquilibre.

        ```
        Après 5, 3 :    5     BF(5)=-1, BF(3)=0
                       /
                      3
        ```

        ---

        **Insertion 4 → déséquilibre en $z=5$**

        ```
             5       h(3)=1 (avec 4 à droite)
            /         BF(5) = (-1) - h(3) = -1 - 1 = -2 ← DÉSÉQUILIBRE
           3
            \
             4
        ```

        $z=5$, $y=3$ (fils gauche de $z$), $x=4$ (fils droit de $y$).
        $y$ GAUCHE de $z$, $x$ DROIT de $y$ → **zig-zag (gauche-droit)**.

        `restructure(x=4)` : $a=3$, $b=4$, $c=5$.

        **Rotation 1** — rotation gauche autour de $y=3$ :

        ```
             5
            /
           4       ← 4 monte, 3 descend à gauche de 4
          /
         3
        ```

        **Rotation 2** — rotation droite autour de $z=5$ :

        ```
             4      ← 4 monte à la place de 5, 5 descend à droite
            / \
           3   5
        ```

        **Arbre résultant :**

        ```
           4
          / \
         3   5
        ```

        BF(4) = 0. Arbre équilibré ✓. Notez que c'est $x=4$ (le petit-fils) qui est devenu la nouvelle racine du sous-arbre — caractéristique des cas **zig-zag**.

??? question "Q23 — Trace d'un splay"

    Soit l'arbre BST suivant (obtenu par des insertions précédentes dans un arbre Splay) :

    ```
            20
           /  \
         10    30
        /  \
       5   15
      /
     3
    ```

    On effectue `get(3)` sur cet arbre Splay. Tracez chaque étape du splay de $3$ jusqu'à la racine en identifiant zig, zig-zig ou zig-zag à chaque fois.

    ??? success "Réponse"

        **Étape 1** : $p=3$, $q=\text{parent}(3)=5$, $r=\text{parent}(5)=10$.

        $p=3$ est fils **gauche** de $q=5$, et $q=5$ est fils **gauche** de $r=10$ → **zig-zig (gauche-gauche)**.

        `rotate(q=5)` d'abord (rotation droite autour de 10 : 5 monte, 10 descend) :

        ```
                20
               /  \
             5    30
            / \
           3   10
              /  \
             (∅)  15
        ```

        Puis `rotate(p=3)` (rotation droite autour de 5 : 3 monte, 5 descend) :

        ```
                20
               /  \
             3    30
              \
               5
                \
                10
                  \
                  15
        ```

        ---

        **Étape 2** : $p=3$, $q=\text{parent}(3)=20$. $q=20$ est la racine → **zig**.

        `rotate(p=3)` (rotation droite autour de 20 : 3 monte, 20 descend). Le fils droit de 3 ($5$) devient le fils gauche de 20 :

        ```
             3
              \
              20
             /  \
            5   30
             \
             10
               \
               15
        ```

        ---

        **Résultat final** : 3 est à la racine. Parcours infixe : $3, 5, 10, 15, 20, 30$ ✓

        Récapitulatif des étapes :

        | Étape | Cas | Opérations |
        |-------|-----|------------|
        | 1 | Zig-zig (GG) | `rotate(5)` puis `rotate(3)` |
        | 2 | Zig | `rotate(3)` |

        Total : 3 rotations pour remonter un nœud de profondeur 3. Le nœud $3$ (qui était le plus profond) se retrouve maintenant à la racine — les prochains accès à $3$ coûteront $O(1)$.

---

## 5. Exercices de code

??? question "Q24 — Compléter `tallerChild`"

    La méthode `tallerChild(p)` retourne le fils de $p$ ayant la plus grande hauteur. En cas d'**égalité**, le livre choisit le fils du **même côté** que $p$ par rapport à son parent (pour favoriser un cas zig-zig plutôt que zig-zag).

    Complétez les lignes manquantes (`???`) :

    ```java
    protected Position<Entry<K,V>> tallerChild(Position<Entry<K,V>> p) {
        int hL = height(left(p));
        int hR = height(right(p));
        if (hL > hR) return left(p);
        if (hR > hL) return right(p);
        // égalité de hauteurs : choisir le même côté que p par rapport à son parent
        if (isRoot(p)) return left(p);
        if (p == left(parent(p))) return ???;   // (A)
        else                      return ???;   // (B)
    }
    ```

    1. Donnez les expressions pour `(A)` et `(B)`.
    2. Expliquez pourquoi ce choix favorise un zig-zig plutôt qu'un zig-zag.

    ??? success "Réponse"

        ```java
        if (p == left(parent(p))) return left(p);   // (A)
        else                      return right(p);   // (B)
        ```

        **Explication :**

        Supposons que $p$ est le fils **gauche** de son parent $z$ (i.e. $p = y$). On cherche $x = \text{tallerChild}(y)$. Si on retourne `left(p)` (A), alors $x$ est lui aussi à gauche → $x$ et $y$ sont du même côté par rapport à leur parent respectif → **zig-zig**.

        Si on avait retourné `right(p)`, on aurait $x$ à droite de $y$ gauche de $z$ → **zig-zag** (double rotation). En cas d'égalité, les deux cas sont valides, mais préférer zig-zig évite une rotation inutile.

        **Cas symétrique :** si $p$ est fils droit, on retourne `right(p)` pour les mêmes raisons.

??? question "Q25 — Déboguer `splay`"

    L'implémentation suivante de `splay` contient **deux erreurs** : les corps des cas zig-zig et zig-zag ont été échangés.

    ```java
    private void splay(Position<Entry<K,V>> p) {
        while (!isRoot(p)) {
            Position<Entry<K,V>> q = parent(p);
            Position<Entry<K,V>> r = parent(q);
            if (r == null || isRoot(q)) {
                rotate(p);                    // zig — correct
            } else if ((q == left(r)) == (p == left(q))) {
                rotate(p); rotate(p);         // (A) — incorrect
            } else {
                rotate(q); rotate(p);         // (B) — incorrect
            }
        }
    }
    ```

    1. Expliquez précisément pourquoi chaque branche est incorrecte.
    2. Donnez le code corrigé.
    3. Quelle propriété fondamentale de l'arbre Splay est violée par ce bug ?

    ??? success "Réponse"

        **1. Analyse des erreurs :**

        - **Branche (A) — zig-zig incorrect :** la condition `(q == left(r)) == (p == left(q))` détecte un cas zig-zig (même côté). Or, le corps `rotate(p); rotate(p)` correspond au **zig-zag** (on tourne $p$ deux fois). En zig-zig, il faut tourner le **parent $q$ d'abord**, puis $p$ — cela permet à $p$ de "sauter" deux niveaux d'un coup et crée la structure en escalier caractéristique qui fonde la garantie amortie.

        - **Branche (B) — zig-zag incorrect :** la condition `else` détecte un zig-zag. Or, `rotate(q); rotate(p)` correspond au **zig-zig splay**. En zig-zag, il faut tourner $p$ deux fois (une fois pour le placer au-dessus de $q$, une fois pour le placer au-dessus de $r$).

        **2. Code corrigé :**

        ```java
        private void splay(Position<Entry<K,V>> p) {
            while (!isRoot(p)) {
                Position<Entry<K,V>> q = parent(p);
                Position<Entry<K,V>> r = parent(q);
                if (r == null || isRoot(q)) {
                    rotate(p);                // zig
                } else if ((q == left(r)) == (p == left(q))) {
                    rotate(q); rotate(p);     // zig-zig : parent d'abord
                } else {
                    rotate(p); rotate(p);     // zig-zag : p deux fois
                }
            }
        }
        ```

        **3. Propriété violée :**

        Avec le code bogué, $p$ n'atteint **jamais la racine** dans certains cas. Par exemple, en zig-zig sur un chemin droit ($p$ → $q$ → $r$ → racine), `rotate(p); rotate(p)` place $p$ deux niveaux plus haut mais ne fait pas monter $q$ et $r$ de manière optimale — la boucle peut cycler sans progresser vers la racine. La garantie $O(\log n)$ amorti est également perdue car c'est précisément le zig-zig "parent d'abord" qui permet la réduction de la fonction de potentiel.

??? question "Q26 — Vérifier la propriété AVL"

    Implémentez la méthode `isValidAVL()` qui retourne `true` si et seulement si l'arbre courant satisfait la propriété AVL en tout nœud. Utilisez une méthode auxiliaire récursive.

    ```java
    /**
     * Retourne la hauteur du sous-arbre enraciné en p
     * si ce sous-arbre est un AVL valide,
     * ou Integer.MIN_VALUE si la propriété AVL est violée.
     */
    private int checkAVL(Position<Entry<K,V>> p) {
        if (isExternal(p)) return ???;              // (A) — cas de base

        int hL = checkAVL(left(p));
        if (???) return Integer.MIN_VALUE;          // (B) — propager l'échec gauche

        int hR = checkAVL(right(p));
        if (???) return Integer.MIN_VALUE;          // (C) — propager l'échec droit

        if (???) return Integer.MIN_VALUE;          // (D) — vérifier l'équilibre local

        return ???;                                 // (E) — retourner la hauteur
    }

    public boolean isValidAVL() {
        return checkAVL(root()) != Integer.MIN_VALUE;
    }
    ```

    Complétez les cinq expressions `(A)` à `(E)`.

    ??? success "Réponse"

        ```java
        private int checkAVL(Position<Entry<K,V>> p) {
            if (isExternal(p)) return -1;                        // (A)

            int hL = checkAVL(left(p));
            if (hL == Integer.MIN_VALUE) return Integer.MIN_VALUE; // (B)

            int hR = checkAVL(right(p));
            if (hR == Integer.MIN_VALUE) return Integer.MIN_VALUE; // (C)

            if (Math.abs(hL - hR) > 1) return Integer.MIN_VALUE;  // (D)

            return 1 + Math.max(hL, hR);                          // (E)
        }
        ```

        **Explication ligne par ligne :**

        - **(A)** : Un sous-arbre vide (nœud externe / feuille sentinelle) a hauteur $-1$ par convention. C'est la base de la récurrence.
        - **(B)** : Si le sous-arbre gauche n'est pas AVL, on propage immédiatement l'échec sans examiner le sous-arbre droit — court-circuit pour éviter un travail inutile.
        - **(C)** : Même raisonnement pour le sous-arbre droit.
        - **(D)** : Vérification locale de la propriété AVL : la différence de hauteurs doit être au plus 1. On utilise `Math.abs` pour traiter les deux cas (gauche plus haut ou droit plus haut).
        - **(E)** : Si tout est valide, on retourne la hauteur de ce nœud pour permettre à l'appelant de vérifier son propre équilibre. La hauteur d'un nœud est $1 + \max(h_L, h_R)$.

        **Complexité :** $O(n)$ — chaque nœud est visité exactement une fois.

??? question "Q27 — Compléter `rebalance` pour AVL"

    Voici une version simplifiée de `rebalance` pour `AVLTreeMap`. La boucle remonte depuis le nœud inséré/supprimé jusqu'à la racine, recalcule les hauteurs et restructure si nécessaire.

    ```java
    protected void rebalance(Position<Entry<K,V>> p) {
        while (p != null) {
            int oldH = height(p);
            if (!isBalanced(p)) {
                // (A) : déterminer x et appeler restructure
                p = restructure(???);
                // (B) : recalculer les hauteurs des deux fils du nouveau sous-arbre
                recomputeHeight(???);
                recomputeHeight(???);
            }
            // (C) : recalculer la hauteur de p (qu'il y ait eu restructure ou non)
            recomputeHeight(p);
            // (D) : optimisation — arrêter si la hauteur n'a pas changé
            if (height(p) == oldH) break;
            p = parent(p);
        }
    }
    ```

    1. Complétez `(A)` : quel argument passe-t-on à `restructure` ?
    2. Complétez `(B)` : sur quels nœuds recalcule-t-on la hauteur immédiatement après `restructure` ?
    3. Expliquez l'optimisation `(D)` : dans quel cas peut-on s'arrêter tôt, et pourquoi cela est-il correct ?

    ??? success "Réponse"

        **1. Argument de `restructure` (A) :**

        ```java
        p = restructure(tallerChild(tallerChild(p)));
        ```

        `tallerChild(p)` donne $y$ (le fils le plus haut de $z = p$), puis `tallerChild(y)` donne $x$ (le petit-fils le plus haut). C'est $x$ qui est passé à `restructure(x)`. Après l'appel, `p` pointe vers la nouvelle racine du sous-arbre ($b$, le médian).

        **2. Hauteurs à recalculer après `restructure` (B) :**

        ```java
        recomputeHeight(left(p));
        recomputeHeight(right(p));
        ```

        `restructure` a déplacé $a$ et $c$ comme fils de $b$. Leurs hauteurs stockées dans `aux` ne sont plus nécessairement correctes (les sous-arbres ont été réarrangés). On recalcule $a$ et $c$ en premier, puis `p` ($= b$) au point `(C)`, dans le bon ordre (fils avant parent).

        **3. Optimisation — arrêt si hauteur inchangée (D) :**

        Si après la mise à jour de `p`, `height(p) == oldH`, alors la hauteur de ce sous-arbre n'a **pas changé** par rapport à avant l'opération. Aucun ancêtre de `p` ne peut donc avoir vu son facteur d'équilibre affecté → on peut s'arrêter.

        Ce cas se produit **toujours après une insertion** (la restructuration ramène la hauteur à sa valeur d'avant l'insertion). Pour une suppression, la hauteur peut diminuer, obligeant à continuer jusqu'à la racine. L'optimisation est donc automatiquement inefficace (elle ne s'active pas) lors des suppressions qui provoquent une cascade, mais elle évite des tours de boucle inutiles dans les cas favorables.

---

# Références

Goodrich, Tamassia, Goldwasser — *Data Structures and Algorithms in Java*, 6th ed. — Chapitres 11.2, 11.3 et 11.4
