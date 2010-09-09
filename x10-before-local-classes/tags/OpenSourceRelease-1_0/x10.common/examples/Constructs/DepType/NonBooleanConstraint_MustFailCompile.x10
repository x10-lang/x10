import harness.x10Test;

/**
 * The test checks that a nonboolean constraint is rejected.
 *
 * @author pvarma
 */
public class NonBooleanConstraint_MustFailCompile(int i, int j : i) extends x10Test {

	public NonBooleanConstraint_MustFailCompile(int k) {
	    this.i=k; this.j=k;
	}
	public boolean run() {
	    return true;
	}
	public static void main(String[] args) {
		new NonBooleanConstraint_MustFailCompile(2).execute();
	}
}


