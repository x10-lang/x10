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

final class VarRow extends Row {

    private global val row:Rail[int]!;

    public def this(cols: Int, init: (Int)=>int) {
        super(cols);
        row = Rail.make[int](cols, init);
    }
    
    public def this(cols: Int) {
        super(cols);
        row = Rail.make[int](cols);
    }
    
    global def row() = row as Rail[int]!;
    public safe global def apply(i:Int) = row()(i);

    public safe global def set(v:int, i:Int) = (row()(i) = v);
}
