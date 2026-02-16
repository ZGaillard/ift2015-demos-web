# 演示 2：列表与位置列表

本演示涵盖教材 *Data Structures and Algorithms in Java (第6版)* 的**第7章** — **列表与位置列表**。

!!! abstract "学习目标"
    完成本演示后，您应该能够：

    - 区分不同的列表抽象和实现
    - 说明在适当的应用场景中使用位置列表的理由
    - 根据结构选择推断时间复杂度
    - 安全地实现链式数据结构
    - 理解循环性作为结构设计的选择

---

## 理论回顾

### 列表 ADT

**列表**表示一个有序的元素序列，其中：

- 每个元素在序列中有一个明确定义的位置
- 允许重复元素
- 顺序很重要

### 索引列表的局限性

| 操作 | 数组列表 | 链表 |
|-----------|-------------------|---------------|
| 按索引访问 | O(1) | O(n) |
| 在开头插入 | O(n) | O(1) |
| 在末尾插入 | O(1) 均摊 | O(1) |
| 在中间插入 | O(n) | O(1)* |

*\* 如果已经拥有对该位置的引用*

### 位置列表

**位置列表** (Positional List) 将元素存储在节点中，通过 **Position 对象**而非数值索引来访问。

- 一个 `Position` 表示列表中的一个稳定位置
- 只要位置没有被显式删除，它就保持有效
- 相对于已知位置的插入和删除操作为 O(1)

### 哨兵节点

**哨兵节点** (Sentinel)（header 和 trailer）：

- 不存储用户数据
- 消除了在两端插入或删除时的特殊情况
- 简化了实现逻辑

### 循环列表

**循环列表** (Circular List) 是一种链表，其中：

- 最后一个节点指向第一个节点
- 没有自然的起点或终点
- 无需特殊情况即可均匀遍历

---

## 第一部分 — 理论练习

### 1.1 判断对错

对于每个陈述，判断其是**对**还是**错**并解释原因。

??? question "问题 1 — 位置的稳定性"
    考虑一个包含元素 `[A, B, C, D]` 的位置列表。您将 `B` 的位置存储在变量 `posB` 中。从列表中删除元素 `C` 后，位置 `posB` 仍然有效，并且仍然指向 `B`。

    ??? success "答案"
        **对。** 这正是位置列表相对于索引列表的优势。一个位置表示结构中的一个稳定位置。删除另一个元素（`C`）不会影响 `posB` 的有效性。

        相比之下，如果使用索引（索引 1 对应 `B`），删除 `C` 不会改变 `B` 的索引，但如果删除的是 `A`，`B` 的索引就会从 1 变为 0。

        一个位置只有在**其自身的元素**被删除时才会失效。

??? question "问题 2 — 插入的复杂度"
    在一个单向链表中（没有对最后一个节点的引用），在列表**末尾**插入一个元素是 O(1) 操作。

    ??? success "答案"
        **错。** 如果没有直接引用最后一个节点，就需要遍历整个列表才能到达末尾，这需要 O(n)。

        这就是为什么实际实现通常维护一个指向最后一个元素的 `tail` 引用，从而实现 O(1) 的插入。然而，题目明确说明"没有对最后一个节点的引用"。

??? question "问题 3 — 循环列表中的删除"
    在一个仅维护指向最后一个元素的 `tail` 指针的单向循环链表中，删除**第一个**元素的操作为 O(1)。

    ??? success "答案"
        **对。** 这是带 `tail` 指针的循环列表一个微妙但重要的优势。

        - 第一个元素是 `tail.next`
        - 要删除它，只需执行 `tail.next = tail.next.next`

        这不需要任何遍历。相反，删除**最后一个**元素（即 `tail` 指向的元素）则需要遍历列表找到倒数第二个节点，因为在单向链表中无法向后回溯。

??? question "问题 4 — 哨兵与用户"
    在带哨兵的位置列表中，当列表为空时，`first()` 方法返回 header 节点的位置。

    ??? success "答案"
        **错。** 哨兵是实现细节，**绝不应该**暴露给用户。当列表为空时，`first()` 必须返回 `null`（或根据规格抛出异常）。

        哨兵的存在是为了通过消除特殊情况来简化内部代码，但 ADT 的抽象必须隐藏这些细节。如果用户能够获得指向哨兵的位置，他们就可能通过在其中存储数据或删除它来破坏结构。

