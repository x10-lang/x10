import x10.lang.*;

/**
 * Automatic boxing and unboxing of a final value class
 * during up-cast and down-cast
 */
public class Boxing0 {
	public boolean run() {
		x10.lang.Object o=X.five();
		int i= (int) o + 1;
		return (i==6);
	}
	public static void main(String args[]) {
		boolean b= (new Boxing0()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}

class X {
	public static int five() { return 5;}
}
