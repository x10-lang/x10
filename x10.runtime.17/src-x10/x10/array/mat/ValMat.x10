// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array.mat;

public final value class ValMat extends Mat[ValRow] {
    
    private val r: ValRail[ValRow];

    public def this(cols: nat, r:ValRail[ValRow]) {
        super(r.length, cols);
        this.r = r;
    }

    public def this(rows: nat, cols: nat, init:(nat)=>ValRow) {
        super(rows, cols);
        r = Rail.makeVal[ValRow](rows, init);
    }

    public def this(rows: nat, cols: nat, init: (i:nat,j:nat)=>int)
        = this(rows, cols, (i:nat)=>new ValRow(cols, (j:nat)=>init(i,j)));

    public def apply(i: nat) = r(i);

    public def iterator() = r.iterator();

    public def $times(that: ValMat): ValMat {
        return new ValMat(this.rows, that.cols, (i:nat,j:nat) => {
            var sum:int = 0;
            for (var k:int=0; k<this.cols; k++)
                sum += this(i)(k)*that(k)(j);
            return sum;
        });
    }
}





