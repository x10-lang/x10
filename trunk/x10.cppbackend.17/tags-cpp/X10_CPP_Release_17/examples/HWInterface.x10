public class HWInterface {
	static class HWIImp implements HWI {
		public String hw() { return "Hello, World!"; }
	}
	public static void main(String[] a) {
		System.out.println(new HWIImp().hw());
	}
}
