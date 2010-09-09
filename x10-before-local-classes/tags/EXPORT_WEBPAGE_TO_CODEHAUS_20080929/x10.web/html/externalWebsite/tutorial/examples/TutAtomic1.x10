public class TutAtomic1 {
    double rSum;
   
    public static void main(String[] args) {
	new TutAtomic1().run();
    }
    
    void run() {
        final int n = 100;
        finish {
            // Compute odd reciprocals in parallel with even reciprocals
            async for (int i=1 ; i<=n ; i+=2 ) {
                double r = 1.0d / i ; atomic rSum += r; 
            }
            for (int j=2 ; j<=n ; j+=2 ) {
                double r = 1.0d / j ; atomic rSum += r; 
            }
        }
        // Wait till all reciprocals have been accumulated before printing
        System.out.println("rSum = " + rSum);
    }
} // TutAtomic1

/* 

Output:

rSum = 5.187377517639618

*/
