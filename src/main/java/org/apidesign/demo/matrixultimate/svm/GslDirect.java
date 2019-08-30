package org.apidesign.demo.matrixultimate.svm;

import org.apidesign.demo.matrixultimate.GreatScientificLibrary;
import org.apidesign.demo.matrixultimate.MatrixSearchResult;
import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.word.Pointer;
import org.graalvm.word.PointerBase;
import org.graalvm.word.WordFactory;

@CContext(GslDirectives.class)
final class GslDirect implements GreatScientificLibrary<Long> {
    static interface GslMatrix extends PointerBase {
        @CField long size1();
        @CField long size2();
    }

    private static final GslDirect GSL = new GslDirect();

    private GslDirect() {
    }

    @CFunction
    private static native GslMatrix gsl_matrix_alloc(long size1, long size2);
    @CFunction
    private static native void gsl_matrix_free(GslMatrix m);
    @CFunction
    private static native double gsl_matrix_get(GslMatrix p, long r, long c);
    @CFunction
    private static native void gsl_matrix_set(GslMatrix p, long r, long c, double v);

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMIsolate_svmInit", builtin = CEntryPoint.Builtin.CREATE_ISOLATE)
    public static native long svmInit();

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_create0")
    public static GslMatrix create0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, long size1, long size2) {
        return gsl_matrix_alloc(size1, size2);
    }

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_free0")
    public static void free0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, GslMatrix ptr) {
        gsl_matrix_free(ptr);
    }

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_get0")
    public static double get0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, GslMatrix ptr, long r, long c) {
        return gsl_matrix_get(ptr, r, c);
    }

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_set0")
    public static void set0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, GslMatrix ptr, long r, long c, double v) {
        gsl_matrix_set(ptr, r, c, v);
    }

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_size0")
    public static long size0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, GslMatrix ptr, int type) {
        switch (type) {
            case 1: return ptr.size1();
            case 2: return ptr.size2();
            default: throw new IllegalStateException();
        }
    }

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMBiggestSquare_directlyComputeViaSvm")
    public static long directlyComputeViaSvm(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, GslMatrix ptr) {
        MatrixSearchResult result = MatrixSearchResult.searchSquare(GSL, ptr.rawValue());
        System.err.printf("  ditto via native code took %d ms\n", result.getMilliseconds());
        return result.hashStamp();
    }

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
}
