import harness.x10Test;

/**
 * The test checks that final fields can be accessed in a depclause.
 *
 * @author vj
 */
public class SelfFieldsMustBeProperties1_MustFailCompile extends x10Test {
	class Test(int i) {
	   public final boolean bad; // not declared as a property.
	   public Test(int ii) {
	     i = ii;
	     bad = true;
	   }
	}
	
	public boolean run() {
	   Test (:i==52) a = (Test(:i==52 && self.bad==true)) new Test(52);
	    return true;
	}
	public static void main(String[] args) {
		new SelfFieldsMustBeProperties1_MustFailCompile().execute();
	}
}

