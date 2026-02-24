# Demo 6: Maps and Hash Tables

This demo covers **Chapters 10.1 (Maps)** and **10.2 (Hash Tables)** from the textbook *Data Structures and Algorithms in Java (6th ed.)*.

!!! abstract "Learning Objectives"

    - Understand the Map ADT and its fundamental operations
    - Implement a simple map with `UnsortedTableMap`
    - Master hash functions (codes + compression)
    - Distinguish separate chaining from open addressing
    - Analyze the role of the load factor and rehashing
    - Understand the `hashCode()`/`equals()` contract in Java

---

# Part I — Theory

## 1. Map ADT (§10.1.1)

A **map** (associative array) stores **key–value** pairs $(k, v)$ called **entries**.

Keys are **unique**: the key → value association defines a *function* (in the mathematical sense).

### Fundamental Operations

| Operation     | Description                                                  |
|---------------|--------------------------------------------------------------|
| `get(k)`      | Returns the value associated with $k$, or `null` if absent      |
| `put(k, v)`   | Inserts $(k,v)$ or replaces the existing value; returns the old value or `null` |
| `remove(k)`   | Removes the entry and returns its value, or `null`           |
| `size()`      | Number of entries                                             |
| `isEmpty()`   | Tests whether the map is empty                                     |
| `keySet()`    | Iterable collection of keys                                 |
| `values()`    | Iterable collection of values                              |
| `entrySet()`  | Iterable collection of $(k,v)$ pairs                       |

!!! warning "Ambiguity of `null`"

    `get(k) == null` can mean two things:

    - the key $k$ is **absent** from the map, or
    - the key is **present** but associated with the value `null`.

    In Java, `containsKey(k)` resolves the ambiguity.

### Execution Trace Example (Example 10.1 from the textbook)

| Operation     | Return  | Map State                          |
|---------------|---------|-----------------------------------------|
| `put(5, A)`   | `null`  | {(5,A)}                                 |
| `put(7, B)`   | `null`  | {(5,A), (7,B)}                          |
| `put(2, C)`   | `null`  | {(5,A), (7,B), (2,C)}                   |
| `put(2, E)`   | `C`     | {(5,A), (7,B), (2,E)}                   |
| `get(7)`      | `B`     | unchanged                                |
| `get(4)`      | `null`  | unchanged                                |
| `remove(5)`   | `A`     | {(7,B), (2,E)}                          |

---

## 2. Simple Implementation: UnsortedTableMap (§10.1.4)

Entries are stored in an unsorted `ArrayList`.

Each operation requires a **linear scan** to search for the key.

| Method     | Complexity |
|------------|------------|
| `get(k)`   | $O(n)$     |
| `put(k,v)` | $O(n)$     |
| `remove(k)`| $O(n)$     |

!!! tip "Removal Trick"

    To avoid an $O(n)$ shift during `remove`, replace the deleted entry with the **last entry** in the array, then remove the last position → removal in $O(1)$ once the index is found.

---

## 3. Hash Tables (§10.2)

**Goal**: achieve `get`, `put`, `remove` in **expected $O(1)$**.

### Core Idea

Use a **hash function** $h$ to transform a key $k$ into an index $h(k) \in [0, N-1]$ in a **bucket array** of capacity $N$.

$$\text{Key } k \;\xrightarrow{\text{hashCode()}}\; \text{integer} \;\xrightarrow{\text{compression}}\; \text{index } \in [0, N-1]$$

### Two Distinct Steps (§10.2.1)

1. **Hash code**: transforms the key into an integer — **independent** of $N$.
2. **Compression function**: maps the integer into $[0, N-1]$ — **depends** on $N$.

The advantage of this separation: if the table is resized, only the compression changes; the `hashCode()` of each object remains the same.

---

## 4. Hash Codes (§10.2.1)

### Binary Representation

For types fitting in $\leq$ 32 bits (`byte`, `short`, `int`, `char`), the integer value is used directly. For 64-bit types (`long`, `double`), the upper and lower 32 bits are combined (by sum or XOR).

### Polynomial Hash Code

For strings and variable-length objects $(x_0, x_1, \ldots, x_{n-1})$, a constant $a \neq 0, 1$ is chosen and the following is computed:

