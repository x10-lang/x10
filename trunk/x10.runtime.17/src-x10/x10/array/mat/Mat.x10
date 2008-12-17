// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array.mat;

public final class Mat(rows:nat, cols:nat) implements (nat)=>Row {
    
    private val r: ValRail[Row];
    
    public def this(rows: nat, cols: nat) {
        property(rows, cols);
        r = Rail.makeVal[Row](rows, (nat)=>new VarRow(cols) to Row);
    }
    
    public def apply(i:nat) = r(i);
}


