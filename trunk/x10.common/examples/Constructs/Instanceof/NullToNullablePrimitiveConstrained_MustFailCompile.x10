import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 **/
public class NullToNullablePrimitiveConstrained_MustFailCompile extends x10Test {
	 
	public boolean run() {
		return !(null instanceof nullable<int(:self==4)>);
	}
	
	public static void main(String[] args) {
		new NullToNullablePrimitiveConstrained_MustFailCompile().execute();
	}
}
 