/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class PrimitiveSymbolicSubtractorCommutativityChecker {

	SymbolicSubtractorCommutativityChecker subtractorCommutativityChecker = new SymbolicSubtractorCommutativityChecker();

	public PrimitiveSymbolicSubtractorCommutativityChecker() {
	}

	public static void main(String[] args) {
		new PrimitiveSymbolicSubtractorCommutativityChecker().check(1, 1);
	}

	public boolean check(int a, int b) {
		return subtractorCommutativityChecker.check(a, b);
	}

}
