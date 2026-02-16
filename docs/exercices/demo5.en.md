# Demo 5: Mid-Session Review

This demo is a **complete review** covering chapters 6, 7, 9 and sections 14.1-14.2 of the book *Data Structures and Algorithms in Java (6th ed.)*.

!!! abstract "Review Objectives"

    By the end of this demo, you should be able to:

    * **Distinguish** between different data structures and their trade-offs
    * **Choose** the appropriate structure based on the context and dominant operations
    * **Analyze** the time and space complexity of operations
    * **Identify** classic pitfalls and common misconceptions
    * **Apply** your knowledge to realistic scenarios
    * **Compare** graph representations based on density and operations

---

## Covered Material

| Topic | Content | Chapters |
|-------|---------|----------|
| **Introduction** | Structure = algorithmic choice, invariants, hidden costs | — |
| **Lists** | ArrayList, LinkedList, positional lists | 7.1, 7.2, 7.3-7.6 |
| **Favorites List** | Sorting by frequency, move-to-front heuristic | 7.7 |
| **Stacks, Queues, Deques** | LIFO, FIFO, implementations, circular queues | 6 |
| **Concurrency** | Thread-safe queues, blocking queues, synchronization | — |
| **Priority Queues** | Heaps, properties, adaptable priorities | 9 |
| **Graphs** | Definitions, ADT, representations | 14.1, 14.2 |

---

## First Hour: Concepts and Reminders

### Section 1 — Thematic True or False

For each statement, indicate whether it is **true** or **false** and justify your answer.

#### Block A — Foundations and Philosophy

??? question "Question 1 — Complexity and code length"
    An algorithm with more lines of code is necessarily slower than a shorter algorithm solving the same problem.

    ??? success "Answer"
        **False.** Algorithmic complexity depends on the **number of operations as a function of the input size**, not the number of lines of code.

        Example: An insertion sort (short code) is O(n²), while a merge sort (longer code) is O(n log n). For large inputs, the longer code is much faster.

        **Classic pitfall:** Thinking that "more code = slower". Asymptotic complexity takes precedence over code length.

??? question "Question 2 — Cost of invariants"
    Maintaining an invariant (for example, keeping a list sorted) always has a negligible cost compared to the benefits it provides.

    ??? success "Answer"
        **False.** The cost of maintaining an invariant can be significant and must be weighed against the benefits.

        Example: A sorted list allows `min()` in O(1), but each insertion becomes O(n) to maintain the order. If insertions are frequent and minimum lookups are rare, this cost is prohibitive.

        **The right choice depends on the dominant operations.** That is why we compare:

        | Structure | `insert` | `min` | When to use |
        |-----------|----------|-------|-------------|
        | Unsorted list | O(1) | O(n) | Few lookups |
        | Sorted list | O(n) | O(1) | Few insertions |
        | Heap | O(log n) | O(1) | Balanced usage |

??? question "Question 3 — Amortized complexity"
    Amortized O(1) complexity guarantees that each individual operation takes constant time.

    ??? success "Answer"
        **False.** **Amortized** O(1) complexity means that over a long sequence of n operations, the total cost is O(n), so **on average** each operation costs O(1).

        However, some individual operations can take O(n). For example, `ArrayList.add()`:

        - Most additions: O(1)
        - When the array is full: O(n) to resize and copy

        **Classic pitfall:** Confusing amortized complexity with worst-case complexity. Amortized is an average, not a per-operation guarantee.

---

#### Block B — Linear Structures

??? question "Question 4 — LinkedList and insertions"
    For a list of 10,000 elements with frequent insertions in the middle, `LinkedList` is always more performant than `ArrayList`.

    ??? success "Answer"
        **False.** This claim ignores a crucial cost: **finding the insertion position**.

        To insert in the middle:

        | Structure | Finding the position | Inserting | Total |
        |-----------|----------------------|-----------|-------|
        | ArrayList | O(1) by index | O(n) shifting | O(n) |
        | LinkedList | O(n) traversal | O(1) relinking | O(n) |

        Both are O(n)! Moreover, `ArrayList` benefits from better **cache locality** (contiguous elements in memory), which often makes it faster in practice.

        **LinkedList is advantageous only if you already have a reference to the position** (via an iterator or a position in a positional list).

??? question "Question 5 — Optimization of get(i) in LinkedList"
    In a Java `LinkedList`, the call `list.get(n/2)` is optimized to start from the middle of the list.

    ??? success "Answer"
        **False.** Java optimizes by starting from the **beginning or the end** depending on the index:

        - If `i < size/2`: traverses from the beginning
        - If `i >= size/2`: traverses from the end

        But there is **no direct access to the middle**. For `get(n/2)`, Java traverses about n/2 elements from one end.

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

        **Classic pitfall:** Confusing position and index. A position is a stable reference to the container (node), not a numerical rank.

