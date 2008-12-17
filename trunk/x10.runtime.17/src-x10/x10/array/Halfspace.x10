// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.Printer;
import x10.io.StringWriter;

import x10.array.mat.*;


/**
 * This class represents a single polyhedral halfspace of the form
 *
 *     a0*x0 + a1*x1 + ... + constant <= 0
 *
 * The as are stored in the first rank elements of the as[] array; the
 * constant is stored in as[rank()] (using homogeneous coordinates).
 *
 * Equivalently, this class may be considered to represent a linear
 * inequality constraint, or a row in a constraint matrix.
 *
 * @author bdlucas
 */

value class Halfspace(rank:nat) extends ValRow implements Comparable[Halfspace] {

    static type PolyRegion(rank:nat) = PolyRegion{self.rank==rank};
    static type PolyRegionListBuilder(rank:nat) = PolyRegionListBuilder{self.rank==rank};
    static type Halfspace(rank:nat) = Halfspace{self.rank==rank};
    static type HalfspaceList(rank:nat) = HalfspaceList{self.rank==rank};

    def this(as: ValRail[int]): Halfspace(as.length-1) {
        super(as);
        property(as.length-1);
    }

    def this(as: Rail[int]): Halfspace(as.length-1) {
        super(as);
        property(as.length-1);
    }

    def this(p:Point, k:int) {
        super(p.rank+1, (i:nat) => i<p.rank? p(i) : k);
        property(p.rank);
    }


    /**
     * natural sort order for halfspaces: from lo to hi on each
     * axis, from most major to least major axis, with constant as
     * least siginficant part of key
     */

    public def compareTo(that: Halfspace): int {
        for (var i: int = 0; i<cols; i++) {
            if (this(i) < that(i))
                return -1;
            else if (this(i) > that(i))
                return 1;
        }
        return 0;
    }


    /**
     * two halfspaces are parallel if all coefficients are the
     * same; constants may differ
     *
     * XXX only right if first coefficients are the same; needs to
     * allow for multiplication by positive constant
     */

    def isParallel(that: Halfspace): boolean {
        for (var i: int = 0; i<cols-1; i++)
            if (this(i)!=that(i))
                return false;
        return true;
    }


    /**
     * halfspace is rectangular if only one coefficent is
     * non-zero
     */

    def isRect(): boolean {
        var nz: boolean = false;
        for (var i: int = 0; i<cols-1; i++) {
            if (this(i)!=0) {
                if (nz) return false;
                nz = true;
            }
        }
        return true;
    }


    /**
     * determine whether point satisfies halfspace
     */

    def contains(p: Point): boolean {
        var sum: int = this(rank);
        for (var i: int = 0; i<rank; i++)
            sum += this(i)*p(i);
        return sum <= 0;
    }

    /**
     * given
     *    a0*x0 + ... +ar   <=  0
     * complement is
     *    a0*x0 + ... +ar   >   0
     *   -a0*x0 - ... -ar   <   0
     *   -a0*x0 - ... -ar   <= -1
     *   -a0*x0 - ... -ar+1 <=  0
     */

    def complement(): Halfspace {
        val init = (i:nat) => i<rank? -this(i) : -this(rank)+1;
        val as = Rail.makeVal[int](rank+1, init);
        return new Halfspace(as);
    }


    /**
     * print a halfspace in both matrix and equation form
     */

    public def printInfo(ps: Printer): void {
        ps.printf("[");
        for (var i: int = 0; i<cols; i++) {
            ps.printf("%4d", this(i));
            if (i==cols-2) ps.printf(" |");
        }
        ps.printf(" ]   ");
        printEqn(ps, " ");
        ps.printf("\n");
    }

    /**
     * print a halfspace in equation form
     */

    private def printEqn(ps: Printer, spc: String): void {
        var sgn: int = 0;
        var first: boolean = true;
        for (var i: int = 0; i<cols-1; i++) {
            if (sgn==0) {
                if (this(i)<0)
                    sgn = -1;
                else if (this(i)>0)
                    sgn = 1;
            }
            val c = sgn*this(i);
            if (c==1) {
                if (first)
                    ps.print("x" + i);
                else
                    ps.print("+x" + i);
            } else if (c==-1)
                ps.print("-x" + i);
            else if (c!=0)
                ps.print((c>=0?"+":"") + c + "*x" + i + " ");
            if (c!=0)
                first = false;
        }
        if (first)
            ps.print("0");
        if (sgn>0)
            ps.print(spc + "<=" + spc + (-this(cols-1)));
        else
            ps.print(spc + ">=" + spc + (this(cols-1)));
    }

    public def toString(): String {
        val os = new StringWriter();
        val ps = new Printer(os);
        //printInfo(ps);
        printEqn(ps, "");
        return os.toString();
    }
}
