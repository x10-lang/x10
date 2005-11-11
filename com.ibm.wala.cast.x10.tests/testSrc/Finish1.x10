public class Finish1 {
    public static void main(String[] args) {
	new Finish1().foo();
	finish new Finish1().bar();
    }
    public void foo() {
        finish async(here) {
            System.out.println("Hello world");
        };
    }
    public void bar() {
        async(here) {
            System.out.println("Hello world");
        };
    }
}
