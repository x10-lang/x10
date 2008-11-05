// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.PrintStream;

import x10.util.ArrayList;

class HalfspaceList(rank: int) extends ArrayList[Halfspace] {

    public def this(val rank: int): HalfspaceList{self.rank==rank} {
        this.rank = rank;
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


    /**
     * Eliminate redundant parallel halfspaces. Since halfspaces
     * with equal coefficients are sorted in increasing order of
     * the constant (which is the least significant part of the
     * key) taking the last of a set of parallel halfspaces
     * captures the strongest halfspace.
     */

    def simplifyParallel(): HalfspaceList {

        sort();

        if (size()==0)
            return this;

        val result = new HalfspaceList(rank);
        var last: Box[Halfspace] = null as Box[Halfspace];
        for (next:Halfspace in this) {
            if (last!=null && !next.isParallel(last to Halfspace))
                result.add(last to Halfspace);
            last = next to Box[Halfspace];
        }
        result.add(last to Halfspace);

        return result;
    }


    /**
     * Determine whether a halfspace is redundant by reductio ad
     * absurdum: assume the negation of H, and if the result is the
     * empty set then H is implied by the other halfspaces.
     *
     * This is guaranteed to remove redundant halfspaces, but it may
     * be expensive.
     */

    var isSimplified: boolean = false;

    def simplifyAll(): HalfspaceList {
        sort(); // not needed but helps w/ debugging to keep in sorted order
        if (isSimplified)
            return this;
        val result = new HalfspaceList(rank);
        var removed: Rail[boolean] = Rail.makeVar[boolean](size(), (nat)=>false); // XTENLANG-39 workaround
        for (var i: int = 0; i<size(); i++) {
            val h = get(i);
            val trial = new HalfspaceList(rank);
            for (var j: int = 0; j<size(); j++)
                if (!removed(j))
                    trial.add(i==j? h.complement() : get(j));
            if (!trial.isEmpty())
                result.add(h);
            else
                removed(i) = true;
        }
        result.isSimplified = true;
        return result;
    }


    /**
     * Apply Fourier-Motzkin Elimination to eliminate variable k:
     *
     * Copy each halfspace whose kth coefficient is already 0
     *
     * For each pair of halfspaces such that the kth coefficient is of
     * opposite sign, construct a new halfspace by adding the two
     * original halfspaces with appropriate positive multipliers to
     * obtain a halfspace with a kth coefficent of 0.
     *
     * The result is a set of halfspaces that describe the polyhedron
     * that is the projection of the polyhedron described by the
     * original halfspaces onto a rank-1 dimensional subspace obtained
     * by eliminating axis k
     */

    def eliminate(k: int, simplifyDegenerate: boolean): HalfspaceList {
        var result: HalfspaceList = new HalfspaceList(rank);
        for (var i: int = 0; i<size(); i++) {
            val ih = get(i);
            val ia = ih.as(k);
            if (ia==0) {
                result.add(ih);
            } else {
                for (var j: int = i+1; j<size(); j++) {
                    val jh = get(j);
                    val ja = jh.as(k);
                    val as = Rail.makeVar[int](rank+1);
                    if (ia>0 && ja<0) {
                        for (var l: int = 0; l<=rank; l++)
                            as(l) = ia*jh.as(l) - ja*ih.as(l);
                    } else if (ia<0 && ja>0) {
                        for (var l: int = 0; l<=rank; l++)
                            as(l) = ja*ih.as(l) - ia*jh.as(l);
                    }
                    val lim = simplifyDegenerate? rank : rank+1;
                    var degenerate: boolean = true;
                    for (var l: int = 0; l<lim; l++)
                        if (as(l)!=0)
                            degenerate = false;
                    if (!degenerate) {
                        var h: Halfspace = new Halfspace(as);
                        result.add(h);
                    }
                }
            }
        }
        result = result.simplifyParallel();
        return result;
    }


    /**
     * Support for constructing rectangular regions: determining
     * whether or not a cl is rectangular, and computing min/max along
     * each axis if it is.
     *
     * XXX cache these for efficiency during region construction
     * XXX assume halfspaces have been sorted and simplified - check/enforce
     * XXX rectMin/Max only work if isRect is true - check/enforce
     * XXX cache rectMin/rectMax/isZeroBased for performance
     */

    def isRect(): boolean {
        for (h:Halfspace in this) {
            if (!h.isRect())
                return false;
        }
        return true;
    }

    def rectMin(axis: int): int {

        for (h:Halfspace in this) {
            val a = h.as(axis);
            if (a < 0)
                return -h.as(rank()) / a;
        }

        var msg: String = "axis " + axis + " has no minimum";
        throw new UnboundedRegionException(msg);
    }
    
    def rectMax(axis: int): int {

        for (h:Halfspace in this) {
            val a = h.as(axis);
            if (a > 0)
                return -h.as(rank()) / a;
        }

        val msg = "axis " + axis + " has no maximum";
        throw new UnboundedRegionException(msg);
    }

    def rectMin(): Rail[int] {
        val min = Rail.makeVar[int](rank);
        for (var i: int = 0; i<min.length; i++)
            min(i) = rectMin(i);
        return min;
    }

    def rectMax(): Rail[int] {
        val max = Rail.makeVar[int](rank);
        for (var i: int = 0; i<max.length; i++)
            max(i) = rectMax(i);
        return max;
    }

    def isZeroBased(): boolean {
        if (!isRect())
            return false;
        try {
            for (var i: int = 0; i<rank; i++)
                if (rectMin(i)!=0)
                    return false;
        } catch (e: UnboundedRegionException) {
            return false;
        }
        return true;
    }

    def isBounded(): boolean {
        try {
            for (var i: int = 0; i<rank; i++) {
                rectMin(i);
                rectMax(i);
            }
        } catch (e: UnboundedRegionException) {
            return false;
        }
        return true;
    }



    /**
      * A set of halfspaces is empty iff, after eliminating all
      * variables with FME, we are left with a contradiction, i.e. a
      * halfspace k<=0 where k>0.
      */

    public def isEmpty(): boolean {

        // eliminate all variables
        var hl: HalfspaceList = this;
        for (var i: int = 0; i<rank; i++)
            hl = hl.eliminate(i, false);
    
        // look for contradictions
        for (var i: int = 0; i<hl.size(); i++) {
            val h = hl.get(i);
            if (h.as(rank)>0)
                return true;
        }
        return false;
    }


    /**
     *
     */

    public def printInfo(ps: PrintStream, label: String): void {
        ps.printf("%s\n", label);
        for (h:Halfspace in this) {
            ps.printf("    ");
            h.printInfo(ps);
        }

    }

    public def toString(): String {

        var s: String = "(";
        var first: boolean = true;

        for (h:Halfspace in this) {
            if (!first) s += " && ";
            s += h.toString();
            first = false;
        }

        s += ")";
        return s;
    }

}
