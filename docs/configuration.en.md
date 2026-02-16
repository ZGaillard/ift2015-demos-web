# Java 25 + IntelliJ Configuration

Goal: be able to compile and run a Java 25 program in IntelliJ.

## 1) Install a JDK 25

Choose a JDK 25 distribution:
- Temurin (Eclipse Adoptium): https://adoptium.net/
- Oracle JDK: https://www.oracle.com/java/technologies/downloads/

Notes:
- Make sure to get a JDK (not just a JRE).
- Architecture: x64 or ARM depending on your machine.

## 2) Verify in the terminal

Open a terminal and check:

```bash
java -version
javac -version
```

Both commands should display 25.

## 3) Configure IntelliJ

1. Open IntelliJ -> New Project.
2. Choose "Java".
3. Project SDK: select JDK 25.
4. Language level: "SDK default" (or 25).
5. Create the project.

## 4) Test with a small program

Create a file `HelloDS.java`:

```java
public class HelloDS {
    public static void main(String[] args) {
        System.out.println("IFT2015 ready");
    }
}
```

Click Run. You should see `IFT2015 ready`.

## 5) Quick Troubleshooting

- Wrong version in IntelliJ:
  - File -> Project Structure -> Project SDK = 25
- Gradle/Maven:
  - Settings -> Build Tools -> Gradle -> Gradle JVM = Project SDK
- Error "Unsupported class file major version 69":
  - Java is too old in the terminal or IntelliJ.

If you're stuck, bring a screenshot and the output of `java -version`.
