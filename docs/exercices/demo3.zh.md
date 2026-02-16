# 演示 3：栈、队列、双端队列与收藏列表

本演示涵盖教材 *Data Structures and Algorithms in Java (第6版)* 的**第6章** (*Stacks, Queues, and Deques*) 和**第7.7节** (*The Favorites List ADT*)。

!!! abstract "学习目标"

    完成本演示后，您应该能够：

    * 使用 Big-O 记号**分析算法复杂度**
    * 根据访问策略（LIFO vs FIFO）区分栈、队列和双端队列 ADT
    * 使用（循环）数组和链表实现这些结构
    * 理解"移至前端"（move-to-front）启发式方法并分析其对复杂度的影响
    * 将这些结构应用于实际问题（括号匹配、表达式求值、缓存）
    * 认识共享队列中的并发问题

---

## 回顾：渐近复杂度

在讨论数据结构之前，先复习**渐近复杂度**，这是分析算法效率的基本工具。

### 什么是渐近复杂度？

渐近复杂度描述当输入规模 (n) 变得非常大时，算法的**执行时间**（或**内存空间**）如何增长。

!!! info "核心思想"

    我们不测量以秒为单位的精确时间（这取决于硬件），而是衡量操作次数随 n 变化的**增长率**。

### Big-O 记号

**O(...)** 记号（"Big-O"）表示增长的**上界**：

> "f(n) 是 O(g(n))" 意味着对于足够大的 n，f(n) 增长**最多与** g(n) 一样快。

---

### 形式化数学定义

渐近记号用于比较函数的增长。以下是三种主要记号：

#### Big-O：渐近上界

!!! note "定义：O (Big-O)"

    设 f : ℕ → ℝ⁺ 和 g : ℕ → ℝ⁺ 为两个函数。

    $$f(n) \in O(g(n)) \iff \exists\, c > 0,\; \exists\, n_0 \in \mathbb{N},\; \forall\, n \geq n_0 : f(n) \leq c \cdot g(n)$$

    **通俗地说：** 从某个 n₀ 开始，f(n) 被 g(n) 的某个常数倍**上界**。

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
ax.set_ylabel('Temps', fontsize=14)
ax.set_title(r'Interprétation graphique de $f(n) \in O(g(n))$', fontsize=14)
ax.legend(loc='upper left', fontsize=12)
ax.set_xlim(0, 10)
ax.set_ylim(0, 65)
ax.annotate(r'$f(n) \leq c \cdot g(n)$', xy=(7, 25), fontsize=13,
            bbox=dict(boxstyle='round', facecolor='lightgreen', alpha=0.7))

plt.tight_layout()
```

??? example "示例：证明 3n² + 5n + 2 ∈ O(n²)"

    我们寻找 c > 0 和 n₀ 使得对所有 n ≥ n₀ 有 3n² + 5n + 2 ≤ c · n²。

    **方法：** 对于 n ≥ 1，有：

    * 5n ≤ 5n²
    * 2 ≤ 2n²

    因此：3n² + 5n + 2 ≤ 3n² + 5n² + 2n² = 10n²

    **结论：** 取 c = 10 且 n₀ = 1，对所有 n ≥ 1 有 3n² + 5n + 2 ≤ 10n²。

    因此 **3n² + 5n + 2 ∈ O(n²)** ✓

#### Big-Ω：渐近下界

!!! note "定义：Ω (Big-Omega)"

    $$f(n) \in \Omega(g(n)) \iff \exists\, c > 0,\; \exists\, n_0 \in \mathbb{N},\; \forall\, n \geq n_0 : f(n) \geq c \cdot g(n)$$

    **通俗地说：** 从某个 n₀ 开始，f(n) 被 g(n) 的某个常数倍**下界**。

Big-Ω 是 Big-O 的"镜像"：它表示 f 增长**至少与** g 一样快。

??? example "示例：证明 3n² + 5n + 2 ∈ Ω(n²)"

    我们寻找 c > 0 和 n₀ 使得对所有 n ≥ n₀ 有 3n² + 5n + 2 ≥ c · n²。

    由于对 n ≥ 0 有 5n ≥ 0 且 2 ≥ 0，因此：

    3n² + 5n + 2 ≥ 3n²

    **结论：** 取 c = 3 且 n₀ = 0，条件成立。

    因此 **3n² + 5n + 2 ∈ Ω(n²)** ✓

#### Big-Θ：渐近紧确界

!!! note "定义：Θ (Big-Theta)"

    $$f(n) \in \Theta(g(n)) \iff f(n) \in O(g(n)) \;\text{ et }\; f(n) \in \Omega(g(n))$$

    等价于：

    $$f(n) \in \Theta(g(n)) \iff \exists\, c_1, c_2 > 0,\; \exists\, n_0,\; \forall\, n \geq n_0 : c_1 \cdot g(n) \leq f(n) \leq c_2 \cdot g(n)$$

    **通俗地说：** f(n) 与 g(n) 以**完全相同的速率**增长（相差常数倍）。

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
ax.set_ylabel('Temps', fontsize=14)
ax.set_title(r'Interprétation graphique de $f(n) \in \Theta(g(n))$', fontsize=14)
ax.legend(loc='upper left', fontsize=12)
ax.set_xlim(0, 10)
ax.set_ylim(0, 80)
ax.annotate(r'$c_1 \cdot g(n) \leq f(n) \leq c_2 \cdot g(n)$', xy=(5.5, 12), fontsize=13,
            bbox=dict(boxstyle='round', facecolor='plum', alpha=0.7))

plt.tight_layout()
```

