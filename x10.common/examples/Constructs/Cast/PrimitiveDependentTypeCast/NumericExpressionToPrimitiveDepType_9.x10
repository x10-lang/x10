import harness.x10Test;

/**
 * Purpose: Checks the numeric expression is not evaluated several time while checking for constraint
 * Note: The cast should not be inlined to avoid several execution of ++j
 * Note: Also test the constraint is kept in the right primitive type.
 * @author vcave
 **/
public class NumericExpressionToPrimitiveDepType_9 extends x10Test {

	public boolean run() {
		float j = -1.01F;
		float (:self == 0.01F) i = 0.01F;
		i = (float (:self==0.01F)) (++j);
		return ((j==0.01F) && (i==0.01F));
	}

	public static void main(String[] args) {
		new NumericExpressionToPrimitiveDepType_9().execute();
	}

}
 