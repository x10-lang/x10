public class FutureNullable0 {
	public static void main(String[] v) {
		future nullable int x = future { null };
		System.out.println((force x)==null);
	}
}

