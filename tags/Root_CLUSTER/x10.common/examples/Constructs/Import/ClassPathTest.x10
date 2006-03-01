/**
 * Testing if -classpath './x10lib' is recognized
 * testPackage is a package in ./x10lib
 *
 * @author kemal
 */
import testPackage.*;
public class ClassPathTest {

	public boolean run() {
		return T1.m1(49);
	}

	
    public static void main(String[] args) {
        final boxedBoolean b=new boxedBoolean();
        try {
                finish async b.val=(new ClassPathTest()).run();
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
