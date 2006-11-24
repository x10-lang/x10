/** Tests that invariants due to a super constraint and a sub constraint are 
 * consistent with each other.
 *@author pvarma
 *
 */

import harness.x10Test;

public class ConsistentInterfaceInvariants extends x10Test { 

    public static interface Test (int l, int m : m == l ) {
     public int put();
    }
    
    public static interface Test1 (int n: l == n && m == n) extends Test { 
     public int foo();
    }
    
    public boolean run() { 
	 return true;
    }
	
    public static void main(String[] args) {
        new ConsistentInterfaceInvariants().execute();
    }
   

		
}
