

/** Testing an async spawned to a field access.

 */
 
public class AsyncFieldAccess {
	
	static class T extends x10.lang.Object {
		public int i;
	}
	T t;
	public boolean run() {
		place Second = place.FIRST_PLACE.next();
		region r = [0:0];
		distribution D = r->Second;
		finish ateach (point p: D) {
			T NewT = new T();
			async ( this ) {
				this.t = NewT;
			}
		}
		finish async ( t ) {
			atomic t.i = 3;
		}
		return 3 == future(t){t.i}.force();
	}
	public static void main(String args[]) {
		boolean b= (new AsyncFieldAccess()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
