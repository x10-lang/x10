import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 **/
public class PrimitiveToNullablePrimitive1 extends x10Test {
	 
	public boolean run() {
		return (3 instanceof nullable<int>);
	}
	
	public static void main(String[] args) {
		new PrimitiveToNullablePrimitive1().execute();
	}
}
 