??? question "问题 5 — 位置失效"
    考虑以下对位置列表的代码：
    ```java
    Position<String> p = list.first();
    String element = list.remove(p);
    list.addFirst(element);
    Position<String> q = list.first();
    // 此时，p 和 q 引用相同的位置
    ```

    最后的断言为真：`p` 和 `q` 引用相同的位置。

    ??? success "答案"
        **错。** 在 `remove(p)` 之后，位置 `p` 被**失效** — 底层节点被标记为废弃（通常通过设置 `next = null`）。

        调用 `addFirst(element)` 会创建一个**新节点**和一个**新位置** `q`。即使元素相同（`element`），位置 `p` 和 `q` 也是不同的。此外，在删除后尝试使用 `p` 应该会抛出异常。

        这是一个常见错误：混淆元素（数据）与位置（容器）。

??? question "问题 6 — 遍历与修改"
    在使用标准迭代器（`Iterator`）遍历位置列表时，删除元素总是安全的。

    ??? success "答案"
        **错。** Java 中大多数迭代器实现采用 *fail-fast* 策略：如果在迭代期间对列表进行了结构修改（除了通过迭代器自身的 `remove()` 方法），就会抛出 `ConcurrentModificationException`。

        要在遍历期间删除元素，需要：

        1. 使用迭代器的 `remove()` 方法
        2. 收集要删除的位置，然后在遍历结束后删除
        3. 手动使用 `first()`、`after()` 遍历，注意在删除**之前**获取下一个位置

---

### 1.2 选择题

??? question "问题 7 — 执行追踪"
    考虑一个初始为空的位置列表。执行以下操作：

    ```java
    Position<Integer> p1 = list.addFirst(1);
    Position<Integer> p2 = list.addLast(2);
    Position<Integer> p3 = list.addAfter(p1, 3);
    Position<Integer> p4 = list.addBefore(p2, 4);
    list.remove(p3);
    ```

    这些操作完成后，列表的内容是什么（从第一个到最后一个元素）？

    - [ ] A) `[1, 4, 2]`
    - [ ] B) `[1, 3, 4, 2]`
    - [ ] C) `[1, 2, 4]`
    - [ ] D) `[1, 4, 3, 2]`

    ??? success "答案"
        **A) `[1, 4, 2]`**

        让我们追踪操作过程：

        1. `addFirst(1)` → `[1]`
        2. `addLast(2)` → `[1, 2]`
        3. `addAfter(p1, 3)` → 在 p1（即 1）之后插入 3 → `[1, 3, 2]`
        4. `addBefore(p2, 4)` → 在 p2（即 2）之前插入 4 → `[1, 3, 4, 2]`
        5. `remove(p3)` → 删除 p3（即 3）→ `[1, 4, 2]`

??? question "问题 8 — 结构选择"
    您需要实现一个网页浏览历史（"后退"和"前进"按钮）。用户可以：

    - 访问新页面（添加到历史记录）
    - 后退（上一页）
    - 前进（如果可用）
    - 在后退后访问新页面时，所有"前进"历史被清除

    哪种结构最合适？

    - [ ] A) 两个栈（一个用于后退，一个用于前进）
    - [ ] B) 带"当前"位置的位置列表
    - [ ] C) 带当前索引的 ArrayList
    - [ ] D) 循环列表

    ??? success "答案"
        **A) 两个栈（一个用于后退，一个用于前进）**

        让我们分析每个选项：

        - **两个栈**：经典且优雅的解决方案。"后退" = 从后退栈弹出，压入前进栈。"前进" = 反向操作。新访问 = 压入后退栈并清空前进栈。所有操作都是 O(1)。

        - **位置列表** (B)：可行，但比必要的更复杂。清除前进历史需要删除当前位置之后的所有元素。

        - **带索引的 ArrayList** (C)：可行，但清除前进历史在最坏情况下为 O(n)。

        - **循环列表** (D)：不适合，因为历史记录有自然的起点和终点，没有循环性。

