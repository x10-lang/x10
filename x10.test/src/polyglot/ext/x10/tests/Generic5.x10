public class Generic5 {
	static class A<T> {
		T x;
		A(T x) {
			this.x=x;
		}
	}
	public static void main(String[] v) {
		<int>A x=new <int>A((float)5);
	}
}

