import harness.x10Test;

/**
 * Checks that no property must be left uninitialized by a constructor.
 *
 * @author pvarma
 */
public class UninitializedProperty_MustFailCompile(int i, int j) extends x10Test {

	public UninitializedProperty_MustFailCompile(int i, int j) {
	    this.i=i; 
	}
	public boolean run() {
	    return true;
	}
	public static void main(String[] args) {
		new UninitializedProperty_MustFailCompile(2,3).execute();
	}
}


