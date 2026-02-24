# Demo 5: Midterm Review

This demo is a **comprehensive review** covering Chapters 6, 7, 9, and Sections 14.1–14.2 of *Data Structures and Algorithms in Java (6th ed.)*.

!!! abstract "Review Objectives"

    By the end of this demo, you should be able to:

    * **Distinguish** the different data structures and their trade-offs
    * **Choose** the appropriate structure based on context and dominant operations
    * **Analyze** the time and space complexity of operations
    * **Identify** classic pitfalls and common misconceptions
    * **Apply** your knowledge to realistic scenarios
    * **Compare** graph representations based on density and operations

---

## Graph Resources (Chapter 14)

Java implementations of various graph representations, with a detailed guide for understanding design choices and trade-offs:

* [Detailed implementation guide](demo5-graphes-implementation.md)
* [Edge list (undirected) — `EdgeListGraph.java`](../files/code/graphs/src/edgelist/EdgeListGraph.java)
* [Adjacency list (undirected) — `AdjacencyListGraph.java`](../files/code/graphs/src/adjlist/AdjacencyListGraph.java)
* [Adjacency matrix (undirected) — `AdjacencyMatrixGraph.java`](../files/code/graphs/src/matrix/AdjacencyMatrixGraph.java)
* [Directed edge list — `DirectedEdgeListGraph.java`](../files/code/graphs/src/edgelist/DirectedEdgeListGraph.java)
* [Directed adjacency list — `DirectedAdjacencyListGraph.java`](../files/code/graphs/src/adjlist/DirectedAdjacencyListGraph.java)
* [Directed adjacency matrix — `DirectedAdjacencyMatrixGraph.java`](../files/code/graphs/src/matrix/DirectedAdjacencyMatrixGraph.java)

---

## Topics Covered

| Topic | Content | Chapters |
|-------|---------|----------|
| **Introduction** | Structure = algorithmic choice, invariants, hidden costs | — |
| **Lists** | ArrayList, LinkedList, positional lists | 7.1, 7.2, 7.3-7.6 |
| **Favorites list** | Frequency sorting, move-to-front heuristic | 7.7 |
| **Stacks, Queues, Deques** | LIFO, FIFO, implementations, circular queues | 6 |
| **Priority Queues** | Heaps, properties, adaptable priorities | 9 |
| **Graphs** | Definitions, ADT, representations | 14.1, 14.2 |

---

## First Hour: Concepts and Review

### Section 1 — Thematic True/False

For each statement, indicate whether it is **true** or **false** and justify your answer.

#### Block A — Foundations and Philosophy

??? question "Question 1 — Complexity and code length"
    An algorithm with more lines of code is necessarily slower than a shorter algorithm solving the same problem.

    ??? success "Answer"
        **False.** Algorithmic complexity depends on the **number of operations as a function of input size**, not the number of lines of code.

        Example: Insertion sort (short code) is O(n²), while merge sort (longer code) is O(n log n). For large inputs, the longer code is much faster.

        **Classic pitfall:** Thinking that "more code = slower". Asymptotic complexity takes precedence over code length.

??? question "Question 2 — Cost of invariants"
    Maintaining an invariant (for example, keeping a list sorted) always has a negligible cost compared to the benefits it provides.

    ??? success "Answer"
        **False.** The maintenance cost of an invariant can be significant and must be weighed against the benefits.

        Example: A sorted list allows `min()` in O(1), but each insertion becomes O(n) to maintain the order. If insertions are frequent and minimum queries are rare, this cost is prohibitive.

        **The right choice depends on the dominant operations.** This is why we compare:

        | Structure | `insert` | `min` | When to use |
        |-----------|----------|-------|-------------|
        | Unsorted list | O(1) | O(n) | Few queries |
        | Sorted list | O(n) | O(1) | Few insertions |
        | Heap | O(log n) | O(1) | Balanced use |

??? question "Question 3 — Amortized complexity"
    Amortized O(1) complexity guarantees that every individual operation takes constant time.

    ??? success "Answer"
        **False.** **Amortized** O(1) complexity means that over a long sequence of n operations, the total cost is O(n), so **on average** each operation costs O(1).

        However, some individual operations may take O(n). For example, `ArrayList.add()`:

        - Most additions: O(1)
        - When the array is full: O(n) to resize and copy

        **Classic pitfall:** Confusing amortized complexity with worst-case complexity. Amortized is an average, not a per-operation guarantee.

---

#### Block B — Linear Structures

??? question "Question 4 — LinkedList and insertions"
    For a list of 10,000 elements with frequent insertions in the middle, `LinkedList` is always more efficient than `ArrayList`.

    ??? success "Answer"
        **False.** This claim ignores a crucial cost: **finding the insertion position**.

        To insert in the middle:

        | Structure | Find position | Insert | Total |
        |-----------|---------------|--------|-------|
        | ArrayList | O(1) by index | O(n) shift | O(n) |
        | LinkedList | O(n) traversal | O(1) relink | O(n) |

        Both are O(n)! Moreover, `ArrayList` benefits from better **cache locality** (contiguous elements in memory), which often makes it faster in practice.

        **LinkedList is only advantageous if you already have a reference to the position** (via an iterator or a position in a positional list).

??? question "Question 5 — Optimization of get(i) in LinkedList"
    In a Java `LinkedList`, the call `list.get(n/2)` is optimized to start from the middle of the list.

    ??? success "Answer"
        **False.** Java optimizes by starting from the **beginning or end** depending on the index:

        - If `i < size/2`: traverses from the beginning
        - If `i >= size/2`: traverses from the end

        But there is **no direct access to the middle**. For `get(n/2)`, Java traverses approximately n/2 elements from one end.

        ```java
        // Complexity of get(i) in LinkedList
        // Best case: O(1) for i=0 or i=size-1
        // Worst case: O(n/2) = O(n) for i≈n/2
        ```

        **Classic pitfall:** Underestimating the cost of `get(i)` in a linked list. It is always O(n) in the worst case, never O(1).

??? question "Question 6 — Stability of positions"
    A `Position` in a positional list becomes invalid if an element is inserted just before it.

    ??? success "Answer"
        **False.** This is precisely the advantage of positions over indices!

        - **Index**: If I insert before index 5, the element that was at index 5 is now at index 6. Index 5 points to a different element.

        - **Position**: A position represents a **stable** location in the structure. Inserting other elements does not affect it.

        A position becomes invalid **only** when **its own element** is removed from the list.

        **Classic pitfall:** Confusing position and index. A position is a stable reference to the container (node), not a numeric rank.

??? question "Question 7 — Positions in Java collections"
    Java does not expose positions in its standard collections because it is a design oversight that will be corrected in a future version.

    ??? success "Answer"
        **False.** It is a **deliberate design choice** to protect invariants.

        If Java exposed internal nodes (`Position`), a user could:

        - Keep a reference to a removed node
        - Directly modify `next`/`prev` links
        - Corrupt the data structure

        Java prefers to expose **iterators** (which are invalidated after modification) rather than persistent positions. This is a trade-off between flexibility and safety.

        Positional lists are useful when you **control** the environment and need O(1) performance for insertions/deletions at known positions.

