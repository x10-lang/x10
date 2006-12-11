import harness.x10Test;

/**
 * Purpose: Checks a constrained cast leading to primitive unboxing works 
 *          actually checks the unboxed primitive.
 * @author vcave
 **/
 public class UnboxNullablePrimitiveConstrained1_MustFailCompile extends x10Test {

	public boolean run() {
		boolean res2 = false;
		
		nullable<int(:self==4)> ni = 4;

		// should fail compile
		try {
			// (int(:self==3)) <-- nullable<int>
			// not null check when unboxing and deptype check
			int(:self==3) case2a = (int(:self==3)) ni; 
		} catch (ClassCastException e) {
			res2 = true;
		}

		// (nullable<int(:self==3)>) <-- nullable<int>
		// check ni != null, and no deptype check as constraint are expressed on both side
		// nullable<int(:self==3)> case4a = (nullable<int(:self==3)>) ni; //deptype check

		// try {
			// (nullable<int(:self==3)>) <-- nullable<int> (null)
			// nullable<int(:self==3)> case4b = (nullable<int(:self==3)>) nn; //deptype check
		// } catch (ClassCastException e) {
			// res4 = true;
		// }

		// return res1 && res2 && res4;
		return res2;
	}
	

	public static void main(String[] args) {
		new UnboxNullablePrimitiveConstrained1_MustFailCompile().execute();
	}
}