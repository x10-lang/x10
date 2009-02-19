public final class StmtSeq {
    static int fum (int i, int j) { return i * j; };
    static int foo (int x, int y) { return x + y; }
	public final static void main(String[] s) {
	    int i = 11;
	    int j = 12;
	    int k = 13;
	    int x;
	    x = foo (fum(i, j), k);  // this might generate a StmtSeq_c
		System.out.println("Hello, world!" + x);
	}
}
