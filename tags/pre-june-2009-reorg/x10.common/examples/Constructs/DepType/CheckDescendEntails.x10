/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/**
 * Check that x=y implies x.f=y.f.
 *
 * @author vj
 */
public class CheckDescendEntails extends x10Test {
	class Prop(int i, int j) {
		public Prop(:self.i==i&&self.j==j)(final int i, final int j) {
			property(i,j);
		}
	}
  
    class Test(Prop a, Prop b) {
        public Test(:self.a==a&&self.b==b)(final Prop a, final Prop b) { 
        	property(a,b);
        }
     }
  
	public boolean run() {
	   Prop p = new Prop(1,2);	
	   Test(:a == b) t =  new Test(p,p);
	   Test(:a.i == b.i) u = t;
	   return true;
	}
	public static void main(String[] args) {
		new CheckDescendEntails().execute();
	}
}