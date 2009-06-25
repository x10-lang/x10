public class AtEach1 {
    public static void main(String[] args) {
        new AtEach1().foo();
    }
    public void foo() {
        region r = [1:100];
        dist d = r -> here;

        ateach (point p : d) {
            System.out.println(p);
        }
    }
}