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

import x10.io.*;


/**
 * Here's the general scheme for the information used in scanning,
 * illustrated for a region of rank r=4. Each axis Xi is bounded by
 * two sets of halfspaces, min[i] and max[i], obtained from the
 * region halfspaces by FME (resulting in the 0 coefficients as
 * shown). Computing the bounds for Xi requires substituting X0 up to
 * Xi-1 into each halfspace (as shown for the min[i]s) and taking
 * mins and maxes.
 *
 *           0   1   2   r-1
 * 
 * min[0]
 *           A0  0   0   0   B   X0 bounded by B / A0
 *           A0  0   0   0   B   X0 bounded by B / A0
 *           ...                 ...
 * 
 * min[1]
 *           A0  A1  0   0   B   X1 bounded by (B+A0*X0) / A1
 *           A0  A1  0   0   B   X1 bounded by (B+A0*X0) / A1
 *           ...                 ...
 * 
 * min[2]
 *           A0  A1  A2  0   B   X2 bounded by (B+A0*X0+A1*X1) / A2
 *           A0  A1  A2  0   B   X2 bounded by (B+A0*X0+A1*X1) / A2
 *           ...                 ...
 * 
 * min[3]
 *           A0  A1  A2  A3  B   X3 bounded by (B+A0*X0+A1*X1+A2*X2) / A3
 *           A0  A1  A2  A3  B   X3 bounded by (B+A0*X0+A1*X1+A2*X2) / A3
 *           ...                 ...
 *
 * In the innermost loop the bounds for X3 could be computed by
 * substituting the known values of X0 through X2 into each
 * halfspace. However, part of that computation can be pulled out of
 * the inner loop by keeping track for each halfspace in in min[k]
 * and each constraint in max[k] a set of partial sums of the form
 *
 *     minSum[0] = B
 *     minSum[1] = B+A0*X0
 *     ...
 *     minSum[k] = B+A0*X0+A1*X1+...+Ak-1*Xk-1
 *
 * (and similiarly for maxSum) and updating each partial sum
 * minSum[i+1] (and similarly for maxSum[i+1]) every time Xi changes
 * by
 *
 *     minSum[i+1] := sum[i] + Ai*Xi
 *
 * The loop bounds for Xk are then obtained by computing mins and
 * maxes over the sum[k]/Ak for the halfspaces in elim[k].
 */
final class PolyScanner(rank:Long)/*(C:PolyMat)*/ {

     public val C: PolyMat;

  //  public val rank:Int;

    private val myMin: Array[VarMat]{self.rank==1,self.zeroBased,self.rect,self.rail};
    private val myMax: Array[VarMat]{self.rank==1,self.zeroBased,self.rect,self.rail};
    private val minSum: Array[VarMat]{self.rank==1,self.zeroBased,self.rect,self.rail};
    private val maxSum: Array[VarMat]{self.rank==1,self.zeroBased,self.rect,self.rail};

    private val parFlags: Array[Boolean]{self.rank==1,self.zeroBased,self.rect,self.rail};
    private val min2: Array[Array[PolyRow]{self.rank==1,self.zeroBased,self.rect,self.rail}]{self.rank==1,self.zeroBased,self.rect,self.rail};
    private val max2: Array[Array[PolyRow]{self.rank==1,self.zeroBased,self.rect,self.rail}]{self.rank==1,self.zeroBased,self.rect,self.rail};

    public static def make(pm:PolyMat):PolyScanner{self.rank==pm.rank} {
	val x = new PolyScanner(pm);
        x.init();
	return x;
    }

    private def this(pm:PolyMat):PolyScanner{self.rank==pm.rank} {
	property(pm.rank);
        var pm0:PolyMat = pm.simplifyAll();
       
        this.C = pm;
        val r = pm0.rank;
        val n = new Array[VarMat](r);
        myMin = n;
        val x = new Array[VarMat](r);
        myMax = x;
        val nSum = new Array[VarMat](r);
        minSum = nSum;
        val xSum = new Array[VarMat](r);
        maxSum = xSum;
        val n2 = new Array[Array[PolyRow]{self.rank==1,self.zeroBased,self.rect,self.rail}](r);
        min2 = n2;
        val x2 = new Array[Array[PolyRow]{self.rank==1,self.zeroBased,self.rect,self.rail}](r);
        max2 = x2;
        //printInfo(Console.OUT);

        parFlags = new Array[Boolean](r);
    }

