public class TutError3 {
    int oddSum, evenSum;

    public static void main(String[] args) {
	new TutError3().run();
    }
    
    void run() {
	int n = 100;
        finish {
            // Compute oddSum in parallel with evenSum
            async for (int i = 1 ; i <= n ; i += 2 ) oddSum += i; 
            for (int i = 2 ; i <= n ; i += 2 ) evenSum += i;
        }
        // Wait till both oddSum and evenSum have been computed before printing
        System.out.println("oddSum = " + oddSum + " ; evenSum = " + evenSum);
    } 
} // TutError3

/* 

Compiler output:

x10c:
    /home/praun/workspace_x10/x10.web/html/tutorial/examples/TutError3.java:71:
    local variable n is accessed from within inner class; needs to be declared
    final    
     n;    
     ^    
    1 error    
x10c: Non-zero return code: 1
*/
