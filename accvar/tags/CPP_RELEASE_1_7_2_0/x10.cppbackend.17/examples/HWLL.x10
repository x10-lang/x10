public class HWLib {
        public static extern void test();
	public static void main(String[] args) {
                        System.loadLibrary ("test.o");
		System.out.println("Hello World");
                test();
	}
}
