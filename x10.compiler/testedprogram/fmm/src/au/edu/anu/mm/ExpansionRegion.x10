/*
 * This file is part of ANUChem.
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 * (C) Copyright Josh Milthorpe 2010.
 */
package au.edu.anu.mm;

public class ExpansionRegion extends Region  {
    // XTENLANG-49
    static type ExpansionRegion(rank:Int) = ExpansionRegion{self.rank==rank};
    val p : Int;

    /**
     * Constructs a new ExpansionRegion for a multipole
     * or local expansion of the given dimension.
     * @param p the dimension of the expansion
     */
    public def this(p : Int): ExpansionRegion(2) {
        super(2, false, true);
        this.p = p;
    }

    public def isConvex() : Boolean {
        return true;
    }

    public def isEmpty() : Boolean {
        return false;
    }

    public def min():(int)=>int = (i:int)=> {
        if (i==0) return 0;
        else if (i==1) return -p;
        else throw new ArrayIndexOutOfBoundsException("min: "+i+" is not a valid rank for "+this);
    };

    public def max():(int)=>int = (i:int)=> {
        if (i==0) return p;
        else if (i==1) return p;
        else throw new ArrayIndexOutOfBoundsException("max: "+i+" is not a valid rank for "+this);
    };

    /**
     * Returns the number of points in this region.
     * These expansions have a peculiarly shaped region(abs(x1)<=x0 && 0<=x0<=p)
     * (X10 gives it as (x0+x1>=0 && x0-x1>=0 && x0<=3),
     * which gives a size of (p+1)^2.
     */
    public def size() : Int { 
        return (p+1) * (p+1);
    }

    public def contains(p: Point): boolean {
        if (p.rank == 2) {
            return (p(0) >= 0 && p(0) <= this.p && Math.abs(p(1)) <= p(0));
        }
        throw new UnsupportedOperationException("contains(" + p + ")");
    }

    public def contains(r:Region(rank)): boolean {
        if (r instanceof ExpansionRegion && (r as ExpansionRegion).p == this.p)
            return true;
        throw new UnsupportedOperationException("contains(Region)");
    }

    public def complement(): Region(rank) {
        // TODO
        throw new UnsupportedOperationException("complement()");
    }

    public def intersection(t: Region(rank)): Region(rank) {
        // TODO
        throw new UnsupportedOperationException("intersection()");
    }

    public def product(that: Region): Region{self!=null} {
        // TODO
        throw new UnsupportedOperationException("product()");
    }

    public def translate(v: Point(rank)): Region(rank) {
        // TODO
        throw new UnsupportedOperationException("translate()");
    }

    public def projection(axis: int): Region(1) {
        switch (axis) {
            case 0:
                return 0..p;
            case 1:
                return -p..p;
            default:
                throw new UnsupportedOperationException("projection(" + axis + ")");
        }
    }

    public def eliminate(axis: int): Region(1) {
        switch (axis) {
            case 0:
                return (-p..p);
            case 1:
                return (0..p);
            default:
                throw new UnsupportedOperationException("projection(" + axis + ")");
        }
    }

    public def indexOf(pt:Point) {
	    if (pt.rank != 2) return -1;
        return (pt(0) * pt(0)) + pt(1);
    }

    public def boundingBox(): Region(rank) {
        return (0..p) * (-p..p);
    }

    protected def computeBoundingBox(): Region(rank) {
        return (0..p) * (-p..p);
    }

    public def iterator(): Iterator[Point(rank)] {
        return new ExpansionRegionIterator(this) as Iterator[Point(rank)];
    }

    private class ExpansionRegionIterator implements Iterator[Point(2)] {
        val p : Int;
        var l : Int;
        var m : Int;

        def this(val r : ExpansionRegion) {
            this.p = r.p;
            this.l = 0;
            this.m = 0;
        }

        final public def hasNext(): boolean {
            if (l <= p && m <= l) return true;
            else return false;
        }

        final public def next(): Point(2) {
            nextPoint : Point(2) = Point.make(l,m);
            if (m < l) m++;
            else {
                l++;
                m = -l; 
            }
            return nextPoint;
        } 
    }

    public def toString(): String {
        return "ExpansionRegion (p = " + p + ")";
    }
}
