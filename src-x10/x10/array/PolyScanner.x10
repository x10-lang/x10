// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.Printer;
import x10.array.mat.*;

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
 *
 * @author bdlucas
 */

final public class PolyScanner implements Region.Scanner {

    private val rank: int;

    private val min: Rail[Mat];
    private val max: Rail[Mat];
    private val minSum: Rail[Mat];
    private val maxSum: Rail[Mat];

    def this(var hl: HalfspaceList): PolyScanner {

        this.rank = hl.rank;

        min = Rail.makeVar[Mat](rank);
        max = Rail.makeVar[Mat](rank);
        minSum = Rail.makeVar[Mat](rank);
        maxSum = Rail.makeVar[Mat](rank);

        //hl.printInfo(Console.OUT, "axis " + (rank-1));
        init(hl, rank-1);
        for (var k: int = rank-2; k>=0; k--) {
            hl = hl.eliminate(k+1, true);
            //hl.printInfo(Console.OUT, "axis " + k);
            init(hl, k);
        }
        //printInfo(Console.OUT);
    }

    final private def init(hl: HalfspaceList, axis: int): void {

        // count
        var imin: int = 0;
        var imax: int = 0;

        for (h:Halfspace in hl) {
            if (h(axis)<0) imin++;
            if (h(axis)>0) imax++;
        }

        // complain if unbounded
        if (imin==0 || imax==0) {
            val m = imin==0? "minimum" : "maximum";
            val msg = "axis " + axis + " has no " + m;
            throw new UnboundedRegionException(msg);
        }

        // allocate
        min(axis) = new Mat(imin, axis+1);
        max(axis) = new Mat(imax, axis+1);
        minSum(axis) = new Mat(imin, axis+1);
        maxSum(axis) = new Mat(imax, axis+1);

        // fill in
        imin=0; imax=0;
        for (h:Halfspace in hl) {
            if (h(axis)<0) {
                for (var i: int = 0; i<=axis; i++)
                    min(axis)(imin)(i) = h(i);
                minSum(axis)(imin)(0) = h(rank);
                imin++;
            }
            if (h(axis)>0) {
                for (var i: int = 0; i<=axis; i++)
                    max(axis)(imax)(i) = h(i);
                maxSum(axis)(imax)(0) = h(rank);
                imax++;
            }
        }


    }

    final public def set(axis: int, position: int): void {
        for (var k: int = axis+1; k<rank; k++)
            for (var l: int = 0; l<minSum(k).rows; l++)
                minSum(k)(l)(axis+1) = min(k)(l)(axis)*position + minSum(k)(l)(axis);
        for (var k: int = axis+1; k<rank; k++)
            for (var l: int = 0; l<maxSum(k).rows; l++)
                maxSum(k)(l)(axis+1) = max(k)(l)(axis)*position + maxSum(k)(l)(axis);
    }

    final public def min(axis: int): int {
        var result: int = Int.MIN_VALUE;
        for (var k: int = 0; k<min(axis).rows; k++) {
            val a = min(axis)(k)(axis);
            var b: int = minSum(axis)(k)(axis);
            val m = -b / a;
            if (m > result) result = m;
        }
        return result;
    }

    final public def max(axis: int): int {
        var result: int = Int.MAX_VALUE;
        for (var k: int = 0; k<max(axis).rows; k++) {
            val a = max(axis)(k)(axis);
            val b = maxSum(axis)(k)(axis);
            val m = -b / a;
            if (m < result) result = m;
        }
        return result;
    }

    public def printInfo(ps: Printer): void {
        for (var k: int = 0; k<min.length; k++) {
            ps.printf("axis %d\n", k);
            ps.printf("  min\n");
            for (var l: int = 0; l<min(k).rows; l++) {
                ps.printf("  ");
                for (var m: int = 0; m<min(k)(l).cols; m++)
                    ps.printf(" %3d", min(k)(l)(m));
                ps.printf("  sum");
                for (var m: int = 0; m<minSum(k)(l).cols; m++)
                    ps.printf(" %3d", minSum(k)(l)(m));
                ps.printf("\n");
            }
            ps.printf("  max\n");
            for (var l: int = 0; l<max(k).rows; l++) {
                ps.printf("  ");
                for (var m: int = 0; m<max(k)(l).cols; m++)
                    ps.printf(" %3d", max(k)(l)(m));
                ps.printf("  sum");
                for (var m: int = 0; m<maxSum(k)(l).cols; m++)
                    ps.printf(" %3d", maxSum(k)(l)(m));
                ps.printf("\n");
            }
        }
    }
}
