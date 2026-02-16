# Demo 2: Lists and Positional Lists

This demo covers **Chapter 7** of the book *Data Structures and Algorithms in Java (6th ed.)* — **Lists and Positional Lists**.

!!! abstract "Learning Objectives"
    By the end of this demo, you should be able to:

    - Distinguish between the different list abstractions and implementations
    - Justify the use of positional lists in appropriate applications
    - Reason about time complexity based on the choice of structure
    - Safely implement linked data structures
    - Understand circularity as a structural design choice

---

## Theoretical Reminders

### The List ADT

A **list** represents an ordered sequence of elements where:

- Each element has a well-defined position in the sequence
- Duplicates are allowed
- Order matters

### Limitations of Indexed Lists

| Operation | Array-based List | Linked List |
|-----------|------------------|-------------|
| Access by index | O(1) | O(n) |
| Insertion at beginning | O(n) | O(1) |
| Insertion at end | O(1) amortized | O(1) |
| Insertion in the middle | O(n) | O(1)* |

*\* If we already have a reference to the position*

### Positional Lists

A **positional list** stores elements in nodes accessible via **Position objects** rather than numeric indices.

- A `Position` represents a stable location in the list
- Positions remain valid as long as they are not explicitly removed
- Insertions and removals relative to a known position are done in O(1)

### Sentinel Nodes

**Sentinel nodes** (header and trailer):

- Do not store user data
- Eliminate special cases during insertion or removal at the extremities
- Simplify implementation logic

### Circular Lists

A **circular list** is a linked list where:

- The last node points to the first node
- There is no natural beginning or end
- Uniform traversal without special cases

---

## Part 1 — Theoretical Exercises

### 1.1 True or False

For each statement, indicate whether it is **true** or **false** and justify your answer.

??? question "Question 1 — Position Stability"
    Consider a positional list containing the elements `[A, B, C, D]`. You store the position of `B` in a variable `posB`. After removing element `C` from the list, the position `posB` remains valid and still points to `B`.

    ??? success "Answer"
        **True.** This is precisely the advantage of positional lists over indexed lists. A position represents a stable location in the structure. Removing another element (`C`) does not affect the validity of `posB`.

        In contrast, if we were using an index (index 1 for `B`), removing `C` would not change the index of `B`, but if we had removed `A`, the index of `B` would have changed from 1 to 0.

        A position only becomes invalid when **its own element** is removed.

??? question "Question 2 — Insertion Complexity"
    In a singly linked list (without a reference to the last node), inserting an element **at the end** of the list is an O(1) operation.

    ??? success "Answer"
        **False.** Without a direct reference to the last node, we must traverse the entire list to reach the end, which takes O(n).

        This is why practical implementations often maintain a `tail` reference to the last element, which allows insertion in O(1). However, the statement explicitly specifies "without a reference to the last node."

??? question "Question 3 — Removal in a Circular List"
    In a singly linked circular list where we only maintain a `tail` pointer to the last element, removing the **first** element is done in O(1).

    ??? success "Answer"
        **True.** This is a subtle but important advantage of the circular list with a `tail` pointer.

        - The first element is `tail.next`
        - To remove it, we simply do `tail.next = tail.next.next`

        This requires no traversal. On the other hand, removing the **last** element (the one pointed to by `tail`) would require traversing the list to find the second-to-last node, since we cannot go backwards in a singly linked list.

??? question "Question 4 — Sentinels and the User"
    In a positional list with sentinels, the `first()` method returns the position of the header node when the list is empty.

    ??? success "Answer"
        **False.** Sentinels are implementation details that should **never** be exposed to the user. When the list is empty, `first()` must return `null` (or throw an exception depending on the specification).

        Sentinels exist to simplify internal code by eliminating special cases, but the ADT abstraction must hide these details. If a user could obtain a position to a sentinel, they could corrupt the structure by storing data in it or removing it.

??? question "Question 5 — Position Invalidation"
    Consider the following code on a positional list:
    ```java
    Position<String> p = list.first();
    String element = list.remove(p);
    list.addFirst(element);
    Position<String> q = list.first();
    // At this point, p and q reference the same position
    ```

    The final assertion is true: `p` and `q` reference the same position.

    ??? success "Answer"
        **False.** After `remove(p)`, the position `p` is **invalidated** — the underlying node is marked as defunct (typically by setting `next = null`).

        The call `addFirst(element)` creates a **new node** with a **new position** `q`. Even though the element is the same (`element`), the positions `p` and `q` are distinct. Moreover, any attempt to use `p` after its removal should throw an exception.

        This is a common mistake: confusing the element (the data) with the position (the container).

