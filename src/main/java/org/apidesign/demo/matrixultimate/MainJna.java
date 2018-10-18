package org.apidesign.demo.matrixultimate;

import org.apidesign.demo.matrixultimate.jna.JNAScientificLibrary;

final class MainJna {
    public static void main(String... args) throws Exception {
        JNAScientificLibrary gsl = new JNAScientificLibrary();
        Main.main(gsl, args);
    }
}
