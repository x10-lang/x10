/*(C) Copyright IBM Corp. 2007*/
package npb2;


//package NPB3_0_X10;

/*****************************************************************************************
         For X10 implementations of the NPB3.0 Benchmarks 
Constants, flags, and widely used methods are contained in this class.
Author: Tong Wen, IBM Research
Date:   04/04/06
      11/06/06 clean up comments
      
      (C) Copyright IBM Corp. 2006
*****************************************************************************************/
final public class Util {
	public static int max(int x, int y) { return x < y? y : x;}
	public static int/*(:self >=0)*/ log2(int/*(: self>=1)*/ p) {
		int r=0; for (;p>1; p /=2) ++r;
		return r;
	}
	public static boolean powerOf2(int a_int){
		int i=(int)Math.abs(a_int);
		return (i >=1) && (i==1<<log2(i));
	}

}
