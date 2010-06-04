// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

final class VarRow extends Row {

    private global val row:Rail[int]!;

    public def this(cols: Int, init: (Int)=>int) {
        super(cols);
        row = Rail.make[int](cols, init);
    }
    
    public def this(cols: Int) {
        super(cols);
        row = Rail.make[int](cols);
    }
    
    global def row() = row as Rail[int]!;
    public safe global def apply(i:Int) = row()(i);

    public safe global def set(v:int, i:Int) = (row()(i) = v);
}
