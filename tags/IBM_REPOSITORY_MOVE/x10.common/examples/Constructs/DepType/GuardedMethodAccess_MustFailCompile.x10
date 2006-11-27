/** Tests that a method of a class C, guarded with this(:c), is accessed only in objects
 * whose type is a subtype of C(:c).
 *@author pvarma
 *
 */

import harness.x10Test;

public class GuardedMethodAccess_MustFailCompile extends x10Test { 

	class Test(int i, int j) {
		public int value = 0;
		public this(:i==j) int key() {return 5;}
		Test (final int i, final int j ) {
			this.i=i;
			this.j=j;
		}
	}
		
	public boolean run() {
		Test t = new Test(5, 6);
		t.value = t.key() + 1;
	   return true;
	}  
	
    public static void main(String[] args) {
        new GuardedMethodAccess_MustFailCompile().execute();
    }
   

		
}
