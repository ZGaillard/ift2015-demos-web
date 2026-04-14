# Data Structures - Fun Problem Set

A curated set of classic problems that show up often in interviews, contests, and practice sessions. The goal is not just to know the answer, but to quickly recognize **the right pattern** and understand **why** it works.

!!! abstract "How to work through this set"

    - Read the prompt first and try to identify the dominant data structure before opening the answer
    - Look for the key invariant or strategy before thinking about code
    - Compare the problems to each other so the recurring patterns become obvious

---

## Challenge Set

??? question "Question 1 — Two Sum (Hash Map)"
    Given an array `nums` and a target value `target`, find two indices `i` and `j` such that `nums[i] + nums[j] = target`.

    The goal is to do better than the naive `O(n^2)` approach that tests every pair.

    ??? success "Answer"
        **Core idea.** Scan the array once while storing previously seen values in a hash map.

        **Why this works.** If the current value is `x`, then we need `target - x`. If that complement has already been seen, we have found the answer immediately. Otherwise, we store `x` for future elements.

        **Steps.**

        1. Create a `HashMap` from `value -> index`
        2. Traverse the array from left to right
        3. For each `x = nums[i]`, compute `target - x`
        4. If that complement is already in the map, return the two indices
        5. Otherwise, store `x` with its current index

        **Why we do not preload the entire map.** If we stored everything first, we could accidentally reuse the same element twice. The one-pass approach avoids that issue naturally.

        **Complexity.**

        - Time: `O(n)`
        - Space: `O(n)`

        **Pattern.** Fast lookup with a hash map.

---

??? question "Question 2 — Same Tree (Synchronized DFS)"
    You are given two binary trees. Determine whether they are exactly identical, meaning they have the same shape and the same value at every corresponding position.

    ??? success "Answer"
        **Core idea.** Compare the two trees in parallel, node by node.

        **Why DFS fits naturally here.** At each pair of nodes, we need to answer the same question for the left subtrees and for the right subtrees. The recursive structure of the problem matches DFS perfectly.

        **Base cases.**

        - If both nodes are `null`, they match at this position
        - If only one node is `null`, the structures differ
        - If the values differ, the trees are not identical

        **Recursive step.** If both nodes exist and have the same value, then we must verify:

        - that the left subtrees are identical
        - and that the right subtrees are identical

        Both conditions must hold.

        **Complexity.**

        - Time: `O(n)` where `n` is the number of visited nodes
        - Space: `O(h)` for the recursion stack, where `h` is the tree height

        **Pattern.** Synchronized DFS over two structures.

---

??? question "Question 3 — Lowest Common Ancestor in a Binary Tree"
    Find the lowest common ancestor of two nodes `p` and `q` in a general binary tree.

    ??? success "Answer"
        **Core idea.** Explore both subtrees and let information flow back upward.

        **Key observation.** For any node:

        - if both `p` and `q` are in its left subtree, the answer is on the left
        - if both are in its right subtree, the answer is on the right
        - if one is on each side, then the current node is their lowest common ancestor

        **Recursive strategy.**

        1. If the current node is `null`, return `null`
        2. If the current node is `p` or `q`, return it immediately
        3. Recurse into the left subtree and the right subtree
        4. If both calls return non-null values, the current node is the LCA
        5. Otherwise, return the non-null side

        **Why this works.** Each recursive call returns one of three things:

        - `null` if neither `p` nor `q` was found
        - `p` or `q` if one of them was found
        - the LCA itself if it was already determined deeper in the tree

        **Complexity.**

        - Time: `O(n)`
        - Space: `O(h)`

        **Pattern.** Post-order DFS with information propagated upward.

---