??? question "Question 6 — Traversal and Modification"
    It is always safe to remove elements from a positional list while traversing it with a standard iterator (`Iterator`).

    ??? success "Answer"
        **False.** Most iterator implementations in Java adopt a *fail-fast* strategy: if the list is structurally modified during iteration (other than through the iterator's own `remove()` method), a `ConcurrentModificationException` is thrown.

        To remove elements during a traversal, you must either:

        1. Use the iterator's `remove()` method
        2. Collect the positions to remove, then remove them after the traversal
        3. Traverse manually with `first()`, `after()`, being careful to retrieve the next position **before** removing

---

### 1.2 Multiple Choice Questions

??? question "Question 7 — Execution Trace"
    Consider an initially empty positional list. The following operations are executed:

    ```java
    Position<Integer> p1 = list.addFirst(1);
    Position<Integer> p2 = list.addLast(2);
    Position<Integer> p3 = list.addAfter(p1, 3);
    Position<Integer> p4 = list.addBefore(p2, 4);
    list.remove(p3);
    ```

    What is the content of the list after these operations (from first to last element)?

    - [ ] A) `[1, 4, 2]`
    - [ ] B) `[1, 3, 4, 2]`
    - [ ] C) `[1, 2, 4]`
    - [ ] D) `[1, 4, 3, 2]`

    ??? success "Answer"
        **A) `[1, 4, 2]`**

        Let's trace the operations:

        1. `addFirst(1)` → `[1]`
        2. `addLast(2)` → `[1, 2]`
        3. `addAfter(p1, 3)` → inserts 3 after p1 (which is 1) → `[1, 3, 2]`
        4. `addBefore(p2, 4)` → inserts 4 before p2 (which is 2) → `[1, 3, 4, 2]`
        5. `remove(p3)` → removes p3 (which is 3) → `[1, 4, 2]`

??? question "Question 8 — Choice of Structure"
    You need to implement a web browsing history ("Back" and "Forward" buttons). The user can:

    - Visit a new page (adds to history)
    - Go back (previous page)
    - Go forward (if available)
    - When visiting a new page after going back, all "forward" history is cleared

    Which structure is most appropriate?

    - [ ] A) Two stacks (one for back, one for forward)
    - [ ] B) A positional list with a "current" position
    - [ ] C) An ArrayList with a current index
    - [ ] D) A circular list

    ??? success "Answer"
        **A) Two stacks (one for back, one for forward)**

        Let's analyze each option:

        - **Two stacks**: Classic and elegant solution. "Back" = pop from back stack, push onto forward stack. "Forward" = reverse. New visit = push onto back stack and clear forward stack. All operations are O(1).

        - **Positional list** (B): Would work, but more complex than necessary. Clearing the forward history requires removing all elements after the current position.

        - **ArrayList with index** (C): Possible, but clearing the forward history is O(n) in the worst case.

        - **Circular list** (D): Unsuitable because the history has a natural beginning and end, no circularity.

??? question "Question 9 — Subtle Bug"
    Consider this implementation of `addBetween` for a doubly linked list:

    ```java
    private Position<E> addBetween(E e, Node<E> pred, Node<E> succ) {
        Node<E> newest = new Node<>(e, pred, succ);
        succ.setPrev(newest);
        pred.setNext(newest);
        size++;
        return newest;
    }
    ```

    And this implementation of `remove`:

    ```java
    public E remove(Position<E> p) {
        Node<E> node = validate(p);
        Node<E> predecessor = node.getPrev();
        Node<E> successor = node.getNext();
        predecessor.setNext(successor);
        successor.setPrev(predecessor);
        size--;
        return node.getElement();
    }
    ```

    What potential problem does this implementation of `remove` have?

    - [ ] A) It does not handle the case where the list becomes empty
    - [ ] B) It does not mark the node as invalid, allowing its accidental reuse
    - [ ] C) It modifies the pointers in the wrong order
    - [ ] D) It does not update the `size` counter correctly

    ??? success "Answer"
        **B) It does not mark the node as invalid, allowing its accidental reuse**

        The implementation correctly removes the node from the list, but the removed node retains its references (`prev`, `next`, `element`). If the user keeps a reference to the removed position and tries to use it later:

        - `node.getElement()` would still return the old value
        - `node.getNext()` would point to a node that is no longer its logical successor

        The solution is to "decommission" the node after removal:
        ```java
        node.setElement(null);
        node.setNext(null);  // Convention for defunct node
        node.setPrev(null);
        ```

        The `validate()` method can then check if `next == null` to detect an invalid position.

