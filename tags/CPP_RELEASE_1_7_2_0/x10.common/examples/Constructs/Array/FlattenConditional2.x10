/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;


/**
 * 
 */
public class FlattenConditional2 extends x10Test {
    int[.] a;
    public FlattenConditional2() {
      a = new int[[1:10,1:10]] (point [i,j]) { return i+j;};
    }
    int extra = 4;
    int m(int i) {
      if (i==6) throw new Error();
      return i;
    }
	public boolean run() {
     int x = a[1,1]==2? m(a[2,2]) : m(a[3,3]);
	 return x==4;
	
	}

	public static void main(String[] args) {
		new FlattenConditional2().execute();
	}
	
}

