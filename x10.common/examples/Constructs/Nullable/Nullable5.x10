import x10.lang.*;
/**
 * Casting nullable T to T when the value is null
 * should not cause a null pointer exception
 * if T is itself nullable S
 *
 * nullable nullable T should be treated as identical to nullable T
 *
 * @author kemal, 12/2004
 */
public class Nullable5 {
	public  boolean run() {
                boolean gotNull=false;
		try {
			nullable nullable Nullable5 x = X.mynull();
			nullable Nullable5 y = (nullable Nullable5) x;
                        // x and y are of the same type
                        if (x==y) X.use(y); 
                        // y and use2 argument are of the same type
                        X.use2(y);
		} catch (NullPointerException e) {
		        gotNull=true;	
		}
                return !gotNull;
	}
	public static void main(String args[]) {
		boolean b= (new Nullable5()).run();
		System.out.println("++++++ "+(b?"Test succeeded.":"Test failed."));
		System.exit(b?0:1);
	}
}

class X {
   public static void use(nullable Nullable5 y) { }
   public static void use2(nullable nullable nullable Nullable5 y) { }
   public static nullable nullable Nullable5 mynull() {return null;}
}
   