??? question "问题 9 — 微妙的 Bug"
    考虑这个双向链表的 `addBetween` 实现：

    ```java
    private Position<E> addBetween(E e, Node<E> pred, Node<E> succ) {
        Node<E> newest = new Node<>(e, pred, succ);
        succ.setPrev(newest);
        pred.setNext(newest);
        size++;
        return newest;
    }
    ```

    以及这个 `remove` 实现：

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

    这个 `remove` 实现存在什么潜在问题？

    - [ ] A) 它没有处理列表变为空的情况
    - [ ] B) 它没有将节点标记为无效，允许其被意外重用
    - [ ] C) 它修改指针的顺序不正确
    - [ ] D) 它没有正确更新 `size` 计数器

    ??? success "答案"
        **B) 它没有将节点标记为无效，允许其被意外重用**

        该实现正确地将节点从列表中移除，但被删除的节点仍然保留着其引用（`prev`、`next`、`element`）。如果用户保留了对已删除位置的引用并在之后尝试使用它：

        - `node.getElement()` 仍会返回旧值
        - `node.getNext()` 会指向一个不再是其逻辑后继的节点

        解决方案是在删除后"废弃"该节点：
        ```java
        node.setElement(null);
        node.setNext(null);  // Convention pour nœud défunt
        node.setPrev(null);
        ```

        `validate()` 方法随后可以检查 `next == null` 来检测无效位置。

??? question "问题 10 — 复杂度分析"
    您有一个包含 n 个元素的位置列表，想要找到索引为 k（从 0 开始）的元素。可达到的最佳复杂度是什么？

    - [ ] A) O(1)
    - [ ] B) O(k)
    - [ ] C) O(min(k, n-k))
    - [ ] D) O(n)

    ??? success "答案"
        **C) O(min(k, n-k))**

        双向链式位置列表允许双向遍历。要到达索引 k：

        - 如果 k < n/2：从开头出发，前进 k 次 → O(k)
        - 如果 k ≥ n/2：从末尾出发，后退 (n-1-k) 次 → O(n-k)

        通过选择最优方向，得到 O(min(k, n-k))，最坏情况为 O(n/2) = O(n)，但通常更好。

        注意：这种优化并不总是被实现。始终从开头遍历的朴素实现为 O(k)，而只支持前向遍历的实现在最坏情况下为 O(n)。

??? question "问题 11 — 循环列表"
    在一个**没有哨兵**的双向循环链表中，当只包含一个元素时，`node.next` 和 `node.prev` 的值是什么？

    - [ ] A) 两者都是 `null`
    - [ ] B) `next` 指向自身，`prev` 为 `null`
    - [ ] C) 两者都指向节点自身
    - [ ] D) 两者都指向一个隐藏的哨兵

    ??? success "答案"
        **C) 两者都指向节点自身**

        在循环列表中，即使只有一个元素也必须保持循环性。该节点同时是：

        - 自己的后继（`next = this`）
        - 自己的前驱（`prev = this`）

        这正是循环列表与非循环列表的区别。这也避免了特殊情况：插入和删除算法可以统一运行。

---

### 1.3 思考题

??? question "问题 12 — 不变量设计"
    在带哨兵的位置列表实现中，使用约定 `node.next == null` 来表示一个废弃（已删除）的节点。

    1. 为什么不能使用 `node.element == null` 作为废弃节点的标志？
    2. 提出另一种可能的约定并讨论其优缺点。

    ??? success "答案"
        **1. 为什么不用 `element == null`？**

        因为 `null` 是一个合法的元素值！用户可能合理地希望在列表中存储 `null`。使用 `element == null` 作为标志会在"此节点包含 null"和"此节点无效"之间产生歧义。

        相反，在一个结构良好的列表中，`next` 不应该为 `null`：

        - 对于普通节点，`next` 指向后继节点或 trailer
        - 对于 trailer，`next` 可以指向 header（循环）或保持非空

        **2. 可能的替代方案：**

        - **布尔标志 `isValid`**：清晰明确，但增加内存开销（逻辑上 1 位，但由于对齐，实践中通常为 1 字节或更多）。

        - **容器引用**：每个节点保留对其列表的引用。废弃节点的 `container == null`。还可以验证位置是否属于正确的列表。

        - **特殊废弃哨兵 `DEFUNCT`**：`next = DEFUNCT`，其中 DEFUNCT 是一个静态节点。比 `null` 更明确。

        `next == null` 约定是一个很好的折中：不需要额外内存且易于验证。

