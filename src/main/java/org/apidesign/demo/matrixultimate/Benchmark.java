package org.apidesign.demo.matrixultimate;

import org.apidesign.demo.matrixultimate.jna.JNAScientificLibrary;
import org.apidesign.demo.matrixultimate.svm.SVMBiggestSquare;
import org.apidesign.demo.matrixultimate.svm.SVMScientificLibrary;

final class Benchmark {
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
            long ptrMatrix = allocRawMatrix(gslSvm, size);

            MatrixSearch.Result resJna = searchJna == null ? null : searchJna.search(ptrMatrix);
            MatrixSearch.Result resSvm = searchSvm.search(ptrMatrix);

            if (resJna != null && resSvm.hashCode()!= resJna.hashCode()) {
                System.err.println("Different results!");
                dumpRawMatrix(gslJna, ptrMatrix, resJna);
                dumpRawMatrix(gslSvm, ptrMatrix, resSvm);
            }

            MatrixSearch.Result resNative = SVMBiggestSquare.compute(ptrMatrix);
            if (resSvm.hashCode() != resNative.hashCode()) {
                System.err.println("Different result with SVM!");
            }

            String noteJna = "";
            if (resJna != null && resJna.getMilliseconds() > 3000) {
                noteJna = "JNA implementation disqualified!";
                searchJna = null;
            }
            System.err.printf(
                "Searching matrix with size %d took:\n"
                    + "  JNA                  : %s %s\n"
                    + "  JNI via Native Image : %s\n"
                    + "  Fully in Native Image: %s\n",
                size, toTime(resJna), noteJna, toTime(resSvm), toTime(resNative)
            );

            freeRawMatrix(gslSvm, ptrMatrix);


            size *= 2;
        }
    }

    private static String toTime(MatrixSearch.Result r) {
        if (r == null) {
            return "too long";
        }
        return r.getMilliseconds() + " ms";
    }

    private static <Matrix> void dumpRawMatrix(GreatScientificLibrary<Matrix> gsl, long ptr, MatrixSearch.Result l) {
        Matrix matrix = gsl.fromRaw(ptr);
        Dump<Matrix> dump = new Dump<>(gsl, matrix, l.getRow(), l.getColumn(), l.getSize());
        dump.run();
    }
}