??? example "示例：证明 3n² + 5n + 2 ∈ Θ(n²)"

    我们已经证明了：

    * 3n² + 5n + 2 ∈ O(n²)，c₂ = 10
    * 3n² + 5n + 2 ∈ Ω(n²)，c₁ = 3

    因此 **3n² + 5n + 2 ∈ Θ(n²)** ✓

    我们说 3n² + 5n + 2 具有**平方级**增长。

#### 记号总结

| 记号 | 含义 | 类比 |
|----------|---------------|----------|
| f(n) ∈ O(g(n)) | f 增长**最多与** g 一样快 | f ≤ g（渐近地） |
| f(n) ∈ Ω(g(n)) | f 增长**至少与** g 一样快 | f ≥ g（渐近地） |
| f(n) ∈ Θ(g(n)) | f 增长**与** g **完全相同** | f ≈ g（渐近地） |
| f(n) ∈ o(g(n)) | f 增长**严格慢于** g | f < g（渐近地） |
| f(n) ∈ ω(g(n)) | f 增长**严格快于** g | f > g（渐近地） |

---

### 极限判据

**极限判据**通常比定义更便于比较两个函数：

!!! note "定理：极限判据"

    设 $L = \lim_{n \to \infty} \frac{f(n)}{g(n)}$（如果该极限存在或为 +∞）。则：

    | L 的值 | 结论 |
    |-------------|------------|
    | L = 0 | f(n) ∈ o(g(n)) ⊂ O(g(n)) — f 增长**严格慢于** g |
    | 0 < L < +∞ | f(n) ∈ Θ(g(n)) — f 和 g 具有**相同的增长率** |
    | L = +∞ | f(n) ∈ ω(g(n)) ⊂ Ω(g(n)) — f 增长**严格快于** g |

??? example "示例 1：比较 5n³ 和 2n²"

    $$L = \lim_{n \to \infty} \frac{5n^3}{2n^2} = \lim_{n \to \infty} \frac{5n}{2} = +\infty$$

    **结论：** 5n³ ∈ ω(2n²)，因此 5n³ 增长严格快于 2n²。

    换言之：**n³ 支配 n²**。

??? example "示例 2：比较 log(n) 和 √n"

    $$L = \lim_{n \to \infty} \frac{\log n}{\sqrt{n}}$$

    这是不定式 ∞/∞。应用洛必达法则：

    $$L = \lim_{n \to \infty} \frac{1/n}{1/(2\sqrt{n})} = \lim_{n \to \infty} \frac{2\sqrt{n}}{n} = \lim_{n \to \infty} \frac{2}{\sqrt{n}} = 0$$

    **结论：** log(n) ∈ o(√n)，因此 log(n) 增长严格慢于 √n。

    换言之：**√n 支配 log(n)**。

??? example "示例 3：比较 3n² + 7n 和 n²"

    $$L = \lim_{n \to \infty} \frac{3n^2 + 7n}{n^2} = \lim_{n \to \infty} \left(3 + \frac{7}{n}\right) = 3$$

    由于 0 < 3 < +∞，有 **3n² + 7n ∈ Θ(n²)**。

    两个函数具有相同的渐近增长率。

??? example "示例 4：比较 n! 和 2ⁿ"

    $$L = \lim_{n \to \infty} \frac{n!}{2^n}$$

    可以证明（通过达朗贝尔判据或斯特林公式）该极限为 +∞。

    **结论：** n! ∈ ω(2ⁿ)，因此 **n! 支配 2ⁿ**。

    阶乘增长快于指数增长！

---

### 增长率层次

以下是常见函数的增长顺序（从最慢到最快）：

$$1 \prec \log\log n \prec \log n \prec \sqrt{n} \prec n \prec n\log n \prec n^2 \prec n^3 \prec 2^n \prec n! \prec n^n$$

其中 $f \prec g$ 表示 $f(n) \in o(g(n))$，即 f 增长严格慢于 g。

---

### 常用性质

!!! info "Big-O 的代数性质"

    设 f₁(n) ∈ O(g₁(n)) 且 f₂(n) ∈ O(g₂(n))。则：

    1. **求和：** f₁(n) + f₂(n) ∈ O(max(g₁(n), g₂(n)))

    2. **乘积：** f₁(n) · f₂(n) ∈ O(g₁(n) · g₂(n))

    3. **常数：** c · f₁(n) ∈ O(g₁(n))，对任意常数 c > 0

    4. **传递性：** 若 f(n) ∈ O(g(n)) 且 g(n) ∈ O(h(n))，则 f(n) ∈ O(h(n))

??? example "性质应用"

    考虑一个算法包含：

    * 一个 O(n) 的循环
    * 接着一个 O(n²) 的循环
    * 其中执行一个 O(log n) 的操作

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

    **总复杂度：**

    * 循环 2：O(n²) · O(log n) = O(n² log n)
    * 总计：O(n) + O(n² log n) = O(max(n, n² log n)) = **O(n² log n)**

---

### 常见复杂度

| 记号 | 名称 | 示例 | 行为 |
|----------|-----|---------|--------------|
| O(1) | 常数 | 通过索引访问数组元素 | 无论 n 多大，都是瞬时的 |
| O(log n) | 对数 | 二分查找 | 非常高效，增长缓慢 |
| O(n) | 线性 | 遍历列表 | 与 n 成正比 |
| O(n log n) | 线性对数 | 归并排序 | 排序的高效复杂度 |
| O(n²) | 平方 | 插入排序（最坏情况） | 对于小 n 可接受 |
| O(2ⁿ) | 指数 | 某些暴力求解问题 | 对于大 n 不可行 |