??? question "Question 8 — Java's Stack class"
    The `java.util.Stack` class is recommended for implementing a stack in modern code.

    ??? success "Answer"
        **False.** `java.util.Stack` is a **legacy** class (inherited from Java 1.0) that has several problems:

        1. It inherits from `Vector`, a legacy API not centered on LIFO semantics
        2. It exposes `Vector` methods that violate the LIFO principle (`add(index, element)`, `remove(index)`)

        **Official recommendation** (Javadoc):

        ```java
        // Avoid
        Stack<String> stack = new Stack<>();

        // Recommended
        Deque<String> stack = new ArrayDeque<>();
        stack.push("A");
        stack.pop();
        ```

        `ArrayDeque` is faster and strictly respects the stack interface.

??? question "Question 9 — Deque as a universal structure"
    A `Deque` (double-ended queue) can efficiently simulate both a `Stack` and a `Queue`.

    ??? success "Answer"
        **True.** A `Deque` allows insertions and deletions at **both ends** in O(1), which is sufficient to implement both structures:

        | Structure | Operation | Implementation with Deque |
        |-----------|-----------|---------------------------|
        | **Stack** (LIFO) | `push(e)` | `addFirst(e)` |
        | | `pop()` | `removeFirst()` |
        | **Queue** (FIFO) | `enqueue(e)` | `addLast(e)` |
        | | `dequeue()` | `removeFirst()` |

        All these operations are **O(1)** with an `ArrayDeque`.

        This is also why the Java Javadoc recommends `ArrayDeque` for both stacks (`push`/`pop`) and queues (`offer`/`poll`):

        ```java
        Deque<String> stack = new ArrayDeque<>();  // As a stack
        Deque<String> queue = new ArrayDeque<>();  // As a queue
        ```

        **Key point:** The `Deque` is a **generalization** of the stack and queue. If a structure allows operations at both ends, it can be restricted to one end (stack) or use one end for input and the other for output (queue).

---

#### Block C — Queues and Priorities

??? question "Question 10 — Position of the 3rd smallest in a min-heap"
    In a min-heap of n elements (n ≥ 7), **with no repeated keys/priorities**, the 3rd smallest element necessarily resides at depth 1 (i.e., it is a child of the root).

    ??? success "Answer"
        **False.** The 3rd smallest element can reside at depth 1 **or** at depth 2.

        (Assumption used: all priorities are distinct, as stated in the question.)

        The heap invariant guarantees that each parent ≤ its children. Therefore:

        - The **1st minimum** is at the root (depth 0)
        - The **2nd minimum** is one of the root's children (depth 1)
        - The **3rd minimum** can be:
            - The **other child** of the root (depth 1), **or**
            - A **grandchild** of the root (depth 2), if both children of the root are large

        ```
        Example where the 3rd minimum is at depth 2:

              1               Array: [1, 10, 5, 20, 15, 2, 8]
            /   \
          10     5
         / \   / \
        20 15 2   8    ← 2 is the 3rd smallest, at depth 2!
        ```

        **General rule:** The k-th smallest element in a min-heap can reside at any depth from 0 to k−1.

        **Classic pitfall:** Believing that a heap is "almost sorted". The only guarantee is parent ≤ children; there is no relationship between siblings.

??? question "Question 11 — Preorder traversal of a min-heap"
    A preorder traversal of a min-heap always lists the keys in non-decreasing order.

    ??? success "Answer"
        **False.** A preorder traversal visits: root, then left subtree, then right subtree. The heap property (parent ≤ children) does **not** guarantee non-decreasing order during a preorder traversal.

        Counterexample:

        ```
              1
            /   \
           3     2
          / \
         5   4

        Preorder traversal: 1, 3, 5, 4, 2
        ```

        The resulting order is 1, 3, 5, 4, **2** — which is not non-decreasing because 2 < 4 and 2 < 5.

        **Why?** A heap only guarantees that each parent ≤ its children (a **vertical** relationship). There is no guarantee between:

        - siblings (3 vs 2)
        - nodes in different subtrees (5 vs 2)

        It is a **BST** (binary search tree) that would yield a sorted inorder traversal, not a heap with a preorder traversal.

        **Classic pitfall:** Confusing the heap property with that of a BST. A heap is **not** sorted — it is only "partially ordered".

??? question "Question 12 — Position of the second minimum in a heap"
    In a min-heap, the second smallest element is always at index 1 of the array.

    ??? success "Answer"
        **False.** The second smallest element is at index 1 **or** 2 (one of the two children of the root).

        The heap invariant only guarantees that **each parent is ≤ its children**. There is no ordering relationship between the children (indices 1 and 2).

        ```
        Valid heap where the 2nd minimum is at index 2:

              1           Array: [1, 5, 2, 7, 8, 3, 4]
            /   \
           5     2   ← 2 is the 2nd minimum, at index 2
          / \   / \
         7   8 3   4
        ```

        **Classic pitfall:** Believing that a heap is sorted. Only the parent-child relationship is guaranteed.

??? question "Question 13 — Heap vs binary search tree"
    A binary heap and a binary search tree (BST) have the same ordering property.

    ??? success "Answer"
        **False.** The ordering properties are fundamentally different:

        | Property | Heap (min) | BST |
        |----------|------------|-----|
        | Relation | parent ≤ children | left < parent < right |
        | Minimum | Root (O(1)) | Leftmost node (O(h)) |
        | Key search | O(n) | O(h), so O(log n) if balanced |
        | Structure | Complete tree (array) | Variable shape |

        ```
              Heap                    BST
               2                       5
             /   \                   /   \
            5     3                 3     7
           / \                     / \
          7   8                   2   4

        Heap: 5 > 3 but 5 is on the left (OK)
        BST: left < parent < right (always)
        ```

        **Very common classic pitfall:** Confusing these two structures. A heap is NOT a BST!

---

#### Block D — Graphs

??? question "Question 14 — Trees and graphs"
    Every tree is a graph, but not every graph is a tree.

    ??? success "Answer"
        **True.** A tree is a special case of a graph with additional constraints:

        **Definition of a tree**: A connected and acyclic graph.

        - **Connected**: There exists a path between every pair of vertices
        - **Acyclic**: No cycles

        Derived properties (for a tree with n vertices):

        - Exactly n-1 edges
        - A unique path between each pair of vertices

        ```
              Tree                  Graph (not a tree)
                A                          A
               /|\                        /|\
              B C D                      B-C-D
                                          \_/
                                        (cycle!)
        ```

        **Note:** In graph theory, we speak of unrooted trees. In data structures, we often work with rooted trees (with a designated root).

??? question "Question 15 — Matrix vs adjacency list"
    For a graph with 1000 vertices and 3000 edges, an adjacency matrix uses less memory than an adjacency list.

    ??? success "Answer"
        **False.** Let us calculate:

        **Adjacency matrix**:

        - Size: V × V = 1000 × 1000 = **1,000,000 entries**
        - Even for a sparse graph, all cells exist

        **Adjacency list**:

        - Each edge appears in 2 lists (if undirected): 3000 × 2 = **6,000 entries**
        - Plus V = 1,000 list heads

        Ratio: 1,000,000 / 6,000 ≈ **166× more memory for the matrix!**

        **Practical rule**:

        - **Dense** graph (E ≈ V²) → Matrix
        - **Sparse** graph (E << V²) → Adjacency list

        Here, E = 3,000 and V² = 1,000,000, so E << V²: the graph is sparse.

        **Classic pitfall:** Using a matrix by default. Always analyze the density!

