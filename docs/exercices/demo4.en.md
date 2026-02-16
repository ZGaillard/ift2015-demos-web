# Demo 4: Priority Queues and Heaps

This demo covers **Chapter 9** (*Priority Queues*) from the textbook *Data Structures and Algorithms in Java (6th ed.)*.

!!! abstract "Learning Objectives"

    By the end of this demo, you should be able to:

    * Understand the **heap invariant** (heap-order property)
    * Distinguish a **heap** from a **binary search tree**
    * Analyze the complexity of `insert` and `removeMin` operations
    * Implement a heap with an **array** (implicit representation)
    * Compare the different priority queue implementations
    * Understand **adaptable priorities** and their cost

---

## Theoretical Reminders

### The Priority Queue ADT

A **priority queue** is a collection of elements where each element has a **key** (priority) and a **value**. The main operations are:

| Operation | Description |
| --- | --- |
| `insert(k, v)` | Inserts an entry with key `k` and value `v` |
| `min()` | Returns the entry with the smallest key (without removing it) |
| `removeMin()` | Removes and returns the entry with the smallest key |
| `size()` | Returns the number of entries |
| `isEmpty()` | Checks whether the queue is empty |

!!! info "Convention"

    By default, we consider a **min-heap**: the smallest key has the highest priority.
    A **max-heap** reverses this relationship (largest key = highest priority).

### Comparison of Implementations

| Implementation | `insert` | `removeMin` | `min` | Use Case |
| --- | --- | --- | --- | --- |
| Unsorted list | O(1) | O(n) | O(n) | Few extractions |
| Sorted list | O(n) | O(1) | O(1) | Few insertions |
| **Heap** | O(log n) | O(log n) | O(1) | General purpose |

---

### The Binary Heap

A **binary heap** is a binary tree that satisfies two properties:

#### 1. Structural Property: Complete Binary Tree

A **complete** binary tree is filled level by level, from left to right:

```
         ✓ Complete                    ✗ Not complete
            4                              4
          /   \                          /   \
         9     7                        9     7
        / \   /                        / \     \
       15 12 6                        15 12     6
```

!!! note "Height of a heap"

    A heap of **n** elements has a height of **h = ⌊log₂ n⌋**.
    This property guarantees O(log n) operations.

#### 2. Order Property: Heap-Order Property

For every node **v** (except the root), the key of **v** is **greater than or equal to** the key of its parent:

$$\forall v \neq \text{root} : \text{key}(\text{parent}(v)) \leq \text{key}(v)$$

```
         Valid Min-Heap               INVALID Min-Heap
              4                              4
            /   \                          /   \
           9     7                        2     7    ← 2 < 4 violates the invariant!
          / \   /                        / \   /
         15 12 6                        15 12 6
```

!!! warning "Classic pitfall: Heap ≠ Binary Search Tree"

    In a **heap**, the parent is smaller than its children, but there is **no relationship** between the left and right children.

    In a **BST**, the left child < parent < right child.

    ```
            Valid Heap                    Equivalent BST
                4                              9
              /   \                          /   \
             9     7     ← 9 > 7 is OK!    4    12
            / \   /                        / \     \
           15 12 6                        2   7    15
    ```

---

### Array Representation

A heap is efficiently represented in an **array** thanks to the completeness property:

```
Heap:           4
              /   \
             9     7
            / \   /
           15 12 6

Array:  [4, 9, 7, 15, 12, 6]
Index:   0  1  2   3   4  5
```

Parent-child relationships are computed using **index arithmetic**:

| Relationship | Formula | Example (i=1, value 9) |
| --- | --- | --- |
| Parent of i | `(i - 1) / 2` | parent(1) = 0 → value 4 |
| Left child of i | `2i + 1` | left(1) = 3 → value 15 |
| Right child of i | `2i + 2` | right(1) = 4 → value 12 |

!!! tip "Advantage of the array"

    No pointers → memory savings and better cache locality.

---

### Heap Operations

#### Insertion: Up-Heap Bubbling

1. Add the element at the **end** of the array (next free position)
2. **Bubble up** (up-heap) as long as the element is smaller than its parent

