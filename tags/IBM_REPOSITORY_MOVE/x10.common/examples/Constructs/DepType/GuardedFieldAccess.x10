/** Tests that a field of a class C, guarded with this(:c), is accessed only in objects
 * whose type is a subtype of C(:c).
 *@author pvarma
 *
 */

import harness.x10Test;

public class GuardedFieldAccess extends x10Test { 

	class Test(int i, int j) {
		public this(:i==j) int value = 5;
		Test (final int i, final int j ) {
			this.i=i;
			this.j=j;
		}
	}
		
	public boolean run() {
		Test(: i==j) t = (Test (: i ==j)) new Test(5, 5);
		t.value = t.value + 1;
	   return true;
	}  
	
    public static void main(String[] args) {
        new GuardedFieldAccess().execute();
    }
   

		
}