??? question "Question 16 — Maximum number of edges"
    An undirected graph with n vertices can have at most n(n-1) edges.

    ??? success "Answer"
        **False.** The maximum is **n(n-1)/2** for a simple undirected graph (without loops or multiple edges).

        **Undirected graph**: An edge {u, v} is the same as {v, u}.

        - Number of possible pairs: C(n,2) = n(n-1)/2

        **Directed graph**: An arc (u, v) is different from (v, u).

        - Number of possible arcs (without loops): n(n-1)

        | Type | Maximum edges/arcs |
        |------|--------------------|
        | Simple undirected | n(n-1)/2 |
        | Simple directed | n(n-1) |
        | With loops | Add n to the values above |

        **Classic pitfall:** Ignoring the distinction between directed and undirected. It changes the maximum number of edges by a factor of 2.

??? question "Question 17 — Edge list and neighborhood"
    In an edge list representation, the operation `incidentEdges(v)` (listing the edges incident to v) runs in O(deg(v)).

    ??? success "Answer"
        **False.** In an edge list, `incidentEdges(v)` requires **scanning all edges** to find those incident to v, giving **O(m)** where m is the total number of edges.

        This is the main weakness of the edge list compared to the adjacency list:

        | Operation | Edge list | Adjacency list |
        |-----------|-----------|----------------|
        | `incidentEdges(v)` | **O(m)** | **O(deg(v))** |
        | `areAdjacent(v, w)` | **O(m)** | **O(min(deg(v), deg(w)))** |
        | `insertEdge(e)` | **O(1)** | **O(1)** |
        | `removeEdge(e)` | **O(1)** | **O(1)** |
        | Space | O(n + m) | O(n + m) |

        The edge list is the **simplest** but least efficient structure for neighborhood queries. It is useful when one mainly needs to iterate over all edges.

        **Classic pitfall:** Confusing edge list and adjacency list. The edge list does **not** store edges by vertex, but in a global collection.

??? question "Question 18 — Maximum edges with connected components"
    A simple undirected graph with 12 vertices and 3 connected components can have at most 45 edges.

    ??? success "Answer"
        **True.** To maximize the number of edges with exactly k connected components, one must **concentrate the vertices** in one component and make the others as small as possible (1 vertex each).

        With n = 12 vertices and k = 3 components:

        - Put **10 vertices** in one component (complete graph): C(10, 2) = 10 × 9 / 2 = **45 edges**
        - Put **1 vertex** in each of the other 2 components: **0 edges** each

        Total maximum = **45 edges** ✓

        **Why is this distribution optimal?** If we distribute more evenly, for example (4, 4, 4):

        - C(4,2) × 3 = 6 × 3 = **18 edges** — much less than 45!

        **General formula:** For n vertices and k components, the maximum number of edges is C(n−k+1, 2) = (n−k+1)(n−k) / 2.

        Verification: (12−3+1)(12−3) / 2 = 10 × 9 / 2 = 45 ✓

        **Classic pitfall:** Thinking that distributing evenly maximizes edges. It is the opposite — the function C(n, 2) is convex, so concentrating vertices produces more edges.

---

### Section 2 — Comparative MCQs

??? question "Question 1 — Complexity table"
    Complete the complexity table for a list of n elements:

    | Operation | ArrayList | LinkedList | Positional List* |
    |-----------|-----------|------------|------------------|
    | `get(k)` | ? | ? | ? |
    | `add(0, e)` | ? | ? | ? |
    | `add(k, e)` (middle) | ? | ? | ? |
    | `remove(position)` | ? | ? | ? |

    *Position already obtained in advance

    ??? success "Answer"
        | Operation | ArrayList | LinkedList | Positional List* |
        |-----------|-----------|------------|------------------|
        | `get(k)` | **O(1)** | **O(n)** | **O(n)** |
        | `add(0, e)` | **O(n)** | **O(1)** | **O(1)** |
        | `add(k, e)` | **O(n)** | **O(n)** | **O(1)*** |
        | `remove(position)` | **O(n)** | **O(1)*** | **O(1)** |

        *With position/iterator already known

        **Key points:**

        - `ArrayList` excels at indexed access but suffers for insertions/deletions (shifts)
        - `LinkedList` has O(n) access but O(1) insertion/deletion **if you already have the position**
        - The positional list combines the best of both **when positions are kept**

??? question "Question 2 — Graph representations"
    A transportation network has **500 stations** and **800 bidirectional connections**. One must frequently list all neighboring stations of a given station and check whether two stations are directly connected.

    Which representation is most appropriate?

    - [ ] A) Edge list
    - [ ] B) Adjacency matrix
    - [ ] C) Adjacency list
    - [ ] D) All are equivalent

    ??? success "Answer"
        **C) Adjacency list**

        Analysis for this graph (n = 500, m = 800, sparse graph since m << n²):

        | Operation | Edge list | Adjacency matrix | Adjacency list |
        |-----------|-----------|------------------|----------------|
        | `incidentEdges(v)` | **O(m)** = O(800) | **O(n)** = O(500) | **O(deg(v))** ≈ O(3) |
        | `areAdjacent(v, w)` | **O(m)** = O(800) | **O(1)** | **O(min(deg))** ≈ O(3) |
        | Space | O(n + m) = 1,300 | **O(n²)** = 250,000 | O(n + m) = 1,300 |

        - **Edge list**: Too slow for neighborhood queries — must scan **all** edges each time
        - **Matrix**: Wastes 250,000 entries for only 800 connections, and `incidentEdges` requires O(n)
        - **Adjacency list**: Compact space and operations proportional to degree

        **Classic pitfall:** The edge list is simple to implement but becomes prohibitive as soon as frequent neighborhood queries are made.

??? question "Question 3 — Complexity analysis"
    ```java
    void removeNegatives(List<Integer> list) {
        for (int i = 0; i < list.size(); i++) {
            if (list.get(i) < 0) {
                list.remove(i);
                i--;
            }
        }
    }
    ```

    What is the complexity if `list` is a `LinkedList` of n elements, k of which are negative?

    - [ ] A) O(n)
    - [ ] B) O(n + k)
    - [ ] C) O(n²)
    - [ ] D) O(nk)

    ??? success "Answer"
        **C) O(n²)**

        Let us analyze each operation in the loop:

        - `list.get(i)`: **O(i)** in a LinkedList (traversal from the beginning or end)
        - `list.remove(i)`: **O(i)** to find + O(1) to remove

        The loop runs n times (minus the deletions, but let us stay with n as an upper bound).

        Total cost: Σ O(i) for i from 0 to n ≈ O(n²)

        **Why not O(nk)?** Even the `get(i)` calls on positive elements cost O(i). It is not only the k deletions that are expensive.

        **Optimal solution**: Use an iterator!

        ```java
        void removeNegatives(List<Integer> list) {
            Iterator<Integer> it = list.iterator();
            while (it.hasNext()) {
                if (it.next() < 0) {
                    it.remove();  // O(1) because the iterator already has the position
                }
            }
        }
        ```

        Complexity with iterator: **O(n)**.

