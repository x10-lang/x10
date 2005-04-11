/**
 * @author Kemal 4/2005
 * only final free variables can be passed to async body
 * 
 * 
 */
public class AsyncTest4_MustFailCompile  {


	public boolean run() {
		int x=0;
		finish async(here) {x++;}
		final int y;
		finish async(here) {async(here) y=3;}
		System.out.println("x="+x+" y="+y);
	  	return true;
	}
	static void chk(boolean b) {
		if(!b) throw new Error();
	}
	public static void main(String args[]) {
		boolean b= (new AsyncTest4_MustFailCompile()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}
