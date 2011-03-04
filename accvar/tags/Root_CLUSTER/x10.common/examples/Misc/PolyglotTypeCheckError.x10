/**
 * Test for repeating a polyglot type check error.
 * @author Christoph von Praun 5/2005
 * @author Igor 12/2005 -- brought up to date
 */

public class PolyglotTypeCheckError {

	nullable future<int> fut;
	public boolean run() {
		async (here) {
			fut = future (here) { 42 } ;
		};
		// Note that there is a problem here.  X10 assumes sequential consistency,
		// but the underlying Java compiler doesn't.  Thus, it is well within its
		// rights to optimize away the while loop below, and still use the fut
		// field in the force() call (which will cause a NullPointerException).
		// FWIW, this is exactly what happens with the IBM JDK 1.4.1 JIT.  Or, if
		// there's an intervening synchronization operation, the compiler can
		// still cache the "null" value for fut, causing an infinite loop.
		// The fut field should really be volatile for this program to work.
		while (fut == null) ;
		int@here fortytwo = fut.force();
		return fortytwo == 42;
	}

	public static void main(String[] args) {
		final boxedBoolean b=new boxedBoolean();
		try {
			finish b.val=(new PolyglotTypeCheckError()).run();
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
