# Demo 3: Stacks, Queues, Deques and Favorites List

This demo covers **Chapter 6** (*Stacks, Queues, and Deques*) and **Section 7.7** (*The Favorites List ADT*) of the book *Data Structures and Algorithms in Java (6th ed.)*.

!!! abstract "Learning Objectives"

    By the end of this demo, you should be able to:

    * **Analyze the complexity** of an algorithm using Big-O notation
    * Distinguish the Stack, Queue and Deque ADTs by their access policy (LIFO vs FIFO)
    * Implement these structures with (circular) arrays and linked lists
    * Understand the "move-to-front" heuristic and analyze its impact on complexity
    * Apply these structures to concrete problems (parentheses, expressions, cache)
    * Recognize concurrency issues with shared queues

---

## Review: Asymptotic Complexity

Before discussing data structures, let us review **asymptotic complexity**, a fundamental tool for analyzing algorithm efficiency.

### What is Asymptotic Complexity?

Asymptotic complexity describes how the **execution time** (or **memory space**) of an algorithm evolves as the input size (n) becomes very large.

!!! info "The Key Idea"

    We do not measure the exact time in seconds (which depends on the hardware), but the **growth rate** of the number of operations as a function of n.

### Big-O Notation

The **O(...)** ("Big-O") notation expresses an **upper bound** on growth:

> "f(n) is O(g(n))" means that f(n) grows **at most as fast** as g(n) for large values of n.

---

### Formal Mathematical Definitions

Asymptotic notations allow us to compare the growth of functions. Here are the three main ones:

#### Big-O: Asymptotic Upper Bound

!!! note "Definition: O (Big-O)"

    Let f : N -> R+ and g : N -> R+ be two functions.

    $$f(n) \in O(g(n)) \iff \exists\, c > 0,\; \exists\, n_0 \in \mathbb{N},\; \forall\, n \geq n_0 : f(n) \leq c \cdot g(n)$$

    **In words:** From a certain rank n_0 onward, f(n) is **bounded above** by a constant multiple of g(n).

```python
# mkdocs: render
# mkdocs: hidecode
import matplotlib.pyplot as plt
import numpy as np

fig, ax = plt.subplots(figsize=(8, 5))

n = np.linspace(0.1, 10, 200)
n0 = 3

# f(n) starts above c·g(n) then stays well below after n0
g = n ** 1.5
c = 2.0
f = np.where(n < n0, 1.5 * n**1.8 + 2, 2* n + 1)

ax.plot(n, c * g, 'b-', linewidth=2.5, label=r'$c \cdot g(n)$')
ax.plot(n, f, 'r-', linewidth=2.5, label=r'$f(n)$')
ax.axvline(x=n0, color='gray', linestyle='--', linewidth=2, label=r'$n_0$')

ax.fill_between(n[n >= n0], f[n >= n0], c * g[n >= n0], alpha=0.3, color='green')

ax.set_xlabel('n', fontsize=14)
ax.set_ylabel('Time', fontsize=14)
ax.set_title(r'Graphical interpretation of $f(n) \in O(g(n))$', fontsize=14)
ax.legend(loc='upper left', fontsize=12)
ax.set_xlim(0, 10)
ax.set_ylim(0, 65)
ax.annotate(r'$f(n) \leq c \cdot g(n)$', xy=(7, 25), fontsize=13,
            bbox=dict(boxstyle='round', facecolor='lightgreen', alpha=0.7))

plt.tight_layout()
```

??? example "Example: Show that 3n² + 5n + 2 ∈ O(n²)"

    We seek c > 0 and n_0 such that 3n² + 5n + 2 ≤ c · n² for all n ≥ n_0.

    **Method:** For n ≥ 1, we have:

    * 5n ≤ 5n²
    * 2 ≤ 2n²

    Therefore: 3n² + 5n + 2 ≤ 3n² + 5n² + 2n² = 10n²

    **Conclusion:** With c = 10 and n_0 = 1, we indeed have 3n² + 5n + 2 ≤ 10n² for all n ≥ 1.

    Therefore **3n² + 5n + 2 ∈ O(n²)** ✓

#### Big-Omega: Asymptotic Lower Bound

!!! note "Definition: Omega (Big-Omega)"

    $$f(n) \in \Omega(g(n)) \iff \exists\, c > 0,\; \exists\, n_0 \in \mathbb{N},\; \forall\, n \geq n_0 : f(n) \geq c \cdot g(n)$$

    **In words:** From a certain rank onward, f(n) is **bounded below** by a constant multiple of g(n).

Big-Omega is the "mirror" of Big-O: it expresses that f grows **at least as fast** as g.

??? example "Example: Show that 3n² + 5n + 2 ∈ Omega(n²)"

    We seek c > 0 and n_0 such that 3n² + 5n + 2 ≥ c · n² for all n ≥ n_0.

    Since 5n ≥ 0 and 2 ≥ 0 for n ≥ 0, we have:

    3n² + 5n + 2 ≥ 3n²

    **Conclusion:** With c = 3 and n_0 = 0, the condition is satisfied.

    Therefore **3n² + 5n + 2 ∈ Omega(n²)** ✓

#### Big-Theta: Exact Asymptotic Bound

!!! note "Definition: Theta (Big-Theta)"

    $$f(n) \in \Theta(g(n)) \iff f(n) \in O(g(n)) \;\text{ et }\; f(n) \in \Omega(g(n))$$

    Equivalent to:

    $$f(n) \in \Theta(g(n)) \iff \exists\, c_1, c_2 > 0,\; \exists\, n_0,\; \forall\, n \geq n_0 : c_1 \cdot g(n) \leq f(n) \leq c_2 \cdot g(n)$$

    **In words:** f(n) grows **at exactly the same rate** as g(n), up to constant factors.

```python
# mkdocs: render
# mkdocs: hidecode
import matplotlib.pyplot as plt
import numpy as np

fig, ax = plt.subplots(figsize=(8, 5))

n = np.linspace(0.1, 10, 200)
n0 = 3

# f(n) sandwiched between c1·g(n) and c2·g(n) after n0
g = n ** 1.5
c1, c2 = 0.5, 2.5
f = np.where(n < n0, 0.3 * n**2 + 1, 1.5 * n**1.5 + 0.5 * np.sin(n))

ax.plot(n, c2 * g, 'b-', linewidth=2.5, label=r'$c_2 \cdot g(n)$')
ax.plot(n, f, 'r-', linewidth=2.5, label=r'$f(n)$')
ax.plot(n, c1 * g, 'g-', linewidth=2.5, label=r'$c_1 \cdot g(n)$')
ax.axvline(x=n0, color='gray', linestyle='--', linewidth=2, label=r'$n_0$')

ax.fill_between(n[n >= n0], c1 * g[n >= n0], c2 * g[n >= n0], alpha=0.25, color='purple')

ax.set_xlabel('n', fontsize=14)
ax.set_ylabel('Time', fontsize=14)
ax.set_title(r'Graphical interpretation of $f(n) \in \Theta(g(n))$', fontsize=14)
ax.legend(loc='upper left', fontsize=12)
ax.set_xlim(0, 10)
ax.set_ylim(0, 80)
ax.annotate(r'$c_1 \cdot g(n) \leq f(n) \leq c_2 \cdot g(n)$', xy=(5.5, 12), fontsize=13,
            bbox=dict(boxstyle='round', facecolor='plum', alpha=0.7))

plt.tight_layout()
```

