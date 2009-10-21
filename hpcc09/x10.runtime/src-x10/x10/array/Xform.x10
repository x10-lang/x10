// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

public abstract class Xform {

    public static def transpose(rank:nat, i:nat, j:nat) {

        // reverse transform
        val t = new MatBuilder(rank+1, rank+1);
        for (var k:int=0; k<=rank; k++)
            if (k!=i && k!=j)
                t(k, k) = 1;
        t(i, j) = 1;
        t(j, i) = 1;
        val T = t.toXformMat();

        // no extra constraints
        val e = new PolyMatBuilder(2);
        val E = e.toSortedPolyMat(true);

        return new PolyXform(E, T);
    }

    public static def tile(sizes:ValRail[int]) {

        // input rank is sizes.length; 
        // output rank is 2*rank
        val rank = sizes.length;

        // reverse transform
        val t = new MatBuilder(rank+1, 2*rank+1);
        t.setDiagonal(0, 0, sizes.length, sizes);
        t.setDiagonal(0, rank, rank, (nat)=>1);
        t(rank, 2*rank) = 1;
        val T = t.toXformMat();

        // extra constraints
        val e = new PolyMatBuilder(2*rank);
        e.setDiagonal(0, rank, rank, (nat)=>-1);
        e.setDiagonal(rank, rank, rank, (nat)=>1);
        e.setColumn(rank, 2*rank, rank, (i:nat)=>1-sizes(i));
        val E = e.toSortedPolyMat(true);

        return new PolyXform(E, T);
    }

    public static def skew(axis:int, with:ValRail[int]) {

        val rank = with.length - 1;
        
        // reverse transform
        val t = new MatBuilder(rank+1, rank+1);
        t.setDiagonal(0, 0, rank+1, (nat)=>1);
        t.setColumn(0, axis, rank, (i:nat)=>with(i));
        val T = t.toXformMat();

        // no extra constraints
        val e = new PolyMatBuilder(2);
        val E = e.toSortedPolyMat(true);

        return new PolyXform(E, T);
    }

    // compose transforms
    abstract public operator this * (that:Xform!): Xform;
}
