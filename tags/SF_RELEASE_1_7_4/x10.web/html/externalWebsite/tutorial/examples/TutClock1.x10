public class TutClock1 {
    public static void main(String[] args) {
        final int N = 10;
        final int[.] A = new int[[1:N]] (point[i]) { return i; } ;
        final int[.] B = new int[[1:N]]  (point[i]) { return N-i+1; } ;

        finish async {
            final clock c = clock.factory.clock();
            foreach (point[i]: [1:N]) clocked (c) {
                while ( true ) {
                    int old_A_i = A[i];
                    int new_A_i = Math.min(A[i],B[i]);
                    if ( i > 1 ) new_A_i = Math.min(new_A_i,B[i-1]);
                    if ( i < N ) new_A_i = Math.min(new_A_i,B[i+1]);
                    A[i] = new_A_i;
                    next;
                    int old_B_i = B[i];
                    int new_B_i = Math.min(B[i],A[i]);
                    if ( i > 1 ) new_B_i = Math.min(new_B_i,A[i-1]);
                    if ( i < N ) new_B_i = Math.min(new_B_i,A[i+1]);
                    if ( B[i] == new_B_i ) break;
                    B[i] = new_B_i;
                    next;
                    if ( old_A_i == new_A_i && old_B_i == new_B_i ) break;
                } // while
            } // foreach 
        } // finish async

        System.out.print("A =");
        for ( int i = 1 ; i <= N ; i++ ) System.out.print(" " + A[i]);
        System.out.println();
        System.out.print("B =");
        for ( int i = 1 ; i <= N ; i++ ) System.out.print(" " + B[i]);
        System.out.println();
    }
}

/* 

Output:

A = 1 1 1 1 1 1 1 1 1 1
B = 1 1 1 1 1 1 1 1 1 1

*/