??? example "Example: Show that 3n² + 5n + 2 ∈ Theta(n²)"

    We have already shown:

    * 3n² + 5n + 2 ∈ O(n²) with c_2 = 10
    * 3n² + 5n + 2 ∈ Omega(n²) with c_1 = 3

    Therefore **3n² + 5n + 2 ∈ Theta(n²)** ✓

    We say that 3n² + 5n + 2 has **quadratic** growth.

#### Summary of Notations

| Notation | Meaning | Analogy |
|----------|---------|---------|
| f(n) ∈ O(g(n)) | f grows **at most as fast** as g | f ≤ g (asymptotically) |
| f(n) ∈ Omega(g(n)) | f grows **at least as fast** as g | f ≥ g (asymptotically) |
| f(n) ∈ Theta(g(n)) | f grows **exactly like** g | f ≈ g (asymptotically) |
| f(n) ∈ o(g(n)) | f grows **strictly slower** than g | f < g (asymptotically) |
| f(n) ∈ omega(g(n)) | f grows **strictly faster** than g | f > g (asymptotically) |

---

### Limit Criterion

The **limit criterion** is often more practical than the definition for comparing two functions:

!!! note "Theorem: Limit Criterion"

    Let $L = \lim_{n \to \infty} \frac{f(n)}{g(n)}$ (if this limit exists or equals +∞). Then:

    | Value of L | Conclusion |
    |------------|------------|
    | L = 0 | f(n) ∈ o(g(n)) ⊂ O(g(n)) — f grows **strictly slower** than g |
    | 0 < L < +∞ | f(n) ∈ Theta(g(n)) — f and g have the **same growth** |
    | L = +∞ | f(n) ∈ omega(g(n)) ⊂ Omega(g(n)) — f grows **strictly faster** than g |

??? example "Example 1: Compare 5n³ and 2n²"

    $$L = \lim_{n \to \infty} \frac{5n^3}{2n^2} = \lim_{n \to \infty} \frac{5n}{2} = +\infty$$

    **Conclusion:** 5n³ ∈ omega(2n²), so 5n³ grows strictly faster than 2n².

    In other words: **n³ dominates n²**.

??? example "Example 2: Compare log(n) and √n"

    $$L = \lim_{n \to \infty} \frac{\log n}{\sqrt{n}}$$

    This is an indeterminate form ∞/∞. We apply L'Hopital's rule:

    $$L = \lim_{n \to \infty} \frac{1/n}{1/(2\sqrt{n})} = \lim_{n \to \infty} \frac{2\sqrt{n}}{n} = \lim_{n \to \infty} \frac{2}{\sqrt{n}} = 0$$

    **Conclusion:** log(n) ∈ o(√n), so log(n) grows strictly slower than √n.

    In other words: **√n dominates log(n)**.

??? example "Example 3: Compare 3n² + 7n and n²"

    $$L = \lim_{n \to \infty} \frac{3n^2 + 7n}{n^2} = \lim_{n \to \infty} \left(3 + \frac{7}{n}\right) = 3$$

    Since 0 < 3 < +∞, we have **3n² + 7n ∈ Theta(n²)**.

    The two functions have the same asymptotic growth.

??? example "Example 4: Compare n! and 2^n"

    $$L = \lim_{n \to \infty} \frac{n!}{2^n}$$

    One can show (via the ratio test or Stirling's approximation) that this limit equals +∞.

    **Conclusion:** n! ∈ omega(2^n), so **n! dominates 2^n**.

    The factorial grows faster than the exponential!

---

### Growth Hierarchy

Here is the order of growth of common functions (from slowest to fastest):

$$1 \prec \log\log n \prec \log n \prec \sqrt{n} \prec n \prec n\log n \prec n^2 \prec n^3 \prec 2^n \prec n! \prec n^n$$

where $f \prec g$ means $f(n) \in o(g(n))$, that is, f grows strictly slower than g.

---

### Useful Properties

!!! info "Algebraic Properties of Big-O"

    Let f_1(n) ∈ O(g_1(n)) and f_2(n) ∈ O(g_2(n)). Then:

    1. **Sum:** f_1(n) + f_2(n) ∈ O(max(g_1(n), g_2(n)))

    2. **Product:** f_1(n) · f_2(n) ∈ O(g_1(n) · g_2(n))

    3. **Constant:** c · f_1(n) ∈ O(g_1(n)) for any constant c > 0

    4. **Transitivity:** If f(n) ∈ O(g(n)) and g(n) ∈ O(h(n)), then f(n) ∈ O(h(n))

??? example "Applying the Properties"

    Consider an algorithm with:

    * A loop in O(n)
    * Followed by a loop in O(n²)
    * In which an O(log n) operation is performed

``` java
for(int i = 0; i < n; i++) {
    // O(1)
}
for(int i = 0; i < n; i++) {
    for(int j = 0; j < n; j++) {
        // O(log n)
    }
}
```

    **Total complexity:**

    * Loop 2: O(n²) · O(log n) = O(n² log n)
    * Total: O(n) + O(n² log n) = O(max(n, n² log n)) = **O(n² log n)**

---

### Common Complexities

| Notation | Name | Example | Behavior |
|----------|------|---------|----------|
| O(1) | Constant | Accessing an array element by index | Instantaneous, regardless of n |
| O(log n) | Logarithmic | Binary search | Very efficient, grows slowly |
| O(n) | Linear | Traversing a list | Proportional to n |
| O(n log n) | Linearithmic | Merge sort | Efficient for sorting |
| O(n²) | Quadratic | Insertion sort (worst case) | Acceptable for small n |
| O(2^n) | Exponential | Some brute force problems | Impractical for large n |

```
Comparative growth (for n = 1000):

O(1)       →           1 operation
O(log n)   →          10 operations
O(n)       →       1,000 operations
O(n log n) →      10,000 operations
O(n²)      →   1,000,000 operations
O(2^n)     → 10^301 operations (impossible!)
```

### Simplification Rules

When analyzing an algorithm, we simplify the expression:

| Rule | Example | Result |
|------|---------|--------|
| Ignore constants | O(3n) | O(n) |
| Ignore lower-order terms | O(n² + n) | O(n²) |
| Nested loops multiply | O(n) loop inside O(n) loop | O(n²) |
| Sequences add | O(n) then O(n) | O(n + n) = O(n) |

??? example "Example: Analyzing an Algorithm"

    ```java
    public static int example(int[] arr) {
        int n = arr.length;
        int sum = 0;                    // O(1)

        for (int i = 0; i < n; i++) {   // Loop: n iterations
            sum += arr[i];              // O(1) per iteration
        }

        for (int i = 0; i < n; i++) {       // Outer loop: n iterations
            for (int j = 0; j < n; j++) {   // Inner loop: n iterations
                sum += arr[i] * arr[j];     // O(1)
            }
        }

        return sum;                     // O(1)
    }
    ```

    **Analysis:**

    * First loop: O(n)
    * Nested loops: O(n) × O(n) = O(n²)
    * Total: O(1) + O(n) + O(n²) + O(1) = **O(n²)**

    The dominant term O(n²) "absorbs" the others.

### Best Case, Worst Case, Average Case

The same algorithm can have different complexities depending on the input:

| Type | Description | Example (linear search) |
|------|-------------|-------------------------|
| **Best case** | Most favorable input | Element found at the beginning: O(1) |
| **Worst case** | Least favorable input | Element absent or at the end: O(n) |
| **Average case** | Average over all inputs | On average in the middle: O(n/2) = O(n) |

!!! warning "Important Convention"

    Unless stated otherwise, when we say "this algorithm is O(n)", we are referring to the **worst case**. This is the most useful guarantee in practice.

### Amortized Complexity

Sometimes, an operation is **usually fast** but **occasionally slow**. **Amortized complexity** averages the cost over a sequence of operations.

??? example "Example: ArrayList.add() in Java"

    * **Normal case**: Adding an element takes O(1)
    * **Rare case**: When the array is full, it is resized (copying n elements) -> O(n)

    **Amortized analysis:**

    If we double the capacity at each resize:

    * After n insertions, we have made copies of size 1, 2, 4, 8, ..., n
    * Total copies: 1 + 2 + 4 + ... + n ≈ 2n
    * Average cost per operation: 2n / n = **O(1) amortized**

    ```
    Insertions:  1   2   3   4   5   6   7   8   9  ...
    Capacity:   [1] [2] [2] [4] [4] [4] [4] [8] [8] ...
                 ↑   ↑       ↑               ↑
             resize resize  resize         resize
    ```

    Even though some operations are O(n), in the long run each operation "costs" O(1) on average.

### How to Determine Complexity?

!!! tip "Practical Method"

    1. **Identify loops**: Each loop multiplies by its number of iterations
    2. **Spot recursive calls**: Draw the recursion tree
    3. **Find the dominant term**: It determines the complexity
    4. **Check for hidden operations**: `list.contains()` is O(n), not O(1)!

??? question "Self-Assessment: What is the Complexity?"

    For each snippet, determine the complexity as a function of n:

    **A)**
    ```java
    for (int i = 0; i < n; i += 2) {
        System.out.println(i);
    }
    ```

    **B)**
    ```java
    for (int i = 1; i < n; i *= 2) {
        System.out.println(i);
    }
    ```

    **C)**
    ```java
    for (int i = 0; i < n; i++) {
        for (int j = i; j < n; j++) {
            System.out.println(i + j);
        }
    }
    ```

    ??? success "Answers"

        **A) O(n)**

        The loop performs n/2 iterations. We ignore the constant 1/2 -> O(n).

        **B) O(log n)**

        i takes the values 1, 2, 4, 8, ..., up to n. That is 2^k = n, so k = log_2(n) iterations.

        **C) O(n²)**

        * i = 0: j performs n iterations
        * i = 1: j performs n-1 iterations
        * ...
        * i = n-1: j performs 1 iteration

        Total: n + (n-1) + ... + 1 = n(n+1)/2 = **O(n²)**

