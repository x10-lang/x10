import edu.mit.csail.sdg.annotations.Returns;
import edu.mit.csail.sdg.annotations.Throws;

/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class Subtraction {

	public int subtract(int a, int b) {
		return a - b;
	}

	@Throws("Exception : false")
	@Returns("true")
	public boolean isSubtractCommutative(int a, int b) {
		return a - b == b - a;
	}
}
