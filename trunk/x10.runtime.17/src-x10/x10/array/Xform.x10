// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

public abstract class Xform {

    public static def transpose(rank:nat, i:nat, j:nat) {

        // reverse transform
        val v = new MatBuilder(rank+1, rank+1);
        for (var k:int=0; k<=rank; k++)
            if (k!=i && k!=j)
                v(k, k) = 1;
        v(i, j) = 1;
        v(j, i) = 1;
        val V = v.toXformMat();

        // no extra constraints
        val c = new PolyMatBuilder(2);
        val C = c.toSortedPolyMat(true);

        return new PolyXform(V, C);
    }

    public static def tile(sizes:ValRail[int]) {

        // input rank is sizes.length; 
        // output rank is 2*rank
        val rank = sizes.length;

        // reverse transform
        val v = new MatBuilder(rank+1, 2*rank+1);
        v.setDiagonal(0, 0, sizes.length, sizes);
        v.setDiagonal(0, rank, rank, (nat)=>1);
        v(rank, 2*rank) = 1;
        val V = v.toXformMat();

        // extra constraints
        val c = new PolyMatBuilder(2*rank);
        c.setDiagonal(0, rank, rank, (nat)=>-1);
        c.setDiagonal(rank, rank, rank, (nat)=>1);
        c.setColumn(rank, 2*rank, rank, (i:nat)=>1-sizes(i));
        val C = c.toSortedPolyMat(true);

        return new PolyXform(V, C);
    }

    // compose transforms
    abstract public def $times(that:Xform): Xform;
}
