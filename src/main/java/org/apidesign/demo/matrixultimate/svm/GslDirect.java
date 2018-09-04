package org.apidesign.demo.matrixultimate.svm;

import org.graalvm.nativeimage.c.CContext;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.nativeimage.c.function.CFunction;
import org.graalvm.nativeimage.c.struct.CField;
import org.graalvm.nativeimage.c.struct.CStruct;
import org.graalvm.word.Pointer;
import org.graalvm.word.PointerBase;
import org.apidesign.demo.matrixultimate.MatrixSearch;
import org.graalvm.word.WordFactory;

@CContext(GslDirectives.class)
final class GslDirect {
    @CStruct("gsl_matrix")
    static interface GslMatrix extends PointerBase {
        @CField long size1();
        @CField long size2();
    }

    private static final MatrixSearch FIND_BIGGEST_SQUARE = new FindBiggestSquare();

    static final class FindBiggestSquare implements MatrixSearch {
        @Override
        public MatrixSearch.Result search(long matrixPtr) {
            GslMatrix matrix = WordFactory.pointer(matrixPtr);

            long stamp = System.currentTimeMillis();

            final long size1 = matrix.size1();
            final long size2 = matrix.size2();
            GslMatrix sizes = gsl_matrix_alloc(size1, size2);

            long max = 0;
            long row = -1;
            long column = -1;

            for (long i = size1 - 1; i >= 0; i--) {
                for (long j = size2 - 1; j >= 0; j--) {
                    double v00 = gsl_matrix_get(matrix, i, j);
                    double v01 = i == size1 - 1 ? -1 : gsl_matrix_get(matrix, i + 1, j);
                    double v10 = j == size2 - 1 ? -1 : gsl_matrix_get(matrix, i, j + 1);
                    double v11 = i == size1 - 1 || j == size2 - 1 ? -1 : gsl_matrix_get(matrix, i + 1, j + 1);

                    if (v00 == v01 && v10 == v11 && v00 == v11) {
                        double s10 = gsl_matrix_get(sizes, i + 1, j);
                        double s01 = gsl_matrix_get(sizes, i, j + 1);
                        double s11 = gsl_matrix_get(sizes, i + 1, j + 1);

                        double min = s10;
                        if (min > s01) {
                            min = s01;
                        }
                        if (min > s11) {
                            min = s11;
                        }

                        gsl_matrix_set(sizes, i, j, min + 1);
                        if (max <= min) {
                            row = i;
                            column = j;
                            max = (long) min + 1;
                        }
                    } else {
                        gsl_matrix_set(sizes, i, j, 1.0);
                    }
                }
            }
            gsl_matrix_free(sizes);

            long took = System.currentTimeMillis() - stamp;
            return new MatrixSearch.Result(row, column, max, took);
        }
    }

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

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMIsolate_svmInit", builtin = CEntryPoint.Builtin.CreateIsolate)
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
        MatrixSearch.Result result = FIND_BIGGEST_SQUARE.search(ptr.rawValue());
        System.err.printf("  ditto via native code took %d ms\n", result.getMilliseconds());
        return result.hashCode();
    }
}
