# Structures de données - Série de problèmes ludiques

Une sélection de problèmes classiques qui reviennent souvent en entrevue, en compétition ou en pratique. L'objectif n'est pas seulement de connaître la réponse, mais de reconnaître rapidement **le bon pattern** et de comprendre **pourquoi** il fonctionne.

!!! abstract "Comment travailler cette série"

    - Lisez d'abord la question et essayez d'identifier la structure dominante avant d'ouvrir la réponse
    - Cherchez le bon invariant ou la bonne idée avant de penser au code
    - Comparez les problèmes entre eux pour repérer les familles de solutions récurrentes

---

## Série de défis

??? question "Question 1 — Two Sum (Table de hachage)"
    Étant donné un tableau `nums` et une valeur cible `target`, trouver deux indices `i` et `j` tels que `nums[i] + nums[j] = target`.

    L'objectif est de faire mieux qu'une solution naïve en `O(n^2)` qui teste toutes les paires.

    ??? success "Réponse"
        **Idée clé.** On parcourt le tableau une seule fois en mémorisant, dans une table de hachage, les valeurs déjà rencontrées et leur indice.

        **Pourquoi cette approche marche.** Si la valeur courante est `x`, alors il faut chercher `target - x`. Si ce complément a déjà été vu, on a immédiatement trouvé la bonne paire. Sinon, on stocke `x` pour les éléments qui viendront après.

        **Étapes.**

        1. Créer une `HashMap` qui associe `valeur -> indice`
        2. Parcourir le tableau de gauche à droite
        3. Pour chaque `x = nums[i]`, calculer `target - x`
        4. Si ce complément est déjà dans la map, retourner les deux indices
        5. Sinon, stocker `x` avec son indice courant

        **Pourquoi on ne stocke pas tout au début.** Si on remplissait d'abord la map complète, on risquerait d'utiliser deux fois le même élément. Le parcours progressif évite ce piège.

        **Complexité.**

        - Temps : `O(n)`
        - Espace : `O(n)`

        **Pattern.** Recherche rapide par table de hachage.

---

??? question "Question 2 — Same Tree (DFS synchronisé)"
    On vous donne deux arbres binaires. Déterminez s'ils sont exactement identiques, c'est-à-dire qu'ils ont la même structure et les mêmes valeurs à chaque position.

    ??? success "Réponse"
        **Idée clé.** Il faut comparer les deux arbres en parallèle, noeud par noeud.

        **Pourquoi le DFS est naturel ici.** À chaque paire de noeuds, on doit répondre à la même question pour les sous-arbres gauche et droit. La structure récursive du problème correspond donc très bien à un DFS récursif.

        **Cas de base.**

        - Si les deux noeuds sont `null`, ils sont égaux à cet endroit
        - Si un seul est `null`, les structures diffèrent
        - Si les valeurs sont différentes, les arbres ne sont pas identiques

        **Étape récursive.** Si les deux noeuds existent et ont la même valeur, il faut vérifier :

        - que les sous-arbres gauches sont identiques
        - et que les sous-arbres droits sont identiques

        Les deux conditions doivent être vraies.

        **Complexité.**

        - Temps : `O(n)` où `n` est le nombre de noeuds visités
        - Espace : `O(h)` pour la pile récursive, où `h` est la hauteur de l'arbre

        **Pattern.** DFS synchronisé sur deux structures.

---