??? question "Question 7 — Positions in Java collections"
    Java does not expose positions in its standard collections because it is a design oversight that will be corrected in a future version.

    ??? success "Answer"
        **False.** This is a **deliberate design choice** to protect invariants.

        If Java exposed internal nodes (`Position`), a user could:

        - Keep a reference to a deleted node
        - Directly modify the `next`/`prev` links
        - Corrupt the data structure

        Java prefers to expose **iterators** (which are invalidated after modification) rather than persistent positions. It is a trade-off between flexibility and safety.

        Positional lists are useful when you **control** the environment and need O(1) performance for insertions/deletions with known positions.

??? question "Question 8 — Java's Stack class"
    The `java.util.Stack` class is recommended for implementing a stack in modern code.

    ??? success "Answer"
        **False.** `java.util.Stack` is a **legacy** class (inherited from Java 1.0) that poses several problems:

        1. It inherits from `Vector`, so all its methods are `synchronized` (unnecessary overhead in a single-threaded context)
        2. It exposes `Vector` methods that violate the LIFO principle (`add(index, element)`, `remove(index)`)

        **Official recommendation** (Javadoc):

        ```java
        // To avoid
        Stack<String> stack = new Stack<>();

        // Recommended
        Deque<String> stack = new ArrayDeque<>();
        stack.push("A");
        stack.pop();
        ```

        `ArrayDeque` is faster and strictly respects the stack interface.

---

#### Block C — Queues and Priorities

??? question "Question 9 — Move-to-front vs sorted list"
    The move-to-front heuristic is always superior to a list sorted by frequency because `access(e)` is O(n) in both cases.

    ??? success "Answer"
        **False.** Although `access(e)` is O(n) in both cases (linear search), performance differs for **`getFavorites(k)`**:

        | Structure | `access(e)` | `getFavorites(k)` |
        |-----------|-------------|-------------------|
        | List sorted by frequency | O(n) | **O(k)** — the first k are already the favorites |
        | Move-to-front | O(n) | **O(kn)** — must traverse to find the k maximums |

        Move-to-front is advantageous when there is **temporal locality** (recently accessed elements likely to be accessed again). Otherwise, the sorted list is better for `getFavorites`.

        **Classic pitfall:** Optimizing the wrong operation. Analyze which operations are **dominant** before choosing.

??? question "Question 10 — Queue synchronization"
    Adding `synchronized` to all methods of a `Queue` makes it thread-safe and performant for intensive concurrent use.

    ??? success "Answer"
        **False.** `synchronized` makes the structure thread-safe, but **not performant** under heavy contention.

        Problems:

        1. **Contention**: Only one thread can access the queue at a time. The others wait.
        2. **No intelligent blocking**: If the queue is empty, `dequeue` must poll (busy loop) or return null.

        Solution: Use `java.util.concurrent.BlockingQueue`:

        ```java
        BlockingQueue<Task> queue = new ArrayBlockingQueue<>(100);

        // Producer: blocks if full
        queue.put(task);

        // Consumer: blocks if empty
        Task task = queue.take();
        ```

        **Classic pitfall:** Believing that `synchronized` is sufficient. Specialized concurrent structures are designed to minimize contention.

??? question "Question 11 — Position of the second minimum in a heap"
    In a min-heap, the second smallest element is always at index 1 of the array.

    ??? success "Answer"
        **False.** The second smallest element is at index 1 **or** 2 (one of the two children of the root).

        The heap invariant only guarantees that **each parent is ≤ its children**. There is no ordering relationship between siblings (index 1 and 2).

        ```
        Valid heap where the 2nd minimum is at index 2:

              1           Array: [1, 5, 2, 7, 8, 3, 4]
            /   \
           5     2   ← 2 is the 2nd minimum, at index 2
          / \   / \
         7   8 3   4
        ```

        **Classic pitfall:** Believing that a heap is sorted. Only the parent-child relationship is guaranteed.

??? question "Question 12 — Heap vs binary search tree"
    A binary heap and a binary search tree (BST) have the same ordering property.

    ??? success "Answer"
        **False.** The ordering properties are fundamentally different:

        | Property | Heap (min) | BST |
        |----------|------------|-----|
        | Relationship | parent ≤ children | left < parent < right |
        | Minimum | Root (O(1)) | Leftmost node (O(log n)) |
        | Search by key | O(n) | O(log n) if balanced |
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