$$x_0 \cdot a^{n-1} + x_1 \cdot a^{n-2} + \cdots + x_{n-2} \cdot a + x_{n-1}$$

Efficient evaluation via **Horner's method**:

$$x_{n-1} + a\bigl(x_{n-2} + a(x_{n-3} + \cdots + a(x_1 + a \cdot x_0) \cdots)\bigr)$$

Values $a \in \{33, 37, 39, 41\}$ are particularly good for English strings (fewer than 7 collisions over 50,000 words).

### Cyclic-Shift Hash Code

A variant that replaces multiplication by $a$ with a **cyclic bit shift**. A shift of **5 bits** is optimal (190 total collisions over 230,000 English words, compared to 234,735 for a shift of 0, which reduces to a simple sum).

```java
static int hashCode(String s) {
    int h = 0;
    for (int i = 0; i < s.length(); i++) {
        h = (h << 5) | (h >>> 27);   // 5-bit cyclic shift
        h += (int) s.charAt(i);
    }
    return h;
}
```

---

## 5. Compression Functions (§10.2.1)

### Division Method

$$h(k) = i \bmod N$$

If $N$ is **prime**, the distribution is better. If $N$ is not prime, regular patterns in hash codes will repeat.

### MAD Method (*Multiply-Add-and-Divide*)

$$h(k) = \bigl[(a \cdot i + b) \bmod p\bigr] \bmod N$$

where $p$ is a prime $> N$, $a \in [1, p-1]$, $b \in [0, p-1]$, chosen randomly.

MAD eliminates regular patterns better than simple division.

---

## 6. Collision Handling (§10.2.2)

A **collision** occurs when two distinct keys $k_1 \neq k_2$ have the same hash: $h(k_1) = h(k_2)$.

### Separate Chaining

Each bucket $A[j]$ holds a **small secondary map** (typically `UnsortedTableMap`) storing all entries whose hash equals $j$.

- The load factor $\lambda$ **can exceed 1**.
- Simple removal (no sentinel required).
- Expected cost: $O(1 + \lambda)$ per operation.

### Open Addressing

Entries are stored **directly in the array**. If bucket $A[h(k)]$ is occupied, subsequent buckets are probed.

| Strategy            | Probe sequence $f(i)$                 | Main drawback              |
|---------------------|---------------------------------------|----------------------------|
| Linear              | $f(i) = i$                            | Primary clustering         |
| Quadratic           | $f(i) = i^2$                          | Secondary clustering       |
| Double hashing      | $f(i) = i \cdot h'(k)$               | Higher computational cost  |

- $\lambda$ **must remain $< 1$** (there cannot be more entries than slots).
- Removal requires a **DEFUNCT** marker (sentinel) so as not to break probe chains.

---

## 7. Load Factor and Rehashing (§10.2.3)

The **load factor** is $\lambda = n / N$ ($n$ = number of entries, $N$ = array capacity).

### Recommended Thresholds

| Strategy            | Max $\lambda$ threshold |
|---------------------|----------------------|
| Separate chaining   | $< 0.9$ (Java defaults to 0.75) |
| Open addressing     | $< 0.5$ for linear probing       |

### Rehashing

When $\lambda$ exceeds the threshold:

1. Create a **new array** of size $\approx 2N$ (ideally prime).
2. **Recompute the compression** (not the `hashCode()`!) for each entry.
3. Reinsert all entries into the new array.

The cost of rehashing is **amortized** to $O(1)$ per operation (same reasoning as dynamic array resizing).

---

## 8. The `hashCode()` / `equals()` Contract in Java (§10.2.1)

**Fundamental rule**: if `x.equals(y)` returns `true`, then `x.hashCode() == y.hashCode()` **must** be true.

The converse is **false**: two unequal objects *may* have the same hash code (that is a collision).

If `equals()` is overridden without overriding `hashCode()`, objects that are "equal" may land in different buckets → the map can no longer retrieve the entries.

---

## 9. Complexity Summary (Table 10.2 from the textbook)

