package x10.array;

import x10.lang.*;

interface Layout {

    public int size();

    public int offset(Point pt);
    public int offset(int x0);
    public int offset(int x0, int x1);
    public int offset(int x0, int x1, int x2);

    // XXX this is used in array init, but it's a bad way to do it -
    // use Region.Iterator instead and retire this...
    public Point coord(int offset);
}

