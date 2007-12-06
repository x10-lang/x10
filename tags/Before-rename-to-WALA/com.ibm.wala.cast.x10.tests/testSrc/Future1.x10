public class Future1 {
    public static void main(String[] args) {
    	new Future1().foo();
    }
    public void foo() {
	future<double> fd= future(here) { Math.cos(Math.PI) };

	double d= fd.force();
    }
}