import harness.x10Test;

/**
 * Purpose: Checks "side effect aware" checking code detects when a constraint is not meet.
 * Issue: The cast is not valid til constraints is not meet.
 * X10DepTypeSubClassOne(:p==2&&a==2) <-- X10DepTypeSubClassOne(:p==1&&a==1)
 * @author vcave
 **/
public class InstanceofDownCast2 extends x10Test {
	public boolean run() {
		return !(this.getX10DepTypeSubClassOne(1,1) instanceof X10DepTypeSubClassOne(:p==2&&a==2));
	}

	private java.lang.Object getX10DepTypeSubClassOne(int c1, int c2) {
		return new X10DepTypeSubClassOne(c1, c2);
	}

	public static void main(String[] args) {
		new InstanceofDownCast2().execute();
	}
}
 