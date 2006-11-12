import harness.x10Test;

/**
 * Purpose: Check primitive dependent type assignment to primitive variable works.
 * @author vcave
 **/
public class DepTypeToPrimitiveAssignment extends x10Test {

	public boolean run() {
		
		try { 
			int (: self == 0) i = 0;
			int (: self == 1) k = 1;
			int j = 0;
			j = i;
			j = k;
		}catch(Throwable e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		new DepTypeToPrimitiveAssignment().execute();
	}

}
 