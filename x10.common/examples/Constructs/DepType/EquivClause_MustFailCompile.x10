// import harness.x10Test;

/**
 * The test checks that property syntax is accepted.
 *
 * @author vj
 */
public class EquivClause_MustFailCompile { // extends x10Test {
    int (:self==1) i =1;
    int (:self==0) j=i;

/*	public boolean run() {
	   
	    return true;
	}
	public static void main(String[] args) {
		new EquivClause_MustFailCompile().execute();
	}
	*/
}

