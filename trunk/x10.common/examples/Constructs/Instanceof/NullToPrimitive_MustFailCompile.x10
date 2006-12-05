import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 **/
public class NullToPrimitive_MustFailCompile extends x10Test {
	 
	public boolean run() {
		return (null instanceof int);
	}
	
	public static void main(String[] args) {
		new NullToPrimitive_MustFailCompile().execute();
	}
}
 