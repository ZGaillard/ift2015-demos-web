# Démonstration 2 : Listes et listes positionnelles

Cette démonstration porte sur le **Chapitre 7** du livre *Data Structures and Algorithms in Java (6th ed.)* — **Listes et listes positionnelles**.

!!! abstract "Objectifs d'apprentissage"
    À la fin de cette démonstration, vous devriez être capable de :

    - Distinguer les différentes abstractions et implémentations de listes
    - Justifier l'utilisation des listes positionnelles dans les applications appropriées
    - Raisonner sur la complexité temporelle selon le choix de structure
    - Implémenter de façon sécuritaire des structures de données chaînées
    - Comprendre la circularité comme choix de conception structurelle

---

## Rappels théoriques

### L'ADT Liste

Une **liste** représente une séquence ordonnée d'éléments où :

- Chaque élément a une position bien définie dans la séquence
- Les doublons sont permis
- L'ordre est important

### Limites des listes indexées

| Opération | Liste par tableau | Liste chaînée |
|-----------|-------------------|---------------|
| Accès par index | O(1) | O(n) |
| Insertion au début | O(n) | O(1) |
| Insertion à la fin | O(1) amorti | O(1) |
| Insertion au milieu | O(n) | O(1)* |

*\* Si on possède déjà une référence à la position*

### Listes positionnelles

Une **liste positionnelle** stocke les éléments dans des nœuds accessibles via des **objets Position** plutôt que des indices numériques.

- Une `Position` représente un emplacement stable dans la liste
- Les positions restent valides tant qu'elles ne sont pas explicitement supprimées
- Les insertions et suppressions relatives à une position connue se font en O(1)

### Nœuds sentinelles

Les **nœuds sentinelles** (header et trailer) :

- Ne stockent pas de données utilisateur
- Éliminent les cas spéciaux lors de l'insertion ou suppression aux extrémités
- Simplifient la logique d'implémentation

### Listes circulaires

Une **liste circulaire** est une liste chaînée où :

- Le dernier nœud pointe vers le premier nœud
- Il n'y a pas de début ou de fin naturels
- Traversée uniforme sans cas spéciaux

---

## Partie 1 — Exercices théoriques

### 1.1 Vrai ou Faux

Pour chaque énoncé, indiquez s'il est **vrai** ou **faux** et justifiez votre réponse.

??? question "Question 1 — Stabilité des positions"
    Considérez une liste positionnelle contenant les éléments `[A, B, C, D]`. Vous stockez la position de `B` dans une variable `posB`. Après avoir supprimé l'élément `C` de la liste, la position `posB` reste valide et pointe toujours vers `B`.

    ??? success "Réponse"
        **Vrai.** C'est précisément l'avantage des listes positionnelles par rapport aux listes indexées. Une position représente un emplacement stable dans la structure. La suppression d'un autre élément (`C`) ne modifie pas la validité de `posB`.

        En revanche, si on utilisait un indice (index 1 pour `B`), la suppression de `C` ne changerait pas l'indice de `B`, mais si on avait supprimé `A`, l'indice de `B` serait passé de 1 à 0.

        Une position ne devient invalide que lorsque **son propre élément** est supprimé.

??? question "Question 2 — Complexité de l'insertion"
    Dans une liste simplement chaînée (sans référence au dernier nœud), l'insertion d'un élément **à la fin** de la liste est une opération O(1).

    ??? success "Réponse"
        **Faux.** Sans référence directe au dernier nœud, il faut parcourir toute la liste pour atteindre la fin, ce qui prend O(n).

        C'est pourquoi les implémentations pratiques maintiennent souvent une référence `tail` vers le dernier élément, ce qui permet l'insertion en O(1). Cependant, l'énoncé précise explicitement « sans référence au dernier nœud ».

??? question "Question 3 — Suppression dans une liste circulaire"
    Dans une liste circulaire simplement chaînée où on maintient uniquement un pointeur `tail` vers le dernier élément, la suppression du **premier** élément se fait en O(1).

    ??? success "Réponse"
        **Vrai.** C'est un avantage subtil mais important de la liste circulaire avec pointeur `tail`.

        - Le premier élément est `tail.next`
        - Pour le supprimer, il suffit de faire `tail.next = tail.next.next`

        Cela ne nécessite aucun parcours. En revanche, supprimer le **dernier** élément (celui pointé par `tail`) nécessiterait de parcourir la liste pour trouver l'avant-dernier nœud, car on ne peut pas remonter en arrière dans une liste simplement chaînée.

??? question "Question 4 — Sentinelles et utilisateur"
    Dans une liste positionnelle avec sentinelles, la méthode `first()` retourne la position du nœud header lorsque la liste est vide.

    ??? success "Réponse"
        **Faux.** Les sentinelles sont des détails d'implémentation qui ne doivent **jamais** être exposés à l'utilisateur. Quand la liste est vide, `first()` doit retourner `null` (ou lever une exception selon la spécification).

        Les sentinelles existent pour simplifier le code interne en éliminant les cas spéciaux, mais l'abstraction du TAD doit cacher ces détails. Si un utilisateur pouvait obtenir une position vers une sentinelle, il pourrait corrompre la structure en y stockant des données ou en la supprimant.

