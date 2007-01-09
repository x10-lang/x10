/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
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
		final short overflow = (short) aboveShort;
		System.out.println(overflow);
		
		// 32777 stored as an integer
		// then constraint is not meet
		short(:self==overflow) ss = (short(:self==overflow)) overflow;
		short(:self==32777) sss = (short(:self==32777)) overflow;
		short(:self==32777) ssss = (short(:self==32777)) 32777;
		
		return (ss == -32759) && (sss == -32759) && (ssss == -32759);
	}

	public static void main(String[] args) {
		new PrimitiveDepTypeCast_Overflow1().execute();
	}

}
 