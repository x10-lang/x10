import harness.x10Test;

/**
 * Purpose: Checks dynamic cast detects constraint is not meet.
 * X10DepTypeClassTwo(:p==0&&q==2) <-- X10DepTypeClassTwo(:p==0&&q==1)
 * @author vcave
 **/
public class DynamicCast1 extends x10Test {

	public boolean run() {
		
		try {						
			x10.lang.Object object = 
				new X10DepTypeClassTwo(:p==0&&q==1)(0,1);
			
			// contraint not meet
			X10DepTypeClassTwo(:p==0&&q==2) convertedObject =
				(X10DepTypeClassTwo(:p==0&&q==2)) object;
			
		}catch(ClassCastException e) {
			return true;
		}

		return false;
	}

	public static void main(String[] args) {
		new DynamicCast1().execute();
	}

}
 