??? question "Question 4 — Heap trace"
    Keys **8, 5, 10, 3, 7, 2** are inserted successively into an empty min-heap.

    What is the state of the array after all insertions?

    - [ ] A) `[2, 3, 5, 8, 7, 10]`
    - [ ] B) `[2, 5, 3, 8, 7, 10]`
    - [ ] C) `[2, 3, 5, 7, 8, 10]`
    - [ ] D) `[2, 5, 3, 10, 7, 8]`

    ??? success "Answer"
        **B) `[2, 5, 3, 8, 7, 10]`**

        Let us trace each insertion with up-heap:

        1. **insert(8)**: `[8]`

        2. **insert(5)**: `[8, 5]` → 5 < 8, swap → `[5, 8]`

        3. **insert(10)**: `[5, 8, 10]` → 10 > 5, OK

        4. **insert(3)**: `[5, 8, 10, 3]`
           - 3 at index 3, parent = index 1 (value 8)
           - 3 < 8 → swap → `[5, 3, 10, 8]`
           - 3 at index 1, parent = index 0 (value 5)
           - 3 < 5 → swap → `[3, 5, 10, 8]`

        5. **insert(7)**: `[3, 5, 10, 8, 7]`
           - 7 at index 4, parent = index 1 (value 5)
           - 7 > 5 → OK

        6. **insert(2)**: `[3, 5, 10, 8, 7, 2]`
           - 2 at index 5, parent = index 2 (value 10)
           - 2 < 10 → swap → `[3, 5, 2, 8, 7, 10]`
           - 2 at index 2, parent = index 0 (value 3)
           - 2 < 3 → swap → `[2, 5, 3, 8, 7, 10]`

        **Final result: `[2, 5, 3, 8, 7, 10]`** (answer B)

        ```
              2
            /   \
           5     3
          / \   /
         8   7 10
        ```

??? question "Question 5 — Stack and queue size after operations"
    A stack `S` has undergone the following operations: 25 `push`, 12 `top`, 10 `pop` (3 of which returned `null` because the stack was empty).

    What is the current size of `S`?

    - [ ] A) 15
    - [ ] B) 18
    - [ ] C) 3
    - [ ] D) 12

    ??? success "Answer"
        **B) 18**

        Let us analyze the effect of each operation on the size:

        | Operation | Effect on size | Occurrences | Total |
        |-----------|----------------|-------------|-------|
        | `push` | +1 | 25 | +25 |
        | `top` | 0 (query) | 12 | 0 |
        | successful `pop` | −1 | 7 | −7 |
        | `pop` on empty stack | 0 (returns null) | 3 | 0 |

        Size = 25 − 7 = **18**

        **Key points:**

        - `top()` does not modify the stack — it is a query
        - The 3 `pop` calls that return `null` removed nothing (the stack was empty at that moment)
        - Only 10 − 3 = **7** successful `pop` calls removed an element

        **Classic pitfall:** Counting all 10 `pop` calls instead of 7. Pops on an empty stack do not change the size!

??? question "Question 6 — Memory of graph representations"
    A social network has **1 million users**. Each user has an average of **200 friends** (undirected edges).

    What is the approximate memory (in entries) for each representation?

    - [ ] A) Matrix: 1 million, List: 200 million
    - [ ] B) Matrix: 1,000 billion, List: 200 million
    - [ ] C) Matrix: 1,000 billion, List: 400 million
    - [ ] D) Both use the same memory

    ??? success "Answer"
        **B) Matrix: 1,000 billion, List: 200 million**

        **Adjacency matrix**:

        - Size: V² = (10⁶)² = 10¹² = 1,000 billion entries
        - Independent of the number of edges!

        **Adjacency list**:

        - Each user stores their list of 200 friends
        - Total neighbor entries: 10⁶ × 200 = **200 million**
        - Plus V = 1 million list heads (negligible)

        **Ratio**: The matrix uses approximately **5000× more memory**!

        This graph is very sparse: E ≈ 100M vs V² = 10¹². The adjacency list is clearly the right choice.

??? question "Question 7 — Undo/Redo system"
    You are implementing an undo system with the following constraints:

    - Unlimited undo
    - Each action can be "replayed" (Redo) after Undo
    - A new action after Undo clears the Redo history

    What is the minimal structure needed?

    - [ ] A) A single Stack
    - [ ] B) Two Stacks
    - [ ] C) A Deque
    - [ ] D) A positional list

    ??? success "Answer"
        **B) Two Stacks**

        The classic pattern uses two stacks:

        - **undoStack**: performed actions (LIFO — last action = first to undo)
        - **redoStack**: undone actions (LIFO — last undone = first to redo)

        ```java
        class UndoManager {
            Deque<Action> undoStack = new ArrayDeque<>();
            Deque<Action> redoStack = new ArrayDeque<>();

            void doAction(Action a) {
                a.execute();
                undoStack.push(a);
                redoStack.clear();  // New action clears redo
            }

            void undo() {
                if (!undoStack.isEmpty()) {
                    Action a = undoStack.pop();
                    a.reverse();
                    redoStack.push(a);
                }
            }

            void redo() {
                if (!redoStack.isEmpty()) {
                    Action a = redoStack.pop();
                    a.execute();
                    undoStack.push(a);
                }
            }
        }
        ```

        **Why not a Deque?** A Deque could technically work, but it offers no advantage and is less clear conceptually. Two explicit stacks make the intent obvious.

---

### Section 3 — Trace Exercises

??? question "Exercise 1 — Stack and Queue combined"
    We have a Stack `S` and a Queue `Q`, both empty.

    Execute the following operations and give the final state of each structure:

    ```
    S.push(1), Q.enqueue(1), S.push(2), Q.enqueue(2),
    S.push(S.pop() + Q.dequeue()), Q.enqueue(S.top()),
    S.pop(), Q.enqueue(3), S.push(Q.dequeue())
    ```

    ??? success "Answer"
        Let us trace step by step:

        | Operation | Stack S (bottom→top) | Queue Q (front→back) |
        |-----------|----------------------|----------------------|
        | Initial | `[]` | `[]` |
        | `S.push(1)` | `[1]` | `[]` |
        | `Q.enqueue(1)` | `[1]` | `[1]` |
        | `S.push(2)` | `[1, 2]` | `[1]` |
        | `Q.enqueue(2)` | `[1, 2]` | `[1, 2]` |
        | `S.pop()` returns 2 | `[1]` | `[1, 2]` |
        | `Q.dequeue()` returns 1 | `[1]` | `[2]` |
        | `S.push(2+1=3)` | `[1, 3]` | `[2]` |
        | `S.top()` returns 3 | `[1, 3]` | `[2]` |
        | `Q.enqueue(3)` | `[1, 3]` | `[2, 3]` |
        | `S.pop()` returns 3 | `[1]` | `[2, 3]` |
        | `Q.enqueue(3)` | `[1]` | `[2, 3, 3]` |
        | `Q.dequeue()` returns 2 | `[1]` | `[3, 3]` |
        | `S.push(2)` | `[1, 2]` | `[3, 3]` |

        **Final state:**

        - **S** = `[1, 2]` (1 at bottom, 2 at top)
        - **Q** = `[3, 3]` (3 at front, 3 at back)

