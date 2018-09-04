package org.apidesign.demo.matrixultimate;

import org.apidesign.demo.matrixultimate.jna.JNAScientificLibrary;

final class Main {
    static <Matrix> void compute(GreatScientificLibrary<Matrix> gsl, int size) {
        Matrix matrix = gsl.create(size, size);
        final FillRandomly<Matrix> fill = new FillRandomly<>(gsl, matrix);
        fill.run();
        final FindBiggestSquare<Matrix> find = new FindBiggestSquare<>(gsl, matrix);
        find.run();
        Dump dump = new Dump(gsl, matrix, find.getRow(), find.getColumn(), find.getSize());
        dump.run();
        gsl.free(matrix);
    }

    public static void main(String... args) throws Exception {
        GreatScientificLibrary gsl = new JNAScientificLibrary();
        compute(gsl, 50);
    }

}
