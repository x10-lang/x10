// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.util.ArrayList;

import x10.io.Printer;


/**
 * A utility class for constructing a list of Halfspaces, to
 * eventually be turned into a HalfspaceList.
 *
 * @author bdlucas
 */

class HalfspaceListBuilder(rank: int) extends ArrayList[Halfspace] {

    // XTENLANG-49
    static type HalfspaceList(rank:nat) = HalfspaceList{self.rank==rank};
    static type HalfspaceListBuilder(rank:nat) = HalfspaceListBuilder{self.rank==rank};

    /**
     * Create a new empty builder.
     */

    public def this(val rank: int): HalfspaceListBuilder{self.rank==rank} {
        this.rank = rank;
    }

    /**
     * Get the result.
     */

    def toHalfspaceList(): HalfspaceList(rank) = toHalfspaceList(false);

    def toHalfspaceList(isSimplified:boolean): HalfspaceList(rank) {
        sort();
        val result = new HalfspaceList(rank, toValRail(), isSimplified);
        return result as HalfspaceList(rank); // XXXX
    }


    /**
     * a simple mechanism of somewhat dubious utility to allow
     * semi-symbolic specification of halfspaces. For example
     * X0-Y1 >= n is specified as addHalfspace(X(0)-Y(1), GE, n)
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
        add(new Halfspace(as));
    }

}
