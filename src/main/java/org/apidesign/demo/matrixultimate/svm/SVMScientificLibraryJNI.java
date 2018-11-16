package org.apidesign.demo.matrixultimate.svm;

import com.oracle.svm.core.c.function.CEntryPointActions;
import com.oracle.svm.core.c.function.CEntryPointOptions;
import org.apidesign.demo.matrixultimate.MatrixSearch;
import org.apidesign.demo.matrixultimate.svm.JNIEnv.JObject;
import org.graalvm.nativeimage.Isolate;
import org.graalvm.nativeimage.c.function.CEntryPoint;
import org.graalvm.word.Pointer;
import org.graalvm.word.WordFactory;

/** Native image implementation of JNI entry point methods. All the
 * {@link CEntryPoint} methods are on the "boundary" from the native side.
 * The code in here can interface with C libraries without any overhead.
 */
final class SVMScientificLibraryJNI {
    private SVMScientificLibraryJNI() {
    }

    /** Native image implementation of {@link SVMIsolate#svmInit()}
     * @return the {@link SVMIsolate#ID ID} of the native-image VM runtime
     */
    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMIsolate_svmInit", builtin = CEntryPoint.Builtin.CreateIsolate)
    public static native long svmInit();

    /** Native image implementation of {@link SVMScientificLibrary#create0} */
    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_create0")
    public static RawScientificLibrary.GslMatrix create0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, long size1, long size2) {
        return RawScientificLibrary.gsl_matrix_alloc(size1, size2);
    }

    /** Native image implementation of {@link SVMScientificLibrary#free0} */
    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_free0")
    public static void free0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, RawScientificLibrary.GslMatrix ptr) {
        RawScientificLibrary.gsl_matrix_free(ptr);
    }

    /** Native image implementation of {@link SVMScientificLibrary#get0} */
    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_get0")
    public static double get0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, RawScientificLibrary.GslMatrix ptr, long r, long c) {
        return RawScientificLibrary.gsl_matrix_get(ptr, r, c);
    }

    /** Native image implementation of {@link SVMScientificLibrary#set0} */
    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_set0")
    public static void set0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, RawScientificLibrary.GslMatrix ptr, long r, long c, double v) {
        RawScientificLibrary.gsl_matrix_set(ptr, r, c, v);
    }

    /** Native image implementation of {@link SVMScientificLibrary#size0} */
    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibrary_size0")
    public static long size0(Pointer jniEnv, Pointer clazz, @CEntryPoint.IsolateContext long isolateId, RawScientificLibrary.GslMatrix ptr, int type) {
        switch (type) {
            case 1: return ptr.size1();
            case 2: return ptr.size2();
            default: throw new IllegalStateException();
        }
    }

    private static final MatrixSearch FIND_BIGGEST_SQUARE = MatrixSearch.findBiggestSquare(RawScientificLibrary.getDefault());

    /**
     * Implementation of JNI native method.
     *
     * @see SVMBiggestSquare#directlyComputeViaSvm
     */
    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMBiggestSquare_directlyComputeViaSvm")
    public static JObject directlyComputeViaSvm(JNIEnv env, JNIEnv.JClass clazz, @CEntryPoint.IsolateContext long isolateId, RawScientificLibrary.GslMatrix ptr) {
        MatrixSearch.Result result = FIND_BIGGEST_SQUARE.search(ptr.rawValue());
        return convertSVMToJVM(env, result);
    }

   public static final class AttachThreadPrologue {
        static void enter(@CEntryPoint.IsolateContext long id) {
            Isolate isolate = WordFactory.pointer(id);
            int code = CEntryPointActions.enterAttachThread(isolate);
            if (code != 0) {
                CEntryPointActions.bailoutInPrologue();
            }
        }
    }

    @CEntryPointOptions(prologue = AttachThreadPrologue.class)
    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibraryJNI_ownJNIEnv")
    static long ownJNIEnvImpl(JNIEnv env, JNIEnv.JClass clazz, @CEntryPoint.IsolateContext long isolateId) {
        return env.rawValue();
    }
    private static native long ownJNIEnv(long isolateId);

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibraryJNI_objPointer")
    static long objPointerImpl(JNIEnv env, JNIEnv.JClass clazz, @CEntryPoint.IsolateContext long isolateId, JObject obj) {
        JObject global = env.getFunctions().getNewGlobalRef().apply(env, obj);
        return global.rawValue();
    }
    private static native long objPointer(long isolateId, Object obj);

    @CEntryPoint(name = "Java_org_apidesign_demo_matrixultimate_svm_SVMScientificLibraryJNI_releaseGlobalObj")
    static void releaseGlobalObjImpl(JNIEnv env, JNIEnv.JClass clazz, @CEntryPoint.IsolateContext long isolateId, long obj) {
        JObject global = WordFactory.pointer(obj);
        env.getFunctions().getDeleteGlobalRef().apply(env, global);
    }
    private static native void releaseGlobalObj(long isolateId, long objPointer);

    /** Native image object to HotSpot JVM object conversion.
     * Converts {@link MatrixSearch.Result} object of <b>native-image</b> to
     * {@link MatrixSearch.Result} object of standard JVM using {@link JNIEnv}
     * interface to JVM.
     *
     * @param toEnv the interface to the (HotSpot) JVM
     * @param result object to convert
     * @return JObject representing the result in the (HotSpot) JVM
     */
    private static JObject convertSVMToJVM(JNIEnv toEnv, MatrixSearch.Result result) {
        System.loadLibrary("scientificjava");
        final long ownJNI = ownJNIEnv(SVMIsolate.ID);
        final long resultID = objPointer(SVMIsolate.ID, result);
        JObject fromResult = WordFactory.pointer(resultID);
        final String resultClassNameJava = MatrixSearch.Result.class.getName().replace('.', '/');
        final JNIEnv fromEnv = WordFactory.pointer(ownJNI);
        JObject jvmObj = Copy.deepCopy(resultClassNameJava, fromResult, fromEnv, toEnv);
        releaseGlobalObj(SVMIsolate.ID, resultID);
        return jvmObj;
    }
}
