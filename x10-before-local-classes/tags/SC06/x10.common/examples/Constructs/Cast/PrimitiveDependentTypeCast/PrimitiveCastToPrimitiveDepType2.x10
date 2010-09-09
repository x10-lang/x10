import harness.x10Test;

/**
 * Purpose: Cast's dependent type constraint must be satisfied by the primitive.
 * @author vcave
 **/
public class PrimitiveCastToPrimitiveDepType2 extends x10Test {

	public boolean run() {
		
		try { 
			int (: self == 0) i = 0;
			int j = 0;
			i = (int (: self == 0)) j;
		}catch(ClassCastException e) {
			return false;
		}

		return true;
	}

	public static void main(String[] args) {
		new PrimitiveCastToPrimitiveDepType2().execute();
	}

}
 