/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class DividerCommutativityChecker {

	public static void main(String[] args) {
		IntegerOperatorCommutativityChecker isDividerCommutative = new IntegerOperatorCommutativityChecker(
				new Divider());
		isDividerCommutative.checkCommutativity();
	}
}
