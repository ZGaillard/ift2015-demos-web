# Démonstration 10 : Arbres Rouge-Noir

Cette démonstration couvre le **chapitre 11.6** (Arbres Rouge-Noir) du livre *Data Structures and Algorithms in Java (6th ed.)* : propriétés, correspondance avec les arbres (2,4), insertions et suppressions dans `RedBlackTreeMap`.

!!! abstract "Objectifs d'apprentissage"

    - Énoncer les quatre propriétés d'un arbre rouge-noir et expliquer ce qu'est la **hauteur noire** (§11.6)
    - Comprendre la correspondance entre arbres rouge-noir et arbres (2,4) (§11.6.1)
    - Appliquer l'algorithme d'insertion en identifiant le cas **recoloriage** vs **restructuration** (§11.6.2)
    - Appliquer l'algorithme de suppression en résolvant le problème du **double-noir** (§11.6.3)
    - Lire et compléter le code Java de `RedBlackTreeMap` en utilisant le champ `aux` (§11.2 + §11.6)

---

# Partie I — Théorie

## 1. Propriétés des arbres Rouge-Noir (§11.6)

Un arbre rouge-noir est un BST dans lequel chaque nœud est coloré **rouge** ou **noir** et satisfait les quatre propriétés suivantes :

| # | Propriété |
|---|-----------|
| **P1** | La racine est **noire**. |
| **P2** | Les nœuds externes (feuilles sentinelles) sont **noirs**. |
| **P3** | Les enfants d'un nœud **rouge** sont tous les deux **noirs** (pas de rouge-rouge consécutif). |
| **P4** | Tous les chemins simples d'un nœud vers ses descendants externes traversent le **même nombre de nœuds noirs**. |

Ce nombre commun de nœuds noirs s'appelle la **hauteur noire** (*black-height*) du nœud.

### Borne sur la hauteur

!!! info "Théorème"

    Un arbre rouge-noir contenant $n$ nœuds internes a une hauteur $h \leq 2 \log_2(n+1)$.

    **Preuve :** par P4, la hauteur noire de la racine est au moins $h/2$ (au pire, chaque autre nœud sur un chemin est rouge). Un sous-arbre de hauteur noire $k$ contient au moins $2^k - 1$ nœuds internes, donc $n \geq 2^{h/2} - 1$, d'où $h \leq 2\log_2(n+1)$.

### Code `RedBlackTreeMap` : le champ `aux`

`RedBlackTreeMap` hérite de `BalanceableBinaryTree`. Comme pour `AVLTreeMap`, le champ `aux` est réutilisé — ici pour stocker la **couleur** :

```java
// Codage de la couleur dans le champ aux
// aux == 0  →  NOIR
// aux == 1  →  ROUGE

protected boolean isRed(Position<Entry<K,V>> p) {
    return tree.getAux(p) == 1;
}
protected boolean isBlack(Position<Entry<K,V>> p) {
    return tree.getAux(p) == 0;
}
protected void makeRed(Position<Entry<K,V>> p) { tree.setAux(p, 1); }
protected void makeBlack(Position<Entry<K,V>> p) { tree.setAux(p, 0); }
```

Les nœuds **externes** (sentinelles) ont `aux = 0` (noir) — ce qui satisfait automatiquement P2.

---

## 2. Correspondance avec les arbres (2,4) (§11.6.1)

Chaque **nœud noir** avec ses enfants rouges éventuels forme un **super-nœud** correspondant à un nœud de l'arbre (2,4) équivalent :

| Configuration rouge-noir | Nœud (2,4) correspondant |
|--------------------------|--------------------------|
| Noir seul (2 enfants noirs) | Nœud 2-nœud (1 clé) |
| Noir + 1 enfant rouge | Nœud 3-nœud (2 clés) |
| Noir + 2 enfants rouges | Nœud 4-nœud (3 clés) |

```
     B           B              B
    / \         / \            / \
   B   B       R   B          R   R
               |              |   |
              (3-nœud)       (4-nœud)
```

Cette correspondance garantit que la hauteur noire est exactement la hauteur de l'arbre (2,4) équivalent, soit $O(\log n)$.

!!! warning "Attention"

    La correspondance ne signifie **pas** que les deux structures ont la même hauteur. L'arbre rouge-noir peut avoir une hauteur deux fois plus grande (car les nœuds rouges ajoutent des niveaux supplémentaires).

---

## 3. Insertion (§11.6.2)

### Algorithme

1. Insérer $p$ comme dans un BST ordinaire.
2. Colorer $p$ en **rouge**.
3. Si $p$ est la racine → la colorier en **noir** et terminer.
4. Si le parent $q$ de $p$ est **noir** → aucune violation (P3 respectée). Terminer.
5. Si $q$ est **rouge** → **double-rouge** : P3 est violée. Appliquer le cas approprié.

### Résolution du double-rouge

