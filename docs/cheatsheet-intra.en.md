# Cheatsheet — Midterm IFT2015

Review cheatsheet for the IFT2015 midterm exam — Data Structures. Covers **sections 1 to 4 (excluding 4.3)** of the course outline, based on *Data Structures and Algorithms in Java, 6th Edition* (Goodrich, Tamassia, Goldwasser).

---

## 1. Introduction

!!! abstract "Objectives"
    Understand why the choice of a data structure determines the performance of an algorithm.

- **Structure == Function**: a structure is defined by its dominant operations and their asymptotic cost, not merely as a container.
- **Structure == Speedup**: the invariants maintained by a structure enable more efficient algorithms.

!!! warning "Classic pitfalls"
    - Confusing **structure** and **algorithm** — a structure organizes data, an algorithm manipulates it.
    - "More code = slower" — asymptotic complexity does not depend on the number of lines.
    - "One structure is generally better" — every structure involves a trade-off; the choice depends on the operation profile.
    - Forgetting the **cost of maintaining invariants** — keeping a sorted order, heap-order, etc. has a price.

---

## 2. Abstract Data Types (ADT)

### 2.1 ArrayList and LinkedList

**Book: §7.1, §7.2.1–7.2.3**

!!! abstract "Objectives"
    Compare dynamic array and linked list implementations of the List ADT.

- **ArrayList**: Resizable dynamic array. Direct access by index in $O(1)$. Insertion/deletion in $O(n)$ due to element shifting. Amortized $O(1)$ resizing via array doubling.
- **SinglyLinkedList**: Chain of nodes with a `next` pointer. Insertion/deletion at the head in $O(1)$. Index-based access in $O(n)$ — the list must be traversed.
- **DoublyLinkedList**: `prev` and `next` pointers. Deletion in $O(1)$ if the node reference is available. Uses sentinel nodes (header/trailer).

| Operation | ArrayList | DoublyLinkedList |
|---|---|---|
| `get(i)` | $O(1)$ | $O(n)$ |
| `set(i, e)` | $O(1)$ | $O(n)$ |
| `add(i, e)` | $O(n)$ | $O(n)$* |
| `remove(i)` | $O(n)$ | $O(n)$* |
| `size()`, `isEmpty()` | $O(1)$ | $O(1)$ |
| `addFirst` / `addLast` | $O(n)$ / $O(1)$† | $O(1)$ |
| `removeFirst` / `removeLast` | $O(n)$ / $O(1)$† | $O(1)$ |

*\* $O(n)$ to find position i, $O(1)$ if the position is already known.*
*† $O(1)$ at the end of the array only, $O(n)$ at the beginning.*

!!! tip "Amortized complexity of resizing"
    With the **array doubling** strategy, the total cost of $n$ insertions at the end is $O(n)$, giving an **amortized $O(1)$** cost per insertion. The array is resized only $O(\log n)$ times.

---

### 2.2 Positional List

**Book: §7.3, §7.4, §7.5, §7.6**

!!! abstract "Objectives"
    Understand the PositionalList ADT and the concept of a stable position.

- **PositionalList**: ADT based on the concept of *position* rather than index. A position is a stable abstraction — it remains valid even after insertions/deletions elsewhere in the list. Ideally implemented with a DoublyLinkedList.
- Main operations: `first()`, `last()`, `before(p)`, `after(p)`, `addBefore(p, e)`, `addAfter(p, e)`, `set(p, e)`, `remove(p)`.

| Operation | Complexity (DoublyLinkedList) |
|---|---|
| `size()`, `isEmpty()` | $O(1)$ |
| `first()`, `last()`, `before(p)`, `after(p)` | $O(1)$ |
| `addFirst(e)`, `addLast(e)` | $O(1)$ |
| `addBefore(p, e)`, `addAfter(p, e)` | $O(1)$ |
| `set(p, e)` | $O(1)$ |
| `remove(p)` | $O(1)$ |
| Space | $O(n)$ |

!!! warning "Classic pitfalls"
    - **Position ≠ index.** An iterator is not a persistent position.
    - Java does not expose positions in `java.util.LinkedList` in order to protect internal invariants.
    - All operations are $O(1)$ **only if the position is already known** — finding a position by search remains $O(n)$.

