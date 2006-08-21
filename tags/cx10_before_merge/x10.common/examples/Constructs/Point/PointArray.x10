/**
 * Creating an array of points and assigning to its elements should work.
 *
 * @author Igor, 1/2006
 */
public class PointArray {

	public boolean run() {
		point p[] = new point[1];
		p[0] = [1,2];

		return (p[0][0] == 1 && p[0][1] == 2);
	}

	public static void main(String[] args) {
		final boxedBoolean b=new boxedBoolean();
		try {
			finish async b.val=(new PointArray()).run();
		} catch (Throwable e) {
			e.printStackTrace();
			b.val=false;
		}
		System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
		x10.lang.Runtime.setExitCode(b.val?0:1);
	}
	static class boxedBoolean {
		boolean val=false;
	}
}

