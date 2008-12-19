// (C) Copyright IBM Corporation 2006-2008.
// This file is part of X10 Language.

package x10.array.mat;

import x10.util.ArrayList;

public class MatBuilder {
    
    protected val r: ArrayList[Row];
    protected val cols: int;

    public def this(cols: nat) {
        this.cols = cols;
        r = new ArrayList[Row]();
    }

    public def this(rows: nat, cols: nat) {
        this.cols = cols;
        r = new ArrayList[Row](rows);
        need(rows);
    }

    public def add(row:Row) {
        r.add(row);
    }

    public def add(a:(nat)=>int) {
        r.add(new VarRow(cols, a));
    }

    public def set(v:int, i:int, j:int) {
        need(i+1);
        r(i)(j) = v;
    }

    private def need(n:int) {
        while (r.size()<n)
            r.add(new VarRow(cols));
    }

    public def toValMat() {
        return new ValMat(r.size(), cols, (i:nat,j:nat)=>r(i)(j));
    }
}


