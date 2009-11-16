// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

public final class XformMat extends Mat[ValRow] {
    
    public def this(cols: nat, mat: ValRail[ValRow])
        = super(mat.length, cols, mat);

    public def this(rows: nat, cols: nat, init:(nat)=>ValRow)
        = super(rows, cols, ValRail.make[ValRow](rows, init));

    public def this(rows: nat, cols: nat, init: (i:nat,j:nat)=>int)
        = this(rows, cols, (i:nat)=>new ValRow(cols, (j:nat)=>init(i,j)));


    /**
     * Matrix multiplication
     */

    public global operator this * (that: XformMat): XformMat {
        return new XformMat(this.rows, that.cols, (i:nat,j:nat) => {
            var sum:int = 0;
            for (var k:int=0; k<this.cols; k++)
                sum += this(i)(k)*that(k)(j);
            return sum;
        });
    }


    /**
     * Matrix times vector. Assumes homogeneous coordinates.
     */

    public global operator this * (p:Point):Point {
        return Point.make(rows-1, (i:nat)=> {
            var sum:int = this(i)(p.rank);
            for (var j:int=0; j<p.rank; j++)
                sum += p(j)*this(i)(j);
            return sum;
        });
    }


    /**
     * Identity matrix.
     */

    public static def identity(rank:int) = new XformMat(rank+1, rank+1, (i:nat,j:nat)=>(i==j?1:0));

}