---

## Theoretical Reminders

### The Stack ADT

A **stack** is a collection of elements following the **LIFO** (*Last-In, First-Out*) principle:

* Only the element at the **top** is accessible
* Insertions and removals are done only at the top
* Analogy: a stack of plates or a PEZ dispenser

| Operation | Description | Complexity |
| --- | --- | --- |
| `push(e)` | Adds `e` to the top | O(1) |
| `pop()` | Removes and returns the top | O(1) |
| `top()` | Returns the top without removing it | O(1) |
| `size()` | Returns the number of elements | O(1) |
| `isEmpty()` | Checks if the stack is empty | O(1) |

### The Queue ADT

A **queue** is a collection of elements following the **FIFO** (*First-In, First-Out*) principle:

* Elements are added at the **rear** and removed from the **front**
* Analogy: a checkout line at the supermarket

| Operation | Description | Complexity |
| --- | --- | --- |
| `enqueue(e)` | Adds `e` to the rear | O(1) |
| `dequeue()` | Removes and returns the element at the front | O(1) |
| `first()` | Returns the front without removing it | O(1) |
| `size()` | Returns the number of elements | O(1) |
| `isEmpty()` | Checks if the queue is empty | O(1) |

### The Deque ADT (Double-Ended Queue)

A **deque** (pronounced "deck") allows insertions and removals at **both ends**:

* Generalizes both the stack and the queue
* Can simulate a stack (use one end only) or a queue (enter on one side, exit from the other)

| Operation | Description | Complexity |
| --- | --- | --- |
| `addFirst(e)` | Adds `e` to the front | O(1) |
| `addLast(e)` | Adds `e` to the rear | O(1) |
| `removeFirst()` | Removes and returns the front | O(1) |
| `removeLast()` | Removes and returns the rear | O(1) |
| `first()` / `last()` | Accessors without removal | O(1) |

### Circular Queues

To implement a queue with an array, a **circular approach** is used:

* Two indices: `front` and `rear`
* The indices "wrap around" with the modulo operator: `(index + 1) % capacity`
* Avoids shifting all elements during a `dequeue`

**Circular queue** with `front=2`, `rear=5`, `capacity=8`:

| 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
|   |   | **A** | B | C |   |   |   |
|   |   | ↑front |   |   | ↑rear |   |   |

### Favorites List and Move-to-Front Heuristic

A **favorites list** maintains elements ordered by access frequency:

* `access(e)`: accesses element `e`, increments its counter
* `remove(e)`: removes `e` from the list
* `getFavorites(k)`: returns the `k` most accessed elements

The **move-to-front heuristic** is an alternative:

* On each access, the element is moved to the beginning of the list
* Advantage: recently accessed elements are quickly accessible
* Disadvantage: `getFavorites(k)` becomes O(kn) because the list is no longer sorted by frequency

---

## Part 1 -- Theoretical Exercises

### 1.1 True or False

For each statement, indicate whether it is **true** or **false** and justify your answer.

??? question "Question 1 -- Amortized Complexity"

    A stack implemented with a dynamic array (which doubles its capacity when full) has all its `push` operations in O(1) **amortized**.

    ??? success "Answer"

        **True.** Although the occasional resizing takes O(n), it occurs less and less frequently. Over a sequence of n `push` operations, the total cost is O(n), so the amortized cost per operation is O(1).

        This is the same principle as `ArrayList.add()` in Java.

??? question "Question 2 -- Empty or Full Circular Queue"

    In a circular queue where `front` and `rear` are indices, the condition `front == rear` **always** means the queue is empty.

    ??? success "Answer"

        **False.** This is a classic ambiguity of circular queues! The condition `front == rear` can mean:

        * The queue is **empty** (no elements)
        * The queue is **full** (all slots used)

        Common solutions:

        1. Maintain a separate `size` counter
        2. Never fill the array completely (keep one slot empty)
        3. Use an `isEmpty` boolean

