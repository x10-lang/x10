/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;


/**
 * Java and X10 permit a call to a method which returns a value to occur as a statement.
 * The returned value is discarded. However, Java does not permit a variable standing alone
 * as a statement. Thus the x10 compiler must check that as a result of flattening it does
 * not produce a variable standing alone. 
 * In an earlier implementation this would give a t0 not reachable error.
 */
public class FlattenVarInit extends x10Test {
    int[.] a;
    public FlattenVarInit() {
      a = new int[[1:10,1:10]] (point [i,j]) { return i+j;};
    }
    int m(int x) {
      
      return x;
    }
    
	public boolean run() {
	int t0;
	 t0 = m(a[1,1]);
	 return t0==2;
	}

	public static void main(String[] args) {
		new FlattenVarInit().execute();
	}
	
}