??? question "Question 4 — Lowest Common Ancestor in a BST"
    Solve the same problem as above, but now the tree is a **BST**.

    ??? success "Answer"
        **Core idea.** In a BST, the ordering of keys lets us avoid exploring both sides.

        **Property used.**

        - every key in the left subtree is smaller than the root
        - every key in the right subtree is larger than the root

        **Consequence.**

        - If both `p` and `q` are smaller than the root, the LCA must be in the left subtree
        - If both are larger than the root, the LCA must be in the right subtree
        - Otherwise, the current root separates them, so it is the LCA

        **Steps.**

        1. Start at the root
        2. Compare `p.val` and `q.val` with `root.val`
        3. Move left, move right, or stop based on those comparisons

        **Why this is better than the general binary-tree solution.** We do not need two recursive calls at each node. The BST invariant guides the search directly.

        **Complexity.**

        - Time: `O(h)`
        - Space: `O(1)` in an iterative version, `O(h)` in a recursive version

        **Pattern.** Search guided by the BST invariant.

---

??? question "Question 5 — Number of Islands (Grid DFS/BFS)"
    In a grid made of `land` and `water` cells, count the number of islands. Two land cells belong to the same island if they are connected horizontally or vertically.

    ??? success "Answer"
        **Core idea.** An island is just a connected component in a grid.

        **Approach.** Traverse every cell. Whenever you find an unvisited land cell, you have discovered a new island. Then run a DFS or BFS from that cell to consume the entire island so it does not get counted again.

        **Steps.**

        1. Scan the grid cell by cell
        2. If a cell is unvisited land, increment the island count
        3. Run DFS/BFS from that cell
        4. Mark all connected land cells as visited

        **Why the count increases exactly once per island.** The first unvisited cell of an island triggers a traversal that marks the whole island. No other cell in that island can start a new count afterward.

        **Common pitfalls.**

        - forgetting to mark visited cells
        - counting diagonal neighbors when they are not allowed
        - going out of bounds

        **Complexity.**

        - Time: `O(mn)`
        - Space: `O(mn)` in the worst case depending on the traversal structure or recursion depth

        **Pattern.** Connected-component traversal on a grid.

---

??? question "Question 6 — Rotting Oranges (Multi-source BFS)"
    In a grid, some oranges are fresh and some are already rotten. Every minute, a rotten orange infects its adjacent neighbors. Compute the minimum time needed for all oranges to become rotten.

    ??? success "Answer"
        **Core idea.** All initially rotten oranges act as sources that spread infection in parallel.

        **Why multi-source BFS is the right model.** In BFS, each level naturally represents a shortest distance. Here, that shortest distance is the number of minutes from the nearest rotten orange.

        **Steps.**

        1. Put all initially rotten oranges into the queue
        2. Count the number of fresh oranges
        3. Run BFS level by level
        4. Each time you expand, infect adjacent fresh oranges
        5. Decrease the remaining fresh count

        **Time interpretation.** One BFS layer = one minute. The first time a fresh orange is reached is necessarily the earliest possible minute.

        **Failure case.** If fresh oranges still remain at the end, they were unreachable, so the answer is `-1`.

        **Complexity.**

        - Time: `O(mn)`
        - Space: `O(mn)`

        **Pattern.** Multi-source BFS on a grid.

---

??? question "Question 7 — Walls and Gates (Multi-source BFS)"
    You are given a grid containing walls, gates, and empty rooms. Fill each empty room with its distance to the nearest gate.

    ??? success "Answer"
        **Core idea.** Instead of starting from every empty room, start from all gates at once.

        **Why this direction is better.** If you launched BFS from every empty room, you would repeat a huge amount of work. Starting from all gates simultaneously means each room gets its shortest distance the first time it is reached.

        **Steps.**

        1. Add all gates to the queue
        2. Run BFS from those gates simultaneously
        3. Whenever you reach an empty room, assign it `parent distance + 1`
        4. Continue until the queue is empty

        **Why the first distance found is correct.** BFS explores in increasing order of distance. So the first gate that reaches a room reaches it by a shortest path.

        **Complexity.**

        - Time: `O(mn)`
        - Space: `O(mn)`

        **Pattern.** Shortest path in an unweighted grid via multi-source BFS.

---

