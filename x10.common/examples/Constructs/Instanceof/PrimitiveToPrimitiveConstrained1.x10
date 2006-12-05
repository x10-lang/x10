import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 **/
public class PrimitiveToPrimitiveConstrained1 extends x10Test {
	 
	public boolean run() {
		return ((3 instanceof int(:self==3)));
	}
	
	public static void main(String[] args) {
		new PrimitiveToPrimitiveConstrained1().execute();
	}
}
 