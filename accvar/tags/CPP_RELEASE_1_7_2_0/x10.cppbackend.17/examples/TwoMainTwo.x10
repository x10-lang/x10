public final class TwoMainTwo {
    public void bar() { System.out.println("Hello, world bar!"); }
	public final static void main(String[] s) {
	    new TwoMainTwo().bar();
	    new TwoMainOne().foo();
	}
}
