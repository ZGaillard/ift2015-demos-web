# Java 25 + IntelliJ 配置

目标：能够在 IntelliJ 中编译和运行 Java 25 程序。

## 1) 安装 JDK 25

选择一个 JDK 25 发行版：
- Temurin (Eclipse Adoptium): https://adoptium.net/
- Oracle JDK: https://www.oracle.com/java/technologies/downloads/

注意：
- 请确保获取 JDK（不仅仅是 JRE）。
- 架构：根据您的机器选择 x64 或 ARM。

## 2) 在终端中验证

打开终端并检查：

```bash
java -version
javac -version
```

两个命令都应该显示 25。

## 3) 配置 IntelliJ

1. 打开 IntelliJ -> New Project。
2. 选择 "Java"。
3. Project SDK：选择 JDK 25。
4. Language level："SDK default"（或 25）。
5. 创建项目。

## 4) 用小程序测试

创建文件 `HelloDS.java`：

```java
public class HelloDS {
    public static void main(String[] args) {
        System.out.println("IFT2015 ready");
    }
}
```

点击 Run。您应该看到 `IFT2015 ready`。

## 5) 快速故障排除

- IntelliJ 中版本错误：
  - File -> Project Structure -> Project SDK = 25
- Gradle/Maven：
  - Settings -> Build Tools -> Gradle -> Gradle JVM = Project SDK
- 错误 "Unsupported class file major version 69"：
  - 终端或 IntelliJ 中的 Java 版本过旧。

如果遇到问题，请带上截图和 `java -version` 的输出。
