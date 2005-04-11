import x10.lang.*;
/**
 * Javadoc comments before a
 * constructor method caused an x10 compiler error (1/2005).
 */
public class CommentTest {
	public int val;
        /** 
	 * Testing a comment before nullary constructor. 
	 * Testing a comment before nullary constructor. 
	 * Testing a comment before nullary constructor. 
	 */
	public CommentTest() {
		val=10;
	}
	/**
	 * Testing a comment before unary constructor. 
	 * Testing a comment before unary constructor. 
	 * Testing a comment before unary constructor. 
	 */
	public CommentTest(int x) {
		val=x;
	}
	/**
	 * Testing comments for run
	 */
	public boolean run() { 
		return val==10;
	}
	/**
	 * Testing comments for main
         */
	public static void main(String args[]) {
		boolean b=(new CommentTest()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