```
Croissance comparée (pour n = 1000) :

O(1)       →           1 opération
O(log n)   →          10 opérations
O(n)       →       1,000 opérations
O(n log n) →      10,000 opérations
O(n²)      →   1,000,000 opérations
O(2ⁿ)      → 10^301 opérations (impossible!)
```

### 化简规则

分析算法时，我们对表达式进行化简：

| 规则 | 示例 | 结果 |
|-------|---------|----------|
| 忽略常数 | O(3n) | O(n) |
| 忽略低阶项 | O(n² + n) | O(n²) |
| 嵌套循环相乘 | O(n) 循环嵌套 O(n) 循环 | O(n²) |
| 顺序执行相加 | O(n) 后接 O(n) | O(n + n) = O(n) |

??? example "示例：分析一个算法"

    ```java
    public static int example(int[] arr) {
        int n = arr.length;
        int sum = 0;                    // O(1)

        for (int i = 0; i < n; i++) {   // 循环：n 次迭代
            sum += arr[i];              // 每次迭代 O(1)
        }

        for (int i = 0; i < n; i++) {       // 外层循环：n 次迭代
            for (int j = 0; j < n; j++) {   // 内层循环：n 次迭代
                sum += arr[i] * arr[j];     // O(1)
            }
        }

        return sum;                     // O(1)
    }
    ```

    **分析：**

    * 第一个循环：O(n)
    * 嵌套循环：O(n) × O(n) = O(n²)
    * 总计：O(1) + O(n) + O(n²) + O(1) = **O(n²)**

    主导项 O(n²)"吸收"了其他项。

### 最好情况、最坏情况、平均情况

同一算法可能因输入不同而有不同的复杂度：

| 类型 | 描述 | 示例（线性搜索） |
|------|-------------|------------------------------|
| **最好情况** | 最有利的输入 | 元素在开头找到：O(1) |
| **最坏情况** | 最不利的输入 | 元素不存在或在末尾：O(n) |
| **平均情况** | 所有输入的平均值 | 平均在中间：O(n/2) = O(n) |

!!! warning "重要约定"

    除非另有说明，当我们说"该算法是 O(n)"时，指的是**最坏情况**。这是实践中最有用的保证。

### 均摊复杂度

有时，一个操作**通常很快**但**偶尔很慢**。**均摊复杂度**将成本在一系列操作上进行平均。

??? example "示例：Java 中的 ArrayList.add()"

    * **正常情况**：添加一个元素需要 O(1)
    * **罕见情况**：当数组满时，需要扩容（复制 n 个元素）→ O(n)

    **均摊分析：**

    如果每次扩容将容量翻倍：

    * 在 n 次插入后，执行了大小为 1, 2, 4, 8, ..., n 的复制
    * 总复制次数：1 + 2 + 4 + ... + n ≈ 2n
    * 每次操作的平均代价：2n / n = **O(1) 均摊**

    ```
    Insertions :  1   2   3   4   5   6   7   8   9  ...
    Capacité :   [1] [2] [2] [4] [4] [4] [4] [8] [8] ...
                  ↑   ↑       ↑               ↑
              redim. redim.  redim.         redim.
    ```

    即使某些操作是 O(n)，从长远来看每个操作"平均花费" O(1)。

### 如何确定复杂度？

!!! tip "实用方法"

    1. **识别循环**：每个循环将复杂度乘以其迭代次数
    2. **发现递归调用**：画出递归树
    3. **寻找主导项**：它决定了整体复杂度
    4. **检查隐藏操作**：`list.contains()` 是 O(n)，不是 O(1)！

??? question "自我评估：复杂度是多少？"

    对于每个代码片段，确定其关于 n 的复杂度：

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

    ??? success "答案"

        **A) O(n)**

        循环执行 n/2 次迭代。忽略常数 1/2 → O(n)。

        **B) O(log n)**

        i 取值 1, 2, 4, 8, ...，直到 n。即 2^k = n，因此 k = log₂(n) 次迭代。

        **C) O(n²)**

        * i = 0：j 执行 n 次迭代
        * i = 1：j 执行 n-1 次迭代
        * ...
        * i = n-1：j 执行 1 次迭代

        总计：n + (n-1) + ... + 1 = n(n+1)/2 = **O(n²)**

---

## 理论回顾

### 栈 ADT（Stack）

**栈**是遵循 **LIFO**（*Last-In, First-Out*，后进先出）原则的元素集合：

* 只有**栈顶**元素可以访问
* 插入和删除只在栈顶进行
* 类比：一摞盘子或 PEZ 糖果分配器

| 操作 | 描述 | 复杂度 |
| --- | --- | --- |
| `push(e)` | 将 `e` 添加到栈顶 | O(1) |
| `pop()` | 移除并返回栈顶元素 | O(1) |
| `top()` | 返回栈顶元素但不移除 | O(1) |
| `size()` | 返回元素数量 | O(1) |
| `isEmpty()` | 检查栈是否为空 | O(1) |

### 队列 ADT（Queue）

**队列**是遵循 **FIFO**（*First-In, First-Out*，先进先出）原则的元素集合：

* 元素从**队尾**添加，从**队头**移除
* 类比：超市排队

| 操作 | 描述 | 复杂度 |
| --- | --- | --- |
| `enqueue(e)` | 将 `e` 添加到队尾 | O(1) |
| `dequeue()` | 移除并返回队头元素 | O(1) |
| `first()` | 返回队头元素但不移除 | O(1) |
| `size()` | 返回元素数量 | O(1) |
| `isEmpty()` | 检查队列是否为空 | O(1) |

### 双端队列 ADT（Deque）

**双端队列**（发音为"deck"）允许在**两端**进行插入和删除：