```
Insertion of 3 into [4, 9, 7, 15, 12, 6]:

Step 1: Add at the end
[4, 9, 7, 15, 12, 6, 3]
                    ↑ index 6

Step 2: Compare with parent (index 2, value 7)
3 < 7 → Swap
[4, 9, 3, 15, 12, 6, 7]
       ↑

Step 3: Compare with parent (index 0, value 4)
3 < 4 → Swap
[3, 9, 4, 15, 12, 6, 7]
 ↑ New root!
```

**Complexity**: O(log n) — at most h swaps where h is the height.

#### Removing the Minimum: Down-Heap Bubbling

1. Replace the root with the **last** element
2. **Bubble down** (down-heap) by swapping with the smallest child

```
removeMin() on [3, 9, 4, 15, 12, 6, 7]:

Step 1: Replace root with last element
[7, 9, 4, 15, 12, 6]
 ↑ 7 replaces 3, remove the last

Step 2: Compare with children (9 and 4)
min(9, 4) = 4, and 7 > 4 → Swap with 4
[4, 9, 7, 15, 12, 6]
    ↓
       7 moves down to the right

Step 3: 7 has one child (6)
7 > 6 → Swap
[4, 9, 6, 15, 12, 7]

Step 4: 7 has no more children → Done
```

**Complexity**: O(log n) — at most h swaps.

---

### Heap-Sort

**Heap-sort** leverages the heap property to sort in O(n log n):

1. **Phase 1 (Construction)**: Build a heap from the data — O(n)
2. **Phase 2 (Extraction)**: Extract `removeMin()` n times — O(n log n)

!!! note "Construction in O(n) — Bottom-up Heap Construction"

    Rather than n insertions in O(n log n), a heap can be built in O(n) using the **bottom-up** method: start from the leaves and apply down-heap toward the root.

---

### Adaptable Priority Queue

An **adaptable priority queue** allows modifying entries after insertion:

| Operation | Description | Complexity (heap) |
| --- | --- | --- |
| `remove(entry)` | Removes an arbitrary entry | O(log n)* |
| `replaceKey(entry, k)` | Changes the key of an entry | O(log n)* |
| `replaceValue(entry, v)` | Changes the value of an entry | O(1) |

*\* Requires knowing the **position** of the entry in the heap.*

!!! warning "Pitfall: Localization Cost"

    Without a localization mechanism (such as a `Map` key→position), finding an entry costs **O(n)**.
    With localization, `replaceKey` potentially requires an up-heap **or** a down-heap.

---

## Part 1 — Theoretical Exercises

### 1.1 True or False

For each statement, indicate whether it is **true** or **false** and justify your answer.

??? question "Question 1 — Order in a heap"
    In a min-heap, the second smallest element is always at index 1 or 2 of the array.

    ??? success "Answer"
        **True.** The smallest element is at the root (index 0). The second smallest must be a child of the root, because all other elements have a smaller ancestor. The children of the root are at indices 1 (left) and 2 (right).

        **Note**: We cannot know which of the two (index 1 or 2) is the second smallest without comparing them!

??? question "Question 2 — Heap and sorting"
    A min-heap of n elements stores the elements in ascending order in the underlying array.

    ??? success "Answer"
        **False.** This is a classic pitfall! A heap is **not** sorted. The only guarantee is that each parent is smaller than its children, but there is no ordering relationship between elements at the same level or between cousins.

        Example of a valid heap: `[1, 5, 2, 7, 6, 3, 4]`

        - 1 < 5 and 1 < 2 ✓
        - 5 < 7 and 5 < 6 ✓
        - 2 < 3 and 2 < 4 ✓

        But the array is clearly not sorted (5 > 2).

??? question "Question 3 — Construction complexity"
    Building a heap of n elements by performing n successive insertions has a complexity of O(n).

    ??? success "Answer"
        **False.** Performing n successive insertions costs O(n log n) because each insertion costs O(log n) in the worst case.

        However, the **bottom-up** method (heapify) allows building a heap in O(n). This method starts from the leaves and applies down-heap from bottom to top. The analysis shows that the sum of work is linear because the nodes at lower levels (which are numerous) do little work.

