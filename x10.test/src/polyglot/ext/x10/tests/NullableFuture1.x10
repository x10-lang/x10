public class NullableFuture1 {
	public static void main(String[] v) {
		nullable future int x;
		if (true) {
			x=future{5};
		} else {
			x=null;
		}
		System.out.println(force ((future int)x));
	}
}

