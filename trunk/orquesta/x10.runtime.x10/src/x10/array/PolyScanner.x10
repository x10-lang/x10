package x10.array;

import java.io.PrintStream;

import x10.util.Iterator_Halfspace;


//
// Here's the general scheme for the information used in scanning,
// illustrated for a region of rank r=4. Each axis Xi is bounded by
// two sets of halfspaces, min[i] and max[i], obtained from the
// region halfspaces by FME (resulting in the 0 coefficients as
// shown). Computing the bounds for Xi requires substituting X0 up to
// Xi-1 into each halfspace (as shown for the min[i]s) and taking
// mins and maxes.
//
//           0   1   2   r-1
// 
// min[0]
//           A0  0   0   0   B   X0 bounded by B / A0
//           A0  0   0   0   B   X0 bounded by B / A0
//           ...                 ...
// 
// min[1]
//           A0  A1  0   0   B   X1 bounded by (B+A0*X0) / A1
//           A0  A1  0   0   B   X1 bounded by (B+A0*X0) / A1
//           ...                 ...
// 
// min[2]
//           A0  A1  A2  0   B   X2 bounded by (B+A0*X0+A1*X1) / A2
//           A0  A1  A2  0   B   X2 bounded by (B+A0*X0+A1*X1) / A2
//           ...                 ...
// 
// min[3]
//           A0  A1  A2  A3  B   X3 bounded by (B+A0*X0+A1*X1+A2*X2) / A3
//           A0  A1  A2  A3  B   X3 bounded by (B+A0*X0+A1*X1+A2*X2) / A3
//           ...                 ...
//
// In the innermost loop the bounds for X3 could be computed by
// substituting the known values of X0 through X2 into each
// halfspace. However, part of that computation can be pulled out of
// the inner loop by keeping track for each halfspace in in min[k]
// and each constraing in max[k] a set of partial sums of the form
//
//     minSum[0] = B
//     minSum[1] = B+A0*X0
//     ...
//     minSum[k] = B+A0*X0+A1*X1+...+Ak-1*Xk-1
//
// (and similiarly for maxSum) and updating each partial sum
// minSum[i+1] (and similarly for maxSum[i+1]) every time Xi changes
// by
//
//     minSum[i+1] := sum[i] + Ai*Xi
//
// The loop bounds for Xk are then obtained by computing mins and
// maxes over the sum[k]/Ak for the halfspaces in elim[k].
//


public final class PolyScanner implements Region.Scanner {

    private final int rank;

    private final int [][][] min;
    private final int [][][] max;
    private final int [][][] minSum;
    private final int [][][] maxSum;

    PolyScanner(HalfspaceList cl) {

        this.rank = cl.rank;

        min = new int[rank][][];
        max = new int[rank][][];
        minSum = new int[rank][][];
        maxSum = new int[rank][][];

        //cl.printInfo(System.out, "axis " + (rank-1));
        init(cl, rank-1);
        for (int k=rank-2; k>=0; k--) {
            cl = cl.FME(k+1, true);
            //cl.printInfo(System.out, "axis " + k);
            init(cl, k);
        }
        //printInfo(System.out);
    }

    public void printInfo(PrintStream ps) {
        for (int k=0; k<min.length; k++) {
            ps.printf("axis %d\n", k);
            ps.printf("  min\n");
            for (int l=0; l<min[k].length; l++) {
                ps.printf("  ");
                for (int m=0; m<min[k][l].length; m++)
                    ps.printf(" %3d", min[k][l][m]);
                ps.printf("  sum");
                for (int m=0; m<minSum[k][l].length; m++)
                    ps.printf(" %3d", minSum[k][l][m]);
                ps.printf("\n");
            }
            ps.printf("  max\n");
            for (int l=0; l<max[k].length; l++) {
                ps.printf("  ");
                for (int m=0; m<max[k][l].length; m++)
                    ps.printf(" %3d", max[k][l][m]);
                ps.printf("  sum");
                for (int m=0; m<maxSum[k][l].length; m++)
                    ps.printf(" %3d", maxSum[k][l][m]);
                ps.printf("\n");
            }
        }
    }

