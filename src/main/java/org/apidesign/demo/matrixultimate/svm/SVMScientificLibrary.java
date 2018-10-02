package org.apidesign.demo.matrixultimate.svm;

import org.apidesign.demo.matrixultimate.GreatScientificLibrary;
import static org.apidesign.demo.matrixultimate.svm.SVMIsolate.ID;

public final class SVMScientificLibrary implements GreatScientificLibrary<Long> {
    private static native long create0(long id, long size1, long size2);
    private static native void free0(long id, long ptr);
    private static native double get0(long id, long ptr, long r, long c);
    private static native void set0(long id, long ptr, long r, long c, double v);
    private static native long size0(long id, long ptr, int type);

    @Override
    public Long create(long size1, long size2) {
        return create0(ID, size1, size2);
    }

    @Override
    public void free(Long matrix) {
        free0(ID, matrix);
    }

    @Override
    public double get(Long matrix, long i, long j) {
        return get0(ID, matrix, i, j);
    }

    @Override
    public void set(Long matrix, long i, long j, double v) {
        set0(ID, matrix, i, j, v);
    }

    @Override
    public long getSize1(Long matrix) {
        return size0(ID, matrix, 1);
    }

    @Override
    public long getSize2(Long matrix) {
        return size0(ID, matrix, 2);
    }

    @Override
    public long toRaw(Long m) {
        return m;
    }

    @Override
    public Long fromRaw(long m) {
        return m;
    }
}
