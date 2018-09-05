package org.apidesign.demo.matrixultimate;

public abstract class AbstractBiggestSquare<Matrix> implements MatrixSearch {
    @Override
    public Result search(long matrixPtr) {
        Matrix matrix = fromRaw(matrixPtr);

        long stamp = System.currentTimeMillis();

        final long size1 = getSize1(matrix);
        final long size2 = getSize2(matrix);
        Matrix sizes = create(size1, size2);

        long max = 0;
        long row = -1;
        long column = -1;

        for (long i = size1 - 1; i >= 0; i--) {
            for (long j = size2 - 1; j >= 0; j--) {
                double v00 = get(matrix, i, j);
                double v01 = i == size1 - 1 ? -1 : get(matrix, i + 1, j);
                double v10 = j == size2 - 1 ? -1 : get(matrix, i, j + 1);
                double v11 = i == size1 - 1 || j == size2 - 1 ? -1 : get(matrix, i + 1, j + 1);

                if (v00 == v01 && v10 == v11 && v00 == v11) {
                    double s10 = get(sizes, i + 1, j);
                    double s01 = get(sizes, i, j + 1);
                    double s11 = get(sizes, i + 1, j + 1);

                    double min = s10;
                    if (min > s01) {
                        min = s01;
                    }
                    if (min > s11) {
                        min = s11;
                    }

                    set(sizes, i, j, min + 1);
                    if (max <= min) {
                        row = i;
                        column = j;
                        max = (long) min + 1;
                    }
                } else {
                    set(sizes, i, j, 1.0);
                }
            }
        }
        free(sizes);

        long took = System.currentTimeMillis() - stamp;
        return new Result(row, column, max, took);
    }

    protected abstract Matrix fromRaw(long matrixPtr);
    protected abstract void free(Matrix m);
    protected abstract Matrix create(long size1, long size2);
    protected abstract double get(Matrix matrix, long i, long j);
    protected abstract void set(Matrix sizes, long i, long j, double d);
    protected abstract long getSize1(Matrix matrix);
    protected abstract long getSize2(Matrix matrix);

}
