package x10.array;

import java.io.PrintStream;

import x10.util.Iterator_Constraint;


//
// Here's the general scheme for the information used in scanning,
// illustrated for a region of rank r=4. Each axis Xi is bounded
// by a set of constraints elim[i] obtained from the region
// constraints by FME (resulting in the 0 coefficients as
// shown). Computing the bounds for Xi requires substituting X0 up
// to Xi-1 into each constraint (as shown) and taking mins and
// maxes.
//
//           0   1   2   r-1
// 
// elim[0]
//           A0  0   0   0   B   X0 bounded by B / A0
//           A0  0   0   0   B   X0 bounded by B / A0
//           ...                 ...
// 
// elim[1]
//           A0  A1  0   0   B   X1 bounded by (B+A0*X0) / A1
//           A0  A1  0   0   B   X1 bounded by (B+A0*X0) / A1
//           ...                 ...
// 
// elim[2]
//           A0  A1  A2  0   B   X2 bounded by (B+A0*X0+A1*X1) / A2
//           A0  A1  A2  0   B   X2 bounded by (B+A0*X0+A1*X1) / A2
//           ...                 ...
// 
// elim[3]
//           A0  A1  A2  A3  B   X3 bounded by (B+A0*X0+A1*X1+A2*X2) / A3
//           A0  A1  A2  A3  B   X3 bounded by (B+A0*X0+A1*X1+A2*X2) / A3
//           ...                 ...
//
// In the innermost loop the bounds for X3 could be computed by
// substituting the known values of X0 through X2 into each
// constraint. However, part of that computation can be pulled out
// of the inner loop by keeping track for each constraint in
// elim[k] a set of partial sums of the form
//
//     sum[0] = B
//     sum[1] = B+A0*X0
//     ...
//     sum[k] = B+A0*X0+A1*X1+...+Ak-1*Xk-1
//
// and updating each partial sum sum[i+1] every time Xi changes by
//
//     sum[i+1] := sum[i] + Ai*Xi
//
// The loop bounds for Xk are then obtained by computing mins and
// maxes over the sum[k]/Ak for the constraints in elim[k].
//


class PolyScanner implements Region.Scanner {

    int rank;

    int [][][] min;
    int [][][] max;
    int [][][] minSum;
    int [][][] maxSum;

    PolyScanner(ConstraintList cl) {

        this.rank = cl.rank;

        min = new int[rank][][];
        max = new int[rank][][];
        minSum = new int[rank][][];
        maxSum = new int[rank][][];

        //ConstraintList cl = constraints;
        cl.printInfo(System.out, "axis " + (rank-1));
        init(cl, rank-1);
        for (int k=rank-2; k>=0; k--) {
            cl = cl.FME(k+1);
            cl.printInfo(System.out, "axis " + k);
            init(cl, k);
        }
        printInfo(System.out);
    }

    void printInfo(PrintStream ps) {
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

    void init(ConstraintList cl, int axis) {

        // count
        int imin=0, imax=0;
        Iterator_Constraint it = cl.iterator();
        while (it.hasNext()) {
            Constraint c = (Constraint) it.next();
            if (c.cs[axis]<0) imin++;
            if (c.cs[axis]>0) imax++;
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
            Constraint c = (Constraint) it.next();
            if (c.cs[axis]<0) {
                min[axis][imin] = new int [axis+1];
                minSum[axis][imin] = new int[axis+1];
                for (int i=0; i<=axis; i++)
                    min[axis][imin][i] = c.cs[i];
                minSum[axis][imin][0] = c.cs[rank];
                imin++;
            }
            if (c.cs[axis]>0) {
                max[axis][imax] = new int [axis+1];
                maxSum[axis][imax] = new int[axis+1];
                for (int i=0; i<=axis; i++)
                    max[axis][imax][i] = c.cs[i];
                maxSum[axis][imax][0] = c.cs[rank];
                imax++;
            }
        }

    }

    // XXX should get these from Integer but they are missing from
    // x10.lang.Integer in the Java runtime so just put them here
    final static int MAX_VALUE = 2147483647;
    final static int MIN_VALUE = -2147483648;

    public void set(int axis, int position) {
        for (int k=axis+1; k<rank; k++)
            for (int l=0; l<minSum[k].length; l++)
                minSum[k][l][axis+1] = min[k][l][axis]*position + minSum[k][l][axis];
        for (int k=axis+1; k<rank; k++)
            for (int l=0; l<maxSum[k].length; l++)
                maxSum[k][l][axis+1] = max[k][l][axis]*position + maxSum[k][l][axis];
    }

    public int min(int axis) {
        int result = /*Integer.*/MIN_VALUE;
        for (int k=0; k<min[axis].length; k++) {
            int a = min[axis][k][axis];
            int b = minSum[axis][k][axis];
            int m = -b / a;
            if (m > result) result = m;
        }
        return result;
    }

    public int max(int axis) {
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

    ConstraintList [] elim = new ConstraintList[rank];

    Scanner() {
        ConstraintList cl = constraints;
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
