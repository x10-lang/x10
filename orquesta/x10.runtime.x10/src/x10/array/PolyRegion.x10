package x10.array;

import java.util.Iterator;
import java.util.NoSuchElementException;
import java.util.List;
import java.util.ArrayList;
import java.util.Collections; // sort
import java.io.OutputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import x10.lang.Region;
import x10.lang.Point;

class PolyRegion extends BaseRegion {

    PolyRegion(int rank) {
        super(rank);
        constraints = new ConstraintList(rank);
    }

    PolyRegion(ConstraintList cl) {
        super(cl.rank());
        constraints = cl;
    }

    //
    //
    //

    public boolean isConvex() {
        return true;
    }


    //
    // scanners
    //

    private class Scanners implements java.util.Iterator {

        boolean hasNext = true;

        public boolean hasNext() {
            return hasNext;
        }

        public Region.Scanner next() {
            if (hasNext) {
                hasNext = false;
                return scanner();
            } else
                throw new NoSuchElementException();
        }

        public void remove() {
            throw U.unsupported();
        }
    };

    public java.util.Iterator scanners() {
        return new Scanners();
    }

    protected Region.Scanner scanner() {
        return new Scanner1();
    }


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

    private class Scanner1 implements Region.Scanner {

        int [][][] min = new int[rank][][];
        int [][][] max = new int[rank][][];
        int [][][] minSum = new int[rank][][];
        int [][][] maxSum = new int[rank][][];

        Scanner1() {
            ConstraintList cl = constraints;
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
            java.util.Iterator it = cl.iterator();
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

        public void set(int axis, int position) {
            for (int k=axis+1; k<rank; k++)
                for (int l=0; l<minSum[k].length; l++)
                    minSum[k][l][axis+1] = min[k][l][axis]*position + minSum[k][l][axis];
            for (int k=axis+1; k<rank; k++)
                for (int l=0; l<maxSum[k].length; l++)
                    maxSum[k][l][axis+1] = max[k][l][axis]*position + maxSum[k][l][axis];
        }

        public int min(int axis) {
            int result = java.lang.Integer.MIN_VALUE;
            for (int k=0; k<min[axis].length; k++) {
                int a = min[axis][k][axis];
                int b = minSum[axis][k][axis];
                int m = -b / a;
                if (m > result) result = m;
            }
            U.pr("min(" + axis + ") = " + result);
            return result;
        }

        public int max(int axis) {
            int result = java.lang.Integer.MAX_VALUE;
            for (int k=0; k<max[axis].length; k++) {
                int a = max[axis][k][axis];
                int b = maxSum[axis][k][axis];
                int m = -b / a;
                if (m < result) result = m;
            }
            U.pr("max(" + axis + ") = " + result);
            return result;
        }

    }

