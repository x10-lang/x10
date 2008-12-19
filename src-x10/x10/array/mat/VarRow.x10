// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array.mat;

public final value class VarRow extends Row {

    private val r: Rail[int];
    

    public def this(cols: nat, init: (nat)=>int) {
        super(cols);
        r = Rail.makeVar[int](cols, init);
    }
    
    public def this(cols: nat) {
        super(cols);
        r = Rail.makeVar[int](cols);
    }
    
    public def apply(i:nat) = r(i);

    public def set(v:int, i:nat) = (r(i) = v);
}
