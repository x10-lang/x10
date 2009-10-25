// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

final value class VarRow extends Row {

    private val row: Rail[int];

    public def this(cols: nat, init: (nat)=>int) {
        super(cols);
        row = Rail.makeVar[int](cols, init);
    }
    
    public def this(cols: nat) {
        super(cols);
        row = Rail.makeVar[int](cols);
    }
    
    public safe def apply(i:nat) = row(i);

    public safe def set(v:int, i:nat) = (row(i) = v);
}
