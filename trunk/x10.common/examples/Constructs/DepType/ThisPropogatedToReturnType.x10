/*
 *
 * (C) Copyright IBM Corporation 2006
 *
 *  This file is part of X10 Test.
 *
 */
import harness.x10Test;

/** Tests that if t is of type Test(:i==1), and test() on Test returns int(:self==this.i),
* then t.test() returns int(:self==1). That is, information is propagating
* correctly from the type of the receiver into the return type of a method.
* @author vj
*/
public class ThisPropogatedToReturnType extends x10Test {

	 class Test(int i, int j) {
		Test(:self.i==i&&self.j==j)(final int i, final int j) { 
			property(i,j);
			}
		
		int(:self==this.i) test() {
			return  i;
		}
	}
	
	public boolean run() {
		Test(:i==1) t =  new Test(1,2);
		int(:self==1) one = t.test();
		return true;
	}
	public static void main(String[] args) {
		 new ThisPropogatedToReturnType().execute();
	}
}
