package org.apidesign.demo.matrixultimate.svm;

final class SVMIsolate {
    static final long ID;
    static {
        System.loadLibrary("scientificjava");
        ID = svmInit();
    }
    /** Calls native-image builtin: {@link SVMScientificLibraryJNI#svmInit()}.
     * @return ID of the native-image VM runtime
     */
    private static native long svmInit();
}
