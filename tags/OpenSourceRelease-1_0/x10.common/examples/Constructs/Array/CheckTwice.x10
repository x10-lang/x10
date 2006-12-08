import harness.x10Test;

/**
 * Check that the deptype of a local variable declaration is propagated
 correctly to a reference to that variable.
 @author vj
 */
public class CheckTwice  extends x10Test {

	public boolean run() {
		final region(:rank==1) e = [1:10];
		final region(:rank==2) f = [e,e];
		
	
		return true;
	}

	public static void main(String[] args) {
		new CheckTwice().execute();
	}

}