??? question "Question 13 — Trees and graphs"
    Every tree is a graph, but not every graph is a tree.

    ??? success "Answer"
        **True.** A tree is a special case of a graph with additional constraints:

        **Definition of a tree**: A connected and acyclic graph.

        - **Connected**: There exists a path between every pair of vertices
        - **Acyclic**: No cycles

        Derived properties (for a tree with n vertices):

        - Exactly n-1 edges
        - A single path between each pair of vertices

        ```
              Tree                   Graph (not a tree)
                A                          A
               /|\                        /|\
              B C D                      B-C-D
                                          \_/
                                        (cycle!)
        ```

        **Note:** In graph theory, we refer to unrooted trees. In data structures, we often work with rooted trees (with a designated root).

??? question "Question 14 — Matrix vs adjacency list"
    For a graph with 1000 vertices and 3000 edges, an adjacency matrix uses less memory than an adjacency list.

    ??? success "Answer"
        **False.** Let us calculate:

        **Adjacency matrix**:

        - Size: V × V = 1000 × 1000 = **1,000,000 entries**
        - Even for a sparse graph, all cells exist

        **Adjacency list**:

        - Each edge appears in 2 lists (if undirected): 3000 × 2 = **6000 entries**
        - Plus V = 1000 list heads

        Ratio: 1,000,000 / 6000 ≈ **166× more memory for the matrix!**

        **Practical rule**:

        - **Dense** graph (E ≈ V²) → Matrix
        - **Sparse** graph (E << V²) → Adjacency list

        Here, E = 3000 and V² = 1,000,000, so E << V²: the graph is sparse.

        **Classic pitfall:** Using a matrix by default. Always analyze the density!

??? question "Question 15 — Maximum number of edges"
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

??? question "Question 16 — Edge list and neighborhood"
    In an edge list representation, the operation `incidentEdges(v)` (list the edges incident to v) runs in O(deg(v)).

    ??? success "Answer"
        **False.** In an edge list, `incidentEdges(v)` requires **traversing all edges** to find those incident to v, which gives **O(m)** where m is the total number of edges.

        This is the main weakness of the edge list compared to the adjacency list:

        | Operation | Edge list | Adjacency list |
        |-----------|-----------|----------------|
        | `incidentEdges(v)` | **O(m)** | **O(deg(v))** |
        | `areAdjacent(v, w)` | **O(m)** | **O(min(deg(v), deg(w)))** |
        | `insertEdge(e)` | **O(1)** | **O(1)** |
        | `removeEdge(e)` | **O(1)** | **O(1)** |
        | Space | O(n + m) | O(n + m) |

        The edge list is the **simplest** but least performant structure for neighborhood queries. It is useful when you primarily need to iterate over all edges.

        **Classic pitfall:** Confusing edge list and adjacency list. The edge list does **not** store edges by vertex, but in a global collection.

---

### Section 2 — Comparative Multiple Choice Questions

??? question "Question 1 — Complexity table"
    Complete the complexity table for a list of n elements:

    | Operation | ArrayList | LinkedList | Positional List* |
    |-----------|-----------|------------|------------------|
    | `get(k)` | ? | ? | ? |
    | `add(0, e)` | ? | ? | ? |
    | `add(k, e)` (middle) | ? | ? | ? |
    | `remove(position)` | ? | ? | ? |

    *Position already obtained beforehand

    ??? success "Answer"
        | Operation | ArrayList | LinkedList | Positional List* |
        |-----------|-----------|------------|------------------|
        | `get(k)` | **O(1)** | **O(n)** | **O(n)** |
        | `add(0, e)` | **O(n)** | **O(1)** | **O(1)** |
        | `add(k, e)` | **O(n)** | **O(n)** | **O(1)*** |
        | `remove(position)` | **O(n)** | **O(1)*** | **O(1)** |

        *With position/iterator already known

        **Key points:**

        - `ArrayList` excels for indexed access but suffers for insertions/deletions (shifts)
        - `LinkedList` has O(n) access but O(1) insertion/deletion **if you already have the position**
        - The positional list combines the best of both **when you keep the positions**

??? question "Question 2 — Graph representations"
    A transportation network has **500 stations** and **800 bidirectional connections**. We frequently need to list all neighboring stations of a given station and check whether two stations are directly connected.

    Which representation is the most suitable?

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

        - **Edge list**: Too slow for neighborhood queries — you must traverse **all** edges each time
        - **Matrix**: Wastes 250,000 entries for only 800 connections, and `incidentEdges` requires O(n)
        - **Adjacency list**: Compact space and operations proportional to the degree

        **Classic pitfall:** The edge list is simple to implement but becomes prohibitive as soon as you make frequent neighborhood queries.

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

    What is the complexity if `list` is a `LinkedList` of n elements of which k are negative?

    - [ ] A) O(n)
    - [ ] B) O(n + k)
    - [ ] C) O(n²)
    - [ ] D) O(nk)

    ??? success "Answer"
        **C) O(n²)**

        Let us analyze each operation in the loop:

        - `list.get(i)`: **O(i)** in a LinkedList (traversal from the beginning or the end)
        - `list.remove(i)`: **O(i)** to find + O(1) to remove

        The loop executes n times (minus the removals, but let us stick with n for the upper bound).

        Total cost: Σ O(i) for i from 0 to n ≈ O(n²)

        **Why not O(nk)?** Even the `get(i)` calls on positive elements cost O(i). It is not only the k removals that are expensive.

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
    We successively insert the keys **8, 5, 10, 3, 7, 2** into an empty min-heap.

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