---

### 2.3 Favorites List

**Book: §7.7**

!!! abstract "Objectives"
    Compare two strategies for managing access frequencies.

- **FavoritesList (sorted by counter)**: Elements sorted by decreasing access frequency. `access(e)` increments the counter and repositions the element (partial insertion sort). `getFavorites(k)` returns the top k elements in $O(k)$.
- **FavoritesListMTF (Move-to-Front)**: Each access moves the element to the front of the list. Exploits **locality of reference**. `getFavorites(k)` requires a partial sort in $O(kn)$ since the list is not sorted.

| Operation | Sorted | Move-to-Front |
|---|---|---|
| `access(e)` | $O(n)$ | $O(n)$ to search, $O(1)$ to move |
| `remove(e)` | $O(n)$ | $O(n)$ |
| `getFavorites(k)` | $O(k)$ | $O(kn)$ |

!!! tip "Sorted vs MTF trade-off"
    **MTF** is better when **locality of reference is strong** (repetitive sequences — recently accessed elements are at the front). The **sorted** version is better for **uniform access patterns** where the frequency-based ordering is more stable.

---

### 2.4 Stack, Queue, Deque

**Book: Chapter 6 (§6.1–6.3)**

!!! abstract "Objectives"
    Master the three restricted-access ADTs and their implementations.

- **Stack (LIFO)**: Access only at the top. `push(e)`, `pop()`, `top()`. Analogy: a stack of plates.
- **Queue (FIFO)**: Add at the rear, remove from the front. `enqueue(e)`, `dequeue()`, `first()`. Analogy: a waiting line.
- **Deque (Double-ended Queue)**: Insertion and deletion at both ends. Generalizes Stack and Queue. `addFirst`, `addLast`, `removeFirst`, `removeLast`.

| Operation | Stack (Array) | Queue (Circular Array) | Deque (DLL or Circular Array) |
|---|---|---|---|
| `push` / `enqueue` / `addFirst` | $O(1)$ | $O(1)$ | $O(1)$ |
| `pop` / `dequeue` / `removeFirst` | $O(1)$ | $O(1)$ | $O(1)$ |
| `top` / `first` / `last` | $O(1)$ | $O(1)$ | $O(1)$ |
| `addLast` / `removeLast` | — | — | $O(1)$ |
| `size`, `isEmpty` | $O(1)$ | $O(1)$ | $O(1)$ |
| Space | $O(N)$* | $O(N)$* | $O(N)$* or $O(n)$ |

*\* $N$ = allocated array size, $n$ = number of actual elements.*

**Java correspondence (`java.util.Deque`):**

| Our ADT | Throws exception | Special value |
|---|---|---|
| `first()` | `getFirst()` | `peekFirst()` |
| `last()` | `getLast()` | `peekLast()` |
| `addFirst(e)` | `addFirst(e)` | `offerFirst(e)` |
| `addLast(e)` | `addLast(e)` | `offerLast(e)` |
| `removeFirst()` | `removeFirst()` | `pollFirst()` |
| `removeLast()` | `removeLast()` | `pollLast()` |

!!! warning "Classic pitfalls"
    - **Do not use `java.util.Stack`** — legacy class, not thread-safe, inherits from `Vector`.
    - Stack and Queue are **access policies**, not fundamentally different structures — both can be implemented by a Deque.
    - Circular queue: `front`, `rear = (front + size) % capacity`.

---

### 2.5 Concurrent Queues

**Not covered in the book.**

!!! abstract "Objectives"
    Understand the concurrency challenges for queues and the Java solutions.

- In a concurrent (multi-threaded) context, classic structures are not thread-safe.
- **Thread-safe vs non thread-safe**: simultaneous accesses can corrupt data.
- **Blocking vs non-blocking**: `BlockingQueue` blocks the thread if the queue is full or empty.
- **`synchronized`**: simple lock but costly (contention, risk of deadlock).
- **`java.util.concurrent`**: `ArrayBlockingQueue`, `ConcurrentLinkedQueue` — structures optimized for concurrency.

!!! warning "Classic pitfalls"
    - `synchronized` is not always sufficient — granularity too coarse.
    - Thread-safe ≠ always preferable (synchronization overhead).
    - Beware of **deadlocks** and **contention**.

