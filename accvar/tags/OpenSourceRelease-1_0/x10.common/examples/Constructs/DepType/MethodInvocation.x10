/** Tests that a method invocation satisfies the parameter types and constraint
 * of the method declaration.
 *@author pvarma
 *
 */

import harness.x10Test;

public class MethodInvocation extends x10Test { 

	class Test(int i, int j) {
		public int tester (final int k, final int l: k == l) { return k + l;}
		Test (int i, int j ) {
			this.i=i;
			this.j=j;
		}
	}
		

	public boolean run() {
		Test t = new Test (1, 2);
		// the following call types correctly
		t.tester(3, 3);
	   return true;
	}  
	
    public static void main(String[] args) {
        new MethodInvocation().execute();
    }
   

		
}