* 同时泛化了栈和队列
* 可以模拟栈（只使用一端）或队列（一端进，另一端出）

| 操作 | 描述 | 复杂度 |
| --- | --- | --- |
| `addFirst(e)` | 将 `e` 添加到前端 | O(1) |
| `addLast(e)` | 将 `e` 添加到后端 | O(1) |
| `removeFirst()` | 移除并返回前端元素 | O(1) |
| `removeLast()` | 移除并返回后端元素 | O(1) |
| `first()` / `last()` | 访问器（不删除） | O(1) |

### 循环队列

要使用数组实现队列，可以采用**循环方法**：

* 两个索引：`front`（前端）和 `rear`（后端）
* 索引通过取模运算符"环绕"：`(index + 1) % capacity`
* 避免在 `dequeue` 时移动所有元素

**循环队列**，`front=2`，`rear=5`，`capacity=8`：

| 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
|   |   | **A** | B | C |   |   |   |
|   |   | ↑front |   |   | ↑rear |   |   |

### 收藏列表与移至前端启发式方法

**收藏列表**维护按访问频率排序的元素：

* `access(e)`：访问元素 `e`，增加其计数器
* `remove(e)`：从列表中删除 `e`
* `getFavorites(k)`：返回访问次数最多的 `k` 个元素

**移至前端（move-to-front）启发式方法**是一种替代方案：

* 每次访问时，将元素移到列表开头
* 优势：最近访问的元素可以快速访问
* 劣势：`getFavorites(k)` 变为 O(kn)，因为列表不再按频率排序

---

## 第一部分 — 理论练习

### 1.1 判断对错

对于每个陈述，判断其是**对**还是**错**并解释原因。

??? question "问题 1 — 均摊复杂度"

    使用动态数组（满时将容量翻倍）实现的栈，其所有 `push` 操作的**均摊**复杂度为 O(1)。

    ??? success "答案"

        **对。** 虽然偶尔的扩容需要 O(n)，但它发生的频率越来越低。在 n 次 `push` 操作的序列中，总代价为 O(n)，因此每次操作的均摊代价为 O(1)。

        这与 Java 中 `ArrayList.add()` 的原理相同。

??? question "问题 2 — 循环队列的空与满"

    在使用 `front` 和 `rear` 索引的循环队列中，条件 `front == rear` **总是**意味着队列为空。

    ??? success "答案"

        **错。** 这是循环队列的经典歧义！条件 `front == rear` 可能意味着：

        * 队列**为空**（没有元素）
        * 队列**已满**（所有位置都被占用）

        常见解决方案：

        1. 维护一个单独的 `size` 计数器
        2. 永远不完全填满数组（保留一个空位）
        3. 使用布尔标志 `isEmpty`

??? question "问题 3 — 双端队列作为栈和队列"

    双端队列（Deque）可以同时模拟栈和队列。

    ??? success "答案"

        **对。** 双端队列是更通用的结构：

        * **作为栈**：使用 `addFirst()`/`removeFirst()`（或 `addLast()`/`removeLast()`）
        * **作为队列**：使用 `addLast()`/`removeFirst()`

        这就是为什么 Java 推荐在新实现中使用 `ArrayDeque` 而不是 `Stack`。

??? question "问题 4 — 移至前端 vs 按频率排序"

    移至前端启发式方法**总是**比维护按访问频率排序的列表具有更好的性能。

    ??? success "答案"

        **错。** 移至前端是一种**启发式方法**，而非保证。

        * **优势**：如果访问具有时间局部性（最近访问的元素很可能被再次访问），移至前端非常有效。
        * **劣势**：对于均匀分布的访问序列，移至前端可能效率较低，因为它不断打乱顺序。

        此外，使用移至前端时 `getFavorites(k)` 变为 O(kn)，而使用排序列表时为 O(k)。

??? question "问题 5 — 使用链表实现栈"

    在使用单链表实现的栈中，`push` 和 `pop` 操作必须在列表**末尾**进行才能达到 O(1)。

    ??? success "答案"

        **错。** 恰恰相反！在**单链表**中：

        * 在**头部**插入/删除：O(1) — 有直接引用 `head`
        * 在**尾部**插入：如果维护 `tail` 则为 O(1)，但在尾部删除：O(n) — 需要找到倒数第二个节点

        因此，要实现 O(1) 的栈，应使用链表的**头部**作为栈顶。

??? question "问题 6 — pop 后的失效"

    考虑以下代码：
    ```java
    Stack<String> stack = new ArrayStack<>();
    stack.push("A");
    stack.push("B");
    String x = stack.pop();
    String y = stack.top();
    // 此时，x.equals(y) 为 true
    ```

    ??? success "答案"

        **错。** 操作执行后：

        1. `push("A")` → 栈：`[A]`
        2. `push("B")` → 栈：`[A, B]`
        3. `pop()` → 返回 `"B"`，栈：`[A]`
        4. `top()` → 返回 `"A"`

        因此 `x = "B"` 且 `y = "A"`，`x.equals(y)` 为**假**。

---

### 1.2 选择题

??? question "问题 7 — 栈的执行追踪"

    考虑一个初始为空的栈。执行以下操作：

    ```
    push(1), push(2), pop(), push(3), push(4), pop(), pop(), push(5)
    ```

    操作完成后栈的内容是什么（从底到顶）？

    - [ ] A) `[1, 5]`
    - [ ] B) `[1, 3, 5]`
    - [ ] C) `[1, 2, 5]`
    - [ ] D) `[5]`

    ??? success "答案"

        **A) `[1, 5]`**

        逐步追踪操作：

        1. `push(1)` → `[1]`
        2. `push(2)` → `[1, 2]`
        3. `pop()` → 返回 2，栈：`[1]`
        4. `push(3)` → `[1, 3]`
        5. `push(4)` → `[1, 3, 4]`
        6. `pop()` → 返回 4，栈：`[1, 3]`
        7. `pop()` → 返回 3，栈：`[1]`
        8. `push(5)` → `[1, 5]`