??? question "Question 10 — Complexity Analysis"
    You have a positional list of n elements and you want to find the element at index k (0-indexed). What is the best achievable complexity?

    - [ ] A) O(1)
    - [ ] B) O(k)
    - [ ] C) O(min(k, n-k))
    - [ ] D) O(n)

    ??? success "Answer"
        **C) O(min(k, n-k))**

        A doubly linked positional list allows traversal in both directions. To reach index k:

        - If k < n/2: start from the beginning and advance k times → O(k)
        - If k ≥ n/2: start from the end and go back (n-1-k) times → O(n-k)

        By choosing the optimal direction, we get O(min(k, n-k)), which is at worst O(n/2) = O(n), but often better.

        Note: This optimization is not always implemented. A naive implementation that always traverses from the beginning would be O(k), and one that only supports forward traversal would be limited to O(n) in the worst case.

??? question "Question 11 — Circular List"
    In a doubly linked circular list **without sentinels** containing exactly one element, what are the values of `node.next` and `node.prev`?

    - [ ] A) Both are `null`
    - [ ] B) `next` points to itself, `prev` is `null`
    - [ ] C) Both point to the node itself
    - [ ] D) Both point to a hidden sentinel

    ??? success "Answer"
        **C) Both point to the node itself**

        In a circular list, circularity must be maintained even with a single element. The node is both:

        - Its own successor (`next = this`)
        - Its own predecessor (`prev = this`)

        This is what distinguishes a circular list from a non-circular list. It also means there are no special cases: insertion and removal algorithms work uniformly.

---

### 1.3 Reflection Questions

??? question "Question 12 — Invariant Design"
    In the implementation of a positional list with sentinels, the convention `node.next == null` is used to indicate a defunct (removed) node.

    1. Why can't we use `node.element == null` as the defunct node indicator?
    2. Propose another possible convention and discuss its advantages/disadvantages.

    ??? success "Answer"
        **1. Why not `element == null`?**

        Because `null` is a valid element value! A user might legitimately want to store `null` in the list. Using `element == null` as an indicator would create ambiguity between "this node contains null" and "this node is invalid."

        In contrast, in a well-formed list, `next` should never be `null`:

        - For normal nodes, `next` points to the successor or the trailer
        - For the trailer, `next` could point to the header (circular) or remain non-null

        **2. Possible alternatives:**

        - **Boolean flag `isValid`**: Clear and explicit, but adds memory (1 logical bit, but often 1 byte or more in practice due to alignment).

        - **Container reference**: Each node keeps a reference to its list. A defunct node has `container == null`. Also allows verifying that a position belongs to the correct list.

        - **Special sentinel node `DEFUNCT`**: `next = DEFUNCT` where DEFUNCT is a static node. More explicit than `null`.

        The `next == null` convention is a good compromise: no extra memory and easy to check.

??? question "Question 13 — Practical Problem"
    You need to implement a method `moveToFront(Position<E> p)` that moves the element at position `p` to the beginning of the positional list.

    1. Describe the algorithm in pseudocode
    2. What is its time complexity?
    3. Identify an edge case that requires special attention

    ??? success "Answer"
        **1. Algorithm:**

        ```
        moveToFront(p):
            if p is invalid: throw exception
            if p == first(): return (already at the beginning)

            element = remove(p)
            addFirst(element)
        ```

        Or, more efficiently (without creating a new node):

        ```
        moveToFront(p):
            if p is invalid: throw exception
            if p == first(): return

            node = validate(p)
            // Detach the node
            node.prev.next = node.next
            node.next.prev = node.prev

            // Reattach at the beginning
            node.prev = header
            node.next = header.next
            header.next.prev = node
            header.next = node
        ```

        **2. Complexity: O(1)**

        All operations are constant-time pointer manipulations.

        **3. Edge cases:**

        - **`p` is already the first element**: Without the check, we would unnecessarily detach and reattach, which works but is inefficient.

        - **List with a single element**: `p` is both first and last. The check `p == first()` handles this case.

        - **Invalid position**: Must be detected by `validate()`.

