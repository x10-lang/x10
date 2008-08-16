package x10.array;

import java.util.NoSuchElementException;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;

import x10.lang.Region;
import x10.lang.Point;
import x10.util.Iterator_Scanner;
import x10.util.Iterator_Halfspace;


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
            throw U.unsupported(this, "remove");
        }
    };

    public Iterator_Scanner scanners() {
        return new Scanners();
    }

    protected Region.Scanner scanner() {
        return new PolyScanner(halfspaces);
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
    // halfspaces
    //

    protected HalfspaceList halfspaces;


    //
    // Region methods
    //

    public Region intersection(Region t) {

        if (t instanceof PolyRegion) {

            // start
            PolyRegion that = (PolyRegion) t; // XXX
            HalfspaceList hl = new HalfspaceList(rank);

            // these halfspaces
            Iterator_Halfspace it = this.halfspaces.iterator();
            while (it.hasNext())
                hl.add(it.next());

            // those halfspaces
            it = that.halfspaces.iterator();
            while (it.hasNext())
                hl.add(it.next());

            // done
            return PolyRegion.make(hl);

        } else if (t instanceof UnionRegion) {

            return t.intersection(this);

        } else {
            throw U.unsupported(this, "intersection(" + t.getClass().getName() + ")");
        }
    }
                          
                          
    //
    // Projection is computed by using FME to eliminate variables on
    // all but the axis of interest.
    //

    public Region projection(int axis) {
        HalfspaceList hl = halfspaces;
        for (int k=0; k<rank; k++)
            if (k!=axis)
                hl = hl.FME(k, true);
        return Region.makeRectangular(hl.rectMin(axis), hl.rectMax(axis));
    }


    //
    // Cartesian product requires copying the halfspace matrices into
    // the result blockwise
    //

    public Region product(Region r) {
        if (!(r instanceof PolyRegion))
            throw U.unsupported(this, "product(" + r.getClass().getName() + ")");
        PolyRegion that = (PolyRegion) r;
        HalfspaceList result = new HalfspaceList(this.rank + that.rank);
        copy(result, this.halfspaces, 0);         // padded w/ 0s on the right
        copy(result, that.halfspaces, this.rank); // padded w/ 0s on the left
        return PolyRegion.make(result);
    }

    private static void copy(HalfspaceList to, HalfspaceList from, int offset) {
        Iterator_Halfspace it = from.iterator();
        while (it.hasNext()) {
            int [] f = it.next().as;
            int [] t = new int[to.rank+1];
            for (int i=0; i<from.rank; i++)
                t[offset+i] = f[i];
            t[to.rank] = f[from.rank];
            to.add(new Halfspace(t));
        }
    }


    //
    //
    // -H0 || -H1 && H0 || -H2 && H1 && H0 || ...
    //

    public Region complement() {
        
        PolyRegionList rl = new PolyRegionList(rank);

        Iterator_Halfspace i = halfspaces.iterator();
        while (i.hasNext()) {
            Halfspace hi = i.next();
            HalfspaceList hl = new HalfspaceList(rank);
            hl.add(hi.complement());
            Iterator_Halfspace j = halfspaces.iterator();
            while (j.hasNext()) {
                Halfspace hj = j.next();
                if (hj==hi)
                    break;
                hl.add(hj);
            }
            rl.add(PolyRegion.make(hl));
        }

        return new UnionRegion(rl);
    }

    public boolean isEmpty() {
        return halfspaces.isEmpty();
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
            HalfspaceList hl = halfspaces;
            for (int axis=0; axis<rank; axis++) {
                HalfspaceList x = hl;
                for (int k=axis+1; k<rank; k++)
                    x = x.FME(k, true);
                min[axis] = x.rectMin(axis);
                max[axis] = x.rectMax(axis);
                hl = hl.FME(axis, true);
            }
            boundingBox = Region.makeRectangular(min, max);
        }
        return (Region) boundingBox;
    }


    //
    // point
    //

    public boolean contains(Point p) {
        Iterator_Halfspace it = halfspaces.iterator();
        while (it.hasNext()) {
            Halfspace h = it.next();
            if (!h.contains(p))
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

    private static final int ROW = HalfspaceList.X(0);
    private static final int COL = HalfspaceList.X(1);

    public static Region(:rank==2) makeBanded(int rowMin, int colMin, int rowMax, int colMax, int upper, int lower) {
        HalfspaceList(:rank==2) hl = new HalfspaceList(2);
        hl.add(ROW, hl.GE, rowMin);
        hl.add(ROW, hl.LE, rowMax);
        hl.add(COL, hl.GE, colMin);
        hl.add(COL, hl.LE, colMax);
        hl.add(COL-ROW, hl.GE, colMin-rowMin-(lower-1));
        hl.add(COL-ROW, hl.LE, colMin-rowMin+(upper-1));
        return PolyRegion.make(hl);
    }

    public static Region(:rank==2) makeBanded(int size, int upper, int lower) {
        return makeBanded(0, 0, size-1, size-1, upper, lower);
    }

    public static Region(:rank==2) makeUpperTriangular(int rowMin, int colMin, int size) {
        HalfspaceList(:rank==2) hl = new HalfspaceList(2);
        hl.add(ROW, hl.GE, rowMin);
        hl.add(COL, hl.LE, colMin+size-1);
        hl.add(COL-ROW, hl.GE, colMin-rowMin);
        hl.isSimplified = true;
        return PolyRegion.make(hl);
    }

    public static Region(:rank==2) makeLowerTriangular(int rowMin, int colMin, int size) {
        HalfspaceList(:rank==2) hl = new HalfspaceList(2);
        hl.add(COL, hl.GE, colMin);
        hl.add(ROW, hl.LE, rowMin+size-1);
        hl.add(ROW-COL, hl.GE, rowMin-colMin);
        hl.isSimplified = true;
        return PolyRegion.make(hl);
    }



    //
    // here's where we examine the halfspaces and generate
    // special-case subclasses, such as RectRegion, for efficiency
    //
    // XXX special-case isEmpty() etc.?
    // XXX empty PolyRegion (with backwards bounds) is probably not handled correctly
    //

    public static Region(:rank==hl.rank) make(final HalfspaceList hl) {
        if (hl.isEmpty()) {
            return new EmptyRegion(hl.rank);
        } else if (hl.isRect() && hl.isBounded())
            return new RectRegion(hl);
        else
            return new PolyRegion(hl);
    }

    protected PolyRegion(:rank==hl.rank)(final HalfspaceList hl) {

        super(hl.rank, hl.isRect(), hl.isZeroBased());

        // simplifyAll catches more (all) stuff, but may be expensive.
        //this.halfspaces = hl.simplifyParallel();
        this.halfspaces = hl.simplifyAll();
    }

    public int [] min() {
        return boundingBox().min();
    }

    public int [] max() {
        return boundingBox().max();
    }


    //
    // debugging
    //

    public void printInfo(PrintStream out) {
        halfspaces.printInfo(out, this.getClass().getName());
    }

    public String toString() {
        return halfspaces.toString();
    }

}

