// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.util.ArrayList;
import x10.io.Printer;


/**
 * Additional builder utility functions for Mats destined to become
 * PolyMats.
 *
 * @author bdlucas
 */

public class PolyMatBuilder(rank: int) extends MatBuilder {

    // XTENLANG-49
    static type PolyMat(rank:nat) = PolyMat{self.rank==rank};
    static type PolyMatBuilder(rank:nat) = PolyMatBuilder{self.rank==rank};


    /**
     * Create a new empty builder.
     */

    public def this(val rank: int): PolyMatBuilder{self.rank==rank} {
        super(rank+1);
        this.rank = rank;
    }


    /**
     * Get the result.
     */

    public def toSortedPolyMat(isSimplified:boolean): PolyMat(rank) {
        mat.sort(PolyRow.compare.(Row,Row));
        val result = new PolyMat(mat.size(), rank+1, (i:nat,j:nat)=>mat(i)(j), isSimplified);
        return result as PolyMat(rank); // XXXX
    }


    /**
     * a simple mechanism of somewhat dubious utility to allow
     * semi-symbolic specification of halfspaces. For example
     * X0-Y1 >= n is specified as add(X(0)-Y(1), GE, n)
     *
     * XXX coefficients must be -1,0,+1; can allow larger coefficients
     * by increasing # bits per coeff
     */

    private const ZERO: int = 0xAAAAAAA;

    public const GE: int = 0;
    public const LE: int = 1;

    final public static def X(axis: int): int {
        return 0x1<<2*axis;
    }

    public def add(var coeff: int, op: int, k: int): void {
        coeff += ZERO;
        val as = Rail.makeVar[int](rank+1);
        for (var i: int = 0; i<rank; i++) {
            val a = (coeff&3) - 2;
            as(i) = op==LE? a : - a;
            coeff = coeff >> 2;
        }
        as(rank) = op==LE? -k : k;
        add((i:nat) => as(i));
    }
}
