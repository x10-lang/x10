package x10.array;

import java.util.NoSuchElementException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import x10.lang.Region;
import x10.lang.Point;
import x10.util.Iterator_Scanner;
import x10.util.Iterator_Constraint;


// XXX shouldn't be public - temp to support ArrayList_PolyRegion
public class PolyRegion extends BaseRegion {

    //
    //
    //

    public boolean isConvex() {
        return true;
    }


    //
    // scanners
    //

    private class Scanners implements Iterator_Scanner {

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

    public Iterator_Scanner scanners() {
        return new Scanners();
    }

    protected Region.Scanner scanner() {
        return new PolyScanner(constraints);
    }


    //
    // odometer-style iterator
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

    final class Iterator implements Region.Iterator {
        
        private final int rank = PolyRegion.this.rank;
        private final Region.Scanner s = scanner();

        private final int [] x = new int[rank];
        private final int [] min = new int[rank];
        private final int [] max = new int[rank];

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

        public final boolean hasNext() {
            k = rank-1;
            while (x[k]>=max[k])
                if (--k<0)
                    return false;
            return true;
        }

        public final int [] next() {
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

    protected ConstraintList constraints;


    //
    // Region methods
    //

    public Region intersection(Region t) {

        if (t instanceof PolyRegion) {

            // start
            PolyRegion that = (PolyRegion) t; // XXX
            ConstraintList cl = new ConstraintList(rank);

            // these constraints
            Iterator_Constraint it = this.constraints.iterator();
            while (it.hasNext())
                cl.add(it.next());

            // those constraints
            it = that.constraints.iterator();
            while (it.hasNext())
                cl.add(it.next());

            // done
            return PolyRegion.make(cl);

        } else if (t instanceof UnionRegion) {

            return t.intersection(this);

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
    // Cartesian product requires copying the constraint matrices into
    // the result blockwise
    //

    public Region product(Region r) {
        if (!(r instanceof PolyRegion))
            throw U.unsupported();
        PolyRegion that = (PolyRegion) r;
        ConstraintList result = new ConstraintList(this.rank + that.rank);
        copy(result, this.constraints, 0);         // padded w/ 0s on the right
        copy(result, that.constraints, this.rank); // padded w/ 0s on the left
        return PolyRegion.make(result);
    }

    private static void copy(ConstraintList to, ConstraintList from, int offset) {
        Iterator_Constraint it = from.iterator();
        while (it.hasNext()) {
            int [] f = it.next().cs;
            int [] t = new int[to.rank+1];
            for (int i=0; i<from.rank; i++)
                t[offset+i] = f[i];
            t[to.rank] = f[from.rank];
            to.add(new Constraint(t));
        }
    }


    //
    //
    // -H0 || -H1 && H0 || -H2 && H1 && H0 || ...
    //

    public Region inverse() {
        
        PolyRegion [] rs = new PolyRegion[constraints.n()];
        int r = 0;

        Iterator_Constraint i = constraints.iterator();
        while (i.hasNext()) {
            Constraint ci = i.next();
            ConstraintList cl = new ConstraintList(rank);
            cl.add(ci.inverse());
            Iterator_Constraint j = constraints.iterator();
            while (j.hasNext()) {
                Constraint cj = j.next();
                if (cj==ci)
                    break;
                cl.add(cj);
            }
            rs[r++] = PolyRegion.make(cl);
        }

        return new UnionRegion(rank, rs);
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
    // point
    //

    public boolean contains(Point p) {
        Iterator_Constraint it = constraints.iterator();
        while (it.hasNext()) {
            Constraint c = it.next();
            if (!c.contains(p))
                return false;
        }
        return true;
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

    private static final int ROW = ConstraintList.X(0);
    private static final int COL = ConstraintList.X(1);

    public static Region makeBanded(int rowMin, int colMin, int rowMax, int colMax, int upper, int lower) {
        ConstraintList cl = new ConstraintList(2);
        cl.add(ROW, cl.GE, rowMin);
        cl.add(ROW, cl.LE, rowMax);
        cl.add(COL, cl.GE, colMin);
        cl.add(COL, cl.LE, colMax);
        cl.add(COL-ROW, cl.GE, colMin-rowMin-(lower-1));
        cl.add(COL-ROW, cl.LE, colMin-rowMin+(upper-1));
        return PolyRegion.make(cl);
    }

    public static Region makeBanded(int size, int upper, int lower) {
        return makeBanded(0, 0, size-1, size-1, upper, lower);
    }

    public static Region makeBanded(int rowMin, int colMin, int size, boolean upper) {
        if (upper) {
            ConstraintList cl = new ConstraintList(2);
            cl.add(ROW, cl.GE, rowMin);
            cl.add(COL, cl.LE, colMin+size-1);
            cl.add(COL-ROW, cl.GE, colMin-rowMin);
            return PolyRegion.make(cl);
        } else {
            throw U.unsupported();
        }
    }


    //
    // here's where we examine the constraints and generate
    // special-case subclasses, such as RectRegion, for efficiency
    //
    // XXX special-case isEmpty() etc.?
    //

    public static PolyRegion make(ConstraintList cl) {
        if (cl.isRect() && cl.isBounded())
            return new RectRegion(cl);
        else
            return new PolyRegion(cl);
    }

    protected PolyRegion(ConstraintList cl) {
        super(cl.rank, cl.isRect(), cl.isZeroBased());
        this.constraints = cl.reduce();
    }

    int [] min() {
        return ((BaseRegion)boundingBox()).min();
    }

    int [] max() {
        return ((BaseRegion)boundingBox()).max();
    }


    //
    // debugging
    //

    public void printInfo(PrintStream out) {
        constraints.printInfo(out, this.getClass().getName());
    }

    public String toString() {
        return constraints.toString();
    }

}

