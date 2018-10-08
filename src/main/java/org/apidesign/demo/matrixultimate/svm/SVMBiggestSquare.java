package org.apidesign.demo.matrixultimate.svm;

import org.apidesign.demo.matrixultimate.MatrixSearch;

public final class SVMBiggestSquare {
    /** Native method to switch from JVM to Native Image code.
     * 
     * @see SVMScientificLibraryJNI#directlyComputeViaSvm
     */
    private static native MatrixSearch.Result directlyComputeViaSvm(long id, long ptrMatrix);

    public static MatrixSearch.Result compute(long ptrMatrix) {
        return directlyComputeViaSvm(SVMIsolate.ID, ptrMatrix);
    }
}
