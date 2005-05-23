//LIMITATION: 
//This test case will not meet expectations. It is a limitation of the current release.

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

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish b.val=(new ClasspathTest()).run();
        } catch (Throwable e) {
                e.printStackTrace();
                b.val=false;
        }
        System.out.println("++++++ "+(b.val?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b.val?0:1);
    }
    static class boxedBoolean {
        boolean val=false;
    }


}