??? question "Question 3 -- Deque as Stack and Queue"

    A Deque can be used to simulate both a stack AND a queue.

    ??? success "Answer"

        **True.** The Deque is a more general structure:

        * **As a stack**: use `addFirst()`/`removeFirst()` (or `addLast()`/`removeLast()`)
        * **As a queue**: use `addLast()`/`removeFirst()`

        This is why Java recommends using `ArrayDeque` rather than `Stack` for new implementations.

??? question "Question 4 -- Move-to-front vs frequency-sorted"

    The move-to-front heuristic **always** guarantees better performance than maintaining a list sorted by access frequency.

    ??? success "Answer"

        **False.** The move-to-front heuristic is a **heuristic**, not a guarantee.

        * **Advantage**: If accesses follow temporal locality (recently accessed elements likely to be accessed again), move-to-front is excellent.
        * **Disadvantage**: For uniformly distributed access sequences, move-to-front can be less efficient because it constantly disrupts the order.

        Moreover, `getFavorites(k)` becomes O(kn) with move-to-front versus O(k) with a sorted list.

??? question "Question 5 -- Stack with Linked List"

    In a stack implemented with a singly linked list, `push` and `pop` operations must be done at the **end** of the list to be O(1).

    ??? success "Answer"

        **False.** It is the opposite! In a **singly** linked list:

        * Insertion/removal at the **beginning**: O(1) -- we have a direct reference to `head`
        * Insertion at the **end**: O(1) if we maintain `tail`, but removal at the end: O(n) -- we need to find the second-to-last node

        Therefore, for an O(1) stack, we use the **beginning** of the linked list as the top.

??? question "Question 6 -- Invalidation after pop"

    Consider the following code:
    ```java
    Stack<String> stack = new ArrayStack<>();
    stack.push("A");
    stack.push("B");
    String x = stack.pop();
    String y = stack.top();
    // At this point, x.equals(y) is true
    ```

    ??? success "Answer"

        **False.** After the operations:

        1. `push("A")` -> stack: `[A]`
        2. `push("B")` -> stack: `[A, B]`
        3. `pop()` -> returns `"B"`, stack: `[A]`
        4. `top()` -> returns `"A"`

        So `x = "B"` and `y = "A"`, and `x.equals(y)` is **false**.

---

### 1.2 Multiple Choice Questions

??? question "Question 7 -- Stack Execution Trace"

    Consider an initially empty stack. The following operations are performed:

    ```
    push(1), push(2), pop(), push(3), push(4), pop(), pop(), push(5)
    ```

    What is the content of the stack after these operations (from bottom to top)?

    - [ ] A) `[1, 5]`
    - [ ] B) `[1, 3, 5]`
    - [ ] C) `[1, 2, 5]`
    - [ ] D) `[5]`

    ??? success "Answer"

        **A) `[1, 5]`**

        Let us trace the operations:

        1. `push(1)` -> `[1]`
        2. `push(2)` -> `[1, 2]`
        3. `pop()` -> returns 2, stack: `[1]`
        4. `push(3)` -> `[1, 3]`
        5. `push(4)` -> `[1, 3, 4]`
        6. `pop()` -> returns 4, stack: `[1, 3]`
        7. `pop()` -> returns 3, stack: `[1]`
        8. `push(5)` -> `[1, 5]`

??? question "Question 8 -- Queue Execution Trace"

    Consider an initially empty queue. The following operations are performed:

    ```
    enqueue(A), enqueue(B), dequeue(), enqueue(C), dequeue(), enqueue(D)
    ```

    What is the content of the queue (from front to rear)?

    - [ ] A) `[A, D]`
    - [ ] B) `[C, D]`
    - [ ] C) `[B, C, D]`
    - [ ] D) `[D]`

    ??? success "Answer"

        **B) `[C, D]`**

        Let us trace the operations:

        1. `enqueue(A)` -> `[A]`
        2. `enqueue(B)` -> `[A, B]`
        3. `dequeue()` -> returns A, queue: `[B]`
        4. `enqueue(C)` -> `[B, C]`
        5. `dequeue()` -> returns B, queue: `[C]`
        6. `enqueue(D)` -> `[C, D]`

??? question "Question 9 -- Circular Queue Indices"

    A circular queue of capacity 6 currently has `front = 4` and contains 3 elements. What is the value of `rear` (the index where the next element will be added)?

    - [ ] A) 0
    - [ ] B) 1
    - [ ] C) 6
    - [ ] D) 7

    ??? success "Answer"

        **B) 1**

        The 3 elements occupy indices 4, 5 and 0 (circular wrap-around because after index 5, we return to 0).

        Calculation: `rear = (front + size) % capacity = (4 + 3) % 6 = 7 % 6 = 1`

        | 0 | 1 | 2 | 3 | 4 | 5 |
        |:-:|:-:|:-:|:-:|:-:|:-:|
        | C |   |   |   | **A** | B |
        | (wrap) | ↑rear |   |   | ↑front |   |

        The logical order of the queue is: A (front) -> B -> C -> [rear = next insertion at index 1]

??? question "Question 10 -- Complexity of getFavorites"

    In a `FavoritesListMTF` (move-to-front) containing n elements, what is the complexity of `getFavorites(k)`?

    - [ ] A) O(k)
    - [ ] B) O(n)
    - [ ] C) O(kn)
    - [ ] D) O(n log n)

    ??? success "Answer"

        **C) O(kn)**

        With move-to-front, the list is **not sorted** by access frequency. To find the k most accessed elements:

        1. We must traverse the entire list to find the maximum -> O(n)
        2. We repeat k times -> O(kn)

        With a list sorted by frequency, it would be O(k) since the first k elements are already the most accessed.

---

### 1.3 Reflection Questions

??? question "Question 11 -- Why a Circular Queue?"

    We want to implement a queue with an array. Compare two approaches:

    **Approach A (naive)**: `front` is always at index 0. After each `dequeue`, all elements are shifted to the left.

    **Approach B (circular)**: We maintain a `front` index that advances with the modulo operator, without shifting elements.

    Explain why approach B is preferable and what the complexity gain is.

    ??? success "Answer"

        **Problem with approach A:**

        After a `dequeue`, all remaining elements must be shifted one position to the left so that index 0 remains the front of the queue. This shifting takes **O(n)** for each removal.

        ```
        Before dequeue: [A, B, C, D, _, _]
        After dequeue:  [B, C, D, _, _, _]  <- shifting 3 elements: O(n)
        ```

        **Advantage of approach B (circular):**

        We simply increment the `front` index (with modulo for wrap-around). No shifting needed -> **O(1)**.

        ```
        Before dequeue: front=0  [A, B, C, D, _, _]
        After dequeue:  front=1  [_, B, C, D, _, _]  <- just front++: O(1)
        ```

        **Gain:** We go from O(n) to O(1) for the `dequeue` operation, which is crucial for a frequently used queue.