??? question "问题 8 — 队列的执行追踪"

    考虑一个初始为空的队列。执行以下操作：

    ```
    enqueue(A), enqueue(B), dequeue(), enqueue(C), dequeue(), enqueue(D)
    ```

    队列的内容是什么（从前到后）？

    - [ ] A) `[A, D]`
    - [ ] B) `[C, D]`
    - [ ] C) `[B, C, D]`
    - [ ] D) `[D]`

    ??? success "答案"

        **B) `[C, D]`**

        逐步追踪操作：

        1. `enqueue(A)` → `[A]`
        2. `enqueue(B)` → `[A, B]`
        3. `dequeue()` → 返回 A，队列：`[B]`
        4. `enqueue(C)` → `[B, C]`
        5. `dequeue()` → 返回 B，队列：`[C]`
        6. `enqueue(D)` → `[C, D]`

??? question "问题 9 — 循环队列的索引"

    一个容量为 6 的循环队列当前 `front = 4`，包含 3 个元素。`rear`（下一个元素将要添加的索引）的值是多少？

    - [ ] A) 0
    - [ ] B) 1
    - [ ] C) 6
    - [ ] D) 7

    ??? success "答案"

        **B) 1**

        3 个元素占据索引 4、5 和 0（循环环绕，因为索引 5 之后回到 0）。

        计算：`rear = (front + size) % capacity = (4 + 3) % 6 = 7 % 6 = 1`

        | 0 | 1 | 2 | 3 | 4 | 5 |
        |:-:|:-:|:-:|:-:|:-:|:-:|
        | C |   |   |   | **A** | B |
        | (wrap) | ↑rear |   |   | ↑front |   |

        队列的逻辑顺序为：A (front) → B → C → [rear = 下一个添加位置为索引 1]

??? question "问题 10 — getFavorites 的复杂度"

    在包含 n 个元素的 `FavoritesListMTF`（移至前端）中，`getFavorites(k)` 的复杂度是多少？

    - [ ] A) O(k)
    - [ ] B) O(n)
    - [ ] C) O(kn)
    - [ ] D) O(n log n)

    ??? success "答案"

        **C) O(kn)**

        使用移至前端时，列表**未按**访问频率排序。要找到访问次数最多的 k 个元素：

        1. 需要遍历整个列表找到最大值 → O(n)
        2. 重复 k 次 → O(kn)

        使用按频率排序的列表，复杂度为 O(k)，因为前 k 个元素已经是访问最多的。

---

### 1.3 思考题

??? question "问题 11 — 为什么使用循环队列？"

    我们要用数组实现队列。比较两种方法：

    **方法 A（朴素）**：`front` 始终在索引 0。每次 `dequeue` 后，将所有元素向左移动。

    **方法 B（循环）**：维护一个 `front` 索引，使用取模运算符向前推进，无需移动元素。

    解释为什么方法 B 更好，以及复杂度方面的收益。

    ??? success "答案"

        **方法 A 的问题：**

        执行 `dequeue` 后，需要将所有剩余元素向左移动一个位置以保持索引 0 为队头。每次删除需要 **O(n)** 的移动。

        ```
        Avant dequeue:  [A, B, C, D, _, _]
        Après dequeue:  [B, C, D, _, _, _]  ← décalage de 3 éléments : O(n)
        ```

        **方法 B（循环）的优势：**

        只需简单地增加 `front` 索引（使用取模实现环绕）。无需移动元素 → **O(1)**。

        ```
        Avant dequeue: front=0  [A, B, C, D, _, _]
        Après dequeue: front=1  [_, B, C, D, _, _]  ← juste front++ : O(1)
        ```

        **收益：** `dequeue` 操作从 O(n) 降到 O(1)，这对于频繁使用的队列至关重要。

??? question "问题 12 — 栈与递归"

    递归与栈密切相关。解释这种联系，并给出将递归算法转换为使用栈的迭代算法的示例。

    ??? success "答案"

        **联系：**

        每次递归调用都使用系统的**调用栈**（call stack）：

        * 参数和局部变量被压栈
        * 返回时被弹栈

        **递归 → 迭代的转换：**

        以深度优先遍历（DFS）为例：

        ```java
        // Version récursive
        void dfsRecursive(Node node) {
            if (node == null) return;
            visit(node);
            dfsRecursive(node.left);
            dfsRecursive(node.right);
        }

        // Version itérative avec pile explicite
        void dfsIterative(Node root) {
            Stack<Node> stack = new ArrayStack<>();
            stack.push(root);
            while (!stack.isEmpty()) {
                Node node = stack.pop();
                if (node == null) continue;
                visit(node);
                stack.push(node.right);  // right d'abord car LIFO
                stack.push(node.left);
            }
        }
        ```

        **迭代版本的优势：** 避免对非常深的结构产生栈溢出（stack overflow）。

??? question "问题 13 — 移至前端与时间局部性"

    解释什么是**时间局部性**，以及为什么移至前端启发式方法能利用它。

    ??? success "答案"

        **时间局部性：**

        这是一个原则：最近被访问的元素很可能在不久的将来被再次访问。例如：

        * 最近访问的网页
        * 最近打开的文件
        * 最近使用的变量（CPU 缓存）

        **移至前端利用了这一点：**

        通过将每个被访问的元素移到列表开头：

        * "热门"元素（最近频繁访问的）靠近列表开头
        * 对这些元素的后续搜索很快（前 k 个元素为 O(1) 到 O(k)）
        * "冷门"元素自然地迁移到列表末尾

        **类比：** 这类似于操作系统和数据库中使用的 LRU（最近最少使用）缓存。