| Method                         | `UnsortedTableMap` | Hash Table (expected) | Hash Table (worst case) |
|--------------------------------|--------------------|-----------------------|-----------------------|
| `get`                          | $O(n)$             | $O(1)$                | $O(n)$                |
| `put`                          | $O(n)$             | $O(1)$                | $O(n)$                |
| `remove`                       | $O(n)$             | $O(1)$                | $O(n)$                |
| `size`, `isEmpty`              | $O(1)$             | $O(1)$                | $O(1)$                |
| `entrySet`, `keySet`, `values` | $O(n)$             | $O(n)$                | $O(n)$                |

The worst case $O(n)$ occurs when all keys have the same hash code.

---

# Part II — Exercises

## 1. True or False

??? question "Q1 — Key Uniqueness"

    In a map, two different entries can have the **same key**.

    ??? success "Answer"

        **False.** Keys are **unique** by definition. If `put(k, v2)` is called when an entry $(k, v1)$ already exists, the value is **replaced** and the old value $v1$ is returned. The size of the map does not change.

??? question "Q2 — Complexity of UnsortedTableMap"

    In an `UnsortedTableMap`, the `put(k, v)` operation always runs in $O(1)$.

    ??? success "Answer"

        **False.** Before inserting, `put` must first **search** whether key $k$ already exists (via `findIndex`), which requires a linear scan → $O(n)$ in the worst case. The insertion itself is $O(1)$ if the key is new, but the prior search dominates.

??? question "Q3 — Hash Code and Compression Function"

    The hash code of an object depends on the size $N$ of the hash table.

    ??? success "Answer"

        **False.** The **hash code** is independent of $N$ — it is the **compression function** that depends on $N$. This is the main advantage of separating hashing into two steps: if the table is resized, the compression changes but not the hash code of each object.

??? question "Q4 — Separate Chaining and Load Factor"

    With separate chaining, the load factor $\lambda$ can never exceed 1.

    ??? success "Answer"

        **False.** With separate chaining, $\lambda$ **can exceed 1** because each bucket contains a linked list that can hold an arbitrary number of entries. It is **open addressing** that imposes $\lambda \leq 1$ (each slot can hold only one entry).

??? question "Q5 — DEFUNCT Sentinel"

    With open addressing (linear probing), one can simply place `null` in a slot to delete an entry.

    ??? success "Answer"

        **False.** Placing `null` would break probe chains. If key $k_1$ was inserted after probing past the position of $k_2$, deleting $k_2$ by placing `null` would cause the search for $k_1$ to fail (the probe would stop prematurely at the `null`). A **DEFUNCT** marker must be used to distinguish "deleted slot" from "never-used slot".

??? question "Q6 — Linear Clustering"

    Quadratic probing completely resolves the clustering problem in open addressing.

    ??? success "Answer"

        **False.** Quadratic probing eliminates **primary clustering** (contiguous clusters) that is characteristic of linear probing, but it introduces its own problem called **secondary clustering**: keys that hash to the same bucket always follow the same probe sequence. **Double hashing** is more effective at avoiding both forms of clustering.

??? question "Q7 — hashCode/equals Contract"

    If two objects have the same `hashCode()`, then `equals()` necessarily returns `true`.

    ??? success "Answer"

        **False.** The contract only states that `equals() == true` **implies** the same `hashCode()`. The converse is false: two unequal objects *may* have the same hash code — this is precisely what is called a **collision**. Only the direction `equals → hashCode` is guaranteed.

---

## 2. Multiple Choice

??? question "Q1 — Polynomial Hash Code"

    The polynomial hash code of the string `"ABC"` is computed with $a = 33$, using the Unicode values $A=65$, $B=66$, $C=67$.

    The formula is: $x_0 \cdot a^2 + x_1 \cdot a + x_2$

    What is the result?

    - [ ] A) 198
    - [ ] B) 73 030
    - [ ] C) 2 211
    - [ ] D) 65 033

    ??? success "Answer"

        **B) 73 030**

        Computation:

        - $65 \times 33^2 + 66 \times 33 + 67$
        - $= 65 \times 1089 + 66 \times 33 + 67$
        - $= 70\,785 + 2\,178 + 67$
        - $= 73\,030$

        By Horner's method: $67 + 33 \times (66 + 33 \times 65) = 67 + 33 \times (66 + 2145) = 67 + 33 \times 2211 = 67 + 72\,963 = 73\,030$ ✓