Soient $q = \text{parent}(p)$, $r = \text{parent}(q)$ et $s = \text{sibling}(q)$ (l'oncle de $p$).

```
          r (noir)
         / \
        q   s        ← s est l'oncle de p
       / \
      p   ...
    (rouge)
```

#### Cas 1 — Recoloriage (*recoloring*) : $s$ est **rouge**

- Colorer $q$ et $s$ en **noir**, $r$ en **rouge**.
- Si $r$ est la racine, le recolorer en **noir** et terminer.
- Sinon, $r$ peut maintenant créer un nouveau double-rouge avec son propre parent → répéter depuis $r$.

```
Avant :          r (noir)          Après :      r (rouge ← à vérifier)
                / \                             / \
            q(R)  s(R)                      q(N)  s(N)
            /                               /
          p(R)                            p(R)
```

!!! info "Propagation"

    Le recoloriage peut se propager jusqu'à la racine en $O(\log n)$ étapes. C'est le seul cas d'insertion qui peut remonter.

#### Cas 2 — Restructuration (*restructuring*) : $s$ est **noir**

- Appeler `restructure(p)` → le médian $b$ parmi $\{p, q, r\}$ devient racine du sous-arbre.
- Colorer $b$ en **noir**, ses deux enfants ($a$ et $c$) en **rouge**.
- Terminé — aucune propagation.

```
Avant (zig-zag GD) :        r (noir)      Après :       q (noir)
                            / \                          / \
                         q(R)  s(N)                   p(R)  r(R)
                           \                                 \
                           p(R)                              s(N)
```

!!! note "Combinaison des cas"

    Le cas 2 couvre à la fois zig-zig et zig-zag (déterminé par la configuration de $p$ et $q$). Dans les deux cas, après `restructure`, on recolorie la nouvelle racine en **noir** et ses deux fils en **rouge**.

### Code Java

```java
// RedBlackTreeMap (simplifié)

@Override
protected void rebalanceInsert(Position<Entry<K,V>> p) {
    if (!isRoot(p)) {
        makeRed(p);
        resolveRed(p);
    }
    // si p est la racine, elle reste noire (initialisée à 0)
}

private void resolveRed(Position<Entry<K,V>> p) {
    if (isRoot(p)) {
        makeBlack(p);
        return;
    }
    Position<Entry<K,V>> q = parent(p);
    if (isRed(q)) {
        // double-rouge : q et p sont tous les deux rouges
        Position<Entry<K,V>> s = sibling(q);   // oncle de p
        if (isBlack(s)) {
            // Cas 2 : restructuration
            p = restructure(p);
            makeBlack(p);
            makeRed(left(p));
            makeRed(right(p));
        } else {
            // Cas 1 : recoloriage
            makeBlack(q);
            makeBlack(s);
            Position<Entry<K,V>> r = parent(q);
            makeRed(r);
            resolveRed(r);   // récursion éventuelle
        }
    }
}
```

### Complexité de l'insertion

| Phase | Coût |
|-------|------|
| Descente BST | $O(\log n)$ |
| Recoloriages (Cas 1, propagation) | $O(\log n)$ au plus |
| Restructurations (Cas 2) | **Au plus 1** |
| **Total** | **$O(\log n)$** |

---

## 4. Suppression (§11.6.3)

### Algorithme

1. Supprimer le nœud via l'algorithme BST standard. Soit $r$ le nœud **retiré** (nœud interne ayant au plus un enfant interne) et $c$ l'enfant qui le remplace.
2. Si $r$ était **rouge** → aucune propriété violée. Terminer.
3. Si $r$ était **noir** et $c$ est **rouge** → colorer $c$ en noir. Terminer.
4. Si $r$ et $c$ étaient tous les deux **noirs** → $c$ hérite d'un déficit noir (**double-noir**) : P4 est violée.

### Résolution du double-noir

Soit $p = \text{parent}(c)$ et $s = \text{sibling}(c)$ (le frère de $c$).

#### Cas 1 — $s$ est **rouge**

- `rotate(s)` (rotation autour de $p$ pour amener $s$ au-dessus).
- Recolorer $s$ en noir, $p$ en rouge.
- $c$ a maintenant un nouveau frère noir → appliquer Cas 2, 3 ou 4.

#### Cas 2 — $s$ est **noir**, les deux enfants de $s$ sont **noirs**

- Colorer $s$ en rouge.
- Si $p$ est rouge → colorer $p$ en noir et terminer (double-noir absorbé).
- Si $p$ est noir → $p$ hérite du double-noir → répéter depuis $p$.

#### Cas 3 — $s$ est **noir**, l'enfant de $s$ **du côté de $c$** est rouge, l'autre est noir

- `rotate` l'enfant rouge de $s$ pour créer un Cas 4.
- Recolorer $s$ en rouge, le nouvel enfant en noir.

#### Cas 4 — $s$ est **noir**, l'enfant de $s$ **du côté opposé à $c$** est rouge

- `rotate(s)` (rotation autour de $p$).
- Recolorer : le nouvel enfant rouge de $s$ en noir, $s$ prend la couleur de $p$, $p$ en noir.
- Double-noir résolu. Terminer.

### Complexité de la suppression

| Phase | Coût |
|-------|------|
| Descente + suppression BST | $O(\log n)$ |
| Recoloriages Cas 2 (propagation) | $O(\log n)$ au plus |
| Rotations Cas 1/3/4 | **Au plus 3** au total |
| **Total** | **$O(\log n)$** |

---

## 5. Comparaison AVL, Splay et Rouge-Noir

| Critère | AVL | Rouge-Noir | Splay |
|---------|-----|------------|-------|
| Hauteur maximale | $2\log_2 n$ | $2\log_2(n+1)$ | $O(n)$ |
| Rotations à l'insertion | $\leq 1$ | $\leq 1$ | $O(\log n)$ amorti |
| Rotations à la suppression | $O(\log n)$ | $\leq 3$ | $O(\log n)$ amorti |
| Complexité par opération | $O(\log n)$ garanti | $O(\log n)$ garanti | $O(\log n)$ amorti |
| Champ `aux` | hauteur | couleur (0/1) | inutilisé |
| Avantage pratique | Hauteur minimale | Suppressions rapides | Accès répétitifs |

---

# Partie II — Exercices

## 1. Vrai ou Faux

### Bloc A — Propriétés (§11.6)

??? question "Q1 — Nœud rouge à la racine"

    Dans un arbre rouge-noir valide, la racine peut être rouge si tous les autres nœuds satisfont les propriétés P2–P4.

    ??? success "Réponse"

        **Faux.** La propriété P1 impose explicitement que **la racine est noire**. Si on insère un nœud et qu'une cascade de recoloriages remonte jusqu'à la racine en la coloriant rouge, `resolveRed` la remet en noir.

        Cette convention simplifie l'analyse : en forçant la racine noire, on évite qu'un recoloriage de la racine crée immédiatement un double-rouge.

??? question "Q2 — Hauteur noire et P4"

    La propriété P4 garantit que **tous** les chemins de la racine à une feuille externe ont exactement la même longueur (nombre de nœuds total).

    ??? success "Réponse"

        **Faux.** P4 garantit uniquement que tous ces chemins ont le **même nombre de nœuds noirs** (la hauteur noire), pas le même nombre de nœuds au total. Certains chemins peuvent contenir des nœuds rouges supplémentaires, ce qui les rend plus longs en termes de nombre total de nœuds.

        Exemple : un chemin passant par deux nœuds rouges intercalés est plus long qu'un chemin purement noir, tout en ayant la même hauteur noire.

??? question "Q3 — Borne sur la hauteur"

    Un arbre rouge-noir de hauteur $h$ contient au moins $2^{h/2} - 1$ nœuds internes.

    ??? success "Réponse"

        **Vrai.** Par P3, au plus la moitié des nœuds sur tout chemin racine-feuille peuvent être rouges, donc la hauteur noire est au moins $h/2$. Un sous-arbre de hauteur noire $k$ contient au moins $2^k - 1$ nœuds internes. En prenant $k \geq h/2$, on obtient $n \geq 2^{h/2} - 1$.

??? question "Q4 — Correspondance avec (2,4)"

    Dans la correspondance rouge-noir / arbre (2,4), un nœud noir avec **deux enfants rouges** correspond à un nœud 3-nœud de l'arbre (2,4).

    ??? success "Réponse"

        **Faux.** Un nœud noir avec **deux** enfants rouges correspond à un **4-nœud** (3 clés) de l'arbre (2,4). Un nœud noir avec **un** enfant rouge correspond à un 3-nœud (2 clés). Un nœud noir sans enfant rouge correspond à un 2-nœud (1 clé).

### Bloc B — Insertion (§11.6.2)

??? question "Q5 — Nombre de restructurations à l'insertion"

    Lors d'une insertion dans un arbre rouge-noir, au plus **une** restructuration (appel à `restructure`) peut être nécessaire, même si plusieurs recoloriages ont lieu.

    ??? success "Réponse"

        **Vrai.** Les recoloriages (Cas 1) peuvent se propager vers le haut, mais dès qu'une restructuration (Cas 2) est appliquée, le double-rouge est entièrement résolu sans propagation. Autrement dit, soit on fait de 0 à $O(\log n)$ recoloriages et 0 restructuration, soit on fait des recoloriages jusqu'à un certain niveau puis **exactement 1** restructuration.

??? question "Q6 — Cas 1 vs Cas 2 : dépend de l'oncle"

    Le choix entre recoloriage (Cas 1) et restructuration (Cas 2) lors d'une insertion dépend uniquement de la couleur du **parent** de $p$.

    ??? success "Réponse"

        **Faux.** On applique le Cas 1 ou le Cas 2 en fonction de la couleur de l'**oncle** $s$ (le frère du parent $q$) :
        - $s$ rouge → Cas 1 (recoloriage)
        - $s$ noir → Cas 2 (restructuration)

        La couleur du parent $q$ est ce qui déclenche la procédure (il doit être rouge pour qu'il y ait un double-rouge), mais c'est l'oncle qui détermine le cas.

### Bloc C — Suppression (§11.6.3)

??? question "Q7 — Suppression d'un nœud rouge"

    Supprimer un nœud **rouge** dans un arbre rouge-noir ne nécessite **aucun** rééquilibrage car les propriétés P1–P4 sont automatiquement préservées.

    ??? success "Réponse"

        **Vrai** (sous réserve que le nœud supprimé ait au plus un enfant interne, ce qui est assuré par l'algorithme BST). Si le nœud retiré est rouge, sa suppression n'affecte pas la hauteur noire des chemins (P4 reste satisfaite) et ne crée pas de rouge-rouge (P3 reste satisfaite). Aucune correction n'est nécessaire.

??? question "Q8 — Double-noir et propagation"

    Dans la résolution d'un double-noir, le Cas 2 (frère $s$ noir avec deux enfants noirs, parent $p$ **noir**) est le seul cas où le problème peut se **propager** vers le haut.

    ??? success "Réponse"

        **Vrai.** Dans le Cas 2 avec $p$ noir : on colore $s$ en rouge et $p$ hérite du double-noir. On répète alors depuis $p$. Tous les autres cas (Cas 1, Cas 3, Cas 4, et Cas 2 avec $p$ rouge) résolvent le problème **localement** sans propagation.

??? question "Q9 — Nombre de rotations à la suppression"

    La suppression dans un arbre rouge-noir peut nécessiter jusqu'à $O(\log n)$ rotations pour corriger le double-noir.

    ??? success "Réponse"

        **Faux.** La suppression nécessite **au plus 3 rotations**. Le Cas 2 propage le double-noir sans rotation ; les cas 1, 3 et 4 utilisent chacun au plus une rotation. Dans le pire cas, on passe par le Cas 1 (1 rotation), puis le Cas 3 (1 rotation), puis le Cas 4 (1 rotation) → 3 rotations au total.

        C'est un avantage de l'arbre rouge-noir sur l'AVL, qui peut nécessiter $O(\log n)$ restructurations à la suppression.

??? question "Q10 — Champ `aux` dans `RedBlackTreeMap`"

    Dans `RedBlackTreeMap`, la valeur `aux = 1` indique qu'un nœud est **noir** (car 1 est souvent associé à « vrai »).

    ??? success "Réponse"

        **Faux.** Par convention dans l'implémentation du livre, `aux = 0` indique **noir** et `aux = 1` indique **rouge**. Cette convention est cohérente avec le fait que les nœuds externes sont initialisés à `aux = 0` (noir), satisfaisant P2 sans action supplémentaire.

---

## 2. Choix multiples

??? question "Q11 — Hauteur maximale d'un rouge-noir"

    Un arbre rouge-noir contenant $n = 15$ nœuds internes a une hauteur maximale de :

    - [ ] A) 4
    - [ ] B) 6
    - [ ] C) 7
    - [ ] D) 8

    ??? success "Réponse"

        **D) 8.**

        La borne est $h \leq 2\log_2(n+1) = 2\log_2(16) = 2 \times 4 = 8$.

        En pratique, un arbre rouge-noir de 15 nœuds peut atteindre cette hauteur dans un cas extrêmement dégénéré (tous les chemins alternent rouge-noir). En comparaison, un AVL de 15 nœuds a une hauteur maximale d'environ $2\log_2(15) \approx 7{,}8$ (arrondi à 7).

