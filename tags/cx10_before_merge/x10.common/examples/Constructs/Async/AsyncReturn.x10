/** Testing returns in an async body.
 * Should succeed, but fails at compile time.
  @author vj
 */
 
public class AsyncReturn {

	public boolean run() {
		class T {
			int t;
		}
		final T f = new T();
		f.t = 1;
		final int v = f.t;
		finish async {
			if (v==1) 
				return;
			async (f) {
				atomic {
					f.t = 2;
				}
			}
		}
		return (f.t == 1);
	}
	
	public static void main(String[] args) {
		final boxedBoolean b=new boxedBoolean();
		try {
			finish async b.val=(new AsyncReturn()).run();
		} catch (Throwable e) {
			e.printStackTrace();
			b.val = false;
		}
		System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
		x10.lang.Runtime.setExitCode(b.val?0:1);
	}
	
	static class boxedBoolean {
		boolean val=false;
	}
}
