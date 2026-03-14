# Prédiction et sorties — Contrat fonctionnel

## Prédiction
Entrée : token u
Sortie : token v

Règle :
- bigramme si possible
- sinon unigramme
- égalité → lexicographique

## Top-k
Classement par fréquence décroissante puis ordre lexicographique.