??? question "Exercise 2 — Heap removeMin"
    Consider the min-heap represented by the array: `[3, 5, 4, 9, 8, 7, 6]`

    ```
          3
        /   \
       5     4
      / \   / \
     9   8 7   6
    ```

    Execute **two** successive `removeMin()` operations. Show the state of the array after each operation, detailing the down-heap steps.

    ??? success "Answer"
        **First removeMin():**

        1. Remove the root (3), replace with the last element (6)
           - `[6, 5, 4, 9, 8, 7]`

        2. Down-heap from the root:
           - 6 at index 0, children: 5 (index 1) and 4 (index 2)
           - min(5, 4) = 4, and 6 > 4 → swap with index 2
           - `[4, 5, 6, 9, 8, 7]`

        3. Continue down-heap:
           - 6 at index 2, child: 7 (index 5), no right child
           - 6 < 7 → OK, done

        **After first removeMin: `[4, 5, 6, 9, 8, 7]`**

        ```
              4
            /   \
           5     6
          / \   /
         9   8 7
        ```

        ---

        **Second removeMin():**

        1. Remove the root (4), replace with the last element (7)
           - `[7, 5, 6, 9, 8]`

        2. Down-heap from the root:
           - 7 at index 0, children: 5 (index 1) and 6 (index 2)
           - min(5, 6) = 5, and 7 > 5 → swap with index 1
           - `[5, 7, 6, 9, 8]`

        3. Continue down-heap:
           - 7 at index 1, children: 9 (index 3) and 8 (index 4)
           - min(9, 8) = 8, and 7 < 8 → OK, done

        **After second removeMin: `[5, 7, 6, 9, 8]`**

        ```
              5
            /   \
           7     6
          / \
         9   8
        ```

??? question "Exercise 3 — Full trace of a priority queue"
    The following operations are executed on an initially empty min priority queue (min-heap). For each `removeMin()`, indicate the (key, value) pair returned.

    ```
    insert(5,A), insert(4,B), insert(7,F), insert(1,D),
    removeMin(), insert(3,J), insert(6,L), removeMin(),
    removeMin(), insert(8,G), removeMin(), insert(2,H),
    removeMin(), removeMin()
    ```

    ??? success "Answer"
        Let us trace the state of the heap after each operation:

        | # | Operation | Heap (array, min at head) | Return |
        |---|-----------|---------------------------|--------|
        | 1 | `insert(5,A)` | `[(5,A)]` | — |
        | 2 | `insert(4,B)` | `[(4,B), (5,A)]` | — |
        | 3 | `insert(7,F)` | `[(4,B), (5,A), (7,F)]` | — |
        | 4 | `insert(1,D)` | `[(1,D), (4,B), (7,F), (5,A)]` | — |
        | 5 | `removeMin()` | `[(4,B), (5,A), (7,F)]` | **(1,D)** |
        | 6 | `insert(3,J)` | `[(3,J), (4,B), (7,F), (5,A)]` | — |
        | 7 | `insert(6,L)` | `[(3,J), (4,B), (7,F), (5,A), (6,L)]` | — |
        | 8 | `removeMin()` | `[(4,B), (5,A), (7,F), (6,L)]` | **(3,J)** |
        | 9 | `removeMin()` | `[(5,A), (6,L), (7,F)]` | **(4,B)** |
        | 10 | `insert(8,G)` | `[(5,A), (6,L), (7,F), (8,G)]` | — |
        | 11 | `removeMin()` | `[(6,L), (8,G), (7,F)]` | **(5,A)** |
        | 12 | `insert(2,H)` | `[(2,H), (8,G), (7,F), (6,L)]` | — |
        | 13 | `removeMin()` | `[(6,L), (8,G), (7,F)]` | **(2,H)** |
        | 14 | `removeMin()` | `[(7,F), (8,G)]` | **(6,L)** |

        **Summary of removeMin results:**

        1. (1, D)
        2. (3, J)
        3. (4, B)
        4. (5, A)
        5. (2, H)
        6. (6, L)

        **Detail of step 12** (the most interesting — up-heap):

        ```
        Before insert(2,H):     After up-heap:
              6                        2
            /   \                    /   \
           8     7                  6     7
                                  /
                                 8
        insert(2,H) at end → [6, 8, 7, 2]
        2 < 8 (parent) → swap → [6, 2, 7, 8]
        2 < 6 (parent) → swap → [2, 6, 7, 8]
        ```

        **Key point:** Note that `removeMin` does **not** return elements in insertion order. Inserting (2,H) late in the sequence means it is returned **before** (6,L) which was inserted earlier.

??? question "Exercise 4 — Directed graph representations (Ch. 14 + code)"
    This exercise uses exactly the directed graph from `docs/files/code/graphs/src/Main.java`
    (method `demonstrateFullGraph`):

    * Vertices: `V = {a, b, c}`
    * Labeled arcs:
        * `α : a → b`
        * `β : b → a`
        * `γ : b → c`
        * `δ : c → c` (self-loop)

    Use the vertex order **`[a, b, c]`** for the adjacency matrix.

    **Questions**:

    1. Give the exact `edge list` (with labels).
    2. Give the adjacency matrix (3×3), with `1` if the arc exists, otherwise `0`.
    3. Give the adjacency list of **outgoing** and **incoming** arcs for each vertex.
    4. Give `outDegree`, `inDegree`, and `degree` for each vertex.
    5. Give the results of the calls: `getEdge(a,b)`, `getEdge(b,a)`, `getEdge(a,c)`, `getEdge(c,c)`.

    ??? success "Answer"
        **1) Edge list:**

        ```
        E = [
          (a, b, α),
          (b, a, β),
          (b, c, γ),
          (c, c, δ)
        ]
        ```

        ---

        **2) Adjacency matrix (order `[a,b,c]`):**

        |   | a | b | c |
        |---|---|---|---|
        | **a** | 0 | 1 | 0 |
        | **b** | 1 | 0 | 1 |
        | **c** | 0 | 0 | 1 |

        Reading: `M[i][j] = 1` if there exists an arc `i → j`.

        ---

        **3) Outgoing and incoming adjacency lists:**

        ```
        outgoing(a) = [α(a→b)]
        incoming(a) = [β(b→a)]

        outgoing(b) = [β(b→a), γ(b→c)]
        incoming(b) = [α(a→b)]

        outgoing(c) = [δ(c→c)]
        incoming(c) = [γ(b→c), δ(c→c)]
        ```

        ---

        **4) Degrees:**

        | Vertex | `outDegree` | `inDegree` | `degree = out + in` |
        |--------|-------------|------------|----------------------|
        | `a` | 1 | 1 | 2 |
        | `b` | 2 | 1 | 3 |
        | `c` | 1 | 2 | 3 |

        Digraph verification:
        `Σ outDegree = 1 + 2 + 1 = 4 = |E|`
        `Σ inDegree  = 1 + 1 + 2 = 4 = |E|`

        ---

        **5) Results of `getEdge`:**

        * `getEdge(a,b)` returns `α`
        * `getEdge(b,a)` returns `β`
        * `getEdge(a,c)` returns `null`
        * `getEdge(c,c)` returns `δ`

        Important point: in a directed graph, `getEdge(u,v)` is not equivalent to `getEdge(v,u)`.

---

## Second Hour: Application and Synthesis

### Section 4 — Design Scenarios

