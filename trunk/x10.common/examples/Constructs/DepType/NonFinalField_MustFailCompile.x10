import harness.x10Test;

/**
 * The test checks that field accesses within a depclause are checked to be final.
 *
 * @author vj
 */
public class NonFinalField_MustFailCompile extends x10Test {
	class Test(int i) {
	   public boolean bad;
	   public Test(int ii) {
	     i = ii;
	   }
	}
	
	public boolean run() {
	   Test  a = (Test(:i==52 && bad)) new Test(52);
	    return true;
	}
	public static void main(String[] args) {
		new NonFinalField_MustFailCompile().execute();
	}
}

