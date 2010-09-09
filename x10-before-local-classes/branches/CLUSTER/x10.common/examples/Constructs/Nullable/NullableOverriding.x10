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