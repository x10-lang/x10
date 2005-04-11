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

	public static void main(String args[]) {
		boolean b= (new ExpressionParentheses()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
