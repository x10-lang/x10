// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array;

import x10.util.ArrayList;

public class MatBuilder {
    
    protected val mat: ArrayList[Row]!;
    protected val cols: int;

    public def this(cols: Int) {
        this.cols = cols;
        mat = new ArrayList[Row]();
    }

    public def this(rows: Int, cols: Int) {
        this.cols = cols;
	val m = new ArrayList[Row](rows);
        mat = m;
        need(rows, m, cols);
    }

    public safe def add(row:Row) {
        mat.add(row);
    }

    public safe def add(a:(Int)=>int) {
        mat.add(new VarRow(cols, a));
    }

    public safe def set(v:int, i:int, j:int) {
        need(i+1);
        mat(i)(j) = v;
    }

    public safe def setDiagonal(i:Int, j:Int, n:Int, v:(Int)=>int) {
        need(i+n);
        for (var k:int=0; k<n; k++)
            mat(i+k)(j+k) = v(k);
    }

    public safe def setColumn(i:Int, j:Int, n:Int, v:(Int)=>int) {
        need(i+n);
        for (var k:int=0; k<n; k++)
            mat(i+k)(j) = v(k);
    }

    public safe def setRow(i:Int, j:Int, n:Int, v:(Int)=>int) {
        need(i+1);
        for (var k:int=0; k<n; k++)
            mat(i)(j+k) = v(k);
    }

    private safe def need(n:int) = need(n, this.mat, this.cols);
    private static safe def need(n:int, mat:ArrayList[Row]!, cols:int) {
        while (mat.size()<n)
            mat.add(new VarRow(cols));
    }

    public safe def toXformMat() {
        return new XformMat(mat.size(), cols, (i:Int,j:Int)=>mat(i)(j));
    }
}


