public class Generic4 {
	static class A<T> {
		T x;
	}
	public static void main(String[] v) {
		<int>A x=new <int>A();
		x.x=(float)5;
	}
}

