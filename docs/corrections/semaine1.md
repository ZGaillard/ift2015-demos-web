# Corrigé - Semaine 1

Document interne (ne pas publier pour l'instant).

## Exercice 1 - Choisir la structure
Réponses possibles (plusieurs variantes acceptées selon les hypothèses).

| Scénario | Opérations dominantes | Structure proposée | Invariant clé | Coût accepté |
| --- | --- | --- | --- | --- |
| Beaucoup d'insertions, très peu de recherches | insertion | tableau non trié | aucun | recherche O(n) |
| Tests d'appartenance très fréquents, l'ordre n'importe pas | recherche | table de hachage | clé → compartiment | collisions, redimensionnement |
| Accès rapide au minimum (ordonnancement) | min | tas min | parent ≤ enfants | insertion/suppression O(log n) |
| Affichage final trié, insertions pendant la collecte | insertion puis tri | tableau non trié + tri final | aucun (jusqu'au tri) | tri final O(n log n) |
| Recherche fréquente + parcours en ordre croissant | recherche + parcours | arbre de recherche équilibré | gauche < racine < droite | équilibrage/rotations |
| Suppressions et insertions fréquentes tout en gardant l'ordre | updates + ordre | arbre de recherche équilibré | ordre BST | maintenance O(log n) |
| Accès rapide au maximum, insertions modérées | max | tas max | parent ≥ enfants | insertion/suppression O(log n) |
| Beaucoup de lectures, très peu de mises à jour | recherche | tableau trié | ordre total | insertion O(n) |

## Exercice 2 - Invariants et accélération

| Structure | Invariant | Opération accélérée | Coût principal |
| --- | --- | --- | --- |
| Tableau trié | ordre total | recherche dichotomique, min/max | insertion/suppression O(n) |
| Tas (min-heap) | parent ≤ enfants | min en O(1) | réajustement O(log n) |
| Arbre de recherche | gauche < racine < droite | recherche moyenne O(log n) | équilibrage, rotations |
| Table de hachage | clé → compartiment | recherche/insertion O(1) moyenne | collisions, redimensionnement |

## Exercice 3 - Tri par structure
Liste : 7 2 9 4 1 6 3

1) Tri par sélection (deux passes) :
- Après la passe 1 : 1 2 9 4 7 6 3
- Après la passe 2 : 1 2 9 4 7 6 3

2) Tri par insertion (après le 4e élément) :
- 2 4 7 9 1 6 3

3) Heapsort (tas min, représentation en tableau) :
- Tas construit : 1 2 3 4 7 6 9
- Après 1re extraction : 2 4 3 9 7 6 (extrait = 1)
- Après 2e extraction : 3 4 6 9 7 (extrait = 2)

## Exercice 4 - Compromis quantitatifs

1) 800 recherches, 150 insertions, 50 suppressions :
- Table de hachage (si l'ordre n'importe pas), sinon BST équilibré.

2) 500 récupérations du minimum + 500 insertions :
- Tas min.

3) Parcours en ordre croissant à chaque requête :
- Arbre de recherche équilibré (structure dynamique), ou tableau trié si updates rares.

## Exercice 5 - Mini-raisonnement
Exemple : recherche linéaire vs recherche dichotomique.
Un tableau trié impose l'invariant d'ordre total, ce qui permet une recherche en O(log n).
L'algorithme est plus complexe, mais bien plus rapide grâce à l'information structurelle.
