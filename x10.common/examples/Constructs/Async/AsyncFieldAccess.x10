

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
		final dist D = r->Second;
		finish ateach (point p: D) {
			final T NewT = new T();
			async ( this ) {
				t = NewT;
			}
		}
		System.out.println(here+": 't' = "+t);
		final T tt = t;
		finish async ( tt ) {
			atomic {
				tt.i = 3;
			}
		}
		return 3 == future(tt){tt.i}.force();
	}
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new AsyncFieldAccess()).run();
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
