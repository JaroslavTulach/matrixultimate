package org.apidesign.demo.matrixultimate;

import java.util.Random;

final class FillRandomly<Matrix> implements Runnable {
    private final GreatScientificLibrary<Matrix> gsl;
    private final Matrix matrix;

    FillRandomly(GreatScientificLibrary<Matrix> matrix, Matrix m) {
        this.gsl = matrix;
        this.matrix = m;
    }

    @Override
    public void run() {
        final long size1 = gsl.getSize1(matrix);
        final long size2 = gsl.getSize2(matrix);

        Random r = new Random();
        for (long i = 0; i < size1; i++) {
            for (long j = 0; j < size2; j++) {
                double u = r.nextDouble();
                double zeroOrOne = Math.floor(u * 2.0);
                gsl.set(matrix, i, j, zeroOrOne);
            }
        }
    }
}
