import harness.x10Test;

/**
 * The test checks that property syntax is accepted.
 *
 * @author vj
 */
public class Prop(int i, int j) extends x10Test {

	public Prop(int i, int j) {
	    this.i=i; this.j=j;
	}
	public boolean run() {
	    return true;
	}
	public static void main(String[] args) {
		new Prop(2,3).execute();
	}
}