??? question "Q2 — MAD Compression"

    Consider MAD compression with $a = 3$, $b = 5$, $p = 11$, $N = 7$.

    What is the index of the key whose hash code is $i = 15$?

    - [ ] A) 1
    - [ ] B) 3
    - [ ] C) 5
    - [ ] D) 6

    ??? success "Answer"

        **D) 6**

        Computation: $[(3 \times 15 + 5) \bmod 11] \bmod 7 = [50 \bmod 11] \bmod 7 = 6 \bmod 7 = 6$

??? question "Q3 — Separate Chaining: Most Loaded Bucket"

    Table of size $N = 13$, compression $h(k) = k \bmod 13$.

    The following keys are inserted: $\{18, 41, 22, 44, 59, 32, 31, 73\}$.

    Which bucket contains the most entries, and how many?

    - [ ] A) Bucket 9, with 2 entries
    - [ ] B) Bucket 5, with 3 entries
    - [ ] C) Bucket 6, with 3 entries
    - [ ] D) Bucket 2, with 2 entries

    ??? success "Answer"

        **B) Bucket 5, with 3 entries**

        Distribution:

        | Key | $k \bmod 13$ | Bucket |
        |-----|-------------|--------|
        | 18  | 5           | 5      |
        | 41  | 2           | 2      |
        | 22  | 9           | 9      |
        | 44  | 5           | 5      |
        | 59  | 7           | 7      |
        | 32  | 6           | 6      |
        | 31  | 5           | 5      |
        | 73  | 8           | 8      |

        Bucket 5 contains $\{18, 44, 31\}$ → **3 entries**.

??? question "Q4 — When Is Division Sufficient?"

    In which situation is the compression $h(k) = k \bmod N$ **generally sufficient**?

    - [ ] A) The keys are consecutive integers (1001, 1002, 1003, …)
    - [ ] B) $N$ is prime and the hash codes are already well distributed
    - [ ] C) $N$ is a power of 2 and keys differ only in their high-order bits
    - [ ] D) The table is nearly full ($\lambda \approx 1$)

    ??? success "Answer"

        **B)**

        The division method works well when $N$ is prime and the hash codes have no regular pattern. Choosing a prime $N$ helps "spread" the distribution. Option A also works correctly if $N$ is prime, but option B is more general. Option C is problematic because a power of 2 only retains the low-order bits — differences in high-order bits are lost. Option D concerns load factor management, not the quality of the compression.

??? question "Q5 — What Is Recomputed During Rehashing?"

    When a hash table is resized, what must be recomputed for each entry?

    - [ ] A) The `hashCode()` of the object
    - [ ] B) The result of the compression function (the index in the array)
    - [ ] C) The `equals()` method
    - [ ] D) The definition of the load factor

    ??? success "Answer"

        **B)**

        The `hashCode()` of an object is an intrinsic property — it does not change during rehashing. However, the **compression function** depends on the array size $N$ (for example $i \bmod N$). Since $N$ changes, the index of each entry in the new array must be recomputed. This is precisely why the separation of hash code and compression is useful.

??? question "Q6 — Web Cache: Which Strategy?"

    A web cache is being designed (key = URL, value = content). **Deletions are frequent** (pages expire regularly). Which collision strategy is most appropriate?

    - [ ] A) Open addressing with linear probing
    - [ ] B) Open addressing with double hashing
    - [ ] C) Separate chaining
    - [ ] D) None — an `UnsortedTableMap` is sufficient

    ??? success "Answer"

        **C) Separate chaining**

        **Frequent deletion** is the key criterion. With open addressing, each deletion requires a DEFUNCT sentinel, which progressively degrades performance (probe chains lengthen as DEFUNCT markers accumulate). With separate chaining, deletion is straightforward — a node is simply removed from the secondary list with no side effects on other entries.

---

## 3. Advanced Questions

??? question "Q7 — Numerical Load Factor"

    A hash table uses **separate chaining** with $N = 100$ buckets and contains $n = 250$ entries. Assume **uniform hashing** (each bucket is equally likely).

    What is the **expected** number of elements examined when `get(k)` is called?

    - [ ] A) 1
    - [ ] B) 2.5
    - [ ] C) 25
    - [ ] D) 250

    ??? success "Answer"

        **B) 2.5**

        The load factor is $\lambda = n/N = 250/100 = 2.5$.

        With uniform hashing, the expected size of each bucket is $\lambda = 2.5$. The cost of a successful `get` is proportional to the size of the bucket containing the key → on average **2.5 elements** to examine.

