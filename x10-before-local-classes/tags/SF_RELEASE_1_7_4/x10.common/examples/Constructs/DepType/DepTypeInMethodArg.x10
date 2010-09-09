/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that a method can have a deptype argument and it is checked properly.
 *
 * @author vj
 */
 public class DepTypeInMethodArg extends x10Test {
	    class Test(int i, int j) {
	       public Test(:self.i==i&&self.j==j)(final int i, final int j) { 
                   property(i,j);
               }
	    }
	   public boolean m(final Test t1, Test(:i == t1.i) t2) { 
	      return true;
	    }
		public boolean run() {
		   Test(:i==j) x =  new Test(1,1); 
		   return true;
		}
		public static void main(String[] args) {
			new DepTypeInMethodArg().execute();
		}
	}
