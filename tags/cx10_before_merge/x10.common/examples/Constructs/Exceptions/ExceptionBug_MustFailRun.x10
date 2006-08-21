/**
 *@author kemal 5/2005
 *
 * Program that throws an uncaught error was causing a deadlock 
 * as of 5/26/2005.
 */

public class ExceptionBug_MustFailRun {
    int n=0;
    public boolean run() {
	if(n==0) throw new Error("Testing error");
	return true;
    }

    public static void main(String[] args) {
        boolean b=(new ExceptionBug_MustFailRun()).run();
        System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
        x10.lang.Runtime.setExitCode(b?0:1);
    }

}
