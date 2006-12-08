import harness.x10Test;

/**
 * Purpose: Checks a boxed litteral primitive is effectively checked against
 *          a constrained primitive type.
 * @author vcave
 **/
public class PrimitiveToPrimitiveConstrained_MustFailCompile extends x10Test {
	 
	public boolean run() {
		return (3 instanceof int(:self==4));
	}
	
	public static void main(String[] args) {
		new PrimitiveToPrimitiveConstrained_MustFailCompile().execute();
	}
}
 