package org.apidesign.demo.matrixultimate;

public interface GreatScientificLibrary<Matrix> {
    Matrix create(long size1, long size2);
    void free(Matrix matrix);
    long toRaw(Matrix m);
    Matrix fromRaw(long m);

    double get(Matrix matrix, long i, long j);
    void set(Matrix matrix, long i, long j, double v);

    long getSize1(Matrix matrix);
    long getSize2(Matrix matrix);
}