??? question "问题 13 — 实际问题"
    您需要实现一个方法 `moveToFront(Position<E> p)`，将位置 `p` 处的元素移动到位置列表的开头。

    1. 用伪代码描述算法
    2. 其时间复杂度是多少？
    3. 找出一个需要特别注意的边界情况

    ??? success "答案"
        **1. 算法：**

        ```
        moveToFront(p):
            si p est invalide: lever exception
            si p == first(): retourner (déjà au début)

            element = remove(p)
            addFirst(element)
        ```

        或者，更高效的方式（不创建新节点）：

        ```
        moveToFront(p):
            si p est invalide: lever exception
            si p == first(): retourner

            node = validate(p)
            // Détacher le nœud
            node.prev.next = node.next
            node.next.prev = node.prev

            // Rattacher au début
            node.prev = header
            node.next = header.next
            header.next.prev = node
            header.next = node
        ```

        **2. 复杂度：O(1)**

        所有操作都是常数时间的指针操作。

        **3. 边界情况：**

        - **`p` 已经是第一个元素**：如果不检查，就会不必要地分离和重新连接，虽然功能正确但效率低下。

        - **列表只有一个元素**：`p` 同时是第一个和最后一个元素。`p == first()` 的检查处理了这种情况。

        - **无效位置**：必须由 `validate()` 检测。

??? question "问题 14 — 比较分析"
    一位同事声称："位置列表总是比 ArrayList 更好，因为所有插入和删除都是 O(1)。"

    解释为什么这个说法是不正确的，并给出至少两个 ArrayList 更优的场景。

    ??? success "答案"
        这个说法不正确，原因有几个：

        **1. 按索引访问**

        - ArrayList：O(1)
        - 位置列表：O(n)（需要从开头或末尾遍历）

        如果您的应用程序进行大量随机索引访问（例如 `get(i)`），ArrayList 明显更优。

        **2. 内存局部性（缓存）**

        ArrayList 在内存中连续存储元素。在顺序遍历时，CPU 可以预加载后续元素（缓存预取）。链表的节点分散在内存中，导致频繁的 *cache miss*。

        在实践中，对于中等大小列表的频繁遍历，ArrayList 尽管理论复杂度同为 O(n)，但速度可能快 10 倍。

        **3. 内存开销**

        链表的每个节点存储元素 + 2 个指针（prev、next）。对于小元素（int、char），开销可能使内存使用量增加三到四倍。

        **4. 末尾插入**

        ArrayList 的 `add(e)` 在末尾插入为 O(1) 均摊（偶尔重新分配）。如果大多数插入在末尾，ArrayList 与链表一样高效。

        **结论**：选择取决于*使用模式*。位置列表在需要大量中间插入/删除且保留位置引用时表现出色。ArrayList 在索引访问和遍历方面表现出色。

---

## 第二部分 — 实现位置列表

### 2.1 理解结构

带哨兵的双向链式位置列表具有以下结构：

```
Liste vide :
┌─────────┐     ┌─────────┐
│ HEADER  │────▶│ TRAILER │
│  null   │◀────│  null   │
└─────────┘     └─────────┘

Liste avec éléments [A, B, C] :
┌─────────┐     ┌─────┐     ┌─────┐     ┌─────┐     ┌─────────┐
│ HEADER  │────▶│  A  │────▶│  B  │────▶│  C  │────▶│ TRAILER │
│  null   │◀────│     │◀────│     │◀────│     │◀────│  null   │
└─────────┘     └─────┘     └─────┘     └─────┘     └─────────┘
```

!!! info "为什么使用哨兵？"
    哨兵（header 和 trailer）**不包含用户数据**。它们的作用是消除特殊情况：

    - 没有哨兵：`addFirst` 必须检查列表是否为空并进行不同的处理
    - 有哨兵：`addFirst` = `addBetween(e, header, header.next)` — 始终有效！

以下是类的骨架：

```java
public class LinkedPositionalList<E> implements PositionalList<E> {

    //---------------- Classe interne Node ----------------
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
            if (next == null)  // convention pour nœud défunt
                throw new IllegalStateException("Position no longer valid");
            return element;
        }

        public void setElement(E e) { element = e; }
        public Node<E> getPrev() { return prev; }
        public Node<E> getNext() { return next; }
        public void setPrev(Node<E> p) { prev = p; }
        public void setNext(Node<E> n) { next = n; }
    }

    //---------------- Variables d'instance ----------------
    private Node<E> header;
    private Node<E> trailer;
    private int size = 0;

    public int size() { return size; }
    public boolean isEmpty() { return size == 0; }
}
```

---

### 2.2 初始化与验证

