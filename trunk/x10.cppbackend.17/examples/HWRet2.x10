public class HWRet2 {
	private final static dist(:unique) UNIQUE=dist.UNIQUE;
	boolean x = false;
	static void fubar() { return; }
	static void foo() {
		final HWRet2[.] a = new HWRet2[UNIQUE] (point [p]) { return new HWRet2(); };
		if (a[here.id].x) {
			System.out.println("x was true");
			return;
		}
		System.out.println("x was false");
		a[here.id].x = true;
		return;
	}
	static void bar() {
		foo();
		return;
	}
	public static void main(String[] args) {
		bar();
		System.out.println("The end.");
	}
}
