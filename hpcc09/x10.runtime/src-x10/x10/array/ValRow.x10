// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.io.Printer;
import x10.io.StringWriter;


public value class ValRow extends Row {

    private val row: ValRail[int];

    public def this(row: ValRail[int]) {
        super(row.length);
        this.row = row;
    }

    public def this(row: Rail[int]!) {
        this(row.length, (i:nat) => row(i));
    }

    public def this(cols: nat, init: (nat)=>int) {
        super(cols);
        row = Rail.makeVal[int](cols, init);
    }
    
    public safe def apply(i:nat) = row(i);
    
    public safe def set(v:int, i:nat):int {
        throw new IllegalOperationException("ValRow.set");
    }
}