??? question "Question 14 — Comparative Analysis"
    A colleague claims: "Positional lists are always better than ArrayLists because all insertions and removals are O(1)."

    Explain why this statement is incorrect by giving at least two scenarios where an ArrayList would be preferable.

    ??? success "Answer"
        The statement is incorrect for several reasons:

        **1. Access by index**

        - ArrayList: O(1)
        - Positional list: O(n) (must traverse from the beginning or end)

        If your application performs many random accesses by index (e.g., `get(i)`), ArrayList is significantly better.

        **2. Memory locality (cache)**

        ArrayList stores elements contiguously in memory. During sequential traversal, the CPU can prefetch the next elements (cache prefetching). Linked lists have nodes scattered in memory, causing frequent *cache misses*.

        In practice, for frequent traversals on moderately-sized lists, ArrayList can be 10x faster despite the same theoretical O(n) complexity.

        **3. Memory overhead**

        Each node of a linked list stores the element + 2 pointers (prev, next). For small elements (int, char), the overhead can triple or quadruple the memory used.

        **4. Insertions at the end**

        ArrayList with `add(e)` at the end is O(1) amortized (occasional resizing). If the majority of insertions are at the end, ArrayList is just as efficient as a linked list.

        **Conclusion**: The choice depends on the *usage pattern*. Positional lists excel when there are many insertions/removals in the middle with retained positions. ArrayLists excel for indexed access and traversals.

---

## Part 2 — Implementing a Positional List

### 2.1 Understanding the Structure

A doubly linked positional list with sentinels has the following structure:

```
Empty list:
┌─────────┐     ┌─────────┐
│ HEADER  │────▶│ TRAILER │
│  null   │◀────│  null   │
└─────────┘     └─────────┘

List with elements [A, B, C]:
┌─────────┐     ┌─────┐     ┌─────┐     ┌─────┐     ┌─────────┐
│ HEADER  │────▶│  A  │────▶│  B  │────▶│  C  │────▶│ TRAILER │
│  null   │◀────│     │◀────│     │◀────│     │◀────│  null   │
└─────────┘     └─────┘     └─────┘     └─────┘     └─────────┘
```

!!! info "Why sentinels?"
    Sentinels (header and trailer) **do not contain user data**. Their role is to eliminate special cases:

    - Without sentinels: `addFirst` must check if the list is empty and handle it differently
    - With sentinels: `addFirst` = `addBetween(e, header, header.next)` — always valid!

Here is the class skeleton:

```java
public class LinkedPositionalList<E> implements PositionalList<E> {

    //---------------- Inner Node class ----------------
    private static class Node<E> implements Position<E> {
        private E element;
        private Node<E> prev;
        private Node<E> next;

        public Node(E e, Node<E> p, Node<E> n) {
            element = e;
            prev = p;
            next = n;
        }

        public E getElement() throws IllegalStateException {
            if (next == null)  // convention for defunct node
                throw new IllegalStateException("Position no longer valid");
            return element;
        }

        public void setElement(E e) { element = e; }
        public Node<E> getPrev() { return prev; }
        public Node<E> getNext() { return next; }
        public void setPrev(Node<E> p) { prev = p; }
        public void setNext(Node<E> n) { next = n; }
    }

    //---------------- Instance variables ----------------
    private Node<E> header;
    private Node<E> trailer;
    private int size = 0;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}
```

---

### 2.2 Initialization and Validation

??? question "Exercise 2.2.1 — Constructor"
    Implement the constructor that creates an empty list. The sentinels must be linked to each other.

    **Hint:** After the constructor, the structure should look like:
    ```
    header.next = trailer
    trailer.prev = header
    header.prev = null (or ignored)
    trailer.next = null (or ignored)
    ```

    ??? success "Solution"
        ```java
        public LinkedPositionalList() {
            header = new Node<>(null, null, null);
            trailer = new Node<>(null, header, null);
            header.setNext(trailer);
        }
        ```

        **Step-by-step explanation:**

        1. Create the header with everything set to `null`
        2. Create the trailer with `prev = header`
        3. Link `header.next` to trailer

        The order matters: we cannot reference `trailer` before creating it!

??? question "Exercise 2.2.2 — Position Validation"
    The `validate(Position<E> p)` method is crucial for safety. It must:

    1. Verify that `p` is indeed a `Node` (not another type of Position)
    2. Verify that the node has not been removed (convention: `next == null`)
    3. Return the cast node

    Implement this method. What exceptions should be thrown in each case?

    ??? success "Solution"
        ```java
        private Node<E> validate(Position<E> p) throws IllegalArgumentException {
            if (!(p instanceof Node))
                throw new IllegalArgumentException("Invalid position type");
            Node<E> node = (Node<E>) p;
            if (node.getNext() == null)  // convention for defunct node
                throw new IllegalArgumentException("Position is no longer valid");
            return node;
        }
        ```

        **Bonus question:** Why don't we check `node.getPrev() == null`?

        Because only the header has `prev == null`, and the header is never exposed to the user. All valid nodes have `prev != null`. We could add this check, but `next == null` suffices since it is our convention for defunct nodes.

