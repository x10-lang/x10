/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class CommutativityChecker<T> {

	Operator<T> operator;

	public CommutativityChecker(Operator<T> operator) {
		super();
		this.operator = operator;
	}

	public boolean check(T a, T b) {
		return operator.apply(a, b) == operator.apply(b, a);
	}
}