??? question "Q12 — Correspondance (2,4)"

    Dans la correspondance rouge-noir / (2,4), la **hauteur noire** de la racine d'un arbre rouge-noir correspond à quoi dans l'arbre (2,4) équivalent ?

    - [ ] A) Le nombre total de nœuds de l'arbre (2,4)
    - [ ] B) La hauteur de l'arbre (2,4)
    - [ ] C) Le nombre de 4-nœuds de l'arbre (2,4)
    - [ ] D) Le degré maximal des nœuds de l'arbre (2,4)

    ??? success "Réponse"

        **B) La hauteur de l'arbre (2,4).**

        Dans la correspondance, chaque super-nœud (nœud noir + ses enfants rouges éventuels) forme un nœud de l'arbre (2,4). La hauteur noire compte le nombre de nœuds noirs sur un chemin racine-feuille, ce qui correspond exactement au nombre de niveaux de l'arbre (2,4).

??? question "Q13 — Après une insertion, le nouveau nœud est colorié..."

    Lors d'une insertion dans `RedBlackTreeMap`, quelle est la couleur initiale du nouveau nœud interne $p$ après son insertion par `rebalanceInsert` ?

    - [ ] A) Toujours noir
    - [ ] B) Toujours rouge
    - [ ] C) Rouge si $p$ est une feuille, noir sinon
    - [ ] D) La même couleur que son parent

    ??? success "Réponse"

        **B) Toujours rouge.**

        `rebalanceInsert(p)` commence par `makeRed(p)`. Colorer le nouveau nœud en rouge peut créer un double-rouge (P3 violée) mais ne viole **jamais** P4 (la hauteur noire des chemins n'est pas modifiée). Si on avait colorié en noir, P4 serait immédiatement violée (un chemin traversant $p$ aurait une hauteur noire supérieure aux autres).

??? question "Q14 — Cas 2 insertion : quelle couleur après `restructure` ?"

    Après une restructuration (Cas 2) lors d'une insertion, le nœud $b$ (médian, nouvelle racine du sous-arbre) et ses fils $a$, $c$ prennent quelles couleurs ?

    - [ ] A) $b$ rouge, $a$ et $c$ noirs
    - [ ] B) $b$ noir, $a$ et $c$ rouges
    - [ ] C) $b$ noir, $a$ rouge, $c$ noir
    - [ ] D) $b$, $a$ et $c$ prennent la couleur de $r$ (l'ancien grand-parent)

    ??? success "Réponse"

        **B) $b$ noir, $a$ et $c$ rouges.**

        Après `restructure(p)`, la nouvelle racine $b$ est colorée en **noir** et ses deux fils en **rouge**. Cela correspond à ce que faisait l'ancien grand-parent $r$ (qui était noir) : il prend la place de $r$ tout en maintenant P4 (la hauteur noire du sous-arbre est préservée). Pas de double-rouge résiduel car $b$ est noir.

