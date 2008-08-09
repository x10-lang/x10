package x10.array;

import x10.lang.Region;
import x10.lang.Point;

//
// XXX caching of min/max in constructor will generate exception that
// prevents creation of unbounded rectangular RectRegion - fix that so
// it only happens when scanner or iterator are created
//


final class RectRegion extends PolyRegion {

    //
    // computation of size and min/max is deferred until needed to
    // allow unbounded regions
    // 

    private int size = -1;

    RectRegion(ConstraintList cl) {
        super(cl);
    }

    static RectRegion make(int [] min, int [] max) {

        if (max.length!=min.length)
            throw U.illegal("min and max must have same length");

        ConstraintList cl = new ConstraintList(min.length);
        for (int i=0; i<min.length; i++) {
            cl.add(cl.X(i), cl.GE, min[i]);
            cl.add(cl.X(i), cl.LE, max[i]);
        }

        return new RectRegion(cl);
    }


    static RectRegion make(int min, int max) {
        return make(new int [] {min}, new int [] {max});
    }

    public int size() {
        if (size < 0) {
            int [] min = constraints.rectMin();
            int [] max = constraints.rectMax();
            size = 1;
            for (int i=0; i<rank; i++)
                size *= max[i] - min[i] + 1;
        }
        return size;
    }


    //
    // scanner
    //

    private final static class Scanner implements Region.Scanner {

        private final int [] min;
        private final int [] max;

        Scanner(PolyRegion r) {
            min = r.constraints.rectMin();
            max = r.constraints.rectMax();
        }

        final public void set(int axis, int position) {
            // no-op
        }
        
        final public int min(int axis) {
            return min[axis];
        }
        
        final public int max(int axis) {
            return max[axis];
        }
    }

    public Region.Scanner scanner() {
        return new RectRegion.Scanner(this);
    }


    //
    // specialized from PolyRegion.Iterator
    // keep them in sync
    //
    // XXX this is actually SLOWER than the generic PolyRegion.Iterator!!!???
    //

    private final static class Iterator implements Region.Iterator {
        
        // parameters
        private final int rank;
        private final int [] min;
        private final int [] max;

        // state
        private final int [] x;
        private int k;

        Iterator(final RectRegion r) {
            rank = r.rank;
            min = r.constraints.rectMin();
            max = r.constraints.rectMax();
            x = new int[rank];
            for (int i=0; i<rank; i++)
                x[i] = min[i];
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
            for (k=k+1; k<rank; k++)
                x[k] = min[k];
            return x;
        }
    }

    /* slower!!!
    public Region.Iterator iterator() {
        return new RectRegion.Iterator(this);
    }
    */


    //
    // region operations
    //

    public Region boundingBox() {
        return this;
    }

    int [] min() {
        return constraints.rectMin();
    }

    int [] max() {
        return constraints.rectMax();
    }


    public boolean equals(Region that) {

        // we only handle rect==rect
        if (!(that instanceof RectRegion))
            return super.equals(that);

        // ranks must match
        if (this.rank!=that.rank)
            return false;

        // fetch bounds
        int [] thisMin = this.min();
        int [] thisMax = this.max();
        int [] thatMin = ((RectRegion)that).min();
        int [] thatMax = ((RectRegion)that).max();

        // compare 'em
        for (int i=0; i<rank; i++) {
            if (thisMin[i]!=thatMin[i] || thisMax[i]!=thatMax[i])
                return false;
        }
        return true;
    }


    //
    //
    //

    public String toString() {
        int [] min = min();
        int [] max = max();
        String s = "[";
        for (int i=0; i<rank; i++) {
            if (i>0) s += ",";
            s += min[i] + ".." + max[i];
        }
        s += "]";
        return s;
    }

}
