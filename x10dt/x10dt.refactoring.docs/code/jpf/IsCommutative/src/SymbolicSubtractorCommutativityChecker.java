/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class SymbolicSubtractorCommutativityChecker extends
		CommutativityChecker<Integer> {

	public SymbolicSubtractorCommutativityChecker() {
		super(new Subtractor());
	}

	public boolean check(Integer a, Integer b) {
		return operator.apply(a, b) == operator.apply(b, a);
	}

}
