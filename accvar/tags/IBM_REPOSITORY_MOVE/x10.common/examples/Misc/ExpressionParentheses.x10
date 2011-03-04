import harness.x10Test;

/**
 * Expression parentheses test.
 */
public class ExpressionParentheses extends x10Test {

	int x = 0x80000000;
	int n = 16;
	int z;

	public boolean run() {
		z = ((x ^ (x>>>8) ^ (x>>>31)) & (n-1));
		if (z != 1) return false;
		z = ((x | (n-1)) & 1);
		if (z != 1) return false;
		return true;
	}

	public static void main(String[] args) {
		new ExpressionParentheses().execute();
	}
}

