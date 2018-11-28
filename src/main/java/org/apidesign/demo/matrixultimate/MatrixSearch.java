package org.apidesign.demo.matrixultimate;

public interface MatrixSearch {
    public Result search(long matrixPtr);

    public static <Matrix> MatrixSearch findBiggestSquare(GreatScientificLibrary<Matrix> gsl) {
        return new FindBiggestSquare<>(gsl);
    }

    public final class Result {
        private final long row;
        private final long column;
        private final long size;
        private final long time;

        Result(long row, long column, long size, long time) {
            this.row = row;
            this.column = column;
            this.size = size;
            this.time = time;
        }

        public long getRow() {
            return row;
        }

        public long getColumn() {
            return column;
        }

        public long getSize() {
            return size;
        }

        public long getMilliseconds() {
            return time;
        }

        @Override
        public int hashCode() {
            int hash = 7;
            hash = 67 * hash + (int) (this.row ^ (this.row >>> 32));
            hash = 67 * hash + (int) (this.column ^ (this.column >>> 32));
            hash = 67 * hash + (int) (this.size ^ (this.size >>> 32));
            return hash;
        }

        @Override
        public boolean equals(Object obj) {
            if (this == obj) {
                return true;
            }
            if (obj == null) {
                return false;
            }
            if (getClass() != obj.getClass()) {
                return false;
            }
            final Result other = (Result) obj;
            if (this.row != other.row) {
                return false;
            }
            if (this.column != other.column) {
                return false;
            }
            if (this.size != other.size) {
                return false;
            }
            return true;
        }

        @Override
        public String toString() {
            return "Found " + size + " square at " + row + ":" + column + "in " + time + " ms";
        }
    }
}
