import harness.x10Test;

/**
 * An await statement cannot occur in an atomic.
 * @vj
 */
public class NoWhenInAtomic_MustFailCompile extends x10Test {

	boolean b;
	
	public boolean run() {
		atomic {
		  when (b) {
		    System.out.println("Cannot come here"); // must be caught at compile time.
		  }
		  }
		  return true;
	}

	public static void main(String[] args) {
		new NoWhenInAtomic_MustFailCompile().execute();
	}
}

