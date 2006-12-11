import harness.x10Test;

/**
 * Purpose: Checks a constrained cast leading to primitive unboxing works 
 *          actually checks the unboxed primitive.
 * @author vcave
 **/
 public class UnboxPrimitiveConstrained1_MustFailCompile extends x10Test {

	public boolean run() {
		boolean res2 = false;
		
		int(:self==4) ni = 4;
		
		try {
			// (int(:self==3)) <-- int(:c)
			// not null check when unboxing and deptype check
			int(:self==3) case2a = (int(:self==3)) ni; 
		} catch (ClassCastException e) {
			res2 = true;
		}

		return res2;
	}
	

	public static void main(String[] args) {
		new UnboxPrimitiveConstrained1_MustFailCompile().execute();
	}
}