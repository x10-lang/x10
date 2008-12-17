// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array.mat;


public value class ValRow extends Row {

    private val r: ValRail[int];

    public def this(r: ValRail[int]) {
        super(r.length);
        this.r = r;
    }

    public def this(r: Rail[int]) {
        this(r.length, r);
    }

    public def this(cols: nat, init: (nat)=>int) {
        super(cols);
        r = Rail.makeVal[int](cols, init);
    }
    
    public def apply(i:nat) = r(i);
    
    public def set(v:int, i:nat):int {
        throw new IllegalOperationException("ValRow.set");
    }
}


