/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;


/**
 * Check the condition of an if is flattened.
 */
public class FlattenConditional3 extends x10Test {
    int[.] a;
    public FlattenConditional3() {
      a = new int[[1:10,1:10]] (point [i,j]) { return i+j;};
    }
    
    int m(int a) {
     if (a == 2) throw new Error();
     return a;
    }
    
	public boolean run() {
	int b=0;
	if (a[2,2] == 0)
		b=1;
	
	 return b==0;
	}

	public static void main(String[] args) {
		new FlattenConditional3().execute();
	}
	
}