---

### 2.6 Priority Queue and Heap

**Book: Chapter 9 (§9.1–9.5)**

!!! abstract "Objectives"
    Understand the Priority Queue ADT and its implementations, in particular the Heap.

- **Priority Queue (ADT)**: Collection of (key, value) entries where the entry with the minimum key is always accessible. Operations: `insert(k, v)`, `min()`, `removeMin()`.
- **Unsorted List PQ**: Insertion in $O(1)$, finding the min in $O(n)$.
- **Sorted List PQ**: Insertion in $O(n)$ (order maintained), min/removeMin in $O(1)$.
- **Heap (Binary Heap)**: **Complete** binary tree satisfying the heap-order invariant: the key of each node $\leq$ the keys of its children. Stored in an array. Insertion and deletion in $O(\log n)$ via upheap/downheap.
- **Adaptable PQ (location-aware)**: Extends the heap to support `remove(entry)`, `replaceKey(entry, k)`, `replaceValue(entry, v)` in $O(\log n)$, thanks to an `index` field in each entry.

| Operation | Unsorted List | Sorted List | Heap | Adaptable Heap |
|---|---|---|---|---|
| `size`, `isEmpty` | $O(1)$ | $O(1)$ | $O(1)$ | $O(1)$ |
| `insert` | $O(1)$ | $O(n)$ | $O(\log n)$* | $O(\log n)$ |
| `min` | $O(n)$ | $O(1)$ | $O(1)$ | $O(1)$ |
| `removeMin` | $O(n)$ | $O(1)$ | $O(\log n)$* | $O(\log n)$ |
| `remove(entry)` | — | — | — | $O(\log n)$ |
| `replaceKey(entry, k)` | — | — | — | $O(\log n)$ |
| `replaceValue(entry, v)` | — | — | — | $O(1)$ |
| Space | $O(n)$ | $O(n)$ | $O(n)$ | $O(n)$ |

*\* Amortized if dynamic array.*

!!! tip "Heap properties"
    - **Heap-order invariant**: For every node $v$ (except the root), $\text{key}(v) \geq \text{key}(\text{parent}(v))$.
    - **Structural property**: Complete binary tree — all levels are full except the last, which is filled from left to right.
    - **Height**: $O(\log n)$ because the tree is complete.
    - **Array storage**: for a node at index $i$ — left child = $2i + 1$, right child = $2i + 2$, parent = $\lfloor(i-1)/2\rfloor$.
    - **upheap**: after insertion (at the last position), bubble up while the key is smaller than the parent's key.
    - **downheap**: after removeMin (replacing the root with the last element), sink down by swapping with the smaller child.

!!! warning "Classic pitfalls"
    - **Heap ≠ BST**: a heap is not sorted; it only guarantees that the minimum is at the root.
    - A heap **does not support** searching for an arbitrary element in $O(\log n)$.

---

## 3. Graphs I

**Book: §14.1, §14.2 (excluding §14.2.3)**

!!! abstract "Objectives"
    Know the Graph ADT and compare the four classic representations.

- **Graph (ADT)**: A set of vertices $V$ and edges $E$. Can be directed (digraph) or undirected. Operations: `numVertices()`, `numEdges()`, `vertices()`, `edges()`, `getEdge(u,v)`, `outgoingEdges(v)`, `incomingEdges(v)`, `insertVertex(x)`, `insertEdge(u,v,x)`, `removeVertex(v)`, `removeEdge(e)`, `outDegree(v)`, `inDegree(v)`.
- **Edge List**: Two unordered lists — one for vertices, one for edges. Simple but inefficient for queries.
- **Adjacency List**: Each vertex maintains a list of its incident edges. Good space/time trade-off.
- **Adjacency Map**: Like the adjacency list but uses a map (hashing) for incident edges. Access to a specific edge in expected $O(1)$.
- **Adjacency Matrix**: $n \times n$ matrix where `matrix[i][j]` stores edge $(i,j)$. $O(1)$ access but $O(n^2)$ space.