??? question "Question 5 — Concurrent queue"
    For a producer-consumer pattern with a bounded buffer:

    - 4 producer threads
    - 2 consumer threads
    - The consumer must **block** if the buffer is empty

    Which Java structure is the most appropriate?

    - [ ] A) LinkedList with `synchronized`
    - [ ] B) ArrayDeque
    - [ ] C) ArrayBlockingQueue
    - [ ] D) PriorityQueue

    ??? success "Answer"
        **C) ArrayBlockingQueue**

        Analysis:

        | Option | Thread-safe | Blocking | Bounded buffer | Verdict |
        |--------|-------------|----------|----------------|---------|
        | A) LinkedList + synchronized | ✓ (manual) | ✗ | ✗ | Insufficient |
        | B) ArrayDeque | ✗ | ✗ | ✗ | Not thread-safe |
        | C) ArrayBlockingQueue | ✓ | ✓ | ✓ | **Perfect** |
        | D) PriorityQueue | ✗ | ✗ | ✗ | Not suitable |

        `ArrayBlockingQueue` offers:

        - `put(e)`: blocks if the buffer is full
        - `take()`: blocks if the buffer is empty
        - Fixed capacity (bounded buffer)
        - Thread-safe by design

        ```java
        BlockingQueue<Task> buffer = new ArrayBlockingQueue<>(100);

        // Producer
        buffer.put(task);  // Blocks if full

        // Consumer
        Task task = buffer.take();  // Blocks if empty
        ```

??? question "Question 6 — Memory of graph representations"
    A social network has **1 million users**. Each user has on average **200 friends** (undirected edges).

    What is the approximate memory (in entries) for each representation?

    - [ ] A) Matrix: 1 million, List: 200 million
    - [ ] B) Matrix: 1 trillion, List: 200 million
    - [ ] C) Matrix: 1 trillion, List: 400 million
    - [ ] D) Both use the same memory

    ??? success "Answer"
        **B) Matrix: 1 trillion, List: 200 million**

        **Adjacency matrix**:

        - Size: V² = (10⁶)² = 10¹² = 1 trillion entries
        - Independent of the number of edges!

        **Adjacency list**:

        - Each user stores their list of 200 friends
        - Total neighbor entries: 10⁶ × 200 = **200 million**
        - Plus V = 1 million list heads (negligible)

        **Ratio**: The matrix uses approximately **5000× more memory**!

        This graph is very sparse: E ≈ 100M vs V² = 10¹². The adjacency list is clearly the right choice.