    private final void init(HalfspaceList cl, int axis) {

        // count
        int imin=0, imax=0;
        Iterator_Halfspace it = cl.iterator();
        while (it.hasNext()) {
            Halfspace h = (Halfspace) it.next();
            if (h.as[axis]<0) imin++;
            if (h.as[axis]>0) imax++;
        }

        // complain if unbounded
        if (imin==0 || imax==0) {
            String m = imin==0? "minimum" : "maximum";
            String msg = "axis " + axis + " has no " + m;
            throw new UnboundedRegionException(msg);
        }

        // allocate
        min[axis] = new int [imin][];
        max[axis] = new int [imax][];
        minSum[axis] = new int [imin][];
        maxSum[axis] = new int [imax][];

        // fill in
        imin=0; imax=0;
        it = cl.iterator();
        while (it.hasNext()) {
            Halfspace h = (Halfspace) it.next();
            if (h.as[axis]<0) {
                min[axis][imin] = new int [axis+1];
                minSum[axis][imin] = new int[axis+1];
                for (int i=0; i<=axis; i++)
                    min[axis][imin][i] = h.as[i];
                minSum[axis][imin][0] = h.as[rank];
                imin++;
            }
            if (h.as[axis]>0) {
                max[axis][imax] = new int [axis+1];
                maxSum[axis][imax] = new int[axis+1];
                for (int i=0; i<=axis; i++)
                    max[axis][imax][i] = h.as[i];
                maxSum[axis][imax][0] = h.as[rank];
                imax++;
            }
        }

    }

    // XXX should get these from Integer but they are missing from
    // x10.lang.Integer in the Java runtime so just put them here
    final static int MAX_VALUE = 2147483647;
    final static int MIN_VALUE = -2147483648;

    public final void set(int axis, int position) {
        for (int k=axis+1; k<rank; k++)
            for (int l=0; l<minSum[k].length; l++)
                minSum[k][l][axis+1] = min[k][l][axis]*position + minSum[k][l][axis];
        for (int k=axis+1; k<rank; k++)
            for (int l=0; l<maxSum[k].length; l++)
                maxSum[k][l][axis+1] = max[k][l][axis]*position + maxSum[k][l][axis];
    }

    public final int min(int axis) {
        int result = /*Integer.*/MIN_VALUE;
        for (int k=0; k<min[axis].length; k++) {
            int a = min[axis][k][axis];
            int b = minSum[axis][k][axis];
            int m = -b / a;
            if (m > result) result = m;
        }
        return result;
    }

    public final int max(int axis) {
        int result = /*Integer.*/MAX_VALUE;
        for (int k=0; k<max[axis].length; k++) {
            int a = max[axis][k][axis];
            int b = maxSum[axis][k][axis];
            int m = -b / a;
            if (m < result) result = m;
        }
        return result;
    }

}


/*

older version - keep for reference

private class Scanner implements Region.Scanner {

    HalfspaceList [] elim = new HalfspaceList[rank];

    Scanner() {
        HalfspaceList cl = halfspaces;
        elim[rank-1] = cl.init(rank-1);
        for (int k=rank-2; k>=0; k--) {
            cl = cl.FME(k+1);
            //cl.printInfo(System.out, "before init");
            elim[k] = cl.init(k);
            //elim[k].printInfo(System.out, "elim[" + (k) + "]");
        }

    }

    public void set(int axis, int position) {
        for (int k=axis+1; k<rank; k++)
            elim[k].set(axis, position);
    }

    public int min(int axis) {
        return elim[axis].min();
    }

    public int max(int axis) {
        return elim[axis].max();
    }
}

*/