??? question "问题 14 — 中缀、前缀和后缀表示法"

    算术表达式可以用三种表示法书写：

    | 表示法 | (3 + 4) × 5 的示例 |
    |----------|--------------------------|
    | **中缀**（标准） | `(3 + 4) * 5` |
    | **前缀**（波兰表示法） | `* + 3 4 5` |
    | **后缀**（逆波兰表示法） | `3 4 + 5 *` |

    1. 为什么前缀和后缀表示法不需要括号？
    2. 哪种数据结构用于计算后缀表达式？为什么？
    3. 哪种结构适合将中缀表达式转换为后缀？

    ??? success "答案"

        **1. 不需要括号：**

        在前缀/后缀表示法中，运算符相对于操作数的位置**无歧义地**确定了运算顺序。不需要括号或优先级规则。

        * 中缀：`3 + 4 * 5` → 不明确，除非有规则（是 35 还是 23？）
        * 后缀：`3 4 5 * +` = 3 + (4×5) = 23
        * 后缀：`3 4 + 5 *` = (3+4) × 5 = 35

        **2. 后缀求值 → 栈**

        使用**栈**，因为操作数按 LIFO 顺序处理：

        ```
        Expression : 3 4 + 5 *

        Lecture | Action              | Pile
        --------|---------------------|--------
        3       | push(3)             | [3]
        4       | push(4)             | [3, 4]
        +       | pop 4, pop 3        | []
                | push(3+4=7)         | [7]
        5       | push(5)             | [7, 5]
        *       | pop 5, pop 7        | []
                | push(7*5=35)        | [35]

        Résultat : 35
        ```

        最近压入栈的两个操作数正是需要组合的 → 完美契合栈的 LIFO 行为。

        **3. 中缀 → 后缀转换 → 栈（调度场算法）**

        在 Dijkstra 算法中也使用**栈**（用于运算符）：

        * 操作数直接输出
        * 运算符压栈，然后根据优先级弹出
        * 左括号压栈，右括号触发弹出

        栈允许"保留"低优先级的运算符，直到高优先级的运算符被处理完。

---

## 第二部分 — 栈的实现

### 2.1 理解结构

栈可以通过两种主要方式实现：

**数组实现的栈**（栈顶在右侧，`t=3`）：

| Index | 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
| --- | --- | --- | --- | --- | --- | --- | --- | --- |
| Valeur | A | B | C | **D (top)** |  |  |  |  |

**链表实现的栈**（栈顶 = head）：

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

栈的接口如下：

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

### 2.2 数组实现

??? example "练习 2.2.1 — ArrayStack"

    使用固定容量的数组实现一个栈。

    ```java
    public class ArrayStack<E> implements Stack<E> {
        public static final int CAPACITY = 1000;
        private E[] data;
        private int t = -1;  // index du sommet (-1 = vide)

        // À implémenter :
        // Constructeur, size(), isEmpty(), push(e), top(), pop()
    }
    ```

    ??? success "答案"

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
                data[++t] = e;  // incrémente t PUIS stocke
            }

            public E top() {
                if (isEmpty()) return null;
                return data[t];
            }

            public E pop() {
                if (isEmpty()) return null;
                E answer = data[t];
                data[t] = null;  // aide le garbage collector
                t--;
                return answer;
            }
        }
        ```

        **关键点：**

        * `push` 中的 `++t`：先自增，再使用新值
        * `pop` 中的 `data[t] = null`：避免内存泄漏（过期引用）

---

### 2.3 链表实现

??? example "练习 2.3.1 — LinkedStack"

    使用单链表实现一个栈。栈顶是链表的头节点。

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

        // À implémenter...
    }
    ```

    ??? success "答案"

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
                top = new Node<>(e, top);  // nouveau nœud pointe vers l'ancien sommet
                size++;
            }

            public E top() {
                if (isEmpty()) return null;
                return top.getElement();
            }

            public E pop() {
                if (isEmpty()) return null;
                E answer = top.getElement();
                top = top.getNext();  // le sommet devient le suivant
                size--;
                return answer;
            }
        }
        ```

        **相比 ArrayStack 的优势：** 没有容量限制，无需扩容。

---

## 第三部分 — 队列的实现

### 3.1 理解循环队列

**容量为 8、含 4 个元素的循环队列：**

| 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
|   |   | **A** | B | C | D |   |   |
|   |   | ↑front |   |   |   | ↑rear |   |

**执行 `enqueue(E)` 后：**

| 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
|   |   | **A** | B | C | D | E |   |
|   |   | ↑front |   |   |   |   | ↑rear |

**执行 `dequeue()` 后（返回 A）：**

| 0 | 1 | 2 | 3 | 4 | 5 | 6 | 7 |
|:-:|:-:|:-:|:-:|:-:|:-:|:-:|:-:|
|   |   |   | **B** | C | D | E |   |
|   |   |   | ↑front |   |   |   | ↑rear |

队列的接口：

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

### 3.2 循环数组实现

??? example "练习 3.2.1 — ArrayQueue"

    使用循环数组实现队列。使用取模运算符实现环绕。

    ```java
    public class ArrayQueue<E> implements Queue<E> {
        public static final int CAPACITY = 1000;
        private E[] data;
        private int front = 0;
        private int size = 0;

        // À implémenter...
        // Note : rear = (front + size) % data.length
    }
    ```

    ??? success "答案"

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
                data[front] = null;  // aide GC
                front = (front + 1) % data.length;  // avance circulairement
                size--;
                return answer;
            }
        }
        ```

        **关键点：**

        * 维护 `size` 而非 `rear` 以避免空/满歧义
        * `(front + 1) % data.length` 确保循环环绕