??? question "练习 2.2.1 — 构造函数"
    实现创建空列表的构造函数。哨兵必须相互链接。

    **提示：** 构造函数执行后，结构应如下：
    ```
    header.next = trailer
    trailer.prev = header
    header.prev = null (ou ignoré)
    trailer.next = null (ou ignoré)
    ```

    ??? success "答案"
        ```java
        public LinkedPositionalList() {
            header = new Node<>(null, null, null);
            trailer = new Node<>(null, header, null);
            header.setNext(trailer);
        }
        ```

        **逐步解释：**

        1. 创建 header，所有字段为 `null`
        2. 创建 trailer，`prev = header`
        3. 将 `header.next` 链接到 trailer

        顺序很重要：在创建 `trailer` 之前无法引用它！

??? question "练习 2.2.2 — 位置验证"
    `validate(Position<E> p)` 方法对安全性至关重要。它必须：

    1. 验证 `p` 确实是一个 `Node`（而不是其他类型的 Position）
    2. 验证节点未被删除（约定：`next == null`）
    3. 返回强制转换后的节点

    实现此方法。每种情况应抛出什么异常？

    ??? success "答案"
        ```java
        private Node<E> validate(Position<E> p) throws IllegalArgumentException {
            if (!(p instanceof Node))
                throw new IllegalArgumentException("Invalid position type");
            Node<E> node = (Node<E>) p;
            if (node.getNext() == null)  // convention pour nœud défunt
                throw new IllegalArgumentException("Position is no longer valid");
            return node;
        }
        ```

        **附加问题：** 为什么不检查 `node.getPrev() == null`？

        因为只有 header 的 `prev == null`，而 header 永远不会暴露给用户。所有有效节点的 `prev != null`。可以添加这个验证，但 `next == null` 就足够了，因为这是我们对废弃节点的约定。

??? question "练习 2.2.3 — position() 方法"
    实现一个私有辅助方法 `position(Node<E> node)`，将节点作为 Position 返回，如果是哨兵则返回 `null`。

    此方法在 `first()`、`last()`、`before()`、`after()` 中会很有用。

    ??? success "答案"
        ```java
        private Position<E> position(Node<E> node) {
            if (node == header || node == trailer)
                return null;  // ne pas exposer les sentinelles
            return node;
        }
        ```

---

### 2.3 访问操作

??? question "练习 2.3.1 — first() 和 last()"
    使用 `position()` 方法实现 `first()` 和 `last()`。

    ```
    Liste [A, B, C] :
    header ──▶ A ──▶ B ──▶ C ──▶ trailer
              ↑                    ↑
           first()              last()
    ```

    ??? success "答案"
        ```java
        public Position<E> first() {
            return position(header.getNext());
        }

        public Position<E> last() {
            return position(trailer.getPrev());
        }
        ```

        **备注：** 借助 `position()`，无需显式检查 `isEmpty()`。如果列表为空，`header.next == trailer`，因此 `position(trailer)` 返回 `null`。

??? question "练习 2.3.2 — before() 和 after()"
    实现导航方法。注意验证输入位置！

    ```
    Pour la liste [A, B, C] avec position p sur B :

    before(p) retourne la position de A
    after(p) retourne la position de C
    before(first()) retourne null
    after(last()) retourne null
    ```

    ??? success "答案"
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

### 2.4 插入

基本操作是 `addBetween`。所有其他插入操作都使用它。

??? question "练习 2.4.1 — addBetween（关键方法）"
    实现 `addBetween(E e, Node<E> pred, Node<E> succ)`，在两个现有节点之间插入新元素。

    **之前：**
    ```
    pred ──────────▶ succ
         ◀──────────
    ```

    **之后：**
    ```
    pred ────▶ NEW ────▶ succ
         ◀────     ◀────
    ```

    **提示：**

    1. 创建具有正确链接的新节点
    2. 更新 `pred.next`
    3. 更新 `succ.prev`
    4. 递增 `size`

    ??? success "答案"
        ```java
        private Position<E> addBetween(E e, Node<E> pred, Node<E> succ) {
            Node<E> newest = new Node<>(e, pred, succ);  // liens du nouveau nœud
            pred.setNext(newest);                        // pred ──▶ newest
            succ.setPrev(newest);                        // newest ◀── succ
            size++;
            return newest;
        }
        ```

        **操作顺序重要吗？**

        是也不是。在这种情况下，由于我们首先创建了具有正确链接的新节点，所以更新 `pred` 和 `succ` 的顺序无关紧要。但如果用不同的方式操作，可能会丢失引用。

