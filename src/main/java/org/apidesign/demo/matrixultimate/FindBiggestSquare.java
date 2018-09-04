package org.apidesign.demo.matrixultimate;

final class FindBiggestSquare<Matrix> implements Runnable {
    private final GreatScientificLibrary<Matrix> gsl;
    private final Matrix matrix;
    private long atI;
    private long atJ;
    private long size;
    private Long took;

    FindBiggestSquare(GreatScientificLibrary<Matrix> gsl, Matrix matrix) {
        this.gsl = gsl;
        this.matrix = matrix;
    }

    @Override
    public void run() {
        if (took == null) {
            long stamp = System.currentTimeMillis();
            compute();
            took = System.currentTimeMillis() - stamp;
        }
    }

    private void compute() {
        final long size1 = gsl.getSize1(matrix);
        final long size2 = gsl.getSize2(matrix);
        Matrix sizes = gsl.create(size1, size2);

        size = 0;
        for (long i = size1 - 1; i >= 0; i--) {
            for (long j = size2 - 1; j >= 0; j--) {
                double v00 = gsl.get(matrix, i, j);
                double v01 = i == size1 - 1 ? -1 : gsl.get(matrix, i + 1, j);
                double v10 = j == size2 - 1 ? -1 : gsl.get(matrix, i, j + 1);
                double v11 = i == size1 - 1 || j == size2 - 1 ? -1 : gsl.get(matrix, i + 1, j + 1);

                if (v00 == v01 && v10 == v11 && v00 == v11) {
                    double s10 = gsl.get(sizes, i + 1, j);
                    double s01 = gsl.get(sizes, i, j + 1);
                    double s11 = gsl.get(sizes, i + 1, j + 1);

                    double min = s10;
                    if (min > s01) {
                        min = s01;
                    }
                    if (min > s11) {
                        min = s11;
                    }

                    gsl.set(sizes, i, j, min + 1);
                    if (size <= min) {
                        atI = i;
                        atJ = j;
                        size = (long) min + 1;
                    }
                } else {
                    gsl.set(sizes, i, j, 1.0);
                }
            }
        }

        gsl.free(sizes);
    }

    public long getRow() {
        return atI;
    }

    public long getColumn() {
        return atJ;
    }

    public long getSize() {
        return size;
    }

    public long getMilliseconds() {
        return took;
    }
}