??? question "Question 12 -- Stack and Recursion"

    Recursion and stacks are closely related. Explain this connection and give an example of converting a recursive algorithm to an iterative algorithm with a stack.

    ??? success "Answer"

        **The connection:**

        Each recursive call uses the system's **call stack**:

        * Parameters and local variables are pushed
        * Upon return, they are popped

        **Recursive -> iterative conversion:**

        Let us take a depth-first tree traversal (DFS):

        ```java
        // Recursive version
        void dfsRecursive(Node node) {
            if (node == null) return;
            visit(node);
            dfsRecursive(node.left);
            dfsRecursive(node.right);
        }

        // Iterative version with explicit stack
        void dfsIterative(Node root) {
            Stack<Node> stack = new ArrayStack<>();
            stack.push(root);
            while (!stack.isEmpty()) {
                Node node = stack.pop();
                if (node == null) continue;
                visit(node);
                stack.push(node.right);  // right first because LIFO
                stack.push(node.left);
            }
        }
        ```

        **Advantages of the iterative version:** Avoids stack overflows for very deep structures.

??? question "Question 13 -- Move-to-front and Temporal Locality"

    Explain what **temporal locality** is and why the move-to-front heuristic takes advantage of it.

    ??? success "Answer"

        **Temporal locality:**

        The principle that a recently accessed element has a high probability of being accessed again in the near future. Examples:

        * Recently visited web pages
        * Recently opened files
        * Recently used variables (CPU cache)

        **Move-to-front takes advantage of it:**

        By moving each accessed element to the beginning of the list:

        * "Hot" elements (frequently accessed recently) are near the beginning
        * Future searches for these elements are fast (O(1) to O(k) for the first k)
        * "Cold" elements naturally migrate toward the end

        **Analogy:** This is similar to the LRU (Least Recently Used) cache used in operating systems and databases.

??? question "Question 14 -- Infix, Prefix, and Postfix Notations"

    Arithmetic expressions can be written in three notations:

    | Notation | Example for (3 + 4) × 5 |
    |----------|--------------------------|
    | **Infix** (standard) | `(3 + 4) * 5` |
    | **Prefix** (Polish) | `* + 3 4 5` |
    | **Postfix** (reverse Polish) | `3 4 + 5 *` |

    1. Why do prefix and postfix notations not need parentheses?
    2. What data structure is used to evaluate a postfix expression? Why?
    3. What structure would be appropriate for converting an infix expression to postfix?

    ??? success "Answer"

        **1. No need for parentheses:**

        In prefix/postfix notation, the position of the operator relative to the operands determines **unambiguously** the order of operations. There is no need for parentheses or precedence rules.

        * Infix: `3 + 4 * 5` -> ambiguous without rules (is it 35 or 23?)
        * Postfix: `3 4 5 * +` = 3 + (4×5) = 23
        * Postfix: `3 4 + 5 *` = (3+4) × 5 = 35

        **2. Postfix evaluation -> Stack**

        A **stack** is used because operands are processed in LIFO order:

        ```
        Expression: 3 4 + 5 *

        Token   | Action              | Stack
        --------|---------------------|--------
        3       | push(3)             | [3]
        4       | push(4)             | [3, 4]
        +       | pop 4, pop 3        | []
                | push(3+4=7)         | [7]
        5       | push(5)             | [7, 5]
        *       | pop 5, pop 7        | []
                | push(7*5=35)        | [35]

        Result: 35
        ```

        The two most recently pushed operands are the ones that need to be combined -> perfect LIFO behavior for the stack.

        **3. Infix to postfix conversion -> Stack (Shunting-yard algorithm)**

        A **stack** (for operators) is also used in Dijkstra's algorithm:

        * Operands go directly to the output
        * Operators are pushed, then popped according to their precedence
        * Opening parentheses are pushed, closing parentheses trigger popping

        The stack allows us to "hold" low-precedence operators until the high-precedence ones have been processed.

---

## Part 2 -- Stack Implementation

### 2.1 Understanding the Structure

A stack can be implemented in two main ways:

**Array-based stack** (top on the right, `t=3`):

| Index | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Value | A | B | C | **D (top)** |  |  |  |  |

**Linked list stack** (top = head):

<div class="ift-diagram" role="img" aria-label="Pile avec liste chainee: top, D, C, B, A, null">
  <div class="ift-diagram__node ift-diagram__node--label">top</div>
  <span class="ift-diagram__arrow" aria-hidden="true"></span>
  <div class="ift-diagram__node ift-diagram__node--top">D</div>
  <span class="ift-diagram__arrow" aria-hidden="true"></span>
  <div class="ift-diagram__node">C</div>
  <span class="ift-diagram__arrow" aria-hidden="true"></span>
  <div class="ift-diagram__node">B</div>
  <span class="ift-diagram__arrow" aria-hidden="true"></span>
  <div class="ift-diagram__node">A</div>
  <span class="ift-diagram__arrow" aria-hidden="true"></span>
  <div class="ift-diagram__node ift-diagram__node--null">null</div>
</div>

Here is the stack interface:

```java
public interface Stack<E> {
    int size();
    boolean isEmpty();
    void push(E e);
    E top();
    E pop();
}
```

---

### 2.2 Array-based Implementation

??? example "Exercise 2.2.1 -- ArrayStack"

    Implement a stack with a fixed-capacity array.

    ```java
    public class ArrayStack<E> implements Stack<E> {
        public static final int CAPACITY = 1000;
        private E[] data;
        private int t = -1;  // index of top (-1 = empty)

        // To implement:
        // Constructor, size(), isEmpty(), push(e), top(), pop()
    }
    ```

    ??? success "Solution"

        ```java
        public class ArrayStack<E> implements Stack<E> {
            public static final int CAPACITY = 1000;
            private E[] data;
            private int t = -1;

            public ArrayStack() {
                this(CAPACITY);
            }

            @SuppressWarnings("unchecked")
            public ArrayStack(int capacity) {
                data = (E[]) new Object[capacity];
            }

            public int size() {
                return t + 1;
            }

            public boolean isEmpty() {
                return t == -1;
            }

            public void push(E e) throws IllegalStateException {
                if (size() == data.length)
                    throw new IllegalStateException("Stack is full");
                data[++t] = e;  // increments t THEN stores
            }

            public E top() {
                if (isEmpty()) return null;
                return data[t];
            }

            public E pop() {
                if (isEmpty()) return null;
                E answer = data[t];
                data[t] = null;  // helps the garbage collector
                t--;
                return answer;
            }
        }
        ```

        **Key points:**

        * `++t` in `push`: increments first, then uses the new value
        * `data[t] = null` in `pop`: avoids memory leaks (stale references)

---

### 2.3 Linked List Implementation

