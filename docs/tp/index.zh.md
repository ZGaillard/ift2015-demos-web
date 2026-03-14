# :material-numeric-1-circle: 实验1 — 自动补全

[:material-folder-zip: 下载实验1](../files/tp-autocompleter.zip){ .md-button .md-button--primary }

!!! info "演示课说明"

    本实验将在 **3月17日的演示课**上详细介绍。

---

## 背景与目标

自动补全系统依赖于从大量文本中提取的统计信息。在本实验中，您将实现这样一个系统的基础组件，重点在于**选择并高效使用数据结构**。

该模型基于 **n-gram**：从语料库中提取的连续词元序列。预测使用 **Katz 回退法** —— 先查找三元组上下文，再查找二元组，最后查找一元组。前缀补全通过**字典树（trie）**实现。

**学习目标：**

- 为每个问题选择合适的数据结构
- 分析操作的时间和空间复杂度
- 实现高效算法（堆、字典树）
- 将多个数据结构整合到一个可运行的语言模型中

---

## 项目文件

Maven 项目位于 `src/main/java/ca/udem/ift2015/autocompleter/`。提供的包（`model/`、`preprocessing/`、`gui/`）**不得修改**。

您只需完成 `student/` 中的 **4 个文件**：

| 文件 | TODOs | 分值 |
|---|---|---|
| `HashFrequencyTable.java` | 1 – 6 | 8 分 |
| `HeapTopKStrategy.java` | 7 | 10 分 |
| `PrefixTrie.java` | 8 – 10 | 14 分 |
| `KatzBackoffModel.java` | 11 – 14 | 18 分 |

每个 TODO 的详细说明见 **`instructions/TP.pdf`**（包含在压缩包中）。

---

## 图形界面

使用以下命令启动界面：

```bash
mvn javafx:run
```

界面包含四个标签页：

| 标签页 | 说明 |
|---|---|
| **Corpus** | 从 `src/main/resources/corpus/` 中选择一个或多个 `.txt` 文件，然后点击「Entraîner la sélection」 |
| **演示** | 实时测试自动补全 —— 按词显示建议，并详细列出每个级别的前5项（三元组、二元组、一元组、字典树） |
| **基准测试** | 测量训练和查询时间及内存使用情况 |
| **Autograder** | 运行自动评分（63个JUnit测试）。**提交前请使用此工具验证您的代码。** |

---

## 报告

请回答 **`instructions/rapport.pdf`**（包含在压缩包中）中的问题。您可以使用 **`instructions/rapport.tex`** 作为 LaTeX 模板。

| 问题 | 主题 | 分值 |
|---|---|---|
| Q1 | 数据结构不变量 | 12 分 |
| Q2 | 训练的渐近复杂度 | 12 分 |
| Q3 | 查询的渐近复杂度 | 10 分 |
| Q4 | 替代实现的比较 | 6 分 |

---

## 评分标准

| 评分项 | 分值 |
|---|---|
| 实现（Autograder） | 50 分 |
| 代码质量与注释 | 10 分 |
| 报告 | 40 分 |
| **总计** | **100 分** |

!!! success "加分项"

    如果报告以**打印版**在课堂上提交（除了在 Studium 上的数字提交），**最终成绩加 5%**。加分适用于总分，上限为 100 分。

---

## 提交说明

**截止日期：4月12日**，在 Studium 上提交。

提交一个 `.zip` 或 `.tar.gz` 压缩包，包含：

1. `student/` 文件夹中的 4 个 Java 文件：
    - `HashFrequencyTable.java`
    - `HeapTopKStrategy.java`
    - `PrefixTrie.java`
    - `KatzBackoffModel.java`
2. PDF 格式的报告，命名为 `rapport_prenom1Nom1_prenom2Nom2.pdf`
   *示例：* `rapport_aliceDupont_bobMartin.pdf`

**合规要求：**

- 项目必须无错误编译：`mvn compile`
- 只有 `student/` 文件夹中的文件应被修改
- 不得在 `pom.xml` 中添加外部依赖
- 报告必须仅以 PDF 格式提交

!!! info "加分项 — 打印版"

    要获得加分，请**不迟于4月14日的课堂**提交报告的打印版。
