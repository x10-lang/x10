/*
 *  This file is part of the X10 project (http://x10-lang.org).
 *
 *  This file is licensed to You under the Eclipse Public License (EPL);
 *  You may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *      http://www.opensource.org/licenses/eclipse-1.0.php
 *
 *  (C) Copyright IBM Corporation 2006-2010.
 */

package x10.array;

final class VarMat extends Mat[VarRow] {
    
    public def this(cols:Int, mat:Array[VarRow](1)) {
        super(mat.size, cols, mat);
    }

    public def this(rows:Int, cols:Int, init:(Int)=>VarRow) {
        super(rows, cols, new Array[VarRow](rows, init));
    }

    public def this(rows:Int, cols: Int, init:(i:Int,j:Int)=>int) {
        this(rows, cols, (i:Int)=>new VarRow(cols, (j:Int)=>init(i,j)));
    }

    public def this(rows:Int, cols:Int) {
        this(rows, cols, (Int)=>new VarRow(cols));
    }
}


