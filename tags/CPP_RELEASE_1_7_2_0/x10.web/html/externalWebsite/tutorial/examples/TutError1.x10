public class TutError1 {
    int oddSum;

    public static void main(String[] args) {
	new TutError1().run();
    }
    
    void run() {
	final int n = 100;
        finish {
            // Compute oddSum in parallel with evenSum
            async for (int i = 1 ; i <= n ; i += 2 ) oddSum += i; 
            for (int i = 2 ; i <= n ; i += 2 ) evenSum += i;
        }
        // Wait till both oddSum and evenSum have been computed before printing
        System.out.println("oddSum = " + oddSum + " ; evenSum = " + evenSum);
    } 
} // TutError1


/* 

Compiler output:

TutError1.x10:13: Could not find field or local variable "evenSum".
TutError1.x10:16: Could not find field or local variable "evenSum".
2 errors.

*/
