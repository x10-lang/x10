import harness.x10Test;

/**
 * Purpose: Checks casting from an interface works correctly
 * Note: This example must not generates runtime checking code 
 *       as no constraints are specified in the cast.
 * @author vcave
 **/
public class DynamicCastInterface2_Object extends x10Test {

	public boolean run() {
		X10InterfaceOne x10Interface = 
			new X10DepTypeClassOne(:p==0)(0);
		
		X10DepTypeClassOne x10Imple = (X10DepTypeClassOne) x10Interface;

		return true;
	}

	public static void main(String[] args) {
		new DynamicCastInterface2_Object().execute();
	}

}
 