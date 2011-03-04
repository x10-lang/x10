import harness.x10Test;

/**
 * Purpose: Checks constant promotion works.
 * Note: The compiler promotes constant's type from int to int(:self==0)
 * @author vcave
 **/
public class ValueCastToPrimitiveDepType extends x10Test {

	public boolean run() {
		int (: self == 0) i = 0;
		i = (int (: self == 0)) 0;
		return true;
	}

	public static void main(String[] args) {
		new ValueCastToPrimitiveDepType().execute();
	}

}
 