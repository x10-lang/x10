import harness.x10Test;

/**
 * The test checks that final local variables can be accessed in a depclause.
 *
 * @author vj
 */
public class VariableMustBeFinalInDepClause extends x10Test {
	class Test(int i) {
	   public boolean bad;
	   public Test(int ii) {
	     i = ii;
	   }
	}
	
	public boolean run() {
	final int ii = 42;
	   Test  a = (Test(:self.i==ii+10)) new Test(52);
	    return true;
	}
	public static void main(String[] args) {
		new VariableMustBeFinalInDepClause().execute();
	}
}

