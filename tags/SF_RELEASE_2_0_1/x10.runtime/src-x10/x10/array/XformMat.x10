// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

public final class XformMat extends Mat[ValRow] {
    
    public def this(cols: Int, mat: ValRail[ValRow])
        = super(mat.length, cols, mat);

    public def this(rows: Int, cols: Int, init:(Int)=>ValRow)
        = super(rows, cols, ValRail.make[ValRow](rows, init));

    public def this(rows: Int, cols: Int, init: (i:Int,j:Int)=>int)
        = this(rows, cols, (i:Int)=>new ValRow(cols, (j:Int)=>init(i,j)));


    /**
     * Matrix multiplication
     */

    public global operator this * (that: XformMat): XformMat {
        return new XformMat(this.rows, that.cols, (i:Int,j:Int) => {
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
        return Point.make(rows-1, (i:Int)=> {
            var sum:int = this(i)(p.rank);
            for (var j:int=0; j<p.rank; j++)
                sum += p(j)*this(i)(j);
            return sum;
        });
    }


    /**
     * Identity matrix.
     */

    public static def identity(rank:int) = new XformMat(rank+1, rank+1, (i:Int,j:Int)=>(i==j?1:0));

}





