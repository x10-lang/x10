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
		final boolean b=false;
		try {
			finish b=(new AsyncReturn()).run();
		} catch (Throwable e) {
			e.printStackTrace();
		}
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		x10.lang.Runtime.setExitCode(b?0:1);
	}
	
}
