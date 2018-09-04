package org.apidesign.demo.matrixultimate;

final class FindBiggestSquare<Matrix> implements MatrixSearch {
    private final GreatScientificLibrary<Matrix> gsl;

    FindBiggestSquare(GreatScientificLibrary<Matrix> gsl) {
        this.gsl = gsl;
    }

    @Override
    public Result search(long matrixPtr) {
        Matrix matrix = gsl.fromRaw(matrixPtr);

        long stamp = System.currentTimeMillis();

        final long size1 = gsl.getSize1(matrix);
        final long size2 = gsl.getSize2(matrix);
        Matrix sizes = gsl.create(size1, size2);

        long max = 0;
        long row = -1;
        long column = -1;

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
                    if (max <= min) {
                        row = i;
                        column = j;
                        max = (long) min + 1;
                    }
                } else {
                    gsl.set(sizes, i, j, 1.0);
                }
            }
        }
        gsl.free(sizes);

        long took = System.currentTimeMillis() - stamp;
        return new Result(row, column, max, took);
    }
}
