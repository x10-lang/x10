// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.PrintStream;


/**
 * A HalfspaceList is essentially a set of linear inequalisty
 * constraints represented as a constraint matrix. Each row is
 * represented as a Halfspace object. The constraint matrix represents
 * a set of points defined as the intersection of the halfspaces
 * represented by each Halfspace in the list, or equivalently, as the
 * set of points satisfying the conjunction of the linear inequalities
 * represented by each Halfspace object.
 *
 * @author bdlucas
 */

value class HalfspaceList(rank: int) {

    static type HalfspaceList(rank:nat) = HalfspaceList{self.rank==rank};
    static type HalfspaceListBuilder(rank:nat) = HalfspaceListBuilder{self.rank==rank};

    //
    // value
    //

    val halfspaces: ValRail[Halfspace];
    val isSimplified: boolean;

    def iterator() = halfspaces.iterator();


    /**
     * Low-level constructor. For greater convenience use HalfspaceListBuilder.
     */

    def this(rank:nat, halfspaces: ValRail[Halfspace], isSimplified:boolean):
        HalfspaceList(rank)
    {
        property(rank);
        this.halfspaces = halfspaces;
        this.isSimplified = isSimplified;
    }

    public def size():int = halfspaces.length;


    /**
     * Eliminate redundant parallel halfspaces. Since halfspaces
     * with equal coefficients are sorted in increasing order of
     * the constant (which is the least significant part of the
     * key) taking the last of a set of parallel halfspaces
     * captures the strongest halfspace.
     */

    def simplifyParallel(): HalfspaceList {

        if (size()==0)
            return this;

        val hlb = new HalfspaceListBuilder(rank);
        var last: Box[Halfspace] = null as Box[Halfspace];
        for (next:Halfspace in this) {
            if (last!=null && !next.isParallel(last to Halfspace))
                hlb.add(last to Halfspace);
            last = next to Box[Halfspace];
        }
        hlb.add(last to Halfspace);

        return hlb.toHalfspaceList();
    }


    /**
     * Determine whether a halfspace is redundant by reductio ad
     * absurdum: assume the negation of H, and if the result is the
     * empty set then H is implied by the other halfspaces.
     *
     * This is guaranteed to remove redundant halfspaces, but it may
     * be expensive.
     */

    def simplifyAll(): HalfspaceList {

        if (isSimplified)
            return this;

        val hlb = new HalfspaceListBuilder(rank);
        var removed: Rail[boolean] = Rail.makeVar[boolean](size(), (nat)=>false); // XTENLANG-39 workaround

        for (var i: int = 0; i<size(); i++) {
            val h = halfspaces(i);
            val trial = new HalfspaceListBuilder(rank);
            for (var j: int = 0; j<size(); j++)
                if (!removed(j))
                    trial.add(i==j? h.complement() : halfspaces(j));
            if (!trial.toHalfspaceList().isEmpty())
                hlb.add(h);
            else
                removed(i) = true;
        }

        return hlb.toHalfspaceList(true);
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
        val hlb = new HalfspaceListBuilder(rank);
        for (ih:Halfspace in this) {
            val ia = ih.as(k);
            if (ia==0) {
                hlb.add(ih);
            } else {
                for (jh:Halfspace in this) {
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
                        hlb.add(h);
                    }
                }
            }
        }
        return hlb.toHalfspaceList().simplifyParallel();
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

    def rectMin(): ValRail[int]
        = Rail.makeVal[int](rank, (i:nat)=>rectMin(i));

    def rectMax(): ValRail[int]
        = Rail.makeVal[int](rank, (i:nat)=>rectMax(i));

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

    def isEmpty(): boolean {

        // eliminate all variables
        var hl: HalfspaceList = this;
        for (var i: int = 0; i<rank; i++)
            hl = hl.eliminate(i, false);
    
        // look for contradictions
        for (h:Halfspace in hl)
            if (h.as(rank)>0)
                return true;
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