??? question "Question 8 — Design Snake Game (Simulation)"
    Design the logic for a Snake game: the snake moves, eats food, grows, and must avoid hitting walls or itself.

    ??? success "Answer"
        **Core idea.** We need to maintain both the order of the snake's body and a fast way to test whether a cell is already occupied.

        **Best structure combination.**

        - a `Queue` or `Deque` to store the body in head-to-tail order
        - a `Set` to test occupancy in `O(1)`

        **Steps for each move.**

        1. Compute the new head position
        2. Check whether that position leaves the board
        3. Temporarily remove the tail if the snake is not eating
        4. Check whether the new head collides with the body
        5. Add the new head
        6. If the cell contains food, keep the tail removed state disabled so the snake grows

        **Subtle but important detail.** You usually remove the tail before checking some collisions, because the head is allowed to move into the square that the tail is leaving during the same turn.

        **Per-move complexity.**

        - Time: `O(1)`
        - Space: `O(length of snake)`

        **Pattern.** Simulation using an ordered structure plus a membership set.

---

??? question "Question 9 — Majority Element (Boyer-Moore)"
    Find the element that appears strictly more than `n/2` times in an array.

    ??? success "Answer"
        **Core idea.** If an element is truly in the majority, it survives repeated cancellation against different elements.

        **Intuition.** Imagine that every time two different values meet, they cancel each other out. Since the majority element appears more often than all other elements combined, it cannot be completely canceled away.

        **Algorithm.**

        1. Maintain a `candidate` and a `count`
        2. If `count == 0`, choose the current value as the new candidate
        3. If the current value matches the candidate, increment `count`
        4. Otherwise, decrement `count`

        **Why it works.** The decrements represent pairwise cancellations between the current candidate and different values. If a true majority exists, it must remain as the final candidate.

        **Important note.** If the problem does not guarantee that a majority element exists, you need a second pass to verify the final candidate's frequency.

        **Complexity.**

        - Time: `O(n)`
        - Space: `O(1)`

        **Pattern.** State compression and pairwise elimination.

---

??? question "Question 10 — Find Duplicate Number (Floyd's Cycle Detection)"
    Find a duplicate value in an array of integers from `1` to `n`, without modifying the array and using very little extra memory.

    ??? success "Answer"
        **Core idea.** Reinterpret the array as a pointer structure, exactly like a linked list.

        **How this reinterpretation works.** From index `i`, imagine that you "point" to index `nums[i]`. Because some value is duplicated, two different paths eventually merge, which creates a logical cycle.

        **Step 1: detect the cycle.**

        - use a slow pointer that moves one step
        - use a fast pointer that moves two steps
        - they must eventually meet inside the cycle

        **Step 2: find the entry point of the cycle.**

        - reset one pointer to the start
        - move both pointers one step at a time
        - the node where they meet is the duplicate value

        **Why this works.** This is exactly the same reasoning as Floyd's cycle detection for linked lists. Here, the cycle entry corresponds to the duplicate number.

        **Complexity.**

        - Time: `O(n)`
        - Space: `O(1)`

        **Pattern.** Structural reinterpretation plus Floyd's cycle detection.

---

## Pattern Summary

- Hash map -> `Two Sum`
- Synchronized DFS -> `Same Tree`
- Post-order DFS -> `Lowest Common Ancestor`
- BST logic -> `Lowest Common Ancestor in BST`
- Grid DFS/BFS -> `Number of Islands`
- Multi-source BFS -> `Rotting Oranges`, `Walls and Gates`
- Simulation + combined structures -> `Snake Game`
- Pairwise elimination -> `Majority Element`
- Cycle detection -> `Find Duplicate Number`

---

## Mental Models to Keep

- If the problem asks for fast lookup among previously seen elements -> think `HashMap`
- If you are exploring a recursive structure or a graph -> think DFS/BFS
- If many sources spread information simultaneously -> think multi-source BFS
- If the problem depends on ordered updates over time -> think simulation
- If one value survives repeated cancellation -> think Boyer-Moore
- If an array can be viewed as pointers -> think Floyd
