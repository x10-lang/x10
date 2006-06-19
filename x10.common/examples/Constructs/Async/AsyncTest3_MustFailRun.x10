import harness.x10Test;

/**
 * Remote accesses must be flagged by the compiler.
 *
 * NEW SEMANTICS: non-local accesses cause a BadPlaceException
 * to be thrown at run time.
 * Compiler techniques can be used to reduce or remove
 * the run time checks.
 * Compiler can issue a warning when it detects that
 * a BadPlaceException will always occur or will likely
 * occur, but the error is still caught at run time.
 * Accordingly, this test is being renamed
 * *_MustFailRun
 *
 * @author Kemal 4/2005
 */
public class AsyncTest3_MustFailRun extends x10Test {

	public boolean run() {
		final int[.] A = new int[dist.factory.unique()];
		chk(place.MAX_PLACES >= 2);
		chk(A.distribution[0] == here);
		chk(A.distribution[1] != here);
		final X x = new X();

		// Compiler can give one of the answers:
		// YES, NO, MAYBE for each
		// question: is this lvalue expression ever nonlocal?

		// When the compiler answer is YES or NO
		// the actual answer at run time matches
		// the compiler answer on every execution.	.
		// When the compiler answer is MAYBE, the actual
		// answer may be either YES or NO.

		// Compiler: NO: Actual: NO
		// no compiler error on next lines
		finish async(here) { A[0] += 1; }
		A[0] += 1;

		// Compiler: YES, Actual: YES
		//==> Compiler errors expected
		finish async(here) { A[1] += 1; }
		A[1] += 1;

		// Compiler: MAYBE; Actual: NO
		//==> Compiler error expected here
		finish async(here) { A[x.zero()] += 1; }
		A[x.zero()] += 1;

		// Compiler: MAYBE; Actual: YES
		// for lvalue A[X.one()]
		//==> Compiler errors expected here
		finish async(here) { A[0] += A[x.one()]; }
		A[0] += A[x.one()];

		// Compiler YES: Actual: YES (for lvalue A[1])
		// ==> Compiler errors expected on next line
		chk(A[0] == 8 && A[1] == 2);

		return true;
	}

	public static void main(String[] args) {
		new AsyncTest3_MustFailRun().execute();
	}

	/**
	 * Dummy class to make static memory disambiguation difficult
	 * for a typical compiler
	 */
	static class X {
		public int[] z = { 1, 0 };
		int zero() { return z[z[z[1]]]; }
		int one() { return z[z[z[0]]]; }
		void modify() { z[0]++; }
	}
}

