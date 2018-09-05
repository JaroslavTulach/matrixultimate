package org.apidesign.demo.matrixultimate;

final class FindBiggestSquare<Matrix> extends AbstractBiggestSquare<Matrix> {
    private final GreatScientificLibrary<Matrix> gsl;

    FindBiggestSquare(GreatScientificLibrary<Matrix> gsl) {
        this.gsl = gsl;
    }

    @Override
    protected Matrix fromRaw(long matrixPtr) {
        return gsl.fromRaw(matrixPtr);
    }

    @Override
    protected void free(Matrix m) {
        gsl.free(m);
    }

    @Override
    protected Matrix create(long size1, long size2) {
        return gsl.create(size1, size2);
    }

    @Override
    protected double get(Matrix matrix, long i, long j) {
        return gsl.get(matrix, i, j);
    }

    @Override
    protected void set(Matrix sizes, long i, long j, double d) {
        gsl.set(sizes, i, j, d);
    }

    @Override
    protected long getSize1(Matrix matrix) {
        return gsl.getSize1(matrix);
    }

    @Override
    protected long getSize2(Matrix matrix) {
        return gsl.getSize2(matrix);
    }
}
