/** Tests that the constraint d of an extended type D(:d) is entailed by the 
 * type returned by the constructor of the subtype.
 *@author pvarma
 *
 */

import harness.x10Test;

public class SuperExtendsRule_MustFailCompile extends x10Test { 

	class Test(int i, int j) {
		Test(int i, int j) {
			this.i=i;
			this.j=j;
		}
	}
		
	class Test2(int k) extends Test(:i==j){
		Test2(int k) {
		// the call to super below violates the constraint i == j
			super(0,1);
			this.k=k;
		}
	}
	public boolean run() {
	   return true;
	}  
	
    public static void main(String[] args) {
        new SuperExtendsRule_MustFailCompile().execute();
    }
   

		
}