??? question "Question 4 — Heap height"
    A heap containing 100 elements has a height of 7.

    ??? success "Answer"
        **False.** The height of a heap of n elements is h = ⌊log₂ n⌋.

        For n = 100: h = ⌊log₂ 100⌋ = ⌊6.64...⌋ = **6**

        Let's verify:
        - Level 0: 1 node (total: 1)
        - Level 1: 2 nodes (total: 3)
        - Level 2: 4 nodes (total: 7)
        - Level 3: 8 nodes (total: 15)
        - Level 4: 16 nodes (total: 31)
        - Level 5: 32 nodes (total: 63)
        - Level 6: 37 nodes (total: 100) ✓

??? question "Question 5 — Element localization"
    In a min-heap represented as an array, the maximum element is necessarily among the leaves.

    ??? success "Answer"
        **True.** If the maximum element were not a leaf, it would have at least one child. But in a min-heap, each parent is smaller than its children, so this child would be larger than the maximum — a contradiction.

        The leaves of a heap of n elements are at indices ⌊n/2⌋ to n-1.

??? question "Question 6 — Up-heap and down-heap"
    During a `replaceKey(entry, newKey)` operation in an adaptable heap, it may be necessary to perform both an up-heap AND a down-heap.

    ??? success "Answer"
        **False.** Only one direction is needed:

        - If `newKey < oldKey`: the new key is smaller, so the element may violate the invariant with its parent → **up-heap only**
        - If `newKey > oldKey`: the new key is larger, so the element may violate the invariant with its children → **down-heap only**
        - If `newKey == oldKey`: no operation needed

        You can never need both because if the invariant is satisfied upward, it is necessarily satisfied downward, and vice versa (by transitivity).

---

### 1.2 Multiple Choice Questions

??? question "Question 7 — Insertion trace"
    We successively insert the keys **5, 3, 8, 1, 4, 7, 2** into an initially empty min-heap.

    What is the content of the array after all insertions?

    - [ ] A) `[1, 3, 2, 5, 4, 8, 7]`
    - [ ] B) `[1, 2, 3, 4, 5, 7, 8]`
    - [ ] C) `[1, 3, 2, 5, 4, 7, 8]`
    - [ ] D) `[1, 4, 2, 5, 3, 8, 7]`

    ??? success "Answer"
        **A) `[1, 3, 2, 5, 4, 8, 7]`**

        Let's trace the insertions with up-heap:

        1. **insert(5)**: `[5]`
        2. **insert(3)**: `[5, 3]` → 3 < 5, up-heap → `[3, 5]`
        3. **insert(8)**: `[3, 5, 8]` → 8 > 3, OK
        4. **insert(1)**: `[3, 5, 8, 1]` → 1 < 5, swap → `[3, 1, 8, 5]` → 1 < 3, swap → `[1, 3, 8, 5]`
        5. **insert(4)**: `[1, 3, 8, 5, 4]` → 4 > 3, OK
        6. **insert(7)**: `[1, 3, 8, 5, 4, 7]` → 7 < 8, swap → `[1, 3, 7, 5, 4, 8]`

        Wait, let's re-verify step 6:
        - Insertion at index 5, parent = (5-1)/2 = 2 (value 8)
        - 7 < 8 → swap → `[1, 3, 7, 5, 4, 8]`
        - New parent = (2-1)/2 = 0 (value 1)
        - 7 > 1 → OK

        7. **insert(2)**: `[1, 3, 7, 5, 4, 8, 2]`
        - Index 6, parent = (6-1)/2 = 2 (value 7)
        - 2 < 7 → swap → `[1, 3, 2, 5, 4, 8, 7]`
        - New parent = (2-1)/2 = 0 (value 1)
        - 2 > 1 → OK

        Final result: **`[1, 3, 2, 5, 4, 8, 7]`** ✓

