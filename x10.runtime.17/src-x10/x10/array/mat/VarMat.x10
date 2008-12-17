// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array.mat;

public final value class VarMat extends Mat[VarRow] {
    
    public def this(rows: nat, cols: nat) {
        super(rows, cols, (nat)=>new VarRow(cols));
    }
}


