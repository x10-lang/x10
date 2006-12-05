import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 **/
public class PrimitiveToObject extends x10Test {
	 
	public boolean run() {
		return (3 instanceof x10.lang.Object);
	}
	
	public static void main(String[] args) {
		new PrimitiveToObject().execute();
	}
}
 