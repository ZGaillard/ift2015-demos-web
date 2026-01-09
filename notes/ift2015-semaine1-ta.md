# IFT2015 - Semaine 1 (TA plan)

## Objectifs de la seance
- Expliquer l'idee centrale: structure == fonction, structure == acceleration.
- Faire identifier des invariants simples et leurs couts de maintenance.
- Lancer la configuration Java 25 + IntelliJ pour que tout le monde compile.

## Materiel
- Slides Introduction (PDF) pour 5-10 min de rappel.
- Tableaux/feuilles pour les exercices papier.
- Checklist configuration (voir section configuration).

## Timeline (2h max, ajustable)
- 0:00-0:10 Rappel theorie + message cle
- 0:10-0:35 Exercice 1: choisir la structure
- 0:35-0:55 Exercice 2: invariants
- 0:55-1:00 Wrap rapide + pieges
- 1:00-1:10 Demo config Java 25 + IntelliJ (projecteur)
- 1:10-1:45 Installation + tests par les etudiants
- 1:45-2:00 Depannage + validation finale

## Points cle a marteler
- Une structure est un choix algorithmique, pas un simple conteneur.
- Acceleration vient d'un invariant, pas d'un miracle.
- Tout gain a un cout (updates, memoire, complexite).
- Pas de structure universelle.

## Questions rapides (a poser a la classe)
- Quelle operation doit etre rapide dans votre scenario?
- Quel invariant permet d'accelerer cette operation?
- Ou est cache le cout de maintenance?

## Exercice 1: Choisir la structure (reponses attendues)
- Beaucoup d'insertion, peu de recherche -> tableau non trie.
- Recherche frequente -> tableau trie ou arbre (selon updates).
- Min frequent -> tas (heap).
- Test d'appartenance rapide -> table de hachage.

## Exercice 2: Invariants (reponses attendues)
- Tableau trie -> ordre total.
- Tas -> parent <= enfants (min a la racine).
- BST -> cles gauche < racine < cles droite.
- Hash -> cle -> compartiment.

## Exercice 3: Tri par structure (rappel)
- Selection: aucun invariant fort, O(n^2).
- Insertion: prefixe trie, O(n^2) mais souvent meilleur.
- Heapsort: invariant global (tas), O(n log n).

## Configuration (checklist rapide)
- [ ] JDK 25 installe (Temurin ou Oracle)
- [ ] `java -version` = 25
- [ ] `javac -version` = 25
- [ ] IntelliJ Project SDK = 25
- [ ] Run HelloDS OK

## Depannage express
- Mauvais JDK dans IntelliJ -> Project Structure -> Project SDK = 25.
- Gradle: Gradle JVM = Project SDK.
- Erreur "Unsupported class file major version 69" -> Java trop vieux.

## Plan B si retard
- Faire Exercice 1 + demo config.
- Laisser Exercice 2/3 en devoir court.

## Post-seance
- Noter les problemes frequents de configuration.
- Mettre a jour la page de config si besoin.
