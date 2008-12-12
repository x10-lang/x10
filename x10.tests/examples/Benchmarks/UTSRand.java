// (C) Copyright IBM Corporation 2006
// This file is part of X10 Test.

final class UTSRand {

    //
    // For now use util.Random instead of SHA. To substitute SHA
    // redefine descriptor, next(), and number().
    //
    // Instead of actually using util.Random, we replicate its
    // function here to avoid allocating a Random object.
    //

    final static long next(long r, int i) {
        long seed = r+i;
        seed = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
        for (int k=0; k<11; k++)
            seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        int l0 = (int) (seed >>> (48 - 32));
        seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        int l1 = (int) (seed >>> (48 - 32));
        return (((long)l0) << 32) + l1;
    }

    final static double scale = ((double)Long.MAX_VALUE) - ((double)Long.MIN_VALUE);

    final static double number(long r) {return (r / scale) - (Long.MIN_VALUE / scale);}
}
