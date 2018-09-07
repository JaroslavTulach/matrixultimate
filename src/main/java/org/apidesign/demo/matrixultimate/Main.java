package org.apidesign.demo.matrixultimate;

import org.apidesign.demo.matrixultimate.svm.SVMScientificLibrary;

final class Main {
    public static void main(String... args) throws Exception {
        int size;
        if (args.length == 1) {
            size = Integer.parseInt(args[0]);
        } else {
            throw new IllegalArgumentException("Usage: matrixultimate <size_of_the_matrix>");
        }
        runTest(SVMScientificLibrary.getDirect(), size);
    }

    private static <Matrix> void runTest(GreatScientificLibrary<Matrix> gsl, int size) {
        Matrix matrix = gsl.create(size, size);
        FillRandomly<Matrix> fill = new FillRandomly<>(gsl, matrix);
        fill.run();

        final MatrixSearch search = MatrixSearch.findBiggestSquare(gsl);
        MatrixSearch.Result res = search.search(gsl.toRaw(matrix));

        System.out.println("Found square of size " + res.getSize() + " at " + res.getRow() + ":" + res.getColumn());
        Dump<Matrix> dump = new Dump<>(gsl, matrix, res.getRow(), res.getColumn(), res.getSize());
        dump.run();

        System.out.println("Took " + res.getMilliseconds() + " ms");
        gsl.free(matrix);;
    }
}