??? example "Exercise 2.3.1 -- LinkedStack"

    Implement a stack with a singly linked list. The top is the head of the list.

    ```java
    public class LinkedStack<E> implements Stack<E> {

        private static class Node<E> {
            private E element;
            private Node<E> next;

            public Node(E e, Node<E> n) {
                element = e;
                next = n;
            }
            // getters...
        }

        private Node<E> top = null;
        private int size = 0;

        // To implement...
    }
    ```

    ??? success "Solution"

        ```java
        public class LinkedStack<E> implements Stack<E> {

            private static class Node<E> {
                private E element;
                private Node<E> next;

                public Node(E e, Node<E> n) {
                    element = e;
                    next = n;
                }

                public E getElement() { return element; }
                public Node<E> getNext() { return next; }
            }

            private Node<E> top = null;
            private int size = 0;

            public int size() { return size; }

            public boolean isEmpty() { return size == 0; }

            public void push(E e) {
                top = new Node<>(e, top);  // new node points to the old top
                size++;
            }

            public E top() {
                if (isEmpty()) return null;
                return top.getElement();
            }

            public E pop() {
                if (isEmpty()) return null;
                E answer = top.getElement();
                top = top.getNext();  // the top becomes the next node
                size--;
                return answer;
            }
        }
        ```

        **Advantage over ArrayStack:** No capacity limit, no resizing needed.

---

## Part 3 -- Queue Implementation

### 3.1 Understanding the Circular Queue

**Circular queue of capacity 8 with 4 elements:**

| 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
|   |   | **A** | B | C | D |   |   |
|   |   | ↑front |   |   |   | ↑rear |   |

**After `enqueue(E)`:**

| 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
|   |   | **A** | B | C | D | E |   |
|   |   | ↑front |   |   |   |   | ↑rear |

**After `dequeue()` (returns A):**

| 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
|   |   |   | **B** | C | D | E |   |
|   |   |   | ↑front |   |   |   | ↑rear |

The queue interface:

```java
public interface Queue<E> {
    int size();
    boolean isEmpty();
    void enqueue(E e);
    E first();
    E dequeue();
}
```

---

### 3.2 Circular Array Implementation

??? example "Exercise 3.2.1 -- ArrayQueue"

    Implement a queue with a circular array. Use the modulo operator for wrap-around.

    ```java
    public class ArrayQueue<E> implements Queue<E> {
        public static final int CAPACITY = 1000;
        private E[] data;
        private int front = 0;
        private int size = 0;

        // To implement...
        // Note: rear = (front + size) % data.length
    }
    ```

    ??? success "Solution"

        ```java
        public class ArrayQueue<E> implements Queue<E> {
            public static final int CAPACITY = 1000;
            private E[] data;
            private int front = 0;
            private int size = 0;

            public ArrayQueue() {
                this(CAPACITY);
            }

            @SuppressWarnings("unchecked")
            public ArrayQueue(int capacity) {
                data = (E[]) new Object[capacity];
            }

            public int size() { return size; }

            public boolean isEmpty() { return size == 0; }

            public void enqueue(E e) throws IllegalStateException {
                if (size == data.length)
                    throw new IllegalStateException("Queue is full");
                int rear = (front + size) % data.length;
                data[rear] = e;
                size++;
            }

            public E first() {
                if (isEmpty()) return null;
                return data[front];
            }

            public E dequeue() {
                if (isEmpty()) return null;
                E answer = data[front];
                data[front] = null;  // helps GC
                front = (front + 1) % data.length;  // advances circularly
                size--;
                return answer;
            }
        }
        ```

        **Key points:**

        * We maintain `size` rather than `rear` to avoid the empty/full ambiguity
        * `(front + 1) % data.length` ensures circular wrap-around

---

### 3.3 Linked List Implementation

??? example "Exercise 3.3.1 -- LinkedQueue"

    Implement a queue with a singly linked list. Maintain a reference to `head` (front) and `tail` (rear).

    ??? success "Solution"

        ```java
        public class LinkedQueue<E> implements Queue<E> {

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

            private Node<E> head = null;
            private Node<E> tail = null;
            private int size = 0;

            public int size() { return size; }

            public boolean isEmpty() { return size == 0; }

            public void enqueue(E e) {
                Node<E> newest = new Node<>(e, null);
                if (isEmpty())
                    head = newest;
                else
                    tail.setNext(newest);
                tail = newest;
                size++;
            }

            public E first() {
                if (isEmpty()) return null;
                return head.getElement();
            }

            public E dequeue() {
                if (isEmpty()) return null;
                E answer = head.getElement();
                head = head.getNext();
                size--;
                if (isEmpty())
                    tail = null;  // the queue is now empty
                return answer;
            }
        }
        ```

        **Watch out for the edge case:** When the queue becomes empty after a `dequeue`, `tail` must also be set to `null`.

---

## Part 4 -- Deque Implementation

### 4.1 Interface and Structure

```java
public interface Deque<E> {
    int size();
    boolean isEmpty();
    E first();
    E last();
    void addFirst(E e);
    void addLast(E e);
    E removeFirst();
    E removeLast();
}
```

A Deque can be implemented with:

* A **circular array** (similar to `ArrayQueue` but with addition/removal at both ends)
* A **doubly linked list** (O(1) access at both ends)

??? example "Exercise 4.1.1 -- DoublyLinkedDeque"

    Implement a Deque with a doubly linked list with sentinels.

    **Hint:** Reuse the structure of the positional list from Demo 2, but expose only the operations at the extremities.

    ??? success "Solution"

        ```java
        public class LinkedDeque<E> implements Deque<E> {

            private static class Node<E> {
                private E element;
                private Node<E> prev;
                private Node<E> next;

                public Node(E e, Node<E> p, Node<E> n) {
                    element = e;
                    prev = p;
                    next = n;
                }
                // getters and setters...
            }

            private Node<E> header;
            private Node<E> trailer;
            private int size = 0;

            public LinkedDeque() {
                header = new Node<>(null, null, null);
                trailer = new Node<>(null, header, null);
                header.next = trailer;
            }

            public int size() { return size; }
            public boolean isEmpty() { return size == 0; }

            public E first() {
                if (isEmpty()) return null;
                return header.next.element;
            }

            public E last() {
                if (isEmpty()) return null;
                return trailer.prev.element;
            }

            // Private utility method
            private void addBetween(E e, Node<E> pred, Node<E> succ) {
                Node<E> newest = new Node<>(e, pred, succ);
                pred.next = newest;
                succ.prev = newest;
                size++;
            }

            public void addFirst(E e) {
                addBetween(e, header, header.next);
            }

            public void addLast(E e) {
                addBetween(e, trailer.prev, trailer);
            }

            // Private utility method
            private E remove(Node<E> node) {
                Node<E> pred = node.prev;
                Node<E> succ = node.next;
                pred.next = succ;
                succ.prev = pred;
                size--;
                return node.element;
            }

            public E removeFirst() {
                if (isEmpty()) return null;
                return remove(header.next);
            }

            public E removeLast() {
                if (isEmpty()) return null;
                return remove(trailer.prev);
            }
        }
        ```

---

## Part 5 -- Favorites List

### 5.1 Understanding the Structure

A favorites list maintains elements with their **access counter**:

**FavoritesList** after `access(A), access(B), access(A), access(C), access(A)`:

