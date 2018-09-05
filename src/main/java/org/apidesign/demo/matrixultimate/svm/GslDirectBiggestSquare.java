package org.apidesign.demo.matrixultimate.svm;

import org.apidesign.demo.matrixultimate.AbstractBiggestSquare;
import org.graalvm.word.WordFactory;

final class GslDirectBiggestSquare extends AbstractBiggestSquare<GslDirect.GslMatrix> {

    @Override
    protected GslDirect.GslMatrix fromRaw(long matrixPtr) {
        return WordFactory.pointer(matrixPtr);
    }

    @Override
    protected void free(GslDirect.GslMatrix m) {
        GslDirect.gsl_matrix_free(m);
    }

    @Override
    protected GslDirect.GslMatrix create(long size1, long size2) {
        return GslDirect.gsl_matrix_alloc(size1, size2);
    }

    @Override
    protected double get(GslDirect.GslMatrix matrix, long i, long j) {
        return GslDirect.gsl_matrix_get(matrix, i, j);
    }

    @Override
    protected void set(GslDirect.GslMatrix sizes, long i, long j, double d) {
        GslDirect.gsl_matrix_set(sizes, i, j, d);
    }

    @Override
    protected long getSize1(GslDirect.GslMatrix matrix) {
        return matrix.size1();
    }

    @Override
    protected long getSize2(GslDirect.GslMatrix matrix) {
        return matrix.size2();
    }

}
