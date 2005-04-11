
/**
 * Testing if -classpath './ExtraClasses' is recognized
 * testPackage is a package in ./ExtraClasses
 * @author kemal
 */
import testPackage.*;
public class ClasspathTest {

	public boolean run() {
		return m1(49);
	}

	public static void main(String args[]) {
		boolean b= (new ClasspathTest()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}

}
