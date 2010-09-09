// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

final class VarMat extends Mat[VarRow] {
    
    public def this(cols: Int, mat: ValRail[VarRow])
        = super(mat.length, cols, mat);

    public def this(rows: Int, cols: Int, init:(Int)=>VarRow)
        = super(rows, cols, ValRail.make[VarRow](rows, init));

    public def this(rows: Int, cols: Int, init: (i:Int,j:Int)=>int)
        = this(rows, cols, (i:Int)=>new VarRow(cols, (j:Int)=>init(i,j)));

    public def this(rows: Int, cols: Int)
        = this(rows, cols, (Int)=>new VarRow(cols));
}


