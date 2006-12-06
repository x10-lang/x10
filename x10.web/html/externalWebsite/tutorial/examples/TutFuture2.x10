public class TutFuture2 {
    static int fib(final int n) {
        if ( n <= 0 ) return 0;
        else if ( n == 1 ) return 1;
        else {
            future<int> fn_1 = future { fib(n-1) };
            future<int> fn_2 = future { fib(n-2) };
            return fn_1.force() + fn_2.force();
        }
    } // fib()
    public static void main(String[] args) {
	future<int> Fi = future { fib(10) } ;
	int i = Fi.force();
	future<future<int>> FFj= future { future{fib(11)} };
	future<int> Fj = FFj.force();
	int j = Fj.force();
	System.out.println("fib(10) = " + i + " ; fib(11) = " + j);
    } // main()
} // TutFuture2


/* 

Output:

fib(10) = 55 ; fib(11) = 89

*/
