/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * test the 'extern' keyword
 */
public class BasicExternTest1 extends x10Test {

	static extern int doit();
	static extern int overload(float f);
	static extern int overload(long l);
	static extern int alltypes(boolean b, byte bt, char ch, short sh, int i, long l, float f, double d);
	static extern boolean returnBoolean(int x);
	static extern byte returnByte(int x);
	static extern long returnLong(int x);
	static extern float returnFloat(int x);
	static extern double returnDouble(int x);

	static { System.loadLibrary("BasicExtern1"); } // load up 'extern' calls

	public boolean run() {

		if (C.doit() != 100) return false;
		if (doit() != 12) return false;

		if (overload((long)12) != 13) return false;
		if (overload((float)12) != 100) return false;
		if (alltypes(true, (byte) 1, (char) 2, (short)3, (int)4, (long)5, (float)12,  (double)1.2) != 101) return false;
		if (returnByte(12) != (byte)12) return false;
		if (returnBoolean(11) != true) return false;
		if (returnLong(101) != (long)55) return false;
		if (returnFloat(122) != (float) 1007) return false;
		if (returnDouble(99) - (double) 100.3 > 0.0000001) return false;
		return true;
	}

	public static void main(String[] args) {
		new BasicExternTest1().execute();
	}

	static class C {
		static extern int doit();
	}
}

