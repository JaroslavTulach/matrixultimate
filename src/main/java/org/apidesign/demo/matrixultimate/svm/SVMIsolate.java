package org.apidesign.demo.matrixultimate.svm;

final class SVMIsolate {
    static final long ID;
    static {
        long id;
        try {
            System.loadLibrary("scientificjava");
            id = svmInit();
        } catch (LinkageError e) {
            id = -1;
        }

        ID = id;
    }
    /** Calls native-image builtin: {@link SVMScientificLibraryJNI#svmInit()}.
     * @return ID of the native-image VM runtime
     */
    private static native long svmInit();
}
