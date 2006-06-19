import harness.x10Test;

/**
 * Testing returns in an async body.
 * Should succeed, but fails at compile time.
 *
 * @author vj
 */
public class AsyncReturn extends x10Test {

	public boolean run() {
		class T {
			int t;
		}
		final T f = new T();
		f.t = 1;
		final int v = f.t;
		finish async {
			if (v == 1)
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
		new AsyncReturn().execute();
	}
}

