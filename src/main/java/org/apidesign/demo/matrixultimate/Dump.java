package org.apidesign.demo.matrixultimate;

final class Dump<M> implements Runnable {
    private final GreatScientificLibrary<M> gsl;
    private final long column;
    private final long row;
    private final long size;
    private final M matrix;

    Dump(GreatScientificLibrary<M> gsl, M matrix, long row, long column, long size) {
        this.gsl = gsl;
        this.matrix = matrix;
        this.row = row;
        this.column = column;
        this.size = size;
    }

    @Override
    public void run() {
        for (long i = 0; i < gsl.getSize1(matrix); i++) {
            if (i == row || i == row + size) {
                System.out.printf("\n");
            }
            for (long j = 0; j < gsl.getSize2(matrix); j++) {
                if (column == j || j == column + size) {
                    System.out.printf("  ");
                }
                double v = gsl.get(matrix, i, j);
                System.out.printf("%d ", (long) v);
            }
            System.out.printf("\n");
        }
    }

}