??? question "练习 2.4.2 — 四种插入方法"
    使用 `addBetween` 实现：

    - `addFirst(E e)` — 在开头添加
    - `addLast(E e)` — 在末尾添加
    - `addBefore(Position<E> p, E e)` — 在 p 之前添加
    - `addAfter(Position<E> p, E e)` — 在 p 之后添加

    ??? success "答案"
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

        **观察：** 借助 `addBetween`，四个方法都只需一行代码。这就是代码分解的力量！

---

### 2.5 删除

??? question "练习 2.5.1 — remove()"
    实现 `remove(Position<E> p)`，删除节点并返回其元素。

    **之前（删除 B）：**
    ```
    A ────▶ B ────▶ C
      ◀────   ◀────
    ```

    **之后：**
    ```
    A ────────────▶ C
      ◀────────────
    ```

    **重要：** 删除后，节点必须被标记为废弃，以防止被意外重用。

    ??? success "答案"
        ```java
        public E remove(Position<E> p) throws IllegalArgumentException {
            Node<E> node = validate(p);
            Node<E> predecessor = node.getPrev();
            Node<E> successor = node.getNext();

            // Bypass le nœud supprimé
            predecessor.setNext(successor);
            successor.setPrev(predecessor);
            size--;

            E answer = node.getElement();

            // Invalider le nœud (aide aussi le garbage collector)
            node.setElement(null);
            node.setNext(null);      // Convention: nœud défunt
            node.setPrev(null);

            return answer;
        }
        ```

---

### 2.6 set()

??? question "练习 2.6.1 — set() 方法"
    实现 `set(Position<E> p, E e)`，将位置 `p` 处的元素替换为 `e` 并返回旧元素。

    **注意：** 此操作不修改列表的结构，只修改节点的内容。

    ??? success "答案"
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

## 第三部分 — 实现循环列表

### 3.1 理解循环结构

在循环列表中，最后一个元素指向第一个元素，形成一个环。

```
Liste circulaire [A, B, C] avec pointeur tail vers C :

        ┌─────────────────────────────────┐
        │                                 │
        ▼                                 │
      ┌───┐     ┌───┐     ┌───┐          │
      │ A │────▶│ B │────▶│ C │──────────┘
      └───┘     └───┘     └───┘
        ↑                   ↑
      first              tail (dernier)
      = tail.next
```

!!! info "为什么指向最后一个元素？"
    通过指向**最后一个**元素的指针：

    - 访问最后一个：`tail` → O(1)
    - 访问第一个：`tail.next` → O(1)

    如果指向第一个元素，访问最后一个则需要完整的 O(n) 遍历。

以下是骨架：

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

### 3.2 访问操作

??? question "练习 3.2.1 — first() 和 last()"
    实现访问方法。请记住 `tail` 指向**最后一个**元素。

    ??? success "答案"
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

??? question "练习 3.2.2 — rotate()"
    旋转是循环列表的特征操作。它将列表"转动"一个位置：第一个元素变为最后一个。

    ```
    Avant rotate() : tail ──▶ C,  first = A
        A → B → C → (retour à A)

    Après rotate() : tail ──▶ A,  first = B
        B → C → A → (retour à B)
    ```

    **提示：** 这是一个非常简单的 O(1) 操作！

    ??? success "答案"
        ```java
        public void rotate() {
            if (tail != null)
                tail = tail.getNext();
        }
        ```

        就这样！将 `tail` 移动到 `tail.next`，原来的第一个元素变成了新的最后一个。循环性完成了所有工作。

---

### 3.3 插入

??? question "练习 3.3.1 — addFirst()"
    在列表开头插入一个元素。注意空列表的情况！

    **空列表情况：** 新节点必须指向自身。
    ```
    Avant : tail = null
    Après addFirst(A) :
        ┌───────┐
        │       │
        ▼       │
      ┌───┐     │
      │ A │─────┘
      └───┘
        ↑
      tail
    ```

    **非空列表情况：** 在 `tail` 和 `tail.next`（原来的第一个）之间插入。
    ```
    Avant : tail ──▶ C, liste = [A, B, C]
    Après addFirst(X) : tail ──▶ C, liste = [X, A, B, C]
    ```

    ??? success "答案"
        ```java
        public void addFirst(E e) {
            if (isEmpty()) {
                tail = new Node<>(e, null);
                tail.setNext(tail);  // pointe vers lui-même
            } else {
                Node<E> newest = new Node<>(e, tail.getNext());
                tail.setNext(newest);
            }
            size++;
        }
        ```

