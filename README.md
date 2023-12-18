# Java Template

This is a Java 17 template with Maven and JUnit which can use the preview features of Java. Building happens with `make`. The project built process happens via the Maven wrapper which means that you do not need Maven on your
machine.

To use this template for your code, update the file `pom.xml` with your actual main class. If the class `JavaCollectionsTest` in the
package `it.ecubit.java.training.collections.test` contains the main method of your application, the maven file should include the following line.

```xml
<mainClass>it.ecubit.java.training.collections.test.JavaCollectionsTest</mainClass>
```

After running the command `make` in your home directory, Maven compiles a JAR of your application. To run this JAR via
the provided executable script `runMainClass`, change the name of the JAR in the script to the name of your compiled JAR. For
the JAR name `java-training-1.0-SNAPSHOT`, the script includes the following line.

```bash
exec java --enable-preview -jar target/java-training-1.0-SNAPSHOT.jar "$@"
```

You can also rename the executable script. If the executable should be called `runJava`, then run:

```bash
mv runMainClass runJava
```
