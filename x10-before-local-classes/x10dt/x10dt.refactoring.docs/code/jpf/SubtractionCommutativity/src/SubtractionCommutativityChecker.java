/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class SubtractionCommutativityChecker {

	public static void main(String[] args) {
		isThisSubtractionCommutative(1, 2);
	}

	public static void isThisSubtractionCommutative(int a, int b) {
		assert a - b == b - a;
	}

}