??? question "Question 8 — removeMin trace"
    We perform **two** `removeMin()` operations on the heap `[2, 4, 3, 7, 5, 9, 6]`.

    What is the content of the array after these two removals?

    - [ ] A) `[4, 5, 6, 7, 9]`
    - [ ] B) `[4, 5, 3, 7, 9]`
    - [ ] C) `[3, 4, 6, 7, 5]`
    - [ ] D) `[4, 5, 6, 7, 9, 3]`

    ??? success "Answer"
        **A) `[4, 5, 6, 7, 9]`**

        **First removeMin()** on `[2, 4, 3, 7, 5, 9, 6]`:

        1. Remove 2, replace with last (6): `[6, 4, 3, 7, 5, 9]`
        2. Down-heap: children of 6 are 4 and 3, min = 3
        3. 6 > 3 → swap: `[3, 4, 6, 7, 5, 9]`
        4. Children of 6 (index 2) are 9 (index 5), no right child
        5. 6 < 9 → OK, done

        After first removeMin: `[3, 4, 6, 7, 5, 9]`

        **Second removeMin()** on `[3, 4, 6, 7, 5, 9]`:

        1. Remove 3, replace with last (9): `[9, 4, 6, 7, 5]`
        2. Down-heap: children of 9 are 4 and 6, min = 4
        3. 9 > 4 → swap: `[4, 9, 6, 7, 5]`
        4. Children of 9 (index 1) are 7 and 5, min = 5
        5. 9 > 5 → swap: `[4, 5, 6, 7, 9]`
        6. 9 has no more children → done

        Final result: **`[4, 5, 6, 7, 9]`** ✓

??? question "Question 9 — Structure choice"
    You need to implement a task management system where:

    - Thousands of tasks arrive continuously
    - The most urgent task must always be processed first
    - Task priorities change frequently

    Which structure is the most appropriate?

    - [ ] A) List sorted by priority
    - [ ] B) Simple binary heap (without adaptability)
    - [ ] C) Binary heap with adaptable priority queue
    - [ ] D) Hash table with priority as key

    ??? success "Answer"
        **C) Binary heap with adaptable priority queue**

        Let's analyze each option:

        - **Sorted list (A)**: `insert` in O(n), `removeMin` in O(1). With thousands of tasks arriving continuously, O(n) insertions become a bottleneck.

        - **Simple heap (B)**: `insert` and `removeMin` in O(log n), but changing a priority requires searching for the element in **O(n)** then repositioning it.

        - **Adaptable heap (C)**: Combines O(log n) for insert/removeMin AND O(log n) for `replaceKey` thanks to a localization mechanism (often a `Map` entry→index).

        - **Hash table (D)**: O(1) access by key, but finding the minimum requires scanning the entire table in O(n).

        With frequent priority changes, the adaptable priority queue is clearly the best choice.

??? question "Question 10 — Complexity analysis"
    A heap is used to sort n elements (heap-sort). What is the total complexity?

    - [ ] A) O(n)
    - [ ] B) O(n log n)
    - [ ] C) O(n²)
    - [ ] D) O(log n)

    ??? success "Answer"
        **B) O(n log n)**

        Heap-sort breaks down into two phases:

        1. **Heap construction**: O(n) with the bottom-up method
        2. **n `removeMin()` extractions**: each extraction costs O(log n), so n × O(log n) = O(n log n)

        Total: O(n) + O(n log n) = **O(n log n)**

        This is an optimal sort in terms of asymptotic complexity (like merge-sort and quick-sort on average), with the advantage of being **in-place** (O(1) additional space).

??? question "Question 11 — Heap property"
    Which of these arrays represents a valid min-heap?

    - [ ] A) `[1, 2, 3, 4, 5, 6, 7]`
    - [ ] B) `[1, 3, 2, 4, 5, 7, 6]`
    - [ ] C) `[1, 2, 3, 5, 4, 6, 7]`
    - [ ] D) All of the above

    ??? success "Answer"
        **D) All of the above**

        Let's verify each array by checking that parent ≤ children:

        **A) `[1, 2, 3, 4, 5, 6, 7]`**
        ```
              1
            /   \
           2     3
          / \   / \
         4   5 6   7
        ```
        - 1 ≤ 2, 1 ≤ 3 ✓
        - 2 ≤ 4, 2 ≤ 5 ✓
        - 3 ≤ 6, 3 ≤ 7 ✓ → **Valid**

        **B) `[1, 3, 2, 4, 5, 7, 6]`**
        ```
              1
            /   \
           3     2
          / \   / \
         4   5 7   6
        ```
        - 1 ≤ 3, 1 ≤ 2 ✓
        - 3 ≤ 4, 3 ≤ 5 ✓
        - 2 ≤ 7, 2 ≤ 6 ✓ → **Valid**

        **C) `[1, 2, 3, 5, 4, 6, 7]`**
        ```
              1
            /   \
           2     3
          / \   / \
         5   4 6   7
        ```
        - 1 ≤ 2, 1 ≤ 3 ✓
        - 2 ≤ 5, 2 ≤ 4 ✓
        - 3 ≤ 6, 3 ≤ 7 ✓ → **Valid**

        All satisfy the heap invariant. This illustrates that **a heap is not unique** for a given set of keys.

