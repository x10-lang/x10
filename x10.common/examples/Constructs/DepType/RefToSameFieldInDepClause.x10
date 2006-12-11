/** Ref to same field in a dependent clause.
 *@author pvarma
 *
 */

import harness.x10Test;

public class RefToSameFieldInDepClause extends x10Test { 

	int (:v == 0) v;
	
    public  boolean run() {
	
	return true;
    }
	
    public static void main(String[] args) {
        new RefToSameFieldInDepClause().execute();
    }
   

		
}