??? question "Q15 — Nombre maximal de rotations lors d'une suppression"

    Quelle est le nombre **maximal** de rotations effectuées lors d'une suppression dans un arbre rouge-noir ?

    - [ ] A) 1
    - [ ] B) 2
    - [ ] C) 3
    - [ ] D) $O(\log n)$

    ??? success "Réponse"

        **C) 3.**

        Le pire scénario est : Cas 1 (1 rotation pour transformer en Cas 2/3/4), puis Cas 3 (1 rotation pour transformer en Cas 4), puis Cas 4 (1 rotation finale). Cela donne 3 rotations au total. Le Cas 2 propage le double-noir sans rotation.

        C'est là un avantage majeur de l'arbre rouge-noir sur AVL : la suppression coûte un nombre **constant** de rotations, peu importe la taille de l'arbre.

??? question "Q16 — Effet du recoloriage (Cas 1) sur la hauteur noire"

    Après le Cas 1 du recoloriage lors d'une insertion ($q$ et $s$ passent au noir, $r$ passe au rouge), quelle est la hauteur noire du sous-arbre enraciné en $r$ **après** le recoloriage, comparée à **avant** l'insertion de $p$ ?

    - [ ] A) Augmentée de 1
    - [ ] B) Diminuée de 1
    - [ ] C) Inchangée
    - [ ] D) Peut varier selon la structure du sous-arbre

    ??? success "Réponse"

        **C) Inchangée.**

        Avant l'insertion, $r$ était noir et contribuait 1 à la hauteur noire de ses ancêtres. Après le recoloriage, $r$ est rouge et ne contribue plus lui-même, mais $q$ et $s$ sont devenus noirs et contribuent chacun 1 de plus. Le bilan net pour tout chemin traversant $r$ est nul. C'est pourquoi la propagation peut continuer vers les ancêtres de $r$ sans invalider P4 en dessous de $r$.

---

## 3. Courtes réponses

??? question "Q17 — Pourquoi colorer le nouveau nœud en rouge ?"

    Expliquez en 3–4 phrases pourquoi l'insertion colorie le nouveau nœud en **rouge** (et non en noir), et quelle propriété cela risque de violer.

    ??? success "Réponse"

        Si on insérait en **noir**, la hauteur noire des chemins passant par ce nœud augmenterait de 1, violant immédiatement P4 (tous les chemins doivent avoir la même hauteur noire). Corriger P4 serait alors très complexe car il faudrait ajuster tous les chemins voisins.

        En insérant en **rouge**, P4 reste intacte (aucun chemin ne gagne ou ne perd de nœud noir). La seule propriété potentiellement violée est P3 (pas de rouge-rouge consécutif), ce qui n'arrive que si le parent est également rouge. Cette violation est localisée et se résout en $O(\log n)$ par recoloriage ou en $O(1)$ par restructuration.

        En résumé : **rouge préserve P4, et P3 se répare facilement**.

??? question "Q18 — Pourquoi la suppression rouge-noir utilise-t-elle au plus 3 rotations ?"

    Expliquez pourquoi la suppression dans un arbre rouge-noir ne nécessite **jamais** plus de 3 rotations, contrairement à l'AVL qui peut nécessiter $O(\log n)$ restructurations.

    ??? success "Réponse"

        Les cas 1, 3 et 4 de la résolution du double-noir **terminent** la procédure après au plus une rotation chacun. Seul le Cas 2 propage le problème vers le haut, mais il ne fait **aucune** rotation. La clé est que la propagation du Cas 2 ne crée pas de nouvelles rotations : si on arrive au Cas 1, 3 ou 4 après une propagation, c'est la fin.

        Dans le pire cas : Cas 1 → (converti en) Cas 3 → (converti en) Cas 4 → terminé. Cela fait exactement 3 rotations. La propagation Cas 2 peut se répéter $O(\log n)$ fois, mais sans rotation.

        En comparaison, l'AVL peut nécessiter une restructuration à chaque niveau lors d'une suppression car chaque restructuration peut réduire la hauteur locale, déséquilibrant le niveau supérieur.

??? question "Q19 — Arbre rouge-noir et arbre (2,4) : insertions"

    Une insertion dans un arbre rouge-noir correspond à quelle opération dans l'arbre (2,4) équivalent ? Reliez le Cas 1 (recoloriage) et le Cas 2 (restructuration) à leurs équivalents (2,4).

    ??? success "Réponse"

        Dans l'arbre (2,4) équivalent, insérer une clé revient à l'ajouter au nœud feuille correspondant.

        - **Cas 1 (recoloriage)** correspond à un **débordement** (*overflow*) dans un 4-nœud : on sépare le 4-nœud en deux 2-nœuds et on pousse la clé médiane vers le parent. En rouge-noir, cela se traduit par le recoloriage de $q$ et $s$ en noir (séparation) et de $r$ en rouge (remontée vers le parent). La propagation possible reflète le fait que le parent peut lui aussi déborder.

        - **Cas 2 (restructuration)** correspond à l'insertion dans un 2-nœud ou 3-nœud sans débordement : on réarrange les clés localement sans remonter. La restructuration trinode est la réorganisation locale.

