# Configuration Java 25 + IntelliJ

Objectif : pouvoir compiler et exécuter un programme Java 25 dans IntelliJ.

## 1) Installer un JDK 25

Choisissez une distribution JDK 25 :
- Temurin (Eclipse Adoptium): https://adoptium.net/
- Oracle JDK: https://www.oracle.com/java/technologies/downloads/

Notes :
- Prenez un JDK (pas seulement un JRE).
- Architecture: x64 ou ARM selon votre machine.

## 2) Vérifier dans le terminal

Ouvrez un terminal et vérifiez :

```bash
java -version
javac -version
```

Les deux commandes doivent afficher 25.

## 3) Configurer IntelliJ

1. Ouvrir IntelliJ -> New Project.
2. Choisir "Java".
3. Project SDK : sélectionner JDK 25.
4. Language level : "SDK default" (ou 25).
5. Créer le projet.

## 4) Tester avec un mini programme

Créez un fichier `HelloDS.java` :

```java
public class HelloDS {
    public static void main(String[] args) {
        System.out.println("IFT2015 ready");
    }
}
```

Cliquez sur Run. Vous devez voir `IFT2015 ready`.

## 5) Dépannage rapide

- Mauvaise version dans IntelliJ :
  - File -> Project Structure -> Project SDK = 25
- Gradle/Maven :
  - Settings -> Build Tools -> Gradle -> Gradle JVM = Project SDK
- Erreur "Unsupported class file major version 69" :
  - Java trop ancien dans le terminal ou IntelliJ.

Si vous bloquez, apportez une capture d'écran et la sortie de `java -version`.
