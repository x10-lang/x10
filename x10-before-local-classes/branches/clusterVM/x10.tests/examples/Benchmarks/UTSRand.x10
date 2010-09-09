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

    final static type descriptor = long;

    final static def next(r:descriptor, i:nat) {
        var seed: long = r+i;
        seed = (seed ^ 0x5DEECE66DL) & ((1L << 48) - 1);
        for (var k:int=0; k<11; k++)
            seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        val l0 = (seed >>> (48 - 32)) as int;
        seed = (seed * 0x5DEECE66DL + 0xBL) & ((1L << 48) - 1);
        val l1 = (seed >>> (48 - 32)) as int;
        return ((l0 as long) << 32) + l1;
    }

    const scale = (long.MAX_VALUE as double) - (long.MIN_VALUE as double);

    final static def number(r:descriptor) = (r / scale) - (long.MIN_VALUE / scale);

}
