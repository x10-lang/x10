public class FutureNullable1 {
	public static void main(String[] v) {
        future nullable int x =
			future { true?
                 (nullable future int)null:
                 (nullable future int)future { 1 } };
		System.out.println((force x)==null);
	}
}