??? question "Exercise 2.2.3 — position() Method"
    Implement a private utility method `position(Node<E> node)` that returns the node as a Position, or `null` if it is a sentinel.

    This method will be useful for `first()`, `last()`, `before()`, `after()`.

    ??? success "Solution"
        ```java
        private Position<E> position(Node<E> node) {
            if (node == header || node == trailer)
                return null;  // do not expose the sentinels
            return node;
        }
        ```

---

### 2.3 Access Operations

??? question "Exercise 2.3.1 — first() and last()"
    Implement `first()` and `last()` using the `position()` method.

    ```
    List [A, B, C]:
    header ──▶ A ──▶ B ──▶ C ──▶ trailer
              ↑                    ↑
           first()              last()
    ```

    ??? success "Solution"
        ```java
        public Position<E> first() {
            return position(header.getNext());
        }

        public Position<E> last() {
            return position(trailer.getPrev());
        }
        ```

        **Note:** Thanks to `position()`, we don't need to explicitly check `isEmpty()`. If the list is empty, `header.next == trailer`, so `position(trailer)` returns `null`.

??? question "Exercise 2.3.2 — before() and after()"
    Implement the navigation methods. Be careful to validate the input position!

    ```
    For the list [A, B, C] with position p on B:

    before(p) returns the position of A
    after(p) returns the position of C
    before(first()) returns null
    after(last()) returns null
    ```

    ??? success "Solution"
        ```java
        public Position<E> before(Position<E> p) throws IllegalArgumentException {
            Node<E> node = validate(p);
            return position(node.getPrev());
        }

        public Position<E> after(Position<E> p) throws IllegalArgumentException {
            Node<E> node = validate(p);
            return position(node.getNext());
        }
        ```

---

### 2.4 Insertion

The fundamental operation is `addBetween`. All other insertions use it.

??? question "Exercise 2.4.1 — addBetween (key method)"
    Implement `addBetween(E e, Node<E> pred, Node<E> succ)` which inserts a new element between two existing nodes.

    **Before:**
    ```
    pred ──────────▶ succ
         ◀──────────
    ```

    **After:**
    ```
    pred ────▶ NEW ────▶ succ
         ◀────     ◀────
    ```

    **Hints:**

    1. Create the new node with the correct links
    2. Update `pred.next`
    3. Update `succ.prev`
    4. Increment `size`

    ??? success "Solution"
        ```java
        private Position<E> addBetween(E e, Node<E> pred, Node<E> succ) {
            Node<E> newest = new Node<>(e, pred, succ);  // new node's links
            pred.setNext(newest);                        // pred ──▶ newest
            succ.setPrev(newest);                        // newest ◀── succ
            size++;
            return newest;
        }
        ```

        **Does the order of operations matter?**

        Yes and no. In this case, since we first create the new node with its correct links, the order in which we update `pred` and `succ` does not matter. But if we did things differently, we could lose references.

??? question "Exercise 2.4.2 — The Four Insertion Methods"
    Using `addBetween`, implement:

    - `addFirst(E e)` — adds at the beginning
    - `addLast(E e)` — adds at the end
    - `addBefore(Position<E> p, E e)` — adds before p
    - `addAfter(Position<E> p, E e)` — adds after p

    ??? success "Solution"
        ```java
        public Position<E> addFirst(E e) {
            return addBetween(e, header, header.getNext());
        }

        public Position<E> addLast(E e) {
            return addBetween(e, trailer.getPrev(), trailer);
        }

        public Position<E> addBefore(Position<E> p, E e)
                throws IllegalArgumentException {
            Node<E> node = validate(p);
            return addBetween(e, node.getPrev(), node);
        }

        public Position<E> addAfter(Position<E> p, E e)
                throws IllegalArgumentException {
            Node<E> node = validate(p);
            return addBetween(e, node, node.getNext());
        }
        ```

        **Observation:** All four methods fit in a single line thanks to `addBetween`. This is the power of code factorization!

---

### 2.5 Removal

??? question "Exercise 2.5.1 — remove()"
    Implement `remove(Position<E> p)` which removes the node and returns its element.

    **Before (removing B):**
    ```
    A ────▶ B ────▶ C
      ◀────   ◀────
    ```

    **After:**
    ```
    A ────────────▶ C
      ◀────────────
    ```

    **Important:** After removal, the node must be marked as defunct to prevent its accidental reuse.

    ??? success "Solution"
        ```java
        public E remove(Position<E> p) throws IllegalArgumentException {
            Node<E> node = validate(p);
            Node<E> predecessor = node.getPrev();
            Node<E> successor = node.getNext();

            // Bypass the removed node
            predecessor.setNext(successor);
            successor.setPrev(predecessor);
            size--;

            E answer = node.getElement();

            // Invalidate the node (also helps the garbage collector)
            node.setElement(null);
            node.setNext(null);      // Convention: defunct node
            node.setPrev(null);

            return answer;
        }
        ```