??? question "Q8 — When $O(1)$ Disappears"

    A hash table is used with a **fixed** capacity $N$ (no rehashing). Entries are inserted without ever resizing.

    What does the expected complexity of `get(k)` become as $n \to \infty$? Justify your answer.

    ??? success "Answer"

        The expected cost is $O(1 + \lambda) = O(1 + n/N)$.

        If $N$ is fixed and $n \to \infty$, then $\lambda \to \infty$ and the cost becomes **$O(n)$** — the advantage of hashing is lost.

        This is precisely why **rehashing** is necessary: it keeps $\lambda$ bounded by a constant, guaranteeing expected $O(1)$. The cost of rehashing itself is amortized to $O(1)$ per operation.

??? question "Q9 — Building a Pathological `hashCode()`"

    Give a valid Java implementation of `hashCode()` that **respects the** `equals`/`hashCode` **contract** but causes the **worst case** for a hash table. Explain the effect on performance.

    ??? success "Answer"

        ```java
        @Override
        public int hashCode() {
            return 1;  // constante — valide mais catastrophique
        }
        ```

        **Why it is valid**: the contract requires that `equals == true` implies the same `hashCode`. Here, *all* objects have the same hash code, which trivially satisfies the condition (same `hashCode` for everyone, and in particular for equal objects).

        **Effect**: all entries land in the **same bucket**. With separate chaining, that bucket becomes a list of size $n$ → the `get`, `put`, `remove` operations degrade to **$O(n)$** (the same behavior as an `UnsortedTableMap`).

        This is also the DoS attack vector described in the textbook (§10.2.3): an attacker can precompute strings that share the same hash code to saturate a web server.

??? question "Q10 — Memory Trade-off: Chaining vs. Open Addressing"

    Compare the memory overhead of **separate chaining** and **open addressing** in the following two scenarios:

    1. The stored objects are **very small** (~8 bytes).
    2. The stored objects are **very large** (~2 KB).

    ??? success "Answer"

        **Scenario 1 — Small objects (~8 bytes):**

        With separate chaining, each entry incurs a **node/reference overhead** (typically 16–32 bytes per entry in the secondary list). For an 8-byte payload, the overhead can **double or triple** the total memory usage.

        With open addressing, entries are stored directly in the array — no auxiliary node. The only overhead comes from empty slots (since $\lambda < 0.5$ must be maintained, roughly half the array is empty).

        **→ Open addressing is more compact for small objects.**

        **Scenario 2 — Large objects (~2 KB):**

        The per-node overhead of chaining (16–32 bytes) is **negligible** compared to the 2 KB payload (< 2%). In contrast, open addressing wastes $\approx$ 50% of the array space in empty slots, and each empty slot occupies the size of a full entry (or a reference, depending on the implementation).

        **→ For large objects, the relative overhead of chaining is minimal, and chaining also offers simpler deletions.**

??? question "Q11 — `equals()` Overridden Without `hashCode()`"

    A class `Student` is created with an `equals()` override based on the student ID, but `hashCode()` is **forgotten**. Instances of `Student` are used as keys in a Java `HashMap`.

    What is likely to happen? Why?

    ??? success "Answer"

        **The Java contract is violated**: `equals()` returns `true` but `hashCode()` returns different values (because the default version inherited from `Object` is based on memory address).

        **Concrete consequences:**

        1. Two `Student` objects with the same student ID will be considered "equal" by `equals()` but may land in **different buckets** of the `HashMap`.
        2. `map.put(student1, grade)` followed by `map.get(student2)` (where `student1.equals(student2)` → `true`) may return `null`, because `student2` is looked up in a different bucket from where `student1` was inserted.
        3. **Multiple entries** for the same logical key may accumulate, violating the map contract.

        **Takeaway**: any override of `equals()` must be accompanied by a consistent override of `hashCode()`.

---

## 4. Trace Exercises

