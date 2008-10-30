public class TutFuture1 {
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
        System.out.println("fib(10) = " + fib(10));
    } // main()
} // TutFuture1


/* 

Output:

fib(10) = 55

*/
