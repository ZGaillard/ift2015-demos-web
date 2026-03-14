# :material-numeric-1-circle: Lab 1 — Autocomplete

[:material-folder-zip: Download Lab 1](../files/tp-autocompleter.zip){ .md-button .md-button--primary }

---

## Context and objectives

Autocomplete systems rely on statistics extracted from large amounts of text. In this lab, you will implement the fundamental components of such a system, with an emphasis on **choosing and efficiently using data structures**.

The model is based on **n-grams**: sequences of consecutive tokens extracted from a corpus. Prediction uses **Katz Backoff** — we first look for a trigram context, then bigram, then unigram. Prefix completion is handled by a **trie**.

**Learning objectives:**

- Choose appropriate data structures for each problem
- Analyze time and space complexity of operations
- Implement efficient algorithms (heap, trie)
- Integrate multiple structures into a functional language model

---

## Project files

The Maven project is in `src/main/java/ca/udem/ift2015/autocompleter/`. The provided packages (`model/`, `preprocessing/`, `gui/`) **must not be modified**.

You only need to complete the **4 following files** in `student/`:

| File | TODOs | Points |
|---|---|---|
| `HashFrequencyTable.java` | 1 – 6 | 8 pts |
| `HeapTopKStrategy.java` | 7 | 10 pts |
| `PrefixTrie.java` | 8 – 10 | 14 pts |
| `KatzBackoffModel.java` | 11 – 14 | 18 pts |

Detailed instructions for each TODO are in **`instructions/TP.pdf`** (included in the zip).

---

## Graphical interface

Launch the interface with:

```bash
mvn javafx:run
```

It has four tabs:

| Tab | Description |
|---|---|
| **Corpus** | Select one or more `.txt` files from `src/main/resources/corpus/`, then click "Entraîner la sélection" |
| **Demo** | Test autocomplete in real time — suggestions appear word by word and the top-5 for each level (trigram, bigram, unigram, trie) are detailed |
| **Benchmark** | Measures training and query times, as well as memory usage |
| **Autograder** | Runs automated grading (63 JUnit tests). **Use this tool to validate your work before submission.** |

---

## Report

Answer the questions in **`instructions/rapport.pdf`** (included in the zip). You may use **`instructions/rapport.tex`** as a LaTeX template.

| Question | Topic | Points |
|---|---|---|
| Q1 | Data structure invariants | 12 pts |
| Q2 | Asymptotic complexity of training | 12 pts |
| Q3 | Asymptotic complexity of queries | 10 pts |
| Q4 | Comparison of alternative implementations | 6 pts |

---

## Grading

| Criterion | Points |
|---|---|
| Implementation (Autograder) | 50 pts |
| Code quality and comments | 10 pts |
| Report | 40 pts |
| **Total** | **100 pts** |

!!! success "Bonus"

    **+5% on the final grade** if the report is submitted **printed** in class (in addition to the digital submission on Studium). The bonus applies to the total grade, capped at 100.

---

## Submission

**Deadline: April 12** on Studium.

Submit a `.zip` or `.tar.gz` archive containing:

1. The 4 Java files from the `student/` folder:
    - `HashFrequencyTable.java`
    - `HeapTopKStrategy.java`
    - `PrefixTrie.java`
    - `KatzBackoffModel.java`
2. The report as a PDF, named `rapport_firstname1LastName1_firstname2LastName2.pdf`
   *Example:* `rapport_aliceDupont_bobMartin.pdf`

**Conformity requirements:**

- The project must compile without errors: `mvn compile`
- Only files in the `student/` folder should have been modified
- No external dependencies should be added to `pom.xml`
- The report must be submitted in PDF format only

!!! info "Bonus — printed version"

    To receive the bonus, submit a printed version of your report **no later than the April 14 class**.
