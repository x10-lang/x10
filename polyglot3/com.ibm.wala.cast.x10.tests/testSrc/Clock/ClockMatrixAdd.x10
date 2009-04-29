public class ClockMatrixAdd {

    const int NROWS = 1024;
    const int NCOLS = 64;
    const region(:rank==2) DoubleRow = [0:1, 0:NCOLS-1];
    const region(:rank==2) Region = [0:NROWS-1, 0:NCOLS-1];

    public static void main (String[] args) {

        final int [.] A = new int[ Region ] (point [i, j]) { return (i*j + j); },
                      B = new int[ Region ] (point [i, j]) { return 2*(i*j + j);},
                      C = new int[ Region ] (point [i, j]) { return 0; };

        boolean verified = true;

        assert (0 == (NROWS % (place.MAX_PLACES-1)));
        assert (NROWS >=2);

        final dist spus = dist.UNIQUE | [ 1:place.MAX_PLACES-1 ];
        final int nspus = spus.region.size();

        finish ateach (point p: spus) {

            clock red = clock.factory.clock(); 
            clock black = clock.factory.clock();
            int i=0;
            //async clocked(black) read row 0 from A into myA(0);
            //async clocked(black) read row 0 from B into myB(0);
            //async clocked(red) read row 1 from A into myA(1);
            //async clocked(red) read row 1 from B into myB(1);

            red.doNext();
            System.out.println(here.toString() + " sez hello");

            //for (;;) {
            //    black.next();
                //compute myA(i%2), myB(i%2) --> myC(i%2); // black
                //async clocked(black) write myC(i%2) into row i of C;
            //    if (i++ == NROWS-1) break;
                //async clocked(black) read row i+1 from A into myA(i+1%2);
                //async clocked(black) read row i+1 from B into myB (i+1%2);
            //    red.next(); // red phase (odd)
                //compute myA(i%2), myB(i%2) --> myC(i%2);	//red
                //async clocked(red) write myC(%2) into myA((i+2)%2);
            //    if (i++ == NROWS-1) break;
                //async clocked(red) read row i+2 from A into myA(i%2);
                //async clocked(red) read row i+2 from A into myA(i%2);
            //}
            //(i%2==0?black:red).next();
            //compute myA(i%2), myB(i%2) --> myC(i%2);
            //write myC(i%2) into C(i);
        }

        for (int i = 0; i < NROWS; i++) {
            for (int j = 0; j < NCOLS; j++) {
                if (C[i,j] != A[i,j] + B[i,j]) {
                    verified = false;
                }
            }
        }

        if (verified) {
            System.out.println("Verification successful");
        } else {
            System.err.println("ERR: Verification unsuccessful!");
        }

    }
}