---

### 2.6 set()

??? question "Exercise 2.6.1 — set() Method"
    Implement `set(Position<E> p, E e)` which replaces the element at position `p` with `e` and returns the old element.

    **Note:** This operation does not modify the structure of the list, only the content of a node.

    ??? success "Solution"
        ```java
        public E set(Position<E> p, E e) throws IllegalArgumentException {
            Node<E> node = validate(p);
            E old = node.getElement();
            node.setElement(e);
            return old;
        }
        ```

---

---

## Part 3 — Implementing a Circular List

### 3.1 Understanding the Circular Structure

In a circular list, the last element points to the first, forming a cycle.

```
Circular list [A, B, C] with tail pointer to C:

        ┌─────────────────────────────────┐
        │                                 │
        ▼                                 │
      ┌───┐     ┌───┐     ┌───┐          │
      │ A │────▶│ B │────▶│ C │──────────┘
      └───┘     └───┘     └───┘
        ↑                   ↑
      first              tail (last)
      = tail.next
```

!!! info "Why point to the last?"
    With a pointer to the **last** element:

    - Access to the last: `tail` → O(1)
    - Access to the first: `tail.next` → O(1)

    If we pointed to the first, accessing the last would require a full O(n) traversal.

Here is the skeleton:

```java
public class CircularlyLinkedList<E> {

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

    private Node<E> tail = null;
    private int size = 0;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}
```

---

### 3.2 Access Operations

??? question "Exercise 3.2.1 — first() and last()"
    Implement the accessors. Remember that `tail` points to the **last** element.

    ??? success "Solution"
        ```java
        public E first() {
            if (isEmpty()) return null;
            return tail.getNext().getElement();
        }

        public E last() {
            if (isEmpty()) return null;
            return tail.getElement();
        }
        ```

??? question "Exercise 3.2.2 — rotate()"
    Rotation is the characteristic operation of circular lists. It "rotates" the list by one position: the first element becomes the last.

    ```
    Before rotate(): tail ──▶ C,  first = A
        A → B → C → (back to A)

    After rotate(): tail ──▶ A,  first = B
        B → C → A → (back to B)
    ```

    **Hint:** This is a very simple O(1) operation!

    ??? success "Solution"
        ```java
        public void rotate() {
            if (tail != null)
                tail = tail.getNext();
        }
        ```

        That's it! By moving `tail` to `tail.next`, the old first becomes the new last. Circularity does all the work.

---

### 3.3 Insertion

??? question "Exercise 3.3.1 — addFirst()"
    Insert an element at the beginning of the list. Watch out for the empty list case!

    **Empty list case:** The new node must point to itself.
    ```
    Before: tail = null
    After addFirst(A):
        ┌───────┐
        │       │
        ▼       │
      ┌───┐     │
      │ A │─────┘
      └───┘
        ↑
      tail
    ```

    **Non-empty list case:** Insert between `tail` and `tail.next` (the old first).
    ```
    Before: tail ──▶ C, list = [A, B, C]
    After addFirst(X): tail ──▶ C, list = [X, A, B, C]
    ```

    ??? success "Solution"
        ```java
        public void addFirst(E e) {
            if (isEmpty()) {
                tail = new Node<>(e, null);
                tail.setNext(tail);  // points to itself
            } else {
                Node<E> newest = new Node<>(e, tail.getNext());
                tail.setNext(newest);
            }
            size++;
        }
        ```

??? question "Exercise 3.3.2 — addLast()"
    Insert an element at the end. **Tip:** Reuse `addFirst`!

    Think about it: if we add to the beginning and then rotate, where is the new element?

    ??? success "Solution"
        ```java
        public void addLast(E e) {
            addFirst(e);
            tail = tail.getNext();  // the new element becomes the last
        }
        ```

        **Explanation:**

        1. `addFirst(e)` inserts the new element at the beginning
        2. `tail = tail.getNext()` moves `tail` to this new element
        3. Result: the new element is now the last!

---

### 3.4 Removal

??? question "Exercise 3.4.1 — removeFirst()"
    Remove and return the first element. Watch out for the case where only one element remains!

    ??? success "Solution"
        ```java
        public E removeFirst() {
            if (isEmpty()) return null;
            Node<E> head = tail.getNext();
            if (head == tail)        // single element
                tail = null;
            else
                tail.setNext(head.getNext());
            size--;
            return head.getElement();
        }
        ```

