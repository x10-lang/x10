/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2014.
 */

package x10.regionarray;

import x10.util.ArrayList;

class MatBuilder {
    
    protected val mat:ArrayList[Row];
    protected val cols:Int;

    public def this(cols:Int) {
        this.cols = cols;
        mat = new ArrayList[Row]();
    }

    public def this(rows:Int, cols:Int) {
        this.cols = cols;
	val m = new ArrayList[Row](rows);
        mat = m;
        need(rows, m, cols);
    }

    public def add(row:Row) {
        mat.add(row);
    }

    public def add(a:(Int)=>Int) {
        mat.add(new VarRow(cols, a));
    }

    public operator this(i:Int, j:Int) = mat(i)(j);

    public operator this(i:Int, j:Int)=(v:Int) {
        need(i+1n);
        mat(i)(j) = v;
    }

    public def setDiagonal(i:Int, j:Int, n:Int, v:(Int)=>Int) {
        need(i+n);
        for (var k:Int=0n; k<n; k++)
            mat(i+k)(j+k) = v(k);
    }

    public def setColumn(i:Int, j:Int, n:Int, v:(Int)=>Int) {
        need(i+n);
        for (var k:Int=0n; k<n; k++)
            mat(i+k)(j) = v(k);
    }

    public def setRow(i:Int, j:Int, n:Int, v:(Int)=>Int) {
        need(i+1n);
        for (var k:Int=0n; k<n; k++)
            mat(i)(j+k) = v(k);
    }

    private def need(n:Int) { need(n, this.mat, this.cols); }
    private static def need(n:Int, mat:ArrayList[Row], cols:Int) {
        while (mat.size()<n)
            mat.add(new VarRow(cols));
    }

}
