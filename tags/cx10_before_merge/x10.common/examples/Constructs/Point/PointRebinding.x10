/**
 * Must allow binding components to an existing point.
 *
 * @author Igor, 1/2006
 */
public class PointRebinding {

	public boolean run() {
		point p = [1,2];
		point [i,j] = p;

		return (i == 1 && j == 2);
	}

	public static void main(String[] args) {
		final boxedBoolean b=new boxedBoolean();
		try {
			finish async b.val=(new PointRebinding()).run();
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

