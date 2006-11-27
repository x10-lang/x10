//import harness.x10Test;

/**
 * The test checks that final local variables can be accessed in a depclause.
 *
 * @author vj
 */
public class VariableMustBeFinalInDepClause { 
	class Test(int i) {
		public Test(int ii) {
			i = ii;
		}
	}
	public void m(Test t) {
		 Test(:i==52)  a =  (Test(:i==52)) t;
	}
	
}

