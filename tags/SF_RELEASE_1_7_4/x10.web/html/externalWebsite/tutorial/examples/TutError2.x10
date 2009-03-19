public class TutError2 {
    int oddSum, evenSum;

    public static void main(String[] args) {
	new TutError2().run();
    }
    
    void run() {
	final int n = 100d0;
        finish {
            // Compute oddSum in parallel with evenSum
            async for (int i = 1 ; i <= n ; i += 2 ) oddSum += i; 
            for (int i = 2 ; i <= n ; i += 2 ) evenSum += i;
        }
        // Wait till both oddSum and evenSum have been computed before printing
        System.out.println("oddSum = " + oddSum + " ; evenSum = " + evenSum);
    } 
} // TutError2


/* 

Compiler output:

TutError2.x10:9:23:9:23: unexpected token(s) ignored
x10c: Unable to parse TutError2.x10.
1 error.

*/