| Operation | Edge List | Adj. List | Adj. Map | Adj. Matrix |
|---|---|---|---|---|
| `numVertices()`, `numEdges()` | $O(1)$ | $O(1)$ | $O(1)$ | $O(1)$ |
| `vertices()` | $O(n)$ | $O(n)$ | $O(n)$ | $O(n)$ |
| `edges()` | $O(m)$ | $O(m)$ | $O(m)$ | $O(m)$ |
| `getEdge(u,v)` | $O(m)$ | $O(\min(d_u, d_v))$ | $O(1)$ exp. | $O(1)$ |
| `outDegree(v)`, `inDegree(v)` | $O(m)$ | $O(1)$ | $O(1)$ | $O(n)$ |
| `outgoingEdges(v)`, `incomingEdges(v)` | $O(m)$ | $O(d_v)$ | $O(d_v)$ | $O(n)$ |
| `insertVertex(x)` | $O(1)$ | $O(1)$ | $O(1)$ | $O(n^2)$ |
| `removeVertex(v)` | $O(m)$ | $O(d_v)$ | $O(d_v)$ | $O(n^2)$ |
| `insertEdge(u,v,x)` | $O(1)$ | $O(1)$ | $O(1)$ exp. | $O(1)$ |
| `removeEdge(e)` | $O(1)$ | $O(1)$ | $O(1)$ exp. | $O(1)$ |
| **Space** | $O(n+m)$ | $O(n+m)$ | $O(n+m)$ | $O(n^2)$ |

*$n$ = number of vertices, $m$ = number of edges, $d_v$ = degree of $v$.*

!!! tip "How to choose a representation?"
    - **Dense** ($m \approx n^2$) → **Adjacency Matrix**.
    - **Sparse** ($m \ll n^2$) → **Adjacency List or Map**.
    - **Need to check if an edge exists** → **Adj. Map** or **Matrix**.
    - **Iterating over incident edges** → **Adj. List/Map**.

!!! warning "Classic pitfalls"
    - **Graph ≠ tree** — a tree is a connected acyclic graph.
    - Do not use a matrix by default — $O(n^2)$ space is often unnecessary.
    - Distinguish between **directed** and **undirected** graphs (symmetry in the matrix, double entry in the adj. list).

---

## 4. Trees and Trie

### 4.1 Trees and Trie

**Book: §8.1, §13.3**

!!! abstract "Objectives"
    Know the Tree and Binary Tree ADTs, tree traversals, and the Trie structure.

- **Tree (ADT)**: Hierarchical structure with a root, internal nodes, and leaves. Each node has a parent (except the root) and zero or more children. Operations: `root()`, `parent(p)`, `children(p)`, `numChildren(p)`, `isInternal(p)`, `isExternal(p)`, `isRoot(p)`, `size()`, `isEmpty()`.
- **Binary Tree (ADT)**: Tree where each node has at most 2 children (left and right). Additional operations: `left(p)`, `right(p)`, `sibling(p)`.
- **Trie**: Tree where each edge is labeled with a character. Paths from the root to leaves represent strings. Used for prefix search and autocompletion.

**Complexity — Binary Tree (linked structure):**

| Operation | Complexity |
|---|---|
| `size`, `isEmpty` | $O(1)$ |
| `root`, `parent`, `left`, `right`, `sibling`, `children`, `numChildren` | $O(1)$ |
| `isInternal`, `isExternal`, `isRoot` | $O(1)$ |
| `addRoot`, `addLeft`, `addRight`, `set`, `attach`, `remove` | $O(1)$ |
| `depth(p)` | $O(d_p + 1)$ |
| `height` | $O(n)$ |

**Complexity — General Tree (linked structure):**

| Operation | Complexity |
|---|---|
| `size`, `isEmpty` | $O(1)$ |
| `root`, `parent`, `isRoot`, `isInternal`, `isExternal` | $O(1)$ |
| `numChildren(p)` | $O(1)$ |
| `children(p)` | $O(c_p + 1)$ |
| `depth(p)` | $O(d_p + 1)$ |
| `height` | $O(n)$ |

*$c_p$ = number of children of $p$, $d_p$ = depth of $p$.*

**Tree traversals:**

