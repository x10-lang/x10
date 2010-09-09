public class HWInlineFunc {
        public static void foo(int i) {

                System.out.println(i);
                bar();
                System.out.println(i+1);

        }
        public static void bar(){
                
                System.out.println (0);
        }
	public static void main(String[] args) {
		System.out.println("Hello World");
                final int a = 0 ;
                foo(2);

	}
}
