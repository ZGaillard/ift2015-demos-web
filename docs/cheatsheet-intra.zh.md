# 备忘单 — IFT2015 期中考试

IFT2015 课程期中考试复习备忘单 — 数据结构。涵盖课程大纲**第 1 至第 4 节（第 4.3 节除外）**，基于 *Data Structures and Algorithms in Java, 第 6 版*（Goodrich, Tamassia, Goldwasser）。

---

## 1. 引言

!!! abstract "学习目标"
    理解为何数据结构的选择决定了算法的性能。

- **结构 == 功能**：一个数据结构由其主要操作及其渐进代价来定义，而不仅仅是一个容器。
- **结构 == 加速**：数据结构所维护的不变量使得更高效的算法成为可能。

!!! warning "常见陷阱"
    - 混淆**结构**与**算法** — 数据结构组织数据，算法操纵数据。
    - "代码越多 = 越慢" — 渐进复杂度与代码行数无关。
    - "某种结构总体上更好" — 每种结构都有取舍；选择取决于操作的使用模式。
    - 忘记**维护不变量的代价** — 维持有序排列、堆序等是有代价的。

---

## 2. 抽象数据类型（ADT）

### 2.1 ArrayList 与 LinkedList

**教材：§7.1, §7.2.1–7.2.3**

!!! abstract "学习目标"
    比较 List ADT 的动态数组实现与链表实现。

- **ArrayList**：可调整大小的动态数组。按索引直接访问为 $O(1)$。由于元素移位，插入/删除为 $O(n)$。通过数组倍增策略实现摊还 $O(1)$ 的扩容。
- **SinglyLinkedList**：带有 `next` 指针的节点链。在链表头部插入/删除为 $O(1)$。按索引访问为 $O(n)$ — 需要遍历链表。
- **DoublyLinkedList**：具有 `prev` 和 `next` 指针。若已有节点引用，则删除为 $O(1)$。使用哨兵节点（header/trailer）。

| 操作 | ArrayList | DoublyLinkedList |
|---|---|---|
| `get(i)` | $O(1)$ | $O(n)$ |
| `set(i, e)` | $O(1)$ | $O(n)$ |
| `add(i, e)` | $O(n)$ | $O(n)$* |
| `remove(i)` | $O(n)$ | $O(n)$* |
| `size()`, `isEmpty()` | $O(1)$ | $O(1)$ |
| `addFirst` / `addLast` | $O(n)$ / $O(1)$† | $O(1)$ |
| `removeFirst` / `removeLast` | $O(n)$ / $O(1)$† | $O(1)$ |

*\* 查找位置 i 需 $O(n)$，若已知位置则为 $O(1)$。*
*† 仅在数组末尾为 $O(1)$，在数组开头为 $O(n)$。*

!!! tip "扩容的摊还复杂度"
    采用**数组倍增**策略时，$n$ 次末尾插入的总代价为 $O(n)$，即每次插入的**摊还代价为 $O(1)$**。数组仅被扩容 $O(\log n)$ 次。

---

### 2.2 位置列表

**教材：§7.3, §7.4, §7.5, §7.6**

!!! abstract "学习目标"
    理解 PositionalList ADT 及稳定位置的概念。

- **PositionalList**：基于*位置*而非索引的 ADT。位置是一种稳定的抽象 — 即使在列表其他地方发生插入/删除后，它仍然有效。最佳实现方式为 DoublyLinkedList。
- 主要操作：`first()`, `last()`, `before(p)`, `after(p)`, `addBefore(p, e)`, `addAfter(p, e)`, `set(p, e)`, `remove(p)`。

| 操作 | 复杂度（DoublyLinkedList） |
|---|---|
| `size()`, `isEmpty()` | $O(1)$ |
| `first()`, `last()`, `before(p)`, `after(p)` | $O(1)$ |
| `addFirst(e)`, `addLast(e)` | $O(1)$ |
| `addBefore(p, e)`, `addAfter(p, e)` | $O(1)$ |
| `set(p, e)` | $O(1)$ |
| `remove(p)` | $O(1)$ |
| 空间 | $O(n)$ |