| Traversal | Order | Use case |
|---|---|---|
| **Preorder** | Root → Children | Table of contents, tree copy |
| **Postorder** | Children → Root | Disk space calculation, deletion |
| **Inorder** (binary) | Left → Root → Right | Sorted visit of a BST |
| **BFS (level-order)** | Level by level | Shortest path |

!!! tip "Trie properties"
    - Space: at most $n+1$ nodes ($n$ = sum of lengths of all strings).
    - Searching for a word of length $m$: $O(m \times |\Sigma|)$ in the worst case, $O(m)$ if children are stored in a map.
    - Variants: Compressed Trie, Suffix Trie.

---

### 4.2 Binary Search Trees (BST)

**Book: §8.3, §8.4, §11.1**

!!! abstract "Objectives"
    Understand the BST invariant, search/insertion/deletion operations, and degenerate cases.

- **BST**: Binary tree where for each node $v$: all keys in the left subtree $<$ key($v$) $<$ all keys in the right subtree. An inorder traversal yields elements in ascending order.
- Search, insertion, deletion: $O(h)$ where $h$ is the height.
- Best case: $h = O(\log n)$ (balanced tree).
- Worst case: $h = O(n)$ (degenerate tree — insertions in ascending/descending order).

| Operation | Complexity |
|---|---|
| `size`, `isEmpty` | $O(1)$ |
| `get(k)`, `put(k,v)`, `remove(k)` | $O(h)$ |
| `firstEntry`, `lastEntry` | $O(h)$ |
| `ceilingEntry`, `floorEntry`, `lowerEntry`, `higherEntry` | $O(h)$ |
| `subMap` | $O(s + h)$ |
| `entrySet`, `keySet`, `values` | $O(n)$ |

*$h$ = height of the tree, $s$ = number of results in subMap.*

!!! warning "Classic pitfalls"
    - Never **assume** a BST is balanced — the height $h$ can be $n$.
    - **Height ≠ size**: a tree of size $n$ can have a height ranging from $\lfloor\log n\rfloor$ to $n-1$.
    - Degenerate case: insertions in order → linked list → $h = n$.
    - **Deleting a node with 2 children**: replace with the inorder successor (min of the right subtree) or the inorder predecessor (max of the left subtree).

---

## Summary — Categorization of Data Structures

### Lists

| Structure | Access Type | Main Use Case |
|---|---|---|
| ArrayList | Index | Frequent access by position |
| SinglyLinkedList | Sequential | Insertion/deletion at the head |
| DoublyLinkedList | Bidirectional sequential | Base for PositionalList, Deque |
| PositionalList | Position | Insertion/deletion with a stable cursor |
| FavoritesList | Frequency | Top-k elements (sorted by counter) |
| FavoritesListMTF | Frequency + locality | Adaptive cache (move-to-front) |

### Stacks, Queues, Deques

| Structure | Discipline | Implementations |
|---|---|---|
| Stack | LIFO | ArrayStack, LinkedStack |
| Queue | FIFO | ArrayQueue (circular), LinkedQueue |
| Deque | Double-ended | ArrayDeque (circular), LinkedDeque |

### Priority Queues

| Structure | Invariant | Use Case |
|---|---|---|
| UnsortedPriorityQueue | None | Fast insertion, infrequent removeMin |
| SortedPriorityQueue | Sorted list | Frequent removeMin, infrequent insertions |
| HeapPriorityQueue | Heap-order + complete | General use (balanced insert + removeMin) |
| HeapAdaptablePQ | Heap + location-aware | Priority updates (Dijkstra, scheduling) |

### Graphs

| Structure | Space | Use Case |
|---|---|---|
| EdgeList | $O(n+m)$ | Prototyping, small graphs |
| AdjacencyList | $O(n+m)$ | General use, sparse graphs |
| AdjacencyMap | $O(n+m)$ | Frequent getEdge queries |
| AdjacencyMatrix | $O(n^2)$ | Dense graphs, $O(1)$ getEdge |

### Trees

| Structure | Property | Use Case |
|---|---|---|
| General Tree | Hierarchy, n-ary | File systems, DOM |
| Binary Tree | At most 2 children | Base for BST, heap |
| BST | Left < root < right ordering | Search, sorting, ordered map |
| Trie | Paths = strings | Autocompletion, dictionary, prefix search |