??? question "Question 3 — Lowest Common Ancestor dans un arbre binaire"
    Trouver le plus bas ancêtre commun de deux noeuds `p` et `q` dans un arbre binaire quelconque.

    ??? success "Réponse"
        **Idée clé.** On explore les deux sous-arbres et on laisse l'information remonter vers le haut.

        **Observation importante.** Pour un noeud donné :

        - si `p` et `q` sont tous les deux dans son sous-arbre gauche, la réponse est à gauche
        - s'ils sont tous les deux à droite, la réponse est à droite
        - si l'un est à gauche et l'autre à droite, alors ce noeud est leur plus bas ancêtre commun

        **Stratégie récursive.**

        1. Si le noeud courant est `null`, retourner `null`
        2. Si le noeud courant est `p` ou `q`, le retourner immédiatement
        3. Explorer récursivement le sous-arbre gauche et le sous-arbre droit
        4. Si les deux appels retournent une valeur non nulle, le noeud courant est le LCA
        5. Sinon, retourner le seul côté non nul

        **Pourquoi cela fonctionne.** Chaque appel retourne soit :

        - `null` si ni `p` ni `q` n'est trouvé
        - `p` ou `q` si l'un des deux est trouvé
        - le LCA si la réponse a déjà été déterminée plus bas

        **Complexité.**

        - Temps : `O(n)`
        - Espace : `O(h)`

        **Pattern.** DFS post-ordre avec remontée d'information.

---

??? question "Question 4 — Lowest Common Ancestor dans un BST"
    Même problème que précédemment, mais cette fois l'arbre est un **BST**.

    ??? success "Réponse"
        **Idée clé.** Dans un BST, l'ordre des clés permet d'éviter d'explorer les deux côtés.

        **Propriété utilisée.**

        - toutes les clés du sous-arbre gauche sont plus petites que la racine
        - toutes les clés du sous-arbre droit sont plus grandes

        **Conséquence.**

        - Si `p` et `q` sont tous les deux plus petits que la racine, alors le LCA est forcément à gauche
        - S'ils sont tous les deux plus grands, alors le LCA est forcément à droite
        - Sinon, la racine actuelle sépare `p` et `q`, donc c'est le LCA

        **Étapes.**

        1. Partir de la racine
        2. Comparer `p.val` et `q.val` avec `root.val`
        3. Descendre à gauche, à droite, ou s'arrêter selon le cas

        **Pourquoi c'est meilleur que dans un arbre binaire général.** Ici, on n'a pas besoin de lancer deux appels récursifs à chaque noeud. La propriété d'ordre joue le rôle d'un guidage de recherche.

        **Complexité.**

        - Temps : `O(h)`
        - Espace : `O(1)` en version itérative, `O(h)` en version récursive

        **Pattern.** Recherche guidée par invariant BST.

---

??? question "Question 5 — Number of Islands (DFS/BFS sur grille)"
    Dans une grille composée de cases `terre` et `eau`, compter le nombre d'îles. Deux cases de terre appartiennent à la même île si elles sont connectées horizontalement ou verticalement.

    ??? success "Réponse"
        **Idée clé.** Une île correspond à une composante connexe dans une grille.

        **Approche.** On parcourt toutes les cases. Dès qu'on trouve une case de terre non visitée, on sait qu'on a découvert une nouvelle île. Il faut alors explorer complètement cette île avec un DFS ou un BFS pour éviter de la recompter.

        **Étapes.**

        1. Parcourir la grille case par case
        2. Si une case contient de la terre non visitée, incrémenter le compteur
        3. Lancer un DFS/BFS depuis cette case
        4. Marquer toutes les cases connectées comme visitées

        **Pourquoi le compteur augmente exactement une fois par île.** La première case non visitée rencontrée dans une île déclenche un parcours qui absorbe toute l'île. Les cases de cette même île ne pourront donc plus déclencher un nouveau compteur.

        **Pièges classiques.**

        - oublier de marquer les cases visitées
        - compter les diagonales alors qu'elles ne sont pas autorisées
        - sortir de la grille sans vérifier les bornes

        **Complexité.**

        - Temps : `O(mn)`
        - Espace : `O(mn)` dans le pire cas selon la structure de visite ou la récursion

        **Pattern.** Parcours de composantes connexes dans une grille.

---

