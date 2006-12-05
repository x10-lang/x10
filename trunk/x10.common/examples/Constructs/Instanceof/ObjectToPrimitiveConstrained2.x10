import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 **/
public class ObjectToPrimitiveConstrained2 extends x10Test {
	 
	public boolean run() {
		x10.lang.Object primitive = 3;
		return !(primitive instanceof int(:self==4));
	}
	
	public static void main(String[] args) {
		new ObjectToPrimitiveConstrained2().execute();
	}
}
 