!!! warning "常见陷阱"
    - **位置 ≠ 索引。** 迭代器不是持久化的位置。
    - Java 在 `java.util.LinkedList` 中不暴露位置，以保护内部不变量。
    - 所有操作为 $O(1)$，**前提是已持有该位置** — 通过搜索获取位置仍需 $O(n)$。

---

### 2.3 收藏夹列表

**教材：§7.7**

!!! abstract "学习目标"
    比较两种访问频率管理策略。

- **FavoritesList（按计数器排序）**：元素按访问频率降序排列。`access(e)` 递增计数器并重新定位元素（局部插入排序）。`getFavorites(k)` 以 $O(k)$ 返回前 k 个元素。
- **FavoritesListMTF（移至首位）**：每次访问将元素移至列表头部。利用**引用局部性**。由于列表未排序，`getFavorites(k)` 需要 $O(kn)$ 的局部排序。

| 操作 | 排序版 | 移至首位版 |
|---|---|---|
| `access(e)` | $O(n)$ | 查找 $O(n)$，移动 $O(1)$ |
| `remove(e)` | $O(n)$ | $O(n)$ |
| `getFavorites(k)` | $O(k)$ | $O(kn)$ |

!!! tip "排序版与 MTF 的取舍"
    **MTF** 在**引用局部性强**时更优（重复访问序列 — 近期访问的元素在前）。**排序版**在**均匀访问模式**下更优，此时基于频率的排序更为稳定。

---

### 2.4 栈、队列、双端队列

**教材：第 6 章（§6.1–6.3）**

!!! abstract "学习目标"
    掌握三种受限访问 ADT 及其实现。

- **Stack（栈 — LIFO）**：仅能访问栈顶。`push(e)`, `pop()`, `top()`。类比：一摞盘子。
- **Queue（队列 — FIFO）**：从队尾加入，从队头取出。`enqueue(e)`, `dequeue()`, `first()`。类比：排队等候。
- **Deque（双端队列）**：可在两端插入和删除。是栈和队列的推广。`addFirst`, `addLast`, `removeFirst`, `removeLast`。

| 操作 | Stack（数组） | Queue（循环数组） | Deque（双向链表或循环数组） |
|---|---|---|---|
| `push` / `enqueue` / `addFirst` | $O(1)$ | $O(1)$ | $O(1)$ |
| `pop` / `dequeue` / `removeFirst` | $O(1)$ | $O(1)$ | $O(1)$ |
| `top` / `first` / `last` | $O(1)$ | $O(1)$ | $O(1)$ |
| `addLast` / `removeLast` | — | — | $O(1)$ |
| `size`, `isEmpty` | $O(1)$ | $O(1)$ | $O(1)$ |
| 空间 | $O(N)$* | $O(N)$* | $O(N)$* 或 $O(n)$ |

*\* $N$ = 已分配数组的大小，$n$ = 实际元素数量。*

**Java 对应关系（`java.util.Deque`）：**

| 我们的 ADT | 抛出异常版本 | 返回特殊值版本 |
|---|---|---|
| `first()` | `getFirst()` | `peekFirst()` |
| `last()` | `getLast()` | `peekLast()` |
| `addFirst(e)` | `addFirst(e)` | `offerFirst(e)` |
| `addLast(e)` | `addLast(e)` | `offerLast(e)` |
| `removeFirst()` | `removeFirst()` | `pollFirst()` |
| `removeLast()` | `removeLast()` | `pollLast()` |

!!! warning "常见陷阱"
    - **不要使用 `java.util.Stack`** — 遗留类，非线程安全，继承自 `Vector`。
    - 栈和队列是**访问策略**，而非本质不同的结构 — 两者均可由双端队列实现。
    - 循环队列：`front`，`rear = (front + size) % capacity`。

---

### 2.5 并发队列

**教材中未涵盖。**

!!! abstract "学习目标"
    理解队列的并发挑战及 Java 解决方案。

