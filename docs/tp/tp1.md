# :material-numeric-1-circle: TP1 — Autocomplétion

[:material-folder-zip: Télécharger le TP1](../files/tp-autocompleter.zip){ .md-button .md-button--primary }

!!! info "Présentation en démo"

    Le TP sera présenté en détail lors de la **démonstration du 17 mars**.

---

## Contexte et objectifs

Les systèmes d'autocomplétion reposent sur des statistiques extraites de grandes quantités de texte. Dans ce TP, vous allez implémenter les composantes fondamentales d'un tel système, en mettant l'accent sur le **choix et l'utilisation efficace de structures de données**.

Le modèle est basé sur les **n-grammes** : des séquences de tokens consécutifs extraites d'un corpus. La prédiction utilise le **repli de Katz** — on cherche d'abord un contexte trigramme, puis bigramme, puis unigramme. La complétion de préfixe repose sur un **trie**.

**Objectifs pédagogiques :**

- Choisir des structures de données adaptées à chaque problème
- Analyser la complexité temporelle et spatiale des opérations
- Implémenter des algorithmes efficaces (tas, trie)
- Intégrer plusieurs structures au sein d'un modèle de langage fonctionnel

---

## Fichiers du projet

Le projet Maven est dans `src/main/java/ca/udem/ift2015/autocompleter/`. Les packages fournis (`model/`, `preprocessing/`, `gui/`) **ne doivent pas être modifiés**.

Vous ne devez compléter **que les 4 fichiers suivants** dans `student/` :

| Fichier | TODOs | Points |
|---|---|---|
| `HashFrequencyTable.java` | 1 – 6 | 8 pts |
| `HeapTopKStrategy.java` | 7 | 10 pts |
| `PrefixTrie.java` | 8 – 10 | 14 pts |
| `KatzBackoffModel.java` | 11 – 14 | 18 pts |

Les instructions détaillées de chaque TODO se trouvent dans **`instructions/TP.pdf`** (inclus dans le zip).

---

## Interface graphique

Lancez l'interface avec :

```bash
mvn javafx:run
```

Elle comporte quatre onglets :

| Onglet | Description |
|---|---|
| **Corpus** | Sélectionnez un ou plusieurs fichiers `.txt` dans `src/main/resources/corpus/`, puis cliquez sur « Entraîner la sélection » |
| **Démo** | Testez l'autocomplétion en temps réel — les suggestions s'affichent mot par mot et les top-5 de chaque niveau (trigramme, bigramme, unigramme, trie) sont détaillés |
| **Benchmark** | Mesure les temps d'entraînement et de requête, ainsi que la mémoire utilisée |
| **Autograder** | Lance la correction automatisée (63 tests JUnit). **Utilisez cet outil pour valider votre travail avant la remise.** |

---

## Rapport

Répondez aux questions du fichier **`instructions/rapport.pdf`** (inclus dans le zip). Vous pouvez utiliser **`instructions/rapport.tex`** comme gabarit LaTeX.

| Question | Sujet | Points |
|---|---|---|
| Q1 | Invariants des structures de données | 12 pts |
| Q2 | Complexité asymptotique de l'entraînement | 12 pts |
| Q3 | Complexité asymptotique des requêtes | 10 pts |
| Q4 | Comparaison d'implémentations alternatives | 6 pts |

---

## Barème

| Critère | Points |
|---|---|
| Implémentation (Autograder) | 50 pts |
| Qualité du code et commentaires | 10 pts |
| Rapport | 40 pts |
| **Total** | **100 pts** |

!!! success "Bonus"

    **+5 % sur la note finale** si le rapport est remis **imprimé** au cours (en plus de la remise numérique sur Studium). Le bonus s'applique à la note totale, plafonnée à 100.

---

## Remise

**Date limite : 12 avril** sur Studium.

Soumettez une archive `.zip` ou `.tar.gz` contenant :

1. Les 4 fichiers Java du dossier `student/` :
    - `HashFrequencyTable.java`
    - `HeapTopKStrategy.java`
    - `PrefixTrie.java`
    - `KatzBackoffModel.java`
2. Le rapport en PDF, nommé `rapport_prenom1Nom1_prenom2Nom2.pdf`
   *Exemple :* `rapport_aliceDupont_bobMartin.pdf`

**Critères de conformité :**

- Le projet doit compiler sans erreur : `mvn compile`
- Seuls les fichiers du dossier `student/` doivent avoir été modifiés
- Aucune dépendance externe ne doit être ajoutée au `pom.xml`
- Le rapport doit être remis au format PDF uniquement

!!! info "Bonus — version imprimée"

    Pour obtenir le bonus, remettez une version imprimée du rapport **au plus tard au cours du 14 avril**.
