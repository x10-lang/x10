// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.Printer;


/**
 * A PolyMat is a set of linear inequalisty constraints represented as
 * a constraint matrix. Each row is represented as a PolyRow
 * object. The constraint matrix represents a set of points defined as
 * the intersection of the halfspaces represented by each PolyRow in
 * the PolyMat, or equivalently, as the set of points satisfying the
 * conjunction of the linear inequalities represented by each PolyRow
 * object.
 *
 * @author bdlucas
 */

public class PolyMat(rank: int) extends Mat[PolyRow] {

    static type PolyMat(rank:nat) = PolyMat{self.rank==rank};
    static type PolyMatBuilder(rank:nat) = PolyMatBuilder{self.rank==rank};

    //
    // value
    //

    private global val isSimplified: boolean;


    /**
     * Low-level constructor. For greater convenience use PolyMatBuilder.
     */

    public def this(rows: nat, cols: nat, init: (i:nat,j:nat)=>int, isSimplified:boolean) {
        super(rows, cols, ValRail.make[PolyRow](rows, (i:nat)=>new PolyRow(cols, (j:nat)=>init(i,j))));
        property(cols-1);
        this.isSimplified = isSimplified;
    }


    /**
     * Eliminate redundant parallel halfspaces. Since halfspaces
     * with equal coefficients are sorted in increasing order of
     * the constant (which is the least significant part of the
     * key) taking the last of a set of parallel halfspaces
     * captures the strongest halfspace.
     */

    global def simplifyParallel(): PolyMat{self.rank==this.rank} {

        if (rows==0)
            return this;

        val pmb = new PolyMatBuilder(rank);
        var last: Box[PolyRow] = null;
        for (next:PolyRow in this) {
            if (last!=null && !next.isParallel(last as PolyRow))
                pmb.add(last as PolyRow);
            last = new Box[PolyRow](next);
        }
        pmb.add(last.value);

        return pmb.toSortedPolyMat(false);
    }


    /**
     * Determine whether a halfspace is redundant by reductio ad
     * absurdum: assume the negation of H, and if the result is the
     * empty set then H is implied by the other halfspaces.
     *
     * This is guaranteed to remove redundant halfspaces, but it may
     * be expensive.
     */

    public global def simplifyAll(): PolyMat{self.rank==this.rank} {

        if (isSimplified)
            return this;

        val pmb = new PolyMatBuilder(rank);
        var removed:Rail[boolean]! = Rail.make[boolean](rows, (nat)=>false); // XTENLANG-39 workaround

        for (var i: int = 0; i<rows; i++) {
            val r = this(i);
            val trial = new PolyMatBuilder(rank);
            for (var j: int = 0; j<rows; j++)
                if (!removed(j))
                    trial.add(i==j? r.complement() : this(j));
            if (!trial.toSortedPolyMat(false).isEmpty())
                pmb.add(r);
            else
                removed(i) = true;
        }

        return pmb.toSortedPolyMat(true);
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

    global def eliminate(k: int, simplifyDegenerate: boolean): PolyMat{self.rank==this.rank} {
        val pmb = new PolyMatBuilder(rank);
        for (ir:PolyRow in this) {
            val ia = ir(k);
            if (ia==0) {
                pmb.add(ir);
            } else {
                for (jr:PolyRow in this) {
                    val ja = jr(k);
                    val as = Rail.make[int](rank+1);
                    if (ia>0 && ja<0) {
                        for (var l: int = 0; l<=rank; l++)
                            as(l) = ia*jr(l) - ja*ir(l);
                    } else if (ia<0 && ja>0) {
                        for (var l: int = 0; l<=rank; l++)
                            as(l) = ja*ir(l) - ia*jr(l);
                    }
                    val lim = simplifyDegenerate? rank : rank+1;
                    var degenerate: boolean = true;
                    for (var l: int = 0; l<lim; l++)
                        if (as(l)!=0)
                            degenerate = false;
                    if (!degenerate) {
                        var r: PolyRow = new PolyRow(as);
                        pmb.add(r);
                    }
                }
            }
        }
        return pmb.toSortedPolyMat(false).simplifyParallel();
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

    global def isRect(): boolean {
        for (r:PolyRow in this) {
            if (!r.isRect())
                return false;
        }
        return true;
    }

    global def rectMin(axis: int): int {

        for (r:PolyRow in this) {
            val a = r(axis);
            if (a < 0)
                return -r(rank()) / a;
        }

        var msg: String = "axis " + axis + " has no minimum";
        throw new UnboundedRegionException(msg);
    }
    
    global def rectMax(axis: int): int {

        for (r:PolyRow in this) {
            val a = r(axis);
            if (a > 0)
                return -r(rank()) / a;
        }

        val msg = "axis " + axis + " has no maximum";
        throw new UnboundedRegionException(msg);
    }

    global def rectMin(): ValRail[int]
        = ValRail.make[int](rank, (i:nat)=>rectMin(i));

    global def rectMax(): ValRail[int]
        = ValRail.make[int](rank, (i:nat)=>rectMax(i));

    global def isZeroBased(): boolean {
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

    global def isBounded(): boolean {
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

    global def isEmpty(): boolean {
        // eliminate all variables
        var pm: PolyMat = this;
        for (var i: int = 0; i<rank; i++)
            pm = pm.eliminate(i, false);
    
        // look for contradictions
        for (r:PolyRow in pm) {
            if (r(rank)>0)
                return true;
        }

        return false;
    }


    /**
     * Matrix multiplication.
     */

    public global operator this * (that: XformMat): PolyMat {
        return new PolyMat(this.rows, that.cols, (i:nat,j:nat) => {
            var sum:int = 0;
            for (var k:int=0; k<this.cols; k++)
                sum += this(i)(k)*that(k)(j);
            return sum;
        }, true);
    }


    /**
     * Concatenate matrices
     */

    public global operator this || (that: PolyMat) {
        val pmb = new PolyMatBuilder(rank);
        for (r:PolyRow in this)
            pmb.add(r);
        for (r:PolyRow in that)
            pmb.add(r);
        return pmb.toSortedPolyMat(false);
    }


    public global def toString(): String {

        var s: String = "(";
        var first: boolean = true;

        for (r:PolyRow in this) {
            if (!first) s += " && ";
            s += r.toString();
            first = false;
        }

        s += ")";
        return s;
    }

}
