import harness.x10Test;

/**
 * Purpose:
 * Note:
 * @author vcave
 **/
public class PrimitiveDepTypeCast_Overflow1 extends x10Test {
	 private static final int aboveShort = ((int)java.lang.Short.MAX_VALUE) + 10;
	 
	public boolean run() {
		// 32777 stored in a short is overflowed to -32759
		short overflow = (short) aboveShort;
		// 32777 stored as an integer, 
		short(:self==32777) sss = (short(:self==32777)) overflow;
		return true;
	}

	public static void main(String[] args) {
		new PrimitiveDepTypeCast_Overflow1().execute();
	}

}
 