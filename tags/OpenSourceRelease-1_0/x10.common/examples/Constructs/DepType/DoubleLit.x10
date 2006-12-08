
import harness.x10Test;

/**
 * 
 *
 * @author vj
 */
public class DoubleLit extends x10Test {
	public boolean run() {
		double (: self == 1.0) i = 1.0;
	    return true;
	}
	public static void main(String[] args) {
		new DoubleLit().execute();
	}
}

