public class TutAsync {
    int oddSum, evenSum;

    public static void main(String[] args) {
	new TutAsync().run();
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
} // TutAsync

/* 

Output:

oddSum = 2500 ; evenSum = 2550

*/