??? question "Question 7 — Undo/Redo System"
    You are implementing an undo system with the following constraints:

    - Unlimited undo
    - Each action can be "replayed" (Redo) after Undo
    - A new action after Undo erases the Redo history

    What is the minimal structure needed?

    - [ ] A) A single Stack
    - [ ] B) Two Stacks
    - [ ] C) A Deque
    - [ ] D) A Positional List

    ??? success "Answer"
        **B) Two Stacks**

        The classic pattern uses two stacks:

        - **undoStack**: actions performed (LIFO — last action = first to undo)
        - **redoStack**: undone actions (LIFO — last undone = first to redo)

        ```java
        class UndoManager {
            Deque<Action> undoStack = new ArrayDeque<>();
            Deque<Action> redoStack = new ArrayDeque<>();

            void doAction(Action a) {
                a.execute();
                undoStack.push(a);
                redoStack.clear();  // New action erases redo
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

        - **S** = `[1, 2]` (1 at the bottom, 2 at the top)
        - **Q** = `[3, 3]` (3 at the front, 3 at the back)

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

??? question "Exercise 3 — Graph conversion"
    Consider the **directed** graph defined by the arcs: (A→B), (A→C), (B→C), (C→A), (D→B)

    a) Give the **edge list**

    b) Draw the **adjacency matrix** (4×4)

    c) Give the **adjacency list**

    d) What is the **in-degree** and **out-degree** of each vertex?

    ??? success "Answer"
        **a) Edge list:**

        ```
        E = [(A,B), (A,C), (B,C), (C,A), (D,B)]
        ```

        Each arc is an object storing its two endpoints (origin, destination). This is the simplest representation: a global collection of edges.

        ---

        **b) Adjacency matrix:**

        |   | A | B | C | D |
        |---|---|---|---|---|
        | **A** | 0 | 1 | 1 | 0 |
        | **B** | 0 | 0 | 1 | 0 |
        | **C** | 1 | 0 | 0 | 0 |
        | **D** | 0 | 1 | 0 | 0 |

        *Reading: row i, column j = 1 if arc (i→j) exists*

        ---

        **c) Adjacency list:**

        ```
        A → [B, C]
        B → [C]
        C → [A]
        D → [B]
        ```

        ---

        **d) Degrees:**

        | Vertex | Out-degree | In-degree |
        |--------|------------|-----------|
        | A | 2 (→B, →C) | 1 (C→) |
        | B | 1 (→C) | 2 (A→, D→) |
        | C | 1 (→A) | 2 (A→, B→) |
        | D | 1 (→B) | 0 |

        **Verification:** Σ deg_out = Σ deg_in = number of arcs = 5 ✓

---

## Second Hour: Application and Synthesis

### Section 4 — Design Scenarios

??? question "Scenario 1 — IoT Sensor Network"
    **Context**: A network of 200 environmental sensors. The sensors communicate with each other via radio link (limited range). The network has about 600 bidirectional links.

    **Required operations**:

    1. Add/remove a sensor (rare: maintenance)
    2. Add/remove a link when a sensor changes range (occasional)
    3. List all links in the network to calculate the total maintenance cost (frequent)
    4. Find all neighbors of a sensor for data routing (very frequent)

    **Questions**:

    1. For operation 3 (list all links), which representation is the most natural? Complexity?
    2. For operation 4 (find neighbors), compare the three representations (edge list, adjacency list, matrix). Which is the most efficient?
    3. Can we combine two representations? What is the trade-off?
    4. Is this graph dense or sparse? Justify and deduce the representation to avoid.

    ??? success "Answer"
        **1. List all links — Edge list:**

        The **edge list** is the most natural for iterating over all edges:

        - Each edge is an object stored in a collection
        - Iteration: **O(m)** — we traverse the list directly

        With an adjacency list, you must traverse all vertices and their neighbor lists, which also gives O(n + m) but with more overhead. Moreover, each edge appears in two neighbor lists, requiring a mechanism to avoid duplicates.

        With a matrix, you must scan the entire matrix O(n²) to find non-null entries, which is much worse.

        ```
        Edge list:
        [(A,B), (A,C), (B,D), (C,D), (D,E), ...]
        → Direct traversal in O(m)
        ```

        ---

        **2. Find neighbors — Adjacency list:**

        | Representation | `incidentEdges(v)` | Cost for this network |
        |----------------|--------------------|-----------------------|
        | Edge list | O(m) | O(600) per query |
        | Adjacency matrix | O(n) | O(200) |
        | **Adjacency list** | **O(deg(v))** | **O(6)** on average |

        The adjacency list is **100× more efficient** than the edge list for this operation, because the average degree is 2m/n = 1200/200 = 6.

        ---

        **3. Combining representations:**

        Yes, we can maintain **two representations simultaneously**:

        - **Edge list** for operation 3 (global iteration over edges)
        - **Adjacency list** for operation 4 (fast neighborhood)

        **Trade-off**:

        | Aspect | Single structure | Two structures |
        |--------|------------------|----------------|
        | Space | O(n + m) | O(n + m) × 2 |
        | Edge insertion | O(1) | O(1) × 2 operations |
        | Consistency | Automatic | Must keep both in sync |

        The overhead is acceptable if both operations are frequent. This is in fact the recommended approach in the book (Section 14.2).

        ---

        **4. Dense or sparse?**

        - n = 200, m = 600
        - Maximum edges (undirected): n(n-1)/2 = 19,900
        - Ratio: 600 / 19,900 ≈ **3%** → **sparse graph**

        **Representation to avoid**: The **adjacency matrix** which would use 200² = 40,000 entries for only 600 edges (66× more memory than necessary).

??? question "Scenario 2 — Version Control System"
    **Context**: A Git-like system for managing a project's history.

    **Required operations**:

    1. `commit`: save the current state
    2. `checkout`: go back to a previous commit
    3. `branch`: create a branch from the current commit
    4. `log`: display the history from the current commit to the initial commit

    **Questions**:

    1. For a linear history (without branches), what structure? Stack? List?
    2. Adding branches transforms the structure into what?
    3. Which representation for this graph? Justify.
    4. The `log` operation corresponds to which traversal? Complexity?

    ??? success "Answer"
        **1. Linear history:**

        A **singly linked list** where each commit points to its parent:

        ```
        HEAD → C3 → C2 → C1 → null
        ```

        Why not a Stack?

        - A stack does not allow navigating through the history (you can only pop)
        - We need to traverse the history (log) without destroying it
        - Checkout requires accessing an arbitrary commit

        Each commit stores:

        ```java
        class Commit {
            String id;
            Commit parent;
            Snapshot content;
            String message;
        }
        ```

        ---

        **2. With branches — DAG (Directed Acyclic Graph):**

        Branches create a **directed acyclic graph** (DAG):

        ```
                    C5 (feature)
                   /
        C1 ← C2 ← C3 ← C4 (main)
        ```

        - **Directed**: each commit points to its parent(s)
        - **Acyclic**: a commit cannot be its own ancestor
        - **Not a tree**: merges create nodes with 2 parents

        ```java
        class Commit {
            String id;
            List<Commit> parents;  // 1 parent normally, 2 for a merge
            // ...
        }
        ```

        ---

        **3. Representation — Adjacency list:**

        Each commit directly stores its references to parents (implicit adjacency list).

        Why not a matrix?

        - Very sparse graph: each vertex has only 1-2 outgoing arcs
        - Number of commits can be very large (millions)
        - A V² matrix would be enormous and mostly empty

        ---

        **4. Log operation — Traversal:**

        `log` performs an **ancestor traversal** from HEAD:

        ```java
        void log(Commit head) {
            Commit current = head;
            while (current != null) {
                print(current);
                current = current.parent;  // Linear case
            }
        }
        ```

        With branches and merges, it is a **breadth-first search (BFS)** or **DFS** on the ancestor DAG, with detection of already visited commits.

        **Complexity**: O(n) where n = number of ancestor commits, because each commit is visited only once.

??? question "Scenario 3 — Emergency Room Management"
    **Context**: An emergency room must manage the patient waiting queue. About 50 patients are waiting at any time during peak hours.

    **Required operations**:

    1. New patient arrives with a severity score (1 = minor, 10 = critical) — frequent
    2. Call the next patient to treat (the most urgent) — frequent
    3. A patient's condition changes during the wait (score update) — occasional
    4. Patient leaves before being treated — rare

    **Questions**:

    1. Is a simple FIFO queue suitable? Why?
    2. Which data structure is the most suitable for operations 1 and 2? Complexity?
    3. Operation 3 requires modifying the priority of an element already in the structure. Which variant of your structure supports this? Complexity?
    4. Compare the heap approach with the sorted list approach for this scenario.

    ??? success "Answer"
        **1. FIFO queue is insufficient:**

        No, a FIFO queue treats patients by **order of arrival**, not by urgency. A critical patient who arrived last should be treated before a minor patient who arrived first.

        Exception: at equal severity, the order of arrival is a good tiebreaker (FIFO within each priority level).

        ---

        **2. Priority queue (heap):**

        A **max-heap** based on the severity score:

        ```java
        PriorityQueue<Patient> emergencies = new PriorityQueue<>(
            Comparator.comparingInt(Patient::severity).reversed()  // Max-heap
        );
        ```

        | Operation | Complexity |
        |-----------|------------|
        | Insertion (new patient) | O(log n) |
        | Extract max (next patient) | O(log n) |

        With n ≈ 50, log₂(50) ≈ 6. Each operation is very fast.

        ---

        **3. Adaptable priority queue:**

        An **adaptable priority queue** (Section 9.5 of the book) allows modifying the key of an existing entry:

        ```java
        // With adaptable queue
        Entry<Integer, Patient> entry = pq.insert(severity, patient);

        // Later, if the condition changes:
        pq.replaceKey(entry, newSeverity);  // O(log n)
        ```

        The mechanism relies on a **locator**: each entry knows its position in the heap, allowing direct access for updates.

        | Operation | Simple heap | Adaptable heap |
        |-----------|-------------|----------------|
        | `insert` | O(log n) | O(log n) |
        | `removeMin/Max` | O(log n) | O(log n) |
        | `replaceKey` | **O(n)** (search + restructure) | **O(log n)** |
        | `remove(entry)` | **O(n)** | **O(log n)** |

        Without an adaptable queue, modifying the priority requires traversing the entire heap to find the element: O(n).

        ---

        **4. Heap vs sorted list:**

        | Criterion | Heap | Sorted list |
        |-----------|------|-------------|
        | Insertion | **O(log n)** | O(n) — find the position |
        | Extract max | **O(log n)** | **O(1)** — last element |
        | Priority update | **O(log n)** (adaptable) | O(n) — reposition |
        | Space | O(n) array | O(n) |

        For this scenario with frequent insertions and extractions, the **heap is superior**. The sorted list is only advantageous if extractions are much more frequent than insertions.

---

### Section 5 — Code Exercises

??? question "Exercise A — Analysis and correction"
    The following code implements cycle detection in a directed graph. It contains **at least two errors** (logic or performance).

    Identify them and propose corrections.

    ```java
    public class CycleDetector {
        private Map<Integer, List<Integer>> adjList;

        public boolean hasCycle() {
            Set<Integer> visited = new HashSet<>();
            for (Integer vertex : adjList.keySet()) {
                if (hasCycleFrom(vertex, visited)) {
                    return true;
                }
            }
            return false;
        }

        private boolean hasCycleFrom(Integer current, Set<Integer> visited) {
            if (visited.contains(current)) {
                return true;  // Cycle found!
            }
            visited.add(current);

            for (Integer neighbor : adjList.get(current)) {
                if (hasCycleFrom(neighbor, visited)) {
                    return true;
                }
            }
            return false;
        }
    }
    ```

    **Questions**:

    1. Identify the errors and explain why they are problematic.
    2. Correct the code.
    3. What is the complexity of the corrected algorithm?

    ??? success "Answer"
        **Error 1: False positives — confusion between "globally visited" and "in the current path"**

        The current code marks a vertex as visited and never unmarks it. This causes **false positives**:

        ```
        A → B → C
        A → C
        ```

        1. We explore A → B → C, marking all as visited
        2. We return to A, try A → C
        3. C is already visited → **false positive!** (this is not a cycle)

        A cycle exists only if we revisit a vertex **in the current path** (recursion stack), not just any previously explored vertex.

        ---

        **Error 2: Redundant work**

        Without distinguishing between "currently being explored" and "completely explored", we re-explore subgraphs that have already been analyzed, causing exponential complexity in the worst case.

        ---

        **Corrected code:**

        ```java
        public class CycleDetector {
            private Map<Integer, List<Integer>> adjList;

            public boolean hasCycle() {
                Set<Integer> visited = new HashSet<>();      // Exploration complete
                Set<Integer> inStack = new HashSet<>();      // In the current path

                for (Integer vertex : adjList.keySet()) {
                    if (!visited.contains(vertex)) {
                        if (hasCycleFrom(vertex, visited, inStack)) {
                            return true;
                        }
                    }
                }
                return false;
            }

            private boolean hasCycleFrom(Integer current,
                                         Set<Integer> visited,
                                         Set<Integer> inStack) {
                visited.add(current);
                inStack.add(current);  // Enter the path

                for (Integer neighbor : adjList.getOrDefault(current, List.of())) {
                    if (inStack.contains(neighbor)) {
                        return true;  // Cycle: we return to the current path
                    }
                    if (!visited.contains(neighbor)) {
                        if (hasCycleFrom(neighbor, visited, inStack)) {
                            return true;
                        }
                    }
                }

                inStack.remove(current);  // Exit the path
                return false;
            }
        }
        ```

        ---

        **Complexity of the corrected algorithm:**

        - Each vertex is visited **only once** thanks to the `visited` set
        - Each edge is examined **only once**
        - Complexity: **O(V + E)** where V = vertices, E = edges

        This is optimal for cycle detection in a directed graph.

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

        - Traverse the array
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

        - Array traversal: n iterations
        - Each heap operation (`offer`, `poll`, `peek`): O(log k)
        - Number of heap operations: at most 2n (offer + poll for each element)
        - **Total: O(n log k)**

        **Why min-heap and not max-heap?**

        With a min-heap of size k:

        - The root is the **smallest of the k largest** (the "threshold")
        - We compare each element to the threshold in O(1)
        - If larger, we eject the threshold and insert

        With a max-heap, we would not have efficient access to the minimum, thus no threshold.

        **Comparison with sorting:**

        | Approach | Complexity | For n=1M, k=10 |
        |----------|------------|-----------------|
        | Full sort | O(n log n) | ~20M operations |
        | Min-heap of size k | O(n log k) | ~3.3M operations |

        The heap is **6× more efficient** for this case.

---

### Section 6 — Synthesis Exercise

??? question "Energy Distribution Network — Data Architecture"
    A regional electrical network must manage:

    **Entities**:

    - **Nodes**: 500 points (power plants, substations, transformers, consumption points)
    - **Lines**: 1200 transmission lines, each with a maximum capacity (MW) and a maintenance cost
    - The network is **undirected** (electricity can flow in both directions)

    **Critical operations** (by frequency):

    1. **Monitoring**: For a given node, list all connected lines and their current load (very frequent)
    2. **Cost calculation**: Traverse all lines to calculate the total maintenance cost (daily)
    3. **Failure**: Remove a defective line and check if the network remains connected (occasional)
    4. **Extension**: Add a new line between two nodes (rare)

    ---

    **Question 1**: Choice of main representation

    Compare the three representations (edge list, adjacency list, matrix) for this network. Which is the most suitable as the main structure?

    **Question 2**: Storing line attributes

    Each line has attributes (capacity, cost, current load). How to store them in each representation?

    **Question 3**: Cost calculation operation

    For operation 2, is it advantageous to maintain an edge list in addition to the main structure? Justify.

    **Question 4**: Connectivity detection after failure

    After removing a line, how to verify that the network remains connected? Which representation facilitates this verification?

    ??? success "Answer"
        **Question 1: Choice of representation**

        | Criterion | Edge list | Adjacency list | Matrix |
        |-----------|-----------|----------------|--------|
        | Space | O(n + m) = 1,700 | O(n + m) = 1,700 | O(n²) = 250,000 |
        | Op. 1: neighbors of a node | O(m) = O(1200) | **O(deg(v))** ≈ O(5) | O(n) = O(500) |
        | Op. 2: all lines | **O(m)** = O(1200) | O(n + m) = O(1700) | O(n²) = O(250,000) |
        | Op. 3: remove a line | O(1)* | O(deg(v)) | O(1) |
        | Op. 4: add a line | O(1) | O(1) | O(1) |

        **The adjacency list** is the best main structure because operation 1 (the most frequent) is O(deg(v)) instead of O(m).

        The graph is sparse: m = 1200 vs n² = 250,000, so the matrix should be avoided.

        ---

        **Question 2: Storing attributes**

        - **Edge list**: Each Edge object directly stores its attributes — this is the most natural.

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

        **Yes**, it is advantageous. With an adjacency list alone, the total cost calculation requires:

        ```
        For each vertex v:
            For each edge incident to v:
                accumulate the cost
        ```

        Problem: each edge is counted **twice** (once per each endpoint in an undirected graph). You must either divide by 2 or mark visited edges.

        With an **auxiliary edge list**:

        ```
        total_cost = 0
        For each edge e in the edge list:
            total_cost += e.cost
        ```

        Simple, direct, no duplicates. Complexity: **O(m)**.

        **Trade-off**: Keeping both structures in sync during additions/removals. Additional cost: O(1) per modification operation.

        ---

        **Question 4: Connectivity verification**

        After removing an edge, we verify connectivity by a **traversal** (BFS or DFS) from any vertex:

        - If the traversal visits **all n vertices** → the network is still connected
        - Otherwise → the removal has disconnected the network

        **Complexity**: O(n + m) for the traversal.

        **The adjacency list** facilitates this verification because the BFS/DFS traversal needs `incidentEdges(v)` at each vertex, an operation that is O(deg(v)) with the adjacency list vs O(m) with the edge list.

        With the edge list alone, the traversal would be O(n × m) in total — much too slow.

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

### Section 7 — Summary

#### Decision Tree: Choosing Your Structure

```
PRIMARY ACCESS?
│
├─► By index/numerical position
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
│   └─► Concurrent?
│       ├─► Yes + blocking → BlockingQueue
│       └─► No → ArrayDeque or LinkedList
│
└─► Relationships between entities
    └─► GRAPH
        └─► Dominant operation?
            ├─► Iterate over all edges → Edge list
            ├─► Neighborhood / traversal → Adjacency list
            └─► Check adjacency in O(1) → Adjacency matrix (if dense graph)