??? question "Question 5 — Invalidation de position"
    Considérez le code suivant sur une liste positionnelle :
    ```java
    Position<String> p = list.first();
    String element = list.remove(p);
    list.addFirst(element);
    Position<String> q = list.first();
    // À ce point, p et q référencent la même position
    ```

    L'assertion finale est vraie : `p` et `q` référencent la même position.

    ??? success "Réponse"
        **Faux.** Après `remove(p)`, la position `p` est **invalidée** — le nœud sous-jacent est marqué comme défunt (typiquement en mettant `next = null`).

        L'appel `addFirst(element)` crée un **nouveau nœud** avec une **nouvelle position** `q`. Même si l'élément est le même (`element`), les positions `p` et `q` sont distinctes. De plus, toute tentative d'utiliser `p` après sa suppression devrait lever une exception.

        C'est une erreur courante de confondre l'élément (la donnée) avec la position (le conteneur).

??? question "Question 6 — Parcours et modification"
    Il est toujours sécuritaire de supprimer des éléments d'une liste positionnelle pendant qu'on la parcourt avec un itérateur standard (`Iterator`).

    ??? success "Réponse"
        **Faux.** La plupart des implémentations d'itérateurs en Java adoptent une stratégie *fail-fast* : si la liste est modifiée structurellement pendant l'itération (autrement que par la méthode `remove()` de l'itérateur lui-même), une `ConcurrentModificationException` est levée.

        Pour supprimer des éléments pendant un parcours, il faut soit :

        1. Utiliser la méthode `remove()` de l'itérateur
        2. Collecter les positions à supprimer, puis les supprimer après le parcours
        3. Parcourir manuellement avec `first()`, `after()`, en faisant attention à récupérer la position suivante **avant** de supprimer

---

### 1.2 Questions à choix multiples

??? question "Question 7 — Trace d'exécution"
    Considérez une liste positionnelle initialement vide. On exécute les opérations suivantes :

    ```java
    Position<Integer> p1 = list.addFirst(1);
    Position<Integer> p2 = list.addLast(2);
    Position<Integer> p3 = list.addAfter(p1, 3);
    Position<Integer> p4 = list.addBefore(p2, 4);
    list.remove(p3);
    ```

    Quel est le contenu de la liste après ces opérations (du premier au dernier élément) ?

    - [ ] A) `[1, 4, 2]`
    - [ ] B) `[1, 3, 4, 2]`
    - [ ] C) `[1, 2, 4]`
    - [ ] D) `[1, 4, 3, 2]`

    ??? success "Réponse"
        **A) `[1, 4, 2]`**

        Traçons les opérations :

        1. `addFirst(1)` → `[1]`
        2. `addLast(2)` → `[1, 2]`
        3. `addAfter(p1, 3)` → insère 3 après p1 (qui est 1) → `[1, 3, 2]`
        4. `addBefore(p2, 4)` → insère 4 avant p2 (qui est 2) → `[1, 3, 4, 2]`
        5. `remove(p3)` → supprime p3 (qui est 3) → `[1, 4, 2]`

??? question "Question 8 — Choix de structure"
    Vous devez implémenter un historique de navigation web (boutons « Précédent » et « Suivant »). L'utilisateur peut :

    - Visiter une nouvelle page (ajoute à l'historique)
    - Revenir en arrière (page précédente)
    - Aller en avant (si disponible)
    - Quand on visite une nouvelle page après être revenu en arrière, tout l'historique « en avant » est effacé

    Quelle structure est la plus appropriée ?

    - [ ] A) Deux piles (une pour l'arrière, une pour l'avant)
    - [ ] B) Une liste positionnelle avec une position « courante »
    - [ ] C) Une ArrayList avec un index courant
    - [ ] D) Une liste circulaire

    ??? success "Réponse"
        **A) Deux piles (une pour l'arrière, une pour l'avant)**

        Analysons chaque option :

        - **Deux piles** : Solution classique et élégante. « Précédent » = pop de la pile arrière, push sur pile avant. « Suivant » = inverse. Nouvelle visite = push sur pile arrière et vider pile avant. Toutes les opérations sont O(1).

        - **Liste positionnelle** (B) : Fonctionnerait, mais plus complexe que nécessaire. L'effacement de l'historique avant nécessite de supprimer tous les éléments après la position courante.

        - **ArrayList avec index** (C) : Possible, mais l'effacement de l'historique avant est O(n) dans le pire cas.

        - **Liste circulaire** (D) : Inadaptée car l'historique a un début et une fin naturels, pas de circularité.

??? question "Question 9 — Bug subtil"
    Considérez cette implémentation de `addBetween` pour une liste doublement chaînée :

    ```java
    private Position<E> addBetween(E e, Node<E> pred, Node<E> succ) {
        Node<E> newest = new Node<>(e, pred, succ);
        succ.setPrev(newest);
        pred.setNext(newest);
        size++;
        return newest;
    }
    ```

    Et cette implémentation de `remove` :

    ```java
    public E remove(Position<E> p) {
        Node<E> node = validate(p);
        Node<E> predecessor = node.getPrev();
        Node<E> successor = node.getNext();
        predecessor.setNext(successor);
        successor.setPrev(predecessor);
        size--;
        return node.getElement();
    }
    ```

    Quel problème potentiel cette implémentation de `remove` présente-t-elle ?

    - [ ] A) Elle ne gère pas le cas où la liste devient vide
    - [ ] B) Elle ne marque pas le nœud comme invalide, permettant sa réutilisation accidentelle
    - [ ] C) Elle modifie les pointeurs dans le mauvais ordre
    - [ ] D) Elle ne met pas à jour le compteur `size` correctement

    ??? success "Réponse"
        **B) Elle ne marque pas le nœud comme invalide, permettant sa réutilisation accidentelle**

        L'implémentation supprime correctement le nœud de la liste, mais le nœud supprimé conserve ses références (`prev`, `next`, `element`). Si l'utilisateur garde une référence à la position supprimée et tente de l'utiliser plus tard :

        - `node.getElement()` retournerait encore l'ancienne valeur
        - `node.getNext()` pointerait vers un nœud qui n'est plus son successeur logique

        La solution est de « défaire » le nœud après la suppression :
        ```java
        node.setElement(null);
        node.setNext(null);  // Convention pour nœud défunt
        node.setPrev(null);
        ```

        La méthode `validate()` peut alors vérifier si `next == null` pour détecter une position invalide.

