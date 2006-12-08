import harness.x10Test;

/**
 * The x10-style initializer for java arrays was being silently ignored.
 * Language clarification needed. Either reject this kind of initializer
 * for java arrays ([]) or make the initializer work correctly.
 *
 * @author kemal 8/2005
 */
public class JavaArrayInitializer_MustFailCompile extends x10Test {

	const int N = 25;

	public boolean run() {
		int[.] foo1 = new int[[0:N-1]] (point [i]) { return i; };
		System.out.println("1");
		for (point [i]: [0:N-1]) chk(foo1[i] == i);
		int[] foo2 = new int[N] (point [i]) { return i; };
		System.out.println("2");
		for (point [i]: [0:N-1]) chk(foo2[i] == i);
		return true;
	}

	public static void main(String[] args) {
		new JavaArrayInitializer_MustFailCompile().execute();
	}
}

