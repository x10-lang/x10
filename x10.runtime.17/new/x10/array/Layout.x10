// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

abstract value class Layout {

    abstract def size(): int;

    abstract def offset(pt: Point): int;
    abstract def offset(x0: int): int;
    abstract def offset(x0: int, x1: int): int;
    abstract def offset(x0: int, x1: int, x2: int): int;

    /*
      doesn't work (for now? for good?) - use constructor instead
    static def make(min: Rail[int], max: Rail[int]): Layout {
        return new RectLayout(min, max);
    }
    */
}

