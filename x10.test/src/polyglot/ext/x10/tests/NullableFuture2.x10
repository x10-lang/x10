public class NullableFuture2 {
	public static void main(String[] v) {
		nullable future int x;
		if (false) {
			x=future{5};
		} else {
			x=null;
		}
		try {
			System.out.println(force ((future int)x));
		} catch (NullPointerException e) {
			System.out.println("got a null");
		}
	}
}

