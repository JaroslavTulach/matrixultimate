package org.apidesign.demo.matrixultimate.svm;

import org.apidesign.demo.matrixultimate.MatrixSearch;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.word.Pointer;

final class SVMScientificLibraryJNI {
    private SVMScientificLibraryJNI() {
    }

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMIsolate_svmInit", builtin = CEntryPoint.Builtin.CreateIsolate)
    public static native long svmInit();

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_create0")
    public static RawScientificLibrary.GslMatrix create0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, long size1, long size2) {
        return RawScientificLibrary.gsl_matrix_alloc(size1, size2);
    }

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_free0")
    public static void free0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, RawScientificLibrary.GslMatrix ptr) {
        RawScientificLibrary.gsl_matrix_free(ptr);
    }

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_get0")
    public static double get0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, RawScientificLibrary.GslMatrix ptr, long r, long c) {
        return RawScientificLibrary.gsl_matrix_get(ptr, r, c);
    }

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_set0")
    public static void set0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, RawScientificLibrary.GslMatrix ptr, long r, long c, double v) {
        RawScientificLibrary.gsl_matrix_set(ptr, r, c, v);
    }

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_size0")
    public static long size0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, RawScientificLibrary.GslMatrix ptr, int type) {
        switch (type) {
            case 1: return ptr.size1();
            case 2: return ptr.size2();
            default: throw new IllegalStateException();
        }
    }

    private static final MatrixSearch FIND_BIGGEST_SQUARE = MatrixSearch.findBiggestSquare(RawScientificLibrary.getDefault());

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMBiggestSquare_directlyComputeViaSvm")
    public static long directlyComputeViaSvm(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, RawScientificLibrary.GslMatrix ptr) {
        MatrixSearch.Result result = FIND_BIGGEST_SQUARE.search(ptr.rawValue());
        System.err.printf("  ditto via native code took %d ms\n", result.getMilliseconds());
        return result.hashCode();
    }

}