    private def init() {
	var pm:PolyMat =C;
        init(pm, (rank as Int)-1n);
        for (var k:Int = (rank as Int)-2n; k>=0n; k--) {
            pm = pm.eliminate(k+1n, true);
            init(pm, k);
        }
    }

    final private def init(pm:PolyMat, axis:Int) {

        //pm.printInfo(Console.OUT, "pm for axis " + axis);

        // count
        var imin:Int = 0n;
        var imax:Int = 0n;
        for (r:PolyRow in pm) {
            if (r(axis)<0n) imin++;
            if (r(axis)>0n) imax++;
        }

        // complain if unbounded
        if (imin==0n || imax==0n) {
            val m = imin==0n? "minimum" : "maximum";
            val msg = "axis " + axis + " has no " + m;
            throw new UnboundedRegionException(msg);
        }

        // allocate
        myMin(axis) = new VarMat(imin, axis+1n);
        myMax(axis) = new VarMat(imax, axis+1n);
        minSum(axis) = new VarMat(imin, axis+1n);
        maxSum(axis) = new VarMat(imax, axis+1n);
        min2(axis) = new Array[PolyRow](imin);
        max2(axis) = new Array[PolyRow](imax);

        // fill in
        imin=0n; imax=0n;
        for (r:PolyRow in pm) {
            if (r(axis)<0n) {
                for (var i:Int = 0n; i<=axis; i++)
                    myMin(axis)(imin)(i) = r(i);
                minSum(axis)(imin)(0n) = r(rank as Int);
                min2(axis)(imin) = r;
                imin++;
            }
            if (r(axis)>0n) {
                for (var i:Int = 0n; i<=axis; i++)
                    myMax(axis)(imax)(i) = r(i);
                maxSum(axis)(imax)(0n) = r(rank as Int);
                max2(axis)(imax) = r;
                imax++;
            }
        }

        /*
        min(axis).printInfo(Console.OUT, "min for axis " + axis);
        Console.OUT.println("min2 for axis " + axis);
        for (var i:Int=0; i<min2(axis).length; i++)
            min2(axis)(i).printInfo(Console.OUT, i);
        */
    }

    // todo: Message: Semantic Error: set(axis: x10.lang.Int, v: x10.lang.Int): void in x10.regionarray.PolyScanner cannot override set(axis: x10.lang.Int, v: x10.lang.Int): void in x10.regionarray.PolyScanner; overridden method is final
    // Duplicate method "method final public x10.regionarray.PolyScanner.set(axis:x10.lang.Int,v:x10.lang.Int): void"; previous declaration at C:\cygwin\home\Yoav\intellij\sourceforge\x10.runtime\src-x10\x10.regionarray\PolyScanner.x10:176,5-178,5.
    final public operator this(v:Int)=(axis:Int): void { set(axis,v); }

    final public def set(axis:Int, v:Int):void {
        for (var k:Int = axis+1n; k<rank; k++)
            for (var l:Int = 0n; l<minSum(k).rows; l++)
                minSum(k)(l)(axis+1n) = myMin(k)(l)(axis)*v + minSum(k)(l)(axis);
        for (var k:Int = axis+1n; k<rank; k++)
            for (var l:Int = 0n; l<maxSum(k).rows; l++)
                maxSum(k)(l)(axis+1n) = myMax(k)(l)(axis)*v + maxSum(k)(l)(axis);
    }

    final public def min(axis:Int):Int {
        var result:Int = Int.MIN_VALUE;
        for (var k:Int = 0n; k<myMin(axis).rows; k++) {
            val a = myMin(axis)(k)(axis);
            var b:Int = minSum(axis)(k)(axis);
            // ax+b<=0 where a<0 => x>=ceil(-b/a)
            val m = b>0n? (-b+a+1n)/a : -b/a;
            if (m > result) result = m;
        }
        return result;
    }

