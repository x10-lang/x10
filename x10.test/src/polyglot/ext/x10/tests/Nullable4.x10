public class Nullable4 {
	public static void main(String[] v) {
		try {
			nullable int x = 5;
			Object y = (Object)x;
			System.out.println(y);
		} catch (NullPointerException e) {
			System.out.println("got null");
		}
	}
}

