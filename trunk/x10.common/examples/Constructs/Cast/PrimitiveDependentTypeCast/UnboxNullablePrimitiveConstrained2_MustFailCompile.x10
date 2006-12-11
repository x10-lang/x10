import harness.x10Test;

/**
 * Purpose: 
 *          
 * @author vcave
 **/
 public class UnboxNullablePrimitiveConstrained2_MustFailCompile extends x10Test {

	public boolean run() {
		boolean res4 = false;
		
		nullable<int(:self==4)> ni = 4;

		try {
			// (nullable<int(:self==3)>) <-- nullable<int(:c)>
			nullable<int(:self==3)> case4a = (nullable<int(:self==3)>) ni; //deptype check
		} catch (ClassCastException e) {
			res4 = true;
		}

		return res4;
	}
	

	public static void main(String[] args) {
		new UnboxNullablePrimitiveConstrained2_MustFailCompile().execute();
	}
}