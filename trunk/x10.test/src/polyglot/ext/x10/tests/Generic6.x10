public class Generic6 {
	static class A<T> {
		T f() {
			// throw an error because we don't yet have a way
			// of constructing T
			throw new Error();
		}
	}
	public static void main(String[] v) {
		int x=new <float>A().f();
	}
}

