public class Nullable0 {
	public static void main(String[] v) {
		try {
			nullable int x = null;
			int y = (int)x;
		} catch (NullPointerException e) {
			System.out.println("ok");
		}
	}
}

