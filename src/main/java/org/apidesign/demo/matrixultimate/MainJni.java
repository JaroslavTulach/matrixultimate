package org.apidesign.demo.matrixultimate;

import org.apidesign.demo.matrixultimate.svm.SVMScientificLibrary;

final class MainJni {
    public static void main(String... args) throws Exception {
        SVMScientificLibrary gsl = new SVMScientificLibrary();
        Main.main(gsl, args);
    }
}