<div class="ift-diagram" role="img" aria-label="FavoritesList: A count 3, B count 1, C count 1">
  <div class="ift-diagram__node ift-diagram__node--head">
    <div class="ift-diagram__title">A</div>
    <div class="ift-diagram__meta">count = 3 *</div>
  </div>
  <span class="ift-diagram__arrow" aria-hidden="true"></span>
  <div class="ift-diagram__node">
    <div class="ift-diagram__title">B</div>
    <div class="ift-diagram__meta">count = 1</div>
  </div>
  <span class="ift-diagram__arrow" aria-hidden="true"></span>
  <div class="ift-diagram__node">
    <div class="ift-diagram__title">C</div>
    <div class="ift-diagram__meta">count = 1</div>
  </div>
</div>

*Element A is the most accessed (3 accesses) and is at the head of the list.*

### 5.2 Basic Implementation

??? example "Exercise 5.2.1 -- Inner Item Class"

    Create an inner `Item` class that stores an element and its access counter.

    ??? success "Solution"

        ```java
        protected static class Item<E> {
            private E value;
            private int count = 0;

            public Item(E val) {
                value = val;
            }

            public int getCount() { return count; }
            public E getValue() { return value; }
            public void increment() { count++; }
        }
        ```

??? example "Exercise 5.2.2 -- access(e) Method"

    Implement the `access(E e)` method which:

    1. Searches for the element in the list
    2. If it exists, increments its counter
    3. Otherwise, adds it with a counter of 1
    4. Moves the element to maintain descending counter order

    ??? success "Solution"

        ```java
        public class FavoritesList<E> {
            protected PositionalList<Item<E>> list = new LinkedPositionalList<>();

            protected Position<Item<E>> findPosition(E e) {
                Position<Item<E>> walk = list.first();
                while (walk != null && !walk.getElement().getValue().equals(e))
                    walk = list.after(walk);
                return walk;
            }

            protected void moveUp(Position<Item<E>> p) {
                int cnt = p.getElement().getCount();
                Position<Item<E>> walk = p;
                while (walk != list.first() &&
                       list.before(walk).getElement().getCount() < cnt)
                    walk = list.before(walk);
                if (walk != p)
                    list.addBefore(walk, list.remove(p));
            }

            public void access(E e) {
                Position<Item<E>> p = findPosition(e);
                if (p == null)
                    p = list.addLast(new Item<E>(e));
                p.getElement().increment();
                moveUp(p);
            }
        }
        ```

??? example "Exercise 5.2.3 -- getFavorites(k) Method"

    Implement `getFavorites(int k)` which returns the k most accessed elements.

    ??? success "Solution"

        ```java
        public Iterable<E> getFavorites(int k) throws IllegalArgumentException {
            if (k < 0 || k > size())
                throw new IllegalArgumentException("Invalid k");

            List<E> result = new ArrayList<>();
            Iterator<Item<E>> iter = list.iterator();
            for (int i = 0; i < k; i++) {
                result.add(iter.next().getValue());
            }
            return result;
        }
        ```

        Since the list is sorted in descending order of counters, the first k elements are the favorites!

---

### 5.3 Move-to-front heuristic

??? example "Exercise 5.3.1 -- FavoritesListMTF"

    Create a subclass `FavoritesListMTF` that overrides `moveUp` to move the element to the beginning rather than maintaining sorted order.

    ??? success "Solution"

        ```java
        public class FavoritesListMTF<E> extends FavoritesList<E> {

            @Override
            protected void moveUp(Position<Item<E>> p) {
                if (p != list.first())
                    list.addFirst(list.remove(p));
            }

            @Override
            public Iterable<E> getFavorites(int k) throws IllegalArgumentException {
                if (k < 0 || k > size())
                    throw new IllegalArgumentException("Invalid k");

                // Copy into a temporary list
                PositionalList<Item<E>> temp = new LinkedPositionalList<>();
                for (Item<E> item : list)
                    temp.addLast(item);

                // Find the k maximums
                List<E> result = new ArrayList<>();
                for (int i = 0; i < k; i++) {
                    Position<Item<E>> maxPos = temp.first();
                    for (Position<Item<E>> walk = temp.after(maxPos);
                         walk != null;
                         walk = temp.after(walk)) {
                        if (walk.getElement().getCount() > maxPos.getElement().getCount())
                            maxPos = walk;
                    }
                    result.add(maxPos.getElement().getValue());
                    temp.remove(maxPos);
                }
                return result;
            }
        }
        ```

        **Complexity of getFavorites(k):** O(kn) -- we traverse the list k times to find each maximum.

---

## Part 6 -- Queues and Concurrency (Introduction)

### 6.1 The Problem

When multiple **threads** access a shared queue, problems arise:

```java
// Thread 1                    // Thread 2
queue.enqueue("A");            queue.enqueue("B");
```

Without synchronization, both threads can modify `size` or `rear` simultaneously, corrupting the structure.

### 6.2 Race Conditions

??? example "Exercise 6.2.1 -- Identifying the Problem"

    Consider this simplified implementation of `enqueue`:

    ```java
    public void enqueue(E e) {
        int rear = (front + size) % data.length;  // Line 1
        data[rear] = e;                            // Line 2
        size++;                                    // Line 3
    }
    ```

    If two threads T1 and T2 call `enqueue` simultaneously with `size = 5` and `front = 0`, what can happen?

    ??? success "Answer"

        **Problematic scenario:**

        1. T1 executes Line 1: `rear = 5`
        2. T2 executes Line 1: `rear = 5` (same value because `size` has not changed!)
        3. T1 executes Line 2: `data[5] = "A"`
        4. T2 executes Line 2: `data[5] = "B"` (overwrites "A"!)
        5. T1 executes Line 3: `size = 6`
        6. T2 executes Line 3: `size = 7`

        **Result:** "A" is lost, and `size` is incorrectly incremented twice while only one element was effectively added to a valid position.

---

### 6.3 Solutions in Java

#### Synchronized

```java
public synchronized void enqueue(E e) {
    // Only one thread can execute this block at a time
    int rear = (front + size) % data.length;
    data[rear] = e;
    size++;
}

public synchronized E dequeue() {
    // ...
}
```

#### BlockingQueue

Java provides thread-safe queues in `java.util.concurrent`:

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);

// Producer thread
queue.put("item");  // Blocks if full

