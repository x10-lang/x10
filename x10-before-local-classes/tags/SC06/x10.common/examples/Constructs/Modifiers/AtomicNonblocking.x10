import harness.x10Test;

/**
 * Check that a safe method can be overridden only by a safe method.
 * @author vj  9/2006
 */
public class AtomicNonblocking extends x10Test {

    atomic void m2() {
    	
    }
	public nonblocking boolean run() {
		m2();
		return true;
	}

	public static void main(String[] args) {
		new AtomicNonblocking().execute();
	}

	
}
