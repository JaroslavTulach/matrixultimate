package org.apidesign.demo.matrixultimate;

import org.apidesign.demo.matrixultimate.svm.SVMBiggestSquare;
import org.apidesign.demo.matrixultimate.svm.SVMScientificLibrary;

final class MainSvm {
    public static void main(String... args) throws Exception {
        SVMScientificLibrary gsl = new SVMScientificLibrary();
        int size;
        if (args.length == 1) {
            size = Integer.parseInt(args[0]);
        } else {
            throw new IllegalArgumentException("Usage: matrixultimate <size_of_the_matrix>");
        }
        runTest(gsl, size);
    }

    private static <Matrix> void runTest(GreatScientificLibrary<Matrix> gsl, int size) {
        Matrix matrix = Main.prepareTest(size, gsl);

        MatrixSearch.Result res = SVMBiggestSquare.compute(gsl.toRaw(matrix));

        Main.printTestResult(res, gsl, matrix);

        gsl.free(matrix);;
    }
}
