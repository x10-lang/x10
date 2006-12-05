import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 **/
public class ObjectToPrimitive extends x10Test {
	 
	public boolean run() {
		x10.lang.Object primitive = 3;
		return (primitive instanceof int);
	}
	
	public static void main(String[] args) {
		new ObjectToPrimitive().execute();
	}
}
 