    final public def max(axis:Int):Int {
        var result:Int = Int.MAX_VALUE;
        for (var k:Int = 0n; k<myMax(axis).rows; k++) {
            val a = myMax(axis)(k)(axis);
            val b = maxSum(axis)(k)(axis);
            // ax+b<=0 where a>0 => x<=floor(-b/a)
            val m = b>0n? (-b-a+1n)/a : -b/a;
            if (m < result) result = m;
        }
        return result;
    }


    /**
     * odometer-style iterator for this scanner
     *
     * advance() computes the k that is the axis to be bumped:
     *
     * axis    0    1         k        k+1
     * now   x[0] x[1] ...  x[k]   max[k+1] ...  max[rank-1]
     * next  x[0] x[1] ...  x[k]+1 min[k+1] ...  min[rank-1]
     *
     * i.e. bump k, reset k+1 through rank-1
     * finished if k<0
     *
     * next() does the bumping and resetting
     *
     * assumes no degenerate axes, which is guaranteeed by Scanner API
     */
    final private class RailIt implements Iterator[Rail[Int]] {
        private val rank:Long = PolyScanner.this.rank;
        private val s =  PolyScanner.this;

        private val x = new Rail[Int](rank);
        private val myMin = new Rail[Int](rank);
        private val myMax = new Rail[Int](rank);

        private var k:Int;
        private var doesHaveNext:Boolean;
        def this() {
            myMin(0n) = s.min(0n);
            myMax(0n) = s.max(0n);
            x(0n) = s.min(0n);
            for (k=1n; k<rank; k++) {
                s.set(k-1n, x(k-1n));
                val m = s.min(k);
                x(k) = m;
                myMin(k) = m;
                myMax(k) = s.max(k);
            }
            x(rank-1)--;
	    checkHasNext();
	}

        public def hasNext() = doesHaveNext;

        private def checkHasNext():void {
            k = (rank as Int)-1n;
            while (x(k)>=myMax(k)) {
                if (--k<0) {
                    doesHaveNext = false;
                    return;
                }
            }
            doesHaveNext = true;
        }

        final public def next() {
            x(k)++;
            for (k=k+1n; k<rank; k++) {
                s.set(k-1n, x(k-1n));
                val m = s.min(k);
                x(k) = m;
                myMin(k) = m;
                myMax(k) = s.max(k);
            }
            checkHasNext();
            return x;
        }

        public def remove() {}
    }

    /**
     * required by API, but less efficient b/c of allocation
     * XXX figure out how to expose
     *   1. Any/Var/ValPoint?
     *   2. hide inside iterator(body:(Point)=>void)?
     */
    public def iterator():Iterator[Point(rank)] = new Iterator[Point(rank)]() {
    	val it = new RailIt();
        public final def hasNext() = it.hasNext();
        public final def next(): Point(rank) {
            val t:Rail[Int] = it.next();
            return Point.make(new Rail[Long](t.size, (i:Long)=>t(i as Int) as Long)) as Point(rank);
        }
    };


    //
    // debugging info
    //

    // XXX doesn't handle multi-body case
    public def printInfo(ps:Printer) {
        ps.println("PolyScanner");
        C.printInfo(ps, "  C");
    }

    public def printInfo2(ps:Printer):void {
        for (var k:Int = 0n; k<myMin.size; k++) {
            ps.println("axis "+k);
            ps.println("  min");
            for (var l:Int = 0n; l<myMin(k).rows; l++) {
                ps.print("  ");
                for (var m:Int = 0n; m<myMin(k)(l).cols; m++)
                    ps.print(" "+myMin(k)(l)(m));
                ps.print("  sum");
                for (var m:Int = 0n; m<minSum(k)(l).cols; m++)
                    ps.print(" "+minSum(k)(l)(m));
                ps.print("\n");
            }
            ps.printf("  max\n");
            for (var l:Int = 0n; l<myMax(k).rows; l++) {
                ps.print("  ");
                for (var m:Int = 0n; m<myMax(k)(l).cols; m++)
                    ps.print(" "+myMax(k)(l)(m));
                ps.print("  sum");
                for (var m:Int = 0n; m<maxSum(k)(l).cols; m++)
                    ps.print(" "+maxSum(k)(l)(m));
                ps.println();
            }
        }
    }
}
