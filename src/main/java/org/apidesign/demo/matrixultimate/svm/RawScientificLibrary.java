package org.apidesign.demo.matrixultimate.svm;

import java.util.Arrays;
import java.util.List;
import org.apidesign.demo.matrixultimate.GreatScientificLibrary;
import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;

@CContext(RawScientificLibrary.GslDirectives.class)
public final class RawScientificLibrary implements GreatScientificLibrary<Long> {
    @CStruct("gsl_matrix")
    static interface GslMatrix extends PointerBase {
        @CField long size1();
        @CField long size2();
    }

    private static final RawScientificLibrary GSL = new RawScientificLibrary();

    private RawScientificLibrary() {
    }

    public static GreatScientificLibrary<?> getDefault() {
        return GSL;
    }

    @CFunction
    static native GslMatrix gsl_matrix_alloc(long size1, long size2);
    @CFunction
    static native void gsl_matrix_free(GslMatrix m);
    @CFunction
    static native double gsl_matrix_get(GslMatrix p, long r, long c);
    @CFunction
    static native void gsl_matrix_set(GslMatrix p, long r, long c, double v);


    @Override
    public Long create(long size1, long size2) {
        return gsl_matrix_alloc(size1, size2).rawValue();
    }

    @Override
    public void free(Long matrix) {
        gsl_matrix_free(WordFactory.pointer(matrix));
    }

    @Override
    public long toRaw(Long m) {
        return m;
    }

    @Override
    public Long fromRaw(long m) {
        return m;
    }

    @Override
    public double get(Long matrix, long i, long j) {
        return gsl_matrix_get(WordFactory.pointer(matrix), i, j);
    }

    @Override
    public void set(Long matrix, long i, long j, double v) {
        gsl_matrix_set(WordFactory.pointer(matrix), i, j, v);
    }

    @Override
    public long getSize1(Long m) {
        GslMatrix matrix = WordFactory.pointer(m);
        return matrix.size1();
    }

    @Override
    public long getSize2(Long m) {
        GslMatrix matrix = WordFactory.pointer(m);
        return matrix.size2();
    }

    public static final class GslDirectives implements CContext.Directives {

        @Override
        public List<String> getHeaderFiles() {
            return Arrays.asList("<gsl/gsl_matrix.h>");
        }

        @Override
        public List<String> getLibraries() {
            return Arrays.asList("gsl", "gslcblas");
        }

    }
}
