public class Generic8 {
	static class A<T,U> {
		T data;
		<U,T>A next;
	}
	public static void main(String[] v) {
		<float,int>A x=new <float,int>A();
		x.next.data=(float)5;
	}
}

