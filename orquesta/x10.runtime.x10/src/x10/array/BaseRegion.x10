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

    static public Region makeEmpty(int rank) {
        return new EmptyRegion(rank);
    }

    static public Region makeFull(int rank) {
        return new FullRegion(rank);
    }

    static public Region makeUnit() {
        return new FullRegion(0);
    }

    static public Region makeRectangular(int [] min, int [] max) {
        return RectRegion.make(min, max);
    }        

    static public Region makeRectangular(int min, int max) {
        return RectRegion.make(min, max);
    }        

    static public Region makeBanded(int size, int upper, int lower) {
        return PolyRegion.makeBanded(size, upper, lower);
    }

    static public Region makeBanded(int size) {
        return PolyRegion.makeBanded(size, 1, 1);
    }

    static public Region makeUpperTriangular(int size) {
        // XXX
        return PolyRegion.makeBanded(size, size, 1);
    }

    static public Region makeLowerTriangular(int size) {
        // XXX
        return PolyRegion.makeBanded(size, 1, size);
    }

    static public Region makeUpperTriangular(int rowMin, int colMin, int size) {
        return PolyRegion.makeBanded(rowMin, colMin, size, true);
    }

    static public Region makeLowerTriangular(int rowMin, int colMin, int size) {
        return PolyRegion.makeBanded(rowMin, colMin, size, false);
    }

    static public Region make(Region [] regions) {
        Region r = regions[0];
        for (int i=1; i<regions.length; i++)
            r = r.product(regions[i]);
        return r;
    }


    //
    // XXX testing only; add to Region if needed externally
    //

    static public Region makeBanded(int rowMin, int colMin, int rowMax, int colMax, int upper, int lower) {
        return PolyRegion.makeBanded(rowMin, colMin, rowMax, colMax, upper, lower);
    }

    static public PolyRegion make(HalfspaceList hl) {
        return PolyRegion.make(hl);
    }


    //
    // basic information
    //

    public boolean disjoint(Region that) {
        throw U.unsupported();
    }

    public boolean isConvex() {
        throw U.unsupported();
    }

    public boolean isEmpty() {
        throw U.unsupported();
    }

    public int size() {
        throw U.unsupported();
    }


    //
    // region composition
    //

    public Region union(Region that) {
        ArrayList_PolyRegion rs = new ArrayList_PolyRegion();
        UnionRegion.add(rs, this);
        UnionRegion.add(rs, that.difference(this));
        return new UnionRegion(rank, rs.toArray());
    }

    public Region difference(Region that) {
        return this.intersection(that.inverse());
    }

    public Region inverse() {
        throw U.unsupported();
    }

    public Region intersection(Region that) {
        throw U.unsupported();
    }

    public Region product(Region that) {
        throw U.unsupported();
    }

    public Region projection(int axis) {
        throw U.unsupported();
    }

    public Region boundingBox() {
        throw U.unsupported();
    }


    //
    // region comparison operations
    //

    public boolean contains(Region that) {
        throw U.unsupported();
    }

    public boolean equals(Region that) {
        throw U.unsupported();
    }

    public boolean $eq(Region that) {
        return equals(that);
    }


    //
    // pointwise
    //

    public boolean contains(Point p) {
        throw U.unsupported();
    }



    //
    // scanning, iterating
    //
    // XXX - slight generalization, or wrappering, of
    // PolyRegion.Iterator gives us a BaseRegion.Iterator
    //

    public Iterator_Scanner scanners() {
        throw U.unsupported();
    }

    public Region.Iterator iterator() {
        throw U.unsupported();
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

    int [] min() {
        throw U.unsupported();
    }

    int [] max() {
        throw U.unsupported();
    }
}

