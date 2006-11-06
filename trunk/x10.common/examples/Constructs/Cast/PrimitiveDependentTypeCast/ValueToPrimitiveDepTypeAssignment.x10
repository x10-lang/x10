import harness.x10Test;

/**
 * Purpose: Cast's dependent type constraint must be satisfied by the primitive.
 * @author vcave
 **/
public class ValueToPrimitiveDepTypeAssignment extends x10Test {

	public boolean run() {
		
		try { 
			int (: self == 0) i = 0;
			i = 0;
		}catch(Throwable e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		new ValueToPrimitiveDepTypeAssignment().execute();
	}

}
 