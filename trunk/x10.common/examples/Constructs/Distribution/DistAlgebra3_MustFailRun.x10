import harness.x10Test;

/**
 * Testing that disjoint union of dists
 * actually checks for disjointness.
 *
 * @author kemal 4/2005
 */
public class DistAlgebra3_MustFailRun extends x10Test {

	const int N = 24;

	public boolean run() {
		final dist D = dist.factory.cyclic([0:N-1]);
		final dist D2 = D | [0 : N/2-1];
		final dist D3 = D | [N/2 : N-1];
		final dist D4 = D2 || D3; // disjoint
		chk(D4.equals(D));
		final dist D5 = D || D2; // not disjoint
		return true;
	}

	public static void main(String[] args) {
		new DistAlgebra3_MustFailRun().execute();
	}
}

