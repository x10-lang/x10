

/** Testing the ability to assign to the field of an object
 * at place here a reference to an object at place here.next().

 @author vj
 */
 import x10.lang.Object;
public class AsyncNext {
	static class T {
		Object val;
	}
	
	public boolean run() {
		final place Other = here.next();
		final T t = new T();
		finish async (Other){
			final T t1 = new T();
			async (t) t.val = t1;
		}
		return t.val.location == Other;
	}
	
	public static void main(String[] args) {
		boolean b=false;
		try {
			finish b=(new AsyncNext()).run();
		} catch (Throwable e) {
			e.printStackTrace();
			b=false;
		}
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		x10.lang.Runtime.setExitCode(b?0:1);
	}
}