// Consumer thread
String item = queue.take();  // Blocks if empty
```

??? example "Exercise 6.3.1 -- Producer-Consumer"

    Complete this producer-consumer simulation program:

    ```java
    public class ProducerConsumer {
        public static void main(String[] args) {
            BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

            // Producer: adds the numbers 1 to 10
            Thread producer = new Thread(() -> {
                // To complete
            });

            // Consumer: removes and prints the elements
            Thread consumer = new Thread(() -> {
                // To complete
            });

            producer.start();
            consumer.start();
        }
    }
    ```

    ??? success "Solution"

        ```java
        public class ProducerConsumer {
            public static void main(String[] args) {
                BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

                Thread producer = new Thread(() -> {
                    try {
                        for (int i = 1; i <= 10; i++) {
                            System.out.println("Producing: " + i);
                            queue.put(i);  // Blocks if the queue is full
                            Thread.sleep(100);  // Simulates work
                        }
                        queue.put(-1);  // End signal
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

                Thread consumer = new Thread(() -> {
                    try {
                        while (true) {
                            Integer item = queue.take();  // Blocks if empty
                            if (item == -1) break;  // End signal
                            System.out.println("Consuming: " + item);
                            Thread.sleep(150);  // Consumer is slower
                        }
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

                producer.start();
                consumer.start();
            }
        }
        ```

        **Observation:** The producer will sometimes be blocked because the queue (capacity 5) fills up faster than it drains.

---

## Practical Applications

### Application 1: Parenthesis Matching

??? example "Exercise -- Balanced Parentheses"

    Implement a method that checks whether a string contains properly balanced parentheses.

    Examples:

    * `"((()))"` -> true
    * `"({[]})"` -> true
    * `"(()"` -> false
    * `"([)]"` -> false

    ??? success "Solution"

        ```java
        public static boolean isMatched(String expression) {
            final String opening = "({[";
            final String closing = ")}]";
            Stack<Character> buffer = new ArrayStack<>();

            for (char c : expression.toCharArray()) {
                if (opening.indexOf(c) != -1) {
                    buffer.push(c);
                } else if (closing.indexOf(c) != -1) {
                    if (buffer.isEmpty()) return false;
                    if (closing.indexOf(c) != opening.indexOf(buffer.pop()))
                        return false;
                }
            }
            return buffer.isEmpty();
        }
        ```

---

### Application 2: Postfix Expression Evaluation

??? example "Exercise -- Postfix Calculator"

    Evaluate an expression in postfix notation (reverse Polish notation).

    Example: `"3 4 + 5 *"` = (3 + 4) × 5 = 35

    ??? success "Solution"

        ```java
        public static int evaluatePostfix(String expr) {
            Stack<Integer> stack = new ArrayStack<>();
            String[] tokens = expr.split(" ");

            for (String token : tokens) {
                if (token.matches("-?\\d+")) {
                    stack.push(Integer.parseInt(token));
                } else {
                    int b = stack.pop();
                    int a = stack.pop();
                    switch (token) {
                        case "+": stack.push(a + b); break;
                        case "-": stack.push(a - b); break;
                        case "*": stack.push(a * b); break;
                        case "/": stack.push(a / b); break;
                    }
                }
            }
            return stack.pop();
        }
        ```

---

### Application 3: Round-Robin Simulation

??? example "Exercise -- Process Scheduler"

    Simulate a round-robin scheduler where each process receives a fixed time quantum.

    ??? success "Solution"

        ```java
        public static void roundRobinScheduler(String[] processes, int quantum) {
            Queue<String> queue = new ArrayQueue<>();
            int[] remainingTime = {10, 5, 8};  // Remaining time for each process

            for (String p : processes)
                queue.enqueue(p);

            int time = 0;
            while (!queue.isEmpty()) {
                String current = queue.dequeue();
                int idx = current.charAt(1) - '1';  // P1 → 0, P2 → 1, etc.

                int executeTime = Math.min(quantum, remainingTime[idx]);
                time += executeTime;
                remainingTime[idx] -= executeTime;

                System.out.println("Time " + time + ": " + current +
                                   " executed for " + executeTime + "ms");

                if (remainingTime[idx] > 0) {
                    queue.enqueue(current);  // Put back in the queue
                } else {
                    System.out.println(current + " completed!");
                }
            }
        }
        ```

---

## Additional Exercises

??? tip "Challenge 1 -- Implement a Stack with Two Queues"

    Implement a stack using only two queues. Analyze the complexity.

    ??? success "Solution"

        ```java
        public class StackWithQueues<E> implements Stack<E> {
            private Queue<E> q1 = new LinkedQueue<>();
            private Queue<E> q2 = new LinkedQueue<>();

            public void push(E e) {
                q1.enqueue(e);
            }

            public E pop() {
                if (q1.isEmpty()) return null;

                // Move everything except the last element to q2
                while (q1.size() > 1) {
                    q2.enqueue(q1.dequeue());
                }
                E result = q1.dequeue();

                // Swap q1 and q2
                Queue<E> temp = q1;
                q1 = q2;
                q2 = temp;

                return result;
            }

            // ... other methods
        }
        ```

        **Complexity:** `push` = O(1), `pop` = O(n)

??? tip "Challenge 2 -- Detect a Palindrome with a Deque"

    Use a Deque to check whether a string is a palindrome.

    ??? success "Solution"

        ```java
        public static boolean isPalindrome(String s) {
            Deque<Character> deque = new LinkedDeque<>();

            // Add characters (ignoring spaces and case)
            for (char c : s.toLowerCase().toCharArray()) {
                if (Character.isLetterOrDigit(c))
                    deque.addLast(c);
            }

            // Compare from both sides
            while (deque.size() > 1) {
                if (!deque.removeFirst().equals(deque.removeLast()))
                    return false;
            }
            return true;
        }
        ```

??? tip "Challenge 3 -- MinStack"

    Implement a stack that supports `getMin()` in O(1) in addition to the standard operations.

    ??? success "Solution"

        ```java
        public class MinStack {
            private Stack<Integer> stack = new ArrayStack<>();
            private Stack<Integer> minStack = new ArrayStack<>();

            public void push(int x) {
                stack.push(x);
                if (minStack.isEmpty() || x <= minStack.top())
                    minStack.push(x);
            }

            public int pop() {
                int val = stack.pop();
                if (val == minStack.top())
                    minStack.pop();
                return val;
            }

            public int top() {
                return stack.top();
            }

            public int getMin() {
                return minStack.top();
            }
        }
        ```

        **Idea:** Maintain an auxiliary stack that stores the current minimums.

---

## Summary

| Structure | Policy | push/enqueue | pop/dequeue | Access other end | Typical use case |
| --- | --- | --- | --- | --- | --- |
| **Stack** | LIFO | O(1) | O(1) | ✗ | Undo, parsing, DFS |
| **Queue** | FIFO | O(1) | O(1) | ✗ | BFS, scheduling, buffers |
| **Deque** | Both | O(1) | O(1) | O(1) | Undo/Redo, sliding window |
| **FavoritesList** | By frequency | O(n) | O(n) | O(k) | Recommendations |
| **FavoritesListMTF** | Move-to-front | O(n) | O(n) | O(kn) | Adaptive cache |

!!! success "Key Takeaways"

    1. **LIFO vs FIFO**: Choose the stack for reverse order, the queue for arrival order.
    2. **Circular queues**: Avoid the O(n) shift by using modular arithmetic.
    3. **Deque**: Versatile structure that generalizes both stack and queue.
    4. **Move-to-front**: Effective heuristic when there is temporal locality.
    5. **Concurrency**: Always synchronize access to structures shared between threads.

---

## References

* Goodrich, Tamassia, Goldwasser. *Data Structures and Algorithms in Java*, 6th Edition.
    * Chapter 6: Stacks, Queues, and Deques
    * Section 7.7: The Favorites List ADT
* Java Documentation: [`java.util.Deque`](https://docs.oracle.com/javase/8/docs/api/java/util/Deque.html), [`java.util.concurrent.BlockingQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html)
