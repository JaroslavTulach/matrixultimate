package org.apidesign.demo.matrixultimate;

import org.apidesign.demo.matrixultimate.jna.JNAScientificLibrary;
import org.apidesign.demo.matrixultimate.svm.SVMBiggestSquare;
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

    public static void main(String... args) throws Exception {
        GreatScientificLibrary gslJna = new JNAScientificLibrary();
        SVMScientificLibrary gslSvm = new SVMScientificLibrary();

        MatrixSearch searchJna = MatrixSearch.findBiggestSquare(gslJna);
        MatrixSearch searchSvm = MatrixSearch.findBiggestSquare(gslSvm);

        long size = 16;
        for (int i = 1; i <= 10; i++) {
            long ptrMatrix = allocRawMatrix(gslJna, size);

            MatrixSearch.Result resSvm = searchSvm.search(ptrMatrix);
            MatrixSearch.Result resJna = searchJna.search(ptrMatrix);

            if (resSvm.hashCode()!= resJna.hashCode()) {
                System.err.println("Different results!");
                dumpRawMatrix(gslJna, ptrMatrix, resJna);
                dumpRawMatrix(gslSvm, ptrMatrix, resSvm);
            }

            System.err.printf("Searching size %d took %d ms with JNA and %d ms with SVM\n", size, resJna.getMilliseconds(), resSvm.getMilliseconds());
            long hashStamp = SVMBiggestSquare.compute(ptrMatrix);
            if (resSvm.hashCode() != hashStamp) {
                System.err.println("Different result with SVM!");
            }

            freeRawMatrix(gslSvm, ptrMatrix);


            size *= 2;
        }
    }

    private static <Matrix> void dumpRawMatrix(GreatScientificLibrary<Matrix> gsl, long ptr, MatrixSearch.Result l) {
        Matrix matrix = gsl.fromRaw(ptr);
        Dump<Matrix> dump = new Dump<>(gsl, matrix, l.getRow(), l.getColumn(), l.getSize());
        dump.run();
    }
}
