/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
//LIMITATION:
//This test case will not meet expectations. It is a limitation of the current release.
import harness.x10Test;

/**
 * Test to check that unsafe is being parsed correctly.
 */
public class AllArrayExternUnsafe extends x10Test {

	public static extern void howdy(int[.] yyi,
	                                short[.] yys,
	                                long[.] yyl,
	                                char[.] yyc,
	                                byte[.] yyb,
	                                float[.] yyf,
	                                double[.] yyd,
	                                boolean[.] yybool);

	static { System.loadLibrary("AllArrayExternUnsafe"); }

	public boolean run() {
		int high = 10;
		boolean verified = false;
		dist d = [0:high]->here;
		int[.] yint = new int unsafe[d];
		short[.] yshort = new short unsafe[d];
		double[.] ydouble = new double unsafe[d];
		float[.] yfloat = new float unsafe[d];
		char[.] ychar = new char unsafe[d];
		byte[.] ybyte = new byte unsafe[d];
		boolean[.] yboolean = new boolean unsafe[d];
		long[.] ylong = new long unsafe[d];

		for (int j = 0; j < 10; ++j) {
			yint[j] = j;
			yshort[j] = (short)j;
			ybyte[j] = (byte)j;
			ychar[j] = (char)j;
			yfloat[j] = (float)j;
			ydouble[j] = (double)j;
			ylong[j] = (long)j;
			yboolean[j] = false;
		}
		

		howdy(yint,yshort,ylong,ychar,ybyte,yfloat,ydouble,yboolean);

		for (int j = 0; j < 10; ++j) {
			if(!compare(j,(int)yint[j],"int")) return false;
			if(!compare(j,(int)yshort[j],"short")) return false;
			if(!compare(j,(int)yfloat[j],"float")) return false;
			if(!compare(j,(int)ydouble[j],"double")) return false;
			if(!compare(j,(int)ybyte[j],"byte")) return false;
			if(!compare(j,(int)ychar[j],"char")) return false;
			if(!compare(j,(int)ylong[j],"long")) return false;
			if(!yboolean[j]){
			   System.out.println("yboolean["+j+"] is false");
			   return false;
			}
			//System.out.println("y["+j+"]:"+(y[j]));
		}
		return true;
	}
	private static boolean compare(int j,int value,String name){
		int expected = j + 100;
		if (value != expected) {
			System.out.println("y"+name+"["+j+"] = "+value+" != "+expected);
			return false;
		}
		return true;
	}

	public static void main(String[] args) {
		new AllArrayExternUnsafe().execute();
	}
}

