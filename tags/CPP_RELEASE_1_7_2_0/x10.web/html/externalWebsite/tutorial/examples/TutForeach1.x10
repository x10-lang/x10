public class TutForeach1 {
    public static void main(String[] args) {
        final int N = 5;
        final int[.] A = new int[ [1:N,1:N] ] (point [i,j]) { return i+j;} ;

        // For the A[i,j] = F(A[i,j]) case, both loops can execute in parallel 
        finish foreach ( point[i,j] : A.region ) A[i,j] = A[i,j] + 1;

        // For the A[i,j] = F(A[i,j-1]) case, only the outer loop can execute in parallel 
        finish foreach ( point[i] : A.region.rank(0) ) 
            for ( point[j] : [(A.region.rank(1).low()+1):A.region.rank(1).high()] ) 
                A[i,j] = A[i,j-1] + 1;

        // For the A[i,j] = F(A[i-1,j]) case, only the inner loop can execute in parallel 
        for (point[i]: [(A.region.rank(0).low()+1):A.region.rank(0).high()] ) 
            finish foreach ( point[j] : A.region.rank(1) ) 
                A[i,j] = A[i-1,j] + 1;

        // For the A[i,j] = F(A[i-1,j],A[i,j-1]) case, use loop skewing to
        // enable the inner loop to execute in parallel 
        for (point[t] : [4:2*N]) {
            finish foreach (point[j] : [java.lang.Math.max(2,t-N):java.lang.Math.min(N,t-2)]) {
                int i = t - j;
                System.out.print("(" + i + "," + j + ")");
                A[i,j] = A[i-1,j] + A[i,j-1] + 1;
            }
            System.out.println();
        }
    } // main()
} // TutForeach1

/* 

Output:

(2,2)
(3,2)(2,3)
(4,2)(3,3)(2,4)
(5,2)(4,3)(3,4)(2,5)
(5,3)(4,4)(3,5)
(5,4)(4,5)
(5,5)

*/