??? question "Scenario 1 — IoT sensor network"
    **Context**: A network of 200 environmental sensors. Sensors communicate with each other via radio link (limited range). The network has approximately 600 bidirectional links.

    **Required operations**:

    1. Add/remove a sensor (rare: maintenance)
    2. Add/remove a link when a sensor changes range (occasional)
    3. List all network links to calculate the total maintenance cost (frequent)
    4. Find all neighbors of a sensor for data routing (very frequent)

    **Questions**:

    1. For operation 3 (listing all links), which representation is most natural? Complexity?
    2. For operation 4 (finding neighbors), compare the three representations (edge list, adjacency list, matrix). Which is most efficient?
    3. Can two representations be combined? What is the trade-off?
    4. Is this graph dense or sparse? Justify and deduce which representation to avoid.

    ??? success "Answer"
        **1. Listing all links — Edge list:**

        The **edge list** is most natural for iterating over all edges:

        - Each edge is an object stored in a collection
        - Iteration: **O(m)** — directly traverse the list

        In the textbook model (Section 14.2), one can also expose `edges()` in **O(m)** for other representations by maintaining a global collection of edges.

        Without this auxiliary collection, we fall back to:

        - Adjacency list: O(n + m) with duplicate handling
        - Matrix: O(n²) by scanning the matrix

        ```
        Edge list:
        [(A,B), (A,C), (B,D), (C,D), (D,E), ...]
        → Direct traversal in O(m)
        ```

        ---

        **2. Finding neighbors — Adjacency list:**

        | Representation | `incidentEdges(v)` | Cost for this network |
        |----------------|--------------------|-----------------------|
        | Edge list | O(m) | O(600) per query |
        | Adjacency matrix | O(n) | O(200) |
        | **Adjacency list** | **O(deg(v))** | **O(6)** on average |

        The adjacency list is **100× more efficient** than the edge list for this operation, since the average degree is 2m/n = 1200/200 = 6.

        ---

        **3. Combining representations:**

        Yes, one can maintain **two representations simultaneously**:

        - **Edge list** for operation 3 (global edge iteration)
        - **Adjacency list** for operation 4 (fast neighborhood)

        **Trade-off**:

        | Aspect | Single structure | Two structures |
        |--------|-----------------|----------------|
        | Space | O(n + m) | O(n + m) × 2 |
        | Edge insertion | O(1) | O(1) × 2 operations |
        | Consistency | Automatic | Must keep both in sync |

        The overhead is acceptable if both operations are frequent. This is indeed the approach recommended in the textbook (Section 14.2).

        ---

        **4. Dense or sparse?**

        - n = 200, m = 600
        - Maximum edges (undirected): n(n-1)/2 = 19,900
        - Ratio: 600 / 19,900 ≈ **3%** → **sparse graph**

        **Representation to avoid**: The **adjacency matrix** which would use 200² = 40,000 entries for only 600 edges (66× more memory than needed).

??? question "Scenario 2 — Hospital multi-structure system"
    **Context (architecture level)**: A regional hospital with 8 departments must manage three computer subsystems. The hospital receives approximately 200 patients per day.

    **Subsystem A — General queue:**
    Patients arrive at the reception and are treated in order of arrival (first come, first served). About 30 patients are waiting at any given time.

    **Subsystem B — Emergency:**
    Emergency patients are treated according to their severity score (1 = minor, 10 = critical). Sometimes a patient's condition changes during the wait and their score must be updated. About 15 patients are waiting.

    **Subsystem C — Department network:**
    The 8 departments are connected by 12 bidirectional corridors. One must frequently find the neighboring departments of a given department (to transfer a patient).

    **Questions**:

    1. For each subsystem (A, B, C), which data structure is most appropriate? Justify with complexities.
    2. Give a mini table of the dominant operations and their target costs for A, B, and C.
    3. Is subsystem C dense or sparse? Which main representation to choose?
    4. The hospital also wants to produce a daily report "complete list of corridors". Propose a data organization to support both `incidentEdges(v)` and global edge iteration efficiently.

    ??? success "Answer"
        **1. Appropriate structures:**

        | Subsystem | Structure | Justification |
        |-----------|-----------|---------------|
        | **A** — General queue | **Queue (ArrayDeque)** | Pure FIFO: `enqueue` O(1), `dequeue` O(1). Arrival order determines treatment order. |
        | **B** — Emergency | **Priority queue (heap)** | Extraction of the most urgent in O(log n), insertion in O(log n). With n ≈ 15, log₂(15) ≈ 4 — very fast. |
        | **C** — Network | **Adjacency list** | `incidentEdges(v)` in O(deg(v)) for transfers. Sparse graph → memory efficient. |

        ---

        **2. Target costs (dominant operations):**

        | Subsystem | Dominant operation | Target cost |
        |-----------|--------------------|-------------|
        | A — Reception | `enqueue`, `dequeue` | O(1), O(1) |
        | B — Emergency | `insert`, `removeMax` | O(log n), O(log n) |
        | C — Network | `incidentEdges(v)` | O(deg(v)) |

        ---

        **3. Dense or sparse?**

        - n = 8 departments, m = 12 corridors
        - Maximum edges: n(n−1)/2 = 8×7/2 = **28**
        - Ratio: 12/28 ≈ **43%** → graph of **medium density**

        For this small graph, all three representations are reasonable, but the **adjacency list** is the best choice:

        | Representation | `incidentEdges(v)` | Space |
        |----------------|-------------------|-------|
        | Edge list | O(m) = O(12) | O(n + m) = 20 |
        | **Adjacency list** | **O(deg(v))** ≈ O(3) | O(n + m) = 20 |
        | Adjacency matrix | O(n) = O(8) | O(n²) = 64 |

        The dominant operation (finding neighbors for transfers) is O(deg(v)) ≈ O(3) with the adjacency list, more efficient than O(8) with the matrix or O(12) with the edge list.

        ---

        **4. Global reports + local neighborhood:**

        Maintain **two synchronized views** of graph C:

        - Main view: **adjacency list** (fast neighborhood for transfers)
        - Auxiliary view: **edge list** (global corridor report in O(m))

        Trade-off:

        - Local read (`incidentEdges(v)`) very efficient
        - Global read (`edges()`) direct
        - Additional cost: maintaining consistency between the two views during edge additions/removals

