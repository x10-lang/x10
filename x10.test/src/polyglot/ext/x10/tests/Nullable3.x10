public class Nullable3 {
	public static void main(String[] v) {
		try {
			nullable int x = null;
			Object y = (Object)x;
			System.out.println(y);
		} catch (NullPointerException e) {
			System.out.println("got null");
		}
	}
}

