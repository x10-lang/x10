import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 **/
public class PrimitiveToPrimitive extends x10Test {
	 
	public boolean run() {
		return (3 instanceof int);
	}
	
	public static void main(String[] args) {
		new PrimitiveToPrimitive().execute();
	}
}
 