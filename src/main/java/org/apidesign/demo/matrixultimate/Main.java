package org.apidesign.demo.matrixultimate;

import org.apidesign.demo.matrixultimate.jna.JNAScientificLibrary;
import org.apidesign.demo.matrixultimate.svm.SVMScientificLibrary;

final class Main {
    static <Matrix> long allocRawMatrix(GreatScientificLibrary<Matrix> gsl, long size) {
        Matrix matrix = gsl.create(size, size);
        FillRandomly<Matrix> fill = new FillRandomly<>(gsl, matrix);
        fill.run();
        return gsl.toRaw(matrix);
    }

    static <Matrix> void freeRawMatrix(GreatScientificLibrary<Matrix> gsl, long ptr) {
        Matrix matrix = gsl.fromRaw(ptr);
        gsl.free(matrix);
    }

    static <Matrix> FindBiggestSquare<Matrix> compute(GreatScientificLibrary<Matrix> gsl, long rawMatrix) {
        Matrix matrix = gsl.fromRaw(rawMatrix);
        final FindBiggestSquare<Matrix> find = new FindBiggestSquare<>(gsl, matrix);
        find.run();
        return find;
    }

    public static void main(String... args) throws Exception {
        GreatScientificLibrary gslJna = new JNAScientificLibrary();
        SVMScientificLibrary gslSvm = new SVMScientificLibrary();

        long size = 16;
        for (int i = 1; i <= 10; i++) {
            long ptrMatrix = allocRawMatrix(gslJna, size);

            FindBiggestSquare<Long> findSvm = compute(gslSvm, ptrMatrix);
            FindBiggestSquare findJna = compute(gslJna, ptrMatrix);

            if (
                findSvm.getColumn() != findJna.getColumn() ||
                findSvm.getRow() != findJna.getRow() ||
                findSvm.getSize() != findJna.getSize()
            ) {
                System.err.println("different results! Jna: ");
                dumpRawMatrix(gslJna, ptrMatrix, findJna);
                dumpRawMatrix(gslSvm, ptrMatrix, findSvm);
            }

            freeRawMatrix(gslSvm, ptrMatrix);

            System.err.printf("Searching size %d took %d ms with JNA and %d ms with SVM\n", size, findJna.getMilliseconds(), findSvm.getMilliseconds());

            size *= 2;
        }
    }

    private static <Matrix> void dumpRawMatrix(GreatScientificLibrary<Matrix> gsl, long ptr, FindBiggestSquare l) {
        Matrix matrix = gsl.fromRaw(ptr);
        Dump<Matrix> dump = new Dump<>(gsl, matrix, l.getRow(), l.getColumn(), l.getSize());
        dump.run();
    }
}
