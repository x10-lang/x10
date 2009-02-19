public final class TwoMainOne {
    public void foo() { System.out.println("Hello, world foo!"); }
	public final static void main(String[] s) {
	    new TwoMainOne().foo();
	    new TwoMainTwo().bar();
	}
}
