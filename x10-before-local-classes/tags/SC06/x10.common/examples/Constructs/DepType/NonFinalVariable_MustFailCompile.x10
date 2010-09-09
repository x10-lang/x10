import harness.x10Test;

/**
 * The test checks that field accesses within a depclause are checked to be final.
 *
 * @author vj
 */
public class NonFinalVariable_MustFailCompile extends x10Test {
	class Test(int i) {
	   public Test(int ii) {
	     i = ii;
	   }
	}
	
	public boolean run() {
	int ii = 42;
	   Test  a = (Test(:self.i==ii+10)) new Test(52);
	    return true;
	}
	public static void main(String[] args) {
		new NonFinalVariable_MustFailCompile().execute();
	}
}

