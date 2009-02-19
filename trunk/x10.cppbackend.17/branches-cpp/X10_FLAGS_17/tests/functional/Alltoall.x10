public class Alltoall {
    static final class Block {
        final int chunkSize;
        double[:rail] A, B;
        public Block(int size) {
            chunkSize = size;
            region(:rail) R = [0:chunkSize*NUM_PLACES];
            A = new double[R] (point [p]) {return here.id;};
            B = new double[R] (point [p]) {return here.id;};
        }
        public void exchange(final Block[:rail] FFT) {
            for (int k=0; k < NUM_PLACES;++k) {
                final int srcIndex =  k * chunkSize;
                final int dstIndex =  here.id * chunkSize;
                final int kk=k;
                async (UNIQUE[k])
                    x10.lang.Runtime.arrayCopy (A, srcIndex,
                            FFT[kk].B, dstIndex, chunkSize);
            }
        }
        public void print(final char arg) {
            final double[:rail] arr;
            if('A' == arg) {
                arr = A;
            } else {
                arr = B;
            }
            for(int i = 0; i < NUM_PLACES*chunkSize; i++) {
                System.out.print(" " + arr[i] + " ");
            }
            System.out.println("");
        }
    }

    const dist(:unique) UNIQUE = dist.UNIQUE;
    const int NUM_PLACES = place.MAX_PLACES;

    static void print_arrays(final Block[:rail] FFT, final char arg) {
        System.out.println("Array " + arg);
        for(point p : UNIQUE) {
            finish async(FFT[p]) {
                FFT[p].print(arg);
            }
        }
        System.out.println("");
    }

    public static void main(String [] args) {
        System.out.println("Alltoall on " + NUM_PLACES + " places");
        if(args.length < 2) {
            System.err.println("Usage: Alltoall chunkSize iters");
            return;
        }
        final int chunkSize = java.lang.Integer.parseInt(args[0]);
        final int niter = java.lang.Integer.parseInt(args[1]);
        final Block[:rail] FFT = (Block[:rail]) new Block[dist.UNIQUE] (point [p]) {
            return new Block(chunkSize);
        };

        //print_arrays(FFT, 'A');
        long s = - System.nanoTime();
        for(int iter = 0; iter < niter; iter++) {
            finish ateach(point [p]: UNIQUE) {
                FFT[p].exchange(FFT);
            }
        }
        s += System.nanoTime();

        System.out.println("chunkSize " + chunkSize + " Places " + NUM_PLACES + " Time "+(s/1000)/niter+" us ");

        //print_arrays(FFT, 'B');
    }
}

//@@X101X@@========== X10/C++ TEST HARNESS ==========<<START>>
//@@X101X@@VER@@0.1.0 [POLYGLOT 2.3.0]
//@@X101X@@TSTAMP@@20082605.105339
//@@X101X@@TCASE@@Alltoall
//@@X101X@@VCODE@@SUCCEED
//@@X101X@@TOUT@@0 60
//@@X101X@@SRCS@@Alltoall.cc
//@@X101X@@NPLACES@@1
//@@X101X@@FLAGS@@
//@@X101X@@ARGS@@
//@@X101X@@DATA@@Alltoall on 1 places
//@@X101X@@========== X10/C++ TEST HARNESS ==========<<END>>
