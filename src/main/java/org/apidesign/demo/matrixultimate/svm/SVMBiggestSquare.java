package org.apidesign.demo.matrixultimate.svm;

public final class SVMBiggestSquare {
    private static native long directlyComputeViaSvm(long id, long ptrMatrix);

    public static long compute(long ptrMatrix) {
        return directlyComputeViaSvm(SVMIsolate.ID, ptrMatrix);
    }
}
