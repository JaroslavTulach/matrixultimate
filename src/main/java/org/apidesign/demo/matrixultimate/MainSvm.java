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
        Matrix matrix = gsl.create(size, size);
        FillRandomly<Matrix> fill = new FillRandomly<>(gsl, matrix);
        fill.run();

        MatrixSearch.Result res = SVMBiggestSquare.compute(gsl.toRaw(matrix));

        System.out.println("Found square of size " + res.getSize() + " at " + res.getRow() + ":" + res.getColumn());
        Dump<Matrix> dump = new Dump<>(gsl, matrix, res.getRow(), res.getColumn(), res.getSize());
        dump.run();

        System.out.println("Took " + res.getMilliseconds() + " ms");
        gsl.free(matrix);;
    }
}