??? question "Exercise 3.4.2 — removeLast() (more difficult)"
    Remove and return the **last** element.

    **Warning:** This operation is O(n)! Why?

    To remove the last element, we need to update the `next` pointer of the **second-to-last** node. But in a singly linked list, we cannot go backwards — so we must traverse the entire list to find the second-to-last node.

    ??? success "Solution"
        ```java
        public E removeLast() {
            if (isEmpty()) return null;

            if (size == 1) {
                E element = tail.getElement();
                tail = null;
                size--;
                return element;
            }

            // Find the second-to-last node (O(n))
            Node<E> current = tail.getNext();  // start at the first
            while (current.getNext() != tail) {
                current = current.getNext();
            }
            // current is now the second-to-last

            E element = tail.getElement();
            current.setNext(tail.getNext());  // bypass tail
            tail = current;                    // new last
            size--;
            return element;
        }
        ```

        **Lesson:** If you need frequent removals at the end, use a **doubly** linked list!

---

### 3.5 Application: The Hot Potato Game

The **Hot Potato** game is a classic for illustrating circular lists:

- Players stand in a circle
- A "potato" is passed from hand to hand
- After k passes, the player holding the potato is eliminated
- The last remaining player wins

??? question "Exercise 3.5.1 — Game Implementation"
    Implement the method `playHotPotato(CircularlyLinkedList<String> players, int k)` that simulates the game and returns the winner's name.

    **Example:** Players = [Alice, Bob, Carol, David, Eve], k = 3

    - Round 1: A→B→C, Carol eliminated → [Alice, Bob, David, Eve]
    - Round 2: D→E→A, Alice eliminated → [Bob, David, Eve]
    - Round 3: B→D→E, Eve eliminated → [Bob, David]
    - Round 4: B→D→B, Bob eliminated → [David]
    - Winner: David

    ??? success "Solution"
        ```java
        public static String playHotPotato(CircularlyLinkedList<String> players, int k) {
            if (players.isEmpty()) return null;

            while (players.size() > 1) {
                // Pass the potato k times
                for (int i = 0; i < k; i++) {
                    players.rotate();
                }
                // Eliminate the current player (first in the list)
                String eliminated = players.removeFirst();
                System.out.println(eliminated + " is eliminated!");
            }

            return players.first();  // the winner
        }
        ```

        **Variant (Josephus Problem):** Historically, this problem models a group of soldiers in a circle where every k-th person is eliminated. With n soldiers and k passes, which position should you choose to survive? This is a famous mathematical problem!

---

### 3.6 Application: Circular Buffer

A **circular buffer** is used for data streams (streaming, logs, etc.) where we want to keep only the last N elements.

??? question "Exercise 3.6.1 — CircularBuffer"
    Implement a fixed-size circular buffer. When the buffer is full, adding a new element overwrites the oldest one.

    ```java
    public class CircularBuffer<E> {
        private E[] buffer;
        private int head = 0;  // index of the oldest
        private int tail = 0;  // index of the next addition
        private int count = 0; // number of elements
        private int capacity;

        // To implement:
        // void add(E element)
        // E remove()
        // E peek()
    }
    ```

    ??? success "Solution"
        ```java
        @SuppressWarnings("unchecked")
        public CircularBuffer(int capacity) {
            this.capacity = capacity;
            this.buffer = (E[]) new Object[capacity];
        }

        public void add(E element) {
            buffer[tail] = element;
            tail = (tail + 1) % capacity;  // wrap around

            if (count == capacity) {
                // Buffer full: overwrite the oldest
                head = (head + 1) % capacity;
            } else {
                count++;
            }
        }

        public E remove() {
            if (count == 0) return null;
            E element = buffer[head];
            buffer[head] = null;  // help GC
            head = (head + 1) % capacity;
            count--;
            return element;
        }

        public E peek() {
            if (count == 0) return null;
            return buffer[head];
        }
        ```

        **Note:** This implementation uses a circular **array**, not a linked list. This is often more efficient for fixed-size buffers since there is no dynamic node allocation.

---

## Additional Exercises

