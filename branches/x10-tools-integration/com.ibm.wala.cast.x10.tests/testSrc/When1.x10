public class When1 {
    public static void main(String[] args) {
        new When1().foo();
    }
    public void foo() {
	int x = chooseValue();

        when (x == 3) { System.out.println("Hey"); }
        or (x == 5) { System.out.println("Hi"); }
    }
    public int chooseValue() {
        return 5;
    }
}
