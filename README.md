# Matrix the Ultimate

This is a sample project that demonstrates alternative approaches to access a **C** language data structure from **Java** with
the help of OracleLabs [GraalVM](http://graalvm.org). The lessons learned in this project are applicable for everyone who
has a **C** data and needs fast and effective access to them from the **JVM**.

[![Build Status](https://github.com/JaroslavTulach/matrixultimate/actions/workflows/maven.yml/badge.svg)](https://github.com/JaroslavTulach/matrixultimate/actions/workflows/maven.yml)

## The Plot

There is a [GNU Scientific Library](https://www.gnu.org/software/gsl/doc/html/)
and among its various mathematical operations it also provides access to matrix computation.
Imagine we want to use this library in our **Java** application to perform non-trivial matrix operations, yet we also want to
write our own computation in **Java** that manipulates the members of a matrix. How can we do it?

### Installation

The first step is to install the `gsl` library and its necessary header files. Use:
```bash
$ brew install gsl # on Mac
$ apt install libgsl-dev # on Ubuntu
```

We also need **GraalVM** - download it from http://graalvm.org - the
GraalVM version **22.3.1** is known to work - however so did **19.0.x** and all
the versions in between - it is very likely any version of **GraalVM** is going
to work.

### The Computation

The goal of this example is to write a *single* algorithm in Java and use it
in different setups. As such let's encapsulate the algorithm into its own class
[FindBiggestSquare](src/main/java/org/apidesign/demo/matrixultimate/FindBiggestSquare.java) and let it accept
an implementation of [GreatScientificLibrary](src/main/java/org/apidesign/demo/matrixultimate/GreatScientificLibrary.java) 
as an argument during construction. The `GreatScientificLibrary` interface is an abstraction over the operations 
one can do with a matrix without specifying the actual `matrix` implementation. This is an application of a pattern called
[Singletonizer](http://wiki.apidesign.org/wiki/Singletonizer) - a great tool for representing abstract data types in
object oriented languages. Operations are known, yet the unknown (e.g. algebraic) type remains represented as a
generic type parameter `Matrix`.

```java
public interface GreatScientificLibrary<Matrix> {
    Matrix create(long size1, long size2);
    void free(Matrix matrix);
    long toRaw(Matrix m);
    Matrix fromRaw(long m);

    double get(Matrix matrix, long i, long j);
    void set(Matrix matrix, long i, long j, double v);

    long getSize1(Matrix matrix);
    long getSize2(Matrix matrix);
}
```

There will be multiple different implementations of the `GreatScientificLibrary` interface. All of them will use different
access to the underlaying `C` library and its matrix data structure. The `C` structure is going to be allocated 
somewhere outside of **Java** heap, in an unmanaged memory, referenced by a pointer. The best type to represent
possibly 32-bits or 64-bits pointer in **Java** is `long` - hence the conversion methods `toRaw` and `fromRaw` that allow us to
interchange the allocated matrix between different implementations of the `GreatScientificLibrary` abstractions.

The algorithm itself is supposed to compute the biggest square of numbers in the matrix that is filled with the same value and return its size and location:
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
As can be seen there is a lot of calls to `get` a value at particular row and column. Depending on the size of the `matrix`, this can be very time consuming.

### The Boundary

The biggest problem when making a native call from **Java** via **JNI** is the context switch. The **JVM** has no idea
what kind of wild things the `C` code can do and as such it cleans up all its state (registers, interrupts, etc.) before
handling the control to the `C` code. Once the native call returns, the **JVM** needs to resume its state back before
continuing. It is needless to mention that this is very costly and disables any inlining or other optimizations.
Especially if there is a a lot of boundary crossings, like in the above algorithm, the speed is not going to be great.

One option is to give up and rewrite the `FindBiggestSquare` algorithm in `C`. Then there would be just a single boundary
switch (when the `findBiggestSquare` function is called) and everything would be reasonably fast. However, that isn't
what we want to do. We have the algorithm in **Java** and we don't want to rewrite it. What are our options?

## JNA

[JNA](https://github.com/java-native-access/jna/blob/master/README.md) is the standard solution for accessing `C` data
structures from **Java** without writing a single line of `C` code. Let's implement the `GreatScientificLibrary` with
**JNA**. Let's create [JNAScientificLibrary](src/main/java/org/apidesign/demo/matrixultimate/jna/JNAScientificLibrary.java).

The created interface looks nice. The library is using `GslMatrix` wrapper around each `matrix` structure and
just delegates the algebraic type operations to the `native` methods like `gsl_matrix_alloc` that are (thanks to **JNA**)
connected to the actual `C` functions.

Nice, but the overhead of *boundary crossing* is huge. It takes more than three seconds to compute the result for 
matrix 512x512 and that is too slow. In case you are interested, download [GraalVM](http://graalvm.org) and execute:
```bash
MatrixUltimate$ JAVA_HOME=/pathto/graalvm mvn process-classes exec:exec@run-test
```
You'll see the JNA access being the slowest one from all the performed ones (that will be described later).

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
MatrixUltimate$ ls -hl target/matrixultimate
5M target/matrixultimate
```
Wow, **26ms** instead of **3s** in case of `JNA`. That is way faster. A standalone native executable `target/matrixultimate`
is generated. It takes a single parameter - the size of the matrix - then it perform the computation and prints out time
statistics.

If you can compile your whole **Java** application with `native-image`, then access to your `C` data
structures is going to be amazingly fast!

### Access to `C` Structures with Native Image

Remember that our `FindBiggestSquare` algorithm needs implementation of the `GreatScientificLibrary` interface? In
order to access the `C` structures from `native-image` tool, we need such implementation as well. Let's call it
[RawScientificLibrary](src/main/java/org/apidesign/demo/matrixultimate/svm/RawScientificLibrary.java). It uses
API from `graal-sdk` library to describe the layout of the `C` structures and functions, so they can be accessed
from the `@Override` methods of regular **Java**:

```java
import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;

@CContext(RawScientificLibrary.GslDirectives.class)
public final class RawScientificLibrary implements GreatScientificLibrary<Long> {
    @CStruct("gsl_matrix")
    static interface GslMatrix extends PointerBase {
        @CField long size1();
        @CField long size2();
    }

    @CFunction
    static native GslMatrix gsl_matrix_alloc(long size1, long size2);
    @CFunction
    static native void gsl_matrix_free(GslMatrix m);
    @CFunction
    static native double gsl_matrix_get(GslMatrix p, long r, long c);
    @CFunction
    static native void gsl_matrix_set(GslMatrix p, long r, long c, double v);
```

The above part defines elements of the `gsl_matrix` C structure provided by the GNU Scientific Library and 
names and parameters of functions to manipulate the matrix structures. To help `native-image` tool to compile
the code, we need to provide locations of appropriate header files and libraries. That is done by following code:

```java
    public static final class GslDirectives implements CContext.Directives {
        @Override
        public List<String> getHeaderFiles() {
            return Arrays.asList("<gsl/gsl_matrix.h>");
        }

        @Override
        public List<String> getLibraries() {
            return Arrays.asList("gsl", "gslcblas");
        }
    }
```

The rest of the class just implements the `GreatScientificLibrary` interface by converting a pointer represented by
raw `Long` value into appropriate `GslMatrix` pointer and delegating to one of the `C` functions:

```java
    @Override
    public Long create(long size1, long size2) {
        return gsl_matrix_alloc(size1, size2).rawValue();
    }

    @Override
    public void free(Long matrix) {
        gsl_matrix_free(WordFactory.pointer(matrix));
    }

    @Override
    public long toRaw(Long m) {
        return m;
    }

    @Override
    public Long fromRaw(long m) {
        return m;
    }

    @Override
    public double get(Long matrix, long i, long j) {
        return gsl_matrix_get(WordFactory.pointer(matrix), i, j);
    }

    @Override
    public void set(Long matrix, long i, long j, double v) {
        gsl_matrix_set(WordFactory.pointer(matrix), i, j, v);
    }

    @Override
    public long getSize1(Long m) {
        GslMatrix matrix = WordFactory.pointer(m);
        return matrix.size1();
    }

    @Override
    public long getSize2(Long m) {
        GslMatrix matrix = WordFactory.pointer(m);
        return matrix.size2();
    }
}
```
If we want to run the whole computation in native code, we need to obtain instance of the `RawScientificLibrary`,
create matrix of requested size, fill it with random values and pass it into the
constructor of `FindBiggestSquare` algorithm. The *whole computation* will run in
*native mode*. Which is is exactly what the sample class
[Main](src/main/java/org/apidesign/demo/matrixultimate/Main.java) does.

But what if we cannot convert/compile whole our **Java** application to native?

