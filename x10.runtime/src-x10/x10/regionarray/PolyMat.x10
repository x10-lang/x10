/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2015.
 */

package x10.regionarray;

import x10.io.Printer;


/**
 * A PolyMat is a set of linear inequalisty constraints represented as
 * a constraint matrix. Each row is represented as a PolyRow
 * object. The constraint matrix represents a set of points defined as
 * the intersection of the halfspaces represented by each PolyRow in
 * the PolyMat, or equivalently, as the set of points satisfying the
 * conjunction of the linear inequalities represented by each PolyRow
 * object.
 */
class PolyMat(rank:Long) extends Mat[PolyRow] {

    //
    // value
    //

    private val isSimplified: Boolean;


    /**
     * Low-level constructor. For greater convenience use PolyMatBuilder.
     */

    public def this(rows:Int, cols:Int, init: (i:Int,j:Int)=>Int, isSimplified:Boolean) {
        super(rows, cols, new Rail[PolyRow](rows, (i:Long)=>new PolyRow(cols, (j:Int)=>init(i as Int,j))));
        val cols1 = cols-1n;
        property(cols1 as Long);
        this.isSimplified = isSimplified;
    }


    /**
     * Eliminate redundant parallel halfspaces. Since halfspaces
     * with equal coefficients are sorted in increasing order of
     * the constant (which is the least significant part of the
     * key) taking the last of a set of parallel halfspaces
     * captures the strongest halfspace.
     */

    def simplifyParallel(): PolyMat(rank) {

        if (rows==0n)
            return this;

        val pmb = new PolyMatBuilder(rank);
        var last: PolyRow = null;
        for (next:PolyRow in this) {
            if (last!=null && !next.isParallel(last))
                pmb.add(last);
            last = next;
        }
        pmb.add(last);

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

    public def simplifyAll(): PolyMat(rank) {

        if (isSimplified)
            return this;

        val pmb = new PolyMatBuilder(rank);
        val removed = new Rail[Boolean](rows, false);

        for (var i:Int = 0n; i<rows; i++) {
            val r = this(i);
            val trial = new PolyMatBuilder(rank);
            for (var j:Int = 0n; j<rows; j++)
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

    def eliminate(k:Int, simplifyDegenerate: Boolean): PolyMat(rank) {
        val pmb = new PolyMatBuilder(rank);
        for (ir:PolyRow in this) {
            val ia = ir(k);
            if (ia==0n) {
                pmb.add(ir);
            } else {
                for (jr:PolyRow in this) {
                    val ja = jr(k);
                    val as_ = new Rail[Int](rank+1n);
                    if (ia>0n && ja<0n) {
                        for (var l:Int = 0n; l<=rank; l++)
                            as_(l) = ia*jr(l) - ja*ir(l);
                    } else if (ia<0n && ja>0n) {
                        for (var l:Int = 0n; l<=rank; l++)
                            as_(l) = ja*ir(l) - ia*jr(l);
                    }
                    val lim = simplifyDegenerate? rank : rank+1n;
                    var degenerate: Boolean = true;
                    for (var l:Int = 0n; l<lim; l++)
                        if (as_(l)!=0n)
                            degenerate = false;
                    if (!degenerate) {
                        var r: PolyRow = new PolyRow(new Rail[Int](as_.size, (i:Long)=>as_(i)));
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

    def isRect(): Boolean {
        for (r:PolyRow in this) {
            if (!r.isRect())
                return false;
        }
        return true;
    }

    def rectMin(axis:Int):Int {

        for (r:PolyRow in this) {
            val a = r(axis);
            if (a < 0)
                return -r(rank as Int) / a;
        }

        var msg: String = "axis " + axis + " has no minimum";
        throw new UnboundedRegionException(msg);
    }
    
    def rectMax(axis:Int):Int {

        for (r:PolyRow in this) {
            val a = r(axis);
            if (a > 0)
                return -r(rank as Int) / a;
        }

        val msg = "axis " + axis + " has no maximum";
        throw new UnboundedRegionException(msg);
    }

    def rectMin() = new Rail[Int](rank, (i:Long)=>rectMin(i as Int));

    def rectMax() = new Rail[Int](rank, (i:Long)=>rectMax(i as Int));

    def isZeroBased(): Boolean {
        if (!isRect())
            return false;
        try {
            for (var i:Int = 0n; i<rank; i++)
                if (rectMin(i)!=0n)
                    return false;
        } catch (e: UnboundedRegionException) {
            return false;
        }
        return true;
    }

    def isBounded(): Boolean {
        try {
            for (var i:Int = 0n; i<rank; i++) {
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

    def isEmpty(): Boolean {
        // eliminate all variables
        var pm: PolyMat = this;
        for (var i:Int = 0n; i<rank; i++)
            pm = pm.eliminate(i, false);
    
        // look for contradictions
        for (r:PolyRow in pm) {
            if (r(rank as Int)>0)
                return true;
        }

        return false;
    }


    /**
     * Concatenate matrices
     */

    public operator this || (that: PolyMat) {
        val pmb = new PolyMatBuilder(rank);
        for (r:PolyRow in this)
            pmb.add(r);
        for (r:PolyRow in that)
            pmb.add(r);
        return pmb.toSortedPolyMat(false);
    }


    public def toString(): String {

        var s: String = "(";
        var first: Boolean = true;

        for (r:PolyRow in this) {
            if (!first) s += " && ";
            s += r.toString();
            first = false;
        }

        s += ")";
        return s;
    }

}
public type PolyMat(rank:Long) = PolyMat{self.rank==rank};