??? question "Scenario 3 — Emergency room management"
    **Context (operational level)**: We zoom in on the emergency subsystem only. The queue contains approximately 50 patients.

    **Business rules**:

    - Primary priority: severity (10 before 1)
    - Tiebreaker: at equal severity, order of arrival (FIFO)
    - Operations: `insert`, `removeMax`, `replaceKey`, `remove(entry)`

    **Questions**:

    1. Give a priority key that implements these rules (severity + FIFO).
    2. Mini-trace: insert successively `(Alice,4,t1)`, `(Bob,7,t2)`, `(Chloé,7,t3)`, `(David,3,t4)`. What are the next two patients extracted?
    3. Then, David's severity changes from 3 to 9. Give the complexity of this update with a simple heap, then with an adaptable priority queue.
    4. Bob leaves the waiting room before being treated. What operation to use and what complexity (simple heap vs adaptable)?
    5. Compare adaptable heap vs sorted list for this workload: 40 insertions, 30 extractions, 15 priority updates, 5 targeted removals (n≈50).

    ??? success "Answer"
        **1. Priority key:**

        Use a lexicographic key:

        - Primary key: `severity` (descending)
        - Secondary key: `arrivalTime` (ascending)

        So `(7,t2)` has priority over `(7,t3)` because `t2 < t3`.

        ---

        **2. Mini-trace:**

        Queue after insertions:

        ```java
        (Bob,7,t2), (Chloé,7,t3), (Alice,4,t1), (David,3,t4)
        ```

        The next two extracted are:

        1. **Bob** (severity 7, arrived before Chloé)
        2. **Chloé** (severity 7)

        ---

        **3. Updating David (3 → 9):**

        - **Simple heap**: find David O(n), then reheap O(log n) → **O(n)**
        - **Adaptable heap**: `replaceKey(entryDavid, newKey)` → **O(log n)**

        ---

        **4. Bob leaves the queue:**

        - **Simple heap**: find Bob O(n), then delete/reheap O(log n) → **O(n)**
        - **Adaptable heap**: `remove(entryBob)` → **O(log n)**

        ---

        **5. Mixed workload (n≈50, log₂(n)≈6):**

        Approximate total cost:

        - **Adaptable heap**: `(40 + 30 + 15 + 5) × log n ≈ 90 × 6 = 540`
        - **Sorted list**:
            - insertions: `40 × 50 = 2000`
            - extractions: `30 × 1 = 30`
            - priority updates: `15 × 50 = 750`
            - targeted removals: `5 × 50 = 250`
            - total ≈ `3030`

        The adaptable heap is approximately **5 to 6×** more efficient for this workload.

??? question "Scenario 4 — Energy distribution network"
    A regional electrical network must manage:

    **Entities**:

    - **Nodes**: 500 points (power plants, substations, transformers, consumption points)
    - **Lines**: 1200 transmission lines, each with a maximum capacity (MW) and a maintenance cost
    - The network is **undirected** (electricity can flow in both directions)

    **Critical operations** (by frequency):

    1. **Monitoring**: For a given node, list all connected lines and their current load (very frequent)
    2. **Cost calculation**: Scan all lines to calculate total maintenance cost (daily)
    3. **Failure**: Remove a faulty line and verify that the network remains connected (occasional)
    4. **Extension**: Add a new line between two nodes (rare)

    ---

    **Question 1**: Choice of main representation

    Compare the three representations (edge list, adjacency list, matrix) for this network. Which is most suitable as the main structure?

    **Question 2**: Storage of line attributes

    Each line has attributes (capacity, cost, current load). How are they stored in each representation?

    **Question 3**: Cost calculation operation

    For operation 2, is it advantageous to maintain an edge list in addition to the main structure? Justify.

    **Question 4**: Connectivity detection after failure

    After removing a line, how do you verify that the network remains connected? Which representation facilitates this verification?

    ??? success "Answer"
        **Question 1: Choice of representation**

        | Criterion | Edge list | Adjacency list | Matrix |
        |-----------|-----------|----------------|--------|
        | Space | O(n + m) = 1,700 | O(n + m) = 1,700 | O(n²) = 250,000 |
        | Op. 1: neighbors of a node | O(m) = O(1200) | **O(deg(v))** ≈ O(5) | O(n) = O(500) |
        | Op. 2: all lines (`edges()`) | **O(m)** = O(1200) | **O(m)** = O(1200) | **O(m)** = O(1200) |
        | Op. 3: remove a line (`removeEdge(e)`) | O(1)* | O(1)* | O(1) |
        | Op. 4: add a line | O(1) | O(1) | O(1) |

        **The adjacency list** is the best main structure because operation 1 (the most frequent) is O(deg(v)) instead of O(m).

        The graph is sparse: m = 1,200 vs n² = 250,000, so the matrix is to be avoided.

        *Notes (textbook 14.2)*:
        - The O(m) cost for `edges()` assumes a global edge collection maintained in addition to the main representation.
        - The O(1) cost for removing a line assumes a reference to the edge is already available (`removeEdge(e)`).

        ---

        **Question 2: Storing attributes**

        - **Edge list**: Each Edge object stores its attributes directly — this is the most natural.

        ```java
        class Edge {
            Vertex u, v;
            double capacity;
            double cost;
            double currentLoad;
        }
        ```

        - **Adjacency list**: Each entry in the neighbor list must reference the Edge object (or duplicate the data).

        ```
        A → [(B, edge1), (D, edge2)]  // Reference to the Edge object
        ```

        - **Matrix**: Attributes are stored in the matrix cells instead of simple booleans.

        ```
        M[A][B] = Edge(capacity=100, cost=50, load=75)
        ```

        **The edge list is the most natural** for storing attributes. This is a key advantage of this representation.

        ---

        **Question 3: Maintain an edge list in addition?**

        **Yes**, it is advantageous. With an adjacency list alone, calculating the total cost requires:

        ```
        For each vertex v:
            For each edge incident to v:
                accumulate cost
        ```

        Problem: each edge is counted **twice** (once per endpoint in an undirected graph). One must either divide by 2 or mark visited edges.

        With an **auxiliary edge list**:

        ```
        total_cost = 0
        For each edge e in the edge list:
            total_cost += e.cost
        ```

        Simple, direct, no duplicates. Complexity: **O(m)**.

        **Trade-off**: Maintaining both structures in sync during additions/removals. Additional cost: O(1) per modification operation.

        ---

        **Question 4: Connectivity verification**

        After removing an edge, connectivity is verified by a **traversal** (BFS or DFS) from any vertex:

        - If the traversal visits **all n vertices** → the network is still connected
        - Otherwise → the removal has disconnected the network

        **Complexity**: O(n + m) for the traversal.

        **The adjacency list** facilitates this verification because the BFS/DFS traversal needs `incidentEdges(v)` at each vertex, which is O(deg(v)) with the adjacency list vs O(m) with the edge list.

        With the edge list alone, the traversal would be O(n × m) in total — far too slow.

        ```
        BFS traversal to verify connectivity:

        visited = {start_vertex}
        queue = [start_vertex]
        while queue not empty:
            v = queue.dequeue()
            for each neighbor w of v:    ← O(deg(v)) with adjacency list
                if w not in visited:
                    visited.add(w)
                    queue.enqueue(w)
        return |visited| == n
        ```

---

### Section 5 — Code Exercises