??? question "Question 6 — Rotting Oranges (BFS multi-source)"
    Dans une grille, certaines oranges sont fraîches et d'autres sont déjà pourries. Chaque minute, une orange pourrie contamine ses voisines adjacentes. Calculer le temps minimum nécessaire pour que toutes les oranges deviennent pourries.

    ??? success "Réponse"
        **Idée clé.** Toutes les oranges pourries initiales agissent comme des sources qui propagent la contamination en parallèle.

        **Pourquoi un BFS multi-source.** En BFS, chaque niveau correspond naturellement à une distance minimale. Ici, la distance correspond au nombre de minutes écoulées depuis une orange pourrie initiale.

        **Étapes.**

        1. Ajouter toutes les oranges déjà pourries à la file au départ
        2. Compter le nombre d'oranges fraîches
        3. Lancer un BFS niveau par niveau
        4. À chaque expansion, contaminer les voisins frais
        5. Décrémenter le nombre d'oranges fraîches restantes

        **Interprétation du temps.** Un niveau de BFS = une minute. La première fois qu'une orange fraîche est atteinte, c'est forcément au temps minimal.

        **Cas d'échec.** Si, à la fin, il reste des oranges fraîches, cela signifie qu'elles étaient inaccessibles. On retourne alors `-1`.

        **Complexité.**

        - Temps : `O(mn)`
        - Espace : `O(mn)`

        **Pattern.** BFS multi-source sur grille.

---

??? question "Question 7 — Walls and Gates (BFS multi-source)"
    On vous donne une grille contenant des murs, des portes et des pièces vides. Remplir chaque pièce vide avec sa distance jusqu'à la porte la plus proche.

    ??? success "Réponse"
        **Idée clé.** Au lieu de partir de chaque pièce vide, on part de toutes les portes en même temps.

        **Pourquoi ce sens est le bon.** Si on lançait un BFS depuis chaque pièce vide, on referait beaucoup de travail. En partant de toutes les portes simultanément, chaque pièce reçoit directement sa distance minimale lors de sa première visite.

        **Étapes.**

        1. Ajouter toutes les portes à la file
        2. Lancer un BFS simultané depuis ces portes
        3. Lorsqu'on atteint une pièce vide, lui attribuer la distance du parent + 1
        4. Continuer jusqu'à épuisement de la file

        **Pourquoi la première distance trouvée est la bonne.** Le BFS explore par couches de distance croissante. Donc la première porte qui atteint une pièce la rejoint forcément au plus court chemin.

        **Complexité.**

        - Temps : `O(mn)`
        - Espace : `O(mn)`

        **Pattern.** Plus court chemin dans une grille non pondérée via BFS multi-source.

---

??? question "Question 8 — Design Snake Game (Simulation)"
    Concevoir la logique d'un jeu Snake : le serpent avance, mange de la nourriture, grandit et doit éviter les collisions avec les murs ou avec lui-même.

    ??? success "Réponse"
        **Idée clé.** Il faut maintenir à la fois l'ordre du corps du serpent et la possibilité de tester très vite si une case est déjà occupée.

        **Bonne combinaison de structures.**

        - une `Queue` ou `Deque` pour représenter le corps dans l'ordre tête -> queue
        - un `Set` pour savoir en `O(1)` si une case est occupée

        **Étapes à chaque mouvement.**

        1. Calculer la nouvelle position de la tête
        2. Vérifier si cette position sort de la grille
        3. Retirer temporairement la queue si le serpent ne mange pas
        4. Vérifier si la nouvelle tête entre en collision avec le corps
        5. Ajouter la nouvelle tête
        6. Si la case contient de la nourriture, ne pas retirer la queue et faire grandir le serpent

        **Détail subtil.** Il faut généralement retirer la queue avant de tester certaines collisions, car la tête a le droit d'entrer dans la case que la queue est en train de quitter pendant ce tour.

        **Complexité par mouvement.**

        - Temps : `O(1)`
        - Espace : `O(longueur du serpent)`

        **Pattern.** Simulation avec combinaison d'une structure ordonnée et d'un ensemble de présence.

