public class FutureNullable2 {
	public static void main(String[] v) {
		future nullable int x =
			future { false?
				 (nullable future int)null:
				 (nullable future int)future { 1 } };
		System.out.println(((int)(force x))==1);
	}
}

