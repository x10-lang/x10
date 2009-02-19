public class HWCast {
	public static class S {
		public S() { }
		public void id() {
			System.out.println("S");
		}
	}
	public static class C extends S {
		public C() { }
		public void id() {
			System.out.println("C");
		}
	}
	public static void foo(S y) {
		C x = (C) y;
		x.id();
	}
	public static void bar(C x) {
		S y = x;
		y = (S) x;
		y.id();
	}
	public static void main(String[] args) {
		foo(new C());
		bar(new C());
		System.out.println(new C() instanceof S);
		System.out.println(new S() instanceof C);
		C c = new C();
		System.out.println(c instanceof S);
		return;
	}
}

