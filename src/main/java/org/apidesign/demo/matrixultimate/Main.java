package org.apidesign.demo.matrixultimate;

import org.apidesign.demo.matrixultimate.svm.RawScientificLibrary;

final class Main {
    public static void main(String... args) throws Exception {
        main(RawScientificLibrary.getDefault(), args);
    }

    static void main(GreatScientificLibrary<?> gsl, String... args) throws Exception {
        int size;
        if (args.length == 1) {
            size = Integer.parseInt(args[0]);
        } else {
            throw new IllegalArgumentException("Usage: matrixultimate <size_of_the_matrix>");
        }
        runTest(gsl, size);
    }

    static <Matrix> void runTest(GreatScientificLibrary<Matrix> gsl, int size) {
        Matrix matrix = prepareTest(size, gsl);

        final MatrixSearch search = MatrixSearch.findBiggestSquare(gsl);
        MatrixSearch.Result res = search.search(gsl.toRaw(matrix));

        printTestResult(res, gsl, matrix);

        gsl.free(matrix);;
    }

    static <Matrix> void printTestResult(MatrixSearch.Result res, GreatScientificLibrary<Matrix> gsl, Matrix matrix) {
        System.out.println("Found square of size " + res.getSize() + " at " + res.getRow() + ":" + res.getColumn());
        Dump<Matrix> dump = new Dump<>(gsl, matrix, res.getRow(), res.getColumn(), res.getSize());
        dump.run();
        System.out.println("Took " + res.getMilliseconds() + " ms");
    }

    static <Matrix> Matrix prepareTest(int size, GreatScientificLibrary<Matrix> gsl) {
        System.out.println("Generating matrix " + size + "x" + size);
        Matrix matrix = gsl.create(size, size);
        FillRandomly<Matrix> fill = new FillRandomly<>(gsl, matrix);
        fill.run();
        System.out.println("Searching matrix " + size + "x" + size);
        return matrix;
    }
}
