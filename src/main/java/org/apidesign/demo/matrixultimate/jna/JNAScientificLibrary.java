package org.apidesign.demo.matrixultimate.jna;

import com.sun.jna.Native;
import com.sun.jna.Structure;
import java.util.Arrays;
import java.util.List;
import org.apidesign.demo.matrixultimate.GreatScientificLibrary;

public class JNAScientificLibrary implements GreatScientificLibrary<JNAScientificLibrary.GslMatrix> {
    static {
        Native.register("gsl");
    }

    private static native GslMatrix gsl_matrix_alloc(long size1, long size2);
    private static native void gsl_matrix_free(GslMatrix m);
    private static native double gsl_matrix_get(GslMatrix p, long r, long c);
    private static native void gsl_matrix_set(GslMatrix p, long r, long c, double v);

    @Override
    public GslMatrix create(long size1, long size2) {
        return gsl_matrix_alloc(size1, size2);
    }

    @Override
    public void free(GslMatrix matrix) {
        gsl_matrix_free(matrix);
    }

    @Override
    public double get(GslMatrix matrix, long i, long j) {
        return gsl_matrix_get(matrix, i, j);
    }

    @Override
    public void set(GslMatrix matrix, long i, long j, double v) {
        gsl_matrix_set(matrix, i, j, v);
    }

    @Override
    public long getSize1(GslMatrix matrix) {
        return (long) matrix.size1;
    }

    @Override
    public long getSize2(GslMatrix matrix) {
        return (long) matrix.size2;
    }

    public static final class GslMatrix extends Structure implements Structure.ByReference {
        public long size1;
        public long size2;

        @Override
        protected List<String> getFieldOrder() {
            return Arrays.asList("size1", "size2");
        }
    }
}
