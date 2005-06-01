/*
 * Expression parentheses test
 */
public class ExpressionParentheses {

        int x=0x80000000;
	int n=16;
	int z;
	public boolean run() {
		z=((x^(x>>>8)^(x>>>31))&(n-1));
		if (z!=1) return false;
		z=((x|(n-1))&1);
		if (z!=1) return false;
		return true;
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new ExpressionParentheses()).run();
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
