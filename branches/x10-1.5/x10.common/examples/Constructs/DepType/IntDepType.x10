/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a depclause can be added to primitive types such as int.
 *
 * @author vj
 */
public class IntDepType extends x10Test {
    class Test(int i, int j) {
       public Test(int i, int j) { this.i=i; this.j=j;}
    }
  
	public boolean run() {
		int(:self == 0) i = 0;
	   return true;
	}
	public static void main(String[] args) {
		new IntDepType().execute();
	}
}