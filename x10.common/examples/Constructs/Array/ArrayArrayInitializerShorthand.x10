/**
 * Test the shorthand syntax for array of arrays initializer
 *
 * @author igor, 12/2005
 */
public class ArrayArrayInitializerShorthand {

	public boolean run() {
		final dist d=[1:10,1:10]->here;
		final int[.] a = new int[d];
		final (int[.])[.] ia = new (int[.])[d] (point [i,j]){ return a; };
		for (point [i,j]:ia) chk(ia[i,j]==a);
		return true;
	}

	static void chk(boolean b) { if (!b) throw new Error(); }

	public static void main(String[] args) {
		final boxedBoolean b=new boxedBoolean();
		try {
			finish async b.val=(new ArrayArrayInitializerShorthand()).run();
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

