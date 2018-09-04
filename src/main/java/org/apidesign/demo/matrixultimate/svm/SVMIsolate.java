package org.apidesign.demo.matrixultimate.svm;

final class SVMIsolate {
    static final long ID;
    static {
        System.loadLibrary("scientificjava");
        ID = svmInit();
    }
    private static native long svmInit();
}
