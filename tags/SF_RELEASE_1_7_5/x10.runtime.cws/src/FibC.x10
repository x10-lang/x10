public class FibCX {
    static int fib(int n) {
	if (n < 2) return n;
	int x;
	int y;
        finish { 
   	  async x = fib(n-1);
	  y = fib(n-2);
	}
	return x+y;
    }
}
