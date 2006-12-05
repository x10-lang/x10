import harness.x10Test;

/**
 * Purpose: 
 * @author vcave
 **/
public class ObjectToNullablePrimitive extends x10Test {
	 
	public boolean run() {
		x10.lang.Object primitive = 3;
		return (primitive instanceof nullable<int>);
	}
	
	public static void main(String[] args) {
		new ObjectToNullablePrimitive().execute();
	}
}
 