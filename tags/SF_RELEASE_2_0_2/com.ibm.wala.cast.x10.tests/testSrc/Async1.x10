public class Async1 {
    public static void main(String[] args) {
    	new Async1().foo();
    }
    public void foo() {
        async(here) {
            System.out.println("Hello world");
        };
    }
}