??? question "练习 3.3.2 — addLast()"
    在末尾插入一个元素。**技巧：** 复用 `addFirst`！

    思考：如果先在开头添加然后旋转，新元素会在哪里？

    ??? success "答案"
        ```java
        public void addLast(E e) {
            addFirst(e);
            tail = tail.getNext();  // le nouveau devient le dernier
        }
        ```

        **解释：**

        1. `addFirst(e)` 在开头插入新元素
        2. `tail = tail.getNext()` 将 `tail` 移动到这个新元素
        3. 结果：新元素现在是最后一个！

---

### 3.4 删除

??? question "练习 3.4.1 — removeFirst()"
    删除并返回第一个元素。注意只剩一个元素的情况！

    ??? success "答案"
        ```java
        public E removeFirst() {
            if (isEmpty()) return null;
            Node<E> head = tail.getNext();
            if (head == tail)        // un seul élément
                tail = null;
            else
                tail.setNext(head.getNext());
            size--;
            return head.getElement();
        }
        ```

??? question "练习 3.4.2 — removeLast()（较难）"
    删除并返回**最后一个**元素。

    **注意：** 此操作为 O(n)！为什么？

    要删除最后一个元素，需要更新**倒数第二个**节点的 `next` 指针。但在单向链表中，无法向后回溯 — 因此必须遍历整个列表来找到倒数第二个节点。

    ??? success "答案"
        ```java
        public E removeLast() {
            if (isEmpty()) return null;

            if (size == 1) {
                E element = tail.getElement();
                tail = null;
                size--;
                return element;
            }

            // Trouver l'avant-dernier nœud (O(n))
            Node<E> current = tail.getNext();  // commence au premier
            while (current.getNext() != tail) {
                current = current.getNext();
            }
            // current est maintenant l'avant-dernier

            E element = tail.getElement();
            current.setNext(tail.getNext());  // bypass tail
            tail = current;                    // nouveau dernier
            size--;
            return element;
        }
        ```

        **教训：** 如果需要频繁在末尾删除，请使用**双向**链表！

---

### 3.5 应用：烫手山芋游戏

**烫手山芋** (Hot Potato) 游戏是展示循环列表的经典案例：

- 玩家围成一圈
- 一个"山芋"在手中传递
- 经过 k 次传递后，持有山芋的玩家被淘汰
- 最后剩下的玩家获胜

??? question "练习 3.5.1 — 实现游戏"
    实现方法 `playHotPotato(CircularlyLinkedList<String> players, int k)`，模拟游戏并返回获胜者的名字。

    **示例：** 玩家 = [Alice, Bob, Carol, David, Eve]，k = 3

    - 第 1 轮：A→B→C，Carol 被淘汰 → [Alice, Bob, David, Eve]
    - 第 2 轮：D→E→A，Alice 被淘汰 → [Bob, David, Eve]
    - 第 3 轮：B→D→E，Eve 被淘汰 → [Bob, David]
    - 第 4 轮：B→D→B，Bob 被淘汰 → [David]
    - 获胜者：David

    ??? success "答案"
        ```java
        public static String playHotPotato(CircularlyLinkedList<String> players, int k) {
            if (players.isEmpty()) return null;

            while (players.size() > 1) {
                // Passer la patate k fois
                for (int i = 0; i < k; i++) {
                    players.rotate();
                }
                // Éliminer le joueur courant (premier de la liste)
                String eliminated = players.removeFirst();
                System.out.println(eliminated + " est éliminé(e) !");
            }

            return players.first();  // le gagnant
        }
        ```

        **变体（约瑟夫问题）：** 从历史上看，这个问题模拟了一群士兵围成一圈，每第 k 个人被淘汰。有 n 个士兵和 k 次传递时，应该选择哪个位置才能存活？这是一个著名的数学问题！

---

### 3.6 应用：循环缓冲区

**循环缓冲区** (Circular Buffer) 用于数据流（流媒体、日志等），当我们只想保留最近 N 个元素时。

