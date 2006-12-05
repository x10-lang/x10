import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 **/
public class PrimitiveToNullablePrimitiveConstrained1 extends x10Test {
	 
	public boolean run() {
		return (3 instanceof nullable<int(:self==3)>);
	}
	
	public static void main(String[] args) {
		new PrimitiveToNullablePrimitiveConstrained1().execute();
	}
}
 