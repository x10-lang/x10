public class HWDoWhile {
	static final String HW = "Hello, World!";
	static final region R = [0:12];
	static final char[.] chars = new char[R] (point p[i]) { return HW.charAt(i); };
	public static void main(String[] a) {
                int i = 3;
		System.out.println(i);
		do{
                        System.out.println(i);
                        i--;
		} while (i >= 0);
		System.out.println(i);
		while (i < 3){
                        System.out.println(i);
                        i++;
		} ;
		System.out.println(i);

	}
}