??? question "Q20 — Invariant de la hauteur noire après l'insertion"

    Soit un arbre rouge-noir de hauteur noire $k$ avant une insertion. Quelle est la hauteur noire après l'insertion (dans les deux cas : recoloriage et restructuration) ? Justifiez.

    ??? success "Réponse"

        **La hauteur noire reste $k$ dans les deux cas.**

        - **Cas 2 (restructuration)** : après `restructure`, $b$ (médian) est coloré noir et $a$, $c$ en rouge. Le nœud $b$ était auparavant un nœud noir à ce niveau ($r$). Le nombre de nœuds noirs sur tout chemin traversant ce sous-arbre est inchangé.

        - **Cas 1 (recoloriage)** : $q$ et $s$ passent au noir (+1 sur les deux sous-arbres respectifs), $r$ passe au rouge (−1 au niveau de $r$). Bilan net : 0. La hauteur noire de ce sous-arbre est inchangée par rapport à **avant** l'insertion.

        - **Si le recoloriage remonte jusqu'à la racine** et force la racine (rouge) à redevenir noire : la hauteur noire de **tout l'arbre** augmente de 1. C'est le seul scénario où la hauteur noire globale change — mais cela est cohérent car tous les chemins gagnent exactement un nœud noir.

---

## 4. Exercices de trace

??? question "Q21 — Insertions dans un arbre rouge-noir"

    On insère les clés $10, 20, 30, 15, 25, 5$ dans cet ordre dans un arbre rouge-noir initialement vide. Pour chaque déséquilibre, identifiez le cas (recoloriage ou restructuration) et tracez l'arbre résultant.

    !!! note "Convention"

        On note les nœuds rouges entre parenthèses : `(x)` = rouge, `x` = noir.

    ??? success "Réponse"

        **Insertion 10** : racine → noir par défaut.

        ```
        10
        ```

        ---

        **Insertion 20** : fils droit de 10. `rebalanceInsert(20)` → colorie en rouge. Parent 10 est noir → aucun double-rouge.

        ```
           10
             \
            (20)
        ```

        ---

        **Insertion 30** : fils droit de 20. Double-rouge : $p=30$, $q=20$, $r=10$.

        Oncle $s$ = fils gauche de 10 = nœud externe (noir) → **Cas 2 (restructuration)**.

        `restructure(30)` : $a=10$, $b=20$, $c=30$. Rotation gauche autour de 10.

        $b=20$ prend la couleur de $r=10$ (noir), $a=10$ et $c=30$ deviennent rouges.

        ```
            20
           /  \
         (10) (30)
        ```

        ---

        **Insertion 15** : descente BST : 20 → gauche → 10 → droite (15 > 10). 15 est **fils droit de 10**.

        Parent $q=10$ est rouge → double-rouge : $p=15$, $q=10$, $r=20$.

        Oncle $s$ = sibling(10) = fils droit de 20 = (30) = **rouge** → **Cas 1 (recoloriage)**.

        $q=10$ → noir, $s=30$ → noir, $r=20$ → rouge. Mais 20 est la racine → `resolveRed(20)` : racine → recolorer en noir.

        ```
             20
            /  \
           10   30
             \
            (15)
        ```

        Toutes les propriétés sont satisfaites ✓

        ---

        **Insertion 25** : descente BST : 20 → droite → 30 → gauche (25 < 30). 25 est **fils gauche de 30**.

        Parent $q=30$ est **noir** → aucun double-rouge.

        ```
             20
            /  \
           10   30
             \  /
            (15)(25)
        ```

        ---

        **Insertion 5** : descente BST : 20 → gauche → 10 → gauche (5 < 10). 5 est **fils gauche de 10**.

        Parent $q=10$ est **noir** → aucun double-rouge.

        ```
               20
              /  \
            10    30
           /  \   /
          (5)(15)(25)
        ```

        **Arbre final :**

        ```
               20           ← noir
              /  \
            10    30         ← noirs
           /  \   /
          (5)(15)(25)        ← rouges
        ```

        Hauteur noire = 2. Hauteur totale = 3. Toutes les propriétés P1–P4 satisfaites ✓

        Récapitulatif des opérations de rééquilibrage :

        | Insertion | Double-rouge ? | Cas | Opération |
        |-----------|---------------|-----|-----------|
        | 10 | — | — | Racine → noir |
        | 20 | Non | — | — |
        | 30 | Oui (parent 20 rouge, oncle externe noir) | Cas 2 | Restructuration (zig-zig) |
        | 15 | Oui (parent 10 rouge, oncle 30 rouge) | Cas 1 | Recoloriage + racine reste noire |
        | 25 | Non (parent 30 noir) | — | — |
        | 5 | Non (parent 10 noir) | — | — |

??? question "Q22 — Suppression dans un arbre rouge-noir"

    Soit l'arbre rouge-noir suivant (N = noir, R = rouge) :

    ```
             20 (N)
            /    \
          10 (N)  30 (N)
         /    \
        5 (R)  15 (R)
    ```

    On supprime la clé $10$. Tracez les étapes de la suppression et identifiez le cas appliqué pour résoudre d'éventuels problèmes.

    ??? success "Réponse"

        **Étape 1 — Suppression BST de 10**

        10 a deux enfants internes. Par convention BST, on le remplace par son **successeur infixe** (le plus petit élément plus grand que 10), soit $15$.

        On copie la clé $15$ dans le nœud 10, puis on supprime le nœud 15 (qui n'a qu'un enfant gauche vide).

        Le nœud retiré ($r=15$) est **rouge**, et son enfant remplaçant ($c=$ nœud externe) est **noir**.

        **Cas simple** : $r$ est rouge → aucun rééquilibrage nécessaire (P4 intacte).

        ```
                 20 (N)
                /    \
             15 (N)   30 (N)
            /
           5 (R)
        ```

        Vérification des propriétés :
        - P1 : racine 20 est noire ✓
        - P2 : feuilles externes noires ✓
        - P3 : 5 est rouge, ses enfants (externes) sont noirs ✓ ; 15, 20, 30 noirs ✓
        - P4 : hauteur noire = 2 sur tous les chemins ✓

        **Arbre final :**

        ```
                 20 (N)
                /    \
             15 (N)   30 (N)
            /
           5 (R)
        ```

