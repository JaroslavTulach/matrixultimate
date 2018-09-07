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
        final long size1 = gsl.getSize1(matrix);
        final long size2 = gsl.getSize2(matrix);

        final long min1 = Math.max(row - size * 2, 0);
        final long max1 = Math.min(row + size * 3, size1);

        final long min2 = Math.max(column - size * 2, 0);
        final long max2 = Math.min(column + size * 3, size2);

        for (long i = min1; i < max1; i++) {
            if (i == row || i == row + size) {
                System.out.printf("\n");
            }
            for (long j = min2; j < max2; j++) {
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