- 在并发（多线程）环境中，经典数据结构不是线程安全的。
- **线程安全与非线程安全**：并发访问可能导致数据损坏。
- **阻塞与非阻塞**：`BlockingQueue` 在队列满或空时阻塞线程。
- **`synchronized`**：简单锁，但代价高昂（竞争、死锁风险）。
- **`java.util.concurrent`**：`ArrayBlockingQueue`, `ConcurrentLinkedQueue` — 针对并发优化的数据结构。

!!! warning "常见陷阱"
    - `synchronized` 并非总是足够 — 粒度过粗。
    - 线程安全 ≠ 总是更优（同步开销）。
    - 注意**死锁**（deadlock）和**竞争**（contention）。

---

### 2.6 优先队列与堆

**教材：第 9 章（§9.1–9.5）**

!!! abstract "学习目标"
    理解优先队列 ADT 及其实现，尤其是堆。

- **优先队列（ADT）**：由（键, 值）条目组成的集合，键最小的条目始终可访问。操作：`insert(k, v)`, `min()`, `removeMin()`。
- **无序列表优先队列**：插入为 $O(1)$，查找最小值为 $O(n)$。
- **有序列表优先队列**：插入为 $O(n)$（维护顺序），min/removeMin 为 $O(1)$。
- **堆（二叉堆）**：满足堆序不变量的**完全**二叉树：每个节点的键 $\leq$ 其子节点的键。以数组存储。通过上滤/下滤实现 $O(\log n)$ 的插入和删除。
- **可适应优先队列（位置感知）**：扩展堆以支持 `remove(entry)`, `replaceKey(entry, k)`, `replaceValue(entry, v)` 的 $O(\log n)$ 操作，借助每个条目中的 `index` 字段实现。

| 操作 | 无序列表 | 有序列表 | 堆 | 可适应堆 |
|---|---|---|---|---|
| `size`, `isEmpty` | $O(1)$ | $O(1)$ | $O(1)$ | $O(1)$ |
| `insert` | $O(1)$ | $O(n)$ | $O(\log n)$* | $O(\log n)$ |
| `min` | $O(n)$ | $O(1)$ | $O(1)$ | $O(1)$ |
| `removeMin` | $O(n)$ | $O(1)$ | $O(\log n)$* | $O(\log n)$ |
| `remove(entry)` | — | — | — | $O(\log n)$ |
| `replaceKey(entry, k)` | — | — | — | $O(\log n)$ |
| `replaceValue(entry, v)` | — | — | — | $O(1)$ |
| 空间 | $O(n)$ | $O(n)$ | $O(n)$ | $O(n)$ |

*\* 若使用动态数组，则为摊还复杂度。*

!!! tip "堆的性质"
    - **堆序不变量**：对于每个节点 $v$（根节点除外），$\text{key}(v) \geq \text{key}(\text{parent}(v))$。
    - **结构性质**：完全二叉树 — 除最后一层外所有层均满，最后一层从左到右填充。
    - **高度**：$O(\log n)$，因为是完全树。
    - **数组存储**：对于索引 $i$ 处的节点 — 左子节点 = $2i + 1$，右子节点 = $2i + 2$，父节点 = $\lfloor(i-1)/2\rfloor$。
    - **上滤（upheap）**：插入后（在最后位置），当键小于父节点的键时向上冒泡。
    - **下滤（downheap）**：removeMin 后（将根替换为最后一个元素），与较小的子节点交换并向下沉降。

!!! warning "常见陷阱"
    - **堆 ≠ BST**：堆不是有序的，它只保证最小值在根节点。
    - 堆**不支持**在 $O(\log n)$ 内搜索任意元素。

---

## 3. 图（一）

**教材：§14.1, §14.2（§14.2.3 除外）**

!!! abstract "学习目标"
    了解图 ADT 并比较四种经典表示方法。

- **图（ADT）**：顶点集合 $V$ 和边集合 $E$。可以是有向图（digraph）或无向图。操作：`numVertices()`, `numEdges()`, `vertices()`, `edges()`, `getEdge(u,v)`, `outgoingEdges(v)`, `incomingEdges(v)`, `insertVertex(x)`, `insertEdge(u,v,x)`, `removeVertex(v)`, `removeEdge(e)`, `outDegree(v)`, `inDegree(v)`。
- **边列表**：两个无序列表 — 一个存储顶点，一个存储边。简单但查询效率低。
- **邻接列表**：每个顶点维护其关联边的列表。空间/时间折中较好。
- **邻接映射**：类似邻接列表，但使用映射（哈希）存储关联边。期望 $O(1)$ 访问特定边。
- **邻接矩阵**：$n \times n$ 矩阵，`matrix[i][j]` 存储边 $(i,j)$。$O(1)$ 访问，但空间为 $O(n^2)$。

