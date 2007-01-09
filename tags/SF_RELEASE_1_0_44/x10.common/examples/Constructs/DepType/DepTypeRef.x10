/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that array deptypes are properly processed.
 *
 * @author vj
 */
public class DepTypeRef extends x10Test {
	public boolean run() {
  	  region(:rect) R =  [1:2,1:2];
	  double [:rect] a =  (double[:rect]) new double[R] (point p) { return 1.0;};
		//System.out.println("" );//+ foo(a));
	   return true;
	}
	public static void main(String[] args) {
		new DepTypeRef().execute();
	}
}
