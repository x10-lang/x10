import harness.x10Test;
/**
 * Purpose: Check regular java instance of works correctly
 * @author vcave
 **/
public class InstanceofNoConstraints extends x10Test {

	public boolean run() {
		X10DepTypeClassOne identity = new X10DepTypeClassOne(1);
		java.lang.Object upcast = new X10DepTypeClassOne(1);
		X10DepTypeClassOne downcast = new X10DepTypeSubClassOne(1,2);
		
		boolean res1 = identity instanceof X10DepTypeClassOne;
		boolean res2 = upcast instanceof X10DepTypeClassOne;
		boolean res3 = downcast instanceof X10DepTypeClassOne;
		
		return (res1 && res2 && res3);
	}

	public static void main(String[] args) {
		new InstanceofNoConstraints().execute();
	}

}
 