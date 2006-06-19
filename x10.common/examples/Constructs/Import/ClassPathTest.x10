import testPackage.*;
import harness.x10Test;

/**
 * Testing if -classpath './x10lib' is recognized
 * testPackage is a package in ./x10lib
 *
 * @author kemal
 */
public class ClassPathTest extends x10Test {

	public boolean run() {
		return T1.m1(49);
	}

	public static void main(String[] args) {
		new ClassPathTest().execute();
	}
}

