package x10.array;

import x10.util.Iterator_Scanner;

import x10.util.ArrayList_PolyRegion;


//
// union of poly regions
// i.e. CNF
//

class UnionRegion extends BaseRegion {

    private PolyRegion [] regions;

    UnionRegion(int rank, PolyRegion [] regions) {
        super(rank, false, false);
        this.regions = regions;
    }

    public Region intersection(Region that) {

        ArrayList_PolyRegion rs = new ArrayList_PolyRegion();

        for (int i=0; i<regions.length; i++) {
            Region r = regions[i].intersection(that);
            if (r instanceof PolyRegion) {
                if (!r.isEmpty())
                    rs.add((PolyRegion) r);
            } else if (r instanceof UnionRegion)
                throw new Error("not yet");
        }
        return new UnionRegion(rank, rs.toArray());
    }


    //
    // scanner
    //

    class Scanners implements Iterator_Scanner {
        
        private int i = 0;
        
        public boolean hasNext() {
            return i<regions.length;
        }
        
        public Region.Scanner next() {
            return regions[i++].scanner();
        }
    }


    public Iterator_Scanner scanners() {
        return new Scanners();
    }


    //
    // iterator
    //

    class Iterator implements Region.Iterator {

        private int i = 0;
        nullable<Region.Iterator> it = null;
        
        public boolean hasNext() {
            for (;;) {
                if (it!=null && it.hasNext())
                    return true;
                if (i >= regions.length)
                    return false;
                it = regions[i++].iterator();
            }
        }
        
        public final int [] next() {
            return it.next();
        }
    }

    public Region.Iterator iterator() {
        return new Iterator();
    }


    //
    //
    //

    private nullable<Region> boundingBox = null;

    // XXX should get these from Integer but they are missing from
    // x10.lang.Integer in the Java runtime so just put them here
    final static int MAX_VALUE = 2147483647;
    final static int MIN_VALUE = -2147483648;

    public Region boundingBox() {
        if (boundingBox==null) {
            int [] min = new int[rank];
            int [] max = new int[rank];
            for (int axis=0; axis<rank; axis++)
                min[axis] = MAX_VALUE;
            for (int axis=0; axis<rank; axis++)
                max[axis] = MIN_VALUE;
            for (int i=0; i<regions.length; i++) {
                int [] rmin = regions[i].min();
                int [] rmax = regions[i].max();
                for (int axis=0; axis<rank; axis++) {
                    if (rmin[axis]<min[axis]) min[axis] = rmin[axis];
                    if (rmax[axis]>max[axis]) max[axis] = rmax[axis];
                }
            }
            boundingBox = Region.makeRectangular(min, max);
        }
        return (Region) boundingBox;
    }

    int [] min() {
        return ((BaseRegion)boundingBox()).min();
    }

    int [] max() {
        return ((BaseRegion)boundingBox()).max();
    }


    //
    //
    //

    public String toString() {
        String s = "(";
        for (int i=0; i<regions.length; i++) {
            if (i>0) s += " || ";
            s += regions[i];
        }
        s += ")";
        return s;
    }
}

