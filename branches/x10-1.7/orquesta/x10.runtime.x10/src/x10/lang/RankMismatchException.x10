package x10.lang;


public class RankMismatchException extends IllegalOperationException {

    public RankMismatchException(int rank1, int rank2) {
        super("ranks " + rank1 + " and " + rank2 + " don't match");
    }
}