    /*

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


    //
    // hasNext() computes the k that is the axis to be bumped:
    //
    // axis    0    1         k        k+1
    // now   x[0] x[1] ...  x[k]   max[k+1] ...  max[rank-1]
    // next  x[0] x[1] ...  x[k]+1 min[k+1] ...  min[rank-1]
    //
    // i.e. bump k, reset k+1 through rank-1
    // finished if k<0
    //
    // next() does the bumping and resetting
    //

    class Iterator implements Region.Iterator {
        
        public int [] x = new int[rank];

        private Region.Scanner s = scanner();
        private int [] min = new int[rank];
        private int [] max = new int[rank];
        private int k;

        Iterator() {
            min[0] = s.min(0);
            max[0] = s.max(0);
            x[0] = min[0];
            for (k=1; k<rank; k++) {
                s.set(k-1, x[k-1]);
                x[k] = min[k] = s.min(k);
                max[k] = s.max(k);
            }
            x[rank-1]--;
        }

        public boolean hasNext() {
            k = rank-1;
            while (x[k]>=max[k])
                if (--k<0)
                    return false;
            return true;
        }

        public int [] next() {
            x[k]++;
            for (k=k+1; k<rank; k++) {
                s.set(k-1, x[k-1]);
                x[k] = min[k] = s.min(k);
                max[k] = s.max(k);
            }
            return x;
        }
    }

    public Region.Iterator iterator() {
        return new PolyRegion.Iterator();
    }


    //
    // constraints
    //
    // Note: this design for building a list of constraints doesn't
    // work because the Region needs to be a value class and therefore
    // immutable after construction. Need to build a list of
    // constraints and then construct poly object.
    //

    private ConstraintList constraints;

    protected void addConstraint(Constraint c) {
        constraints.add(c);
    }

    protected void endConstraints() {

        // debug
        //constraints.printInfo(System.out, "final constraints");

        // eliminate redundant
        constraints = constraints.reduce();
        //constraints.printInfo(System.out, "reduced constraints");

        // compute whether or not it's rectangular
        boolean isRect = constraints.isRect();
        //U.pr("isRect " + isRect);

        

    }

    //
    // Region methods
    //

    public Region intersection(Region t) {

        if (t instanceof PolyRegion) {

            // start
            PolyRegion that = (PolyRegion) t;
            PolyRegion result = new PolyRegion(rank);

            // these constraints
            java.util.Iterator it = this.constraints.iterator();
            while (it.hasNext())
                result.addConstraint((Constraint)it.next());

            // those constraints
            it = that.constraints.iterator();
            while (it.hasNext())
                result.addConstraint((Constraint)it.next());

            // done
            result.endConstraints();
            return result;

        } else {
            throw U.unsupported();
        }
    }
                          
                          
    //
    // Projection is computed by using FME to eliminate variables on
    // all but the axis of interest.
    //

    public Region projection(int axis) {
        ConstraintList cl = constraints;
        for (int k=0; k<rank; k++)
            if (k!=axis)
                cl = cl.FME(k);
        return Region.makeRectangular(cl.rectMin(axis), cl.rectMax(axis));
    }


    //
    // Bounding box is computed by taking the project on each
    // axis. This implementation is more efficient than computing
    // projection on each axis because it re-uses the FME results.
    //

    private nullable<Region> boundingBox = null;

    public Region boundingBox() {
        if (boundingBox==null) {
            int [] min = new int[rank];
            int [] max = new int[rank];        
            ConstraintList cl = constraints;
            for (int axis=0; axis<rank; axis++) {
                ConstraintList x = cl;
                for (int k=axis+1; k<rank; k++)
                    x = x.FME(k);
                min[axis] = x.rectMin(axis);
                max[axis] = x.rectMax(axis);
                cl = cl.FME(axis);
            }
            boundingBox = Region.makeRectangular(min, max);
        }
        return (Region) boundingBox;
    }


    //
    // a simple mechanism of somewhat dubious utility to allow
    // semi-symbolic specification of constraints. For example
    // X0-Y1 >= n is specified as addConstraint(ZERO+X(0)-Y(1), GE, n)
    //

    protected static final int ZERO = 0xAAAAAAA;

    protected static final int GE = 0;
    protected static final int LE = 1;

    protected static final int X(int axis) {
        return 0x1<<2*axis;
    }

    protected void addConstraint(int coeff, int op, int k) {
        int [] cs = new int[rank+1];
        for (int i=0; i<rank; i++) {
            int c = (coeff&3) - 2;
            cs[i] = op==LE? c : - c;
            coeff = coeff >> 2;
        }
        cs[rank] = op==LE? -k : k;
        addConstraint(new Constraint(cs));
    }


    //
    // lower==1 and lower==1 include the diagonal
    // lower==size and upper==size includes entire size x size square
    //
    // col-colMin >= row-rowMin - (lower-1)
    // col-colMin <= row-rowMin + (upper-1)
    //
    // col-row >= colMin-rowMin - (lower-1)
    // col-row <= colMin-rowMin + (upper-1)
    //

    private static final int ROW = X(0);
    private static final int COL = X(1);

    public static Region makeDiagonal(int rowMin, int colMin, int rowMax, int colMax, int upper, int lower) {

        PolyRegion r = new PolyRegion(2);
        r.addConstraint(ZERO+ROW, GE, rowMin);
        r.addConstraint(ZERO+ROW, LE, rowMax);
        r.addConstraint(ZERO+COL, GE, colMin);
        r.addConstraint(ZERO+COL, LE, colMax);
        r.addConstraint(ZERO+COL-ROW, GE, colMin-rowMin-(lower-1));
        r.addConstraint(ZERO+COL-ROW, LE, colMin-rowMin+(upper-1));
        r.endConstraints();
        return r;
    }

    public static Region makeDiagonal(int size, int upper, int lower) {
        return makeDiagonal(0, 0, size-1, size-1, upper, lower);
    }

    public static Region makeDiagonal(int rowMin, int colMin, int size, boolean upper) {
        if (upper) {
            PolyRegion r = new PolyRegion(2);
            r.addConstraint(ZERO+ROW, GE, rowMin);
            r.addConstraint(ZERO+COL, LE, colMin+size-1);
            r.addConstraint(ZERO+COL-ROW, GE, colMin-rowMin);
            r.endConstraints();
            return r;
        } else {
            throw U.unsupported();
        }
    }

}
