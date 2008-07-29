package x10.array;

import java.lang.UnsupportedOperationException;

import java.util.Iterator;
import java.util.NoSuchElementException;

import x10.lang.Point;
import x10.lang.Region;


class EmptyRegion extends BaseRegion implements java.util.Iterator {


    EmptyRegion(int rank) {
        super(rank);
    }


    //
    // public region API - basic information
    //

    public boolean disjoint(Region that) {
        return true;
    }

    public boolean isConvex() {
        return true;
    }

    public int size() {
        return 0;
    }


    //
    // Scanner Iterator
    //

    public boolean hasNext() {
        return false;
    }

    public Scanner next() {
        throw new NoSuchElementException();
    }

    public void remove() {
        throw U.unsupported();
    }

    public java.util.Iterator scanners() {
        return this;
    }

}