??? question "Exercise A — Graph representations"
    Consider the **undirected** graph G defined by the following adjacency list:

    ```
    A → [B, C, E]
    B → [A, C, D]
    C → [A, B, D, E]
    D → [B, C]
    E → [A, C]
    ```

    **Questions**:

    1. Draw graph G.
    2. Give the **adjacency matrix** of G (5×5).
    3. Give the **edge list** of G.
    4. Calculate the **degree** of each vertex. Verify your answer with the total number of edges.
    5. Is this graph **dense** or **sparse**? Justify.

    ??? success "Answer"
        **1. Drawing of the graph:**

        ```
            A ---- B
           /|\    /|
          / | \  / |
         /  |  \/  |
        E   |  /\  |
         \  | /  \ |
          \ |/    \|
            C ---- D
        ```

        (A-B, A-C, A-E, B-C, B-D, C-D, C-E — 7 edges)

        ---

        **2. Adjacency matrix:**

        |   | A | B | C | D | E |
        |---|---|---|---|---|---|
        | **A** | 0 | 1 | 1 | 0 | 1 |
        | **B** | 1 | 0 | 1 | 1 | 0 |
        | **C** | 1 | 1 | 0 | 1 | 1 |
        | **D** | 0 | 1 | 1 | 0 | 0 |
        | **E** | 1 | 0 | 1 | 0 | 0 |

        **Note:** The matrix is **symmetric** because the graph is undirected (M[i][j] = M[j][i]).

        ---

        **3. Edge list:**

        ```
        E = [{A,B}, {A,C}, {A,E}, {B,C}, {B,D}, {C,D}, {C,E}]
        ```

        Total: **7 edges**.

        ---

        **4. Degrees:**

        | Vertex | Neighbors | Degree |
        |--------|-----------|--------|
        | A | B, C, E | 3 |
        | B | A, C, D | 3 |
        | C | A, B, D, E | 4 |
        | D | B, C | 2 |
        | E | A, C | 2 |

        **Verification** (handshaking lemma):

        Σ deg(v) = 3 + 3 + 4 + 2 + 2 = **14** = 2 × 7 = 2 × |E| ✓

        ---

        **5. Dense or sparse?**

        - Number of edges: m = 7
        - Maximum possible (simple undirected graph): n(n−1)/2 = 5×4/2 = **10**
        - Ratio: 7/10 = **70%**

        This graph is relatively **dense** (70% of possible edges are present).

        For this type of graph, the adjacency matrix is a reasonable choice because:

        - The "wasted" space is small (only 3 zeros out of 10 pairs)
        - Adjacency verification is O(1) instead of O(deg)
        - But for only 5 vertices, the practical difference is negligible

??? question "Exercise B — Implementation"
    ```java
    /**
     * Given an array of integers and an integer k,
     * return the k largest elements (in any order).
     *
     * Constraint: Complexity O(n log k), not O(n log n).
     *
     * Hint: What type of heap to use and of what size?
     */
    public static int[] topK(int[] array, int k) {
        // To implement
    }
    ```

    ??? success "Answer"
        **Key idea:** Use a **min-heap of size k**.

        - Scan the array
        - If the heap has fewer than k elements, add
        - Otherwise, if the current element > minimum of the heap, replace the minimum

        At the end, the heap contains exactly the k largest elements.

        ```java
        public static int[] topK(int[] array, int k) {
            if (k <= 0) return new int[0];
            if (k >= array.length) return array.clone();

            // Min-heap: the smallest of the k largest is at the root
            PriorityQueue<Integer> minHeap = new PriorityQueue<>(k);

            for (int num : array) {
                if (minHeap.size() < k) {
                    minHeap.offer(num);
                } else if (num > minHeap.peek()) {
                    minHeap.poll();   // Remove the minimum
                    minHeap.offer(num);  // Add the new one
                }
            }

            // Convert to array
            int[] result = new int[minHeap.size()];
            for (int i = 0; i < result.length; i++) {
                result[i] = minHeap.poll();
            }
            return result;
        }
        ```

        **Complexity analysis:**

        - Array scan: n iterations
        - `offer` / `poll`: O(log k), `peek`: O(1)
        - Number of heap operations: at most 2n (offer + poll for each element)
        - **Total: O(n log k)**

        **Why a min-heap and not a max-heap?**

        With a min-heap of size k:

        - The root is the **smallest of the k largest** (the "threshold")
        - Each element is compared to the threshold in O(1)
        - If larger, eject the threshold and insert

        With a max-heap, there would be no efficient access to the minimum, so no threshold.

        **Comparison with sorting:**

        | Approach | Complexity | For n=1M, k=10 |
        |----------|------------|----------------|
        | Full sort | O(n log n) | ~20M operations |
        | Min-heap of size k | O(n log k) | ~3.3M operations |

        The heap is **6× more efficient** for this case.

---

### Section 7 — Summary

#### Decision Tree: Choosing Your Structure

```
PRIMARY ACCESS?
│
├─► By index/numeric position
│   └─► Frequent modifications in the middle?
│       ├─► Yes, with known position → Positional list
│       ├─► Yes, without position → LinkedList (beware O(n) search!)
│       └─► No → ArrayList
│
├─► By priority
│   └─► Priorities change after insertion?
│       ├─► Yes → Adaptable priority queue (heap + locator)
│       └─► No → Simple heap
│
├─► LIFO (last in, first out)
│   └─► Stack (use ArrayDeque)
│
├─► FIFO (first in, first out)
│   └─► Queue (ArrayDeque or LinkedList)
│
└─► Relations between entities
    └─► GRAPH
        └─► Dominant operation?
            ├─► Iterate over all edges → Edge list
            ├─► Neighborhood → Adjacency list
            └─► Check adjacency in O(1) → Adjacency matrix (if dense graph)
```

#### Classic Pitfalls — Summary

| Pitfall | Reality |
|---------|---------|
| LinkedList always faster for insertions | Only if you already have the position! `get(i)` = O(n) |
| Heap = sorted array | No! Only guarantee: parent ≤ children |
| `java.util.Stack` = modern choice for LIFO | Legacy class: prefer `Deque` / `ArrayDeque` |
| Position = stable index | Position invalid after deletion of **its own** element |
| Edge list = fast for neighborhood | No! `incidentEdges(v)` = O(m), not O(deg(v)) |
| Matrix = default choice for graphs | Wastes O(V²) memory for sparse graphs |
| Amortized complexity = every operation | No, it is an average over n operations |
| More code = slower | Algorithmic complexity takes precedence |

#### Complexities to Know

| Structure | Insertion | Deletion | Access/Search | Min/Max |
|-----------|-----------|----------|---------------|---------|
| ArrayList | O(n)* | O(n) | O(1) by index | O(n) |
| LinkedList | O(1)** | O(1)** | O(n) | O(n) |
| Stack (ArrayDeque) | O(1) | O(1) | O(1) top only | — |
| Queue (ArrayDeque) | O(1) | O(1) | O(1) front only | — |
| Heap | O(log n) | O(log n) | O(n) | O(1) |
| Adaptable heap | O(log n) | O(log n) | O(1)*** | O(1) |

*O(1) amortized at end of list

**With position/iterator already known

***With locator structure

| Graph representation | Space | Add edge | Check edge | List neighbors | List all edges |
|----------------------|-------|----------|------------|----------------|----------------|
| Edge list | O(V + E) | O(1) | O(E) | O(E) | **O(E)** |
| Adjacency list | O(V + E) | O(1) | O(min(deg(u),deg(v))) | O(deg) | **O(E)** |
| Adjacency matrix | O(V²) | O(1) | O(1) | O(V) | **O(E)** |

*For `List all edges`: textbook assumption (Table 14.1) where a global edge collection is maintained.*

---

## References

* Goodrich, Tamassia, Goldwasser. *Data Structures and Algorithms in Java*, 6th Edition.
    * Chapter 6: Stacks, Queues, and Deques
    * Chapter 7: List and Iterator ADTs
    * Chapter 9: Priority Queues
    * Chapter 14.1-14.2: Graphs
* Java documentation:
    * [`java.util.Deque`](https://docs.oracle.com/javase/8/docs/api/java/util/Deque.html)
    * [`java.util.PriorityQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html)