---

??? question "Question 9 — Majority Element (Boyer-Moore)"
    Trouver l'élément qui apparaît strictement plus de `n/2` fois dans un tableau.

    ??? success "Réponse"
        **Idée clé.** Si un élément est majoritaire, il survit à toute opération d'annulation contre des éléments différents.

        **Intuition.** On peut imaginer qu'à chaque fois qu'on rencontre deux valeurs différentes, elles s'éliminent mutuellement. Comme l'élément majoritaire apparaît plus souvent que tous les autres réunis, il ne peut pas être complètement éliminé.

        **Algorithme.**

        1. Garder un `candidate` et un `count`
        2. Si `count == 0`, adopter la valeur courante comme nouveau candidat
        3. Si la valeur courante égale le candidat, incrémenter `count`
        4. Sinon, décrémenter `count`

        **Pourquoi cela fonctionne.** Les décréments représentent des annulations entre le candidat courant et des valeurs différentes. Si un vrai majoritaire existe, il finit nécessairement comme candidat final.

        **Important.** Si l'énoncé ne garantit pas qu'un majoritaire existe, il faut faire un second passage pour vérifier la fréquence réelle du candidat.

        **Complexité.**

        - Temps : `O(n)`
        - Espace : `O(1)`

        **Pattern.** Compression d'état et élimination par paires.

---

??? question "Question 10 — Find Duplicate Number (Cycle Detection de Floyd)"
    Trouver une valeur dupliquée dans un tableau contenant des entiers entre `1` et `n`, sans modifier le tableau et avec très peu de mémoire supplémentaire.

    ??? success "Réponse"
        **Idée clé.** On peut réinterpréter le tableau comme une structure de pointeurs, exactement comme une liste chaînée.

        **Comment faire cette réinterprétation.** Depuis l'indice `i`, on "pointe" vers l'indice `nums[i]`. Comme une valeur est dupliquée, deux chemins différents finissent par converger, ce qui crée un cycle logique.

        **Étape 1 : détecter le cycle.**

        - utiliser un pointeur lent qui avance d'un pas
        - utiliser un pointeur rapide qui avance de deux pas
        - ils finissent par se rencontrer dans le cycle

        **Étape 2 : trouver l'entrée du cycle.**

        - remettre un des pointeurs au départ
        - faire avancer les deux d'un pas à la fois
        - leur point de rencontre est la valeur dupliquée

        **Pourquoi cela marche.** C'est exactement le même raisonnement que pour la détection de cycle dans une liste chaînée. L'entrée du cycle correspond ici à la valeur dupliquée.

        **Complexité.**

        - Temps : `O(n)`
        - Espace : `O(1)`

        **Pattern.** Réinterprétation structurelle + détection de cycle de Floyd.

---

## Résumé des patterns

- Table de hachage -> `Two Sum`
- DFS synchronisé -> `Same Tree`
- DFS post-ordre -> `Lowest Common Ancestor`
- Logique BST -> `Lowest Common Ancestor in BST`
- DFS/BFS sur grille -> `Number of Islands`
- BFS multi-source -> `Rotting Oranges`, `Walls and Gates`
- Simulation + structures combinées -> `Snake Game`
- Élimination par paires -> `Majority Element`
- Détection de cycle -> `Find Duplicate Number`

---

## Modèles mentaux à retenir

- Si l'énoncé demande une recherche très rapide parmi des éléments déjà vus -> penser `HashMap`
- Si l'on explore une structure récursive ou un graphe -> penser DFS/BFS
- Si toutes les sources doivent propager quelque chose en même temps -> penser BFS multi-source
- Si le problème dépend fortement de l'ordre des événements -> penser simulation
- Si un élément "survit" aux autres par domination -> penser Boyer-Moore
- Si un tableau peut être vu comme des pointeurs -> penser Floyd