---

### 1.3 Reflection Questions

??? question "Question 12 — Heap vs BST"
    Explain why we prefer using a **heap** rather than a **balanced binary search tree** (such as an AVL or Red-Black tree) to implement a priority queue.

    ??? success "Answer"
        Several reasons justify this choice:

        **1. Simplicity of implementation**

        - A heap is represented in a simple array without pointers
        - No need to maintain complex invariants (balance factor, colors)
        - Up-heap/down-heap operations are simpler than rotations

        **2. Practical efficiency**

        - Better **cache locality**: adjacent elements in the array are often used together
        - No dynamic allocation of nodes: less memory fragmentation
        - Lower multiplicative constants in operations

        **3. Sufficient functionality**

        - A priority queue only needs `insert`, `min`, and `removeMin`
        - A BST offers additional operations (key search, in-order traversal) that are not needed here

        **When to prefer a BST?**

        - If you need to search elements by key
        - If you want to traverse elements in order
        - If you need `floor`, `ceiling`, `range queries`

??? question "Question 13 — Hidden cost of adaptable priorities"
    In an adaptable priority queue, the `replaceKey` operation has an advertised complexity of O(log n). Explain why this complexity assumes a localization mechanism, and what the cost of this mechanism is.

    ??? success "Answer"
        **The localization problem**

        To modify the key of an entry, you first need to **find its position** in the heap. Without a special mechanism, this requires a traversal in O(n).

        **Solution: Entry with localization**

        An `Entry` (or `Location`) object is used that:

        1. Stores the key, value, and **current index** in the array
        2. Is updated at each swap in the heap

        With this mechanism:
        - Accessing the position of an entry: **O(1)**
        - Updating the index during a swap: **O(1)** additional per swap

        **Cost of the mechanism**

        | Aspect | Cost |
        | --- | --- |
        | Space | O(n) to store the entries |
        | Index updates | O(1) per swap (added to existing operations) |
        | Entry access | O(1) if the reference was kept |

        **Alternative: Map key→position**

        If keys are unique, a `HashMap<Key, Integer>` can be used to locate entries. But beware:

        - This assumes unique keys
        - Each swap requires a Map update: O(1) amortized but with a higher constant

??? question "Question 14 — Bottom-up vs Top-down"
    Heap construction can be done in O(n) with the bottom-up method, versus O(n log n) with successive insertions. Intuitively explain why the bottom-up method is more efficient.

    ??? success "Answer"
        **Top-down method (n insertions)**

        Each insertion performs an up-heap that can go all the way up to the root:

        - 1st insertion: 0 swaps max
        - 2nd insertion: 1 swap max
        - nth insertion: log n swaps max

        Approximate total: Σ log(i) for i from 1 to n ≈ **O(n log n)**

        **Bottom-up method**

        Starting from the last non-leaf node, apply down-heap toward the root:

        - Leaves (n/2 nodes): 0 swaps
        - Level h-1 (n/4 nodes): 1 swap max each
        - Level h-2 (n/8 nodes): 2 swaps max each
        - ...
        - Root (1 node): h swaps max

        **The key intuition**: the most numerous nodes (the leaves and lower levels) do **little or no** work, while only the root potentially does log n swaps.

        Total: Σ (number of nodes at level k) × (height below that level)
        = n/4 × 1 + n/8 × 2 + n/16 × 3 + ...
        = n × (1/4 + 2/8 + 3/16 + ...)
        = n × Σ (k/2^(k+1)) for k ≥ 1
        = **O(n)**

        The series converges to a constant (~2), hence the linear complexity.

