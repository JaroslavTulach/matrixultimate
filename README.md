# Matrix the Ultimate

This is a sample project that demonstrates alternative approaches to access a **C** language data structure from **Java** with
the help of OracleLabs [GraalVM](http://graalvm.org). The lessons learned in this project are applicable for everyone who
has a **C** data and needs fast and effective access to them from the **JVM**.

## The Plot

There is a GNU Scientific Library and among its various mathematical operations it also provides access to matrix computation.
Imagine we want to use this library in our **Java** application to perform non-trivial matrix operations, yet we also want to
write our own computation in **Java** that manipulates the members of a matrix. How can we do it?

### The Computation

The goal is to write a *single* algorithm in Java and use it in different setups. To achieve that we use generics to abstract
away an algebraic type, but have some operations to work on it. As such let's encapsulate the class into
[FindBiggestSquare](src/main/java/org/apidesign/demo/matrixultimate/FindBiggestSquare.java) and let it accept
an implementation of [GreatScientificLibrary](src/main/java/org/apidesign/demo/matrixultimate/GreatScientificLibrary.java)
- the abstraction over the operations one can do on matrix. Btw. this is an application of a pattern called
[Singletonizer](http://wiki.apidesign.org/wiki/Singletonizer), something I like a lot when providing access to multiple
different implementation over an unknown (e.g. algebraic) type - in this case represented as generic parameter `Matrix`.

There will be multiple different implementations of the `GreatScientificLibrary` interface. All of them will use different
access to the underlaying `C` library and its matrix data structure. The structure is going to be allocated somewhere and such
address is best represented by Java `long` type - hence the conversion methods `toRaw` and `fromRaw` that allow us to
interchange the allocated matrix between different implementations of the `GreatScientificLibrary` abstractions.

The algorithm itself is supposed to compute the biggest square in the matrix with the same value and return its size and location:
```java

        final long size1 = gsl.getSize1(matrix);
        final long size2 = gsl.getSize2(matrix);
        Matrix sizes = gsl.create(size1, size2);

        long max = 0;
        long row = -1;
        long column = -1;

        for (long i = size1 - 1; i >= 0; i--) {
            for (long j = size2 - 1; j >= 0; j--) {
                double v00 = gsl.get(matrix, i, j);
                double v01 = i == size1 - 1 ? -1 : gsl.get(matrix, i + 1, j);
                double v10 = j == size2 - 1 ? -1 : gsl.get(matrix, i, j + 1);
                double v11 = i == size1 - 1 || j == size2 - 1 ? -1 : gsl.get(matrix, i + 1, j + 1);

                if (v00 == v01 && v10 == v11 && v00 == v11) {
                    double s10 = gsl.get(sizes, i + 1, j);
                    double s01 = gsl.get(sizes, i, j + 1);
                    double s11 = gsl.get(sizes, i + 1, j + 1);

                    double min = s10;
                    if (min > s01) {
                        min = s01;
                    }
                    if (min > s11) {
                        min = s11;
                    }

                    gsl.set(sizes, i, j, min + 1);
                    if (max <= min) {
                        row = i;
                        column = j;
                        max = (long) min + 1;
                    }
                } else {
                    gsl.set(sizes, i, j, 1.0);
                }
            }
        }
        gsl.free(sizes);
```
As can be seen there is a lot of calls to `get` a value. Depending on the size of the `matrix`, this can be very time consuming.

### The Boundary

The biggest problem when going from **Java** to native call via **JNI** is the context switch. The **JVM** has no idea
what kind of wild things the `C` code can do and as such it cleans up all its state (registers, interrupts, etc.) before
handling the control to the `C` code. Once the native call returns, the **JVM** needs to resume its state back before
continuing. It is needless to mention that this is very costly. Especially if there is a a lot of boundary crossings, 
like in the above algorithm, the speed is not going to be great.

One option is to give up and rewrite the `FindBiggestSquare` algorithm in `C`. Then there would be just a single boundary
switch (when the `findBiggestSquare` function is called) and everything would be reasonably fast. However, that isn't
what we want to do. We have the algorithm in **Java** and don't want to rewrite it. What are our options?

## JNA

[JNA](https://github.com/java-native-access/jna/blob/master/README.md) is the standard solution for accessing `C` data
structures from **Java** without writing a single line of `C` code. Let's implement the `GreatScientificLibrary` with
**JNA**. Let's create [JNAScientificLibrary](master/src/main/java/org/apidesign/demo/matrixultimate/jna/JNAScientificLibrary.java)

The created interface looks nice. The library is using `GslMatrix` wrapper around each `matrix` data structure and
just delegates the algebraic type operations to the `native` methods like `gsl_matrix_alloc` that are (thanks to **JNA**)
connected to the actual `C` functions.

Nice, but the overhead of *boundary crossing* is huge. It takes more than three seconds to compute the result for 
matrix 512x512 and that is too slow. In case you are interested, download [GraalVM](http://graalvm.org) at least version RC6
and execute:
```bash
MatrixUltimate$ JAVA_HOME=/pathto/graalvm mvn process-classes exec:exec@run-test
```
You'll see the JNA access being the slowest one from all the used ones that will be described later.

## Native Image

It is well known that [GraalVM](http://graalvm.org) contains tool `native-image` that can compile **Java** code into
native one. `native-image` comes with sophisticated `C` interface that allows access to the `C` data structures without 
any overhead. Can we use it? Sure we can:
```bash
MatrixUltimate$ JAVA_HOME=$HOME/bin/graalvm mvn compile exec:exec@build-standalone
MatrixUltimate$ mvn exec:exec@run-standalone -Dexec.args=512
Took 26 ms
MatrixUltimate$ mvn exec:exec@run-standalone -Dexec.args=8192
Took 4650 ms
```
Wow, **26ms** instead of **3s** in case of `JNA`. That is way faster. 
If you can compile your whole **Java** application with `native-image`, then access to your `C` data
structures is going to be amazingly fast!

### Access to `C` Structures with Native Image

Remember that our `FindBiggestSquare` algorithm needs implementation of the `GreatScientificLibrary` interface? In
order to access the `C` structures from `native-image` tool, we need such implementation as well. Let's call it
[RawScientificLibrary](src/main/java/org/apidesign/demo/matrixultimate/svm/RawScientificLibrary.java):




