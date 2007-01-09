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
 * Java and X10 permit a call to a method which returns a value to occur as a statement.
 * The returned value is discarded. However, Java does not permit a variable standing alone
 * as a statement. Thus the x10 compiler must check that as a result of flattening it does
 * not produce a variable standing alone. 
 * In an earlier implementation this would give a t0 not reachable error.
 */
public class FlattenPlaceCast extends x10Test {
    Test[.] a;
    place[.] d;
    public FlattenPlaceCast() {
      a = new Test[[1:10,1:10]] (point [i,j]) { return new Test();};
      d = new place[[1:10]] (point [i]) { return here;};
    }
   
    static class Test extends x10.lang.Object {};
	public boolean run() {
		Test x = (@d[1].next()) a[1,1];
	
	 return true;
	}

	public static void main(String[] args) {
		new FlattenPlaceCast().execute();
	}
	
}

