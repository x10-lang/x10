package x10.array;

import java.util.NoSuchElementException;

import x10.lang.Point;
import x10.lang.Region;
import x10.util.Iterator_Scanner;

//
// XXX re-implement as PolyRegion w/ non-overlapping halfspaces to
// pick up all ops
//

class EmptyRegion extends BaseRegion implements Iterator_Scanner {


    EmptyRegion(int rank) {
        super(rank, true, true);
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

    public Iterator_Scanner scanners() {
        return this;
    }

}