| 操作 | 边列表 | 邻接列表 | 邻接映射 | 邻接矩阵 |
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
| **空间** | $O(n+m)$ | $O(n+m)$ | $O(n+m)$ | $O(n^2)$ |

*$n$ = 顶点数，$m$ = 边数，$d_v$ = $v$ 的度数。*

!!! tip "如何选择表示方法？"
    - **稠密图**（$m \approx n^2$）→ **邻接矩阵**。
    - **稀疏图**（$m \ll n^2$）→ **邻接列表或邻接映射**。
    - **需要判断边是否存在** → **邻接映射**或**邻接矩阵**。
    - **遍历关联边** → **邻接列表/邻接映射**。

!!! warning "常见陷阱"
    - **图 ≠ 树** — 树是连通无环图。
    - 不要默认使用矩阵表示 — $O(n^2)$ 的空间复杂度通常是不必要的。
    - 区分**有向图**和**无向图**（矩阵中的对称性，邻接列表中的双重条目）。

---

## 4. 树与字典树

### 4.1 树与字典树（Trie）

**教材：§8.1, §13.3**

!!! abstract "学习目标"
    了解 Tree 和 Binary Tree ADT、树的遍历方式，以及 Trie 数据结构。

- **树（ADT）**：具有根节点、内部节点和叶节点的层次结构。每个节点有一个父节点（根节点除外）和零个或多个子节点。操作：`root()`, `parent(p)`, `children(p)`, `numChildren(p)`, `isInternal(p)`, `isExternal(p)`, `isRoot(p)`, `size()`, `isEmpty()`。
- **二叉树（ADT）**：每个节点至多有 2 个子节点（左子节点和右子节点）的树。附加操作：`left(p)`, `right(p)`, `sibling(p)`。
- **字典树（Trie）**：每条边以一个字符为标签的树。从根到叶的路径表示字符串。用于前缀搜索和自动补全。

**复杂度 — 二叉树（链式结构）：**

| 操作 | 复杂度 |
|---|---|
| `size`, `isEmpty` | $O(1)$ |
| `root`, `parent`, `left`, `right`, `sibling`, `children`, `numChildren` | $O(1)$ |
| `isInternal`, `isExternal`, `isRoot` | $O(1)$ |
| `addRoot`, `addLeft`, `addRight`, `set`, `attach`, `remove` | $O(1)$ |
| `depth(p)` | $O(d_p + 1)$ |
| `height` | $O(n)$ |

**复杂度 — 一般树（链式结构）：**

| 操作 | 复杂度 |
|---|---|
| `size`, `isEmpty` | $O(1)$ |
| `root`, `parent`, `isRoot`, `isInternal`, `isExternal` | $O(1)$ |
| `numChildren(p)` | $O(1)$ |
| `children(p)` | $O(c_p + 1)$ |
| `depth(p)` | $O(d_p + 1)$ |
| `height` | $O(n)$ |

*$c_p$ = $p$ 的子节点数，$d_p$ = $p$ 的深度。*

**树的遍历方式：**

| 遍历方式 | 顺序 | 使用场景 |
|---|---|---|
| **前序遍历（Preorder）** | 根 → 子节点 | 目录结构，树的复制 |
| **后序遍历（Postorder）** | 子节点 → 根 | 磁盘空间计算，删除操作 |
| **中序遍历（Inorder）**（二叉树） | 左 → 根 → 右 | 有序访问 BST |
| **广度优先（BFS，按层次）** | 逐层遍历 | 最短路径 |

