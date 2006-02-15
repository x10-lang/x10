package x10.lang;

/**
 * @author Igor Peshansky
 */
public class RankMismatchException extends x10.lang.Exception {
	private final x10.lang.point p_;
	private final int r_;

	public RankMismatchException(x10.lang.point p, int r) {
		p_ = p;
		r_ = r;
	}

	public String toString() {
		return "RankMismatchException(point " + p_ + " accessed as rank " + r_ + ")";
	}
}

