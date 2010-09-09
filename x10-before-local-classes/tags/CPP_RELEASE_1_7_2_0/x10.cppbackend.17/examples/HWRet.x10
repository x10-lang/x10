public class HWRet {
	boolean x = false;
	final void foo() {
		HWRet o = new HWRet();
		if (o.x) {
			System.out.println("x was true");
			return;
		}
		System.out.println("x was false");
		o.x = true;
		return;
	}
	final void bar() {
		new HWRet().foo();
	}
	final void fubar() { return; }
	public static void main(String[] args) {
		new HWRet().bar();
		new HWRet().fubar();
		System.out.println("The end.");
	}
}
