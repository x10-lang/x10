// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

final class VarRow extends Row {

    private global val row:Rail[int]!;

    public def this(cols: nat, init: (nat)=>int) {
        super(cols);
        row = Rail.make[int](cols, init);
    }
    
    public def this(cols: nat) {
        super(cols);
        row = Rail.make[int](cols);
    }
    
    global def row() = row as Rail[int]!;
    public safe global def apply(i:nat) = row()(i);

    public safe global def set(v:int, i:nat) = (row()(i) = v);
}
