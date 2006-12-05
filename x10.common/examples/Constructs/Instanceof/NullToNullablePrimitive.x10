import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 **/
public class NullToNullablePrimitive extends x10Test {
	 
	public boolean run() {
		return (null instanceof nullable<int>);
	}
	
	public static void main(String[] args) {
		new NullToNullablePrimitive().execute();
	}
}
 