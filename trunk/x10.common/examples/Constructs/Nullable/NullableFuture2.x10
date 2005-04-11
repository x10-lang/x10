import x10.lang.*;
/**
 * converting nullable future int to future int causes
 * exception when value is null
 */
public class NullableFuture2 {
	public boolean run() {
                boolean gotNull=false;
		nullable future<int> x;
		if (!X.t()) {
			x=future{42};
		} else {
			x=null;
		}
		try {
			X.use(((future<int>)x).force());
		} catch (NullPointerException e) {
			gotNull=true;
		}
                return gotNull;
	}
	public static void main(String args[]) {
		boolean b= (new NullableFuture2()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}

class X {
   public static boolean t() {return true;}
   public static void use(int x) {}
}
   

