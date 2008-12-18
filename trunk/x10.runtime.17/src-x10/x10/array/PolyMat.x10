// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.Printer;
import x10.array.mat.*;


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

public value class PolyMat(rank: int) extends Mat[PolyRow] {

    static type PolyMat(rank:nat) = PolyMat{self.rank==rank};
    static type PolyMatBuilder(rank:nat) = PolyMatBuilder{self.rank==rank};

    //
    // value
    //

    val isSimplified: boolean;


    /**
     * Low-level constructor. For greater convenience use PolyMatBuilder.
     */

    public def this(rank:nat, halfspaces: ValRail[PolyRow], isSimplified:boolean):
        PolyMat(rank)
    {
        super(rank+1, halfspaces);
        property(rank);
        this.isSimplified = isSimplified;
    }

    public def this(rows:nat, cols:nat, init:(i:nat,j:nat)=>int) {
        super(rows, cols, (i:nat)=>new PolyRow(cols, (j:nat)=>init(i,j)));
        property(cols-1);
        this.isSimplified = true; // XXX
    }

    public def this(rows:nat, cols:nat, init:ValRail[ValRail[int]]) {
        this(rows, cols, (i:nat,j:nat)=>init(i)(j));
    }


    /**
     * Eliminate redundant parallel halfspaces. Since halfspaces
     * with equal coefficients are sorted in increasing order of
     * the constant (which is the least significant part of the
     * key) taking the last of a set of parallel halfspaces
     * captures the strongest halfspace.
     */

    def simplifyParallel(): PolyMat {

        if (rows==0)
            return this;

        val hlb = new PolyMatBuilder(rank);
        var last: Box[PolyRow] = null as Box[PolyRow];
        for (next:PolyRow in this) {
            if (last!=null && !next.isParallel(last to PolyRow))
                hlb.add(last to PolyRow);
            last = next to Box[PolyRow];
        }
        hlb.add(last to PolyRow);

        return hlb.toPolyMat();
    }


    /**
     * Determine whether a halfspace is redundant by reductio ad
     * absurdum: assume the negation of H, and if the result is the
     * empty set then H is implied by the other halfspaces.
     *
     * This is guaranteed to remove redundant halfspaces, but it may
     * be expensive.
     */

    def simplifyAll(): PolyMat {

        if (isSimplified)
            return this;

        val hlb = new PolyMatBuilder(rank);
        var removed: Rail[boolean] = Rail.makeVar[boolean](rows, (nat)=>false); // XTENLANG-39 workaround

        for (var i: int = 0; i<rows; i++) {
            val h = this(i);
            val trial = new PolyMatBuilder(rank);
            for (var j: int = 0; j<rows; j++)
                if (!removed(j))
                    trial.add(i==j? h.complement() : this(j));
            if (!trial.toPolyMat().isEmpty())
                hlb.add(h);
            else
                removed(i) = true;
        }

        return hlb.toPolyMat(true);
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

    def eliminate(k: int, simplifyDegenerate: boolean): PolyMat {
        val hlb = new PolyMatBuilder(rank);
        for (ih:PolyRow in this) {
            val ia = ih(k);
            if (ia==0) {
                hlb.add(ih);
            } else {
                for (jh:PolyRow in this) {
                    val ja = jh(k);
                    val as = Rail.makeVar[int](rank+1);
                    if (ia>0 && ja<0) {
                        for (var l: int = 0; l<=rank; l++)
                            as(l) = ia*jh(l) - ja*ih(l);
                    } else if (ia<0 && ja>0) {
                        for (var l: int = 0; l<=rank; l++)
                            as(l) = ja*ih(l) - ia*jh(l);
                    }
                    val lim = simplifyDegenerate? rank : rank+1;
                    var degenerate: boolean = true;
                    for (var l: int = 0; l<lim; l++)
                        if (as(l)!=0)
                            degenerate = false;
                    if (!degenerate) {
                        var h: PolyRow = new PolyRow(as);
                        hlb.add(h);
                    }
                }
            }
        }
        return hlb.toPolyMat().simplifyParallel();
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
        for (h:PolyRow in this) {
            if (!h.isRect())
                return false;
        }
        return true;
    }

    def rectMin(axis: int): int {

        for (h:PolyRow in this) {
            val a = h(axis);
            if (a < 0)
                return -h(rank()) / a;
        }

        var msg: String = "axis " + axis + " has no minimum";
        throw new UnboundedRegionException(msg);
    }
    
    def rectMax(axis: int): int {

        for (h:PolyRow in this) {
            val a = h(axis);
            if (a > 0)
                return -h(rank()) / a;
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
        var hl: PolyMat = this;
        for (var i: int = 0; i<rank; i++)
            hl = hl.eliminate(i, false);
    
        // look for contradictions
        for (h:PolyRow in hl)
            if (h(rank)>0)
                return true;
        return false;
    }


    /**
     * Matrix multiplication.
     */

    public def $times(that: PolyMat) {
        return new PolyMat(this.rows, that.cols, (i:nat,j:nat) => {
            var sum:int = 0;
            for (var k:int=0; k<this.cols; k++)
                sum += this(i)(k)*that(k)(j);
            return sum;
        });
    }


    /**
     * Matrix times vector.
     */

    public def $times(p:Point):Point {
        return Point.make(p.rank, (i:nat)=> {
            var sum:int = this(i)(p.rank);
            for (var j:int=0; j<p.rank; j++)
                sum += p(j)*this(i)(j);
            return sum;
        });
    }

    /**
     * Concatenate matrices
     */

    public def $or(that: PolyMat) {
        return new PolyMat(this.rows+that.rows, this.cols, (i:nat,j:nat) =>
            i<this.rows? this(i)(j) : that(i-this.rows)(j)
        );
    }


    /**
     *
     */

    public def printInfo(ps: Printer, label: String): void {
        ps.printf("%s\n", label);
        for (h:PolyRow in this) {
            ps.printf("    ");
            h.printInfo(ps);
        }

    }

    public def toString(): String {

        var s: String = "(";
        var first: boolean = true;

        for (h:PolyRow in this) {
            if (!first) s += " && ";
            s += h.toString();
            first = false;
        }

        s += ")";
        return s;
    }

}
