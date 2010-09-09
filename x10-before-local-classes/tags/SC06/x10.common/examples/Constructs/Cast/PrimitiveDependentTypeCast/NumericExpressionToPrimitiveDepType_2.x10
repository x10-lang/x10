import harness.x10Test;

/**
 * Purpose: Checks the numeric expression is not evaluated several time while checkink for constraint
 * Note: This test should never be able to fail for this release. The reason is a primitive constrained 
 *       type can only contains one clause
 * @author vcave
 **/
public class NumericExpressionToPrimitiveDepType_2 extends x10Test {

	public boolean run() {
		try {
			int j = -1;
			int (: self == 0) i = 0;
			i = (int (: self == 0)) j++;
		} catch (ClassCastException e) {
			return true;
		}

		return false;
	}

	public static void main(String[] args) {
		new NumericExpressionToPrimitiveDepType_2().execute();
	}

}
 