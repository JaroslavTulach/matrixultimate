package org.apidesign.demo.matrixultimate.svm;

import java.util.Arrays;
import java.util.List;
import org.graalvm.nativeimage.c.CContext;

public final class GslDirectives implements CContext.Directives {

    @Override
    public List<String> getHeaderFiles() {
        return Arrays.asList("<gsl/gsl_matrix.h>");
    }

    @Override
    public List<String> getLibraries() {
        return Arrays.asList("gsl");
    }

}
