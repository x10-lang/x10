public class X10Binops {
	static final String HW = "Hello, World!";
	static final region R = [0:12];
	static final char[.] chars = new char[R] (point p[i]) { 
	   if (p > [0]) ;
	   
	   
	   return HW.charAt(i); };
	public static void main(String[] a) {
	    region RR = R || R;
	    region RRR = R && R;
	    char [.] charss = chars | here;
	    region RRRR = R - R;
		for (point p : chars) {
			System.out.println("" + chars[+(- (-p))]);
		}
		System.out.println();
	}
}