---

## Summary — Module 2 Recap

This module covered the fundamental **Abstract Data Types (ADTs)**:

| ADT | Main Implementations | Key Operations | Typical Complexity |
| --- | --- | --- | --- |
| **List** | ArrayList, LinkedList | get, add, remove | O(1) to O(n) depending on position |
| **Positional List** | DoublyLinkedList with sentinels | addAfter, addBefore, remove | O(1) with known position |
| **Stack** | Array, LinkedList | push, pop, top | O(1) |
| **Queue** | Circular array, LinkedList | enqueue, dequeue | O(1) |
| **Deque** | Circular array, DoublyLinkedList | addFirst/Last, removeFirst/Last | O(1) |
| **Priority Queue** | List, Heap | insert, min, removeMin | O(log n) for heap |

!!! success "Key Takeaways"

    1. **Choose the structure based on dominant operations**: A heap is optimal for priority queues, but not for key-based search.

    2. **A heap is NOT sorted**: It only guarantees that the parent is smaller than its children.

    3. **Array representation is efficient**: Cache locality, no pointers, simple arithmetic.

    4. **Adaptable priorities have a cost**: The localization mechanism adds complexity.

    5. **Bottom-up beats top-down**: O(n) vs O(n log n) for building a heap.

---

## Practical Exercise: Task Scheduler

### Problem Statement

We want to implement a simplified **task scheduler** that simulates how a processor works. The system must manage multiple tasks with different priorities and varying arrival times.

**Specifications:**

- Each task has:
    - A unique **identifier**
    - A **duration** (number of time units needed)
    - An **arrival time** (when the task becomes available)
    - A **priority** (smaller = higher priority)

- The processor:
    - Executes one task at a time
    - Always chooses the **highest priority** task among those waiting
    - Once a task starts, it runs to completion (non-preemptive)

**Goal:** Use a `PriorityQueue` (heap-based priority queue) to efficiently manage the selection of the next task to execute.

[:material-download: Download the code](../files/code/Scheduler.zip){ .md-button .md-button--primary }

---

### Possible Improvements

Here are some extensions to deepen your understanding of priority queues:

??? tip "Preemptive Round-Robin"

    **Current problem:** A long task with high priority monopolizes the processor, making all other tasks wait.

    **Solution:** Implement a **preemptive Round-Robin** scheduling where each task runs for a **time quantum** (e.g., 2 units) before being put back in the queue.

    **Suggested modifications:**

    1. Add a `quantum` parameter to the `Processor`
    2. Track the execution time of the current task
    3. If the quantum is reached and the task is not finished:
        - Put the task back in the `PriorityQueue`
        - Move to the next task

    **Reflection:** How can you ensure fairness between tasks with equal priority?

??? tip "Priority based on remaining time (Shortest Remaining Time First)"

    **Current problem:** Priority is fixed and defined at task creation.

    **Solution:** Use **remaining time** as the priority criterion. The task with the least remaining time is always prioritized.

    **Suggested modifications:**

    1. Modify `Task.compareTo()` to compare by `remainingTime`
    2. **Warning:** Since `remainingTime` changes during execution, you need to use an **adaptable priority queue** or remove/reinsert the task after each time unit

    **Reflection:** What is the advantage of SRTF? What is its main disadvantage (hint: *starvation*)?

??? tip "Combination: Dynamic priority with aging"

    **Problem:** With SRTF or fixed priorities, long or low-priority tasks can wait indefinitely (*starvation*).

    **Solution:** Implement **aging**: progressively increase the priority of tasks that have been waiting for a long time.

    **Idea:**
    ```java
    // At each tick, for waiting tasks:
    effectivePriority = basePriority - (currentTime - arrivalTime) / agingFactor;
    ```

    This requires an adaptable priority queue with `replaceKey`.

---

## References

* Goodrich, Tamassia, Goldwasser. *Data Structures and Algorithms in Java*, 6th Edition.
    * Chapter 9: Priority Queues
* Java Documentation: [`java.util.PriorityQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/PriorityQueue.html)