```

#### Classic Pitfalls — Summary

| Pitfall | Reality |
|---------|---------|
| LinkedList always faster for insertions | Only if you already have the position! `get(i)` = O(n) |
| Heap = sorted array | No! Only guarantee: parent ≤ children |
| `synchronized` = performant thread-safety | Thread-safe yes, but contention under load |
| Position = stable index | Position invalid after deletion of **its** element |
| Edge list = fast for neighborhood | No! `incidentEdges(v)` = O(m), not O(deg(v)) |
| Matrix = default choice for graphs | Wastes O(V²) memory for sparse graphs |
| Amortized complexity = each operation | No, it is an average over n operations |
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

*O(1) amortized at the end of the list

**With position/iterator already known

***With locator structure

| Graph representation | Space | Add edge | Check edge | List neighbors | List all edges |
|----------------------|-------|----------|------------|----------------|----------------|
| Edge list | O(V + E) | O(1) | O(E) | O(E) | **O(E)** |
| Adjacency list | O(V + E) | O(1) | O(deg) | O(deg) | O(V + E) |
| Adjacency matrix | O(V²) | O(1) | O(1) | O(V) | O(V²) |

---

## References

* Goodrich, Tamassia, Goldwasser. *Data Structures and Algorithms in Java*, 6th Edition.
    * Chapter 6: Stacks, Queues, and Deques
    * Chapter 7: List and Iterator ADTs
    * Chapter 9: Priority Queues
    * Chapter 14.1-14.2: Graphs
* Java Documentation:
    * [`java.util.Deque`](https://docs.oracle.com/javase/8/docs/api/java/util/Deque.html)
    * [`java.util.PriorityQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html)
    * [`java.util.concurrent.BlockingQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html)
