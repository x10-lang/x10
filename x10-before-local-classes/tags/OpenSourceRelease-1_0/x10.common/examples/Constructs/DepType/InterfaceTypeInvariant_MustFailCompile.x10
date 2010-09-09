/** Tests that the properties of an interface are implemented by a compliant class 
 * and that the interface constraint is entailed by the compliant class.
 *@author pvarma
 *
 */

import harness.x10Test;

public class InterfaceTypeInvariant_MustFailCompile extends x10Test { 

    public static interface Test (int l, int m : m == l ) {
     public int put();
    }
    
    class Tester(int l, int m : m == 2 && l == 3 ) implements Test{
      public Tester() { l = 3; m = 2;}
      public int put() {
        return 0;
      }
	}
 
    public boolean run() { 
	 return true;
    }
	
    public static void main(String[] args) {
        new InterfaceTypeInvariant_MustFailCompile().execute();
    }
   

		
}
