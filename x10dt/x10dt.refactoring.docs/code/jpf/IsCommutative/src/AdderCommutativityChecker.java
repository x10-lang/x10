/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class AdderCommutativityChecker {

	public static void main(String[] args) {
		IntegerOperatorCommutativityChecker isAdderCommutative = new IntegerOperatorCommutativityChecker(
				new Adder());
		isAdderCommutative.checkCommutativity();
	}
}
