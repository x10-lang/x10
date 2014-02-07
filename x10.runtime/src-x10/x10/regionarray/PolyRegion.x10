/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.regionarray;

import x10.io.Printer;
import x10.compiler.Incomplete;

/**
 * A PolyRegion represents a polyhedral region represented as the
 * intersection of a list of PolyRows. The halfspaces are stored in
 * a PolyMat object, which is essentially a constraint matrix
 * that defines the region. The PolyRegion object wraps a
 * HalfpaceList, adding some static factory methods for PolyRegions
 * and some methods such as region algebra that operate on
 * PolyMat objects.
 */
class PolyRegion extends Region {

    public val mat: PolyMat{self.rank==this.rank};

    public def isConvex(): Boolean {
        return true;
    }

    var size:Long = -1; // uninitialized
    public def size():Long {
    	if (size < 0) {
    	  var s:Long=0;
          val it= iterator();
          for (p:Point in this)
        	 s++;
          size=s;
    	}
        return size;     
    }

    @Incomplete public def indexOf(Point):Long {
        throw new UnsupportedOperationException();
    }


    public def iterator():Iterator[Point(rank)]
          = PolyScanner.make(mat).iterator(); 
  

    //
    // Region methods
    //

    public def intersection(t: Region(rank)): Region(rank) {

        if (t instanceof PolyRegion) {

            // start
            val that = t as PolyRegion; 
            val pmb = new PolyMatBuilder(rank);

            // these halfspaces
            for (r:PolyRow in this.mat)
                pmb.add(r);

            // those halfspaces
            for (r:PolyRow in that.mat)
                pmb.add(r);

            // done
            val pm = pmb.toSortedPolyMat(false);
            return PolyRegion.make(pm); 

        } else if (t instanceof RectRegion) {
        	return intersection((t as RectRegion).toPolyRegion());
        } else if (t instanceof RectRegion1D) {
                return intersection((t as RectRegion1D).toRectRegion().toPolyRegion() as Region(rank));
        }
        /*else if (t instanceof UnionRegion) {

            return (t as Region(rank)).intersection(this);

        } */  else {
            throw new UnsupportedOperationException("intersection(" + t/*.getClass().getName()*/ + ")");
        }
    }
                          
                          
    public def contains(that: Region(rank)): Boolean = 
    	computeBoundingBox().contains(that.computeBoundingBox());
     
    /**
     * Projection is computed by using FME to eliminate variables on
     * all but the axis of interest.
     */

    public def projection(axis:Long): Region(1) {
        var pm: PolyMat{self.rank==this.rank} = mat;
        for (var k:Int = 0n; k<rank; k++)
            if (k!=(axis as Int))
                pm = pm.eliminate(k, true);
        return Region.makeRectangular(pm.rectMin(axis as Int), pm.rectMax(axis as Int));// as Region(1);
    }

    /**
     * Eliminate the ith axis.
     */

    // XXX add a test case for this; also for projection!
  
    public def eliminate(axis:Long): Region/*(rank1)*/ {
        val pm = mat.eliminate(axis as Int, true); 
        val result = PolyRegion.make(pm);
        return result /*as Region(rank1)*/;
    }

    /**
     * Cartesian product requires copying the halfspace matrices into
     * the result blockwise
     */

    public def product(r: Region): Region{self != null} {
        if (!(r instanceof PolyRegion))
            throw new UnsupportedOperationException("product(" + r/*.getClass().getName()*/ + ")");
        val that = r as PolyRegion;
        val pmb = new PolyMatBuilder(this.rank + that.rank);
        copy(pmb, this.mat, 0n);         // padded w/ 0s on the right
        copy(pmb, that.mat, this.rank as Int); // padded w/ 0s on the left
        val pm = pmb.toSortedPolyMat(false);
        return PolyRegion.make(pm);
    }

    private static def copy(tt: PolyMatBuilder, ff: PolyMat, offset:Int): void {
        for (r:PolyRow in ff) {
            val f = r;
            val t = new Rail[Int](tt.rank+1);
            for (var i:Int = 0n; i<ff.rank; i++)
                t(offset+i) = f(i);
            t(tt.rank) = f(ff.rank as Int);
            tt.add(new PolyRow(t));
        }
    }


    public def translate(v: Point(rank)): Region(rank) {
        val pmb = new PolyMatBuilder(this.rank);
        translate(pmb, this.mat, v);
        val pm = pmb.toSortedPolyMat(false);
        return PolyRegion.make(pm);
    }

    private static def translate(tt: PolyMatBuilder, ff: PolyMat, v: Point(ff.rank)): void {
        for (r:PolyRow in ff) {
            val f = r;
            val t = new Rail[Int](ff.rank+1);
            var s:Int = 0n;
            for (var i:Int = 0n; i<ff.rank; i++) {
                t(i) = f(i);
                s += f(i)*v(i);
            }
            t(ff.rank) = f(ff.rank as Int) - s;
            tt.add(new PolyRow(t));
        }
    }


    /**
     * -H0 || -H1 && H0 || -H2 && H1 && H0 || ...
     */

