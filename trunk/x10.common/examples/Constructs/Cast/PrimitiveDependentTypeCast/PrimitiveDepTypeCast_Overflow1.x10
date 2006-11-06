import harness.x10Test;

/**
 * Purpose: Checks the numeric expression is not evaluated several time while checking for constraint
 * Note: The cast should not be inlined to avoid several execution of ++j
 * @author vcave
 **/
public class PrimitiveDepTypeCast_Overflow1 extends x10Test {
	 private static final int aboveShort = ((int)java.lang.Short.MAX_VALUE) + 10;
	 
	public boolean run() {
		short s = 2;
		short(:self==2) ss = (short(:self==2)) s;
		short overflow = (short) aboveShort;
		System.out.println(overflow);
		short(:self==32777) sss = (short(:self==32777)) overflow;
		return true;
	}

	public static void main(String[] args) {
		new PrimitiveDepTypeCast_Overflow1().execute();
	}

}
 