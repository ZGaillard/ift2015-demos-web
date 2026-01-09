# Semaine 1 - Introduction aux structures de données

Cette séance accompagne les slides d'introduction et met l'accent sur les idées clés :
structure == fonction, structure == accélération, invariants et compromis.

## Objectifs
- Relier un besoin logiciel aux opérations critiques.
- Choisir une structure simple en fonction des opérations dominantes.
- Identifier un invariant et expliquer le coût de maintenance associé.

## Format
- Rappel théorie (5-10 min)
- Exercices en classe (30-45 min)
- Configuration Java 25 + IntelliJ (voir la page de configuration)

---

## Exercice 1 - Choisir la structure (scénarios)
Pour chaque scénario, choisissez une structure et justifiez votre choix.
Indiquez l'opération la plus critique et le coût que vous acceptez en échange.

| Scénario | Opérations dominantes | Structure proposée | Invariant clé | Coût accepté |
| --- | --- | --- | --- | --- |
| Beaucoup d'insertions, très peu de recherches |  |  |  |  |
| Tests d'appartenance très fréquents, l'ordre n'importe pas |  |  |  |  |
| Accès rapide au minimum (ordonnancement) |  |  |  |  |
| Affichage final trié, insertions pendant la collecte |  |  |  |  |
| Recherche fréquente + parcours en ordre croissant |  |  |  |  |
| Suppressions et insertions fréquentes tout en gardant l'ordre |  |  |  |  |
| Accès rapide au maximum, insertions modérées |  |  |  |  |
| Beaucoup de lectures, très peu de mises à jour |  |  |  |  |

Questions rapides :
- Pourquoi cette structure plutôt qu'un tableau non trié ?
- Où se cache le coût de maintenance ?

---

## Exercice 2 - Invariants et accélération
Complétez le tableau en associant à chaque structure son invariant,
l'opération accélérée et le coût principal.

| Structure | Invariant | Opération accélérée | Coût principal |
| --- | --- | --- | --- |
| Tableau trié |  |  |  |
| Tas (min-heap) |  |  |  |
| Arbre de recherche |  |  |  |
| Table de hachage |  |  |  |

Discussion :
- Quel invariant est le plus fort ?
- Quel invariant est le plus coûteux à maintenir ?

---

## Exercice 3 - Tri par structure (papier)
On veut trier la liste suivante :

```
7  2  9  4  1  6  3
```

1) Tri par sélection : montrez l'état du tableau après les deux premières passes.
2) Tri par insertion : montrez le tableau après l'insertion du 4e élément.
3) Heapsort :
   - construisez le tas min (représentation en tableau),
   - puis effectuez deux extractions du minimum.

Questions :
- Quelle méthode réutilise le plus d'information entre deux étapes ?
- Quel invariant est maintenu par chaque méthode ?

---

## Exercice 4 - Compromis quantitatifs
Choisissez la structure la plus pertinente dans chaque cas et justifiez.

1) Sur 1000 opérations : 800 recherches, 150 insertions, 50 suppressions.
2) On doit récupérer le minimum 500 fois et insérer 500 fois.
3) On doit parcourir en ordre croissant à chaque requête.

Indiquez si vous privilégiez :
- tableau non trié,
- tableau trié,
- tas,
- arbre de recherche,
- table de hachage.

---

## Exercice 5 - Mini-raisonnement (bonus)
Expliquez pourquoi un algorithme plus complexe peut être plus rapide si la
structure maintient un invariant fort. Donnez un exemple concret.

---

## À faire après la séance
- Vérifier votre configuration Java 25 + IntelliJ.
- Relire les slides et repérer les invariants cités.
