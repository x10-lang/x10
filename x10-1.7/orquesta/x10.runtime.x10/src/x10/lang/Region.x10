package x10.lang;

import x10.lang.Point;
import x10.util.Iterator_Scanner;
import x10.array.BaseRegion;


public abstract value class Region(
    int rank,
    boolean rect,
    boolean zeroBased,
    boolean rail
) implements SetOps_Region {


    //
    // basic information
    //

    public abstract int size();
    public abstract boolean isConvex();
    public abstract boolean isEmpty();
    public abstract boolean disjoint(Region that);


    //
    // factories
    //

    public static Region(:self.rank==rank) makeEmpty(final int rank) {
        return BaseRegion.makeEmpty(rank);
    }

    public static Region(:self.rank==rank) makeFull(final int rank) {
        return BaseRegion.makeFull(rank);
    }

    public static Region(:self.rank==0) makeUnit() {
        return BaseRegion.makeUnit();
    }

    public static Region(:self.rank==min.length) makeRectangular(final int [] min, final int [] max) {
        return BaseRegion.makeRectangular(min, max);
    }        

    public static Region(:rank==1) makeRectangular(int min, int max) {
        return BaseRegion.makeRectangular(min, max);
    }        

    public static Region(:rank==2) makeBanded(int size, int upper, int lower) {
        return BaseRegion.makeBanded(size, upper, lower);
    }

    public static Region(:rank==2) makeBanded(int size) {
        return BaseRegion.makeBanded(size);
    }

    public static Region(:rank==2) makeUpperTriangular(int size) {
        return BaseRegion.makeUpperTriangular(size);
    }

    public static Region(:rank==2) makeLowerTriangular(int size) {
        return BaseRegion.makeLowerTriangular(size);
    }

    public static Region(:rank==1) make(int min, int max) {
        return BaseRegion.makeRectangular(min, max);
    }

    public static Region(:rank==regions.length) make(final Region [] regions) {
        return BaseRegion.make(regions);
    }


    //
    // region operations
    //

    public abstract Region complement();
    public abstract Region union(Region that);
    public abstract Region disjointUnion(Region that);
    public abstract Region intersection(Region that);
    public abstract Region difference(Region that);
    public abstract Region product(Region that);
    public abstract Region projection(int axis);
    public abstract Region boundingBox();

    public abstract int [] min();
    public abstract int [] max();


    //
    // set ops
    //

    public Region $not() {
        return complement();
    }

    public Region $and(Region that) {
        return intersection(that);
    }

    public Region $or(Region that) {
        return union(that);
    }

    public Region $minus(Region that) {
        return difference(that);
    }


    //
    // comparison operations
    //

    public abstract boolean contains(Region that);
    public abstract boolean equals(Region that);

    public abstract boolean contains(Point p);

    public abstract boolean $eq(Region that);


    //
    // efficient scanning - rank is known at compile time
    //
    // Iterator_Scanner it = r.scanners();
    // while (it.hasNext()) {
    //     Region.Scanner s = it.next();
    //     int min0 = s.min(0);
    //     int max0 = s.max(0);
    //     for (int i0=min0; i0<=max0; i0++) {
    //         s.set(0, i0);
    //         int min1 = s.min(1);
    //         int max1 = s.max(1);
    //         for (int i1=min1; i1<=max1; i1++)
    //             ... body using i0,i1, etc. ...
    //     }
    // }
    //

    public abstract Iterator_Scanner scanners();

    public static interface Scanner {
        void set(int axis, int position);
        int min(int axis);
        int max(int axis);
    }


    //
    // iteration - rank is not known at compile time
    //
    // Region.Iterator it = r.iterator();
    // while (it.hasNext()) {
    //     int [] x = it.next();
    //     ... body using x[0],x[1], etc. ...
    // }
    //

    public static interface Iterator {
        boolean hasNext();
        int [] next();
    }

    public abstract Region.Iterator iterator();


    //
    //
    //

    protected Region(int rank, boolean rect, boolean zeroBased) {
        this.rank = rank;
        this.rect = rect;
        this.zeroBased = zeroBased;
        this.rail = rank==1 && rect && zeroBased;
    }

}
