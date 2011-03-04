public class HWInterface2 {
	public static void main(String[] a) {
		System.out.println(new HWI() {
			public String hw() {
				return "Hello, World!";
			}
		}.hw());
	}
}
