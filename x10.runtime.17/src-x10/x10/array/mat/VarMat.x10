// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array.mat;

public final value class VarMat extends Mat[VarRow] {
    
    private val r: ValRail[VarRow];

    public def this(cols: nat, r:ValRail[VarRow]) {
        super(r.length, cols);
        this.r = r;
    }

    public def this(rows: nat, cols: nat, init:(nat)=>VarRow) {
        super(rows, cols);
        r = Rail.makeVal[VarRow](rows, init);
    }

    public def this(rows: nat, cols: nat, init: (i:nat,j:nat)=>int)
        = this(rows, cols, (i:nat)=>new VarRow(cols, (j:nat)=>init(i,j)));

    public def this(rows: nat, cols: nat)
        = this(rows, cols, (nat)=>new VarRow(cols));

    public def apply(i: nat) = r(i);

    public def iterator() = r.iterator();

}