??? question "Question 10 — Analyse de complexité"
    Vous avez une liste positionnelle de n éléments et vous souhaitez trouver l'élément à l'index k (0-indexé). Quelle est la meilleure complexité atteignable ?

    - [ ] A) O(1)
    - [ ] B) O(k)
    - [ ] C) O(min(k, n-k))
    - [ ] D) O(n)

    ??? success "Réponse"
        **C) O(min(k, n-k))**

        Une liste positionnelle doublement chaînée permet de parcourir dans les deux directions. Pour atteindre l'index k :

        - Si k < n/2 : partir du début et avancer k fois → O(k)
        - Si k ≥ n/2 : partir de la fin et reculer (n-1-k) fois → O(n-k)

        En choisissant la direction optimale, on obtient O(min(k, n-k)), ce qui est au pire O(n/2) = O(n), mais souvent meilleur.

        Note : Cette optimisation n'est pas toujours implémentée. Une implémentation naïve qui parcourt toujours depuis le début serait O(k), et une qui ne supporte que le parcours avant serait limitée à O(n) dans le pire cas.

??? question "Question 11 — Liste circulaire"
    Dans une liste circulaire doublement chaînée **sans sentinelles** contenant exactement un élément, quelles sont les valeurs de `node.next` et `node.prev` ?

    - [ ] A) Les deux sont `null`
    - [ ] B) `next` pointe vers lui-même, `prev` est `null`
    - [ ] C) Les deux pointent vers le nœud lui-même
    - [ ] D) Les deux pointent vers une sentinelle cachée

    ??? success "Réponse"
        **C) Les deux pointent vers le nœud lui-même**

        Dans une liste circulaire, la circularité doit être maintenue même avec un seul élément. Le nœud est à la fois :

        - Son propre successeur (`next = this`)
        - Son propre prédécesseur (`prev = this`)

        C'est ce qui distingue une liste circulaire d'une liste non-circulaire. Cela permet aussi de ne pas avoir de cas spécial : les algorithmes d'insertion et de suppression fonctionnent uniformément.

---

### 1.3 Questions de réflexion

