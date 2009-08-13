
import x10.lang.*;

public class Places {
	public static void main(String[] args) {
    	new Places().foo();
    }
    public void foo() {
    	place p = here;
    	async (p) {
    	   System.out.println ("hello!");
    	}
    }
}