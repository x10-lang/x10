// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

final value class VarMat extends Mat[VarRow] {
    
    public def this(cols: nat, mat: ValRail[VarRow])
        = super(mat.length, cols, mat);

    public def this(rows: nat, cols: nat, init:(nat)=>VarRow)
        = super(rows, cols, Rail.makeVal[VarRow](rows, init));

    public def this(rows: nat, cols: nat, init: (i:nat,j:nat)=>int)
        = this(rows, cols, (i:nat)=>new VarRow(cols, (j:nat)=>init(i,j)));

    public def this(rows: nat, cols: nat)
        = this(rows, cols, (nat)=>new VarRow(cols));
}


