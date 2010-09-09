/**
 * 
 * @author Mohsen Vakilian
 * 
 */
public class Divider implements Operator<Integer> {

	@Override
	public Integer apply(Integer a, Integer b) {
		return a / b;
	}

}
