import harness.x10Test;

/**
 * Check that the local annotation is recognized.
 * @author vj  9/2006
 */
public class Local extends x10Test {

    public local void m() {
    
    }
	public boolean run() {
		m();
		return true;
	}

	public static void main(String[] args) {
		new Local().execute();
	}

	
}

