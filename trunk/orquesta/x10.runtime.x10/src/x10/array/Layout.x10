package x10.array;

import x10.lang.*;

abstract class Layout {

    abstract int size();

    abstract int offset(Point pt);
    abstract int offset(int x0);
    abstract int offset(int x0, int x1);
    abstract int offset(int x0, int x1, int x2);

    // XXX this is used in array init, but it's a bad way to do it -
    // use Region.Iterator instead and retire this...
    abstract Point coord(int offset);

    static Layout make(int [] min, int [] max) {
        return new RectLayout(min, max);
    }
}