??? question "Question 12 — Conception d'invariant"
    Dans l'implémentation d'une liste positionnelle avec sentinelles, on utilise la convention que `node.next == null` indique un nœud défunt (supprimé).

    1. Pourquoi ne peut-on pas utiliser `node.element == null` comme indicateur de nœud défunt ?
    2. Proposez une autre convention possible et discutez ses avantages/inconvénients.

    ??? success "Réponse"
        **1. Pourquoi pas `element == null` ?**

        Parce que `null` est une valeur d'élément valide ! Un utilisateur pourrait légitimement vouloir stocker `null` dans la liste. Utiliser `element == null` comme indicateur créerait une ambiguïté entre « ce nœud contient null » et « ce nœud est invalide ».

        En revanche, dans une liste bien formée, `next` ne devrait jamais être `null` :

        - Pour les nœuds normaux, `next` pointe vers le successeur ou le trailer
        - Pour le trailer, `next` pourrait pointer vers le header (circulaire) ou rester non-null

        **2. Alternatives possibles :**

        - **Flag booléen `isValid`** : Clair et explicite, mais ajoute de la mémoire (1 bit logique, mais souvent 1 byte ou plus en pratique à cause de l'alignement).

        - **Référence au conteneur** : Chaque nœud garde une référence à sa liste. Un nœud défunt a `container == null`. Permet aussi de vérifier qu'une position appartient à la bonne liste.

        - **Nœud sentinelle spécial `DEFUNCT`** : `next = DEFUNCT` où DEFUNCT est un nœud statique. Plus explicite que `null`.

        La convention `next == null` est un bon compromis : pas de mémoire supplémentaire et facile à vérifier.

??? question "Question 13 — Problème pratique"
    Vous devez implémenter une méthode `moveToFront(Position<E> p)` qui déplace l'élément à la position `p` au début de la liste positionnelle.

    1. Décrivez l'algorithme en pseudocode
    2. Quelle est sa complexité temporelle ?
    3. Identifiez un cas limite qui nécessite une attention particulière

    ??? success "Réponse"
        **1. Algorithme :**

        ```
        moveToFront(p):
            si p est invalide: lever exception
            si p == first(): retourner (déjà au début)

            element = remove(p)
            addFirst(element)
        ```

        Ou, plus efficacement (sans créer de nouveau nœud) :

        ```
        moveToFront(p):
            si p est invalide: lever exception
            si p == first(): retourner

            node = validate(p)
            // Détacher le nœud
            node.prev.next = node.next
            node.next.prev = node.prev

            // Rattacher au début
            node.prev = header
            node.next = header.next
            header.next.prev = node
            header.next = node
        ```

        **2. Complexité : O(1)**

        Toutes les opérations sont des manipulations de pointeurs en temps constant.

        **3. Cas limites :**

        - **`p` est déjà le premier élément** : Sans vérification, on détacherait et rattacherait inutilement, ce qui fonctionne mais est inefficace.

        - **Liste à un seul élément** : `p` est à la fois premier et dernier. La vérification `p == first()` gère ce cas.

        - **Position invalide** : Doit être détectée par `validate()`.

??? question "Question 14 — Analyse comparative"
    Un collègue affirme : « Les listes positionnelles sont toujours meilleures que les ArrayList car toutes les insertions et suppressions sont O(1). »

    Expliquez pourquoi cette affirmation est incorrecte en donnant au moins deux scénarios où une ArrayList serait préférable.

    ??? success "Réponse"
        L'affirmation est incorrecte pour plusieurs raisons :

        **1. Accès par index**

        - ArrayList : O(1)
        - Liste positionnelle : O(n) (il faut parcourir depuis le début ou la fin)

        Si votre application fait beaucoup d'accès aléatoires par index (ex: `get(i)`), ArrayList est nettement supérieure.

        **2. Localité mémoire (cache)**

        ArrayList stocke les éléments de façon contiguë en mémoire. Lors d'un parcours séquentiel, le CPU peut précharger les éléments suivants (cache prefetching). Les listes chaînées ont des nœuds dispersés en mémoire, causant des *cache misses* fréquents.

        En pratique, pour des parcours fréquents sur des listes de taille modérée, ArrayList peut être 10x plus rapide malgré la même complexité théorique O(n).

        **3. Overhead mémoire**

        Chaque nœud d'une liste chaînée stocke l'élément + 2 pointeurs (prev, next). Pour des éléments petits (int, char), l'overhead peut tripler ou quadrupler la mémoire utilisée.

        **4. Insertions à la fin**

        ArrayList avec `add(e)` à la fin est O(1) amorti (redimensionnement occasionnel). Si la majorité des insertions sont à la fin, ArrayList est aussi efficace qu'une liste chaînée.

        **Conclusion** : Le choix dépend du *pattern d'utilisation*. Les listes positionnelles excellent quand on fait beaucoup d'insertions/suppressions au milieu avec des positions conservées. Les ArrayList excellent pour l'accès indexé et les parcours.

---

## Partie 2 — Implémentation d'une liste positionnelle

### 2.1 Comprendre la structure

Une liste positionnelle doublement chaînée avec sentinelles a la structure suivante :

```
Liste vide :
┌─────────┐     ┌─────────┐
│ HEADER  │────▶│ TRAILER │
│  null   │◀────│  null   │
└─────────┘     └─────────┘

Liste avec éléments [A, B, C] :
┌─────────┐     ┌─────┐     ┌─────┐     ┌─────┐     ┌─────────┐
│ HEADER  │────▶│  A  │────▶│  B  │────▶│  C  │────▶│ TRAILER │
│  null   │◀────│     │◀────│     │◀────│     │◀────│  null   │
└─────────┘     └─────┘     └─────┘     └─────┘     └─────────┘
```

!!! info "Pourquoi des sentinelles ?"
    Les sentinelles (header et trailer) **ne contiennent pas de données utilisateur**. Leur rôle est d'éliminer les cas spéciaux :

    - Sans sentinelles : `addFirst` doit vérifier si la liste est vide et traiter différemment
    - Avec sentinelles : `addFirst` = `addBetween(e, header, header.next)` — toujours valide !

Voici le squelette de la classe :

```java
public class LinkedPositionalList<E> implements PositionalList<E> {

    //---------------- Classe interne Node ----------------
    private static class Node<E> implements Position<E> {
        private E element;
        private Node<E> prev;
        private Node<E> next;

        public Node(E e, Node<E> p, Node<E> n) {
            element = e;
            prev = p;
            next = n;
        }

        public E getElement() throws IllegalStateException {
            if (next == null)  // convention pour nœud défunt
                throw new IllegalStateException("Position no longer valid");
            return element;
        }

        public void setElement(E e) { element = e; }
        public Node<E> getPrev() { return prev; }
        public Node<E> getNext() { return next; }
        public void setPrev(Node<E> p) { prev = p; }
        public void setNext(Node<E> n) { next = n; }
    }

    //---------------- Variables d'instance ----------------
    private Node<E> header;
    private Node<E> trailer;
    private int size = 0;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}
```

---

### 2.2 Initialisation et validation

??? question "Exercice 2.2.1 — Constructeur"
    Implémentez le constructeur qui crée une liste vide. Les sentinelles doivent être liées entre elles.

    **Indice :** Après le constructeur, la structure doit ressembler à :
    ```
    header.next = trailer
    trailer.prev = header
    header.prev = null (ou ignoré)
    trailer.next = null (ou ignoré)
    ```

    ??? success "Solution"
        ```java
        public LinkedPositionalList() {
            header = new Node<>(null, null, null);
            trailer = new Node<>(null, header, null);
            header.setNext(trailer);
        }
        ```

        **Explication pas à pas :**

        1. Créer le header avec tout à `null`
        2. Créer le trailer avec `prev = header`
        3. Lier `header.next` vers trailer

        L'ordre est important : on ne peut pas référencer `trailer` avant de le créer !

??? question "Exercice 2.2.2 — Validation de position"
    La méthode `validate(Position<E> p)` est cruciale pour la sécurité. Elle doit :

    1. Vérifier que `p` est bien un `Node` (pas un autre type de Position)
    2. Vérifier que le nœud n'a pas été supprimé (convention : `next == null`)
    3. Retourner le nœud casté

    Implémentez cette méthode. Quelles exceptions lever dans chaque cas ?

    ??? success "Solution"
        ```java
        private Node<E> validate(Position<E> p) throws IllegalArgumentException {
            if (!(p instanceof Node))
                throw new IllegalArgumentException("Invalid position type");
            Node<E> node = (Node<E>) p;
            if (node.getNext() == null)  // convention pour nœud défunt
                throw new IllegalArgumentException("Position is no longer valid");
            return node;
        }
        ```

        **Question bonus :** Pourquoi ne vérifie-t-on pas `node.getPrev() == null` ?

        Parce que seul le header a `prev == null`, et le header n'est jamais exposé à l'utilisateur. Tous les nœuds valides ont `prev != null`. On pourrait ajouter cette vérification, mais `next == null` suffit car c'est notre convention pour les nœuds défunts.

??? question "Exercice 2.2.3 — Méthode position()"
    Implémentez une méthode utilitaire privée `position(Node<E> node)` qui retourne le nœud comme Position, ou `null` si c'est une sentinelle.

    Cette méthode sera utile pour `first()`, `last()`, `before()`, `after()`.

    ??? success "Solution"
        ```java
        private Position<E> position(Node<E> node) {
            if (node == header || node == trailer)
                return null;  // ne pas exposer les sentinelles
            return node;
        }
        ```

---

### 2.3 Opérations d'accès

??? question "Exercice 2.3.1 — first() et last()"
    Implémentez `first()` et `last()` en utilisant la méthode `position()`.

    ```
    Liste [A, B, C] :
    header ──▶ A ──▶ B ──▶ C ──▶ trailer
              ↑                    ↑
           first()              last()
    ```

    ??? success "Solution"
        ```java
        public Position<E> first() {
            return position(header.getNext());
        }

        public Position<E> last() {
            return position(trailer.getPrev());
        }
        ```

        **Remarque :** Grâce à `position()`, on n'a pas besoin de vérifier `isEmpty()` explicitement. Si la liste est vide, `header.next == trailer`, donc `position(trailer)` retourne `null`.

??? question "Exercice 2.3.2 — before() et after()"
    Implémentez les méthodes de navigation. Attention à valider la position d'entrée !

    ```
    Pour la liste [A, B, C] avec position p sur B :

    before(p) retourne la position de A
    after(p) retourne la position de C
    before(first()) retourne null
    after(last()) retourne null
    ```

    ??? success "Solution"
        ```java
        public Position<E> before(Position<E> p) throws IllegalArgumentException {
            Node<E> node = validate(p);
            return position(node.getPrev());
        }

        public Position<E> after(Position<E> p) throws IllegalArgumentException {
            Node<E> node = validate(p);
            return position(node.getNext());
        }
        ```

---

### 2.4 Insertion

L'opération fondamentale est `addBetween`. Toutes les autres insertions l'utilisent.

??? question "Exercice 2.4.1 — addBetween (méthode clé)"
    Implémentez `addBetween(E e, Node<E> pred, Node<E> succ)` qui insère un nouvel élément entre deux nœuds existants.

    **Avant :**
    ```
    pred ──────────▶ succ
         ◀──────────
    ```

    **Après :**
    ```
    pred ────▶ NEW ────▶ succ
         ◀────     ◀────
    ```

    **Indices :**

    1. Créer le nouveau nœud avec les bons liens
    2. Mettre à jour `pred.next`
    3. Mettre à jour `succ.prev`
    4. Incrémenter `size`

    ??? success "Solution"
        ```java
        private Position<E> addBetween(E e, Node<E> pred, Node<E> succ) {
            Node<E> newest = new Node<>(e, pred, succ);  // liens du nouveau nœud
            pred.setNext(newest);                        // pred ──▶ newest
            succ.setPrev(newest);                        // newest ◀── succ
            size++;
            return newest;
        }
        ```

        **L'ordre des opérations est-il important ?**

        Oui et non. Dans ce cas, comme on crée d'abord le nouveau nœud avec ses liens corrects, l'ordre de mise à jour de `pred` et `succ` n'importe pas. Mais si on faisait les choses différemment, on pourrait perdre des références.

??? question "Exercice 2.4.2 — Les quatre méthodes d'insertion"
    En utilisant `addBetween`, implémentez :

    - `addFirst(E e)` — ajoute au début
    - `addLast(E e)` — ajoute à la fin
    - `addBefore(Position<E> p, E e)` — ajoute avant p
    - `addAfter(Position<E> p, E e)` — ajoute après p

    ??? success "Solution"
        ```java
        public Position<E> addFirst(E e) {
            return addBetween(e, header, header.getNext());
        }

        public Position<E> addLast(E e) {
            return addBetween(e, trailer.getPrev(), trailer);
        }

        public Position<E> addBefore(Position<E> p, E e)
                throws IllegalArgumentException {
            Node<E> node = validate(p);
            return addBetween(e, node.getPrev(), node);
        }

        public Position<E> addAfter(Position<E> p, E e)
                throws IllegalArgumentException {
            Node<E> node = validate(p);
            return addBetween(e, node, node.getNext());
        }
        ```

        **Observation :** Les quatre méthodes tiennent en une seule ligne grâce à `addBetween`. C'est la puissance de la factorisation du code !

---

### 2.5 Suppression

??? question "Exercice 2.5.1 — remove()"
    Implémentez `remove(Position<E> p)` qui supprime le nœud et retourne son élément.

    **Avant (suppression de B) :**
    ```
    A ────▶ B ────▶ C
      ◀────   ◀────
    ```

    **Après :**
    ```
    A ────────────▶ C
      ◀────────────
    ```

    **Important :** Après suppression, le nœud doit être marqué comme défunt pour éviter sa réutilisation accidentelle.

    ??? success "Solution"
        ```java
        public E remove(Position<E> p) throws IllegalArgumentException {
            Node<E> node = validate(p);
            Node<E> predecessor = node.getPrev();
            Node<E> successor = node.getNext();

            // Bypass le nœud supprimé
            predecessor.setNext(successor);
            successor.setPrev(predecessor);
            size--;

            E answer = node.getElement();

            // Invalider le nœud (aide aussi le garbage collector)
            node.setElement(null);
            node.setNext(null);      // Convention: nœud défunt
            node.setPrev(null);

            return answer;
        }
        ```

---

### 2.6 set()

??? question "Exercice 2.6.1 — Méthode set()"
    Implémentez `set(Position<E> p, E e)` qui remplace l'élément à la position `p` par `e` et retourne l'ancien élément.

    **Note :** Cette opération ne modifie pas la structure de la liste, seulement le contenu d'un nœud.

    ??? success "Solution"
        ```java
        public E set(Position<E> p, E e) throws IllegalArgumentException {
            Node<E> node = validate(p);
            E old = node.getElement();
            node.setElement(e);
            return old;
        }
        ```

---

---

## Partie 3 — Implémentation d'une liste circulaire

### 3.1 Comprendre la structure circulaire

Dans une liste circulaire, le dernier élément pointe vers le premier, formant un cycle.

```
Liste circulaire [A, B, C] avec pointeur tail vers C :

        ┌─────────────────────────────────┐
        │                                 │
        ▼                                 │
      ┌───┐     ┌───┐     ┌───┐          │
      │ A │────▶│ B │────▶│ C │──────────┘
      └───┘     └───┘     └───┘
        ↑                   ↑
      first              tail (dernier)
      = tail.next
```

!!! info "Pourquoi pointer vers le dernier ?"
    Avec un pointeur vers le **dernier** élément :

    - Accès au dernier : `tail` → O(1)
    - Accès au premier : `tail.next` → O(1)

    Si on pointait vers le premier, accéder au dernier nécessiterait un parcours complet O(n).

Voici le squelette :

```java
public class CircularlyLinkedList<E> {

    private static class Node<E> {
        private E element;
        private Node<E> next;

        public Node(E e, Node<E> n) {
            element = e;
            next = n;
        }

        public E getElement() { return element; }
        public Node<E> getNext() { return next; }
        public void setNext(Node<E> n) { next = n; }
    }

    private Node<E> tail = null;
    private int size = 0;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}
```

---

### 3.2 Opérations d'accès

??? question "Exercice 3.2.1 — first() et last()"
    Implémentez les accesseurs. Rappelez-vous que `tail` pointe vers le **dernier** élément.

    ??? success "Solution"
        ```java
        public E first() {
            if (isEmpty()) return null;
            return tail.getNext().getElement();
        }

        public E last() {
            if (isEmpty()) return null;
            return tail.getElement();
        }
        ```

??? question "Exercice 3.2.2 — rotate()"
    La rotation est l'opération caractéristique des listes circulaires. Elle « fait tourner » la liste d'une position : le premier élément devient le dernier.

    ```
    Avant rotate() : tail ──▶ C,  first = A
        A → B → C → (retour à A)

    Après rotate() : tail ──▶ A,  first = B
        B → C → A → (retour à B)
    ```

    **Indice :** C'est une opération O(1) très simple !

    ??? success "Solution"
        ```java
        public void rotate() {
            if (tail != null)
                tail = tail.getNext();
        }
        ```

        C'est tout ! En déplaçant `tail` vers `tail.next`, l'ancien premier devient le nouveau dernier. La circularité fait tout le travail.

---

### 3.3 Insertion

??? question "Exercice 3.3.1 — addFirst()"
    Insérez un élément au début de la liste. Attention au cas de la liste vide !

    **Cas liste vide :** Le nouveau nœud doit pointer vers lui-même.
    ```
    Avant : tail = null
    Après addFirst(A) :
        ┌───────┐
        │       │
        ▼       │
      ┌───┐     │
      │ A │─────┘
      └───┘
        ↑
      tail
    ```

    **Cas liste non vide :** Insérer entre `tail` et `tail.next` (l'ancien premier).
    ```
    Avant : tail ──▶ C, liste = [A, B, C]
    Après addFirst(X) : tail ──▶ C, liste = [X, A, B, C]
    ```

    ??? success "Solution"
        ```java
        public void addFirst(E e) {
            if (isEmpty()) {
                tail = new Node<>(e, null);
                tail.setNext(tail);  // pointe vers lui-même
            } else {
                Node<E> newest = new Node<>(e, tail.getNext());
                tail.setNext(newest);
            }
            size++;
        }
        ```

??? question "Exercice 3.3.2 — addLast()"
    Insérez un élément à la fin. **Astuce :** Réutilisez `addFirst` !

    Réfléchissez : si on ajoute au début puis qu'on fait une rotation, où se trouve le nouvel élément ?

    ??? success "Solution"
        ```java
        public void addLast(E e) {
            addFirst(e);
            tail = tail.getNext();  // le nouveau devient le dernier
        }
        ```

        **Explication :**

        1. `addFirst(e)` insère le nouvel élément au début
        2. `tail = tail.getNext()` déplace `tail` vers ce nouvel élément
        3. Résultat : le nouvel élément est maintenant le dernier !

---

### 3.4 Suppression

??? question "Exercice 3.4.1 — removeFirst()"
    Supprimez et retournez le premier élément. Attention au cas où il ne reste qu'un seul élément !

    ??? success "Solution"
        ```java
        public E removeFirst() {
            if (isEmpty()) return null;
            Node<E> head = tail.getNext();
            if (head == tail)        // un seul élément
                tail = null;
            else
                tail.setNext(head.getNext());
            size--;
            return head.getElement();
        }
        ```

??? question "Exercice 3.4.2 — removeLast() (plus difficile)"
    Supprimez et retournez le **dernier** élément.

    **Attention :** Cette opération est O(n) ! Pourquoi ?

    Pour supprimer le dernier élément, il faut mettre à jour le pointeur `next` de l'**avant-dernier** nœud. Mais dans une liste simplement chaînée, on ne peut pas remonter en arrière — il faut donc parcourir toute la liste pour trouver l'avant-dernier.

    ??? success "Solution"
        ```java
        public E removeLast() {
            if (isEmpty()) return null;

            if (size == 1) {
                E element = tail.getElement();
                tail = null;
                size--;
                return element;
            }

            // Trouver l'avant-dernier nœud (O(n))
            Node<E> current = tail.getNext();  // commence au premier
            while (current.getNext() != tail) {
                current = current.getNext();
            }
            // current est maintenant l'avant-dernier

            E element = tail.getElement();
            current.setNext(tail.getNext());  // bypass tail
            tail = current;                    // nouveau dernier
            size--;
            return element;
        }
        ```

        **Leçon :** Si vous avez besoin de suppressions fréquentes à la fin, utilisez une liste **doublement** chaînée !

---

### 3.5 Application : Le jeu de la patate chaude

Le jeu de la **patate chaude** (Hot Potato) est un classique pour illustrer les listes circulaires :

- Des joueurs sont en cercle
- Une « patate » passe de main en main
- Après k passages, le joueur qui tient la patate est éliminé
- Le dernier joueur restant gagne

??? question "Exercice 3.5.1 — Implémentation du jeu"
    Implémentez la méthode `playHotPotato(CircularlyLinkedList<String> players, int k)` qui simule le jeu et retourne le nom du gagnant.

    **Exemple :** Joueurs = [Alice, Bob, Carol, David, Eve], k = 3

    - Tour 1 : A→B→C, Carol éliminée → [Alice, Bob, David, Eve]
    - Tour 2 : D→E→A, Alice éliminée → [Bob, David, Eve]
    - Tour 3 : B→D→E, Eve éliminée → [Bob, David]
    - Tour 4 : B→D→B, Bob éliminé → [David]
    - Gagnant : David

    ??? success "Solution"
        ```java
        public static String playHotPotato(CircularlyLinkedList<String> players, int k) {
            if (players.isEmpty()) return null;

            while (players.size() > 1) {
                // Passer la patate k fois
                for (int i = 0; i < k; i++) {
                    players.rotate();
                }
                // Éliminer le joueur courant (premier de la liste)
                String eliminated = players.removeFirst();
                System.out.println(eliminated + " est éliminé(e) !");
            }

            return players.first();  // le gagnant
        }
        ```

        **Variante (Problème de Josèphe) :** Historiquement, ce problème modélise un groupe de soldats en cercle où chaque k-ième personne est éliminée. Avec n soldats et k passages, quelle position choisir pour survivre ? C'est un problème mathématique célèbre !

---

### 3.6 Application : Buffer circulaire

Un **buffer circulaire** est utilisé pour les flux de données (streaming, logs, etc.) où on veut garder seulement les N derniers éléments.

??? question "Exercice 3.6.1 — CircularBuffer"
    Implémentez un buffer circulaire de taille fixe. Quand le buffer est plein, l'ajout d'un nouvel élément écrase le plus ancien.

    ```java
    public class CircularBuffer<E> {
        private E[] buffer;
        private int head = 0;  // index du plus ancien
        private int tail = 0;  // index du prochain ajout
        private int count = 0; // nombre d'éléments
        private int capacity;

        // À implémenter :
        // void add(E element)
        // E remove()
        // E peek()
    }
    ```

    ??? success "Solution"
        ```java
        @SuppressWarnings("unchecked")
        public CircularBuffer(int capacity) {
            this.capacity = capacity;
            this.buffer = (E[]) new Object[capacity];
        }

        public void add(E element) {
            buffer[tail] = element;
            tail = (tail + 1) % capacity;  // wrap around

            if (count == capacity) {
                // Buffer plein : on écrase le plus ancien
                head = (head + 1) % capacity;
            } else {
                count++;
            }
        }

        public E remove() {
            if (count == 0) return null;
            E element = buffer[head];
            buffer[head] = null;  // aide GC
            head = (head + 1) % capacity;
            count--;
            return element;
        }

        public E peek() {
            if (count == 0) return null;
            return buffer[head];
        }
        ```

        **Note :** Cette implémentation utilise un **tableau** circulaire, pas une liste chaînée. C'est souvent plus efficace pour les buffers de taille fixe car il n'y a pas d'allocation dynamique de nœuds.

---

## Exercices supplémentaires

??? question "Défi 1 — Inverser une liste positionnelle"
    Écrivez une méthode qui inverse une liste positionnelle **en place** (sans créer de nouvelle liste ni de nouveaux nœuds).

    ```java
    public static <E> void reverse(PositionalList<E> list)
    ```

    **Exemple :** `[A, B, C, D]` → `[D, C, B, A]`

    ??? success "Solution"
        **Approche 1 : Utiliser l'API publique**
        ```java
        public static <E> void reverse(PositionalList<E> list) {
            if (list.size() <= 1) return;

            Position<E> front = list.first();
            while (list.after(front) != null) {
                E element = list.remove(list.last());
                list.addBefore(front, element);
            }
        }
        ```

        **Complexité :** O(n) — chaque élément est déplacé une fois.

        **Approche 2 : Manipulation directe des pointeurs** (si on a accès aux nœuds)
        ```java
        // À l'intérieur de LinkedPositionalList
        public void reverse() {
            if (size <= 1) return;

            Node<E> current = header;
            do {
                // Échanger prev et next pour chaque nœud
                Node<E> temp = current.prev;
                current.prev = current.next;
                current.next = temp;
                current = current.prev;  // avancer (qui est l'ancien next)
            } while (current != header);

            // Échanger header et trailer
            Node<E> temp = header;
            header = trailer;
            trailer = temp;
        }
        ```

??? question "Défi 2 — Fusionner deux listes triées"
    Fusionnez deux listes positionnelles **triées** en une seule liste triée. Les listes d'entrée peuvent être modifiées.

    ```java
    public static <E extends Comparable<E>> PositionalList<E> merge(
            PositionalList<E> list1, PositionalList<E> list2)
    ```

    ??? success "Solution"
        ```java
        public static <E extends Comparable<E>> PositionalList<E> merge(
                PositionalList<E> list1, PositionalList<E> list2) {

            PositionalList<E> result = new LinkedPositionalList<>();
            Position<E> p1 = list1.first();
            Position<E> p2 = list2.first();

            while (p1 != null && p2 != null) {
                if (p1.getElement().compareTo(p2.getElement()) <= 0) {
                    result.addLast(p1.getElement());
                    p1 = list1.after(p1);
                } else {
                    result.addLast(p2.getElement());
                    p2 = list2.after(p2);
                }
            }

            // Ajouter les éléments restants
            while (p1 != null) {
                result.addLast(p1.getElement());
                p1 = list1.after(p1);
            }
            while (p2 != null) {
                result.addLast(p2.getElement());
                p2 = list2.after(p2);
            }

            return result;
        }
        ```

        **Complexité :** O(n + m) où n et m sont les tailles des deux listes.

??? question "Défi 3 — Détecter un cycle"
    Écrivez une méthode qui détecte si une liste simplement chaînée (non circulaire par design) contient accidentellement un cycle.

    **Indice :** Algorithme de Floyd (tortue et lièvre).

    ```java
    public static <E> boolean hasCycle(Node<E> head)
    ```

    ??? success "Solution"
        ```java
        public static <E> boolean hasCycle(Node<E> head) {
            if (head == null) return false;

            Node<E> slow = head;  // tortue : avance de 1
            Node<E> fast = head;  // lièvre : avance de 2

            while (fast != null && fast.getNext() != null) {
                slow = slow.getNext();
                fast = fast.getNext().getNext();

                if (slow == fast) {
                    return true;  // cycle détecté !
                }
            }

            return false;  // fast a atteint la fin
        }
        ```

        **Pourquoi ça marche ?** Si un cycle existe, le lièvre (rapide) finira par rattraper la tortue (lente) car ils tournent en boucle. Si aucun cycle n'existe, le lièvre atteindra `null`.

        **Complexité :** O(n) en temps, O(1) en espace.

??? question "Défi 4 — indexOf avec Position"
    Implémentez une méthode qui retourne la **position** d'un élément (pas l'index), ou `null` si non trouvé.

    ```java
    public Position<E> positionOf(E element)
    ```

    ??? success "Solution"
        ```java
        public Position<E> positionOf(E element) {
            Position<E> current = first();
            while (current != null) {
                if (Objects.equals(current.getElement(), element)) {
                    return current;
                }
                current = after(current);
            }
            return null;
        }
        ```

        **Note :** On utilise `Objects.equals()` pour gérer le cas où `element` est `null`.

---

## Résumé

| Structure | Accès par index | Insertion début | Insertion fin | Insertion milieu |
|-----------|-----------------|-----------------|---------------|------------------|
| ArrayList | O(1) | O(n) | O(1) amorti | O(n) |
| Liste simplement chaînée | O(n) | O(1) | O(n)* | O(n)** |
| Liste doublement chaînée | O(n) | O(1) | O(1) | O(1)*** |
| Liste positionnelle | O(n) | O(1) | O(1) | O(1)*** |
| Liste circulaire | O(n) | O(1) | O(1) | O(n)** |

*\* O(1) si on maintient une référence au dernier nœud*
*\*\* O(1) si on possède une référence au nœud précédent*
*\*\*\* Si on possède déjà la position*

!!! tip "Points clés à retenir"
    1. **Choisissez la structure selon les opérations fréquentes** : Si les insertions/suppressions au milieu sont fréquentes, privilégiez les listes positionnelles.
    2. **Les sentinelles simplifient le code** : Elles éliminent les cas spéciaux et réduisent les erreurs.
    3. **Validez toujours les positions** : Une position invalidée peut causer des comportements imprévisibles.
    4. **La circularité a ses usages** : Round-robin, buffers, et toute application nécessitant un parcours cyclique.