??? question "练习 3.6.1 — CircularBuffer"
    实现一个固定大小的循环缓冲区。当缓冲区满时，添加新元素会覆盖最旧的元素。

    ```java
    public class CircularBuffer<E> {
        private E[] buffer;
        private int head = 0;  // index du plus ancien
        private int tail = 0;  // index du prochain ajout
        private int count = 0; // nombre d'éléments
        private int capacity;

        // À implémenter :
        // void add(E element)
        // E remove()
        // E peek()
    }
    ```

    ??? success "答案"
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
                // Buffer plein : on écrase le plus ancien
                head = (head + 1) % capacity;
            } else {
                count++;
            }
        }

        public E remove() {
            if (count == 0) return null;
            E element = buffer[head];
            buffer[head] = null;  // aide GC
            head = (head + 1) % capacity;
            count--;
            return element;
        }

        public E peek() {
            if (count == 0) return null;
            return buffer[head];
        }
        ```

        **注意：** 此实现使用**循环数组**而非链表。对于固定大小的缓冲区，这通常更高效，因为不需要动态分配节点。

---

## 补充练习

??? question "挑战 1 — 反转位置列表"
    编写一个方法，**原地**反转位置列表（不创建新列表或新节点）。

    ```java
    public static <E> void reverse(PositionalList<E> list)
    ```

    **示例：** `[A, B, C, D]` → `[D, C, B, A]`

    ??? success "答案"
        **方法 1：使用公共 API**
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

        **复杂度：** O(n) — 每个元素被移动一次。

        **方法 2：直接操作指针**（如果可以访问节点）
        ```java
        // À l'intérieur de LinkedPositionalList
        public void reverse() {
            if (size <= 1) return;

            Node<E> current = header;
            do {
                // Échanger prev et next pour chaque nœud
                Node<E> temp = current.prev;
                current.prev = current.next;
                current.next = temp;
                current = current.prev;  // avancer (qui est l'ancien next)
            } while (current != header);

            // Échanger header et trailer
            Node<E> temp = header;
            header = trailer;
            trailer = temp;
        }
        ```

??? question "挑战 2 — 合并两个有序列表"
    将两个**已排序**的位置列表合并为一个有序列表。输入列表可以被修改。

    ```java
    public static <E extends Comparable<E>> PositionalList<E> merge(
            PositionalList<E> list1, PositionalList<E> list2)
    ```

    ??? success "答案"
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

            // Ajouter les éléments restants
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

        **复杂度：** O(n + m)，其中 n 和 m 是两个列表的大小。

??? question "挑战 3 — 检测环"
    编写一个方法，检测一个单向链表（设计上非循环的）是否意外包含一个环。

    **提示：** Floyd 算法（龟兔赛跑）。

    ```java
    public static <E> boolean hasCycle(Node<E> head)
    ```

    ??? success "答案"
        ```java
        public static <E> boolean hasCycle(Node<E> head) {
            if (head == null) return false;

            Node<E> slow = head;  // tortue : avance de 1
            Node<E> fast = head;  // lièvre : avance de 2

            while (fast != null && fast.getNext() != null) {
                slow = slow.getNext();
                fast = fast.getNext().getNext();

                if (slow == fast) {
                    return true;  // cycle détecté !
                }
            }

            return false;  // fast a atteint la fin
        }
        ```

        **为什么有效？** 如果存在环，兔子（快的）最终会追上乌龟（慢的），因为它们在环中循环。如果不存在环，兔子会到达 `null`。

        **复杂度：** 时间 O(n)，空间 O(1)。

??? question "挑战 4 — 带 Position 的 indexOf"
    实现一个方法，返回元素的**位置**（不是索引），如果未找到则返回 `null`。

    ```java
    public Position<E> positionOf(E element)
    ```

    ??? success "答案"
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

        **注意：** 使用 `Objects.equals()` 来处理 `element` 为 `null` 的情况。

---

## 总结

| 结构 | 按索引访问 | 在开头插入 | 在末尾插入 | 在中间插入 |
|-----------|-----------------|-----------------|---------------|------------------|
| ArrayList | O(1) | O(n) | O(1) 均摊 | O(n) |
| 单向链表 | O(n) | O(1) | O(n)* | O(n)** |
| 双向链表 | O(n) | O(1) | O(1) | O(1)*** |
| 位置列表 | O(n) | O(1) | O(1) | O(1)*** |
| 循环列表 | O(n) | O(1) | O(1) | O(n)** |

*\* 如果维护了对最后一个节点的引用则为 O(1)*
*\*\* 如果拥有对前一个节点的引用则为 O(1)*
*\*\*\* 如果已经拥有该位置*

!!! tip "关键要点"
    1. **根据频繁操作选择结构**：如果中间的插入/删除操作频繁，请优先使用位置列表。
    2. **哨兵简化代码**：它们消除特殊情况并减少错误。
    3. **始终验证位置**：失效的位置可能导致不可预测的行为。
    4. **循环性有其用途**：轮转调度、缓冲区以及任何需要循环遍历的应用。
