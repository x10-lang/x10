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
        return new PolyScanner(constraints);
    }


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
