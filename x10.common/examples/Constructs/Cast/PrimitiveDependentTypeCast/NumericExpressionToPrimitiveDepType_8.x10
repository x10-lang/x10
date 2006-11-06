import harness.x10Test;

/**
 * Purpose: Checks the numeric expression is not evaluated several time while checking for constraint
 * Note: The cast should not be inlined to avoid several execution of ++j
 * Note: Also test the constraint is kept in the right primitive type.
 * @author vcave
 **/
public class NumericExpressionToPrimitiveDepType_8 extends x10Test {

	public boolean run() {
		double j = -1.01;
		double (:self == 0.01) i = 0.01;
		i = (double (:self==0.01)) (++j);
		return ((j==0.01) && (i==0.01));
	}

	public static void main(String[] args) {
		new NumericExpressionToPrimitiveDepType_8().execute();
	}

}
 