---

### 3.3 链表实现

??? example "练习 3.3.1 — LinkedQueue"

    使用单链表实现队列。维护指向 `head`（前端）和 `tail`（后端）的引用。

    ??? success "答案"

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
                    tail = null;  // la file est maintenant vide
                return answer;
            }
        }
        ```

        **注意边界情况：** 当 `dequeue` 后队列变空时，也需要将 `tail` 设为 `null`。

---

## 第四部分 — 双端队列的实现

### 4.1 接口与结构

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

双端队列可以通过以下方式实现：

* **循环数组**（类似 `ArrayQueue`，但两端都可添加/删除）
* **双向链表**（两端 O(1) 访问）

??? example "练习 4.1.1 — DoublyLinkedDeque"

    使用带哨兵节点的双向链表实现双端队列。

    **提示：** 复用演示 2 中位置列表的结构，但只暴露两端的操作。

    ??? success "答案"

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
                // getters et setters...
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

            // Méthode utilitaire privée
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

            // Méthode utilitaire privée
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

## 第五部分 — 收藏列表

### 5.1 理解结构

收藏列表维护带有**访问计数器**的元素：

**FavoritesList** 在执行 `access(A), access(B), access(A), access(C), access(A)` 后：

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

*元素 A 是访问次数最多的（3 次访问），位于列表头部。*

### 5.2 基本实现

??? example "练习 5.2.1 — 内部类 Item"

    创建一个内部类 `Item`，存储元素及其访问计数器。

    ??? success "答案"

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

??? example "练习 5.2.2 — access(e) 方法"

    实现 `access(E e)` 方法，该方法：

    1. 在列表中搜索元素
    2. 如果存在，增加其计数器
    3. 否则，添加该元素并将计数器设为 1
    4. 移动元素以维护计数器的降序排列

    ??? success "答案"

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

??? example "练习 5.2.3 — getFavorites(k) 方法"

    实现 `getFavorites(int k)` 方法，返回访问次数最多的 k 个元素。

    ??? success "答案"

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

        由于列表按计数器降序排列，前 k 个就是收藏！

---

### 5.3 移至前端启发式方法

??? example "练习 5.3.1 — FavoritesListMTF"

    创建子类 `FavoritesListMTF`，重写 `moveUp` 方法，将元素移到开头而非维护排序顺序。

    ??? success "答案"

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

                // Copier dans une liste temporaire
                PositionalList<Item<E>> temp = new LinkedPositionalList<>();
                for (Item<E> item : list)
                    temp.addLast(item);

                // Trouver les k maximums
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

        **getFavorites(k) 的复杂度：** O(kn) — 遍历列表 k 次以找到每个最大值。

---

## 第六部分 — 队列与并发（简介）

### 6.1 问题

当多个**线程**访问共享队列时，会出现问题：

```java
// Thread 1                    // Thread 2
queue.enqueue("A");            queue.enqueue("B");
```

没有同步机制的情况下，两个线程可能同时修改 `size` 或 `rear`，从而破坏数据结构。

### 6.2 竞态条件（Race Conditions）

??? example "练习 6.2.1 — 识别问题"

    考虑以下 `enqueue` 的简化实现：

    ```java
    public void enqueue(E e) {
        int rear = (front + size) % data.length;  // Ligne 1
        data[rear] = e;                            // Ligne 2
        size++;                                    // Ligne 3
    }
    ```

    如果两个线程 T1 和 T2 在 `size = 5` 且 `front = 0` 时同时调用 `enqueue`，会发生什么？

    ??? success "答案"

        **问题场景：**

        1. T1 执行第 1 行：`rear = 5`
        2. T2 执行第 1 行：`rear = 5`（相同值，因为 `size` 还未改变！）
        3. T1 执行第 2 行：`data[5] = "A"`
        4. T2 执行第 2 行：`data[5] = "B"`（覆盖了 "A"！）
        5. T1 执行第 3 行：`size = 6`
        6. T2 执行第 3 行：`size = 7`

        **结果：** "A" 丢失了，而 `size` 被错误地增加了两次，但实际上只有一个元素被有效添加到了有效位置。

---

### 6.3 Java 中的解决方案

#### Synchronized

```java
public synchronized void enqueue(E e) {
    // Un seul thread peut exécuter ce bloc à la fois
    int rear = (front + size) % data.length;
    data[rear] = e;
    size++;
}

public synchronized E dequeue() {
    // ...
}
```

#### BlockingQueue

Java 在 `java.util.concurrent` 中提供了线程安全的队列：

```java
import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.BlockingQueue;

BlockingQueue<String> queue = new ArrayBlockingQueue<>(100);

// Thread producteur
queue.put("item");  // Bloque si plein

