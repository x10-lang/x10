/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class SubtractorCommutativityChecker {

	public static void main(String[] args) {
		IntegerOperatorCommutativityChecker isAdderCommutative = new IntegerOperatorCommutativityChecker(
				new Subtractor());
		isAdderCommutative.checkCommutativity();
	}
}
