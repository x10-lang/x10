package x10.array;

import x10.util.Iterator_Scanner;

import x10.util.ArrayList_PolyRegion;


//
// union of poly regions
// i.e. CNF
//

import x10.util.ArrayList_PolyRegion;

class UnionRegion extends BaseRegion {

    PolyRegion [] regions;

    UnionRegion(PolyRegionList rs) {
        super(rs.rank, false, false);
        this.regions = rs.toArray();
    }

    public Region intersection(Region that) {
        PolyRegionList rs = new PolyRegionList(rank);
        for (int i=0; i<regions.length; i++)
            rs.add(regions[i].intersection(that));
        return new UnionRegion(rs);
    }

    public Region inverse() {
        Region r = Region.makeFull(rank);
        for (int i=0; i<regions.length; i++)
            r = r.intersection(regions[i].inverse());
        return r;
    }

    public boolean isEmpty() {
        return regions.length==0;
    }

    public boolean isConvex() {
        return false;
    }

    public int size() {
        int size = 0;
        for (int i=0; i<regions.length; i++)
            size += regions[i].size();
        return size;
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

