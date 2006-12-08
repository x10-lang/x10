import harness.x10Test;

/**
 * 
 */
import x10.lang.Object;
public class NullableObjectArray extends x10Test {
	
	 public void toArray(nullable<Object>[:rank==1] a) {
		    a[9] = null;
	       
	    }
	public boolean run() {
	        return true;
	    
	}

	public static void main(String[] args) {
		new NullableObjectArray().execute();
	}

	
}

