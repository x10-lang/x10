public class Nullable2 {
	public static void main(String[] v) {
		try {
			nullable String x = "blah";
			String y = (String)x;
			System.out.println(y);
		} catch (NullPointerException e) {
			System.out.println("got null");
		}
	}
}