!!! tip "字典树的性质"
    - 空间：至多 $n+1$ 个节点（$n$ = 所有字符串长度之和）。
    - 搜索长度为 $m$ 的单词：最坏情况为 $O(m \times |\Sigma|)$，若子节点存储在映射中则为 $O(m)$。
    - 变体：压缩字典树（Compressed Trie）、后缀字典树（Suffix Trie）。

---

### 4.2 二叉搜索树（BST）

**教材：§8.3, §8.4, §11.1**

!!! abstract "学习目标"
    理解 BST 不变量、查找/插入/删除操作及退化情形。

- **BST**：二叉树，对于每个节点 $v$：左子树中所有键 $<$ key($v$) $<$ 右子树中所有键。中序遍历以升序给出所有元素。
- 查找、插入、删除：$O(h)$，其中 $h$ 为树的高度。
- 最优情况：$h = O(\log n)$（平衡树）。
- 最差情况：$h = O(n)$（退化树 — 按升序/降序插入）。

| 操作 | 复杂度 |
|---|---|
| `size`, `isEmpty` | $O(1)$ |
| `get(k)`, `put(k,v)`, `remove(k)` | $O(h)$ |
| `firstEntry`, `lastEntry` | $O(h)$ |
| `ceilingEntry`, `floorEntry`, `lowerEntry`, `higherEntry` | $O(h)$ |
| `subMap` | $O(s + h)$ |
| `entrySet`, `keySet`, `values` | $O(n)$ |

*$h$ = 树的高度，$s$ = subMap 中的结果数量。*

!!! warning "常见陷阱"
    - 不要**假设** BST 是平衡的 — 高度 $h$ 可能为 $n$。
    - **高度 ≠ 大小**：大小为 $n$ 的树的高度范围是从 $\lfloor\log n\rfloor$ 到 $n-1$。
    - 退化情况：按顺序插入 → 链表 → $h = n$。
    - **删除有 2 个子节点的节点**：用中序后继（右子树的最小值）或中序前驱（左子树的最大值）替换。

---

## 总结 — 数据结构分类

### 列表

| 数据结构 | 访问类型 | 主要使用场景 |
|---|---|---|
| ArrayList | 索引 | 按位置频繁访问 |
| SinglyLinkedList | 顺序访问 | 在头部插入/删除 |
| DoublyLinkedList | 双向顺序访问 | PositionalList、Deque 的基础 |
| PositionalList | 位置 | 使用稳定游标的插入/删除 |
| FavoritesList | 频率 | Top-k 元素（按计数器排序） |
| FavoritesListMTF | 频率 + 局部性 | 自适应缓存（移至首位） |

### 栈、队列、双端队列

| 数据结构 | 访问策略 | 实现方式 |
|---|---|---|
| Stack | LIFO | ArrayStack, LinkedStack |
| Queue | FIFO | ArrayQueue（循环），LinkedQueue |
| Deque | 双端 | ArrayDeque（循环），LinkedDeque |

### 优先队列

| 数据结构 | 不变量 | 使用场景 |
|---|---|---|
| UnsortedPriorityQueue | 无 | 快速插入，removeMin 次数少 |
| SortedPriorityQueue | 有序列表 | 频繁 removeMin，插入次数少 |
| HeapPriorityQueue | 堆序 + 完全树 | 通用场景（插入与 removeMin 均衡） |
| HeapAdaptablePQ | 堆 + 位置感知 | 优先级更新（Dijkstra，调度） |

### 图

| 数据结构 | 空间 | 使用场景 |
|---|---|---|
| EdgeList | $O(n+m)$ | 原型设计，小型图 |
| AdjacencyList | $O(n+m)$ | 通用场景，稀疏图 |
| AdjacencyMap | $O(n+m)$ | 频繁 getEdge 查询 |
| AdjacencyMatrix | $O(n^2)$ | 稠密图，$O(1)$ getEdge |

### 树

| 数据结构 | 性质 | 使用场景 |
|---|---|---|
| General Tree | 层次结构，n 叉 | 文件系统，DOM |
| Binary Tree | 最多 2 个子节点 | BST、堆的基础 |
| BST | 左 < 根 < 右的排序 | 查找，排序，有序映射 |
| Trie | 路径 = 字符串 | 自动补全，字典，前缀搜索 |
