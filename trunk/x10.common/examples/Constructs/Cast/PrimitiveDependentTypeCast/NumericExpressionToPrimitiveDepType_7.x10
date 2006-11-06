import harness.x10Test;

/**
 * Purpose: Checks the numeric expression is not evaluated several time while checking for constraint
 * Note: The cast should not be inlined to avoid several execution of ++j
 * Note: Also test the constraint is kept in the right primitive type. 
 * @author vcave
 **/
public class NumericExpressionToPrimitiveDepType_7 extends x10Test {

	public boolean run() {
		long j = -1;
		long (: self == 0) i = 0;
		i = (long (: self == 0)) (++j);
		return ((j == 0) && (i==0));
	}

	public static void main(String[] args) {
		new NumericExpressionToPrimitiveDepType_7().execute();
	}

}
 