??? question "Q12 — Full Trace (Separate Chaining)"

    A hash table with **separate chaining** of capacity $N = 7$ is used.

    Keys are 2-character strings. For a key $s = s_0 s_1$:

    - **Hash code**: $hc(s) = 1 \cdot code(s_0) + 2 \cdot code(s_1)$
    - **Compression**: $h(s) = hc(s) \bmod 7$

    New entries are inserted at the **head of the list** in each bucket.

    Execute the following trace and give:

    1. the value returned by each operation;
    2. the final state of the table (contents of each non-empty bucket).

    Trace:

    1. `put("AX", 10)`
    2. `put("HQ", 20)`
    3. `put("BY", 30)`
    4. `put("CZ", 40)`
    5. `put("DX", 50)`
    6. `get("HQ")`
    7. `remove("BY")`
    8. `put("EY", 60)`
    9. `get("BY")`

    ??? success "Answer"

        **Bucket Computations**

        | Key  | $hc(s)$ | $h(s)=hc \bmod 7$ |
        |------|---------|-------------------|
        | AX   | $65 + 2 \cdot 88 = 241$ | 3 |
        | HQ   | $72 + 2 \cdot 81 = 234$ | 3 |
        | BY   | $66 + 2 \cdot 89 = 244$ | 6 |
        | CZ   | $67 + 2 \cdot 90 = 247$ | 2 |
        | DX   | $68 + 2 \cdot 88 = 244$ | 6 |
        | EY   | $69 + 2 \cdot 89 = 247$ | 2 |

        **Operation Return Values**

        1. `put("AX", 10)` → `null`
        2. `put("HQ", 20)` → `null`
        3. `put("BY", 30)` → `null`
        4. `put("CZ", 40)` → `null`
        5. `put("DX", 50)` → `null`
        6. `get("HQ")` → `20`
        7. `remove("BY")` → `30`
        8. `put("EY", 60)` → `null`
        9. `get("BY")` → `null`

        **Final State**

        - Bucket 2: `("EY",60) -> ("CZ",40)`
        - Bucket 3: `("HQ",20) -> ("AX",10)`
        - Bucket 6: `("DX",50)`

??? question "Q13 — Full Trace (Open Addressing)"

    A hash table with **open addressing** (linear probing) of capacity $N = 11$ is used.

    For an integer key $k$:

    - **Hash code**: $hc(k) = 2k + 5$
    - **MAD Compression**: $h(k) = \bigl[(3 \cdot hc(k) + 7) \bmod 31\bigr] \bmod 11$
    - **Probing**: $p_t(k) = (h(k) + t) \bmod 11$

    Deletion uses a `DEFUNCT` sentinel.

    Execute the following trace and give:

    1. the value returned by each operation;
    2. the final state of the table (non-empty indices).

    Trace:

    1. `put(10, "A")`
    2. `put(41, "B")`
    3. `put(72, "C")`
    4. `put(21, "D")`
    5. `remove(41)`
    6. `get(72)`
    7. `put(103, "E")`
    8. `get(41)`
    9. `put(54, "F")`

    ??? success "Answer"

        **Initial Indices**

        | $k$ | $hc(k)=2k+5$ | $h(k)=((3hc+7)\bmod 31)\bmod 11$ |
        |-----|--------------|-----------------------------------|
        | 10  | 25           | 9 |
        | 41  | 87           | 9 |
        | 72  | 149          | 9 |
        | 21  | 47           | 2 |
        | 103 | 211          | 9 |
        | 54  | 113          | 5 |

        Keys 10, 41, 72, and 103 all start at index 9, so linear probing is applied.

        **Operation Return Values**

        1. `put(10, "A")` → `null`
        2. `put(41, "B")` → `null`
        3. `put(72, "C")` → `null`
        4. `put(21, "D")` → `null`
        5. `remove(41)` → `"B"` (slot marked `DEFUNCT`)
        6. `get(72)` → `"C"` (the search traverses the `DEFUNCT` slot)
        7. `put(103, "E")` → `null` (reuses the first `DEFUNCT` slot)
        8. `get(41)` → `null`
        9. `put(54, "F")` → `null`

        **Final State (non-empty indices)**

        - 0 : `(72,"C")`
        - 2 : `(21,"D")`
        - 5 : `(54,"F")`
        - 9 : `(10,"A")`
        - 10 : `(103,"E")`

---

# References

Goodrich, Tamassia, Goldwasser — *Data Structures and Algorithms in Java*, 6th ed. — Chapters 10.1 and 10.2