// Thread consommateur
String item = queue.take();  // Bloque si vide
```

??? example "练习 6.3.1 — 生产者-消费者"

    完成这个生产者-消费者模拟程序：

    ```java
    public class ProducerConsumer {
        public static void main(String[] args) {
            BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

            // Producteur : ajoute les nombres 1 à 10
            Thread producer = new Thread(() -> {
                // À compléter
            });

            // Consommateur : retire et affiche les éléments
            Thread consumer = new Thread(() -> {
                // À compléter
            });

            producer.start();
            consumer.start();
        }
    }
    ```

    ??? success "答案"

        ```java
        public class ProducerConsumer {
            public static void main(String[] args) {
                BlockingQueue<Integer> queue = new ArrayBlockingQueue<>(5);

                Thread producer = new Thread(() -> {
                    try {
                        for (int i = 1; i <= 10; i++) {
                            System.out.println("Producing: " + i);
                            queue.put(i);  // Bloque si la file est pleine
                            Thread.sleep(100);  // Simule du travail
                        }
                        queue.put(-1);  // Signal de fin
                    } catch (InterruptedException e) {
                        Thread.currentThread().interrupt();
                    }
                });

                Thread consumer = new Thread(() -> {
                    try {
                        while (true) {
                            Integer item = queue.take();  // Bloque si vide
                            if (item == -1) break;  // Signal de fin
                            System.out.println("Consuming: " + item);
                            Thread.sleep(150);  // Consommateur plus lent
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

        **观察：** 生产者有时会被阻塞，因为队列（容量为 5）填充速度比清空速度快。

---

## 实际应用

### 应用 1：括号匹配验证

??? example "练习 — 平衡括号"

    实现一个方法，验证字符串中的括号是否正确匹配。

    示例：

    * `"((()))"` → true
    * `"({[]})"` → true
    * `"(()"` → false
    * `"([)]"` → false

    ??? success "答案"

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

### 应用 2：后缀表达式求值

??? example "练习 — 后缀计算器"

    计算后缀（逆波兰）表示法的表达式。

    示例：`"3 4 + 5 *"` = (3 + 4) × 5 = 35

    ??? success "答案"

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

### 应用 3：轮转调度模拟

??? example "练习 — 进程调度器"

    模拟一个轮转（Round-Robin）调度器，其中每个进程获得固定的时间片。

    ??? success "答案"

        ```java
        public static void roundRobinScheduler(String[] processes, int quantum) {
            Queue<String> queue = new ArrayQueue<>();
            int[] remainingTime = {10, 5, 8};  // Temps restant pour chaque processus

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
                    queue.enqueue(current);  // Remet dans la file
                } else {
                    System.out.println(current + " completed!");
                }
            }
        }
        ```

---

## 补充练习

??? tip "挑战 1 — 用两个队列实现栈"

    仅使用两个队列实现一个栈。分析其复杂度。

    ??? success "答案"

        ```java
        public class StackWithQueues<E> implements Stack<E> {
            private Queue<E> q1 = new LinkedQueue<>();
            private Queue<E> q2 = new LinkedQueue<>();

            public void push(E e) {
                q1.enqueue(e);
            }

            public E pop() {
                if (q1.isEmpty()) return null;

                // Déplacer tout sauf le dernier vers q2
                while (q1.size() > 1) {
                    q2.enqueue(q1.dequeue());
                }
                E result = q1.dequeue();

                // Échanger q1 et q2
                Queue<E> temp = q1;
                q1 = q2;
                q2 = temp;

                return result;
            }

            // ... autres méthodes
        }
        ```

        **复杂度：** `push` = O(1)，`pop` = O(n)

??? tip "挑战 2 — 用双端队列检测回文"

    使用双端队列（Deque）验证字符串是否为回文。

    ??? success "答案"

        ```java
        public static boolean isPalindrome(String s) {
            Deque<Character> deque = new LinkedDeque<>();

            // Ajouter les caractères (en ignorant espaces et casse)
            for (char c : s.toLowerCase().toCharArray()) {
                if (Character.isLetterOrDigit(c))
                    deque.addLast(c);
            }

            // Comparer des deux côtés
            while (deque.size() > 1) {
                if (!deque.removeFirst().equals(deque.removeLast()))
                    return false;
            }
            return true;
        }
        ```

??? tip "挑战 3 — MinStack"

    实现一个栈，除标准操作外还支持 O(1) 的 `getMin()` 操作。

    ??? success "答案"

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

        **思路：** 维护一个辅助栈来存储当前最小值。

---

## 总结

| 结构 | 策略 | push/enqueue | pop/dequeue | 另一端访问 | 典型用例 |
| --- | --- | --- | --- | --- | --- |
| **栈 (Stack)** | LIFO | O(1) | O(1) | ✗ | 撤销、解析、DFS |
| **队列 (Queue)** | FIFO | O(1) | O(1) | ✗ | BFS、调度、缓冲区 |
| **双端队列 (Deque)** | 两者兼有 | O(1) | O(1) | O(1) | 撤销/重做、滑动窗口 |
| **FavoritesList** | 按频率 | O(n) | O(n) | O(k) | 推荐系统 |
| **FavoritesListMTF** | 移至前端 | O(n) | O(n) | O(kn) | 自适应缓存 |

!!! success "关键要点"

    1. **LIFO vs FIFO**：需要逆序时选择栈，需要按到达顺序时选择队列。
    2. **循环队列**：通过模运算避免 O(n) 的元素移动。
    3. **双端队列（Deque）**：泛化栈和队列的通用结构。
    4. **移至前端**：当存在时间局部性时非常有效的启发式方法。
    5. **并发**：线程间共享结构时必须同步访问。

---

## 参考资料

* Goodrich, Tamassia, Goldwasser. *Data Structures and Algorithms in Java*, 第6版。
    * 第6章：Stacks, Queues, and Deques
    * 第7.7节：The Favorites List ADT
* Java 文档：[`java.util.Deque`](https://docs.oracle.com/javase/8/docs/api/java/util/Deque.html), [`java.util.concurrent.BlockingQueue`](https://docs.oracle.com/javase/8/docs/api/java/util/concurrent/BlockingQueue.html)
