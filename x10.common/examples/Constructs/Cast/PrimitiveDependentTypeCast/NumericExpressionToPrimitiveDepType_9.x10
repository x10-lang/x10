import harness.x10Test;

/**
 * Purpose: Checks the numeric expression is not evaluated several time while checking for constraint
 * Note: The cast should not be inlined to avoid several execution of j*=2
 * Note: The compiler stores all decimals to float format. However constraint's *       pretty print method looses this information. Hence a float 
 *       representing 0.001 is now output as 0.001F.
 * @author vcave
 **/
public class NumericExpressionToPrimitiveDepType_9 extends x10Test {

	public boolean run() {
		float j = 1.001F;
		float (:self == 2.002F) i = 2.002F;
		i = (float (:self==2.002F)) (j*=2);
		return ((j==2.002F) && (i==2.002F));
	}

	public static void main(String[] args) {
		new NumericExpressionToPrimitiveDepType_9().execute();
	}

}
 