   /* public def complement(): Region(rank) {
        val prlb = new PolyRegionListBuilder(rank);

        for (r:PolyRow in mat) {
            val ri = r as PolyRow(rank); // XXXX
            val pmb = new PolyMatBuilder(rank);
            pmb.add(ri.complement());
            for (rj:PolyRow in mat) {
                if (rj==ri)
                    break;
                pmb.add(rj);
            }
            val pm = pmb.toSortedPolyMat(false);
            val region = PolyRegion.make(pm);
            prlb.add(region as Region(rank)); // XXXX
        }

        return new UnionRegion(prlb as PolyRegionListBuilder{self.rank == this.rank}); 
    }
*/
    public def isEmpty(): Boolean {
        val tmp = mat.isEmpty();
        return tmp;
    }

    protected def computeBoundingBox(): Region(rank){self.rect} {
        val min = new Rail[Long](rank);
        val max = new Rail[Long](rank);
        var pm: PolyMat{self.rank==this.rank} = mat;
        for (var axis:Int = 0n; axis<rank; axis++) {
            var x: PolyMat = pm;
            for (var k:Int = axis+1n; k<rank; k++)
                x = x.eliminate(k, true);
            min(axis) = x.rectMin(axis);
            max(axis) = x.rectMax(axis);
            pm = pm.eliminate(axis, true);
        }
        return Region.makeRectangular(min, max) as Region(rank){self.rect};
    }


    /**
     * point
     */

    public def contains(p: Point): Boolean {

        for (r:PolyRow in mat) {
            if (!r.contains(p))
                return false;
        }

        return true;
    }



    /**
     * lower==1 and lower==1 include the diagonal
     * lower==size and upper==size includes entire size x size square
     *
     * col-colMin >= row-rowMin - (lower-1)
     * col-colMin <= row-rowMin + (upper-1)
     *
     * col-row >= colMin-rowMin - (lower-1)
     * col-row <= colMin-rowMin + (upper-1)
     */

    private static ROW:Int = PolyMatBuilder.X(0n);
    private static COL:Int = PolyMatBuilder.X(1n);

    public static def makeBanded(rowMin:Int, colMin:Int, rowMax:Int, colMax:Int, upper:Int, lower:Int): Region(2) {
        val pmb = new PolyMatBuilder(2);
        pmb.add(ROW, pmb.GE, rowMin);
        pmb.add(ROW, pmb.LE, rowMax);
        pmb.add(COL, pmb.GE, colMin);
        pmb.add(COL, pmb.LE, colMax);
        pmb.add(COL-ROW, pmb.GE, colMin-rowMin-(lower-1n));
        pmb.add(COL-ROW, pmb.LE, colMin-rowMin+(upper-1n));
        val pm = pmb.toSortedPolyMat(false);
        return PolyRegion.make(pm);
    }

    public static def makeBanded(size:Int, upper:Int, lower:Int): Region(2) {
        return makeBanded(0n, 0n, size-1n, size-1n, upper, lower);
    }

    public static def makeUpperTriangular2(rowMin:Int, colMin:Int, size:Int): Region(2) {
        val pmb = new PolyMatBuilder(2n);
        pmb.add(ROW, pmb.GE, rowMin);
        pmb.add(COL, pmb.LE, colMin+size-1n);
        pmb.add(COL-ROW, pmb.GE, colMin-rowMin);
        val pm = pmb.toSortedPolyMat(true);
        return PolyRegion.make(pm) as Region(2);
    }

    public static def makeLowerTriangular2(rowMin:Int, colMin:Int, size:Int): Region(2) {
        val pmb = new PolyMatBuilder(2n);
        pmb.add(COL, pmb.GE, colMin);
        pmb.add(ROW, pmb.LE, rowMin+size-1n);
        pmb.add(ROW-COL, pmb.GE, rowMin-colMin);
        val pm = pmb.toSortedPolyMat(true);
        return PolyRegion.make(pm) as Region(2);
    }



    /**
     * here's where we examine the halfspaces and generate
     * special-case subclasses, such as RectRegion, for efficiency
     */

    public static def make(pm: PolyMat): Region(pm.rank){self != null} {
        if (pm.isEmpty()) {
            return new EmptyRegion(pm.rank);
        } else {
            return new PolyRegion(pm, false);
        }
    }

    protected def this(pm: PolyMat, hack198:Boolean): PolyRegion(pm.rank) {

        super(pm.rank, pm.isRect(), pm.isZeroBased());

        // simplifyAll catches more (all) stuff, but may be expensive.
        //this.mat = pm.simplifyParallel();
        this.mat = pm.simplifyAll() as PolyMat{self.rank==this.rank}; // safe..

        // cache stuff up front
        // vj: no dont. THis creates a cycle of global values
        // Not handled in 2.0
	//        cache = new Cache(this, hack198);
    }

    public def min(): (Long)=>Long {
        val t = boundingBox().min();
        return (i:Long)=>t(i as Int);
    }

    public def max(): (Long)=>Long {
        val t = boundingBox().max();
        return (i:Long)=>t(i as Int);
    }


    //
    // debugging
    //

    public def printInfo(out: Printer): void {
        mat.printInfo(out, /*this.getClass().getName()*/this.toString());
    }

    public def toString(): String {
        return mat.toString();
    }

}
public type PolyRegion(rank:Long) = PolyRegion{self.rank==rank};