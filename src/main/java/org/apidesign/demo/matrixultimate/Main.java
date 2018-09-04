package org.apidesign.demo.matrixultimate;

import org.apidesign.demo.matrixultimate.jna.JNAScientificLibrary;
import org.apidesign.demo.matrixultimate.svm.SVMScientificLibrary;

final class Main {
    private static void measure(String msgFormat, Runnable r) {
        long stamp = System.currentTimeMillis();
        r.run();
        long took = System.currentTimeMillis() - stamp;
        System.out.println(String.format(msgFormat, took));
    }

    static <Matrix> void compute(GreatScientificLibrary<Matrix> gsl, boolean show, int size) {
        Matrix matrix = gsl.create(size, size);
        final FillRandomly<Matrix> fill = new FillRandomly<>(gsl, matrix);
        fill.run();
        final FindBiggestSquare<Matrix> find = new FindBiggestSquare<>(gsl, matrix);
        measure(gsl.getClass().getSimpleName() + " at size " + size + " took %d ms", find);
        if (show) {
            Dump dump = new Dump(gsl, matrix, find.getRow(), find.getColumn(), find.getSize());
            dump.run();
        }
        gsl.free(matrix);
    }

    public static void main(String... args) throws Exception {
        GreatScientificLibrary gsl1 = new JNAScientificLibrary();
        SVMScientificLibrary gsl2 = new SVMScientificLibrary();

        for (int i = 1; i <= 10; i++) {
            compute(gsl1, false, 50 * i);
            compute(gsl2, false, 50 * i);
        }
    }

}
