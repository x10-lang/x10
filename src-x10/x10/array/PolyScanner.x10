// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.Printer;


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
 * and each constraing in max[k] a set of partial sums of the form
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
 *
 * @author bdlucas
 */

final public class PolyScanner implements Region.Scanner {

    static class Rail1 {
        val r: Rail[int];
        def this(n:int) {
            r = Rail.makeVar[int](n);
        }
    }

    static class Rail2 {
        val r: Rail[Rail1];
        def this(n:int) {
            r = Rail.makeVar[Rail1](n);
        }
    }

    static class Rail3 {
        val r: Rail[Rail2];
        def this(n:int) {
            r = Rail.makeVar[Rail2](n);
        }
    }


    private val rank: int;

    private val min: Rail3;
    private val max: Rail3;
    private val minSum: Rail3;
    private val maxSum: Rail3;

    def this(var hl: HalfspaceList): PolyScanner {

        this.rank = hl.rank;

        min = /*Rail.makeVar[Rail2](rank)*/ new Rail3(rank);
        max = /*Rail.makeVar[Rail2](rank)*/ new Rail3(rank);
        minSum = /*Rail.makeVar[Rail2](rank)*/ new Rail3(rank);
        maxSum = /*Rail.makeVar[Rail2](rank)*/ new Rail3(rank);

        //hl.printInfo(Console.OUT, "axis " + (rank-1));
        init(hl, rank-1);
        for (var k: int = rank-2; k>=0; k--) {
            hl = hl.eliminate(k+1, true);
            //hl.printInfo(Console.OUT, "axis " + k);
            init(hl, k);
        }
        //printInfo(Console.OUT);
    }

    public def printInfo(ps: Printer): void {
        for (var k: int = 0; k<min.r.length; k++) {
            ps.printf("axis %d\n", k);
            ps.printf("  min\n");
            for (var l: int = 0; l<min.r(k).r.length; l++) {
                ps.printf("  ");
                for (var m: int = 0; m<min.r(k).r(l).r.length; m++)
                    ps.printf(" %3d", min.r(k).r(l).r(m));
                ps.printf("  sum");
                for (var m: int = 0; m<minSum.r(k).r(l).r.length; m++)
                    ps.printf(" %3d", minSum.r(k).r(l).r(m));
                ps.printf("\n");
            }
            ps.printf("  max\n");
            for (var l: int = 0; l<max.r(k).r.length; l++) {
                ps.printf("  ");
                for (var m: int = 0; m<max.r(k).r(l).r.length; m++)
                    ps.printf(" %3d", max.r(k).r(l).r(m));
                ps.printf("  sum");
                for (var m: int = 0; m<maxSum.r(k).r(l).r.length; m++)
                    ps.printf(" %3d", maxSum.r(k).r(l).r(m));
                ps.printf("\n");
            }
        }
    }


    final private def init(hl: HalfspaceList, axis: int): void {

        // count
        var imin: int = 0;
        var imax: int = 0;

        for (h:Halfspace in hl) {
            if (h.as(axis)<0) imin++;
            if (h.as(axis)>0) imax++;
        }

        // complain if unbounded
        if (imin==0 || imax==0) {
            val m = imin==0? "minimum" : "maximum";
            val msg = "axis " + axis + " has no " + m;
            throw new UnboundedRegionException(msg);
        }

        // allocate
        min.r(axis) = /*Rail.makeVar[Rail1](imin);*/ new Rail2(imin);
        max.r(axis) = /*Rail.makeVar[Rail1](imax);*/ new Rail2(imax);
        minSum.r(axis) = /*Rail.makeVar[Rail1](imin);*/ new Rail2(imin);
        maxSum.r(axis) = /*Rail.makeVar[Rail1](imax);*/ new Rail2(imax);

        // fill in
        imin=0; imax=0;
        for (h:Halfspace in hl) {
            if (h.as(axis)<0) {
                min.r(axis).r(imin) = /*Rail.makeVar[int](axis+1)*/ new Rail1(axis+1);
                minSum.r(axis).r(imin) = /*Rail.makeVar[int](axis+1)*/ new Rail1(axis+1);
                for (var i: int = 0; i<=axis; i++)
                    min.r(axis).r(imin).r(i) = h.as(i);
                minSum.r(axis).r(imin).r(0) = h.as(rank);
                imin++;
            }
            if (h.as(axis)>0) {
                max.r(axis).r(imax) = /*Rail.makeVar[int](axis+1)*/ new Rail1(axis+1);
                maxSum.r(axis).r(imax) = /*Rail.makeVar[int](axis+1);*/ new Rail1(axis+1);
                for (var i: int = 0; i<=axis; i++)
                    max.r(axis).r(imax).r(i) = h.as(i);
                maxSum.r(axis).r(imax).r(0) = h.as(rank);
                imax++;
            }
        }


    }


    final public def set(axis: int, position: int): void {
        for (var k: int = axis+1; k<rank; k++)
            for (var l: int = 0; l<minSum.r(k).r.length; l++)
                minSum.r(k).r(l).r(axis+1) = min.r(k).r(l).r(axis)*position + minSum.r(k).r(l).r(axis);
        for (var k: int = axis+1; k<rank; k++)
            for (var l: int = 0; l<maxSum.r(k).r.length; l++)
                maxSum.r(k).r(l).r(axis+1) = max.r(k).r(l).r(axis)*position + maxSum.r(k).r(l).r(axis);
    }

    final public def min(axis: int): int {
        var result: int = int.MIN_VALUE;
        for (var k: int = 0; k<min.r(axis).r.length; k++) {
            val a = min.r(axis).r(k).r(axis);
            var b: int = minSum.r(axis).r(k).r(axis);
            val m = -b / a;
            if (m > result) result = m;
        }
        return result;
    }

    final public def max(axis: int): int {
        var result: int = int.MAX_VALUE;
        for (var k: int = 0; k<max.r(axis).r.length; k++) {
            val a = max.r(axis).r(k).r(axis);
            val b = maxSum.r(axis).r(k).r(axis);
            val m = -b / a;
            if (m < result) result = m;
        }
        return result;
    }

}
