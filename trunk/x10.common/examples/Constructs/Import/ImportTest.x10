import x10.lang.*;
/**
 * Minimal package and import test
 * @author kemal
 * 1/2005
 */
public class ImportTest {

	public boolean run() {
		return future(here){_T2.m2(49)}.force();
	}
		
	public static void main(String args[]) {
		boolean b=(new ImportTest()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
