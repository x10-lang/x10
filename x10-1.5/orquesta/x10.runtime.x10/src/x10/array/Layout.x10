package x10.array;

import x10.lang.*;

abstract class Layout {

    abstract int size();

    abstract int offset(Point pt);
    abstract int offset(int x0);
    abstract int offset(int x0, int x1);
    abstract int offset(int x0, int x1, int x2);

    static Layout make(int [] min, int [] max) {
        return new RectLayout(min, max);
    }
}

