public class Generic4 {
	static class A<T> {
		T x;
	}
	public static void main(String[] v) {
        A<int> x=new A<int>();
		x.x=(float)5;
	}
}

