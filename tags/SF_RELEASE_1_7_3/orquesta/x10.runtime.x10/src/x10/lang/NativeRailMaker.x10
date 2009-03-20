package x10.lang;


/**
 A factory class that knows how to make native rails of the given type.
*/
@NativeRailMaker.T
public class NativeRailMaker {
	public static  interface T extends Parameter(:x==1) {}
	
	/**
	A native static method that returns an instance of NativeArray@T.  
	TODO: Fix 1.5 compiler so that it permits extern methods to take arguments and return values
	at any type.
	*/
	public static native NativeRail(:length==l)@T  make(final int l);
	
	public static interface Initializer {
		T make(int i);
	}
	public static native NativeValRail(:length==l)@T make(final int l, Initializer init);
	
	
}