---

## 5. Exercices de code

??? question "Q23 — Compléter `resolveRed`"

    Voici une version squelette de `resolveRed`, la méthode qui résout un double-rouge après une insertion. Complétez les lignes marquées `???` :

    ```java
    private void resolveRed(Position<Entry<K,V>> p) {
        if (isRoot(p)) {
            makeBlack(p);    // (A) — racine toujours noire
            return;
        }
        Position<Entry<K,V>> q = parent(p);
        if (isRed(q)) {
            Position<Entry<K,V>> s = sibling(q);   // oncle de p
            if (???) {                              // (B) — condition pour Cas 2
                // Cas 2 : restructuration
                p = restructure(p);
                ???;                               // (C) — colorier b (= p après restructure)
                makeRed(left(p));
                makeRed(right(p));
            } else {
                // Cas 1 : recoloriage
                makeBlack(q);
                ???;                               // (D) — colorier l'oncle
                Position<Entry<K,V>> r = parent(q);
                makeRed(r);
                ???;                               // (E) — récursion éventuelle
            }
        }
    }
    ```

    1. Donnez les expressions pour `(B)`, `(C)`, `(D)` et `(E)`.
    2. Expliquez pourquoi après `restructure(p)` on colorie `p` en **noir** ET ses deux fils en **rouge** (et non l'inverse).

    ??? success "Réponse"

        ```java
        if (isBlack(s)) {                    // (B)
            p = restructure(p);
            makeBlack(p);                    // (C)
            makeRed(left(p));
            makeRed(right(p));
        } else {
            makeBlack(q);
            makeBlack(s);                    // (D)
            Position<Entry<K,V>> r = parent(q);
            makeRed(r);
            resolveRed(r);                   // (E)
        }
        ```

        **Explication : pourquoi $b$ noir et ses fils rouges ?**

        Après `restructure(p)`, la variable `p` pointe vers $b$, la **nouvelle racine du sous-arbre** (le médian parmi $\{x, y, z\}$). Il faut :

        - **$b$ noir** : $b$ prend la place de l'ancien grand-parent $r$, qui était noir. Si $b$ était rouge, la hauteur noire des chemins traversant ce sous-arbre diminuerait de 1, violant P4.
        - **Fils $a$ et $c$ rouges** : $a$ et $c$ étaient respectivement l'un des anciens nœuds rouges ($p$ ou $q$) et l'ancien $r$ restructuré. Les colorer rouges assure que la hauteur noire locale est conservée : un chemin passant par $b$ puis par $a$ (ou $c$) compte toujours exactement un nœud noir à ce niveau ($b$), comme avant.

        Si on avait fait l'inverse ($b$ rouge, fils noirs), on aurait ajouté des nœuds noirs sur les chemins passant par $a$ et $c$, violant P4 dans les sous-arbres voisins.

??? question "Q24 — Valider un arbre rouge-noir"

    Implémentez la méthode `isValidRedBlack()` qui retourne `true` si et seulement si l'arbre courant satisfait **toutes** les propriétés P1–P4. Utilisez une méthode auxiliaire récursive qui retourne la hauteur noire du sous-arbre (ou `-1` en cas de violation).

    ```java
    /**
     * Retourne la hauteur noire du sous-arbre enraciné en p
     * si ce sous-arbre est un rouge-noir valide,
     * ou -1 si une propriété est violée.
     */
    private int checkRB(Position<Entry<K,V>> p, boolean parentRed) {
        if (isExternal(p)) return ???;                // (A) — cas de base

        // Vérifier P3 : pas de rouge-rouge
        if (???) return -1;                           // (B)

        int hL = checkRB(left(p), isRed(p));
        if (???) return -1;                           // (C) — propagation gauche

        int hR = checkRB(right(p), isRed(p));
        if (???) return -1;                           // (D) — propagation droite

        if (???) return -1;                           // (E) — P4 : égalité des hauteurs noires

        return ???;                                   // (F) — hauteur noire de p
    }

    public boolean isValidRedBlack() {
        if (isEmpty()) return true;
        if (isRed(root())) return false;              // P1
        return checkRB(root(), false) != -1;
    }
    ```

    Complétez les expressions `(A)` à `(F)`.

    ??? success "Réponse"

        ```java
        private int checkRB(Position<Entry<K,V>> p, boolean parentRed) {
            if (isExternal(p)) return 0;                        // (A)

            if (parentRed && isRed(p)) return -1;               // (B)

            int hL = checkRB(left(p), isRed(p));
            if (hL == -1) return -1;                            // (C)

            int hR = checkRB(right(p), isRed(p));
            if (hR == -1) return -1;                            // (D)

            if (hL != hR) return -1;                            // (E)

            return hL + (isBlack(p) ? 1 : 0);                  // (F)
        }
        ```

        **Explication ligne par ligne :**

        - **(A)** : Les nœuds externes sont noirs (P2) et contribuent 0 à la hauteur noire (on ne les compte pas, ou on les considère comme $-1$ et on ajoute 1 pour les feuilles — ici on commence à compter à partir de 0 pour les externes et 1 pour les internes noirs).
        - **(B)** : Vérifie P3 : si le parent est rouge et le nœud courant aussi → violation.
        - **(C)-(D)** : Propagation d'une violation détectée en dessous.
        - **(E)** : Vérifie P4 : les hauteurs noires gauche et droite doivent être égales.
        - **(F)** : La hauteur noire de $p$ est $h_L$ (= $h_R$) + 1 si $p$ est noir, ou $h_L$ si $p$ est rouge (les nœuds rouges ne contribuent pas à la hauteur noire).

        **Complexité :** $O(n)$ — chaque nœud est visité exactement une fois.

??? question "Q25 — Déboguer `resolveRed`"

    L'implémentation suivante de `resolveRed` contient **une erreur** : les corps des cas 1 et 2 ont été échangés (comme dans la variante bogué du splay en démo 9).

    ```java
    private void resolveRed(Position<Entry<K,V>> p) {
        if (isRoot(p)) { makeBlack(p); return; }
        Position<Entry<K,V>> q = parent(p);
        if (isRed(q)) {
            Position<Entry<K,V>> s = sibling(q);
            if (isBlack(s)) {
                // (A) — corps incorrect
                makeBlack(q);
                makeBlack(s);
                Position<Entry<K,V>> r = parent(q);
                makeRed(r);
                resolveRed(r);
            } else {
                // (B) — corps incorrect
                p = restructure(p);
                makeBlack(p);
                makeRed(left(p));
                makeRed(right(p));
            }
        }
    }
    ```

    1. Expliquez précisément pourquoi chaque branche est incorrecte.
    2. Quelle propriété rouge-noir est violée par ce bug dans chacun des deux cas ?
    3. Donnez le code corrigé.

    ??? success "Réponse"

        **1. Analyse des erreurs :**

        - **Branche (A) — condition `isBlack(s)`, corps recoloriage** : quand l'oncle $s$ est **noir**, il faut **restructurer** (Cas 2). Or le code effectue un recoloriage : il colore $q$ et $s$ en noir et $r$ en rouge. Mais $s$ était déjà noir — `makeBlack(s)` est sans effet. Pire, colorer $r$ en rouge alors que $q$ reste rouge crée un **nouveau double-rouge** ($r$ rouge avec $q$ rouge) sans résoudre le problème d'origine.

        - **Branche (B) — condition `else` (oncle rouge), corps restructuration** : quand l'oncle $s$ est **rouge**, il faut **recolorer** (Cas 1). Or le code appelle `restructure(p)` et recolore. La restructuration est mathématiquement valide localement, mais elle ne résout pas le bon problème : dans ce contexte, la hauteur noire du sous-arbre change par rapport à ce qu'elle était avant l'insertion, violant P4 pour les ancêtres.

        **2. Propriétés violées :**

        - Branche (A) : **P3** est violée (double-rouge entre $r$ et $q$ non résolu, voire aggravé) ET la propagation correcte n'a pas lieu.
        - Branche (B) : **P4** est violée car la restructuration dans ce contexte (oncle rouge) modifie la hauteur noire locale au lieu de la conserver.

        **3. Code corrigé :**

        ```java
        private void resolveRed(Position<Entry<K,V>> p) {
            if (isRoot(p)) { makeBlack(p); return; }
            Position<Entry<K,V>> q = parent(p);
            if (isRed(q)) {
                Position<Entry<K,V>> s = sibling(q);
                if (isBlack(s)) {
                    // Cas 2 : oncle noir → restructuration
                    p = restructure(p);
                    makeBlack(p);
                    makeRed(left(p));
                    makeRed(right(p));
                } else {
                    // Cas 1 : oncle rouge → recoloriage
                    makeBlack(q);
                    makeBlack(s);
                    Position<Entry<K,V>> r = parent(q);
                    makeRed(r);
                    resolveRed(r);
                }
            }
        }
        ```

---

# Corrections des défis

## Défi 1 — File avec 2 piles

**Problème :** implémenter l'interface `Queue<E>` (`enqueue`, `dequeue`, `first`, `size`, `isEmpty`) en utilisant **uniquement deux piles** (`Stack<E>`).

**Idée clé :** une pile inverse l'ordre des éléments ; deux inversions le restaurent. On distingue une pile d'**entrée** (`inbox`) et une pile de **sortie** (`outbox`).

- `enqueue` : empiler sur `inbox` → O(1).
- `dequeue` / `first` : si `outbox` est vide, transverser **tout** `inbox` vers `outbox` (chaque élément est transvasé au plus une fois). Dépiler depuis `outbox` → **O(1) amorti**.

```java
public class QueueWith2Stacks<E> implements Queue<E> {

    private ArrayStack<E> inbox  = new ArrayStack<>();
    private ArrayStack<E> outbox = new ArrayStack<>();

    @Override public int  size()    { return inbox.size() + outbox.size(); }
    @Override public boolean isEmpty() { return inbox.isEmpty() && outbox.isEmpty(); }

    @Override
    public void enqueue(E e) {
        inbox.push(e);                  // O(1)
    }

    @Override
    public E first() {
        refill();                       // transvase inbox→outbox si outbox vide
        if (outbox.isEmpty()) throw new NoSuchElementException();
        return outbox.top();
    }

    @Override
    public E dequeue() {
        refill();
        if (outbox.isEmpty()) throw new NoSuchElementException();
        return outbox.pop();
    }

    /** Transvase inbox dans outbox uniquement si outbox est vide. */
    private void refill() {
        if (outbox.isEmpty())
            while (!inbox.isEmpty())
                outbox.push(inbox.pop());
    }
}
```

**Analyse de complexité :**

Chaque élément effectue exactement **deux** mouvements dans sa vie :
1. `inbox.push` lors de son `enqueue`
2. `outbox.push` lors d'un `refill` (au plus une fois)

| Opération | Pire cas | Amorti |
|-----------|----------|--------|
| `enqueue` | $O(1)$ | $O(1)$ |
| `dequeue` / `first` | $O(n)$ | **$O(1)$** |

!!! warning "Pire cas vs amorti"

    Un seul appel à `dequeue` peut coûter $O(n)$ si `outbox` est vide et `inbox` contient $n$ éléments. Mais ce coût est amorti sur la séquence : les $n$ éléments transvasés ne le seront plus jamais — les $n$ appels suivants à `dequeue` coûteront chacun $O(1)$.

---

## Défi 2 — Pile avec 2 files

**Problème :** implémenter l'interface `Stack<E>` (`push`, `pop`, `top`, `size`, `isEmpty`) en utilisant **uniquement deux files** (`Queue<E>`).

**Contrainte fondamentale :** une file ne permet d'accéder qu'à son premier élément (FIFO). Pour simuler LIFO, il faut que le dernier élément enfilé soit le prochain à sortir. On ne peut pas faire cela sans **parcourir toute la file** à chaque `pop`.

!!! info "Résultat optimal"

    Il est **impossible** de simuler une pile avec 2 files en $O(1)$ amorti pour les deux opérations. Le mieux atteignable est : soit `push` en $O(1)$ et `pop` en $O(n)$, soit l'inverse. Nous choisissons la première stratégie (plus intuitive).

**Stratégie — `push` $O(1)$, `pop` $O(n)$ :**

- `main` contient tous les éléments dans l'ordre FIFO normal.
- `push(e)` : enfiler dans `main` → O(1).
- `pop()` : transférer tout sauf le **dernier** élément de `main` vers `aux`, déenfiler le dernier (c'est le sommet de la pile), puis échanger `main` et `aux`.

```java
public class StackWith2Queues<E> implements Stack<E> {

    private LinkedQueue<E> main = new LinkedQueue<>();
    private LinkedQueue<E> aux  = new LinkedQueue<>();

    @Override public int  size()      { return main.size(); }
    @Override public boolean isEmpty() { return main.isEmpty(); }

    @Override
    public void push(E e) {
        main.enqueue(e);                // O(1)
    }

    @Override
    public E top() {
        if (isEmpty()) throw new EmptyStackException();
        // Transférer tout sauf le dernier dans aux
        while (main.size() > 1)
            aux.enqueue(main.dequeue());
        E top = main.first();           // sommet = dernier enfilé
        aux.enqueue(main.dequeue());    // le remettre dans aux aussi
        swap();
        return top;
    }

    @Override
    public E pop() {
        if (isEmpty()) throw new EmptyStackException();
        while (main.size() > 1)
            aux.enqueue(main.dequeue());
        E top = main.dequeue();         // O(n) au total
        swap();
        return top;
    }

    private void swap() {
        LinkedQueue<E> tmp = main;
        main = aux;
        aux = tmp;
    }
}
```

**Analyse :**

| Opération | Complexité |
|-----------|------------|
| `push` | $O(1)$ |
| `pop` / `top` | $O(n)$ |

Aucune amélioration amortie n'est possible ici : contrairement au Défi 1, il n'existe pas d'argument de « prépaiement » car chaque `pop` doit **inspecter tous les éléments** pour trouver le dernier.

---

## Défi 3 — File de priorité avec min et max en O(1) : le tas min-max

**Problème :** implémenter une file de priorité supportant `min()` **et** `max()` en $O(1)$, tout en maintenant `insert`, `removeMin`, `removeMax` en $O(\log n)$.

### Idée : niveaux alternés min / max

Un **tas min-max** (*min-max heap*) est un tas binaire complet (stocké en tableau) dont les niveaux **alternent** entre niveaux-min et niveaux-max :

```
Niveau 0 (min) :           1          ← racine = minimum global
                          / \
Niveau 1 (max) :         3   6        ← max parmi les fils = maximum global
                        / \ / \
Niveau 2 (min) :       4  5 7  8
                      ...
```

**Invariant :** tout nœud sur un niveau-min est ≤ tous ses descendants ; tout nœud sur un niveau-max est ≥ tous ses descendants.

**Conséquences immédiates :**
- `min()` → `heap[1]` (racine) → **O(1)**
- `max()` → `max(heap[2], heap[3])` (enfants de la racine) → **O(1)**

```java
public E min() {
    if (size == 0) throw new NoSuchElementException();
    return heap[1];                    // O(1) : racine = min global
}

public E max() {
    if (size == 0) throw new NoSuchElementException();
    if (size == 1) return heap[1];
    if (size == 2) return heap[2];
    // Les deux fils de la racine sont sur un niveau-max
    return heap[2].compareTo(heap[3]) >= 0 ? heap[2] : heap[3];  // O(1)
}
```

### Insertion — `pushUp`

On ajoute l'élément à la fin du tableau, puis on le remonte selon l'invariant :

1. Si le nœud $i$ est sur un **niveau-min** :
    - Si `heap[i] > heap[parent(i)]` (on est plus grand que le max-parent) → échanger avec le parent et continuer en `pushUpMax` depuis le parent.
    - Sinon → `pushUpMin` : remonter par grands-parents tant que plus petit.
2. Symétrique pour un **niveau-max**.

```java
public void insert(E e) {
    if (size == heap.length - 1) resize();
    heap[++size] = e;
    pushUp(size);
}

private void pushUp(int i) {
    if (i == 1) return;
    int p = i / 2;
    if (isMinLevel(i)) {
        if (heap[i].compareTo(heap[p]) > 0) { swap(i, p); pushUpMax(p); }
        else                                 { pushUpMin(i); }
    } else {
        if (heap[i].compareTo(heap[p]) < 0) { swap(i, p); pushUpMin(p); }
        else                                 { pushUpMax(i); }
    }
}

private void pushUpMin(int i) {
    int gp = i / 4;       // grand-parent
    if (gp >= 1 && heap[i].compareTo(heap[gp]) < 0) {
        swap(i, gp);
        pushUpMin(gp);
    }
}

private void pushUpMax(int i) {
    int gp = i / 4;
    if (gp >= 1 && heap[i].compareTo(heap[gp]) > 0) {
        swap(i, gp);
        pushUpMax(gp);
    }
}

// Niveau d'un nœud i : floor(log2(i))
// Niveau pair = niveau-min, niveau impair = niveau-max
private boolean isMinLevel(int i) {
    return (Integer.numberOfTrailingZeros(Integer.highestOneBit(i)) % 2) == 0;
}
```

### Suppression du minimum — `removeMin`

1. Remplacer `heap[1]` par `heap[size--]`.
2. Faire descendre la nouvelle racine vers son bon emplacement sur les niveaux-min (`pushDownMin`).

```java
public E removeMin() {
    if (size == 0) throw new NoSuchElementException();
    E min = heap[1];
    heap[1] = heap[size--];
    if (size > 0) pushDownMin(1);
    return min;
}

private void pushDownMin(int i) {
    // Trouver le plus petit parmi enfants et petits-enfants
    int m = smallestDescendant(i, 2);   // 2 générations
    if (m == -1) return;
    if (m > 2 * i + 1) {                // m est un petit-enfant
        if (heap[m].compareTo(heap[i]) < 0) {
            swap(m, i);
            if (heap[m].compareTo(heap[m / 2]) > 0)  // vérifier le parent (niveau-max)
                swap(m, m / 2);
            pushDownMin(m);
        }
    } else {                            // m est un enfant direct
        if (heap[m].compareTo(heap[i]) < 0)
            swap(m, i);
    }
}
```

`removeMax` est **symétrique** : on retire `heap[2]` ou `heap[3]` (le plus grand fils de la racine) et on fait descendre avec `pushDownMax`.

### Récapitulatif des complexités

| Opération | Complexité |
|-----------|------------|
| `min()` | **$O(1)$** |
| `max()` | **$O(1)$** |
| `insert(e)` | $O(\log n)$ |
| `removeMin()` | $O(\log n)$ |
| `removeMax()` | $O(\log n)$ |

!!! info "Pourquoi le max est-il toujours parmi les deux fils de la racine ?"

    Par l'invariant des niveaux-max : les fils de la racine (niveau 1, max-niveau) sont ≥ tous leurs descendants. Tout élément de l'arbre est soit la racine, soit dans le sous-arbre de l'un des fils. La racine est le minimum global, donc le maximum doit être parmi les fils — il ne peut pas être plus profond car un nœud de niveau-max plus bas est contraint à être ≤ son ancêtre de niveau-max au-dessus de lui.
