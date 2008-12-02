package x10.array;

import java.io.PrintStream;

import x10.util.Iterator_Scanner;

import x10.lang.Point;
import x10.lang.Region;
import x10.lang.IllegalOperationException;

import x10.util.ArrayList_PolyRegion;


public abstract class BaseRegion extends Region {

    //
    // factories
    //

    static public Region(:self.rank==rank) makeEmpty(final int rank) {
        return new EmptyRegion(rank);
    }

    static public Region(:self.rank==rank) makeFull(final int rank) {
        return new FullRegion(rank);
    }

    static public Region(:self.rank==0) makeUnit() {
        return new FullRegion(0);
    }

    static public Region(:self.rank==min.length) makeRectangular(final int [] min, final int [] max) {
        return RectRegion.make(min, max);
    }        

    static public Region(:rank==1) makeRectangular(int min, int max) {
        return (Region(:rank==1)) RectRegion.make(min, max);
    }        

    static public Region(:rank==2) makeBanded(int size, int upper, int lower) {
        return PolyRegion.makeBanded(size, upper, lower);
    }

    static public Region(:rank==2) makeBanded(int size) {
        return PolyRegion.makeBanded(size, 1, 1);
    }

    static public Region(:rank==2) makeUpperTriangular(int size) {
        return PolyRegion.makeUpperTriangular(0, 0, size);
    }

    static public Region(:rank==2) makeLowerTriangular(int size) {
        return PolyRegion.makeLowerTriangular(0, 0, size);
    }

    static public Region(:rank==regions.length) make(final Region [] regions) {
        Region r = regions[0];
        for (int i=1; i<regions.length; i++)
            r = r.product(regions[i]);
        return (Region(:rank==regions.length)) r;
    }


    //
    // basic information
    //

    public boolean isConvex() {
        throw U.unsupported(this, "isConvex");
    }

    public boolean isEmpty() {
        throw U.unsupported(this, "isEmpty");
    }

    public int size() {
        throw U.unsupported(this, "size");
    }


    //
    // region composition
    //

    public Region union(Region that) {
        PolyRegionList rs = new PolyRegionList(rank);
        rs.add(this);
        rs.add(that.difference(this));
        return UnionRegion.make(rs);
    }

    public Region disjointUnion(Region that) {
        if (!this.intersection(that).isEmpty())
            throw U.illegal("regions are not disjoint");
        PolyRegionList rs = new PolyRegionList(rank);
        rs.add(this);
        rs.add(that);
        return UnionRegion.make(rs);
    }

    public Region difference(Region that) {
        // complement might be expensive so we do some special casing
        if (this.isEmpty() || that.isEmpty())
            return this;
        else
            return this.intersection(that.complement());
    }

    public boolean disjoint(Region that) {
        return this.intersection(that).isEmpty();
    }

    public Region complement() {
        throw U.unsupported(this, "complement");
    }

    public Region intersection(Region that) {
        throw U.unsupported(this, "intersection");
    }

    public Region product(Region that) {
        throw U.unsupported(this, "product");
    }

    public Region projection(int axis) {
        throw U.unsupported(this, "projection");
    }

    public Region boundingBox() {
        throw U.unsupported(this, "boundingBox");
    }


    //
    // region comparison operations
    //

    public boolean contains(Region that) {
        return that.difference(this).isEmpty();
    }

    public boolean equals(Region that) {
        return this.contains(that) && that.contains(this);
    }

    public boolean $eq(Region that) {
        return equals(that);
    }


    //
    // pointwise
    //

    public boolean contains(Point p) {
        throw U.unsupported(this, "contains(Point)");
    }



    //
    // scanning, iterating
    //
    // XXX - slight generalization, or wrappering, of
    // PolyRegion.Iterator gives us a BaseRegion.Iterator
    //

    public Iterator_Scanner scanners() {
        throw U.unsupported(this, "scanners()");
    }

    public Region.Iterator iterator() {
        throw U.unsupported(this, "iterator()");
    }


    //
    // debugging
    //

    public void printInfo(PrintStream out) {
        out.println("Region " + this.getClass().getName());
    }


    //
    //
    //

    protected BaseRegion(int rank, boolean rect, boolean zeroBased) {
        super(rank, rect, zeroBased);
    }

    public int [] min() {
        throw U.unsupported(this, "min()");
    }

    public int [] max() {
        throw U.unsupported(this, "max()");
    }
}

