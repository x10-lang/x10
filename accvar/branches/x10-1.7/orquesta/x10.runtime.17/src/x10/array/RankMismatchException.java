package x10.array;

public class RankMismatchException extends RuntimeException {
    public RankMismatchException(Object o, int rank) {
        super(o + " should have rank " + rank);
    }
}
