import java.util.Random;

/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class IntegerOperatorCommutativityChecker {

	Operator<Integer> operator;

	public IntegerOperatorCommutativityChecker(Operator<Integer> operator) {
		super();
		this.operator = operator;
	}

	public void checkCommutativity() {
		Random random = new Random();
		int a = random.nextInt();
		int b = random.nextInt();
		CommutativityChecker<Integer> integerOperatorCommutativityChecker = new CommutativityChecker<Integer>(
				operator);
		assert integerOperatorCommutativityChecker.check(a, b);
	}

}
