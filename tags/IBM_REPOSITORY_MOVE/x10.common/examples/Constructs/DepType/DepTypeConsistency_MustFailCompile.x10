/** Tests that a free variable in DepType that is not parametrically consistent 
 * causes compilation failure.
 *@author pvarma
 *
 */

import harness.x10Test;

public class DepTypeConsistency_MustFailCompile extends x10Test { 

/* free variable j is not parametrically consistent */
    class Tester(int i : j == 2 ) {
      public Tester(int arg) { i = arg;}
    }
	
 
    public boolean run() { 
	 return true;
    }
	
    public static void main(String[] args) {
        new DepTypeConsistency_MustFailCompile().execute();
    }
   

		
}