??? question "Challenge 1 — Reverse a Positional List"
    Write a method that reverses a positional list **in place** (without creating a new list or new nodes).

    ```java
    public static <E> void reverse(PositionalList<E> list)
    ```

    **Example:** `[A, B, C, D]` → `[D, C, B, A]`

    ??? success "Solution"
        **Approach 1: Using the public API**
        ```java
        public static <E> void reverse(PositionalList<E> list) {
            if (list.size() <= 1) return;

            Position<E> front = list.first();
            while (list.after(front) != null) {
                E element = list.remove(list.last());
                list.addBefore(front, element);
            }
        }
        ```

        **Complexity:** O(n) — each element is moved once.

        **Approach 2: Direct pointer manipulation** (if we have access to the nodes)
        ```java
        // Inside LinkedPositionalList
        public void reverse() {
            if (size <= 1) return;

            Node<E> current = header;
            do {
                // Swap prev and next for each node
                Node<E> temp = current.prev;
                current.prev = current.next;
                current.next = temp;
                current = current.prev;  // advance (which is the old next)
            } while (current != header);

            // Swap header and trailer
            Node<E> temp = header;
            header = trailer;
            trailer = temp;
        }
        ```

??? question "Challenge 2 — Merge Two Sorted Lists"
    Merge two **sorted** positional lists into a single sorted list. The input lists may be modified.

    ```java
    public static <E extends Comparable<E>> PositionalList<E> merge(
            PositionalList<E> list1, PositionalList<E> list2)
    ```

    ??? success "Solution"
        ```java
        public static <E extends Comparable<E>> PositionalList<E> merge(
                PositionalList<E> list1, PositionalList<E> list2) {

            PositionalList<E> result = new LinkedPositionalList<>();
            Position<E> p1 = list1.first();
            Position<E> p2 = list2.first();

            while (p1 != null && p2 != null) {
                if (p1.getElement().compareTo(p2.getElement()) <= 0) {
                    result.addLast(p1.getElement());
                    p1 = list1.after(p1);
                } else {
                    result.addLast(p2.getElement());
                    p2 = list2.after(p2);
                }
            }

            // Add remaining elements
            while (p1 != null) {
                result.addLast(p1.getElement());
                p1 = list1.after(p1);
            }
            while (p2 != null) {
                result.addLast(p2.getElement());
                p2 = list2.after(p2);
            }

            return result;
        }
        ```

        **Complexity:** O(n + m) where n and m are the sizes of the two lists.

??? question "Challenge 3 — Detect a Cycle"
    Write a method that detects whether a singly linked list (not circular by design) accidentally contains a cycle.

    **Hint:** Floyd's algorithm (tortoise and hare).

    ```java
    public static <E> boolean hasCycle(Node<E> head)
    ```

    ??? success "Solution"
        ```java
        public static <E> boolean hasCycle(Node<E> head) {
            if (head == null) return false;

            Node<E> slow = head;  // tortoise: advances by 1
            Node<E> fast = head;  // hare: advances by 2

            while (fast != null && fast.getNext() != null) {
                slow = slow.getNext();
                fast = fast.getNext().getNext();

                if (slow == fast) {
                    return true;  // cycle detected!
                }
            }

            return false;  // fast reached the end
        }
        ```

        **Why does this work?** If a cycle exists, the hare (fast) will eventually catch up to the tortoise (slow) because they are both looping. If no cycle exists, the hare will reach `null`.

        **Complexity:** O(n) in time, O(1) in space.

??? question "Challenge 4 — indexOf with Position"
    Implement a method that returns the **position** of an element (not the index), or `null` if not found.

    ```java
    public Position<E> positionOf(E element)
    ```

    ??? success "Solution"
        ```java
        public Position<E> positionOf(E element) {
            Position<E> current = first();
            while (current != null) {
                if (Objects.equals(current.getElement(), element)) {
                    return current;
                }
                current = after(current);
            }
            return null;
        }
        ```

        **Note:** We use `Objects.equals()` to handle the case where `element` is `null`.

---

## Summary

| Structure | Access by index | Insertion at beginning | Insertion at end | Insertion in the middle |
|-----------|-----------------|------------------------|------------------|-------------------------|
| ArrayList | O(1) | O(n) | O(1) amortized | O(n) |
| Singly linked list | O(n) | O(1) | O(n)* | O(n)** |
| Doubly linked list | O(n) | O(1) | O(1) | O(1)*** |
| Positional list | O(n) | O(1) | O(1) | O(1)*** |
| Circular list | O(n) | O(1) | O(1) | O(n)** |

*\* O(1) if we maintain a reference to the last node*
*\*\* O(1) if we have a reference to the previous node*
*\*\*\* If we already have the position*

!!! tip "Key Takeaways"
    1. **Choose the structure based on frequent operations**: If insertions/removals in the middle are frequent, prefer positional lists.
    2. **Sentinels simplify code**: They eliminate special cases and reduce errors.
    3. **Always validate positions**: An invalidated position can cause unpredictable behavior.
    4. **Circularity has its uses**: Round-robin, buffers, and any application requiring cyclic traversal.
