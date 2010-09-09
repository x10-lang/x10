import harness.x10Test;

/**
 * Minimal package and import test
 * @author kemal
 * 1/2005
 */
public class ImportTest extends x10Test {

	public boolean run() {
		return future(here) { _T2.m2(49) }.force();
	}

	public static void main(String[] args) {
		new ImportTest().execute();
	}
}

