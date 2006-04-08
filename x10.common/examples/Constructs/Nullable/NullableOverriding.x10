//LIMITATION: 
//This test case will not meet expectations. It is a limitation of the current release.
/**
 * @author grothoff 01/2006
 *
 * Language clarification needed.
 * Methods whose parameter types are only distinguished by whether they are
 * nullable end up with the same Java signature after erasing the nullable
 * attribute.
 *
 * Ideally we should produce a meaningful compile-time error here, along the
 * lines of what Java does for generics.
 * Alternatively, we can implement a C++-style name mangling scheme to
 * help distinguish those cases.
 */
class NullableOverriding {

	public static boolean test() {
		NullableOverriding a = new NullableOverriding();
		nullable NullableOverriding b = new NullableOverriding();
		return 3 == m(a) + m(b);
	}

	public static int m(nullable NullableOverriding o) {
		return 1;
	}

	public static int m(NullableOverriding o) {
		return 2;
	}
	public static void main(String[] args) {
		final boxedBoolean b=new boxedBoolean();
		try {
			finish async b.val=test();
		} catch (Throwable e) {
			e.printStackTrace();
			b.val=false;
		}
		System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
		x10.lang.Runtime.setExitCode(b.val?0:1);
	}
	static class boxedBoolean {
		boolean val=false;
	}
}

