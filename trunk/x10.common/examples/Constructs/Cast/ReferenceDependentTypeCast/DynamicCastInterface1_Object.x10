import harness.x10Test;
\
 /**
  * Purpose: Checks casting from an interface works correctly.
  * @author vcave
  **/
public class DynamicCastInterface1_Object extends x10Test {

	public boolean run() {
		X10InterfaceOne x10Interface = 
			new X10DepTypeClassOne(:p==0)(0);
		
		X10DepTypeClassOne(:p==0) x10Impl = 
			(X10DepTypeClassOne(:p==0)) x10Interface;
		
		return true;
	}

	public static void main(String[] args) {
		new DynamicCastInterface1_Object().execute();
	}

}
 