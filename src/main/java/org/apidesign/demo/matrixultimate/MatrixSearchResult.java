package org.apidesign.demo.matrixultimate;

public interface MatrixSearchResult {
    public long getRow();
    public long getColumn();
    public long getSize();
    public long getMilliseconds();

    default long hashStamp() {
        return getRow() ^ getColumn() ^ getSize();
    }

    public static <Matrix> MatrixSearchResult searchSquare(GreatScientificLibrary<Matrix> gsl, Matrix matrix) {
        FindBiggestSquare<Matrix> find = new FindBiggestSquare<>(gsl, matrix);
        find.run();
        return find;
    }

    public static <Matrix> MatrixSearchResult searchSquare(GreatScientificLibrary<Matrix> gsl, long matrixPtr) {
        Matrix matrix = gsl.fromRaw(matrixPtr);
        return searchSquare(gsl, matrix);
    }
}
