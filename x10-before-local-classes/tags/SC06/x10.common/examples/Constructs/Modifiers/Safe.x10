import harness.x10Test;

/**
 * Check that the safe annotation is recognized.
 * @author vj  9/2006
 */
public class Safe extends x10Test {

    public safe void m() {
    
    }
	public boolean run() {
		m();
		return true;
	}

	public static void main(String[] args) {
		new Safe().execute();
	}

	
}

