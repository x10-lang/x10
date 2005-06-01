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
	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new CommentTest()).run();
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
