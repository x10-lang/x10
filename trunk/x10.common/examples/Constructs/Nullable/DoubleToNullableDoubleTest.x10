import harness.x10Test;

public class DoubleToNullableDoubleTest extends x10Test {
	public boolean run() {
		nullable double data = 1.0;
		return true;
	}

	public static void main(String[] args) {
		new DoubleToNullableDoubleTest().execute();
	}
}

